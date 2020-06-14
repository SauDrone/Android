package com.saudrone.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadLog extends AppCompatActivity {

    TextView textView;
    String fileName="";
    File fileFolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_log);

        Bundle extras = getIntent().getExtras();
        textView=findViewById(R.id.fileText);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());
        textView.setMovementMethod(new ScrollingMovementMethod());

        if (extras != null) {
            fileName = extras.getString("fileName");
            String folderName="SaudroneLogs";
            File baseFolder = new File(ReadLog.this.getFilesDir(),folderName);
            fileFolder=new File(baseFolder,fileName);
            String texts=readFile(fileFolder);
            textView.setText(texts);


        }
    }

    private String readFile(File path) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) { }
        String result = text.toString();
        return result;
    }
}
