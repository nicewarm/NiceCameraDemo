package camera.nice.com.nicecamerademo.before;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sreay on 14-10-24.
 */
public class BitmapUtil {
    /**
     * 从exif信息获取图片旋转角度
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 对图片进行压缩选择处理
     *
     * @param picPath
     * @return
     */
    public static Bitmap compressRotateBitmap(String picPath) {
        Bitmap bitmap = null;
        int degree = readPictureDegree(picPath);
        if (degree == 90) {
            bitmap = featBitmapToSuitable(picPath, 500, 1.8f);
            bitmap = rotate(bitmap, 90);
        } else {
            bitmap = featBitmapToSuitable(picPath, 500, 1.8f);
        }
        return bitmap;
    }

    /**
     * 转换bitmap为字节数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        final int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] image = out.toByteArray();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;

    }

    /**
     * 获取合适尺寸的图片 图片的长或高中较大的值要 < suitableSize*factor
     *
     * @param path
     * @param suitableSize
     * @return
     */
    public static Bitmap featBitmapToSuitable(String path, int suitableSize,
                                              float factor) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            int bitmap_w = options.outWidth;
            int bitmap_h = options.outHeight;
            int max_edge = bitmap_w > bitmap_h ? bitmap_w : bitmap_h;
            while (max_edge / (float) suitableSize > factor) {
                options.inSampleSize <<= 1;
                max_edge >>= 1;
            }
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
        }
        return bitmap;
    }

    public static Bitmap featBitmap(String path, int width) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            int bitmap_w = options.outWidth;
            while (bitmap_w / (float) width > 2) {
                options.inSampleSize <<= 1;
                bitmap_w >>= 1;
            }
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
        }
        return bitmap;
    }

    public static Bitmap loadBitmap(String path, int maxSideLen) {
        if (null == path) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inSampleSize = Math.max(options.outWidth / maxSideLen, options.outHeight / maxSideLen);
        if (options.inSampleSize < 1) {
            options.inSampleSize = 1;
        }
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            if (bitmap != bitmap) {
                bitmap.recycle();
            }
            return bitmap;
        } catch (OutOfMemoryError e) {
            Debug.debug(e.toString());
        }
        return null;
    }

    public static Bitmap loadBitmap(String path) {
        if (null == path) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        //不对图进行压缩
        int inSampleSize = 1;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
            while (bitmap == null) {
                options.inSampleSize = inSampleSize * 2;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(path, options);
            }
        } catch (OutOfMemoryError e) {
            while (bitmap == null) {
                options.inSampleSize = inSampleSize * 2;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeFile(path, options);
            }
        }
        return bitmap;
    }

    public static Bitmap loadFromAssets(Activity activity, String name, int sampleSize, Bitmap.Config config) {
        AssetManager asm = activity.getAssets();
        try {
            InputStream is = asm.open(name);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            options.inPreferredConfig = config;
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                return bitmap;
            } catch (OutOfMemoryError e) {
                Debug.debug("decode bitmap " + e.toString());
            }
        } catch (IOException e) {
            Debug.debug(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeByteArrayUnthrow(byte[] data, BitmapFactory.Options opts) {
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (Throwable e) {
            Debug.debug(e.toString());
        }

        return null;
    }

    // Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (null != b2 && b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

    public static Bitmap rotateNotRecycleOriginal(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees);
            try {
                return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

    public static Bitmap rotateAndScaleForWechat(Bitmap b, int degrees, float maxSideLen, boolean recycle) {
        if (null == b || degrees == 0 && b.getWidth() <= maxSideLen + 10 && b.getHeight() <= maxSideLen + 10) {
            return b;
        }

        Matrix m = new Matrix();
        if (degrees != 0) {
            m.setRotate(degrees);
        }

        float scale = Math.min(maxSideLen / b.getWidth(), maxSideLen / b.getHeight());
        if (scale < 1) {
            m.postScale(scale, scale);
        }
        Debug.debug("degrees: " + degrees + ", scale: " + scale);
        try {
            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            if (null != b2 && b != b2) {
                if (recycle) {
                    b.recycle();
                }
                b = b2;
            }
        } catch (OutOfMemoryError e) {
        }
        return b;
    }

    public static Bitmap rotateAndScale(Bitmap b, int degrees, float maxSideLen) {
        if (null == b || degrees == 0 && b.getWidth() <= maxSideLen + 10 && b.getHeight() <= maxSideLen + 10) {
            return b;
        }

        Matrix m = new Matrix();
        if (degrees != 0) {
            m.setRotate(degrees);
        }

        float scale = Math.min(maxSideLen / b.getWidth(), maxSideLen / b.getHeight());
        if (scale < 1) {
            m.postScale(scale, scale);
        }
        Debug.debug("degrees: " + degrees + ", scale: " + scale);

        try {
            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            if (null != b2 && b != b2) {
                b.recycle();
                b = b2;
            }
        } catch (OutOfMemoryError e) {
        }

        return b;
    }

    public static Bitmap getViewBitmap(View view) {
        boolean willNotCache = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(false);
        int color = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            view.destroyDrawingCache();
        }
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCache);
        view.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    public static boolean saveBitmap2file(Bitmap bmp, File file, Bitmap.CompressFormat format, int quality) {
        if (file.isFile())
            file.delete();
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Debug.debug(e.toString());
            return false;
        }

        return bmp.compress(format, quality, stream);
    }

    public static Bitmap makeWaterMark(Bitmap bitmap, Bitmap waterMarkBitmap) {
        if (bitmap == null || waterMarkBitmap == null) {
            return null;
        }
        int bgWidth = bitmap.getWidth();
        int bgHeight = bitmap.getHeight();
        int waterMarkWidth = waterMarkBitmap.getWidth();
        int waterMarkHeight = waterMarkBitmap.getHeight();
        float widthRatio = bgWidth / waterMarkWidth;
        float heightRatio = bgHeight / waterMarkHeight;
        float finalMinRatio = Math.min(widthRatio, heightRatio);
        if (bgWidth > bgHeight && finalMinRatio > 4) //横评时会占满屏幕，休整下
        {
            finalMinRatio = 4;
        }
        finalMinRatio = finalMinRatio * 0.65f;
        Bitmap resultBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        if (finalMinRatio > 1) {
            Matrix matrixTranslate = new Matrix();
            matrixTranslate.setTranslate((bgWidth - waterMarkWidth * finalMinRatio) / 2 - 20, (bgHeight - waterMarkHeight * finalMinRatio) / 2);
            Matrix matrixScale = new Matrix();
            matrixScale.setScale(finalMinRatio, finalMinRatio);
            Matrix matrix = new Matrix();
            matrix.setConcat(matrixTranslate, matrixScale);
            canvas.drawBitmap(waterMarkBitmap, matrix, null);
        } else {
            Matrix matrix = new Matrix();
            matrix.setScale(finalMinRatio, finalMinRatio);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return resultBitmap;
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    public static Bitmap cropPhotoImage(Bitmap bitmap,int cropWidth,int cropHeight) {
        Bitmap result = bitmap;
        try {
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            float scale = Math.max(cropWidth * 1.0f / bw, cropHeight * 1.0f / bh);
            Bitmap cropBitmap = Bitmap.createBitmap(bitmap, (int) ((bw * scale - cropWidth) / scale / 2), (int) ((bh * scale - cropHeight) / scale / 2), (int) (cropWidth / scale), (int) (cropHeight / scale));
            if (cropBitmap != null && cropBitmap != bitmap) {
                bitmap.recycle();
            }
            result = Bitmap.createScaledBitmap(cropBitmap, cropWidth, cropHeight, false);
            if (result != null && cropBitmap != result) {
                cropBitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
