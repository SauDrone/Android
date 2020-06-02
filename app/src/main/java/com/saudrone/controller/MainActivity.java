package com.saudrone.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.SeekBar;

import com.saudrone.canvas.ArtificialHorizon;

public class MainActivity extends AppCompatActivity {
    ArtificialHorizon artificialHorizon;
    SeekBar armBar;
    float yaw=0.0f,pitch=0.0f,roll=0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        artificialHorizon=findViewById(R.id.horizon);
        armBar=findViewById(R.id.arm);

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

    private void updateArtificialHorizon(float yaw,float pitch,float roll){
        artificialHorizon.roll= roll;
        artificialHorizon.pitch=pitch;
        artificialHorizon.yaw=yaw;
        artificialHorizon.invalidate();
    }
}
