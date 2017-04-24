package com.xy.highlightview.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Xavier Yin on 4/11/17.
 */

public class RenderHelper {
    private int focusedOnViewWidth;
    private int focusedOnViewHeight;
    private int highlightShape;
    private int circleCenterX;
    private int circleCenterY;
    private int focusedOnViewRadius;
    private boolean isFocus;

    public RenderHelper(Activity activity, int highlightShape, View focusedOnView) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (focusedOnView != null) {
            int adjustHeight = this.getStatusBarHeight(activity);
            int[] viewPoints = new int[2];
            focusedOnView.getLocationInWindow(viewPoints);

            this.focusedOnViewWidth = focusedOnView.getWidth();
            this.focusedOnViewHeight = focusedOnView.getHeight();
            this.highlightShape = highlightShape;

            this.circleCenterX = viewPoints[0] + this.focusedOnViewWidth / 2;
            this.circleCenterY = viewPoints[1] + this.focusedOnViewHeight / 2 - adjustHeight;

            this.focusedOnViewRadius = (int) (Math.hypot(this.focusedOnViewWidth, this.focusedOnViewHeight) / 2);
            this.isFocus = true;
        } else {
            this.isFocus = false;
        }
    }

    public int getHighlightShape() {
        return this.highlightShape;
    }

    public int getCircleCenterX() {
        return this.circleCenterX;
    }

    public int getCircleCenterY() {
        return this.circleCenterY;
    }

    public boolean isFocus() {
        return this.isFocus;
    }

    public float getCircleRadius(int animCounter, double animFactor) {
        return (float) (this.focusedOnViewRadius + animCounter * animFactor);
    }

    public float getRoundRectLeft(int animCounter, double animMoveFactor) {
        return (float) (this.circleCenterX - this.focusedOnViewWidth / 2 - animCounter * animMoveFactor);
    }

    public float getRoundRectTop(int animCounter, double animMoveFactor) {
        return (float) (this.circleCenterY - this.focusedOnViewHeight / 2 - animCounter * animMoveFactor);
    }

    public float getRoundRectRight(int animCounter, double animMoveFactor) {
        return (float) (this.circleCenterX + this.focusedOnViewWidth / 2 + animCounter * animMoveFactor);
    }

    public float getRoundRectBottom(int animCounter, double animMoveFactor) {
        return (float) (this.circleCenterY + this.focusedOnViewHeight / 2 + animCounter * animMoveFactor);
    }

    public int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }
}
