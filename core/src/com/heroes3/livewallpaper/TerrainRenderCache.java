package com.heroes3.livewallpaper;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TerrainRenderCache {

    private SpriteCache spriteCache;
    private int cacheId;

    public TerrainRenderCache() {
        spriteCache = new SpriteCache(8191,true);
    }

    public void clear(int size) {
        spriteCache.dispose();
        spriteCache = new SpriteCache(size,true);
    }

    public void update(OrthographicCamera camera) {
        spriteCache.setTransformMatrix(camera.view);
        spriteCache.setProjectionMatrix(camera.projection);
    }

    public void beginCache() {
        spriteCache.beginCache();
    }

    public void endCache() {
        cacheId = spriteCache.endCache();
    }

    public void draw() {
        spriteCache.begin();
        spriteCache.draw(cacheId);
        spriteCache.end();
    }

    public void add(TextureRegion terrain, int x, int y) {
        spriteCache.add(terrain, x, y);
    }
}
