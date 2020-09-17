/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.scinan.sdk.config.Configuration;

import java.util.List;

/**
 * Created by lijunjie on 16/9/23.
 */
public class SNLocationManager implements LocationListener {

    private static SNLocationManager sInstance;

    private SNLocationManager() {
    }

    public static SNLocationManager getInstance() {
        if (sInstance == null) {
            sInstance = new SNLocationManager();
        }
        return sInstance;
    }

    public void requestLocation() {
        LocationManager locationManager = (LocationManager) Configuration.getContext().getSystemService(Context.LOCATION_SERVICE);
        //获取手机支持的provider，未必所有手机都有network或者gps的provider
        List<String> providers = locationManager.getAllProviders();

        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtil.d("onLocationChanged and location is " + location);
        ((LocationManager) Configuration.getContext().getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
