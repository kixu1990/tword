package com.example.tword;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.google.android.flexbox.FlexboxLayout;

/**
 * Created by Administrator on 2019/10/31.
 */

public class HiddenAnimUtils {
    private int mHeight;
    private View hideView,down;
    private RotateAnimation animation;
    private  Context hideContext;

    public static HiddenAnimUtils newInstance(Context context,View hideView,View down,int height){
        return new HiddenAnimUtils(context,hideView,down,height);
    }
    private HiddenAnimUtils(Context context, View hideView, View down, int height){
        this.hideView = hideView;
        this.down = down;
        this.hideContext = context;
        float mDensity = context.getResources().getDisplayMetrics().density;
        mHeight = (int)(mDensity * height + 0.5);
    }
    public void toggle(){
        startAnimation();
        if(View.VISIBLE == hideView.getVisibility()){
            closeAnim(hideView);
        }else {
            openAnim(hideView);
        }
    }

    public void toggle(String s){
        startAnimation();
        if(s.equals("off")){
            closeAnim(hideView);
        }else if(s.equals("on")){
            openAnim(hideView);
        }
    }

    private void openAnim(View hideView) {
        hideView.setVisibility(View.VISIBLE);
        int height;
        if(hideView.getClass() == FlexboxLayout.class){
            float mDensity = hideContext.getResources().getDisplayMetrics().density;
            height = (int)((((FlexboxLayout)hideView).getFlexLines().size() * 38) * mDensity + 0.5) + mHeight;
        }else {
            height = mHeight;
        }
        ValueAnimator animator = createDropAnimation(hideView,0,height);
        animator.start();

    }

    private void closeAnim(final View hideView) {
        int origHeight = hideView.getHeight();
        ValueAnimator animator = createDropAnimation(hideView,origHeight,0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hideView.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    public void startAnimation() {
        if (View.VISIBLE == hideView.getVisibility()) {
            animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        animation.setDuration(30);//设置动画持续时间
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatMode(Animation.REVERSE);//设置反方向执行
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        down.startAnimation(animation);
    }

    private ValueAnimator createDropAnimation(final View v,int start,int end){
        ValueAnimator animator = ValueAnimator.ofInt(start,end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int)valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
