package com.heroes3.livewallpaper.utils;

import com.heroes3.livewallpaper.AppSettings;

public enum AnimationTimeManager {
    INSTANCE;

    private boolean changeAnimation;
    private long lastChangeAnimationTime;

    AnimationTimeManager(){
        lastChangeAnimationTime = System.currentTimeMillis();
    }

    public void update(){
        long currentTime = System.currentTimeMillis();
        long delta = currentTime - lastChangeAnimationTime;
        if(delta > AppSettings.UPDATE_DELAY){
            changeAnimation = true;
            lastChangeAnimationTime = currentTime;
        }
        else  changeAnimation = false;
    }

    public boolean shouldAnimationBeChanged(){
        return changeAnimation;
    }
}
