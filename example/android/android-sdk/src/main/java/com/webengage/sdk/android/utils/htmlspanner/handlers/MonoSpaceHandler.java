/*

 */
package com.webengage.sdk.android.utils.htmlspanner.handlers;

import com.webengage.sdk.android.utils.htmlspanner.style.Style;

/**
 * Sets monotype font.
 * 

 * 
 */
public class MonoSpaceHandler extends StyledTextHandler {

    @Override
    public Style getStyle() {
        return new Style().setFontFamily(
                getSpanner().getFontResolver().getMonoSpaceFont() );
    }
}
