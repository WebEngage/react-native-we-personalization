/*

 */
package com.webengage.sdk.android.utils.htmlspanner.handlers;


import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;

import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

/**
 * Creates clickable links.
 * 

 * 
 */
public class LinkHandler extends TagNodeHandler {

	@Override
	public void handleTagNode(TagNode node, SpannableStringBuilder builder,
			int start, int end, SpanStack spanStack) {

		final String href = node.getAttributeByName("href");
		spanStack.pushSpan(new URLSpan(href), start, end);
	}
}