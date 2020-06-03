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
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.saudrone.canvas.ArtificialHorizon;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {
    ArtificialHorizon artificialHorizon;
    SeekBar armBar;
    TextView textYaw,textPitch,textRoll,textGyroX,textGyroY,textGyroZ,textRCYaw,textRCPitch,textRCRoll,textRCThrottle,textDataHertz,textLidar;
    SensorManager gyroManager, accManager;
    Sensor gyroSensor, accSensor;

    //serial variables
    public int count=0;
    public int fark=0;
    public String messageBuffer ="";


    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    UsbSerialPort port;
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
                                        if (count%20==0)
                                        updateSerialDatas(parts[0]);


                                        count++;
                                        messageBuffer="";
                                    }else{ // 20 dan buyukse bu paket okunmaz, buffer temizlenir
                                        messageBuffer="";
                                        Toast.makeText(MainActivity.this, "olmadiiiii", Toast.LENGTH_SHORT).show();
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
                                        if (count%20==0)
                                        updateSerialDatas(parts[0]);
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
        setContentView(R.layout.activity_main);
        gyroManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        accManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = accManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        initViews();

        baglan();
        new CountDownTimer(Long.MAX_VALUE, 1000) {

            public void onTick(long millisUntilFinished) {

                textDataHertz.setText(" Frekans:" + (count-fark)+" Hz");
                fark=count;
            }

            public void onFinish() {

            }
        }.start();


        armBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress()<95){
                    armBar.setProgress(0);
                }else{
                    // intent  works
                }
            }
        });


    }

    public void baglanEvent(View view) {
        usbIoManager.writeAsync(("aaaaaaaaa").getBytes());
    }


    private void baglan(){
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (availableDrivers.isEmpty()) {
            Toast.makeText(this, "cihaz yok", Toast.LENGTH_SHORT).show();
            return;
        }
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());

        if (connection == null) {
            if (!manager.hasPermission(driver.getDevice())) {
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                manager.requestPermission(driver.getDevice(), usbPermissionIntent);
            }


            Toast.makeText(this, "con yok", Toast.LENGTH_SHORT).show();
            return;
        }

        port = driver.getPorts().get(0); // Most devices have just one port (port 0)
        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            usbIoManager =new SerialInputOutputManager(port,mListener);
            mExecutor.submit(usbIoManager);

            Timer t=new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try{
                        if (count > 10) {
                            port.write(("0000000000000000.").getBytes(), 3);
                        }
                        //usbIoManager.writeAsync(("000000000000.").getBytes());
                    }catch (Exception e){
                    }
                }
            },0,5);
            Toast.makeText(this, "basarili", Toast.LENGTH_SHORT).show();

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

            textGyroX.setText("X:" + String.format("%.2f",x) + " rad/s");
            textGyroY.setText("Y:" + String.format("%.2f",y) + " rad/s");
            textGyroZ.setText("Z:" + String.format("%.2f",z) + " rad/s");
        }
    };

    public SensorEventListener accListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double roll=calculateRoll(x,y,z);
            double pitch=calculatePitch(y,z);
            textYaw.setText("0.0");
            textRoll.setText(String.format("%.2f",roll));
            textPitch.setText(String.format("%.2f",pitch));

            updateArtificialHorizon(0.0f,(float)(int)pitch+1,(float)(int)roll+1);


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    private void updateArtificialHorizon(float yaw,float pitch,float roll){
        artificialHorizon.roll= roll;
        artificialHorizon.pitch=pitch;
        artificialHorizon.yaw=yaw;
        artificialHorizon.invalidate();
    }

    private double calculatePitch(float y, float z){
        double _roll  = (atan2(y, z)*180.0)/PI;
        if (_roll >60.0)
            return 60.0;
        if (_roll <-60.0)
            return -60.0;
        return _roll;
    }

    private double calculateRoll(float x, float y, float z){
        Double _pitch = (atan2(-x, sqrt(y*y + z*z))*180.0)/PI;
        if (_pitch >60.0)
            return 60.0;
        if (_pitch<-60.0)
            return -60.0;
        return _pitch;
    }

    private void initViews(){
        artificialHorizon=findViewById(R.id.horizon);
        armBar=findViewById(R.id.arm);

        textYaw=findViewById(R.id.yawTextView);
        textPitch=findViewById(R.id.pitchTextView);
        textRoll=findViewById(R.id.rollTextView);

        textGyroX=findViewById(R.id.gyroXTextView);
        textGyroY=findViewById(R.id.gyroYTextView);
        textGyroZ=findViewById(R.id.gyroZTextView);

        textRCYaw=findViewById(R.id.yawRCTextView);
        textRCPitch=findViewById(R.id.pitchRCTextView);
        textRCRoll=findViewById(R.id.rollRCTextView);
        textRCThrottle=findViewById(R.id.throttleRCTextView);

        textDataHertz=findViewById(R.id.dataHertzTextView);

        textLidar=findViewById(R.id.lidarTextView);
    }

    private void updateSerialDatas(String comingText){
        if (comingText.length()!=20){
            return;
        }
        String yaw=comingText.substring(0,4);
        String pitch=comingText.substring(4,8);
        String roll=comingText.substring(8,12);
        String throttle=comingText.substring(12,16);
        String lidar=comingText.substring(16,20);

        textRCYaw.setText(yaw);
        textRCPitch.setText(pitch);
        textRCRoll.setText(roll);
        textRCThrottle.setText(throttle);
        textLidar.setText(lidar);
    }
}
