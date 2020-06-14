package com.saudrone.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

public class FlightLogs extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_logs);
        listView=findViewById(R.id.files);
        String folderName="SaudroneLogs";
        File baseFolder = new File(FlightLogs.this.getFilesDir(),folderName);
        File[] files = baseFolder.listFiles();
        if (files!=null) {
            final String[] fileNames = convert(files);
            ArrayAdapter<String> veriAdaptoru = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, android.R.id.text1, fileNames);
            listView.setAdapter(veriAdaptoru);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    Intent intent = new Intent(FlightLogs.this, ReadLog.class);
                    intent.putExtra("fileName", fileNames[position]);
                    startActivity(intent);

                }
            });
        }
    }

    private String [] convert(File[] file){
        String [] a= new String[file.length];
        for (int i=0;i<file.length;i++){
            a[i]=file[i].getName();
        }
        return a;
    }
}
