package com.scinan.sdk.bluetooth;

import com.scinan.sdk.util.ByteUtil;
import com.scinan.sdk.util.LogUtil;

import java.io.File;
import java.io.Serializable;

/**
 * Created by lijunjie on 17/4/26.
 */

public class BLEBinFile extends File implements Serializable {
    public static final int OAD_BLOCK_SIZE = 16;
    public static final int PKT_INTERVAL = 8;
    public static final int HAL_FLASH_WORD_SIZE = 4;
    private short ver;
    private short len;
    private boolean isTypeB;
    private byte[] uid = new byte[4];
    private byte[] source;
    private int estDuration;

    public BLEBinFile() {
        super("");
    }

    public BLEBinFile(String path) {
        super(path);
    }

    public short getVer() {
        return ver;
    }

    public void setVer(short ver) {
        this.ver = ver;
    }

    public short getLen() {
        return len;
    }

    public void setLen(short len) {
        this.len = len;
        setEstDuration(((PKT_INTERVAL * len * 4) / OAD_BLOCK_SIZE) / 1000);
    }

    public int getEstDuration() {
        return estDuration;
    }

    public void setEstDuration(int estDuration) {
        this.estDuration = estDuration;
    }

    public boolean isTypeB() {
        return isTypeB;
    }

    public void setTypeB(boolean typeB) {
        isTypeB = typeB;
    }

    public byte[] getUid() {
        return uid;
    }

    public void setUid(byte[] uid) {
        this.uid = uid;
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }

    public void log() {
        LogUtil.t("=========================");
        LogUtil.t("ver:     " + ver);
        LogUtil.t("len:     " + len);
        LogUtil.t("isType: " + (isTypeB ? "B" : "A"));
        LogUtil.t("uid:     " + ByteUtil.bytes2HexString(uid));
        LogUtil.t("=========================");
    }
}
