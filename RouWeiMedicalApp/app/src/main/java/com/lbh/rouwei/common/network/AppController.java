package com.lbh.rouwei.common.network;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.lbh.rouwei.R;
import com.scinan.sdk.api.v2.agent.SensorAgent;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.lan.v1.FetchLANDataCallback;
import com.scinan.sdk.lan.v1.LANRequestHelper;
import com.scinan.sdk.service.IPushService;
import com.scinan.sdk.service.PushService;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.ToastUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lijunjie on 16/1/17.
 */
public class AppController implements FetchDataCallback, FetchLANDataCallback {

    private static AppController sController = null;
    private CopyOnWriteArrayList<ControllerCallback> mFetchDataListeners;
    private SensorAgent mSensorAgent;
    private LANRequestHelper mLANRequestHelper;
    private Context mContext;
    private IPushService mPushService;

    private AppController(Context context) {
        mContext = context.getApplicationContext();
        mFetchDataListeners = new CopyOnWriteArrayList<ControllerCallback>();
        mSensorAgent = new SensorAgent(context);
        mSensorAgent.registerAPIListener(this);

        mLANRequestHelper = LANRequestHelper.getInstance(mContext);

        // bind push service
        Intent bindPushService = new Intent(mContext, PushService.class);
        bindPushService.setAction(Constants.ACTION_LISTEN_PUSH_STATUS);
        mContext.bindService(bindPushService, mPushServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public static synchronized AppController getController(Context context) {
        if (sController == null)
            sController = new AppController(context.getApplicationContext());
        return sController;
    }

    public void sendCommand(int optionCode, String deviceId, String value) {
        sendCommand(optionCode, deviceId, value, true);
    }

    public void sendCommand(int optionCode, String deviceId, String value, boolean useLanConnection) {
        LogUtil.d("option code=" + optionCode + ",deviceId=" + deviceId + ", value=" + value + ",lan=" + useLanConnection);
        if (optionCode < 0) {
            LogUtil.e("error request id is " + optionCode);
            return;
        }

        if (isPushConnected()) {
            LogUtil.d("push connected use network to send the command");
            mSensorAgent.controlSensor(deviceId, optionCode, "1", value);
        } else if (mLANRequestHelper.getConnection(deviceId) != null && mLANRequestHelper.getConnection(deviceId).isConnected() && useLanConnection) {
            LogUtil.d("push disabled use lan to send the command");
            mLANRequestHelper.control(optionCode, deviceId, value, this);
        } else if (AndroidUtil.isNetworkEnabled(mContext)) {
            connectPush();
            ToastUtil.showMessage(mContext, R.string.local_service_not_ready);
        } else {
            ToastUtil.showMessage(mContext, R.string.network_error);
        }
    }

    public void registerAPIListener(ControllerCallback listener) {
        if (!mFetchDataListeners.contains(listener)) {
            mFetchDataListeners.add(listener);
        }
    }

    public void unRegisterAPIListener(ControllerCallback listener) {
        if (mFetchDataListeners.contains(listener)) {
            mFetchDataListeners.remove(listener);
        }
    }

    private void connectPush() {
        try {
            LogUtil.d("AppController connectPush =========");
            mPushService.connectPush();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
    }

    private boolean isPushConnected() {
        try {
            return mPushService.isPushConnected();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return false;
    }

    ServiceConnection mPushServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPushService = IPushService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPushService = null;
        }
    };

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
        notifyCallbacks(api - RequestHelper.API_SENSOR_CONTROL, true, 1, responseBody);
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        notifyCallbacks(api - RequestHelper.API_SENSOR_CONTROL, false, 1, responseBody);
    }

    @Override
    public void OnFetchLANDataSuccess(int api, String responseBody) {
        notifyCallbacks(api, true, 2, responseBody);
    }

    @Override
    public void OnFetchLANDataFailed(int api, Throwable error) {
        notifyCallbacks(api, false, 2, error == null ? "" : error.getMessage());
    }

    private void notifyCallbacks(int optionCode, boolean success, int protocol, String msg) {
        for (ControllerCallback callback : mFetchDataListeners) {
            if (success)
                callback.OnControlSuccess(optionCode, protocol, msg);
            else
                callback.OnControlFailed(optionCode, protocol, msg);
        }
    }

    public interface ControllerCallback {
        void OnControlSuccess(int optionCode, int protocol, String response);
        void OnControlFailed(int optionCode, int protocol, String error);
    }
}
