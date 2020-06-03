package com.saudrone.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.saudrone.canvas.ArtificialHorizon;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {
    ArtificialHorizon artificialHorizon;
    SeekBar armBar;
    TextView textYaw,textPitch,textRoll,textGyroX,textGyroY,textGyroZ,textRCYaw,textRCPitch,textRCRoll,textRCThrottle;
    float yaw=0.0f,pitch=0.0f,roll=0.0f;
    SensorManager gyroManager, accManager;
    Sensor gyroSensor, accSensor;
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
    }
}
