package com.saudrone.canvas;

public class SimpleLine {
    private float startX,startY,endX,endY;

    public SimpleLine(MyPoint centerPoint, float lineDistance, float angle) {
        float radyanAngle= (float) (angle*(Math.PI/180.0));
        this.startX= (float) ((lineDistance/2.0)*Math.cos(radyanAngle) +centerPoint.getX());
        this.startY= (float) ((lineDistance/2.0)*Math.sin(radyanAngle) +centerPoint.getY());

        this.endX=  (float) (-(lineDistance/2.0)*Math.cos(radyanAngle) +centerPoint.getX());
        this.endY= (float) (-(lineDistance/2.0)*Math.sin(radyanAngle) +centerPoint.getY());
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }
}
