package com.scinan.sdk.hardware;

import android.text.TextUtils;

import com.scinan.sdk.util.ByteUtil;
import com.scinan.sdk.util.Crc8Util;
import com.scinan.sdk.util.LogUtil;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by lijunjie on 2017/11/30.
 */

public class Smart6120Transfer implements Serializable {

    enum Category {
        NA,
        From_APP_response,
        From_APP_request,
        From_BLE_response,
        From_BLE_request
    }

    //是什么类型的消息
    private Category category = Category.NA;
    //类型的值
    private int categoryId;
    //命令ID
    private int cmdId;
    //序号
    private byte no;
    //总包数
    private byte total;
    //17个字节的数据
    private String data;
    private byte result;
    private byte crc;

    public Smart6120Transfer() {
    }

    public Smart6120Transfer(Category category, int cmdId, byte no, byte result) {
        setCategory(category);
        setCmdId(cmdId);
        setNo(no);
        setCrc((byte)ByteUtil.hex2int(Crc8Util.calcCrcCommon(new byte[]{getHeader(), no, result})));
        setResult(result);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        switch (this.category) {
            case From_APP_response:
            case From_BLE_request:
                this.categoryId = 0x10;
                break;
            case From_APP_request:
            case From_BLE_response:
                this.categoryId = 0x00;
                break;
        }
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getCmdId() {
        return cmdId;
    }

    public byte getHeader() {
        return (byte) (categoryId + cmdId);
    }

    public void setCmdId(int cmdId) {
        this.cmdId = cmdId;
    }

    public byte getNo() {
        return no;
    }

    public void setNo(byte no) {
        this.no = no;
    }

    public byte getTotal() {
        return total;
    }

    public void setTotal(byte total) {
        this.total = total;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public byte getCrc() {
        return crc;
    }

    public void setCrc(byte crc) {
        this.crc = crc;
    }

    //此处的解析是解析来自ble的数据，有两种可能，1种是来自ble的被动回复，另外是来自ble的主动推送
    public static Smart6120Transfer parse(byte[] datas) throws Exception {
        Smart6120Transfer transfer = new Smart6120Transfer();
        ByteBuffer buffer = ByteBuffer.wrap(datas).order(ByteOrder.LITTLE_ENDIAN);
        byte header = buffer.get();
        boolean isBLERequest = false;
        //头部小于0x10说明，这是一条被动回复信息
        if (header < 0x10) {
            transfer.setCategory(Category.From_BLE_response);
            transfer.setCmdId(header - 0x00);
        } else {
            transfer.setCategory(Category.From_BLE_request);
            transfer.setCmdId(header - 0x10);
            isBLERequest = true;
        }
        transfer.setNo(buffer.get());
        if (!isBLERequest) {
            transfer.setResult(buffer.get());
            transfer.setCrc(buffer.get());
            String scinanCrc = Crc8Util.calcCrcCommon(new byte[] {transfer.getHeader(), transfer.getNo(), transfer.getResult()});
            String bleCrc = ByteUtil.byte2HexString(transfer.getCrc());
            if (!TextUtils.equals(scinanCrc, bleCrc)) {
                throw new Exception(String.format("crc not compare our is %s, return is %s", scinanCrc, bleCrc));
            }
            LogUtil.d("fine, crc check successful, ok well");
        } else {
            transfer.setTotal(buffer.get());
            String dataHex = "";
            while (buffer.remaining() > 0) {
                dataHex += ByteUtil.byte2HexString(buffer.get());
            }
            transfer.setData(dataHex);
        }

        return transfer;
    }

    public boolean isEmpty() {
        return getCategory() == Category.NA;
    }

    public boolean isLastOne() {
        if (getCategory() == Category.From_BLE_response || getCategory() == Category.From_APP_response) {
            return false;
        }

        return no == total - 1;
    }

    public boolean isResponse() {
        if (getCategory() == Category.From_BLE_response || getCategory() == Category.From_APP_response) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "NULL";
        }

        String full = null;
        switch (getCategory()) {
            case From_APP_request:
            case From_BLE_request:
                full = ByteUtil.bytes2HexString(new byte[] {(byte) (categoryId + cmdId), no, total}) + data;
                break;
            case From_APP_response:
            case From_BLE_response:
                full = ByteUtil.bytes2HexString(new byte[]{(byte) (categoryId + cmdId), no, result, crc}) + "0A";
                break;
        }
        return full;
    }
}