package com.wqj.selectfileandeditimage.selectpicture.utils;

import android.os.Environment;

import java.io.File;
import java.util.List;

/**
 * Created by snsoft on 2016/9/23.
 */

public class Constant {
    public static int width;
    public static int height;
    public static String newPath = "";
    public static List<File> list;
    public static List<File> thumbnailList;
//
//    public static String picturePath = Environment.getExternalStorageDirectory()+ File.separator + "SelectFileTemp" + File.separator + "picture";
//    public static String voicePath = Environment.getExternalStorageDirectory()+ File.separator + "SelectFileTemp" + File.separator + "voice";


    public static String picturePath = Environment.getExternalStorageDirectory() + File.separator;
    public static String voicePath = Environment.getExternalStorageDirectory() + File.separator;

}
