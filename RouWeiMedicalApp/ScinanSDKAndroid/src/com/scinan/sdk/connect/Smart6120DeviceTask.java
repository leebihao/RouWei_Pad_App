/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.bluetooth.BLEBluzDevice;
import com.scinan.sdk.bluetooth.IBluzDevice;
import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.error.ResponseTransferException;
import com.scinan.sdk.error.WaitNextTransferException;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.hardware.Smart6120Api;
import com.scinan.sdk.hardware.Smart6120DataCmd;
import com.scinan.sdk.hardware.Smart6120DeliveryHouse;
import com.scinan.sdk.hardware.Smart6120Transfer;
import com.scinan.sdk.interfaces.ConfigDeviceCallback;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;
import com.scinan.sdk.util.ByteUtil;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lijunjie on 15/12/11.
 */
public class Smart6120DeviceTask extends ScinanConfigDeviceTask implements IBluzDevice.OnConnectionListener {

    private String mAPSSID, mAPPasswd, mAPNetworkId, mHardwareCmd;

    private Object mLock = new Object();

    private BLEBluzDevice mBLEBluzDevice;

    private BluetoothDevice mBluetoothDevice;

    private Smart6120DeliveryHouse mSmart6120DeliveryHouse;

    private CopyOnWriteArrayList<Smart6120Transfer> mTransferQueue;

    //发送ssid，pwd成功的标志位，不是最终成功与否的标志
    private boolean isSendSSIDSuccess;
    private boolean isNeedCloseBLE;

    private Status mStatus = Status.IDEL;

    enum Status {
        IDEL,
        Connecting,
        Disconnecting,
        Connected,
        Authed
    }

    public Smart6120DeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
        mBLEBluzDevice = new BLEBluzDevice(context);
        mSmart6120DeliveryHouse = new Smart6120DeliveryHouse();
        mTransferQueue = new CopyOnWriteArrayList<Smart6120Transfer>();
    }

    public Smart6120DeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
        super(context, scinanDevice, callback);
        mBLEBluzDevice = new BLEBluzDevice(context);
        mSmart6120DeliveryHouse = new Smart6120DeliveryHouse();
        mTransferQueue = new CopyOnWriteArrayList<Smart6120Transfer>();
    }

    @Override
    public Void doInBackground(String... params) {
        ConnectWakeLock.acquireWakeLock(mContext);
        publishProgress(String.valueOf(STEP_START));
        mBluetoothDevice = null;
        mBLEBluzDevice.registerOnConnectionListener(this);
        mBluetoothDevice = mBLEBluzDevice.getRemoteDevice(params[0]);
        mAPSSID = params[1];
        mAPPasswd = params[2];
        mAPNetworkId = params[3];
        mHardwareCmd = params[4];
        isNeedCloseBLE = params.length > 5 ? Boolean.valueOf(params[5]) : false;
        logT("params is mBluetoothDevice="+ mBluetoothDevice + ",mAPSSID=" + mAPSSID + ",mAPPasswd=" + mAPPasswd + ",mAPNetworkId=" + mAPNetworkId + ",CompanyID=" + (mScinanConnectDevice == null ? "null" : mScinanConnectDevice.getCompanyId()));
        mStatus = Status.IDEL;
        connect();
        ConnectWakeLock.releaseWakeLock();
        return null;
    }

    private void holdTask(long millis) {
        logT("holdTask the task");
        synchronized (mLock) {
            try {
                mLock.wait(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseTask() {
        logT("release the task");
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }

    private void sleep(long time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (InterruptedException e) {
            publishProgress(String.valueOf(STEP_FAIL), e.getMessage());
        }
    }

    void connect2() {
        mBLEBluzDevice.connect(mBluetoothDevice, 5000);
        holdTask(10000);

        while (!isCancelled()) {
            try {
                mBLEBluzDevice.write(mBluetoothDevice, ByteUtil.hex2Bytes("0200012F4231302F312F30910A"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            sleep(60);
        }
    }

    StringBuffer log = new StringBuffer();

    private void connect() {
        logT("===begin to connect ble " + mBluetoothDevice);
        log.delete(0, log.length());
        log.append("BLE部分分步计时开始：(单位毫秒)").append("\n");
        while (!isCancelled()) {
            try {

                logT("begin to connect this is in while, mStatus is " + mStatus);
                long startTime = System.currentTimeMillis();
                if (!mBLEBluzDevice.isEnabled()) {
                    mBLEBluzDevice.enable();
                    sleep(2000);
                    mStatus = Status.IDEL;
                    continue;
                }

                logT("bt check enable ok, then go2 connect ble");

                if (mStatus == Status.Connecting) {
                    logT("ble status is connecting already, hold them");
                    holdTask(8000);
                    if (!isAvailableStatus()) {
                        disconnectAndHold();
                        continue;
                    }
                }

                //如果大循环，状态还是可用的，就不重连
                if (!isAvailableStatus()) {

                    mStatus = Status.Connecting;
                    mBLEBluzDevice.connect(mBluetoothDevice, 5000);
                    //等待连接结果
                    holdTask(10000);

                    log.append("连接成功时间为：" + (System.currentTimeMillis() - startTime)).append("\n");
                    startTime = System.currentTimeMillis();
                    logT("connect result is return ,then go on, used ");
                    if (isCancelled()) {
                        break;
                    }

                    if (!isAvailableStatus()) {
                        logT("ble connect is timeout,  disconnect ble then sleep 1000ms");
                        disconnectAndHold();
                        continue;
                    }
                }

                logT("ble connected, sleep 100 to write msg");
                sleep(100);

                logT("ble is connected, then go2 auth");

                //必须清空掉所有任务队列
                mTransferQueue.clear();
                //蓝牙已经连上，进行认证
                ArrayList<Smart6120Transfer> verionTransfers = Smart6120Api.buildQueryVersionTransfers();
                mTransferQueue.addAll(verionTransfers);
                sendReqCmd(mTransferQueue.get(0));

                //等待auth完成
                holdTask(1000);

                if (isCancelled()) {
                    break;
                }

                //如果连不上，尝试断开蓝牙重走一下流程
                if (!isAvailableStatus()) {
                    logT("ble is not connected, disconnect them");
                    disconnectAndHold();
                    continue;
                }

                log.append("Auth成功时间为：" + (System.currentTimeMillis() - startTime)).append("\n");
                startTime = System.currentTimeMillis();

                mStatus = Status.Authed;

                logT("auth success, then go2 config network");
                //认证成功，发送配网信息
                mTransferQueue.clear();
                ArrayList<Smart6120Transfer> configTransfers = Smart6120Api.buildConfigNetworkTransfers(mAPSSID, mAPPasswd);
                mTransferQueue.addAll(configTransfers);

                sendReqCmd(mTransferQueue.get(0));
                //等待发送完成
                holdTask(2000);

                if (isCancelled()) {
                    break;
                }

                //如果连不上，尝试断开蓝牙重走一下流程
                if (!isAvailableStatus()) {
                    logT("ble is not connected, disconnect them");
                    disconnectAndHold();
                    continue;
                }


                logT("config network return, result is " + isSendSSIDSuccess);

                if (isSendSSIDSuccess) {
                    log.append("配网成功时间为：" + (System.currentTimeMillis() - startTime)).append("\n");
                    logT("AAAAAAAAAA" + log.toString());
                    logT("ssid and pwd send success, then go on");
                    if (!isNeedCloseBLE) {
                        logT("ssid and pwd send success, and no need to close ble, go2 finish");
                        isConfigSuccess = true;
                        mHardwareCmds.add(HardwareCmd.parse(mHardwareCmd));
                        publishProgress(String.valueOf(STEP_SUCCESS));
                        finish();
                        break;
                    }

                    logT("ssid and pwd send success, and need to close ble, go2 close ble");
                    mTransferQueue.clear();
                    ArrayList<Smart6120Transfer> closeTransfers = Smart6120Api.buildCloseBLETransfers();
                    mTransferQueue.addAll(closeTransfers);
                    sendReqCmd(mTransferQueue.get(0));
                    //等待发送完成
                    holdTask(1000);

                    if (isCancelled()) {
                        break;
                    }

                    //如果连不上，尝试断开蓝牙重走一下流程
                    if (!mBLEBluzDevice.isConnected(mBluetoothDevice)) {
                        logT("ble is not connected, disconnect them");
                        mBLEBluzDevice.disconnect(mBluetoothDevice);
                        continue;
                    }

                    logT("close ble returned, isConfigSuccess is " + isConfigSuccess);

                    if (isConfigSuccess) {
                        mHardwareCmds.add(HardwareCmd.parse(mHardwareCmd));
                        publishProgress(String.valueOf(STEP_SUCCESS));
                        finish();
                        break;
                    }
                }

                logT("one while do finish but failed, sleep 2 second to retry");
                sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void disconnectAndHold() {
        mBLEBluzDevice.disconnect(mBluetoothDevice);
        holdTask(2000);
        //超时时候判断强制切到IDEL
        mStatus = Status.IDEL;
    }

        @Override
        public synchronized void finish () {
            logT("begin to finish the task================");
            cancel(true);
            mBLEBluzDevice.unRegisterOnConnectionListener(this);
            mBLEBluzDevice.disconnect();
            releaseTask();
        }

        @Override
        public void onConnected (BluetoothDevice bluetoothDevice){
            logT("onConnected, and address is " + bluetoothDevice);
            if (!bluetoothDevice.getAddress().equals(mBluetoothDevice.getAddress())) {
                return;
            }
            mStatus = Status.Connected;
            releaseTask();
        }

        @Override
        public void onDisconnected (BluetoothDevice bluetoothDevice){
            logT("onDisconnected, and address is " + bluetoothDevice);
            if (!bluetoothDevice.getAddress().equals(mBluetoothDevice.getAddress())) {
                return;
            }
            mStatus = Status.IDEL;
        }

        @Override
        public void onRetryConnecting (BluetoothDevice bluetoothDevice){
            logT("onRetryConnecting, and address is " + bluetoothDevice);
            if (!bluetoothDevice.getAddress().equals(mBluetoothDevice.getAddress())) {
                return;
            }
            mStatus = Status.Connecting;
        }

        @Override
        public void onError (BluetoothDevice bluetoothDevice,int reason){
            logT("onError, and address is " + bluetoothDevice);
            if (!bluetoothDevice.getAddress().equals(mBluetoothDevice.getAddress())) {
                return;
            }
            mStatus = Status.IDEL;
        }

        @Override
        public void onReceive (BluetoothDevice bluetoothDevice,int type, BluetoothGattCharacteristic characteristics,byte[] data){
            if (type == BLEBluzDevice.RECEIVE_TYPE_WRITE) {
                logT("WRITE<------" + ByteUtil.bytes2HexString(data));
                return;
            }
            if (type != BLEBluzDevice.RECEIVE_TYPE_CHA) {
                return;
            }

            logT("NOTIFY<------" + ByteUtil.bytes2HexString(data));

            if (!isAvailableStatus()) {
                logT("onReceive but our status is not available , mStatus is " + mStatus);
                return;
            }
            if (!bluetoothDevice.getAddress().equals(mBluetoothDevice.getAddress())) {
                return;
            }

            try {
                Smart6120Transfer transfer = Smart6120Transfer.parse(data);

                if (transfer.isEmpty()) {
                    logT("receive one rubbish transfer");
                    return;
                }
                if (transfer.isResponse()) {
                    if (mTransferQueue.size() > 0) {
                        Smart6120Transfer t = mTransferQueue.get(0);
                        //对方回复上一条发失败了，重发
                        if (transfer.getResult() != 0) {
                            sendReqCmd(t);
                            return;
                        }
                        //对方回复上一条发成功了
                        mTransferQueue.remove(0);
                        if (mTransferQueue.size() > 0) {
                            sendReqCmd(mTransferQueue.get(0));
                        }
                    }
                    return;
                }

                //接下来，先发回执，再进行核心业务处理
                boolean responseResult = true;
                Smart6120DataCmd cmd = null;
                try {
                    cmd = mSmart6120DeliveryHouse.deliverCmd(transfer);
                    logT("cmd is " + cmd.toString());
                } catch (WaitNextTransferException e) {
                    e.printStackTrace();
                    logT(e.getMessage());
                    responseResult = true;
                } catch (ResponseTransferException e) {
                    e.printStackTrace();
                    logT(e.getMessage());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    logT(e.getMessage());
                    responseResult = false;
                }

                System.out.println("responseResult is " + responseResult);
                //发送回执
                sendRspCmd(responseResult, transfer);

                //发送回执必须等待15毫秒进行下一步
                //sleep(15);

                if (cmd == null) {
                    return;
                }

                logT("response send ok, we go2 parse cmd <--------" + cmd);

                //回执已发完，现在我们来自己玩业务
                switch (cmd.optionCode) {
                    //收到版本号的协议，流程往下走
                    case Smart6120Api.OPTIONCODE_VERSION:
                        releaseTask();
                        break;
                    case Smart6120Api.OPTIONCODE_CONFIG:
                        if (TextUtils.equals("1", cmd.data)) {
                            isSendSSIDSuccess = true;
                        }
                        releaseTask();
                        break;
                    case Smart6120Api.OPTIONCODE_CLOSE_BLE:
                        if (TextUtils.equals("0", cmd.data)) {
                            isConfigSuccess = true;
                        }
                        releaseTask();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //发送这里必须成功，SDK改过机制了
        private void sendReqCmd(Smart6120Transfer transfer) throws Exception {
            String hex = transfer.toString();
            mBLEBluzDevice.write(mBluetoothDevice, ByteUtil.hex2Bytes(hex));
            logT("REQ------->" + hex);
        }

        //发送这里必须成功，SDK改过机制了
        private void sendRspCmd(boolean result, Smart6120Transfer transfer) throws Exception {
            String hex = Smart6120Api.buildResponseTransfer(result, transfer).toString();
            mBLEBluzDevice.write(mBluetoothDevice, ByteUtil.hex2Bytes(hex));
            logT("RSP------->" + hex);
        }

        private boolean isAvailableStatus() {
            boolean available = true;
            switch (mStatus) {
                case IDEL:
                case Connecting:
                case Disconnecting:
                    available = false;
                    break;
            }
            return mBLEBluzDevice.isConnected(mBluetoothDevice) && available;
        }
}
