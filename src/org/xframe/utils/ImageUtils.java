package org.xframe.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.*;

public class ImageUtils {
    public static class ImageScaleResult {
        public String imageUri;
        public int width;
        public int height;
    }

    // maxWidth 必须小于等于 maxHeight
    // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    public static Bitmap getThumb(String filePath, int maxWidth, int maxHeight) {
        // 获取Bitmap的Size信息
        // JustDecodeBounds
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(new File(filePath)), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        int scale = 1;

        int outWidth = 0;
        int outHeight = 0;
        if (options.outWidth <= options.outHeight) {
            outWidth = options.outWidth;
            outHeight = options.outHeight;
        } else {
            outWidth = options.outHeight;
            outHeight = options.outWidth;
        }
        while (outWidth / scale > maxWidth || outHeight / scale > maxHeight)
            scale *= 2;

        options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        try {
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(new File(filePath)), null, options);
            return bmp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static ImageScaleResult scaleImageToWithSize(String filePath, int maxWidth, int maxHeight) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        try {
//            BitmapFactory.decodeStream(new FileInputStream(new File(filePath)), null, options);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        int scale = 1;
//
//        int outWidth = 0;
//        int outHeight = 0;
//        if (options.outWidth <= options.outHeight) {
//            outWidth = options.outWidth;
//            outHeight = options.outHeight;
//        } else {
//            outWidth = options.outHeight;
//            outHeight = options.outWidth;
//        }
//        while (outWidth / scale > maxWidth || outHeight / scale > maxHeight)
//            scale *= 2;
//
//        options = new BitmapFactory.Options();
//        options.inSampleSize = scale;
//        try {
//            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(new File(filePath)), null,
//                    options);
//
//            File myCaptureFile = null;
//
//            myCaptureFile = FileUtility.getTempImageFile();
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//
//            bos.flush();
//            bos.close();
//            ImageScaleResult result = new ImageScaleResult();
//            result.imageUri = myCaptureFile.getAbsolutePath();
//            result.width = outWidth / scale;
//            result.height = outHeight / scale;
//            return result;
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static String scaleImageTo(String filePath, int maxWidth, int maxHeight) {
//        ImageScaleResult result = scaleImageToWithSize(filePath, maxWidth, maxHeight);
//        if (result == null)
//            return null;
//        else
//            return result.imageUri;
//    }

    public static Bitmap scaleImage(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();

        // 缩放图片动作
        matrix.postScale(scale, scale);

        // 旋转图片 动作
        matrix.postRotate(45);

        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String imageUri2Path(Activity activity, Uri imageUri) {
        String imagePath = imageUri.getPath();
        if (imageUri.toString().startsWith("content://")) {
            // content provider返回的uri要另外处理
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = activity.managedQuery(imageUri, proj, null, null, null);
            if (cursor != null) {
                try {
                    int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        imagePath = cursor.getString(idx);
                    }
                } catch (IllegalArgumentException e) {
                }
            }
        } 
        return imagePath;
    }
}
