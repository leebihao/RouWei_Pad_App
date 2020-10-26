package com.lbh.rouwei.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.lbh.rouwei.common.bean.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/10/07
 *     desc   :
 * </pre>
 */
public class FileManager {

    private static FileManager mInstance;
    private static Context mContext;
    public static ContentResolver mContentResolver;
    private static Object mLock = new Object();

    public static FileManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new FileManager();
                    mContext = context;
                    mContentResolver = context.getContentResolver();
                }
            }
        }
        return mInstance;
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件的路径
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 获取本机视频列表
     *
     * @return
     */
    public static List<Video> getVideos() {

        List<Video> videos = new ArrayList<>();

        Cursor c = null;
        try {
            // String[] mediaColumns = { "_id", "_data", "_display_name",
            // "_size", "date_modified", "duration", "resolution" };
            c = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                if (!isExists(path)) {
                    continue;
                }

                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 视频的id
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 视频名称
                String resolution = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)); //分辨率
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
                long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));// 时长
                long date = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));//修改时间

                Video video = new Video(id, path, name, resolution, size, date, duration);
                videos.add(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return videos;
    }

    // 获取视频缩略图
    public static Bitmap getVideoThumbnail(int id) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);
        return bitmap;
    }

    public static List<String> getFilesAllName(String path) {
        //传入指定文件夹的路径
        File file = new File(path);
        File[] files = file.listFiles();
        List<String> imagePaths = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (checkIsImageFile(files[i].getPath())) {
                imagePaths.add(files[i].getPath());
            }

        }
        return imagePaths;
    }

    /**
     * 判断是否是照片
     */
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        //获取拓展名
        String fileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (fileEnd.equals("jpg") || fileEnd.equals("png") || fileEnd.equals("gif")
                || fileEnd.equals("jpeg") || fileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }
}
