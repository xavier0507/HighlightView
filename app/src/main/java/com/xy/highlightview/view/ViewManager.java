package com.xy.highlightview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ViewAnimator;

import com.xy.highlightview.R;

/**
 * Created by Xavier Yin on 4/10/17.
 */

public class ViewManager implements View.OnClickListener {
    public static final String HIGHLIGHT_CONTAINER_TAG = "highlight_container_tag";
    public static final int SHAPE_CIRCLE = 0;
    public static final int SHAPE_RECT = 1;

    private Activity activity;
    private View focusedOnView;
    private int highlightShape;
    private int backgroundColor;
    private int centerX;
    private int centerY;

    private RenderHelper renderHelper;
    private ViewGroup mainViewGroup;
    private FrameLayout highlightContainer;

    private ViewManager(Activity activity,
                        View focusedOnView,
                        int highlightShape,
                        int backgroundColor) {
        this.init(activity, focusedOnView, highlightShape, backgroundColor);
    }

    @Override
    public void onClick(View v) {
        this.exitAnimation();
    }

    public void render() {
        this.findView();
        this.launch();
    }

    private void init(Activity activity,
                      View focusedOnView,
                      int highlightShape,
                      int backgroundColor) {
        this.initMemberVariables(activity, focusedOnView, highlightShape, backgroundColor);
        this.initScreenResources();
    }

    private void initMemberVariables(Activity activity, View focusedOnView, int highlightShape, int backgroundColor) {
        this.activity = activity;
        this.focusedOnView = focusedOnView;
        this.highlightShape = (highlightShape == 0 ? SHAPE_CIRCLE : SHAPE_RECT);
        this.backgroundColor = backgroundColor != 0 ? this.activity.getResources().getColor(backgroundColor) : this.activity.getResources().getColor(R.color.default_color_CC000000);
    }

    private void initScreenResources() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        this.centerX = deviceWidth / 2;
        this.centerY = deviceHeight / 2;
    }

    private void findView() {
        if (this.activity == null) {
            return;
        }

        this.renderHelper = new RenderHelper(this.activity, this.highlightShape, this.focusedOnView);
        final ViewGroup originalViewGroup = (ViewGroup) this.activity.findViewById(android.R.id.content);
        this.mainViewGroup = (ViewGroup) originalViewGroup.getParent().getParent();
        this.highlightContainer = (FrameLayout) this.mainViewGroup.findViewWithTag(HIGHLIGHT_CONTAINER_TAG);
    }

    private void launch() {
        if (this.highlightContainer == null) {
            this.addContainerView();
            this.addHighlightView();
            this.enterAnimation();
        }
    }

    private void addContainerView() {
        this.highlightContainer = new FrameLayout(this.activity);
        this.highlightContainer.setTag(HIGHLIGHT_CONTAINER_TAG);
        this.highlightContainer.setClickable(true);
        this.highlightContainer.setOnClickListener(this);
        this.highlightContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.mainViewGroup.addView(this.highlightContainer);
    }

    private void addHighlightView() {
        HighlightImageView imageView = new HighlightImageView(this.activity);
        if (this.renderHelper.isFocus()) {
            this.centerX = this.renderHelper.getCircleCenterX();
            this.centerY = this.renderHelper.getCircleCenterY();
        }
        imageView.setBasicResources(this.backgroundColor, this.renderHelper);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.highlightContainer.addView(imageView);
    }

    private void enterAnimation() {
        final int revealRadius = (int) Math.hypot(highlightContainer.getWidth(), highlightContainer.getHeight());
        int startRadius = 0;

        if (focusedOnView != null) {
            startRadius = focusedOnView.getWidth() / 2;
        }

        Animator enterAnimator = ViewAnimationUtils.createCircularReveal(highlightContainer, centerX, centerY, startRadius, revealRadius);
        enterAnimator.setDuration(1000);
        enterAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        enterAnimator.start();

        this.highlightContainer.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        highlightContainer.getViewTreeObserver().removeOnPreDrawListener(this);

                        final int revealRadius = (int) Math.hypot(highlightContainer.getWidth(), highlightContainer.getHeight());
                        int startRadius = 0;

                        if (focusedOnView != null) {
                            startRadius = focusedOnView.getWidth() / 2;
                        }

                        Animator enterAnimator = ViewAnimationUtils.createCircularReveal(highlightContainer, centerX, centerY, startRadius, revealRadius);
                        enterAnimator.setDuration(1000);
                        enterAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        enterAnimator.start();

                        return false;
                    }
                });
    }

    private void exitAnimation() {
        final int revealRadius = (int) Math.hypot(highlightContainer.getWidth(), highlightContainer.getHeight());

        Animator exitAnimator = ViewAnimationUtils.createCircularReveal(highlightContainer, centerX, centerY, revealRadius, 0f);
        exitAnimator.setDuration(300);
        exitAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        exitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainViewGroup.removeView(highlightContainer);
            }
        });
        exitAnimator.start();
    }

    public static class HighlightEffectBuilder {
        private Activity activity;
        private View focusedOnView;
        private int highlightShape;
        private int backgroundColor;

        public HighlightEffectBuilder(Activity activity) {
            this.activity = activity;
        }

        public HighlightEffectBuilder focusedOn(View focusedOnView) {
            this.focusedOnView = focusedOnView;
            return this;
        }

        public HighlightEffectBuilder highlightShape(int highlightShape) {
            this.highlightShape = highlightShape;
            return this;
        }

        public HighlightEffectBuilder backgroundColor(int backgroundColorResourceId) {
            this.backgroundColor = backgroundColorResourceId;
            return this;
        }

        public ViewManager build() {
            return new ViewManager(
                    this.activity,
                    this.focusedOnView,
                    this.highlightShape,
                    this.backgroundColor);
        }
    }
}
