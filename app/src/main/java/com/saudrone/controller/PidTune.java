package com.saudrone.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PidTune extends AppCompatActivity {

    EditText acroYawP,acroYawI,acroYawD,acroPitchP,acroPitchI,acroPitchD,acroRollP,acroRollI,acroRollD;
    EditText stabYawP,stabYawI,stabYawD,stabPitchP,stabPitchI,stabPitchD,stabRollP,stabRollI,stabRollD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pid_tune);
        initViews();
        getDatas();
    }

    private void initViews(){
        acroYawP=findViewById(R.id.acroYawP);
        acroYawI=findViewById(R.id.acroYawI);
        acroYawD=findViewById(R.id.acroYawD);

        acroPitchP=findViewById(R.id.acroPitchP);
        acroPitchI=findViewById(R.id.acroPitchI);
        acroPitchD=findViewById(R.id.acroPitchD);

        acroRollP=findViewById(R.id.acroRollP);
        acroRollI=findViewById(R.id.acroRollI);
        acroRollD=findViewById(R.id.acroRollD);


        stabYawP=findViewById(R.id.stabYawP);
        stabYawI=findViewById(R.id.stabYawI);
        stabYawD=findViewById(R.id.stabYawD);

        stabPitchP=findViewById(R.id.stabPitchP);
        stabPitchI=findViewById(R.id.stabPitchI);
        stabPitchD=findViewById(R.id.stabPitchD);

        stabRollP=findViewById(R.id.stabRollP);
        stabRollI=findViewById(R.id.stabRollI);
        stabRollD=findViewById(R.id.stabRollD);


    }

    private void getDatas(){
        acroYawP.setText(prefCek(Constanst.acroYawPName,Constanst.acroYawPKey,"0.000"));
        acroYawI.setText(prefCek(Constanst.acroYawIName,Constanst.acroYawIKey,"0.000"));
        acroYawD.setText(prefCek(Constanst.acroYawDName,Constanst.acroYawDKey,"0.000"));

        acroPitchP.setText(prefCek(Constanst.acroPitchPName,Constanst.acroPitchPKey,"0.000"));
        acroPitchI.setText(prefCek(Constanst.acroPitchIName,Constanst.acroPitchIKey,"0.000"));
        acroPitchD.setText(prefCek(Constanst.acroPitchDName,Constanst.acroPitchDKey,"0.000"));

        acroRollP.setText(prefCek(Constanst.acroRollPName,Constanst.acroRollPKey,"0.000"));
        acroRollI.setText(prefCek(Constanst.acroRollIName,Constanst.acroRollIKey,"0.000"));
        acroRollD.setText(prefCek(Constanst.acroRollDName,Constanst.acroRollDKey,"0.000"));


        stabYawP.setText(prefCek(Constanst.stabYawPName,Constanst.stabYawPKey,"0.000"));
        stabYawI.setText(prefCek(Constanst.stabYawIName,Constanst.stabYawIKey,"0.000"));
        stabYawD.setText(prefCek(Constanst.stabYawDName,Constanst.stabYawDKey,"0.000"));

        stabPitchP.setText(prefCek(Constanst.stabPitchPName,Constanst.stabPitchPKey,"0.000"));
        stabPitchI.setText(prefCek(Constanst.stabPitchIName,Constanst.stabPitchIKey,"0.000"));
        stabPitchD.setText(prefCek(Constanst.stabPitchDName,Constanst.stabPitchDKey,"0.000"));

        stabRollP.setText(prefCek(Constanst.stabRollPName,Constanst.stabRollPKey,"0.000"));
        stabRollI.setText(prefCek(Constanst.stabRollIName,Constanst.stabRollIKey,"0.000"));
        stabRollD.setText(prefCek(Constanst.stabRollDName,Constanst.stabRollDKey,"0.000"));

    }


    public void setDatas(View view) {

        prefEkle(Constanst.acroYawPName,Constanst.acroYawPKey,acroYawP.getText().toString());
        prefEkle(Constanst.acroYawIName,Constanst.acroYawIKey,acroYawI.getText().toString());
        prefEkle(Constanst.acroYawDName,Constanst.acroYawDKey,acroYawD.getText().toString());

        prefEkle(Constanst.acroPitchPName,Constanst.acroPitchPKey,acroPitchP.getText().toString());
        prefEkle(Constanst.acroPitchIName,Constanst.acroPitchIKey,acroPitchI.getText().toString());
        prefEkle(Constanst.acroPitchDName,Constanst.acroPitchDKey,acroPitchD.getText().toString());

        prefEkle(Constanst.acroRollPName,Constanst.acroRollPKey,acroRollP.getText().toString());
        prefEkle(Constanst.acroRollIName,Constanst.acroRollIKey,acroRollI.getText().toString());
        prefEkle(Constanst.acroRollDName,Constanst.acroRollDKey,acroRollD.getText().toString());


        prefEkle(Constanst.stabYawPName,Constanst.stabYawPKey,stabYawP.getText().toString());
        prefEkle(Constanst.stabYawIName,Constanst.stabYawIKey,stabYawI.getText().toString());
        prefEkle(Constanst.stabYawDName,Constanst.stabYawDKey,stabYawD.getText().toString());

        prefEkle(Constanst.stabPitchPName,Constanst.stabPitchPKey,stabPitchP.getText().toString());
        prefEkle(Constanst.stabPitchIName,Constanst.stabPitchIKey,stabPitchI.getText().toString());
        prefEkle(Constanst.stabPitchDName,Constanst.stabPitchDKey,stabPitchD.getText().toString());

        prefEkle(Constanst.stabRollPName,Constanst.stabRollPKey,stabRollP.getText().toString());
        prefEkle(Constanst.stabRollIName,Constanst.stabRollIKey,stabRollI.getText().toString());
        prefEkle(Constanst.stabRollDName,Constanst.stabRollDKey,stabRollD.getText().toString());

        Toast.makeText(this, "Eklendi", Toast.LENGTH_SHORT).show();
    }

    private void prefEkle(String name,String key,String value){
        SharedPreferences sharedPreferences=getSharedPreferences(name,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }



    private String prefCek(String name,String key,String yoksa){
        SharedPreferences sharedPreferences=getSharedPreferences(name,MODE_PRIVATE);
        return sharedPreferences.getString(key,yoksa);
    }

    public void resetAcro(View view) {
        acroYawP.setText("0.000");
        acroYawI.setText("0.000");
        acroYawD.setText("0.000");

        acroPitchP.setText("0.000");
        acroPitchI.setText("0.000");
        acroPitchD.setText("0.000");

        acroRollP.setText("0.000");
        acroRollI.setText("0.000");
        acroRollD.setText("0.000");


    }

    public void resetStab(View view) {
        stabYawP.setText("0.000");
        stabYawI.setText("0.000");
        stabYawD.setText("0.000");

        stabPitchP.setText("0.000");
        stabPitchI.setText("0.000");
        stabPitchD.setText("0.000");

        stabRollP.setText("0.000");
        stabRollI.setText("0.000");
        stabRollD.setText("0.000");
    }
}
