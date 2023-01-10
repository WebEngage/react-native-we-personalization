/*

 */

package com.webengage.sdk.android.utils.htmlspanner.handlers;

import android.text.SpannableStringBuilder;

import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.handlers.attributes.WrappingStyleHandler;
import com.webengage.sdk.android.utils.htmlspanner.spans.ListItemSpan;
import com.webengage.sdk.android.utils.htmlspanner.style.Style;

import org.htmlcleaner.TagNode;

/**
 * Handles items in both numbered and unordered lists.
 * 

 * 
 */
public class ListItemHandler extends WrappingStyleHandler {

	public ListItemHandler(StyledTextHandler wrappedHandler) {
		super(wrappedHandler);
	}

	private int getMyIndex(TagNode node) {
		if (node.getParent() == null) {
			return -1;
		}

		int i = 1;

		for (Object child : node.getParent().getAllChildren()) {
			if (child == node) {
				return i;
			}

			if (child instanceof TagNode) {
				TagNode childNode = (TagNode) child;
				if ("li".equals(childNode.getName())) {
					i++;
				}
			}
		}

		return -1;
	}

	private String getParentName(TagNode node) {
		if (node.getParent() == null) {
			return null;
		}

		return node.getParent().getName();
	}

	@Override
	public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, Style useStyle, SpanStack spanStack) {
		if (builder.length() > 0
				&& builder.charAt(builder.length() - 1) != '\n') {
			builder.append("\n");
		}

		if ("ol".equals(getParentName(node))) {
			ListItemSpan bSpan = new ListItemSpan(getMyIndex(node));
            spanStack.pushSpan(bSpan, start, end);
		} else if ("ul".equals(getParentName(node))) {
			// Unicode bullet character.
			ListItemSpan bSpan = new ListItemSpan();
            spanStack.pushSpan(bSpan, start, end);
		}
		super.handleTagNode(node, builder, start, end, useStyle, spanStack);
	}
}