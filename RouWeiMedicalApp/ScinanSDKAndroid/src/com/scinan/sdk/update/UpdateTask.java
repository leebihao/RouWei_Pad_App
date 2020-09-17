/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.update;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.scinan.sdk.R;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.FileReadUtils;
import com.scinan.sdk.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Luogical on 16/1/23.
 */
public class UpdateTask extends AsyncTask<String,Integer,Void> {
    private Boolean canceled;
    private Context mContext;
    private static final int UPDATE_DOWNLOAD_ERROR = 101;
    private static final int UPDATE_DOWNLOAD_COMPLETED = 102;
    private static final int UPDATE_DOWNLOAD_CANCELED = 103;
    public static final int UPDATE_TYPE_FOR_APP = 1;
    public static final int UPDATE_TYPE_FOR_HARDWARE = 2;
    public static final int UPDATE_TYPE_FOR_PLUGIN = 3;
    private int updateType = 0;
    private String UPDATE_SAVEFOLDER;
    private ProgressDialog updateProgressDialog;
    private final String UPDATE_SAVENAME = AndroidUtil.getTimeString(System.currentTimeMillis(),"yyyyMMddHHmmss");
    private UpdateListener mListener;

    public UpdateTask(Context context, int updateType) {
        this(context, updateType, null);
    }

    public UpdateTask(Context context, int updateType, UpdateListener listener) {
        mContext = context;
        this.updateType = updateType;
        this.UPDATE_SAVEFOLDER = checkUpdateType(updateType);
        canceled = false;
        mListener = listener;
    }


    private String checkUpdateType(int updateType) {

        switch (updateType) {
            case UPDATE_TYPE_FOR_APP:
                UPDATE_SAVEFOLDER = AndroidUtil.getDownLoadAPPPath(mContext);
                break;
            case UPDATE_TYPE_FOR_HARDWARE:
                UPDATE_SAVEFOLDER = AndroidUtil.getDownLoadHardwarePath(mContext);
                break;
            case UPDATE_TYPE_FOR_PLUGIN:
                UPDATE_SAVEFOLDER = AndroidUtil.getSmartPluginPath(mContext);
                break;
            default:
                UPDATE_SAVEFOLDER = AndroidUtil.getDownLoadPackagePath(mContext);
                break;
        }
        return UPDATE_SAVEFOLDER;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener == null)
            initDialog();
    }

    @Override
    protected Void doInBackground(String... params) {
        downloadFile(params[0]);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0]<101){
            if (mListener != null) {
                mListener.onProgress(values[0]);
            } else {
                updateProgressDialog.setProgress(values[0]);
            }
        }
        else{
            switch (values[0]){
                case UPDATE_DOWNLOAD_ERROR:
                    ToastUtil.showMessage(mContext, mContext.getString(R.string.downloading_update_error));
                    if (mListener != null) {
                        mListener.onError();
                    } else {
                        updateProgressDialog.dismiss();
                    }
                    break;
                case UPDATE_DOWNLOAD_CANCELED:
                    if (mListener != null) {
                        mListener.onCancel();
                    } else {
                        updateProgressDialog.dismiss();
                    }
                    break;
                case UPDATE_DOWNLOAD_COMPLETED:
                    if (mListener != null) {
                        try {
                            mListener.onComplete(FileReadUtils.getFile(UPDATE_SAVENAME, UPDATE_SAVEFOLDER).getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            mListener.onError();
                        }
                    } else {
                        AndroidUtil.installApp(mContext, UPDATE_SAVEFOLDER, UPDATE_SAVENAME);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mListener == null)
            updateProgressDialog.dismiss();
    }

    private void initDialog(){
        updateProgressDialog = new ProgressDialog(
                mContext);
        updateProgressDialog
                .setMessage(mContext.getResources().getString(R.string.downloading_update));
        updateProgressDialog.setIndeterminate(false);
        updateProgressDialog
                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        updateProgressDialog.setMax(100);
        updateProgressDialog.setProgress(0);
        updateProgressDialog.setCancelable(false);
        //updateProgressDialog.setCanceledOnTouchOutside(false);
        updateProgressDialog.show();
    }


    private void downloadFile(String url){


        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();

            File downFile = FileReadUtils.getFile(UPDATE_SAVENAME, UPDATE_SAVEFOLDER);
            //判断更新文件是否存在，存在则删除旧文件
            if (downFile.exists()) {

                downFile.delete();
            }

            FileOutputStream fos = new FileOutputStream(downFile);

            int count = 0;
            byte buf[] = new byte[4096];

            do {

                int numread = is.read(buf);
                count += numread;
                int progress = (int) (((float) count / length) * 100);
                publishProgress(progress);
                if (numread <= 0) {

                    publishProgress(UPDATE_DOWNLOAD_COMPLETED);

                    break;
                }
                fos.write(buf, 0, numread);
            } while (!canceled);
            if (canceled) {

                publishProgress(UPDATE_DOWNLOAD_CANCELED);
            }
            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            publishProgress(UPDATE_DOWNLOAD_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(UPDATE_DOWNLOAD_ERROR);
        }
    }

    public interface UpdateListener {
        void onProgress(int progress);
        void onCancel();
        void onComplete(final String path);
        void onError();
    }

}
