package com.webengage.sdk.android.utils.htmlspanner.style;

import static java.lang.Math.min;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import com.webengage.sdk.android.utils.htmlspanner.FontFamily;
import com.webengage.sdk.android.utils.htmlspanner.HtmlSpanner;
import com.webengage.sdk.android.utils.htmlspanner.SpanCallback;
import com.webengage.sdk.android.utils.htmlspanner.spans.AlignNormalSpan;
import com.webengage.sdk.android.utils.htmlspanner.spans.AlignOppositeSpan;
import com.webengage.sdk.android.utils.htmlspanner.spans.BorderSpan;
import com.webengage.sdk.android.utils.htmlspanner.spans.CenterSpan;
import com.webengage.sdk.android.utils.htmlspanner.spans.FontFamilySpan;
import com.webengage.sdk.android.utils.htmlspanner.spans.LineHeightSpanImpl;

/**
 * To change this template use File | Settings | File Templates.
 */
public class StyleCallback implements SpanCallback {

    private int start;
    private int end;

    private FontFamily defaultFont;
    private Style useStyle;

    private static final float SCREEN_DENSITY=(Resources.getSystem().getDisplayMetrics().densityDpi)/ DisplayMetrics.DENSITY_DEFAULT;

    public StyleCallback( FontFamily defaultFont, Style style, int start, int end ) {
        this.defaultFont = defaultFont;
        this.useStyle = style;
        this.start = start;
        this.end = end;
    }

    @Override
    public void applySpan(HtmlSpanner spanner, SpannableStringBuilder builder) {

        if ( useStyle.getFontFamily() != null || useStyle.getFontStyle() != null || useStyle.getFontWeight() != null ) {

            FontFamilySpan originalSpan = getFontFamilySpan(builder, start, end);
            FontFamilySpan newSpan;

            if ( useStyle.getFontFamily() == null && originalSpan == null ) {
                newSpan = new FontFamilySpan(this.defaultFont);
            } else if ( useStyle.getFontFamily() != null ) {
                newSpan = new FontFamilySpan(  useStyle.getFontFamily() );
            } else {
                newSpan = new FontFamilySpan(originalSpan.getFontFamily());
            }

            if ( useStyle.getFontWeight() != null ) {
                newSpan.setBold( useStyle.getFontWeight() == Style.FontWeight.BOLD );
                builder.setSpan(new StyleSpan(Typeface.BOLD),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else if ( originalSpan != null && originalSpan.isBold()) {
                newSpan.setBold( originalSpan.isBold() );
                builder.setSpan(new StyleSpan(Typeface.BOLD),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if ( useStyle.getFontStyle() != null ) {
                newSpan.setItalic( useStyle.getFontStyle() == Style.FontStyle.ITALIC );
                builder.setSpan(new StyleSpan(Typeface.ITALIC),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ( originalSpan != null && originalSpan.isItalic()) {
                newSpan.setItalic( originalSpan.isItalic() );
                builder.setSpan(new StyleSpan(Typeface.ITALIC),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            //Log.d("StyleCallback", "Applying FontFamilySpan from " + start + " to " + end + " on text " + builder.subSequence(start, end));
            //Log.d("StyleCallback", "FontFamilySpan: " + newSpan );

            builder.setSpan(newSpan, start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //If there's no border, we use a BackgroundColorSpan to draw colour behind the text
        if ( spanner.isUseColoursFromStyle() &&  useStyle.getBackgroundColor() != null  && useStyle.getBorderStyle() == null ) {
            //Log.d("StyleCallback", "Applying BackgroundColorSpan with color " + useStyle.getBackgroundColor() + " from " + start + " to " + end + " on text " + builder.subSequence(start, end));
              builder.setSpan(new BackgroundColorSpan(useStyle.getBackgroundColor()), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //If there's a line height, we use an implementation of LineHeightSpan to draw space behind the text
        if (useStyle.getLineHeight() != null) {
            //Log.d("StyleCallback", "Applying LineHeightSpan with value " + useStyle.getLineHeight().getIntValue() + " from " + start + " to " + end + " on text " + builder.subSequence(start, end));
            StyleValue lineHeight = useStyle.getLineHeight();
            LineHeightSpanImpl span;
            if(lineHeight.getUnit() == StyleValue.Unit.PX){
                span = new LineHeightSpanImpl((int) Math.ceil(lineHeight.getIntValue() * SCREEN_DENSITY));
            } else {
                span = new LineHeightSpanImpl(lineHeight.getFloatValue(), lineHeight.getUnit(), spanner.getTextSize());
            }
            builder.setSpan(span,start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //If there is a border, the BorderSpan will also draw the background colour if needed.
        if ( useStyle.getBorderStyle() != null ) {
            builder.setSpan(new BorderSpan(useStyle, start, end, spanner.isUseColoursFromStyle()), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ( useStyle.getFontSize() != null ) {

            StyleValue styleValue = useStyle.getFontSize();

            if ( styleValue.getUnit() == StyleValue.Unit.PX || styleValue.getUnit() == StyleValue.Unit.PT ) {
                if ( styleValue.getIntValue() > 0 ) {
                    // Log.d("StyleCallback", "Applying AbsoluteSizeSpan with size " + useStyle.getAbsoluteFontSize() + " from " + start + " to " + end + " on text " + builder.subSequence(start, end));
                    builder.setSpan(new AbsoluteSizeSpan(styleValue.getIntValue()), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                if ( styleValue.getFloatValue() > 0f ) {
                    //Log.d("StyleCallback", "Applying RelativeSizeSpan with size " + useStyle.getRelativeFontSize() + " from " + start + " to " + end + " on text " + builder.subSequence(start, end));
                    builder.setSpan(new RelativeSizeSpan(styleValue.getFloatValue()), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        if ( spanner.isUseColoursFromStyle() && useStyle.getColor() != null ) {
            //Log.d("StyleCallback", "Applying ForegroundColorSpan from " + start + " to " + end + " on text " + builder.subSequence(start, end) );
            builder.setSpan(new ForegroundColorSpan(useStyle.getColor()), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ( useStyle.getTextAlignment() != null ) {

            AlignmentSpan alignSpan = null;

            switch ( useStyle.getTextAlignment()  ) {
                case LEFT:
                    alignSpan = new AlignNormalSpan();
                    break;
                case CENTER:
                    alignSpan = new CenterSpan();
                    break;
                case RIGHT:
                    alignSpan = new AlignOppositeSpan();
                    break;
            }

            //Log.d("StyleCallback", "Applying AlignmentSpan from " + start + " to " + end + " on text " + builder.subSequence(start, end) );
            builder.setSpan(alignSpan, start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        //apply text decoration
        if(useStyle.getTextDecoration() != null){

            switch (useStyle.getTextDecoration()){
                case UNDERLINE: {builder.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); break;}
                case LINETHROUGH: {builder.setSpan(new StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);break;}
                default: break;
            }
        }


        if ( useStyle.getTextIndent() != null ) {

            StyleValue styleValue = useStyle.getTextIndent();

            int marginStart = start;
            while ( marginStart < end && builder.charAt(marginStart) == '\n' ) {
                marginStart++;
            }

            int marginEnd = min( end, marginStart +1 );

            //Log.d("StyleCallback", "Applying LeadingMarginSpan from " + marginStart + " to " + marginEnd + " on text " + builder.subSequence(marginStart, marginEnd));

            if ( styleValue.getUnit() == StyleValue.Unit.PX ) {
                if ( styleValue.getIntValue() > 0 ) {
                    builder.setSpan(new LeadingMarginSpan.Standard(styleValue.getIntValue(), 0), marginStart, marginEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            } else {
                if ( styleValue.getFloatValue() > 0f ) {
                    builder.setSpan(new LeadingMarginSpan.Standard( (int)
                        ( HtmlSpanner.HORIZONTAL_EM_WIDTH * styleValue.getFloatValue()), 0), marginStart, marginEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

        }

        /* We ignore negative horizontal margins, since that would cause the text to be rendered off-screen */
        if ( useStyle.getMarginLeft() != null ) {
            StyleValue styleValue = useStyle.getMarginLeft();

            if ( styleValue.getUnit() == StyleValue.Unit.PX ) {
                if ( styleValue.getIntValue() > 0 ) {
                    builder.setSpan(new LeadingMarginSpan.Standard(styleValue.getIntValue() ), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            } else if ( styleValue.getFloatValue() > 0f ) {
                builder.setSpan(new LeadingMarginSpan.Standard(
                        (int) (HtmlSpanner.HORIZONTAL_EM_WIDTH * styleValue.getFloatValue())), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

    }

    /**
     * Returns the current FontFamilySpan in use on the given subsection of the builder.
     *
     * If no FontFamily has been set yet, spanner.getDefaultFont() is returned.
     *
     * @param builder the text to check
     * @param start start of the section
     * @param end end of the section
     * @return a FontFamily object
     */
    private FontFamilySpan getFontFamilySpan( SpannableStringBuilder builder, int start, int end ) {

        FontFamilySpan[] spans = builder.getSpans(start, end, FontFamilySpan.class);

        if ( spans != null && spans.length > 0 ) {
            return spans[spans.length-1];
        }

        return null;
    }

}
