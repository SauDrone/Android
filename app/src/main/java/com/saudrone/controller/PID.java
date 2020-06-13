package com.saudrone.controller;

public class PID {

    private double lastSample,P,I,D,kP,kI,kD;

    public PID(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public double process(double setPoint, double sample){
        double error=setPoint - sample;
        double deltaTime=0.005;
        P=error*kP;
        I=I+ (error * kI) * deltaTime;
        I=constrain(I,-50,50);

        D=(lastSample - sample) * kD / deltaTime;
        lastSample=sample;

        double pid=P + I + D;
        return pid;


    }

    private double constrain(double val, double min_val, double max_val){
        return Math.min(max_val, Math.max(min_val,val));
    }




}
