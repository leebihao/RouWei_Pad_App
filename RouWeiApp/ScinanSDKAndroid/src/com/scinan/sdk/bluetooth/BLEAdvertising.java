package com.scinan.sdk.bluetooth;

import com.scinan.sdk.util.ByteUtil;
import com.scinan.sdk.util.LogUtil;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by lijunjie on 17/4/13.
 */

public class BLEAdvertising implements Serializable {

    ArrayList<UUID> uuids;
    byte flags;
    String localName;
    StringBuilder manufacturer;

    public BLEAdvertising() {
        uuids = new ArrayList<UUID>();
        manufacturer = new StringBuilder();
    }

    public ArrayList<UUID> getUuids() {
        return uuids;
    }

    public void setUuids(ArrayList<UUID> uuids) {
        this.uuids = uuids;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public StringBuilder getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(StringBuilder manufacturer) {
        this.manufacturer = manufacturer;
    }

    public static BLEAdvertising parse(byte[] adv_data) throws Exception {
        BLEAdvertising parsedAd = new BLEAdvertising();
        ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            //广播包31字节，响应包31字节，只解析广播包
            byte length = buffer.get();
            if (length == 0)
                break;

            byte type = buffer.get();
            length -= 1;
            switch (type) {
                //标志位
                case 0x01:
                    parsedAd.flags = buffer.get();
                    length--;
                    break;
                //16位UUID
                case 0x02:
                case 0x03:
                case 0x14:
                    while (length >= 2) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;
                //32位UUID
                case 0x04:
                case 0x05:
                    while (length >= 4) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getInt())));
                        length -= 4;
                    }
                    break;
                //128位UUID
                case 0x06:
                case 0x07:
                case 0x15:
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        parsedAd.uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;
                //短的设备名称
                case 0x08:
                    //完整的设备名称
                case 0x09:
                    byte sb[] = new byte[length];
                    buffer.get(sb, 0, length);
                    length = 0;
                    parsedAd.localName = new String(sb).trim();
                    break;
                case (byte) 0xFF: //自定义数据
                    while (length > 0) {
                        parsedAd.manufacturer.append(ByteUtil.byte2HexString(buffer.get()));
                        length--;
                    }
                    break;
                default: // skip
                    //LogUtil.d("rubbish type:" + ByteUtil.byte2HexString(type));
                    break;
            }
            if (length > 0) {
                buffer.position(buffer.position() + length);
            }
        }
        return parsedAd;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UUID:").append(uuids.toString()).append("\n");
        sb.append("manufacturer:").append(manufacturer.toString()).append("\n");
        sb.append("localName:").append(localName);
        return sb.toString();
    }

    public void log() {
        LogUtil.d("===================");
        LogUtil.d(toString());
        LogUtil.d("===================");
    }
}
