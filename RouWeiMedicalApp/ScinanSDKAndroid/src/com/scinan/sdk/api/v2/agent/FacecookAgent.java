/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.agent;

import android.content.Context;

import com.scinan.sdk.api.v2.base.BaseHelper;
import com.scinan.sdk.api.v2.network.RequestHelper;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by Ian on 2016/3/7.
 */
public class FacecookAgent extends BaseHelper implements Serializable {

    public FacecookAgent(Context context) {
        super(context);
    }

    public void getFoodDetail(String foodMenuId) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("food_menu_id", foodMenuId);
        RequestHelper.getInstance(mContext).getFoodDetail(params, this);
    }

    public void foodControl(String deviceId, int sensorId, String sensorType, String foodMenuId) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("device_id", deviceId);
        params.put("sensor_id", String.format("%02d", sensorId));
        params.put("sensor_type", sensorType);
        params.put("food_menu_id", foodMenuId);
        RequestHelper.getInstance(mContext).controlFood(sensorId, params, this);
    }

    public void foodShare(String foodMenuId) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("food_menu_id", foodMenuId);
        RequestHelper.getInstance(mContext).foodShard(params, this);
    }

    public void getFoodList(String page) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("page_number", page);
        RequestHelper.getInstance(mContext).getFoodList(params, this);
    }

    public void getFoodList(String searchWord, String page) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("page_number", page);
        params.put("food_menu_name", searchWord);
        RequestHelper.getInstance(mContext).getFoodList(params, this);
    }

    public void getFoodFavoritelist(String page) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("page_number", page);
        RequestHelper.getInstance(mContext).getFoodFavoriteList(params, this);
    }

    public void addFoodFavorite(String foodMenuId) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("food_menu_id", foodMenuId);
        params.put("action", "1");
        RequestHelper.getInstance(mContext).setFoodFavorite(params, this);
    }

    public void removeFoodFavorite(String foodMenuId) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("food_menu_id", foodMenuId);
        params.put("action", "0");
        RequestHelper.getInstance(mContext).setFoodFavorite(params, this);
    }

    public void getFoodMarquee() {
        TreeMap<String, String> params = new TreeMap<String, String>();
        RequestHelper.getInstance(mContext).getFoodMarquee(params, this);
    }


}
