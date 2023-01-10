/*

 */
package com.webengage.sdk.android.utils.htmlspanner.handlers;

import android.text.SpannableStringBuilder;

import com.webengage.sdk.android.utils.htmlspanner.FontFamily;
import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.TagNodeHandler;
import com.webengage.sdk.android.utils.htmlspanner.TextUtil;
import com.webengage.sdk.android.utils.htmlspanner.spans.FontFamilySpan;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

/**
 * Handles pre tags, setting the style to monospace and preserving the
 * formatting.
 * 

 * 
 */
public class PreHandler extends TagNodeHandler {

	private void getPlainText(StringBuffer buffer, Object node) {
		if (node instanceof ContentNode) {

			ContentNode contentNode = (ContentNode) node;
			String text = TextUtil.replaceHtmlEntities(contentNode.getContent()
					.toString(), true);

			buffer.append(text);

		} else if (node instanceof TagNode) {
			TagNode tagNode = (TagNode) node;
			for (Object child : tagNode.getAllChildren()) {
				getPlainText(buffer, child);
			}
		}
	}

	@Override
	public void handleTagNode(TagNode node, SpannableStringBuilder builder,
			int start, int end, SpanStack spanStack) {

		StringBuffer buffer = new StringBuffer();
		getPlainText(buffer, node);

		builder.append(buffer.toString());

        FontFamily monoSpace = getSpanner().getFontResolver().getMonoSpaceFont();
		spanStack.pushSpan(new FontFamilySpan(monoSpace), start, builder.length());
		appendNewLine(builder);
		appendNewLine(builder);
	}

	@Override
	public boolean rendersContent() {
		return true;
	}

}