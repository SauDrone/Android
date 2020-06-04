package com.saudrone.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.saudrone.controller.MainActivity.port;

public class FlightScreen extends AppCompatActivity {
    int yaw=1000,pitch=1000,roll=1000,throttle=1000,lidar=0;
    int rcStatus=0;
    int statusOneCounter=0;
    int statusTwoCounter=0;
    int statusThreeCounter=0;
    TextView info;
    SensorManager gyroManager, accManager;
    Sensor gyroSensor, accSensor;

    public String messageBuffer ="";


    public int count=0;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    SerialInputOutputManager usbIoManager;
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    System.out.println("stop");
                }

                @Override
                public void onNewData(final byte[] data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String appended = messageBuffer + new String(data, StandardCharsets.US_ASCII);
                            if (appended.contains(".")){
                                String[] parts = appended.split("\\.");
                                if (parts.length == 1){ // parcanin sonunda extra paket yok
                                    if (parts[0].length() == 20){ // uzunluk dogru ama ileride butaya pariti kontrol de eklenmeli
                                        //log.append(parts[0]+" length:"+parts[0].length()+"\n");

                                        rcParse(parts[0]);
                                        count++;


                                        messageBuffer="";
                                    }else{ // 20 dan buyukse bu paket okunmaz, buffer temizlenir
                                        messageBuffer="";
                                        //Toast.makeText(MainActivity.this, "olmadiiiii", Toast.LENGTH_SHORT).show();
                                    }
                                }else{ //birden fazla parca varsa (ben 1. yi okudum, sonuncuyu buffera attım

                                    String realmessage="";
                                    for (int i=0;i<parts.length;i++){
                                        if (parts[i].length() == 20 ){ //burda pariti kontrol de gelecek
                                            realmessage=parts[i]; //for dongusunde oldugu icin en sonuncu duzgun datayı alacak
                                            if (i == parts.length-1){
                                                messageBuffer="";
                                            }
                                        }else { //paket 20 dan kucukse
                                            if (i == parts.length-1){ // son parca ise buffera ekle
                                                messageBuffer=parts[i];
                                            }
                                        }
                                    }

                                    if (!realmessage.isEmpty()){

                                        //log.append(realmessage+" length:"+realmessage.length()+"\n");

                                        rcParse(parts[0]);
                                        count++;
                                    }
                                }
                            }else  { // paket tam gelmemis, buffera at
                                messageBuffer=appended;
                            }

                            /*String s = new String(data, StandardCharsets.US_ASCII);
                            log.append(s+" le"+data.length+"\n");
                            count++;*/

                        }
                    });
                }
            };

    public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_screen);

        info=findViewById(R.id.textInfo);

        gyroManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        accManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = accManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        baglan();

    }


    private void baglan(){

        try {
            usbIoManager =new SerialInputOutputManager(port,mListener);
            mExecutor.submit(usbIoManager);


            Timer t=new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try{
                        if (count > 10) {
                            if (rcStatus!=2){//arm edilecek

                                port.write(("1000100010001000.").getBytes(), 3);
                            }else {
                                String sendingMessage = "" + yaw + pitch + roll + throttle + ".";
                                port.write((sendingMessage).getBytes(), 3);
                            }
                        }
                        //usbIoManager.writeAsync(("000000000000.").getBytes());
                    }catch (Exception e){
                    }
                }
            },0,4);
            Toast.makeText(this, "basarili", Toast.LENGTH_SHORT).show();
            info.setText("Waiting");

        }catch (Exception e){
            Toast.makeText(this, "hataaa", Toast.LENGTH_SHORT).show();
        }
    }

    public void onResume() {
        super.onResume();
        gyroManager.registerListener(gyroListener, gyroSensor,
                SensorManager.SENSOR_DELAY_UI);

        accManager.registerListener(accListener, accSensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    public void onStop() {
        super.onStop();
        gyroManager.unregisterListener(gyroListener);
        accManager.unregisterListener(accListener);

    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

        }
    };

    public SensorEventListener accListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];



        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void rcParse(String comingText){
        if (comingText.length()!=20){
            return;
        }
        yaw=rcValueControl(Integer.parseInt(comingText.substring(0,4)));
        pitch=rcValueControl(Integer.parseInt(comingText.substring(4,8)));
        roll=rcValueControl(Integer.parseInt(comingText.substring(8,12)));
        throttle=rcValueControl(Integer.parseInt(comingText.substring(12,16)));
        lidar=lidarValueControl(Integer.parseInt(comingText.substring(16,20)));


        rcStatusChecker();

    }
    private void rcStatusChecker(){
        if (rcStatus == 0){ //waiting for arm
            if ((yaw <=2000 && yaw >=1850) && (throttle >=1000 && throttle <=1150) && (pitch <=1600 && pitch >=1400) && (roll <=1600 && roll >=1400) ){
                statusOneCounter++;
            }
            if (statusOneCounter>600){
                rcStatus=1;
                info.setText("Armed!");
                statusOneCounter=0;
            }
        }else if (rcStatus == 1){ //waiting for throttle low
            if ((yaw <=1600 && yaw >=1400) && (throttle >=1000 && throttle <=1150) && (pitch <=1600 && pitch >=1400) && (roll <=1600 && roll >=1400) ){
                statusTwoCounter++;
            }
            if (statusTwoCounter>600){
                rcStatus=2;
                info.setText("Ready");
                statusTwoCounter=0;
            }
        }else if (rcStatus == 2){ //waiting for disarm
            if ((yaw <=1150 && yaw >=1000) && (throttle >=1000 && throttle <=1150) && (pitch <=1600 && pitch >=1400) && (roll <=1600 && roll >=1400) ){
                statusThreeCounter++;
            }
            if (statusThreeCounter>600){
                rcStatus=0;
                info.setText("Stop!");
                statusThreeCounter=0;
            }
        }
    }

    private int rcValueControl(int value){
        if (value<1000){
            return 1000;
        }else if (value >2000){
            return 2000;
        }
        return value;
    }

    private int lidarValueControl(int value){
        if (value<0){
            return 0;
        }else if (value>10000){
            return 9999;
        }
        return value;
    }

}
