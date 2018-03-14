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
 * Custom View for an analog Clockface.
 *
 * @author Lei <matleivs@gmail.com>
 */
public class ClockView extends View {

    // TODO WIP the attributes should be moved to resources and be styleable attributes
    private int height = 0, width = 0;
    private int padding = 0;
    private int fontSize = 0;
    private int numeralSpacing = 0;
    private int handTruncation = 0, hourHandTruncation = 0;
    private int radius = 0;
    private Paint paint;
    private boolean isInit;
    private int[] hours = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
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

    /**
     * Initialize the necessary values.
     */
    private void initClock() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 90; // spacing from the circle border to the inside = where the hours are
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
        int min = Math.min(width, height);
        radius = min / 2 - padding;
        // for maintaining different heights among the clock-hands
        handTruncation = min / 20;
        hourHandTruncation = min / 17;
        paint = new Paint();
        isInit = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        /*  WIP TODO
            Instead of checking "if (!isInit)" every time in onDraw (a method that's called very frequently if you do animations), instead override View.onSizeChanged ()
            and grab height and width from there.
            Not only is this more efficient, but more importantly, it allows the view to handle size changes due run-time layout changes
            (e.g. if another view becomes invisible, it can force a re-layout).*/
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


    /**
     * Draw the outer circle which is the outer border for the clock.
     *
     * @param canvas
     */
    private void drawOuterCircle(Canvas canvas) {
        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, radius + padding - 10, paint);
    }

    /**
     * Draw the center of the clock, aka the center point which the hands will be rotated from.
     *
     * @param canvas
     */
    private void drawCenterCircle(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, 12, paint);
    }

    /**
     * Draw the numbers of the clock from 1 to 12 inside the clock circle.
     *
     * @param canvas
     */
    private void drawNumerals(Canvas canvas) {
        paint.setTextSize(fontSize);

        for (int hour : hours) {
            String nrString = String.valueOf(hour);
            paint.getTextBounds(nrString, 0, nrString.length(), rect); // for circle-wise bounding


            // some trigonometry -> calc the x- & y-coordinates for the numerals (circle-wise (x, y) position), using cos for the x and sin for the y position
            double angle = Math.PI / 6 * (hour - 3); //  (number - 3) makes the 12 be in the top center spot
            int xNum = (int) (width / 2 + Math.cos(angle) * radius - rect.width() / 2);
            int yNum = (int) (height / 2 + Math.sin(angle) * radius + rect.height() / 2);
            canvas.drawText(nrString, xNum, yNum, paint);
        }
    }

    /**
     * Draw the hands for hour, minutes and seconds.
     *
     * @param canvas
     */
    private void drawHands(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        float hour = calendar.get(Calendar.HOUR_OF_DAY);
        hour = hour > 12 ? hour - 12 : hour;

        float minuteOfDay = calendar.get(Calendar.MINUTE) + 60 * calendar.get(Calendar.HOUR_OF_DAY); // We get total minutes of the day.
        minuteOfDay = calendar.get(Calendar.HOUR_OF_DAY) > 12 ? minuteOfDay - 12 * 60 : minuteOfDay; // Analog clock has only 12 hours and 60*12 = 720 minutes
        double angleOfDay = minuteOfDay * 360 / (12 * 60) - 90; // Angle in deg
        double radianOfDay = angleOfDay * ((2 * Math.PI / 360)); // Angle in rad
        drawHourHand(canvas, radianOfDay);

//        drawHand(canvas, (hour + calendar.get(Calendar.MINUTE) / 60) * 5d, true);
        drawHand(canvas, calendar.get(Calendar.MINUTE), false);
        drawHand(canvas, calendar.get(Calendar.SECOND), false);
    }

    /**
     * Helper method for drawing the hourhand.
     *
     * @param canvas
     * @param loc
     */
    private void drawHourHand(Canvas canvas, double loc) {
        int handRadius = radius - handTruncation - hourHandTruncation;
        canvas.drawLine(width / 2, height / 2,
                (float) (width / 2 + Math.cos(loc) * handRadius),
                (float) (height / 2 + Math.sin(loc) * handRadius),
                paint);
    }

    /**
     * Draw the hands which are not for hours (minutes, seconds)
     *
     * @param canvas
     * @param loc
     * @param isHour
     */
    private void drawHand(Canvas canvas, double loc, boolean isHour) {
        double angle = Math.PI * loc / 30 - Math.PI / 2;
        int handRadius = isHour ? radius - handTruncation - hourHandTruncation : radius - handTruncation;
        canvas.drawLine(width / 2, height / 2,
                (float) (width / 2 + Math.cos(angle) * handRadius),
                (float) (height / 2 + Math.sin(angle) * handRadius), paint);

    }
}
