package com.example.john.fteamtalk;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * 用于ImageLoader的图片缓存
 * Created by ZhangRui on 2017/3/6.
 */

public class VolleyBitmapCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> mCache;

    //缓存图片的大小设置为10M
    public VolleyBitmapCache() {
        int maxSize = 10 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }

}