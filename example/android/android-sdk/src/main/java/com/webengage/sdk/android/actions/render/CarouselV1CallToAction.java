package com.webengage.sdk.android.actions.render;


public class CarouselV1CallToAction extends CallToAction {

    private String imageURL = null;

    public CarouselV1CallToAction(String id, String text, String action, String imageURL) {
        super(id, text, action, false, false);
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
