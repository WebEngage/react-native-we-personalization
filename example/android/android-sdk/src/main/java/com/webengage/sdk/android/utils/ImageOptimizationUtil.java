package com.webengage.sdk.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.webengage.sdk.android.Logger;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageOptimizationUtil {

    public static int getImageByteCount(Bitmap image) {
        if(image == null)
            return 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
            return image.getByteCount();
        else
            return image.getRowBytes() * image.getHeight();
    }

    public static Bitmap getRenderableImage(Bitmap image, int maxSize) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (image.getByteCount() >= maxSize) {

                Logger.d(WebEngageConstant.TAG, "Bitmap size exceeds RemoteView limit");
                Logger.d(WebEngageConstant.TAG, "Bitmap size " + image.getByteCount());
                Logger.d(WebEngageConstant.TAG, "Bitmap maxSizePossible " + maxSize);

                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                byte[] imageByteArray = (bitmapStream.toByteArray());

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, options);

                options.inSampleSize = getSampleSize(maxSize, options.outHeight, options.outWidth);

                options.inJustDecodeBounds = false;

                return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, options);
            } else {
                Logger.d(WebEngageConstant.TAG, "Returning image without modification");
                return image;
            }
        }
        return image;
    }

    public static int getSampleSize(int maxSize, int currentHeightInPixels, int currentWidthInPixels) {
        int sampleSize = 1;
        int bitmapSize = (currentHeightInPixels * currentWidthInPixels * 4) / (sampleSize * sampleSize);
        Logger.d(WebEngageConstant.TAG, "Bitmap calculated size " + bitmapSize);

        while (maxSize < bitmapSize) {
            sampleSize *= 2;
            bitmapSize /= 4;
        }
        Logger.d(WebEngageConstant.TAG, "Bitmap Size set to " + bitmapSize);
        Logger.d(WebEngageConstant.TAG, "Bitmap sampleSize set to " + sampleSize);
        return sampleSize;
    }

    public static List<Bitmap> getOptimizedPriorityRenderableImageLists(List<Bitmap> validImages, int maxPossibleSize, int limit) {
        int highPriorityIndex = 1;
        float threshHoldPercentage = 0.4f;

        int sumOfSizeOfImages = 0;
        int thresholdSizeCenter = (int) (maxPossibleSize * threshHoldPercentage);

        Bitmap highPriorityImage = validImages.get(highPriorityIndex);
        if (getImageByteCount(highPriorityImage) <= thresholdSizeCenter) {
            sumOfSizeOfImages += getImageByteCount(highPriorityImage);
        } else {
            Bitmap renderableImage = getRenderableImage(highPriorityImage, thresholdSizeCenter);
            validImages.set(highPriorityIndex, renderableImage);
            sumOfSizeOfImages += getImageByteCount(renderableImage);
        }

        int thresholdSize = (maxPossibleSize - sumOfSizeOfImages) / (limit - 1);

        for (int i = 0; i < limit; i++) {

            if (i != highPriorityIndex) {
                Logger.d(WebEngageConstant.TAG, "ThresholdSize : " + thresholdSize);
                Logger.d(WebEngageConstant.TAG, "sumOfSizeOfImages : " + sumOfSizeOfImages);

                if (getImageByteCount(validImages.get(i)) <= thresholdSize) {
                    sumOfSizeOfImages += getImageByteCount(validImages.get(i));
                    Logger.d(WebEngageConstant.TAG, "Image size " + getImageByteCount(validImages.get(i)) + " is below threshold of " + thresholdSize);
                } else {
                    Bitmap renderableImage = getRenderableImage(validImages.get(i), thresholdSize);
                    validImages.set(i, renderableImage);
                    sumOfSizeOfImages += getImageByteCount(renderableImage);
                    Logger.d(WebEngageConstant.TAG, "renderableImage Size " + getImageByteCount(renderableImage));
                }
                if (limit - 2 - i > 0)
                    thresholdSize = (maxPossibleSize - sumOfSizeOfImages) / (limit - 2 - i);
                Logger.d(WebEngageConstant.TAG, "remaining images :" + (limit - 2 - i));
            }
        }

        Logger.d(WebEngageConstant.TAG, "Final sumOfSizeOfImages : " + sumOfSizeOfImages);

        return validImages;
    }

    public static List<Bitmap> getOptimizedRenderableImageLists(List<Bitmap> validImages, int maxPossibleSize, int limit) {
        List<Bitmap> renderableImagesList = new ArrayList<>();
        int thresholdSize = maxPossibleSize / limit;
        int sumOfSizeOfImages = 0;

        int currentSumOfBitmaps = 0;
        for (int i = 0; i < limit; i++) {
            currentSumOfBitmaps += getImageByteCount(validImages.get(i));
        }

        for (int i = 0; i < validImages.size(); i++) {
            Logger.d(WebEngageConstant.TAG, "ThresholdSize : " + thresholdSize);
            Logger.d(WebEngageConstant.TAG, "sumOfSizeOfImages : " + sumOfSizeOfImages);
            Logger.d(WebEngageConstant.TAG, "currentSumOfBitmaps : " + currentSumOfBitmaps);
            if (currentSumOfBitmaps > 5000000) {
                if (getImageByteCount(validImages.get(i)) > thresholdSize) {
                    Bitmap renderableImage = getRenderableImage(validImages.get(i), thresholdSize);
                    currentSumOfBitmaps = currentSumOfBitmaps - (getImageByteCount(validImages.get(i)) - getImageByteCount(renderableImage));
                    renderableImagesList.add(renderableImage);
                    sumOfSizeOfImages += getImageByteCount(renderableImage);
                    Logger.d(WebEngageConstant.TAG, "renderableImage Size " + getImageByteCount(renderableImage));
                } else {
                    renderableImagesList.add(validImages.get(i));
                    sumOfSizeOfImages += getImageByteCount(validImages.get(i));
                    Logger.d(WebEngageConstant.TAG, "Image size " + getImageByteCount(validImages.get(i)) + " is below threshold of " + thresholdSize);
                }
            } else {
                renderableImagesList.add(validImages.get(i));
                sumOfSizeOfImages += getImageByteCount(validImages.get(i));
                Logger.d(WebEngageConstant.TAG, "no need of resizing as imagelist size is less than 5mb");
            }
            if (limit - 1 - i != 0)
                thresholdSize = (maxPossibleSize - sumOfSizeOfImages) / (limit - 1 - i);
        }

        Logger.d(WebEngageConstant.TAG, "Final sumOfSizeOfImages : " + sumOfSizeOfImages);

        return renderableImagesList;
    }
}
