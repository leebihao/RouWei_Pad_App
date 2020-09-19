package com.scinan.sdk.bluetooth;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.error.ResponseTransferException;
import com.scinan.sdk.error.WaitNextTransferException;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.hardware.Smart6120Api;
import com.scinan.sdk.hardware.Smart6120DataCmd;
import com.scinan.sdk.hardware.Smart6120DeliveryHouse;
import com.scinan.sdk.hardware.Smart6120Transfer;
import com.scinan.sdk.util.ByteUtil;
import com.scinan.sdk.util.LogUtil;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by lijunjie on 2017/12/20.
 */

public class S6120BleController implements IBluzDevice.OnConnectionListener {
    private static S6120BleController sController = null;
    private BLEBluzDevice mBLEBluzDevice;
    private Smart6120DeliveryHouse mSmart6120DeliveryHouse;

    private Status mStatus = Status.IDEL;

    private S6120BleControllerCallback mS6120BleControllerCallback;

    private BluetoothDevice mBluetoothDevice;

    private Stack<S6120BleControllerCallback> mCallbackStack;

    private S6120WriteThread mS6120WriteThread;

    public interface S6120BleControllerCallback {
        //涉及到UI的需要反馈到外部
        boolean on6120BTEnable();
        void onConnected(BluetoothDevice bluetoothDevice);
        void onDisconnected(BluetoothDevice bluetoothDevice);
        void onPush(BluetoothDevice bluetoothDevice, Smart6120DataCmd hardwareCmd);
        void onLogD(String log);
        void onLogE(String log);
    }

    enum Status {
        IDEL,
        Connecting,
        Disconnecting,
        Connected,
        Authed
    }

    private S6120BleController(Context context) {
        mBLEBluzDevice = new BLEBluzDevice(context);
        mBLEBluzDevice.registerOnConnectionListener(this);
        mSmart6120DeliveryHouse = new Smart6120DeliveryHouse();
        mCallbackStack = new Stack<S6120BleControllerCallback>();
        mS6120WriteThread = new S6120WriteThread();
        mS6120WriteThread.start();
    }

    public static synchronized S6120BleController getController(Context context) {
        if (sController == null)
            sController = new S6120BleController(context.getApplicationContext());
        return sController;
    }

    public void pushCallback(S6120BleControllerCallback callback) {
        synchronized (mCallbackStack) {
            mCallbackStack.push(callback);
            mS6120BleControllerCallback = mCallbackStack.peek();
        }
    }

    public void popCallback() {
        synchronized (mCallbackStack) {
            mCallbackStack.pop();
            if (mCallbackStack.empty()) {
                mS6120BleControllerCallback = null;
            } else {
                mS6120BleControllerCallback = mCallbackStack.peek();
            }
        }
    }

    //程序退出时候用
    public void finish() {
        mBLEBluzDevice.unRegisterOnConnectionListener(this);
        mCallbackStack.clear();
        mS6120WriteThread.finish();
        sController = null;
    }

    public boolean connect(String bluetooth_address) {

        if (!mS6120BleControllerCallback.on6120BTEnable()) {
            return false;
        }

        if (mS6120BleControllerCallback == null) {
            logE("why no callback in connect");
            return false;
        }

        BluetoothDevice bluetoothDevice = mBLEBluzDevice.getRemoteDevice(bluetooth_address);

        if (mBluetoothDevice != null && !TextUtils.equals(mBluetoothDevice.getAddress(), bluetoothDevice.getAddress())) {
            logE(String.format("Already one bluetooth (%s)device is in use, return (%s)", mBluetoothDevice.getAddress(), bluetoothDevice.getAddress()));
            return false;
        }
        synchronized (mStatus) {
            if (mStatus != Status.IDEL) {
                log("bluetooth status is not idel");
                return false;
            }
        }

        log("begin to connect " + bluetooth_address);
        mBluetoothDevice = bluetoothDevice;
        mBLEBluzDevice.connect(mBluetoothDevice);
        return true;
    }

    public void disconnect() {
        if (mBluetoothDevice == null) {
            return;
        }

        if (!isAvailableStatus()) {
            mStatus = Status.IDEL;
            return;
        }

        mStatus = Status.Disconnecting;
        mBLEBluzDevice.disconnect(mBluetoothDevice);
    }

    public boolean write(HardwareCmd cmd) {
        return write(new Smart6120DataCmd(cmd.optionCodeString, cmd.sensorType, cmd.data));
    }

    public boolean write(Smart6120DataCmd cmd) {
        if (!isAvailableStatus()) {
            mS6120WriteThread.clear();
            return false;
        }

        ArrayList<Smart6120Transfer> transfers = Smart6120Api.buildRequestTransfer(cmd);
        mS6120WriteThread.write(transfers);
        log("发送队列中还有的消息条数是：" + mS6120WriteThread.size());
        return true;
    }

    public boolean checkEnable(Activity activity) {
        return mBLEBluzDevice.checkEnable(activity);
    }

    public boolean isEnable() {
        return mBLEBluzDevice.isEnabled();
    }

    boolean isHopeDevice(BluetoothDevice bluetoothDevice) {
        boolean ok =  mBluetoothDevice != null && bluetoothDevice.getAddress().equals(mBluetoothDevice.getAddress());
        LogUtil.d("isHopeDevice is " + ok + ", and mBluetoothDevice is " + mBluetoothDevice + ", bluetoothDevice is " + bluetoothDevice);
        return ok;
    }

    @Override
    public void onConnected(BluetoothDevice bluetoothDevice) {
        log("onConnected " + bluetoothDevice);
        if (!isHopeDevice(bluetoothDevice)) {
            logE("bluetooth is not compare return");
            return;
        }

        synchronized (mStatus) {
            log("connected, sleep 200ms to auth");
            mStatus = Status.Connected;
            //休眠200毫秒进行auth认证
            sleep(200);
            sendAuthCmd();
        }
    }

    @Override
    public void onDisconnected(BluetoothDevice bluetoothDevice) {
        log("onDisconnected " + bluetoothDevice);
        if (!isHopeDevice(bluetoothDevice)) {
            logE("bluetooth is not compare return");
            return;
        }
        mStatus = Status.IDEL;
        if (mS6120BleControllerCallback != null) {
            mS6120BleControllerCallback.onDisconnected(bluetoothDevice);
        }
    }

    @Override
    public void onRetryConnecting(BluetoothDevice bluetoothDevice) {
        log("onRetryConnecting " + bluetoothDevice);
        if (!isHopeDevice(bluetoothDevice)) {
            logE("bluetooth is not compare return");
            return;
        }
    }

    @Override
    public void onError(BluetoothDevice bluetoothDevice, int reason) {
        log("onError " + bluetoothDevice);
        if (!isHopeDevice(bluetoothDevice)) {
            logE("bluetooth is not compare return");
            return;
        }
        mStatus = Status.IDEL;
        if (mS6120BleControllerCallback != null) {
            mS6120BleControllerCallback.onDisconnected(bluetoothDevice);
        }
    }

    @Override
    public void onReceive(BluetoothDevice bluetoothDevice, int type, BluetoothGattCharacteristic characteristics, byte[] data) {
        log("onReceive " + bluetoothDevice);
        if (type == BLEBluzDevice.RECEIVE_TYPE_WRITE) {
            log("WRITE<------" + ByteUtil.bytes2HexString(data));
            //我只关心REQ的WRITE，RSP的滚犊子
            if (mS6120WriteThread.getLastHex().equals(ByteUtil.bytes2HexString(data))) {
                mS6120WriteThread.go();
            }
            return;
        }

        if (type != BLEBluzDevice.RECEIVE_TYPE_CHA) {
            return;
        }

        log("NOTIFY<------" + ByteUtil.bytes2HexString(data));

        if (!isHopeDevice(bluetoothDevice)) {
            logE("bluetooth is not compare return");
            return;
        }

        if (!isAvailableStatus()) {
            log("onReceive but our status is not available , mStatus is " + mStatus);
            return;
        }

        try {
            Smart6120Transfer transfer = Smart6120Transfer.parse(data);

            if (transfer.isEmpty()) {
                log("receive one rubbish transfer");
                return;
            }
            if (transfer.isResponse()) {
                //不管成功失败，老子不管了
                return;
            }

            //接下来，先发回执，再进行核心业务处理
            boolean responseResult = true;
            Smart6120DataCmd cmd = null;
            try {
                cmd = mSmart6120DeliveryHouse.deliverCmd(transfer);
                logE("cmd is " + cmd.toString());
            } catch (WaitNextTransferException e) {
                logE(e.getMessage());
                responseResult = true;
            } catch (ResponseTransferException e) {
                logE(e.getMessage());
                return;
            } catch (Exception e) {
                logE(e.getMessage());
                responseResult = false;
            }

            //发送回执
            sendRspCmd(responseResult, transfer);

            if (cmd == null) {
                return;
            }

            log("response send ok, we go2 parse cmd <--------" + cmd);

            //回执已发完，现在我们来自己玩业务
            switch (cmd.optionCode) {
                //收到版本号的协议，流程往下走
                case Smart6120Api.OPTIONCODE_VERSION:
                    mStatus = Status.Authed;
                    if (mS6120BleControllerCallback != null) {
                        mS6120BleControllerCallback.onConnected(mBluetoothDevice);
                    }
                    break;
                default:
                    if (mS6120BleControllerCallback != null) {
                        mS6120BleControllerCallback.onPush(mBluetoothDevice, cmd);
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(String log) {
        if (mS6120BleControllerCallback != null) {
            mS6120BleControllerCallback.onLogD(log);
        }
    }

    private void logE(Throwable log) {
        if (mS6120BleControllerCallback != null) {
            mS6120BleControllerCallback.onLogE(LogUtil.getExceptionString(log));
        }
    }

    private void logE(String log) {
        if (mS6120BleControllerCallback != null) {
            mS6120BleControllerCallback.onLogE(log);
        }
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

    private void sleep(long time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (InterruptedException e) {
        }
    }

    //新版的SDKwrite不可能写入失败，把重试放到底层处理，所以write返回值没意义，取消
    private void sendRspCmd(boolean result, Smart6120Transfer transfer) throws Exception {
        String hex = Smart6120Api.buildResponseTransfer(result, transfer).toString();
        synchronized (mBLEBluzDevice) {
            mBLEBluzDevice.write(mBluetoothDevice, ByteUtil.hex2Bytes(hex));
            log("RSP------>" + hex);
        }
    }

    private void sendAuthCmd() {
        //必须清空掉所有任务队列
        mS6120WriteThread.clear();
        //蓝牙已经连上，进行认证
        ArrayList<Smart6120Transfer> verionTransfers = Smart6120Api.buildQueryVersionTransfers();
        mS6120WriteThread.write(verionTransfers);
    }


    class S6120WriteThread extends Thread {
        final BlockingQueue<String> queue;
        boolean finish;
        Object mHoldLock = new Object();
        String lastHex;

        S6120WriteThread() {
            queue = new PriorityBlockingQueue<String>();
            finish = false;
        }

        void finish() {
            finish = true;
        }

        @Override
        public void run() {
            log("S6120WriteThread starting");
            while (!finish) {
                try {
                    lastHex = queue.take();
                    synchronized (mBLEBluzDevice) {
                        mBLEBluzDevice.write(mBluetoothDevice, ByteUtil.hex2Bytes(lastHex));
                        log("REQ------>" + lastHex);
                        log("发送队列中还有的消息条数是：" + queue.size());
                    }
                    synchronized (mHoldLock) {
                        try {
                            mHoldLock.wait(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    logE(e);
                } catch (Exception e) {
                    logE(e);
                }
            }
            LogUtil.d("S6120WriteThread ending");
        }

        void write(Smart6120Transfer transfer) {
            LogUtil.d("S6120WriteThread sending");
            write(transfer.toString());
        }

        void write(ArrayList<Smart6120Transfer> transfers) {
            for (Smart6120Transfer transfer : transfers) {
                write(transfer);
            }
        }

        void write(String hex) {
            synchronized (queue) {
                queue.add(hex);
            }
        }

        void clear() {
            synchronized (queue) {
                queue.clear();
            }
        }

        int size() {
            synchronized (queue) {
                return queue.size();
            }
        }

        void go() {
            log("gooooooooooo");
            synchronized (mHoldLock) {
                mHoldLock.notify();
            }
        }

        String getLastHex() {
            return lastHex;
        }
    }
}
