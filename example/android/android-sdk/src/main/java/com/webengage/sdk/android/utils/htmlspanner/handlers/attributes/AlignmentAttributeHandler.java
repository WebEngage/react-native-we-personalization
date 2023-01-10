/*

 */
package com.webengage.sdk.android.utils.htmlspanner.handlers.attributes;

import android.text.SpannableStringBuilder;

import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.handlers.StyledTextHandler;
import com.webengage.sdk.android.utils.htmlspanner.style.Style;

import org.htmlcleaner.TagNode;

/**
 * Handler for align='left|right|center' attributes.
 * 

 *
 */
public class AlignmentAttributeHandler extends WrappingStyleHandler {
	

	public AlignmentAttributeHandler(StyledTextHandler wrapHandler) {
		super(wrapHandler);
	}


	@Override
	public void handleTagNode(TagNode node, SpannableStringBuilder builder,
			int start, int end, Style style, SpanStack spanStack) {
		
		String align = node.getAttributeByName("align");

		if ( "right".equalsIgnoreCase(align) ) {
		    style = style.setTextAlignment(Style.TextAlignment.RIGHT);
		} else if ( "center".equalsIgnoreCase(align) ) {
            style =  style.setTextAlignment(Style.TextAlignment.CENTER);
		} else if ( "left".equalsIgnoreCase(align) ) {
            style =  style.setTextAlignment(Style.TextAlignment.LEFT);
		}
		
		super.handleTagNode(node, builder, start, end, style, spanStack);
	}
	
}
