package com.xy.highlightview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Xavier Yin on 4/10/17.
 */

public class HighlightImageView extends ImageView {
    private static final int ANIM_COUNTER_MAX = 20;

    private RenderHelper renderHelper;
    private Paint backgroundPaint, erasePaint, redPaint, redHeartPaint;
    private int backgroundColor = Color.TRANSPARENT;
    private Bitmap bitmap;
    private RectF rectF;

    private int animCounter = 0;
    private double animMoveFactor = 1;
    private int step = 1;
    private int roundRectRadius = 20;

    public HighlightImageView(Context context) {
        super(context);
        this.init();
    }

    public HighlightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public HighlightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.bitmap == null) {
            this.bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            this.bitmap.eraseColor(this.backgroundColor);
        }

        canvas.drawBitmap(this.bitmap, 0, 0, this.backgroundPaint);

        if (this.renderHelper.isFocus()) {
            this.animCounter = this.animCounter + this.step;

            if (this.renderHelper.getHighlightShape() == ViewManager.SHAPE_CIRCLE) {
                this.drawCircle(canvas);
            } else {
                this.drawRoundedRect(canvas);
            }

            if (this.animCounter == ANIM_COUNTER_MAX) {
                this.step = -1;
            } else if (this.animCounter == 0) {
                this.step = 1;
            }
        }

        this.postInvalidate();
    }

    public void setBasicResources(int backgroundColor, RenderHelper renderHelper) {
        this.backgroundColor = backgroundColor;
        this.renderHelper = renderHelper;
    }

    private void init() {
        this.setLayerType(LAYER_TYPE_HARDWARE, null);
        this.setWillNotDraw(false);

        this.backgroundPaint = new Paint();
        this.backgroundPaint.setAntiAlias(true);
        this.backgroundPaint.setColor(this.backgroundColor);
        this.backgroundPaint.setAlpha(0xFF);

        this.erasePaint = new Paint();
        this.erasePaint.setAntiAlias(true);
        this.erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.erasePaint.setAlpha(0xFF);

        this.redPaint = new Paint();
        this.redPaint.setAntiAlias(true);
        this.redPaint.setColor(0xFFFF4081);
        this.redPaint.setStrokeWidth(8);
        this.redPaint.setStyle(Paint.Style.STROKE);

        this.redHeartPaint = new Paint();
        this.redHeartPaint.setAntiAlias(true);
        this.redHeartPaint.setColor(0xFFFF4081);

        this.rectF = new RectF();
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(this.renderHelper.getCircleCenterX(), this.renderHelper.getCircleCenterY(), this.renderHelper.getCircleRadius(this.animCounter, this.animMoveFactor), this.erasePaint);
        canvas.drawCircle(this.renderHelper.getCircleCenterX(), this.renderHelper.getCircleCenterY(), this.renderHelper.getCircleRadius(this.animCounter, this.animMoveFactor), this.redPaint);
    }

    private void drawRoundedRect(Canvas canvas) {
        float left = this.renderHelper.getRoundRectLeft(this.animCounter, this.animMoveFactor);
        float top = this.renderHelper.getRoundRectTop(this.animCounter, this.animMoveFactor);
        float right = this.renderHelper.getRoundRectRight(this.animCounter, this.animMoveFactor);
        float bottom = this.renderHelper.getRoundRectBottom(this.animCounter, this.animMoveFactor);

        this.rectF.set(left, top, right, bottom);
        canvas.drawRoundRect(this.rectF, this.roundRectRadius, this.roundRectRadius, this.erasePaint);
    }

    private void drawHeart(Canvas canvas) {
        int centerX = this.renderHelper.getCircleCenterX();
        int centerY = this.renderHelper.getCircleCenterY();
        float radius = this.renderHelper.getCircleRadius(this.animCounter, this.animMoveFactor);

        canvas.rotate(45, centerX, centerY);

        canvas.drawRect(centerX - radius, centerY - radius, centerX + radius, centerY + radius, this.erasePaint);
        canvas.drawCircle(centerX - radius, centerY, radius, this.erasePaint);
        canvas.drawCircle(centerX, centerY - radius, radius, this.erasePaint);
    }
}
