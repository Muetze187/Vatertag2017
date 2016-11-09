package com.helfholz.muetze187.vatertag2017;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Comparator;

/**
 * Created by Muetze187 on 28.06.2016.
 */
public class Teams{

    private String name;

    private String strackLevel;
    private String vielfalt;
    private int drunk, counterOne, counterTwo, counterThree;
    String formattedOne, formattedTwo, formattedThree;
    private int view;
    private boolean isAlerted;
    Animation animation;
    int progressStrack;


    public Teams(){
        animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        strackLevel = "Stracklevel: ";
        vielfalt = "Verteilung: ";
        isAlerted = false;
        progressStrack = 0;
        drunk = 0;
        counterOne = 0;
        counterTwo = 0;
        counterThree = 0;
        view = 0;
    }

    public int getCounterOne(){
        return counterOne;
    }

    public int getCounterTwo(){
        return counterTwo;
    }

    public int getCounterThree(){
        return counterThree;
    }

    public String getCounterOneFormatted(){
        formattedOne = String.format("%02d", counterOne);
        return formattedOne;
    }

    public String getCounterTwoFormatted(){
        formattedTwo = String.format("%02d", counterTwo);
        return formattedTwo;
    }

    public String getCounterThreeFormatted(){
        formattedThree = String.format("%02d", counterThree);
        return formattedThree;
    }

    public void setCounterOne(int amount){
        this.counterOne = amount;
    }

    public void setCounterTwo(int amount){
        this.counterTwo = amount;
    }

    public void setCounterThree(int amount){
        this.counterThree = amount;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getStrackLevelName(){
        return strackLevel;
    }


    public void increaseStrackLevel(int level){
        progressStrack += level;
    }

    public int getProgressStrack(){
        return progressStrack;
    }

    public String getDrunk(){
        return "Getrunken: " +drunk;
    }

    public int getDrunkPlain(){
        return drunk+1;
    }

    public String getVielfalt(){
        return vielfalt;
    }

    public void setDrunk(int drunk){
        this.drunk = drunk;
    }

    public void setAlerted(boolean alert){
        if(alert){
            this.view = R.drawable.redalert;
            isAlerted = true;
        }else if(alert == false){
            this.view = 0;
            isAlerted = false;
        }
    }
    public boolean getAlerted(){
        return isAlerted;
    }
    public int getView(){
        return view;
    }


    public static Comparator<Teams> teamsComparator = new Comparator<Teams>() {
        @Override
        public int compare(Teams t1, Teams t2) {
            Boolean alert1 = t1.getAlerted();
            Boolean alert2 = t2.getAlerted();
            return alert2.compareTo(alert1);
        }
    };

}
