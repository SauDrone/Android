package com.saudrone.canvas;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Toast;

import java.util.ArrayList;

public class ArtificialHorizon extends androidx.appcompat.widget.AppCompatImageView {
    Paint paintRollPitchColor =new Paint();
    Paint paintCenterPoint= new Paint();
    Paint paintpitchLines = new Paint();
    Paint paintPitchText=new Paint();
    Paint paintRollLines=new Paint();
    Paint paintCompassLines=new Paint();
    Paint paintCompassText=new Paint();
    Paint paintCompassRectangle=new Paint();
    public float roll=0.0f,pitch=0.0f,yaw=0.0f;
    public ArtificialHorizon(Context context) {
        super(context);
    }

    public ArtificialHorizon(Context context, AttributeSet attrst) {
        super(context, attrst);
    }

    public ArtificialHorizon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onDraw(final Canvas canvas) {



        paintRollLines.setColor(0xffffffff);
        paintRollLines.setStrokeWidth(5);
        paintRollPitchColor.setColor(0xff2196F3);
        paintRollPitchColor.setStrokeWidth(1);
        /*
        Path wallpath = new Path();
        wallpath.reset(); // only needed when reusing this path for a new build
        wallpath.moveTo(50, 50); // used for first point
        wallpath.lineTo(300, 125);
        wallpath.lineTo(50, 200);
        wallpath.lineTo(300, 200);
        wallpath.lineTo(50, 50);

        canvas.drawPath(wallpath,paint);
        */

        /**
         * roll < 45:
         *   n1:0, ymax/90 *(45-roll)
         *   n2:0,0
         *   n3:xmax,0
         *   n4:xmax,ymax/90*(45+roll
         * */

        // roll pitch renk verme kodu
        Path wallpath=givePath(roll,pitch,canvas.getWidth(),canvas.getHeight());
        canvas.drawPath(wallpath, paintRollPitchColor);


        // merkez noktası kısmı
        paintCenterPoint.setColor(0xffff0000);
        paintCenterPoint.setStrokeWidth(10.0f);
        float centerPointLength=canvas.getWidth()/10.0f;
        MyPoint centerPoint=new MyPoint(canvas.getWidth()/2.0f,canvas.getHeight()/2.0f);
        canvas.drawLine(centerPoint.getX(),centerPoint.getY(),
                (float) (centerPoint.getX()+Math.cos((Math.PI/180.0)*35.0)*centerPointLength),
                (float) (centerPoint.getY()+Math.sin((Math.PI/180.0)*35.0)*centerPointLength),
                paintCenterPoint);
        canvas.drawLine(centerPoint.getX(),centerPoint.getY(),
                (float) (centerPoint.getX()+Math.cos((Math.PI/180.0)*145.0)*centerPointLength),
                (float) (centerPoint.getY()+Math.sin((Math.PI/180.0)*145.0)*centerPointLength),
                paintCenterPoint);

        //rol merkez noktası
        float rollCenterPointLength=canvas.getWidth()/25.0f;
        MyPoint rollCenterPoint=new MyPoint(canvas.getWidth()/2.0f,canvas.getHeight()/8.0f);
        canvas.drawLine(rollCenterPoint.getX(),rollCenterPoint.getY(),
                (float) (rollCenterPoint.getX()+Math.cos((Math.PI/180.0)*35.0)*rollCenterPointLength),
                (float) (rollCenterPoint.getY()+Math.sin((Math.PI/180.0)*35.0)*rollCenterPointLength),
                paintCenterPoint);
        canvas.drawLine(rollCenterPoint.getX(),rollCenterPoint.getY(),
                (float) (rollCenterPoint.getX()+Math.cos((Math.PI/180.0)*145.0)*rollCenterPointLength),
                (float) (rollCenterPoint.getY()+Math.sin((Math.PI/180.0)*145.0)*rollCenterPointLength),
                paintCenterPoint);

        //horizontal compass
        //dikdortgen sınır
        paintCompassRectangle.setStyle(Paint.Style.FILL);
        paintCompassRectangle.setColor(0x4d000000);
        int compassRectStartY= (int)( (canvas.getHeight()/9f)*7.3f);
        Rect rect=new Rect(0,compassRectStartY,canvas.getWidth(),canvas.getHeight());
        canvas.drawRect(rect,paintCompassRectangle);

        //merkez cizgisi
        float compassCenterPointLength=canvas.getWidth()/33.0f;
        MyPoint compassCenterPoint=new MyPoint(canvas.getWidth()/2.0f,canvas.getHeight()/9.0f*7.5f);
        canvas.drawLine(compassCenterPoint.getX(),compassCenterPoint.getY(),
                (float) (compassCenterPoint.getX()+Math.cos((Math.PI/180.0)*-35.0)*compassCenterPointLength),
                (float) (compassCenterPoint.getY()+Math.sin((Math.PI/180.0)*-35.0)*compassCenterPointLength),
                paintCenterPoint);
        canvas.drawLine(compassCenterPoint.getX(),compassCenterPoint.getY(),
                (float) (compassCenterPoint.getX()+Math.cos((Math.PI/180.0)*-145.0)*compassCenterPointLength),
                (float) (compassCenterPoint.getY()+Math.sin((Math.PI/180.0)*-145.0)*compassCenterPointLength),
                paintCenterPoint);
        //aci cizgileri

        paintCompassLines.setColor(0xffffffff);
        paintCompassLines.setStrokeWidth(5f);

        float oneDegreePaddingCompass=canvas.getWidth()/125f;
        float compassLinesDistance=(canvas.getHeight()-compassRectStartY)/2f;
        MyPoint compassLineCenterPoint = new MyPoint(centerPoint.getX(),compassRectStartY+(compassLinesDistance/2f));
        ArrayList<SimpleLine> compassLines=new ArrayList<>();
        float compassModPadding=(yaw%15f)*oneDegreePaddingCompass;
        compassLines.add(new SimpleLine(new MyPoint(compassLineCenterPoint.getX()-compassModPadding,compassLineCenterPoint.getY()),
                compassLinesDistance,90f));
        for (int i=15;i<=60;i=i+15){
            MyPoint pointRight=new MyPoint(compassLineCenterPoint.getX()+oneDegreePaddingCompass*i-compassModPadding,compassLineCenterPoint.getY());
            MyPoint pointLeft =new MyPoint(compassLineCenterPoint.getX()-oneDegreePaddingCompass*i-compassModPadding,compassLineCenterPoint.getY());
            compassLines.add(new SimpleLine(pointRight,compassLinesDistance,90f));
            compassLines.add(new SimpleLine(pointLeft,compassLinesDistance,90f));
        }
        MyPoint yedekpoint =new MyPoint(compassLineCenterPoint.getX()+oneDegreePaddingCompass*75f-compassModPadding,compassLineCenterPoint.getY());
        compassLines.add(new SimpleLine(yedekpoint,compassLinesDistance,90f));

        for (SimpleLine line: compassLines){
            canvas.drawLine(line.getStartX(),line.getStartY(),line.getEndX(),line.getEndY(),paintCompassLines);
        }
        //aci yazilari
        float compassTextSize=compassLinesDistance/3*2;
        paintCompassText.setColor(0xffffffff);
        paintCompassText.setTextSize(compassTextSize);


        int compassCenterNum=(int) (yaw/15) *15;
        int compassLeftOneNum=calculateCompassText(compassCenterNum,-15);
        int compassRightOneNum=calculateCompassText(compassCenterNum,15);
        int compassLeftTwoNum=calculateCompassText(compassCenterNum,-30);
        int compassRightTwoNum=calculateCompassText(compassCenterNum,30);
        int compassLeftThreeNum=calculateCompassText(compassCenterNum,-45);
        int compassRightThreeNum=calculateCompassText(compassCenterNum,45);
        int compassLeftFourNum=calculateCompassText(compassCenterNum,-60);
        int compassRightFourNum=calculateCompassText(compassCenterNum,60);
        int compassYedekNum=calculateCompassText(compassCenterNum,75);

        float compassTextY=compassLines.get(0).getStartY()+compassLinesDistance/4*3;
        canvas.drawText(String.valueOf(compassCenterNum),compassLines.get(0).getStartX()-getTextWidth(String.valueOf(compassCenterNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassRightOneNum),compassLines.get(1).getStartX()-getTextWidth(String.valueOf(compassRightOneNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassLeftOneNum),compassLines.get(2).getStartX()-getTextWidth(String.valueOf(compassLeftOneNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassRightTwoNum),compassLines.get(3).getStartX()-getTextWidth(String.valueOf(compassRightTwoNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassLeftTwoNum),compassLines.get(4).getStartX()-getTextWidth(String.valueOf(compassLeftTwoNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassRightThreeNum),compassLines.get(5).getStartX()-getTextWidth(String.valueOf(compassRightThreeNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassLeftThreeNum),compassLines.get(6).getStartX()-getTextWidth(String.valueOf(compassLeftThreeNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassRightFourNum),compassLines.get(7).getStartX()-getTextWidth(String.valueOf(compassRightFourNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassLeftFourNum),compassLines.get(8).getStartX()-getTextWidth(String.valueOf(compassLeftFourNum),
                compassTextSize/2),compassTextY,paintCompassText);

        canvas.drawText(String.valueOf(compassYedekNum),compassLines.get(9).getStartX()-getTextWidth(String.valueOf(compassYedekNum),
                compassTextSize/2),compassTextY,paintCompassText);




        // pitch cizgileri
        paintpitchLines.setColor(0xffffffff);
        paintpitchLines.setStrokeWidth(5.0f);

        float oneDegreePadding=canvas.getHeight()/90.0f;
        float pitchLinesDistance=canvas.getWidth()/8.0f;
        paintPitchText.setColor(0xffffffff);
        paintPitchText.setTextSize(oneDegreePadding*7.0f);
        ArrayList<SimpleLine> pitchLines=new ArrayList<>();
        float modPadding=(pitch%10.0f)*oneDegreePadding;

        pitchLines.add(new SimpleLine(new MyPoint(centerPoint.getX(),centerPoint.getY()+modPadding),pitchLinesDistance,0.0f));

        for (int i=10;i<=20;i=i+10){
            MyPoint pointTop=new MyPoint(centerPoint.getX(),centerPoint.getY()-oneDegreePadding*i+modPadding);
            MyPoint pointBottom=new MyPoint(centerPoint.getX(),centerPoint.getY()+oneDegreePadding*i+modPadding);
            pitchLines.add(new SimpleLine(pointTop,pitchLinesDistance,0.0f));
            pitchLines.add(new SimpleLine(pointBottom,pitchLinesDistance,0.0f));
        }
        canvas.save();
        canvas.rotate(-roll,centerPoint.getX(),centerPoint.getY());
        for (SimpleLine line: pitchLines){
            canvas.drawLine(line.getStartX(),line.getStartY(),line.getEndX(),line.getEndY(),paintpitchLines);
        }
        // pitch sayilari
        if ((int)pitch ==0){
            pitch+=1.0f;
        }

        int pitchOneNum= (int) (pitch/10) *10;
        int pitchTwoNumPlus =calculatePitchText(pitchOneNum,10);
        int pitchTwoNumNeg = calculatePitchText(pitchOneNum,-10);
        int pitchThreeNumPlus=calculatePitchText(pitchOneNum,20);
        int pitchThreeNumNeg=calculatePitchText(pitchOneNum,-20);
        //merkez
        canvas.drawText(String.valueOf(pitchOneNum),pitchLines.get(0).getStartX(),pitchLines.get(0).getStartY()+oneDegreePadding*2.5f,
                paintPitchText);
        canvas.drawText(String.valueOf(pitchOneNum),pitchLines.get(0).getEndX()-getTextWidth(String.valueOf(pitchOneNum),
                oneDegreePadding*8.0f), pitchLines.get(0).getEndY()+oneDegreePadding*2.5f,paintPitchText);
        //ust 1
        canvas.drawText(String.valueOf(pitchTwoNumPlus),pitchLines.get(1).getStartX(),pitchLines.get(1).getStartY()+oneDegreePadding*2.5f,
                paintPitchText);
        canvas.drawText(String.valueOf(pitchTwoNumPlus),pitchLines.get(1).getEndX()-getTextWidth(String.valueOf(pitchTwoNumPlus),
                oneDegreePadding*8.0f), pitchLines.get(1).getEndY()+oneDegreePadding*2.5f,paintPitchText);
        //alt 1
        canvas.drawText(String.valueOf(pitchTwoNumNeg),pitchLines.get(2).getStartX(),pitchLines.get(2).getStartY()+oneDegreePadding*2.5f,
                paintPitchText);
        canvas.drawText(String.valueOf(pitchTwoNumNeg),pitchLines.get(2).getEndX()-getTextWidth(String.valueOf(pitchTwoNumNeg),
                oneDegreePadding*8.0f), pitchLines.get(2).getEndY()+oneDegreePadding*2.5f,paintPitchText);
        //ust 2
        canvas.drawText(String.valueOf(pitchThreeNumPlus),pitchLines.get(3).getStartX(),pitchLines.get(3).getStartY()+oneDegreePadding*2.5f,
                paintPitchText);
        canvas.drawText(String.valueOf(pitchThreeNumPlus),pitchLines.get(3).getEndX()-getTextWidth(String.valueOf(pitchThreeNumPlus),
                oneDegreePadding*8.0f), pitchLines.get(3).getEndY()+oneDegreePadding*2.5f,paintPitchText);
        //alt 2
        canvas.drawText(String.valueOf(pitchThreeNumNeg),pitchLines.get(4).getStartX(),pitchLines.get(4).getStartY()+oneDegreePadding*2.5f,
                paintPitchText);
        canvas.drawText(String.valueOf(pitchThreeNumNeg),pitchLines.get(4).getEndX()-getTextWidth(String.valueOf(pitchThreeNumNeg),
                oneDegreePadding*8.0f), pitchLines.get(4).getEndY()+oneDegreePadding*2.5f,paintPitchText);



        //roll yamuk cizgi

        float bestebir=canvas.getWidth()/5.0f;
        float x1=bestebir,x2=bestebir*4.0f;
        float yler=(canvas.getHeight()/5.0f);
        float y1=yler,y2=yler;
        int curveRadius=360;
        final Path path = new Path();
        float midX            = x1 + ((x2 - x1) / 2);
        float midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
        float pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));

        paintRollLines.setAntiAlias(true);
        paintRollLines.setStyle(Paint.Style.STROKE);
        path.moveTo(x1, y1);
        path.cubicTo(x1,y1,pointX, pointY, x2, y2);
        canvas.drawPath(path, paintRollLines);

        //rol parca cizgiler
        float rollLineDistance=canvas.getHeight()/20.0f;
        MyPoint[] rollLines=getPoints(path);
        float angles[]= {45f,60f,75f,90f,-75f,-60f,-45f};
        for (int i=0;i<rollLines.length;i++){
            SimpleLine simpleLine=new SimpleLine(rollLines[i],rollLineDistance,angles[i]);
            canvas.drawLine(simpleLine.getStartX(),simpleLine.getStartY(),simpleLine.getEndX(),simpleLine.getEndY(),paintpitchLines);
        }







        canvas.restore();






        this.postInvalidate();
    }


    private MyPoint[] getPoints(Path path0) {
        MyPoint[] pointArray = new MyPoint[7];
        PathMeasure pm = new PathMeasure(path0, false);
        float length = pm.getLength();
        float distance = 0f;
        float speed = length / 6;
        int counter = 0;
        float[] aCoordinates = new float[2];

        while ((distance <= length) && (counter < 7)) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null);
            pointArray[counter] = new MyPoint(aCoordinates[0],
                    aCoordinates[1]);
            counter++;
            distance = distance + speed;
        }

        return pointArray;
    }

    private int calculateCompassText(int center,int artis){
        int temp=center+artis;
        if (temp<0){
            temp+=360;
        }else if(temp >= 360){
            temp-=360;
        }
        return temp;
    }

    private int calculatePitchText(int center,int artis){
        int temp=center+artis;
        if (temp>180 ){
            return temp - 360;
        }else if(temp <-180){
            return temp + 360;
        }else {
            return temp;
        }
    }
    private float getTextWidth(String text,float textSize){

        Paint paint=new Paint();
        paint.setTextSize(textSize);
        //return textBounds.width();
        return paint.measureText(text);
    }


    private float oran(float max,float aci,float taban){
        return (max/90.0f)*(taban+aci);
    }
    private Path givePath(float roll,float pitch,float xMax,float yMax){
        float pitchDistanceY=(yMax/90)*pitch;
        float pitchDistanceX=(xMax/90)*pitch;
        Path wallpath=new Path();
        MyPoint n1 = null,n2=null,n3=null,n4=null;
        if (roll>=-45.0f && roll <=45.0f ) {
            n1 = new MyPoint(0.0f, oran(yMax, roll,45)+pitchDistanceY);
            n2 = new MyPoint(0.0f, 0.0f);
            n3 = new MyPoint(xMax, 0.0f);
            n4 = new MyPoint(xMax, oran(yMax, -roll,45)+pitchDistanceY); // TODO: 3/16/2020  n1 in y sinden cikarsan daha hizli olur
        }else if(roll > 45.0f && roll <= 135.0f){
            n1=new MyPoint(oran(xMax,roll,-45.0f)+pitchDistanceX,yMax);
            n2=new MyPoint(0.0f,yMax);
            n3=new MyPoint(0.0f,0.0f);
            n4=new MyPoint(oran(xMax,-roll,135.0f)+pitchDistanceX,0.0f);

        }else if((roll > 135.0f && roll<=180.0f) || (roll >= -180.0f && roll <=-135.0f) ){
            if (roll > 135.0f && roll<=180.0f){
                n1=new MyPoint(xMax,oran(yMax,-roll,225)+pitchDistanceY);
                n2=new MyPoint(xMax,yMax);
                n3=new MyPoint(0.0f,yMax);
                n4=new MyPoint(0.0f,oran(yMax,roll-135.0f,0.0f)+pitchDistanceY);
            }else{
                n1=new MyPoint(xMax,oran(yMax,-(roll+135.0f),0.0f)+pitchDistanceY);
                n2=new MyPoint(xMax,yMax);
                n3=new MyPoint(0.0f,yMax);
                n4=new MyPoint(0.0f,oran(yMax,roll,225.0f)+pitchDistanceY);
            }
        }else if(roll> -135.0f && roll <-45.0f){
            n1=new MyPoint(oran(xMax,roll,+135.0f)-pitchDistanceX,yMax);
            n2=new MyPoint(xMax,yMax);
            n3=new MyPoint(xMax,0.0f);
            n4=new MyPoint(oran(xMax,-roll,-45.0f)-pitchDistanceX,0.0f);
        }

        wallpath.reset(); // only needed when reusing this path for a new build
        wallpath.moveTo(n1.getX(), n1.getY()); // used for first point
        wallpath.lineTo(n2.getX(), n2.getY());
        wallpath.lineTo(n3.getX(), n3.getY());
        wallpath.lineTo(n4.getX(), n4.getY());
        wallpath.lineTo(n1.getX(), n1.getY());
        return wallpath;
    }


}

