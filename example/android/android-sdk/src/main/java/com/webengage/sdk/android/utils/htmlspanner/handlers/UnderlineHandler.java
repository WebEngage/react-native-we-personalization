/*

 */
package com.webengage.sdk.android.utils.htmlspanner.handlers;


import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

/**
 * Creates clickable links.
 * 

 * 
 */
public class UnderlineHandler extends TagNodeHandler {

	@Override
	public void handleTagNode(TagNode node, SpannableStringBuilder builder,
			int start, int end, SpanStack spanStack) {

		spanStack.pushSpan(new UnderlineSpan(), start, end);
	}
}