/*

 */
package com.webengage.sdk.android.utils.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import android.text.style.SuperscriptSpan;

import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

/**
 * Applies superscript.
 * 

 * 
 */
public class SuperScriptHandler extends TagNodeHandler {

	public void handleTagNode(TagNode node, SpannableStringBuilder builder,
			int start, int end, SpanStack spanStack) {
		spanStack.pushSpan(new SuperscriptSpan(), start, end);
	}

}
