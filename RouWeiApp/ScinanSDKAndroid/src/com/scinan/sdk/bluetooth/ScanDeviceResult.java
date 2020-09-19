package com.scinan.sdk.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by lijunjie on 17/3/2.
 */

//这是一个公用的扫描结果接口
public class ScanDeviceResult implements Parcelable {

    //这是公共的返回实例
    BluetoothDevice bluetoothDevice;

    String bluetooth_name;
    String bluetooth_address;

    //下面是ble特有的返回实例
    int ble_rssi;
    byte[] ble_record;

    public ScanDeviceResult(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.bluetooth_name = bluetoothDevice.getName();
        this.bluetooth_address = bluetoothDevice.getAddress();
    }

    protected ScanDeviceResult(Parcel in) {
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        ble_rssi = in.readInt();
        ble_record = in.createByteArray();
        bluetooth_name = in.readString();
        bluetooth_address = in.readString();

    }

    public static final Creator<ScanDeviceResult> CREATOR = new Creator<ScanDeviceResult>() {
        @Override
        public ScanDeviceResult createFromParcel(Parcel in) {
            return new ScanDeviceResult(in);
        }

        @Override
        public ScanDeviceResult[] newArray(int size) {
            return new ScanDeviceResult[size];
        }
    };

    @Deprecated
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.bluetooth_name = bluetoothDevice.getName();
        this.bluetooth_address = bluetoothDevice.getAddress();
    }

    public int getBle_rssi() {
        return ble_rssi;
    }

    public ScanDeviceResult setBle_rssi(int ble_rssi) {
        this.ble_rssi = ble_rssi;
        return this;
    }

    public byte[] getBle_record() {
        return ble_record;
    }

    public ScanDeviceResult setBle_record(byte[] ble_record) {
        this.ble_record = ble_record;
        return this;
    }

    public String getBluetooth_name() {
        return bluetooth_name;
    }

    public void setBluetooth_name(String bluetooth_name) {
        this.bluetooth_name = bluetooth_name;
    }

    public String getBluetooth_address() {
        return bluetooth_address;
    }

    public void setBluetooth_address(String bluetooth_address) {
        this.bluetooth_address = bluetooth_address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (bluetoothDevice != null) {
            sb.append("bluetoothDevice is <------" + bluetooth_address + "/" + bluetooth_name);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ScanDeviceResult) {
            ScanDeviceResult a = (ScanDeviceResult) o;
            return a.getBluetooth_address().equals(getBluetooth_address());
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bluetoothDevice, flags);
        dest.writeInt(ble_rssi);
        dest.writeByteArray(ble_record);
        dest.writeString(bluetooth_name);
        dest.writeString(bluetooth_address);
    }
}
