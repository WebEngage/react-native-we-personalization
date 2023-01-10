package com.webengage.sdk.android.utils.htmlspanner.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import android.util.Log;

import com.webengage.sdk.android.utils.htmlspanner.HtmlSpanner;
import com.webengage.sdk.android.utils.htmlspanner.style.Style;
import com.webengage.sdk.android.utils.htmlspanner.style.StyleValue;

/**
 */
public class BorderSpan implements LineBackgroundSpan {

    private int start;
    private int end;

    private Style style;

    private boolean usecolour;

    public BorderSpan( Style style, int start, int end, boolean usecolour ) {
        this.start = start;
        this.end = end;

        this.style = style;
        this.usecolour = usecolour;
    }


    @Override
    public void drawBackground(Canvas c, Paint p,
                               int left, int right,
                               int top, int baseline, int bottom,
                               CharSequence text, int start, int end,
                               int lnum) {

        int baseMargin = 0;

        if ( style.getMarginLeft() != null ) {
            StyleValue styleValue = style.getMarginLeft();

            if ( styleValue.getUnit() == StyleValue.Unit.PX ) {
                if ( styleValue.getIntValue() > 0 ) {
                    baseMargin = styleValue.getIntValue();
                }
            } else if ( styleValue.getFloatValue() > 0f ) {
                baseMargin = (int) (styleValue.getFloatValue() * HtmlSpanner.HORIZONTAL_EM_WIDTH);
            }

            //Leave a little bit of room
            baseMargin--;
        }

        if ( baseMargin > 0 ) {
            left = left + baseMargin;
        }

        int originalColor = p.getColor();
        float originalStrokeWidth = p.getStrokeWidth();

        if ( usecolour && style.getBackgroundColor() != null ) {
            p.setColor(style.getBackgroundColor());
            p.setStyle(Paint.Style.FILL);

            c.drawRect(left,top,right,bottom,p);
        }

        if ( usecolour && style.getBorderColor() != null ) {
            p.setColor( style.getBorderColor() );
        }

        int strokeWidth;

        if ( style.getBorderWidth() != null && style.getBorderWidth().getUnit() == StyleValue.Unit.PX ) {
            strokeWidth = style.getBorderWidth().getIntValue();
        } else {
            strokeWidth = 1;
        }

        p.setStrokeWidth( strokeWidth );
        right -= strokeWidth;

        if ( start <= this.start ) {
            //Log.d("BorderSpan", "Drawing first line");
            c.drawLine(left, top, right, top, p);
        }

        if ( end >= this.end ) {
            //Log.d("BorderSpan", "Drawing last line");
            c.drawLine(left, bottom, right, bottom, p);
        }

        c.drawLine(left,top,left,bottom, p);
        c.drawLine(right,top,right,bottom, p);


        p.setColor(originalColor);
        p.setStrokeWidth(originalStrokeWidth);
    }



}

