package com.scinan.sdk.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.scinan.sdk.util.LogUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by lijunjie on 17/4/26.
 */

public class BLEOADManager {

    protected static final UUID UUID_OAD_SERVICE                            = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");

    protected static final UUID UUID_OAD_IDENTIFY_CHA                       = UUID.fromString("f000ffc1-0451-4000-b000-000000000000");
    protected static final UUID UUID_OAD_BLOCK_CHA                          = UUID.fromString("f000ffc2-0451-4000-b000-000000000000");

    private static final int OAD_IMG_HDR_SIZE = 8;

    private static BLEOADManager sInstance;
    private Timer mTimer;
    private OTATimerTask mOTATimerTask;
    private volatile short mCurrentBlock;
    private volatile int mCurrentByte;
    private volatile short mMaxBlocks;
    private byte[] mOadBuffer = new byte[BLEBinFile.OAD_BLOCK_SIZE + 2];

    private BLEOADManager() {
        mTimer = new Timer();
    }

    protected static BLEOADManager getInstance() {
        if (sInstance == null) {
            synchronized (BLEOADManager.class) {
                if (sInstance == null) {
                    sInstance = new BLEOADManager();
                }
            }
        }
        return sInstance;
    }

    protected void sendQueryTargetImageInfoCmd(BLEBluzDevice device, BluetoothGatt gatt) throws Exception {
        LogUtil.t("sendQueryTargetImageInfoCmd------>" + device);
        Services services = getOADService(gatt);
        device.enableNotification(gatt, services.oadIdentify, true);
        device.ensureWrite(gatt.getDevice(), services.oadIdentify, new byte[] {(byte) 0});
        device.ensureWrite(gatt.getDevice(), services.oadIdentify, new byte[] {(byte) 1});
    }

    protected boolean isSupportOAD(BluetoothGatt gatt) {
        return getOADService(gatt) != null;
    }

    protected boolean isOADCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            return false;
        }

        return UUID_OAD_IDENTIFY_CHA.toString().equals(characteristic.getUuid().toString());
    }

    protected void beginOTA(final BLEBluzDevice device, final BluetoothGatt gatt, final BLEBinFile file, final BLEOADCallback callback) {
        LogUtil.t("beginOTA------>" + device);
        try {
            final Services services = getOADService(gatt);
            byte[] buf = new byte[OAD_IMG_HDR_SIZE + 2 + 2];
            buf[0] = Conversion.loUint16(file.getVer());
            buf[1] = Conversion.hiUint16(file.getVer());
            buf[2] = Conversion.loUint16(file.getLen());
            buf[3] = Conversion.hiUint16(file.getLen());
            System.arraycopy(file.getUid(), 0, buf, 4, 4);

            // Send image notification
            device.ensureWrite(gatt.getDevice(), services.oadIdentify, buf);

            resetOTAStatus();
            mMaxBlocks = (short) (file.getLen() / (file.OAD_BLOCK_SIZE / file.HAL_FLASH_WORD_SIZE));

            mOTATimerTask = new OTATimerTask(device, services.oadBlock, gatt, file, callback);
            mTimer.scheduleAtFixedRate(mOTATimerTask, 0, BLEBinFile.PKT_INTERVAL);


        } catch (Exception e) {
            e.printStackTrace();
            callback.onCancel(gatt.getDevice());
        }
    }

    protected void cancelOTA() {
        resetOTAStatus();
    }

    protected boolean isOTARunning() {
        return mOTATimerTask != null;
    }

    private void resetOTAStatus() {
        if (mOTATimerTask != null) {
            mOTATimerTask.cancel();
            mTimer.purge();
            mOTATimerTask = null;
        }
        mCurrentBlock = 0;
        mCurrentByte = 0;
    }

    private class OTATimerTask extends TimerTask {

        BLEBluzDevice d;
        BluetoothGatt g;
        BLEBinFile f;
        BLEOADCallback c;
        BluetoothGattCharacteristic b;

        OTATimerTask(BLEBluzDevice device, BluetoothGattCharacteristic characteristic, BluetoothGatt gatt, BLEBinFile file, BLEOADCallback callback) {
            d = device;
            g = gatt;
            f = file;
            c = callback;
            b = characteristic;
        }

        @Override
        public void run() {

            if (mCurrentBlock < mMaxBlocks) {
                // Prepare block
                mOadBuffer[0] = Conversion.loUint16(mCurrentBlock);
                mOadBuffer[1] = Conversion.hiUint16(mCurrentBlock);
                System.arraycopy(f.getSource(), mCurrentByte, mOadBuffer, 2, BLEBinFile.OAD_BLOCK_SIZE);

                // Send block
                try {
                    boolean ok = d.write(g.getDevice(), b, mOadBuffer);
                    if (ok) {
                        mCurrentBlock++;
                        mCurrentByte += BLEBinFile.OAD_BLOCK_SIZE;
                        c.onProgress(g.getDevice(), (mCurrentBlock * 100) / mMaxBlocks);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    c.onCancel(g.getDevice());
                    resetOTAStatus();
                }
            } else {
                c.onSuccess(g.getDevice());
                resetOTAStatus();
            }
        }
    }

    private Services getOADService(BluetoothGatt gatt) {
        try {
            BluetoothGattService oadService = gatt.getService(UUID_OAD_SERVICE);
            BluetoothGattCharacteristic oadIdentify = oadService.getCharacteristic(UUID_OAD_IDENTIFY_CHA);
            BluetoothGattCharacteristic oadBlock = oadService.getCharacteristic(UUID_OAD_BLOCK_CHA);
            if (oadIdentify == null || oadBlock == null ){ // || ccConnReq == null) {
                throw new Exception();
            }
            Services service = new Services();
            service.oadService = oadService;
            service.oadIdentify = oadIdentify;
            service.oadBlock = oadBlock;
            return service;
        } catch (Exception e) {
            LogUtil.t(e);
            e.printStackTrace();
        }

        return null;
    }

    class Services {
        BluetoothGattService oadService;
        BluetoothGattCharacteristic oadIdentify;
        BluetoothGattCharacteristic oadBlock;
    }
}
