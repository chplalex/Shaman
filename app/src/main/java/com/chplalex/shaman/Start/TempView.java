package com.chplalex.shaman.Start;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.chplalex.shaman.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TempView extends View {

    private boolean certain;
    private float temp;
    private int tempMax;
    private float width;
    private float height;
    private float radius;
    private Paint paint;
    private int colorPrimary;
    private int colorText;
    private int colorDark;

    public TempView(Context context) {
        super(context);
        init();
    }

    public TempView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TempView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TempView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        tempMax = (((int) Math.abs(temp) / 10) + 1) * 10;
        if (paint == null) paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.certain = true;
        this.temp = temp;
        init();
        invalidate();
    }

    public void setUncertain() {
        certain = false;
        temp = 0;
        init();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = (float) getWidth();
        height = (float) getHeight();

        colorPrimary = getResources().getColor(R.color.primaryColor);
        colorText = getResources().getColor(R.color.primaryTextColor);
        colorDark = getResources().getColor(R.color.primaryDarkColor);

        drawMainRect(canvas);
        drawMainCircleWithTemp(canvas);
        drawRuler(canvas);
        drawTempOnRuler(canvas);
        drawMarksAndTextOnRuler(canvas);
    }

    // риски и подписи на линейке
    private void drawMarksAndTextOnRuler(Canvas canvas) {
        final float startX = radius;
        final float stopX = width - radius;
        final int segmentsCount = 4 * tempMax / 10;
        final float dX = (stopX - startX) / segmentsCount;
        final float yMark = height - radius;
        final float yText = height - radius * 0.25f;

        NumberFormat nf = new DecimalFormat("+#;-#");

        paint.setTextSize(height / 12);

        for (int i = 0; i <= segmentsCount; i++) {
            final float x = startX + dX * i;
            paint.setColor(colorDark);
            if (i % 2 == 0) {
                canvas.drawCircle(x, yMark, 15.0f, paint);
            } else {
                canvas.drawCircle(x, yMark, 10.0f, paint);
            }
            final int tempInMark = i * 5 - tempMax;
            paint.setColor(colorText);
            canvas.drawText(nf.format(tempInMark), x, yText, paint);
        }
    }

    // линейка
    private void drawRuler(Canvas canvas) {
        final float startX = radius;
        final float startY = height - radius;
        final float stopX = width - radius;
        final float stopY = startY;
        paint.setStrokeWidth(height / 200);
        paint.setColor(colorDark);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private void drawMainCircleWithTemp(Canvas canvas) {
        // круг
        final float x = width / 2;
        final float y = height / 2;
        final float r = Math.min(x, y);
        canvas.drawCircle(x, y, r, paint);

        // температура в круге
        final float textSize = r / 2;
        paint.setTextSize(textSize);
        paint.setColor(colorText);
        String text;
        if (certain) {
            text = new DecimalFormat("+#°C;-#°C").format(temp);
        } else {
            text = "--°C";
        }
        canvas.drawText(text, x, y, paint);
    }

    // основной прямоугольник
    private void drawMainRect(Canvas canvas) {
        final float w = width;
        final float h = height / 3;
        radius = (w > h) ? (h / 2) : (w / 2);

        paint.setColor(colorPrimary);

        final RectF rect = new RectF(0, 2 * h, width, height);

        canvas.drawRoundRect(rect, radius, radius, paint);
    }

    // температура на линейке термометра
    private void drawTempOnRuler(Canvas canvas) {
        final float w = width;
        final float h = height / 3;
        final float radius = (w > h) ? (h / 2) : (w / 2);
        final float lengthRuler = width / 2 - radius;
        final float lengthTemp = temp * lengthRuler / tempMax;

        float startX;
        float stopX;

        if (temp > 0) {
            startX = width / 2;
            stopX = startX + lengthTemp;
        } else {
            stopX = width / 2;
            startX = stopX + lengthTemp;
        }

        final float startY = height - radius;
        final float stopY = startY;

        paint.setColor(colorText);
        paint.setStrokeWidth(height / 50);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}
