package com.dudu.common.widget;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dudu.common.R;
import com.dudu.common.util.DensityUtils;

import androidx.annotation.NonNull;


/**
 * 用于显示 Loading 的 {@link View}，支持颜色和大小的设置。
 *
 * @author xuexiang
 * @since 2018/12/1 下午11:24
 */
public class MiniLoadingView extends View {

    private int mSize;
    private int mPaintColor;
    private int mAnimateValue = 0;
    private ValueAnimator mAnimator;
    private Paint mPaint;
    private static final int LINE_COUNT = 12;
    private static final int DEGREE_PER_LINE = 360 / LINE_COUNT;

    public MiniLoadingView(Context context) {
        this(context, null);
    }

    public MiniLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.MiniLoadingStyle);// 当前MiniLoadingStyle还未定义默认属性
    }

    public MiniLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        initPaint();
    }

    public MiniLoadingView(Context context, int size, int color) {
        super(context);
        mSize = size;
        mPaintColor = color;
        initPaint();
    }

    // AttributeSet： XML 属性（从 XML inflate 的时候使用）
    // int defStyleAttr： 应用到 View 的默认风格（定义在主题中）
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.MiniLoadingView, defStyleAttr, 0);
        mSize = array.getDimensionPixelSize(R.styleable.MiniLoadingView_mlv_loading_view_size, DensityUtils.dp2px(context, 32));
        mPaintColor = array.getColor(R.styleable.MiniLoadingView_mlv_loading_view_color, Color.WHITE);
        array.recycle();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setColor(int color) {
        mPaintColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    public void setSize(int size) {
        mSize = size;
        requestLayout();
    }

    private ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mAnimateValue = (int) animation.getAnimatedValue();
            invalidate();
        }
    };

    public void start() {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofInt(0, LINE_COUNT - 1);
            mAnimator.addUpdateListener(mUpdateListener);
            mAnimator.setDuration(600);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.start();
        } else if (!mAnimator.isStarted()) {
            mAnimator.start();
        }
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.removeUpdateListener(mUpdateListener);
            mAnimator.removeAllUpdateListeners();
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private void drawLoading(Canvas canvas, int rotateDegrees) {
        int width = mSize / 12, height = mSize / 6;
        mPaint.setStrokeWidth(width);

        canvas.rotate(rotateDegrees, mSize / 2F, mSize / 2F);
        canvas.translate(mSize / 2F, mSize / 2F);

        for (int i = 0; i < LINE_COUNT; i++) {
            canvas.rotate(DEGREE_PER_LINE);
            mPaint.setAlpha((int) (255f * (i + 1) / LINE_COUNT));
            canvas.translate(0, -mSize / 2F + width / 2F);
            canvas.drawLine(0, 0, 0, height, mPaint);
            canvas.translate(0, mSize / 2F - width / 2F);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize, mSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        drawLoading(canvas, mAnimateValue * DEGREE_PER_LINE);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            start();
        } else {
            stop();
        }
    }

}
