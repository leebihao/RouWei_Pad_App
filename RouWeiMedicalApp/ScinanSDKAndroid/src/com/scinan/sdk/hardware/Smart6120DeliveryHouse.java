package com.scinan.sdk.hardware;

import android.text.TextUtils;

import com.scinan.sdk.error.ResponseTransferException;
import com.scinan.sdk.error.WaitNextTransferException;
import com.scinan.sdk.util.ByteUtil;
import com.scinan.sdk.util.Crc8Util;
import com.scinan.sdk.util.LogUtil;

/**
 * Created by lijunjie on 2017/11/30.
 */

public class Smart6120DeliveryHouse {

    private StringBuilder hexBuilder;
    private int cmdId = -1;
    private int no = -1;

    public Smart6120DeliveryHouse() {
        this.hexBuilder = new StringBuilder();
    }

    private void reset() {
        hexBuilder.delete(0, hexBuilder.length());
        cmdId = -1;
        no = -1;
    }

    public synchronized Smart6120DataCmd deliverCmd(Smart6120Transfer transfer) throws Exception {
        if (transfer == null || transfer.isEmpty()) {
            throw new Exception("empty transfer");
        }

        //response不需要孵化
        if (transfer.isResponse()) {
            throw new ResponseTransferException("response no need deliver");
        }

        //收到一条不同的cmd，清空缓存
        if (cmdId != -1 && cmdId != transfer.getCmdId()) {
            reset();
        }

        //收到一条非顺序的包，清空缓存
        if (no != -1 && no != transfer.getNo() -1) {
            reset();
        }

        //好，接下来，开始装载到缓存
        no = transfer.getNo();
        cmdId = transfer.getCmdId();
        hexBuilder.append(transfer.getData());

        //如果不是最后一个，歇着吧，等下一个
        if (!transfer.isLastOne()) {
            throw new WaitNextTransferException("go on wait receive data");
        }

        String cmdHex = hexBuilder.toString();
        LogUtil.t("receive cmd hex is " + cmdHex);
        //检查OA结束符
        if (!cmdHex.endsWith("0A")) {
            throw new Exception("not end with 0A, full is " + cmdHex);
        }
        //进行crc校验
        String scinanCrc = Crc8Util.calcCrcCommon(cmdHex.substring(0, cmdHex.length() - 4));
        String bleCrc = cmdHex.substring(cmdHex.length() - 4, cmdHex.length() - 2);
        LogUtil.t("our crc is " + scinanCrc + ", ble crc is " + bleCrc);
        if (!TextUtils.equals(scinanCrc, bleCrc)) {
            throw new Exception("crc is not compare, app is " + scinanCrc + ", ble is " + bleCrc);
        }
        LogUtil.t("ok, well, cmd is ok");
        String cmdAscii = ByteUtil.hex2Ascii(cmdHex.substring(0, cmdHex.length() - 4));
        Smart6120DataCmd cmd = Smart6120DataCmd.parse(cmdAscii);
        if (cmd == null) {
            throw new Exception("whate a big pity, parse fail ," + cmdAscii);
        }
        reset();
        return cmd;
    }
}
