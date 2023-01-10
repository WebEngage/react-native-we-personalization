/*

 */
package com.webengage.sdk.android.utils.htmlspanner.handlers;

import android.text.SpannableStringBuilder;

import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

/**
 * Adds a specified number of newlines.
 * 
 * Used to implement p and br tags.
 * 

 * 
 */
public class NewLineHandler extends WrappingHandler {

	private int numberOfNewLines;

	/**
	 * Creates this handler for a specified number of newlines.
	 * 
	 * @param howMany
	 */
	public NewLineHandler(int howMany, TagNodeHandler wrappedHandler) {
        super(wrappedHandler);
		this.numberOfNewLines = howMany;
	}

	public void handleTagNode(TagNode node, SpannableStringBuilder builder,
			int start, int end, SpanStack spanStack) {

        super.handleTagNode(node, builder, start, end, spanStack);

		for (int i = 0; i < numberOfNewLines; i++) {
			appendNewLine(builder);
		}
	}
}
