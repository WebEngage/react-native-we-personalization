/*

 */

package com.webengage.sdk.android.utils.htmlspanner.spans;

import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

public class AlignOppositeSpan implements AlignmentSpan {

	@Override
	public Alignment getAlignment() {
		return Alignment.ALIGN_OPPOSITE;
	}
}
