package com.pwlsj.chat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.pwlsj.chat.R;


public class Xcircleindicator extends View {

    private int radius = 4;
    private final Paint mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int currentScroll = 0;
    private int flowWidth = 0;
    private int pageTotalCount = 1;
    private int currentPage = 0;
    private int circleInterval = radius;


    public Xcircleindicator(Context context) {
        super(context);
        initColors(0xFFFFFFFF, 0xFFFFFFFF);
    }

    public Xcircleindicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Retrieve styles attributs
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XCircleIndicator);

        try {
            // Retrieve the colors to be used for this view and apply them.
            int fillColor = a.getColor(R.styleable.XCircleIndicator_indicator_fillColor,
                    0xFFFFFFFF);
            int strokeColor = a.getColor(
                    R.styleable.XCircleIndicator_indicator_strokeColor, 0xFFFFFFFF);
            // Retrieve the radius
            radius = (int) a.getDimension(R.styleable.XCircleIndicator_indicator_radius,
                    radius);
            circleInterval = (int) a.getDimension(
                    R.styleable.XCircleIndicator_indicator_circleInterval, radius);

            initColors(fillColor, strokeColor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }

    }

    //设置当前应显示圆点的总数
    public void initData(int count, int contentWidth) {
        this.pageTotalCount = count;
        this.flowWidth = contentWidth;
        invalidate();
    }

    //设置当前圆点
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        invalidate();
    }


    public void setPageTotalCount(int pageTotalCount) {
        this.pageTotalCount = pageTotalCount;
        invalidate();
    }

    @Override
    public void invalidate() {
        requestLayout();
        super.invalidate();
    }

    private void initColors(int fillColor, int strokeColor) {
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(strokeColor);
        mPaintStroke.setAntiAlias(true);
        mPaintStroke.setStrokeWidth(2);

        mPaintFill.setAntiAlias(true);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(fillColor);
    }

    //设置 被选中圆点的颜色
    public void setFillColor(int color) {
        mPaintFill.setColor(color);
        invalidate();
    }

    //设置  未选中圆点的颜色
    public void setStrokeColor(int color) {
        mPaintStroke.setColor(color);
        invalidate();
    }

    // 圆点间间距的大小
    public void setCircleInterval(int circleInterval) {
        this.circleInterval = circleInterval;
        invalidate();
    }

    // 圆点的大小
    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw stroked circles
        for (int index = 0; index < pageTotalCount; index++) {
            canvas.drawCircle(getPaddingLeft() + radius + (index * (2 * radius + circleInterval)), getPaddingTop() + radius, radius, mPaintStroke);
        }
        int cx = (2 * radius + circleInterval) * currentPage;
        canvas.drawCircle(getPaddingLeft() + radius + cx, getPaddingTop()
                + radius, radius, mPaintFill);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else { // Calculate the width according the views count
            result = getPaddingLeft() + getPaddingRight() + (pageTotalCount * 2 * radius) + (pageTotalCount - 1) * circleInterval;
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // Measure the height
        else {
            result = 2 * radius + getPaddingTop() + getPaddingBottom();
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public void onScrolled(int h, int v, int oldh, int oldv) {
        currentScroll = h;
        invalidate();
    }

}