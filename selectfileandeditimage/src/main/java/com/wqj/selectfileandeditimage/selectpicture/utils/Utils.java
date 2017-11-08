package com.wqj.selectfileandeditimage.selectpicture.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.util.TypedValue;
import com.wqj.selectfileandeditimage.R;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by snsoft on 2016/9/28.
 */

public class Utils {

    private static final String TAG = "Utils";
    public static long mb = 1024 * 1024;
    public static long gb = 1024 * 1024 * 1024;
    static DecimalFormat format = new DecimalFormat("#0.00");

    public static Bitmap getImageBitmap(Bitmap bmp) {
        return ThumbnailUtils.extractThumbnail(bmp,
                (int) (Constant.width * 0.25), (int) (Constant.width * 0.25));
    }

    public static String getTimeFormat(int time) {
        String time1 = new BigDecimal(time * 0.001).setScale(0,
                BigDecimal.ROUND_HALF_UP) + "";
        time = Integer.parseInt(time1);// 这个time单位是秒
        String hhS = "00";
        String fenS = "00";
        String miaoS = "00";
        int hh = time / 3600;
        int fen = (time - hh * 3600) / 60;
        int miao = time % 60;
        if (hh < 10) {
            hhS = "0" + hh;
        } else {
            hhS = "" + hh;
        }
        if (fen < 10) {
            fenS = "0" + fen;
        } else {
            fenS = "" + fen;
        }
        if (miao < 10) {
            miaoS = "0" + miao;
        } else {
            miaoS = "" + miao;
        }
        Log.e("时间:", fenS + ":" + miaoS);
        Log.e("时间2:", hhS + ":" + fenS + ":" + miaoS);
        if (hh == 0) {
            return fenS + ":" + miaoS;
        } else {
            return hhS + ":" + fenS + ":" + miaoS;
        }

    }

    public static String getTimeFormatByS(int time) {
//        String time1 = new BigDecimal(time * 0.001).setScale(0,
//                BigDecimal.ROUND_HALF_UP) + "";
//        time = Integer.parseInt(time1);// 这个time单位是秒
        String hhS = "00";
        String fenS = "00";
        String miaoS = "00";
        int hh = time / 3600;
        int fen = (time - hh * 3600) / 60;
        int miao = time % 60;
        if (hh < 10) {
            hhS = "0" + hh;
        } else {
            hhS = "" + hh;
        }
        if (fen < 10) {
            fenS = "0" + fen;
        } else {
            fenS = "" + fen;
        }
        if (miao < 10) {
            miaoS = "0" + miao;
        } else {
            miaoS = "" + miao;
        }
        Log.e("时间:", fenS + ":" + miaoS);
        Log.e("时间2:", hhS + ":" + fenS + ":" + miaoS);
        if (hh == 0) {
            return fenS + ":" + miaoS;
        } else {
            return hhS + ":" + fenS + ":" + miaoS;
        }

    }

    public static File saveBitmapFile(Bitmap bitmap) {
        //bitmap = ExUtils.comp(bitmap);//先按比例压缩，再按质量压缩,但是压缩会失真,但是不压缩，又会加载很慢，还是按大小压缩下吧
        String name = System.currentTimeMillis() + "";
        File fileFolder = new File(Constant.picturePath + "yijiadownload");
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        File file = new File(Constant.picturePath + "yijiadownload/" + name + ".png");// 将要保存图片的路径
//        Log.i(TAG,"图片保存地址："+Constant.picturePath + File.separator + name+ ".png");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static int px2dp(float pxValue, Context context) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static String dateToString(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String str = sdf.format(date);
        return str;
    }

    public static String getModifiedFormat(long time) {
        Date date = new Date(time);
        return dateToString(date, "yyyy-MM-dd HH:mm");
    }

    //    String result = format.format(percent);
    public static String getSizeformat(long fileSize) {
        String size = fileSize + "B";
        if (fileSize < 1024) {
            size = fileSize + "B";
        } else if (fileSize < mb) {
            //大于1B小于1Mb
            size = format.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < gb) {
            //大于1Mb小于1G
            size = format.format((double) fileSize / mb) + "M";
        } else {
            //大于1G
            size = format.format((double) fileSize / gb) + "G";
        }
        return size;
    }


    public static int getPicByFileType(String fileName) {
        //获取后缀名并转为小写
        String type = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        int typeImage = R.mipmap.unknown;
        switch (type) {
            case "jpg":
            case "jpeg":
            case "bmp":
            case "png":
            case "gif":
                typeImage = R.mipmap.pic;
                break;
            case "txt":
            case "html":
            case "xml":
            case "log":
            case "conf":
                typeImage = R.mipmap.txt;
                break;
            case "apk":
                typeImage = R.mipmap.apk;
                break;
            case "mp4":
            case "3gp":
            case "avi":
            case "rmvb":
            case "wmv":
            case "rm":
            case "asf":
            case "mov":
                typeImage = R.mipmap.sp;
                break;
            case "mp3":
            case "wav":
            case "wma":
            case "ogg":
            case "ape":
            case "acc":
                typeImage = R.mipmap.music;
                break;
            case "rar":
            case "zip":
                typeImage = R.mipmap.zip;
                break;
            case "doc":
            case "docx":
                typeImage = R.mipmap.doc;
                break;
            default:
                typeImage = R.mipmap.unknown;
                break;

        }
        return typeImage;
    }

    public static Bitmap getBitmapFromUrl(String url, double width, double height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = BitmapFactory.decodeFile(url);
        // 防止OOM发生
        options.inJustDecodeBounds = false;
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = 1;
        float scaleHeight = 1;
        // 按照固定宽高进行缩放
        if (mWidth <= mHeight) {
            scaleWidth = (float) (width / mWidth);
            scaleHeight = (float) (height / mHeight);
        } else {
            scaleWidth = (float) (height / mWidth);
            scaleHeight = (float) (width / mHeight);
        }
        // 按照固定大小对图片进行缩放
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight, matrix, true);
        // 用完了记得回收
        bitmap.recycle();
        return newBitmap;
    }


    //质量压缩
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    //图片按比例大小压缩方法（根据路径获取图片并压缩）
    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }


    //图片按比例大小压缩方法（根据Bitmap图片压缩）
    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }


    public static File BitmapFilesave(Bitmap bitmap, String photoPath) {

        File file1 = new File(photoPath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file1));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file1;
    }


}
