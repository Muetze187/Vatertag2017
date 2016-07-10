package com.helfholz.muetze187.vatertag2017;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by Muetze187 on 28.06.2016.
 */
public class Teams {

    private String name;
    private int drunk;
    private int view = 0;
    private boolean isAlerted = false;
    Animation animation;

    public Teams(){
        animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDrunk(){
        return "Getrunken: " +drunk;
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


}
