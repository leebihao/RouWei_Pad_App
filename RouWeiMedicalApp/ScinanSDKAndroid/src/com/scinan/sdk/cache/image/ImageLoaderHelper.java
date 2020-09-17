/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.cache.image;

import android.content.Context;

import com.scinan.sdk.volley.RequestQueue;
import com.scinan.sdk.volley.toolbox.ImageLoader;
import com.scinan.sdk.volley.toolbox.ImageLoader.ImageCache;
import com.scinan.sdk.volley.toolbox.Volley;
import com.scinan.sdk.cache.image.ImageBitmapCache.ImageCacheParams;

public class ImageLoaderHelper {

    public static final String IMAGE_CACHE_DIR = "temp";

    private RequestQueue mImageQueue;
    private ImageLoader mImageLoader;
    private ImageCache mImageCache;

    private static ImageLoaderHelper mInstance;

    private ImageLoaderHelper(Context context) {
        mImageQueue = Volley.newRequestQueue(context);
        ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);
        cacheParams.memCacheSize = 1024 * 1024 * ImageUtils.getMemoryClass(context) / 3;
        mImageCache = ImageBitmapCache.getInstanceCache(context, cacheParams);
        mImageLoader = new ImageLoader(mImageQueue, mImageCache);
    }

    public static synchronized ImageLoaderHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ImageLoaderHelper(context);
        }
        return mInstance;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public ImageCache getImageCache() {
        return mImageCache;
    }

}
