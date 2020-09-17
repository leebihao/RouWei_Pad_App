package com.lbh.rouwei.zmodule.config.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.scinan.sdk.util.UnitUtils;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/07
 *     desc   :
 * </pre>
 */
public class CircleProgressBar extends View {
    private int maxProgress = 60;
    private int progress = 0;
    private int progressStrokeWidth = 30;
    private Context context;
    private int textColor, circleColor;

    RectF oval;
    Paint paint, paint_text;

    public static final int TYPE_SHOW_SECONDS = 0;
    public static final int TYPE_SHOW_TIMESTAMP = 1;

    int type = TYPE_SHOW_SECONDS;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        oval = new RectF();
        paint = new Paint();
        paint_text = new Paint();
        this.context = context;
        textColor = Color.rgb(148, 214, 10);
        circleColor = Color.rgb(148, 214, 10);
    }

    public void setTextRGBColor(int red, int green, int blue) {
        textColor = Color.rgb(red, green, blue);
    }

    public void setCircleRGBColor(int red, int green, int blue) {
        circleColor = Color.rgb(red, green, blue);
    }

    public void setTextRGBColor(int textColor) {
        this.textColor = textColor;
    }

    public void setCircleRGBColor(int circleColor) {
        this.circleColor = circleColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }
        paint_text.setAntiAlias(true);
        paint.setAntiAlias(true);
        // paint.setColor(Color.WHITE);
        paint.setColor(Color.rgb(0xeb, 0xeb, 0xeb));
        canvas.drawColor(Color.TRANSPARENT);
        paint.setStrokeWidth(progressStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        oval.left = progressStrokeWidth / 2;
        oval.top = progressStrokeWidth / 2;
        oval.right = width - progressStrokeWidth / 2;
        oval.bottom = height - progressStrokeWidth / 2;

        canvas.drawArc(oval, -90, 360, false, paint);
        // paint.setColor(Color.rgb(0x57, 0x87, 0xb6));
        paint_text.setColor(textColor);

        if (type == TYPE_SHOW_SECONDS) {
            //#228cff
            paint.setColor(circleColor);//
            canvas.drawArc(oval, -90, ((float) progress / maxProgress) * 360,
                    false, paint);
        }

        Typeface typeFaceRobotoLight = Typeface.createFromAsset(
                context.getAssets(), "Roboto-Light.ttf");
        paint_text.setStrokeWidth(0);

        String text = "";
        switch (type) {
            case TYPE_SHOW_SECONDS:
                text = progress + "s";
                break;
            case TYPE_SHOW_TIMESTAMP:
                int hour = progress / 3600;
                int minute = (progress - hour * 3600) / 60;
                int second = progress - hour * 3600 - minute * 60;
                if (hour > 0) {
                    text = String.format("%02d:%02d:%02d", hour, minute, second);
                } else {
                    text = String.format("%02d:%02d", minute, second);
                }
                break;
        }

        paint_text.setTextSize(UnitUtils.sp2px(getContext(), getResources().getDimension(com.scinan.sdk.R.dimen.dp_15)));
        paint_text.setTypeface(typeFaceRobotoLight);
        int textHeight = height / 4;
        int textWidth = (int) paint_text.measureText(text, 0, text.length());
        paint_text.setStyle(Paint.Style.FILL);
        Paint.FontMetricsInt fontMetrics = paint_text.getFontMetricsInt();
        int baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(text, width / 2 - textWidth / 2, baseline, paint_text);

    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.invalidate();
    }


    public void setProgressNotInUiThread(int progress) {

        if (progress <= maxProgress) {
            this.progress = progress;
            this.postInvalidate();
        }
    }

    public void setType(int type) {
        this.type = type;
    }


}
