package com.scinan.sdk.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.scinan.sdk.util.ByteUtil;
import com.scinan.sdk.util.Crc8Util;
import com.scinan.sdk.util.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by lijunjie on 17/3/2.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEBluzDevice extends BaseBluzDevice {

    public static final int RECEIVE_TYPE_WRITE = 1;
    public static final int RECEIVE_TYPE_READ  = 2;
    public static final int RECEIVE_TYPE_CHA   = 3;

    BLEInputDeviceExtra[] mExtra;
    //已经连上的低功耗服务列表
    ConcurrentHashMap<String, BLEConnectedData> mConnectedBluetoothGatt;

    volatile ScanFilter mScanFilter;

    BLEOADManager mBLEOADManager;

    public BLEBluzDevice(Context context) {
        this(context, new BLEInputDeviceExtra[]{new BLEInputDeviceExtra() {

            @Override
            public UUID getReadServiceUuid() {
                return UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
            }

            @Override
            public UUID getWriteServiceUuid() {
                return UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
            }

            @Override
            public UUID getReadCharacteristic() {
                return UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
            }

            @Override
            public UUID getWriteCharacteristic() {
                return UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
            }
        }});
    }

    public BLEBluzDevice(Context context, InputDeviceExtra[] extra) {
        super(context);
        mExtra = (BLEInputDeviceExtra[])extra;
        mConnectedBluetoothGatt = new ConcurrentHashMap<String, BLEConnectedData>();
        mBLEOADManager = BLEOADManager.getInstance();
    }

    public void startDiscovery(ScanFilter filter, long timeoutTimeMillis) {
        super.startDiscovery(timeoutTimeMillis);
        mScanFilter = filter;
        mOnDiscoveryListener.onDiscoveryStarted();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @Override
    public void startDiscovery(long timeoutTimeMillis) {
        startDiscovery(null, timeoutTimeMillis);
    }

    @Override
    public void stopDiscovery() {
        super.stopDiscovery();
        //停止可能存在的低功耗扫描
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mScanFilter = null;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (!filter(scanRecord)) {
                return;
            }

            mOnDiscoveryListener.onFound(new ScanDeviceResult(device).setBle_record(scanRecord).setBle_rssi(rssi));
        }
    };

    private boolean filter(byte[] scanRecord) {
        try {
            String[] recordHex = ByteUtil.bytes2HexStrArray(scanRecord);
            LogUtil.t("receive the record is " + Arrays.toString(recordHex));

            //如果没有过滤器直接放行
            if (mScanFilter == null) {
                return true;
            }

            BLEAdvertising advertising = BLEAdvertising.parse(scanRecord);
            advertising.log();

            String manufacturerHex = advertising.getManufacturer().toString();
            if (TextUtils.isEmpty(manufacturerHex)) {
                LogUtil.t("manufacturer is empty skip");
                return false;
            }

            if (manufacturerHex.length() != 20) {
                LogUtil.t("sad, manufacturer length is not compare, it's length is " + manufacturerHex.length());
                return false;
            }

            byte[] scinanData = ByteUtil.hex2Bytes(manufacturerHex.substring(0, manufacturerHex.length() - 2));
            String crc = Crc8Util.calcCrcCommon(scinanData);

            if (!TextUtils.equals(crc, manufacturerHex.substring(18))) {
                LogUtil.t("sad, this record is not scinan record, scinan crc is " + crc);
                return false;
            }
            LogUtil.t("yes, we found scinan bt record and continue filter");
            String company = manufacturerHex.substring(12, 16);
            //注意，这里的type是十六进制的，要换成10进制
            int type = ByteUtil.hex2int(manufacturerHex.substring(16, 18));

            if (!TextUtils.isEmpty(mScanFilter.getCompanyId())) {
                if (!company.equals(mScanFilter.getCompanyId())) {
                    LogUtil.t(String.format("sad, company is not compare, we need %s but found %s", mScanFilter.getCompanyId(), company));
                    return false;
                }
            }

            if (!TextUtils.isEmpty(mScanFilter.getType())) {
                if (type != Integer.valueOf(mScanFilter.getType())) {
                    LogUtil.t(String.format("sad, type is not compare, we need %s but found %s", mScanFilter.getType(), type));
                    return false;
                }
            }

            LogUtil.t("yeah, i want you!" + ByteUtil.bytes2HexString(scinanData));
            //通过层层过滤的数据才是成功的
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice, long timeout) {
        if (mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()) != null) {
            LogUtil.t("begin to connect device but this device has exists, disconnect first");
            BluetoothGatt gatt = mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).getGatt();
            if (BluetoothGatt.STATE_CONNECTED == mBluetoothManager.getConnectionState(bluetoothDevice, BluetoothProfile.GATT)) {
                LogUtil.t("connect device status is connected, return");
                return;
            } else {
                LogUtil.t("connect device in cache, but not connected, disconnect it");
                gatt.disconnect();
                gatt.close();
            }
        }

        super.connect(bluetoothDevice, timeout);

        BluetoothGatt gatt = bluetoothDevice.connectGatt(mContext, false, mBluetoothGattCallback);
        if (gatt != null) {
            LogUtil.t("AAAAAAAAAAA---->" + gatt.toString());
            mConnectedBluetoothGatt.put(bluetoothDevice.getAddress(), new BLEConnectedData(gatt));
        } else {
            mOnConnectionListener.onError(bluetoothDevice, ERROR_CONN_DIABLE);
        }

    }

    @Override
    public void connect(ArrayList<BluetoothDevice> bluetoothDevices) {

    }

    @Override
    public void disconnect(BluetoothDevice bluetoothDevice) {
        super.disconnect(bluetoothDevice);
        if (!mConnectedBluetoothGatt.containsKey(bluetoothDevice.getAddress())) {
            mOnConnectionListener.onDisconnected(bluetoothDevice);
            return;
        }
        try {
            mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).gatt.disconnect();
            mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).gatt.disconnect();
            mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).gatt.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnect(BluetoothGatt gatt) {
        if (gatt == null) {
            return;
        }
        gatt.disconnect();
        gatt.close();
    }

    public boolean isSupportOAD(BluetoothDevice bluetoothDevice) {
        if (!isConnected(bluetoothDevice)) {
            LogUtil.t("BLEBluezDevice isSupportOAD --------> connect false");
            return false;
        }

        BluetoothGatt gatt = mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).getGatt();
        return mBLEOADManager.isSupportOAD(gatt);
    }

    public void beginOAD(BluetoothDevice bluetoothDevice, BLEBinFile file, BLEOADCallback callback) {
        if (!isConnected(bluetoothDevice)) {
            callback.onCancel(bluetoothDevice);
            return;
        }
        BluetoothGatt gatt = mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).getGatt();
        mBLEOADManager.beginOTA(this, gatt, file, callback);
    }

    public void cancelOAD() {
        if (!isOADRunning()) {
            return;
        }
        mBLEOADManager.cancelOTA();
    }

    public boolean isOADRunning() {
        return mBLEOADManager.isOTARunning();
    }

    public boolean isOADCharacteristic(BluetoothGattCharacteristic characteristic) {
        return mBLEOADManager.isOADCharacteristic(characteristic);
    }

    public void queryTargetImageInfo(BluetoothDevice bluetoothDevice) throws Exception {
        if (!isConnected(bluetoothDevice)) {
            return;
        }

        BluetoothGatt gatt = mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).getGatt();
        mBLEOADManager.sendQueryTargetImageInfoCmd(this, gatt);
    }

    @Override
    public void disconnect() {
        for (BLEConnectedData data : mConnectedBluetoothGatt.values()) {
            disconnect(data.getGatt().getDevice());
        }
        mHopeConnectBluetoothDevice.clear();
        for (String address : mConnectedBluetoothGatt.keySet()) {
            removeConnectedGatt(getRemoteDevice(address));
        }
        mConnectedBluetoothDevice.clear();
        mOnDiscoveryListeners.clear();
        mOnConnectionListeners.clear();
    }

    public void enableNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enable) {
        gatt.setCharacteristicNotification(characteristic, enable);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        boolean ok = false;
        while (!ok) {
            ok = gatt.writeDescriptor(descriptor);
        }
    }

    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtil.t("onConnectionStateChange<-----gatt is " + gatt.getDevice() + ", status is " + status + ", newState is connected ? " + (newState == BluetoothProfile.STATE_CONNECTED));

            if (mConnectedBluetoothGatt.get(gatt.getDevice().getAddress()) == null) {
                LogUtil.t("why received this rubbish message");
                gatt.disconnect();
                gatt.close();
                return;
            }

            BluetoothGatt originGatt = mConnectedBluetoothGatt.get(gatt.getDevice().getAddress()).getGatt();
            LogUtil.t("originGatt is " + originGatt + ", receive gatt is " + gatt);
            if (originGatt != gatt) {
                LogUtil.t("return because originGatt is " + originGatt + ", but receive gatt is " + gatt);
                gatt.disconnect();
                gatt.close();
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                addConnectedDevice(gatt.getDevice());
                LogUtil.t("begin to discoverServices " + gatt.discoverServices() + "------->" + gatt.getDevice());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                int id = getIntfromDevice(gatt.getDevice());
                removeConnectedDevice(gatt.getDevice());
                removeConnectedGatt(gatt.getDevice());

                if (mHandler.hasMessages(id)) {
                    LogUtil.t("bt is disconnected status but we try to help reconnect");
                    mOnConnectionListener.onRetryConnecting(gatt.getDevice());
                    connect(gatt.getDevice());
                } else {
                    mOnConnectionListener.onDisconnected(gatt.getDevice());
                    mHopeConnectBluetoothDevice.remove(gatt.getDevice());
                }
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            LogUtil.t("onServicesDiscovered<-----gatt is " + gatt.getDevice() + ", status is " + status);

            if (mConnectedBluetoothGatt.get(gatt.getDevice().getAddress()) == null) {
                LogUtil.t("why received this rubbish message");
                gatt.close();
                return;
            }

            BluetoothGatt originGatt = mConnectedBluetoothGatt.get(gatt.getDevice().getAddress()).getGatt();
            LogUtil.t("originGatt is " + originGatt + ", receive gatt is " + gatt);
            if (originGatt != gatt) {
                LogUtil.t("return because originGatt is " + originGatt + ", but receive gatt is " + gatt);
                return;
            }

            if (status == BluetoothGatt.GATT_SUCCESS) {
                try {
                    BLEConnectedData bleConnectedData = mConnectedBluetoothGatt.get(gatt.getDevice().getAddress());
                    if (bleConnectedData == null) {
                        LogUtil.t("this bleConnteddata is null " + gatt.getDevice().getAddress());
                        return;
                    }

                    for (BluetoothGattService service : gatt.getServices()) {
                        LogUtil.t("device service =" + service.getUuid().toString());
                        for (BluetoothGattCharacteristic chara : service.getCharacteristics()) {
                            LogUtil.t("            device chara =" + chara.getUuid().toString());
                        }
                    }

                    for (BLEInputDeviceExtra extra : mExtra) {
                        //获取读服务
                        BluetoothGattService gattReadService = gatt.getService(extra.getReadServiceUuid());
                        if (gattReadService == null) {
                            LogUtil.t("gattReadService is not found " + extra.getReadServiceUuid());
                            continue;
                        }
                        LogUtil.t("gattReadService found and uuid is " + extra.getReadServiceUuid());
                        bleConnectedData.setReadService(gattReadService);

                        //获取读特征值
                        BluetoothGattCharacteristic readCharacteristic = gattReadService.getCharacteristic(extra.getReadCharacteristic());
                        if (readCharacteristic == null) {
                            LogUtil.t("readCharacteristic is not found " + extra.getReadCharacteristic());
                            continue;
                        }
                        LogUtil.t("readCharacteristic found and uuid is " + extra.getReadCharacteristic());
                        bleConnectedData.setRead(readCharacteristic);

                        //获取写服务
                        BluetoothGattService gattWriteService = gatt.getService(extra.getWriteServiceUuid());
                        if (gattWriteService == null) {
                            LogUtil.t("gattWriteService is not found " + extra.getReadServiceUuid());
                            continue;
                        }
                        LogUtil.t("gattWriteService found and uuid is " + extra.getReadServiceUuid());
                        bleConnectedData.setWriteService(gattReadService);

                        //获取写特征值
                        BluetoothGattCharacteristic writeCharacteristic = gattWriteService.getCharacteristic(extra.getWriteCharacteristic());
                        if (writeCharacteristic == null) {
                            LogUtil.t("writeCharacteristic is not found " + extra.getWriteCharacteristic());
                            continue;
                        }
                        LogUtil.t("writeCharacteristic found and uuid is " + extra.getWriteCharacteristic());
                        bleConnectedData.setWrite(writeCharacteristic);

                        LogUtil.t("begin enableNotification");

                        //打开通知通道
                        enableNotification(gatt, readCharacteristic, true);

                        LogUtil.t("end enableNotification");

                        //回调给上层告知真正连接成功
                        mOnConnectionListener.onConnected(gatt.getDevice());

                        if (!bleConnectedData.isEmpty()) {
                            LogUtil.t("check all service ok");
                            break;
                        }
                    }

                    if (bleConnectedData.isEmpty()) {
                        LogUtil.t("check service fail go to disconnect");
                        mOnConnectionListener.onError(gatt.getDevice(), ERROR_CONN_SERVICE_NOT_SUPPORT);
                        disconnect(gatt.getDevice());
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mOnConnectionListener.onError(gatt.getDevice(), ERROR_CONN_UNKNOWN);
                    disconnect(gatt.getDevice());
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.t("onCharacteristicRead<-----gatt is " + gatt.getDevice() + ", characteristic is " + characteristic.getUuid().toString() + ", status is " + status);
            mOnConnectionListener.onReceive(gatt.getDevice(), RECEIVE_TYPE_READ, characteristic, characteristic.getValue());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.t("onCharacteristicWrite<-----gatt is " + gatt.getDevice() + ", characteristic is " + characteristic.getUuid().toString() + ", value is " + ByteUtil.bytes2HexString(characteristic.getValue()));
            mOnConnectionListener.onReceive(gatt.getDevice(), RECEIVE_TYPE_WRITE, characteristic, characteristic.getValue());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            LogUtil.t("onCharacteristicChanged<-----gatt is " + gatt.toString() + ", characteristic is " + characteristic.getUuid().toString() + ", value is " + ByteUtil.bytes2HexString(characteristic.getValue()));
            mOnConnectionListener.onReceive(gatt.getDevice(), RECEIVE_TYPE_CHA, characteristic, characteristic.getValue());
        }
    };

    public void removeConnectedGatt(BluetoothDevice bluetoothDevice) {
        if (mConnectedBluetoothGatt.containsKey(bluetoothDevice.getAddress())) {
            mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).clear();
            mConnectedBluetoothGatt.remove(bluetoothDevice.getAddress());
        }
    }

    protected BLEConnectedData getConnectedData(BluetoothDevice bluetoothDevice) {
        BLEConnectedData data = mConnectedBluetoothGatt.get(bluetoothDevice.getAddress());
        if (data == null || data.isEmpty()) {
            return null;
        }
        return data;
    }

    @Override
    public boolean write(BluetoothDevice bluetoothDevices, byte[] var1) throws Exception {
        BLEConnectedData data = mConnectedBluetoothGatt.get(bluetoothDevices.getAddress());
        if (data == null || data.isEmpty()) {
            throw new Exception("bluetooth device status error");
        }
        return write(bluetoothDevices, data.getWrite(), var1);
    }

    public boolean ensureWrite(BluetoothDevice bluetoothDevices, byte[] var1) throws Exception {
        boolean ok = false;
        while (!ok) {
            ok = write(bluetoothDevices, var1);
            LogUtil.t("write result is " + ok);
        }
        return true;
    }

    public boolean write(BluetoothDevice bluetoothDevices, BluetoothGattCharacteristic characteristic,  byte[] var1) throws Exception {
        BLEConnectedData data = mConnectedBluetoothGatt.get(bluetoothDevices.getAddress());
        if (data == null || data.isEmpty()) {
            throw new Exception("bluetooth device status error");
        }
        synchronized (characteristic) {
            LogUtil.t("write-------->" + ByteUtil.bytes2HexString(var1));
            characteristic.setValue(var1);
            return data.getWriteThread().writeCharacteristic(characteristic);
        }
    }

    public boolean ensureWrite(BluetoothDevice bluetoothDevices, BluetoothGattCharacteristic characteristic,  byte[] var1) throws Exception {
        boolean ok = false;
        while (!ok) {
            ok = write(bluetoothDevices, characteristic, var1);
            LogUtil.t("write result is " + ok);
        }
        return true;
    }

    @Override
    public int getConnectDeviceType() {
        return ConnectDeviceFactory.BT_TYPE_BLE;
    }

    @Override
    public boolean isReallyConnected(BluetoothDevice bluetoothDevice) {
        BLEConnectedData data = mConnectedBluetoothGatt.get(bluetoothDevice.getAddress());
        if (data == null) {
            return false;
        }

        return !data.isEmpty();
    }

    public boolean isConnecting(BluetoothDevice bluetoothDevice) {
        BLEConnectedData data = mConnectedBluetoothGatt.get(bluetoothDevice.getAddress());
        if (data == null) {
            return false;
        }

        return data.isEmpty();
    }

    @Override
    protected void clear(BluetoothDevice bluetoothDevice) {
        if (mConnectedBluetoothGatt != null && bluetoothDevice != null
                && bluetoothDevice.getAddress() != null
                && mConnectedBluetoothGatt.containsKey(bluetoothDevice.getAddress())) {
            mConnectedBluetoothGatt.get(bluetoothDevice.getAddress()).clear();
            mConnectedBluetoothGatt.remove(bluetoothDevice.getAddress());
        }
    }

    class BLEConnectedData implements Serializable {
        BluetoothGatt gatt;
        BluetoothGattService readService;
        BluetoothGattService writeService;
        BluetoothGattCharacteristic read;
        BluetoothGattCharacteristic write;
        BLEWriteThread writeThread;

        public BLEConnectedData(BluetoothGatt gatt) {
            this.gatt = gatt;
            this.writeThread = new BLEWriteThread(gatt);
            this.writeThread.start();
        }

        public BluetoothGatt getGatt() {
            return gatt;
        }

        public void setGatt(BluetoothGatt gatt) {
            this.gatt = gatt;
        }

        public BluetoothGattCharacteristic getRead() {
            return read;
        }

        public void setRead(BluetoothGattCharacteristic read) {
            this.read = read;
        }

        public BluetoothGattCharacteristic getWrite() {
            return write;
        }

        public void setWrite(BluetoothGattCharacteristic write) {
            this.write = write;
        }

        public BluetoothGattService getReadService() {
            return readService;
        }

        public void setReadService(BluetoothGattService readService) {
            this.readService = readService;
        }

        public BluetoothGattService getWriteService() {
            return writeService;
        }

        public void setWriteService(BluetoothGattService writeService) {
            this.writeService = writeService;
        }

        public BLEWriteThread getWriteThread() {
            return writeThread;
        }

        public boolean isEmpty() {
            return gatt == null || readService == null || writeService == null || read == null || write == null;
        }

        public void clear() {
            writeThread.interrupt();
            writeThread = null;
            gatt = null;
            readService = null;
            writeService = null;
            read = null;
            write = null;
        }
    }

    class Characteristic implements Comparable {
        BluetoothGattCharacteristic characteristic;
        Characteristic(BluetoothGattCharacteristic c) {
            characteristic = c;
        }

        public BluetoothGattCharacteristic getCharacteristic() {
            return characteristic;
        }

        @Override
        public int compareTo(Object another) {
            return -1;
        }
    }

    class BLEWriteThread extends Thread {
        final BlockingQueue<Characteristic> queue;
        final BluetoothGatt gatt;

        BLEWriteThread(BluetoothGatt g) {
            gatt = g;
            queue = new PriorityBlockingQueue<Characteristic>();
        }
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    LogUtil.d("BLEWriteThread starting");
                    Characteristic c = queue.take();
                    LogUtil.d("BLEWriteThread receiving");
                    sleep(10);
                    BluetoothGattCharacteristic characteristic = c.getCharacteristic();
                    boolean ok = false;
                    while (!ok) {
                        ok = gatt.writeCharacteristic(characteristic);
                        LogUtil.t("write result in looper is " + ok);
                        sleep(50);
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    break;
                }
            }
            LogUtil.d("BLEWriteThread ending");
        }

        boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
            LogUtil.d("BLEWriteThread sending");
            return queue.add(new Characteristic(characteristic));
        }
    }

}
