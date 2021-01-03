package com.lbh.rouwei.common.event;

/**
 * <pre>
 *     @author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/12/20
 *     desc   :
 * </pre>
 */
public class SerialDataEvent {
    //0--> 错误， 1--->正常
    public int cmd_type = 1;
    public String data;

    public SerialDataEvent(int cmd_type, String data) {
        this.cmd_type = cmd_type;
        this.data = data;
    }
}
