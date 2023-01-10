package com.webengage.sdk.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetworkUtils {

    public static Map<String, Object> getAsMap(InputStream inputStream, boolean shouldHandleTransit) throws Exception {
        JsonParser jsonParser = new JsonParser(inputStream, shouldHandleTransit);
        return jsonParser.getAsMap();
    }

    public static List<Object> getAsList(InputStream inputStream, boolean shouldHandleTransit) throws Exception {
        JsonParser jsonParser = new JsonParser(inputStream, shouldHandleTransit);
        return jsonParser.getAsList();
    }

    public static String readEntireStream(InputStream inputStream) {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            inputStream.close();
            String data = sb.toString();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap loadBitmap(Response response, float reqWidthInDP, float reqHeightInDP, Context context) {
        int reqWidthInPixels = WebEngageUtils.dpToPixels(reqWidthInDP, context.getApplicationContext());
        int reqHeightInPixels = WebEngageUtils.dpToPixels(reqHeightInDP, context.getApplicationContext());
        return loadBitmap(response, reqWidthInPixels, reqHeightInPixels, context);
    }

    public static Bitmap loadBitmapByWidth(Response response, float reqWidthInDP, Context context) {
        int reqWidthInPixels = WebEngageUtils.dpToPixels(reqWidthInDP, context.getApplicationContext());
        int reqHeightInPixels = WebEngageUtils.getDisplayMetrics(context.getApplicationContext()).heightPixels;
        return loadBitmap(response, reqWidthInPixels, reqHeightInPixels, context);
    }

    public static Bitmap loadBitmapByHeight(Response response, float reqHeightInDP, Context context) {
        int reqHeightInPixels = WebEngageUtils.dpToPixels(reqHeightInDP, context.getApplicationContext());
        int reqWidthInPixels = WebEngageUtils.getDisplayMetrics(context.getApplicationContext()).widthPixels;
        return loadBitmap(response, reqWidthInPixels, reqHeightInPixels, context);
    }

    private static Bitmap loadBitmap(Response response, int reqWidthInPixels, int reqHeightInPixles, Context context) {
        InputStream is = response.getInputStream();
        if (is == null) {
            return null;
        }

        long contentLength = 0L;
        Map<String, List<String>> headers = response.getResponseHeaders();
        if (headers != null && headers.containsKey("content-length") && headers.get("content-length") != null && headers.get("content-length").size() > 0) {
            try {
                contentLength = Long.valueOf(headers.get("content-length").get(0));
            } catch (NumberFormatException e) {

            }
        }

        File file = null;
        FileOutputStream fout = null;
        try {
            file = File.createTempFile("image__", ".temp", context.getCacheDir());
            fout = new FileOutputStream(file);
            byte[] ar = new byte[1024];
            int length;
            while ((length = is.read(ar)) != -1) {
                fout.write(ar, 0, length);
            }
        } catch (Exception e) {

        } finally {
            try {
                is.close();
                fout.close();
            } catch (Exception e) {

            }
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (file != null) {
                double minSize = 0.98 * contentLength;
                if (file.length() >= minSize) {
                    BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    options.inSampleSize = calculateInSampleSize(options, reqWidthInPixels, reqHeightInPixles);
                    options.inJustDecodeBounds = false;
                    return loadBitmap(options, file, reqWidthInPixels, reqHeightInPixles);
                } else {
                    Logger.e(WebEngageConstant.TAG, "Incomplete image downloaded [url: " + response.getURL() + ", total image size: " + contentLength + " bytes, downloaded image size: " + file.length() + " bytes]");
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            file.delete();
        }
    }

    /**
     * works fine upto image size of 41 MB, decodeFile function was returning null for image of size 180 MB.
     * @param options
     * @param file
     * @param reqWidthInPixels
     * @param reqHeightInPixles
     * @return
     */
    private static Bitmap loadBitmap(BitmapFactory.Options options, File file, int reqWidthInPixels, int reqHeightInPixles) {
        if (options.inSampleSize > 32) {
            throw new OutOfMemoryError();
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            if (bitmap != null) {
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                double aspectRatioOfBitmap = (double) bitmapWidth / (double) bitmapHeight;
                double aspectRatioOfView = (double) reqWidthInPixels / (double) reqHeightInPixles;
                if (aspectRatioOfBitmap == aspectRatioOfView && bitmapWidth >= reqWidthInPixels && bitmapHeight >= reqHeightInPixles) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, reqWidthInPixels, reqHeightInPixles, false);
                }
                return bitmap;
            } else {
                // Handles null bitmap returned from BitmapFactory.decodeFile() for some corrupt image files
                InputStream inputStream = new FileInputStream(file.getAbsolutePath());
                InputStream jpegInputStream = new JpegClosedInputStream(inputStream);
                bitmap = BitmapFactory.decodeStream(jpegInputStream);
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
                return bitmap;
            }
        } catch (OutOfMemoryError error) {
            options.inSampleSize *= 2;
            return loadBitmap(options, file, reqWidthInPixels, reqHeightInPixles);
        } catch (Exception e) {
            return null;
        }
    }

    private static class JpegClosedInputStream extends InputStream {
        private static final int JPEG_EOI_1 = 0xFF;
        private static final int JPEG_EOI_2 = 0xD9;
        private final InputStream inputStream;
        private int bytesPastEnd;

        private JpegClosedInputStream(final InputStream iInputStream) {
            inputStream = iInputStream;
            bytesPastEnd = 0;
        }

        @Override
        public int read() throws IOException {
            int buffer = inputStream.read();
            if (buffer == -1) {
                if (bytesPastEnd > 0) {
                    buffer = JPEG_EOI_2;
                } else {
                    ++bytesPastEnd;
                    buffer = JPEG_EOI_1;
                }
            }
            return buffer;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Set<Response> preFetchResources(Set<String> urls, Context context) {
        Set<Response> responses = new HashSet<Response>();
        if (urls == null || urls.size() == 0) {
            return responses;
        }
        Iterator<String> iterator = urls.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            if (!url.isEmpty()) {
                final RequestObject requestObject = new RequestObject.Builder(url, RequestMethod.GET, context.getApplicationContext())
                        .setCachePolicy(CachePolicy.GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE)
                        .build();
                responses.add(requestObject.execute());
            }
        }
        return responses;

    }

    public static void preFetchResourcesAsync(final Set<String> urls, final Context context) {
        if (urls == null || urls.size() == 0) {
            return;
        }
        Iterator<String> iterator = urls.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            if (!url.isEmpty()) {
                RequestObject requestObject = new RequestObject.Builder(url, RequestMethod.GET, context.getApplicationContext())
                        .setCachePolicy(CachePolicy.GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE)
                        .build();
                AsyncRunner.execute(requestObject);

            }
        }

    }
}
