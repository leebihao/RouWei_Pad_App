package com.lbh.rouwei.common.serial;

import android.content.Context;

import com.lbh.rouwei.common.utils.ByteUtil;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lijunjie on 17/3/27.
 */

public class SerialPortManager {

    private static final String PORT_PATH = "/dev/ttyS0";
    private static final int PORT_BAUDRATE = 9600;
    private static final String END_FLAG = "0D0A";

    private static SerialPortManager sInstance = null;
    protected CopyOnWriteArrayList<OnUartCallback> mFetchDataListeners;
    private Context mContext;
    private SerialPort mSerialPort;
    private ReadThread mReadThread;
    private volatile long mLastReceiveTime;
    private OnUartCallback mCallback = new OnUartCallback() {
        @Override
        public void onUartDataReceived(byte[] data) {
            for (OnUartCallback callback : mFetchDataListeners) {
                callback.onUartDataReceived(data);
            }
        }

        @Override
        public void onUartError() {
            for (OnUartCallback callback : mFetchDataListeners) {
                callback.onUartError();
            }
        }
    };

    private SerialPortManager(Context context) {
        mContext = context.getApplicationContext();
        mFetchDataListeners = new CopyOnWriteArrayList<>();
    }

    public static synchronized SerialPortManager getInstance(Context context) {
        if (sInstance == null)
            sInstance = new SerialPortManager(context.getApplicationContext());
        return sInstance;
    }

    public boolean isSerialPortReady() {
        try {
            return mSerialPort != null && (System.currentTimeMillis() - mLastReceiveTime < 15000);
        } catch (Exception e) {
        }
        return false;
    }

    public void close() {
        if (mSerialPort == null) {
            return;
        }

        mSerialPort.close();
    }

    public boolean open() throws Exception {

        if (mSerialPort != null) {
            return true;
        }

        int fail = 0;
        try {
            mSerialPort = new SerialPort(new File(PORT_PATH), PORT_BAUDRATE, 2);
            mReadThread = new ReadThread();
            mReadThread.start();
            return true;
        } catch (SecurityException e) {
            e.printStackTrace();
            fail = 1;
        } catch (IOException e) {
            fail = 2;
            e.printStackTrace();
        } catch (Exception e) {
            fail = 3;
            e.printStackTrace();
        }

        throw new Exception(String.valueOf(fail));
    }

    public void registerUartCallback(OnUartCallback listener) {
        if (!mFetchDataListeners.contains(listener)) {
            mFetchDataListeners.add(listener);
        }
    }

    public void unRegisterUartCallback(OnUartCallback listener) {
        mFetchDataListeners.remove(listener);
    }

    public void write(final byte[] data) throws Exception {

        if (mSerialPort == null || mSerialPort.getOutputStream() == null) {
            throw new Exception("4");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSerialPort.getOutputStream().write(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface OnUartCallback {
        void onUartDataReceived(byte[] data);

        void onUartError();
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            do {

                try {
                    byte[] header = new byte[11];
                    int headerFlag = 0;
                    while (headerFlag < header.length) {
                        byte[] tmp = new byte[header.length - headerFlag];
                        int tmpLenght = mSerialPort.getInputStream().read(tmp);
                        System.arraycopy(tmp, 0, header, headerFlag, tmpLenght);



                        headerFlag = headerFlag + tmpLenght;
                    }

                    byte[] protoLengthByte = new byte[4];
                    System.arraycopy(header, 7, protoLengthByte, 0, 4);
                    String lengthHex = ByteUtil.hex2Ascii(ByteUtil.bytes2HexString(protoLengthByte));
                    KLog.d("=========lengthHex:" + lengthHex);
                    //最后一位是0D0A不在长度的计算范围内，这里要加上去
                    int protoLengthInt = ByteUtil.hex2int(lengthHex) + END_FLAG.length() / 2;
                    KLog.d("=========protoLengthInt:" + protoLengthInt);
                    //错误的数据将会等待0A结尾丢弃
                    if (protoLengthInt >= 1024 || protoLengthInt <= 0) {
                        byte[] tmp = new byte[2];
                        while (mSerialPort.getInputStream().read(tmp) > 0) {
                            if (END_FLAG.equals(ByteUtil.bytes2HexString(tmp))) {
                                break;
                            }
                        }
                        KLog.e("Uart.ReadThread fuck data1 but continue");
                        continue;
                    }
                    byte[] data = new byte[protoLengthInt];
                    int flag = 0;
                    while (flag < protoLengthInt) {
                        byte[] tmp = new byte[protoLengthInt - flag];
                        int tmpLenght = mSerialPort.getInputStream().read(tmp);
                        System.arraycopy(tmp, 0, data, flag, tmpLenght);
                        flag = flag + tmpLenght;
                    }

                    mLastReceiveTime = System.currentTimeMillis();
                    byte[] full = new byte[header.length + protoLengthInt];
                    System.arraycopy(header, 0, full, 0, header.length);
                    System.arraycopy(data, 0, full, header.length, data.length);
                    System.out.println("Uart.ReadThread<-------------------------" + ByteUtil.hex2Ascii(ByteUtil.bytes2HexString(full)));
                    //错误的数据将会等待0A结尾丢弃
                    if (!ByteUtil.bytes2HexString(full).endsWith(END_FLAG)) {
                        byte[] tmp = new byte[2];
                        while (mSerialPort.getInputStream().read(tmp) > 1) {
                            if (END_FLAG.equals(ByteUtil.bytes2HexString(tmp))) {
                                break;
                            }
                        }
                        KLog.e("Uart.ReadThread fuck data2 but continue");
                        continue;
                    }
                    //todo
                    mCallback.onUartDataReceived(full);
                    //                    mCallback.onUartDataReceived(ByteUtil.hex2Bytes(ByteUtil.ascill2hex("/S00/1/00441203140A111403E800044C500A643211011111646464000007D000000064012C058F") + "0D0A"));
                } catch (Exception e) {
                    e.printStackTrace();
                    KLog.e(e);
                    mCallback.onUartError();
                }
            } while (!isInterrupted());
        }
    }
}
