package com.apps.matlei.customanalogclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

/**
 * @author Lei <matleivs@gmail.com>
 */
public class ClockView extends View {

    private int height = 0, width = 0;
    private int padding = 0;
    private int fontSize = 0;
    private int numeralSpacing = 0;
    private int handTruncation = 0, hourHandTruncation = 0;
    private int radius = 0;
    private Paint paint;
    private boolean isInit;
    private int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private Rect rect = new Rect();


    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initClock() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 90;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getResources().getDisplayMetrics());
        int min = Math.min(width, height);
        radius = min / 2 - padding;
        handTruncation = min / 20;
        hourHandTruncation = min / 7;
        paint = new Paint();
        isInit = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (!isInit) {
            initClock();
        }

        canvas.drawColor(Color.BLACK); // background
        drawOuterCircle(canvas);
        drawCenterCircle(canvas);
        drawNumerals(canvas);
        drawHands(canvas);

        postInvalidateDelayed(500);
//        invalidate(); -> not needed here cos we call postInvalidateDelayed()
    }


    private void drawOuterCircle(Canvas canvas) {
        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, radius + padding - 10, paint);
    }

    private void drawCenterCircle(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, 12, paint);
    }

    private void drawNumerals(Canvas canvas) {
        paint.setTextSize(fontSize);

        for (int number : numbers) {
            String nrString = String.valueOf(number);
            paint.getTextBounds(nrString, 0, nrString.length(), rect);
            // some trigonometry -> calc the x- & y-coordinates for the numerals, cos for the x and sin for the y position
            double angle = Math.PI / 6 * (number - 3); //  (number - 3) makes the 12 be in the top center spot
            int xNum = (int) (width / 2 + Math.cos(angle) * radius - rect.width() / 2);
            int yNum = (int) (height / 2 + Math.sin(angle) * radius - rect.height() / 2);
            canvas.drawText(nrString, xNum, yNum, paint);
        }
    }


    private void drawHands(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        float hour = calendar.get(Calendar.HOUR_OF_DAY);
        hour = hour > 12 ? hour - 12 : hour;

        // draw hourHand, minutesHand, and secondsHand
        drawHand(canvas, (hour + calendar.get(Calendar.MINUTE) / 60) * 5d, true);
        drawHand(canvas, calendar.get(Calendar.MINUTE), false);
        drawHand(canvas, calendar.get(Calendar.SECOND), false);
    }

    private void drawHand(Canvas canvas, double loc, boolean isHour) {
        double angle = Math.PI * loc / 30 - Math.PI / 2;
        int handRadius = isHour ? radius - handTruncation - hourHandTruncation : radius - handTruncation;
        canvas.drawLine(width / 2, height / 2,
                (float) (width / 2 + Math.cos(angle) * handRadius),
                (float) (height / 2 + Math.sin(angle) * handRadius), paint);

    }

}