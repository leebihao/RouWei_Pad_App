package com.lbh.rouwei.common.hardware;

import android.text.TextUtils;
import android.util.Log;

import com.kongqw.serialportlibrary.SerialPortManager;
import com.lbh.rouwei.bese.AppApplication;
import com.lbh.rouwei.common.utils.ByteUtil;


/**
 * <pre>
 *     @author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/12/20
 *     desc   :
 * </pre>
 */
public class CmdControlManager {
    private static CmdControlManager instance;
    private AppApplication app;
    private SerialPortManager serialPortManager;

    private CmdControlManager() {
        app = AppApplication.getApp();
        serialPortManager = app.getSerialPortManager();

    }

    public static synchronized CmdControlManager getInstance() {
        if (instance == null) {
            instance = new CmdControlManager();
        }
        return instance;
    }

    /**
     * 发送指令
     *
     * @param cmdCode
     * @param data
     */
    public void sendUartCmd(int cmdCode, String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        String sendStr = "/S" + AppOptionCode.getOptionCodeNoS(cmdCode) + "/1/" + data + "\n";
        sendCommand(sendStr);
    }

    /**
     * 发送串口指令
     *
     * @param cmdStr
     */
    private void sendCommand(String cmdStr) {
        Log.d("TAG_cmd", "sendCommand: " + cmdStr);
        Log.d("TAG_cmd", "sendCommand hex: " + ByteUtil.toStringHex(cmdStr));
        app.sendSerialCmdData(cmdStr);

    }

}
