/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.cache.image;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.scinan.sdk.volley.toolbox.ImageLoader.ImageCache;

/**
 * This class holds our bitmap caches (memory and disk).
 */
public class ImageBitmapCache implements ImageCache {
    // Default memory cache size
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB

    // Default disk cache size
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    // Compression settings when writing images to disk cache
    private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.PNG;
    private static final int DEFAULT_COMPRESS_QUALITY = 70;

    // Constants to easily toggle various caches
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
    private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;

    private DiskLruCache mDiskCache;
    private LruCache<String, Bitmap> mMemoryCache;

    private static ImageBitmapCache instanceCache = null;

    public static synchronized ImageBitmapCache getInstanceCache(Context context, ImageCacheParams cacheParams) {
        if (instanceCache == null) {
            instanceCache = new ImageBitmapCache(context, cacheParams);
        }
        return instanceCache;
    }

    /**
     * Creating a new ImageCache object using the specified parameters.
     * 
     * @param context
     *            The context to use
     * @param cacheParams
     *            The cache parameters to use to initialize the cache
     */
    private ImageBitmapCache(Context context, ImageCacheParams cacheParams) {
        init(context, cacheParams);
    }

    public static synchronized ImageBitmapCache getInstanceCache(Context context, String uniqueName) {
        if (instanceCache == null) {
            instanceCache = new ImageBitmapCache(context, uniqueName);
        }
        return instanceCache;
    }

    /**
     * Creating a new ImageCache object using the default parameters.
     * 
     * @param context
     *            The context to use
     * @param uniqueName
     *            A unique name that will be appended to the cache directory
     */
    private ImageBitmapCache(Context context, String uniqueName) {
        init(context, new ImageCacheParams(uniqueName));
    }

    /**
     * Initialize the cache, providing all parameters.
     * 
     * @param context
     *            The context to use
     * @param cacheParams
     *            The cache parameters to initialize the cache
     */
    private void init(Context context, ImageCacheParams cacheParams) {
        final File diskCacheDir = DiskLruCache.getDiskCacheDir(context, cacheParams.uniqueName);

        // Set up disk cache
        if (cacheParams.diskCacheEnabled) {
            mDiskCache = DiskLruCache.openCache(context, diskCacheDir, cacheParams.diskCacheSize);
            if (mDiskCache == null)
                return;
            mDiskCache.setCompressParams(cacheParams.compressFormat, cacheParams.compressQuality);
            if (cacheParams.clearDiskCacheOnStart) {
                mDiskCache.clearCache();
            }
        }

        // Set up memory cache
        if (cacheParams.memoryCacheEnabled) {
            mMemoryCache = new LruCache<String, Bitmap>(cacheParams.memCacheSize) {
                /**
                 * Measure item size in bytes rather than units which is more
                 * practical for a bitmap cache
                 */
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return ImageUtils.getBitmapSize(bitmap);
                }
            };
        }
    }

    public void addBitmapToCache(String data, Bitmap bitmap) {
        if (data == null || bitmap == null) {
            return;
        }

        // Add to memory cache
        if (mMemoryCache != null && mMemoryCache.get(data) == null) {
            mMemoryCache.put(data, bitmap);
            mMemoryCache.trimToSize(DEFAULT_MEM_CACHE_SIZE);
        }

        // Add to disk cache
        if (mDiskCache != null && !mDiskCache.containsKey(data)) {
            mDiskCache.put(data, bitmap);
        }
    }

    /**
     * Get from memory cache.
     * 
     * @param data
     *            Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromMemCache(String data) {
        if (mMemoryCache != null) {
            final Bitmap memBitmap = mMemoryCache.get(data);
            if (memBitmap != null) {
                return memBitmap;
            }
        }
        return null;
    }

    /**
     * Get from disk cache.
     * 
     * @param data
     *            Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromDiskCache(String data) {
        if (mDiskCache != null) {
            return mDiskCache.get(data);
        }
        return null;
    }

    public void clearCaches() {
        if (mDiskCache != null)
            mDiskCache.clearCache();
        if (mMemoryCache != null)
            mMemoryCache.evictAll();
    }

    public void clearMemCaches() {
        if (mMemoryCache != null)
            mMemoryCache.evictAll();
    }

    /**
     * A holder class that contains cache parameters.
     */
    public static class ImageCacheParams {
        public String uniqueName;
        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
        public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
        public int compressQuality = DEFAULT_COMPRESS_QUALITY;
        public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
        public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
        public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;

        public ImageCacheParams(String uniqueName) {
            this.uniqueName = uniqueName;
        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        Bitmap data = getBitmapFromMemCache(url);
        if (data == null) {
            data = getBitmapFromDiskCache(url);
        }
        return data;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        addBitmapToCache(url, bitmap);
    }

}
