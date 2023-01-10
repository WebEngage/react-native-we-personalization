package com.webengage.sdk.android.utils.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import android.util.Log;

import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.TagNodeHandler;
import com.webengage.sdk.android.utils.htmlspanner.css.CSSCompiler;
import com.webengage.sdk.android.utils.htmlspanner.cssparser.CSSParser;
import com.webengage.sdk.android.utils.htmlspanner.cssparser.Rule;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

/**
 * TagNodeHandler that reads <style> blocks and parses the CSS rules within.
 */
public class StyleNodeHandler extends TagNodeHandler {

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {

        if ( getSpanner().isAllowStyling() ) {

            if ( node.getAllChildren().size() == 1 ) {
                Object childNode = node.getAllChildren().get(0);

                if ( childNode instanceof ContentNode ) {
                    parseCSSFromText( ( (ContentNode) childNode ).getContent(),
                            spanStack );
                }
            }
        }

    }

    private void parseCSSFromText( String text, SpanStack spanStack ) {
        try {
            for ( Rule rule: CSSParser.parse( text ) ) {
                spanStack.registerCompiledRule(CSSCompiler.compile(rule, getSpanner()));
            }
        } catch ( Exception e ) {
            Logger.e(WebEngageConstant.TAG, "Unparseable CSS definition", e );
        }
    }

    @Override
    public boolean rendersContent() {
        return true;
    }
}
