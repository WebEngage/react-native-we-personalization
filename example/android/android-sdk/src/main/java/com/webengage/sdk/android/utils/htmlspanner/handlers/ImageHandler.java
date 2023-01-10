/*

 */
package com.webengage.sdk.android.utils.htmlspanner.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableStringBuilder;

import com.webengage.sdk.android.utils.htmlspanner.SpanStack;
import com.webengage.sdk.android.utils.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;

/*import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;*/

/**
 * Handles image tags.
 * 
 * The default implementation tries to load images through a URL.openStream(),
 * override loadBitmap() to implement your own loading.
 * 

 * 
 */
public class ImageHandler extends TagNodeHandler {

    private static final String ERRORTAG = "Image error";
    private static final long MAXIMUM_TIME=450;

    @Override
	public void handleTagNode(TagNode node, final SpannableStringBuilder builder,
							  final int start, final int end, final SpanStack stack) {
		/*String src = node.getAttributeByName("src");

		builder.append("\uFFFC");

        synchronized (this) {
            // we load the image on a different thread
                @Override
                public void onCompleted() {
                    // do nothing
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(ERRORTAG, "" + e);
                }

                @Override
                public void onNext(String s) {
                    Bitmap bitmap = loadBitmap(s);
                    if (bitmap != null) {
                        Drawable drawable = new BitmapDrawable(bitmap);
                        drawable.setBounds(0, 0, bitmap.getWidth() - 1,
                                bitmap.getHeight() - 1);
                        stack.pushSpan(new ImageSpan(drawable), start, builder.length());
                    }
                }
            });
            // we wait until the image is loaded,
            // if the image is not loaded until the maximum time we didn't show it
            try {
                this.wait(MAXIMUM_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

	/**
	 * Loads a Bitmap from the given url.
	 * 
	 * @param url
	 * @return a Bitmap, or null if it could not be loaded.
	 */
	protected Bitmap loadBitmap(String url) {
		try {
			return BitmapFactory.decodeStream(new URL(url).openStream());
		} catch (IOException io) {
			return null;
		}
	}
}
