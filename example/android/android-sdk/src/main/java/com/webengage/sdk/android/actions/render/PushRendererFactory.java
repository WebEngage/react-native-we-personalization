package com.webengage.sdk.android.actions.render;

import com.webengage.sdk.android.callbacks.CustomPushRender;
import com.webengage.sdk.android.callbacks.CustomPushRerender;
import com.webengage.sdk.android.utils.WebEngageConstant;

class PushRendererFactory {
    static CustomPushRender getRender(WebEngageConstant.STYLE style) {
        switch (style) {
            case BIG_TEXT:
                return new BigTextRenderer();
            case BIG_PICTURE:
                return new BigPictureRenderer();
            case CAROUSEL_V1:
                return new CarouselRenderer();
            case RATING_V1:
                return new RatingRenderer();
        }
        return null;
    }

    static CustomPushRerender getRerender(WebEngageConstant.STYLE style) {
        switch (style) {
            case CAROUSEL_V1:
                return new CarouselRenderer();
            case RATING_V1:
                return new RatingRenderer();
        }
        return null;
    }
}
