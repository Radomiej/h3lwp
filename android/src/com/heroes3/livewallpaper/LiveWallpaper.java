package com.heroes3.livewallpaper;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;
import com.badlogic.gdx.backends.android.AndroidWallpaperListener;

public class LiveWallpaper extends AndroidLiveWallpaperService {
    @Override
    public void onCreateApplication() {
        super.onCreateApplication();
        initialize(createListener());
    }

    public ApplicationListener createListener() {
        return new LiveWallpaperHeroes3LWP();
    }

    public AndroidApplicationConfiguration createConfig() {
        return new AndroidApplicationConfiguration();
    }

    public static class LiveWallpaperHeroes3LWP extends Heroes3LWP implements AndroidWallpaperListener {

        @Override
        public void offsetChange(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            Gdx.app.log("LiveWallpaper", "offset changed: " + xPixelOffset + ", " + yPixelOffset + " step: " + xOffsetStep + " normal: " + xOffset);
            setPixelOffset(xOffset, xOffsetStep, xPixelOffset);
        }


        @Override
        public void previewStateChange(boolean isPreview) {

        }
    }
}
