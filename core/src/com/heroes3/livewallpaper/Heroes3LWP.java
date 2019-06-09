package com.heroes3.livewallpaper;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.heroes3.livewallpaper.utils.CameraHookAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import MapReader.Map;
import MapReader.MapReader;

public class Heroes3LWP extends ApplicationAdapter {
    private final static int RECT_CHANGE_INTERVAL = 1000 * 60 * 30; // 30 minutes in milliseconds
    private SpriteBatch batch;
    private MapRender mapRender;
    private OrthographicCamera camera;
    private long rectChangeTime = 0;
    private String currentMap;


    private CameraHookAdapter cameraHookAdapter;

    private AtomicBoolean redrawRequest = new AtomicBoolean();

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;
        camera.setToOrtho(true);
        camera.update();
        cameraHookAdapter = new CameraHookAdapter(camera);


        batch = new SpriteBatch();

        mapRender = new MapRender(camera);

        setNewRandomRect(false);

        if (Gdx.app.getType() == Application.ApplicationType.Desktop) setupDesktop();
        else if (Gdx.app.getType() == Application.ApplicationType.Desktop) setupAndroidWallpaper();
    }

    private void setupAndroidWallpaper() {
        Gdx.graphics.setContinuousRendering(false);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Gdx.graphics.requestRendering();
//            }
//        }, AppSettings.UPDATE_DELAY, AppSettings.UPDATE_DELAY);
    }

    private void setupDesktop() {
        Gdx.graphics.setContinuousRendering(true);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                setNewRandomRect(true);
                return true;
            }
        });
    }

    protected void setPixelOffset(float xOffset, float xOffsetStep, int xPixelOffset) {
        cameraHookAdapter.setFenceX(-xPixelOffset);
        redrawRequest.set(true);
    }


    private Map readMap(String filename) {
        InputStream file = Gdx.files.internal(filename).read();

        try {
            return new MapReader(file).read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void render() {
        mapRender.assets.finishLoading();
        cameraHookAdapter.update();

        manuallyDelayRenderIfNeed();

        camera.update();

        batch.setTransformMatrix(camera.view);
        batch.setProjectionMatrix(camera.projection);
        mapRender.updateTerrainCacheView();

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRender.drawCache();

        batch.enableBlending();
        batch.begin();
        mapRender.renderSprites(batch);
        batch.end();
    }

    private void manuallyDelayRenderIfNeed() {
        if (cameraHookAdapter.needRedraw()) return;
//        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;

        int chunkUpdatePeriod = AppSettings.UPDATE_DELAY / AppSettings.UPDATE_CHECK_CHUNK;

        for (int i = 0; i < AppSettings.UPDATE_CHECK_CHUNK; i++) {
            if (redrawRequest.getAndSet(false)) return;
            try {
                Thread.sleep(chunkUpdatePeriod);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.update();
        setNewRandomRect(false);
    }

    @Override
    public void resume() {
        setNewRandomRect(false);
    }

    private void setNewRandomRect(boolean force) {
        if (TimeUtils.timeSinceMillis(rectChangeTime) > RECT_CHANGE_INTERVAL || force) {
            rectChangeTime = TimeUtils.millis();
            String nextMap = getRandomMapName();
            if (!nextMap.equals(currentMap)) {
                currentMap = nextMap;
                mapRender.setMap(readMap("maps/" + currentMap));
            }

            int width = Math.round(Gdx.graphics.getWidth() * 4 * camera.zoom);
            int height = Math.round(Gdx.graphics.getHeight() * camera.zoom);
            mapRender.setRandomRect(width, height);
        }
    }

    private String getRandomMapName() {
        FileHandle[] maps = Gdx.files.internal("maps").list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".h3m");
            }
        });

        int id = new Random(TimeUtils.millis()).nextInt(maps.length);
        return maps[id].name();
    }
}
