package camera.nice.com.nicecamerademo.before;


import android.content.Context;

import java.io.File;

/**
 * Created by sreay on 14-8-18.
 */
public class PathManager {
    public static String AreaFileName = "area.txt";
    public static String TempAreaFileName = "temp_area.txt";

    //自定义相机存储路径（图片经过剪裁后的图片，生成640*640）
    public static File getCropPhotoPath(Context context) {
        File photoFile = new File(getCropPhotoDir(context).getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpeg");
        return photoFile;
    }

    //私聊 群聊  保存图片存储的路径
    public static File getSavePicPath(Context context) {
        File photoFile = new File(getSavePicDir(context).getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpeg");
        return photoFile;
    }

    //存储剪裁后的图片的文件夹
    public static File getCropPhotoDir(Context context) {
        String path = FileUtil.getRootPath(context) + "/higo/crop/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    //调用系统相机拍照后图片存储路径
    public static File getCameraPhotoPath(Context context) {
        File photoFile = new File(getCameraPhotoDir(context).getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpeg");
        return photoFile;
    }

    //调用系统相机拍照后图片所在的文件夹
    public static File getCameraPhotoDir(Context context) {
        String path = FileUtil.getRootPath(context) + "/higo/photo/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    //存储私聊和群聊后的图片的文件夹
    public static File getSavePicDir(Context context) {
        String path = FileUtil.getRootPath(context) + "/higo/chat/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    //文件缓存目录
    public static File getImageLoaderCacheDir(Context context) {
        String path = FileUtil.getRootPath(context) + "/imageloader/cache";
        File cacheDir = new File(path);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    //文件缓存目录
    public static File getWebviewCacheDir(Context context) {
        String path = FileUtil.getRootPath(context) + "/webview/cache";
        File cacheDir = new File(path);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    //省市区文件的存储目录
    public static File getAreaCacheDir(Context context) {
        String path = FileUtil.getRootPath(context) + "/higo/area/";
        File cacheDir = new File(path);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    public static File getAreaTempFile(Context context) {
        File file = new File(getAreaCacheDir(context) + "/" + TempAreaFileName);
        return file;
    }

    public static File getAreaRealFile(Context context) {
        File file = new File(getAreaCacheDir(context) + "/" + AreaFileName);
        return file;
    }
}
