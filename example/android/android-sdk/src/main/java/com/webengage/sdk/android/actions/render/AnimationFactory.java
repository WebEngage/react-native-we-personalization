package com.webengage.sdk.android.actions.render;


import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationFactory {

    public static final String TOP_IN = "TOP_IN";
    public static final String TOP_OUT = "TOP_OUT";
    public static final String BOTTOM_IN = "BOTTOM_IN";
    public static final String BOTTOM_OUT = "BOTTOM_OUT";
    public static final String LEFT_IN = "LEFT_IN";
    public static final String LEFT_OUT = "LEFT_OUT";
    public static final String RIGHT_IN = "RIGHT_IN";
    public static final String RIGHT_OUT = "RIGHT_OUT";
    public static final String FADE_IN = "FADE_IN";
    public static final String FADE_OUT = "FADE_OUT";


    public static Animation newAnimation(String animationType, Animation.AnimationListener animationListener, long duration) {
        Animation animation = null;
        if (TOP_IN.equals(animationType)) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, -1.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (TOP_OUT.equals(animationType)) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, -1.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (BOTTOM_IN.equals(animationType)) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 1.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (BOTTOM_OUT.equals(animationType)) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 1.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (LEFT_IN.equals(animationType)) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (LEFT_OUT.equals(animationType)) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, -1.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (RIGHT_IN.equals(animationType)) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (RIGHT_OUT.equals(animationType)) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 1.0F, Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (FADE_IN.equals(animationType)) {
            animation = new AlphaAnimation(0.0F, 1.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        } else if (FADE_OUT.equals(animationType)) {
            animation = new AlphaAnimation(1.0F, 0.0F);
            animation.setDuration(duration);
            animation.setAnimationListener(animationListener);
        }
        return animation;
    }

}
