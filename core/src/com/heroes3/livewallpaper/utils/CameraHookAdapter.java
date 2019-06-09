package com.heroes3.livewallpaper.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.heroes3.livewallpaper.AppSettings;

import java.util.concurrent.atomic.AtomicBoolean;

public class CameraHookAdapter {
    private Vector3 cameraMiddle;
    private Vector3 cameraTarget;
    private OrthographicCamera camera;

    private boolean isOnPlace = false;

    public CameraHookAdapter(OrthographicCamera camera) {
        this.camera = camera;
        cameraMiddle = camera.position.cpy();
        cameraTarget = camera.position.cpy();
    }

    public void update() {
        Vector3 cameraPosition = camera.position;
        float totalDistanceStep = cameraPosition.x - cameraTarget.x;
        float totalDistanceAbs = Math.abs(totalDistanceStep);

        if(totalDistanceAbs < 0.00001f){
            isOnPlace = true;
            camera.position.x = cameraTarget.x;
            return;
        }
        else isOnPlace = false;

        int directionIndicator = totalDistanceStep >= 0 ? -1 : 1;
        float maxDistanceStep = AppSettings.CAMERA_SMOOTH_SPEED * Math.min(Gdx.graphics.getDeltaTime(), 0.4f);

        float distanceStep = Math.min(maxDistanceStep, totalDistanceAbs) * directionIndicator;
        camera.position.x += distanceStep;

        Gdx.app.log(getClass().getSimpleName(), "camera posX: " + camera.position.x + " distance step: " + distanceStep);


    }

    public boolean needRedraw() {
        return !isOnPlace;
    }

    public void setFenceX(int xPixelOffset) {
        cameraTarget.x = cameraMiddle.x + xPixelOffset;
    }
}
