package com.lbh.rouwei.common.utils;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/02
 *     desc   :
 * </pre>
 */
public class CountDownButtonHelper {


    // 倒计时timer
    private CountDownTimer countDownTimer;
    // 计时结束的回调接口
    private OnFinishListener listener;

    private Button button;

    /**
     *
     * @param button
     *            需要显示倒计时的Button
     * @param defaultString
     *            默认显示的字符串
     * @param max
     *            需要进行倒计时的最大值,单位是秒
     * @param interval
     *            倒计时的间隔，单位是秒
     */
    public CountDownButtonHelper(final Button button,
                                 final String defaultString, int max, int interval) {

        this.button = button;
        countDownTimer = new CountDownTimer(max * 1000, interval * 1000 - 10) {

            @Override
            public void onTick(long time) {
                // 第一次调用会有1-10ms的误差，因此需要+15ms，防止第一个数不显示，第二个数显示2s
                button.setText(/*defaultString + "("*/ + ((time + 15) / 1000)
                        + "S");
                Log.d("CountDownButtonHelper", "time = " + (time) + " text = "
                        + ((time + 15) / 1000));
            }

            @Override
            public void onFinish() {
                button.setEnabled(true);
                button.setText(defaultString);
                if (listener != null) {
                    listener.finish();
                }
            }
        };
    }

    /**
     * 开始倒计时
     */
    public void start() {
        button.setEnabled(false);
        countDownTimer.start();
    }

    /**
     * 设置倒计时结束的监听器
     *
     * @param listener
     */
    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }

    /**
     * 计时结束的回调接口
     *
     *
     */
    public interface OnFinishListener {
        public void finish();
    }

}
