/*

 */

package com.webengage.sdk.android.utils.htmlspanner.spans;

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import com.webengage.sdk.android.utils.htmlspanner.FontFamily;

public class FontFamilySpan extends TypefaceSpan {

	private final FontFamily fontFamily;

	private boolean bold;
	private boolean italic;

	public FontFamilySpan(FontFamily type) {
		super(type == null ? null : type.getName());
		this.fontFamily = type;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public FontFamily getFontFamily() {
		return fontFamily;
	}
	
	public boolean isBold() {
		return bold;
	}
	
	public boolean isItalic() {
		return italic;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		applyCustomTypeFace(ds, this.fontFamily);
	}

	@Override
	public void updateMeasureState(TextPaint paint) {
		applyCustomTypeFace(paint, this.fontFamily);
	}

    public void updateMeasureState(Paint paint) {
        applyCustomTypeFace(paint, this.fontFamily);
    }


	private void applyCustomTypeFace(Paint paint, FontFamily tf) {

		paint.setAntiAlias(true);
		
		paint.setTypeface(tf.getDefaultTypeface());

		if (bold) {
			if (tf.isFakeBold()) {
				paint.setFakeBoldText(true);
			} else {
				paint.setTypeface(tf.getBoldTypeface());
			}
		}

		if (italic) {
			if (tf.isFakeItalic()) {
				paint.setTextSkewX(-0.25f);
			} else {
				paint.setTypeface(tf.getItalicTypeface());
			}
		}

		if (bold && italic && tf.getBoldItalicTypeface() != null) {
			paint.setTypeface(tf.getBoldItalicTypeface());
		}
	}

    public String toString() {
        StringBuilder builder = new StringBuilder("{\n");
        if(fontFamily != null)
        	builder.append( "  font-family: " + fontFamily.getName() + "\n" );
        builder.append( "  bold: " + isBold() + "\n");
        builder.append( "  italic: " + isItalic() + "\n" );
        builder.append( "}");

        return builder.toString();
    }
}
