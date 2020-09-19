/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by lijunjie on 15/12/9.
 */
public class TimeUtil {

    /** 时间日期格式化到年月日时分秒. */
    public static final String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";

    /** 时间日期格式化到年月日. */
    public static final String dateFormatYMD = "yyyy-MM-dd";

    /** 时间日期格式化到年月. */
    public static final String dateFormatYM = "yyyy-MM";

    /** 时间日期格式化到年月日时分. */
    public static final String dateFormatYMDHM = "yyyy-MM-dd HH:mm";

    /** 时间日期格式化到月日. */
    public static final String dateFormatMD = "MM/dd";

    /** 时分秒. */
    public static final String dateFormatHMS = "HH:mm:ss";

    /** 时分. */
    public static final String dateFormatHM = "HH:mm";

    /** 上午. */
    public static final String AM = "AM";

    /** 下午. */
    public static final String PM = "PM";

    /**
     * 描述：获取表示当前日期时间的字符串.
     *
     * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String String类型的当前日期时间
     */
    public static String getCurrentDate(String format) {
        String curDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            Calendar c = new GregorianCalendar();
            curDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;

    }

    /**
     * 描述：获取src中的图片资源.
     *
     * @param src 图片的src路径，如（“image/arrow.png”）
     * @return Bitmap 图片
     */
    public static Bitmap getBitmapFromSrc(String src){
        Bitmap bit = null;
        try {
            bit = BitmapFactory.decodeStream(JavaUtil.class.getResourceAsStream(src));
        } catch (Exception e) {
            LogUtil.d("获取图片异常：" + e.getMessage());
        }
        return bit;
    }

    /**
     * 获取当前日期是星期几
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }
}
