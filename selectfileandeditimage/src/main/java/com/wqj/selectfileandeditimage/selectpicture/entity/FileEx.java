package com.wqj.selectfileandeditimage.selectpicture.entity;

import java.io.File;

/**
 * Created by snsoft on 2016/12/12.
 */

public class FileEx {

    public static final int SELECTED = 201;
    public static final int NOTSELECTED = 202;
    public static final int SECTION = 203;

    private File file;
    private int drawableId;//对应的图标id
    private int isSelected;//是否选中
    private int childSize;
    private String fileSize;//文件大小，是格式化后的字符串
    private String lastModified;//最后修改日期 格式化后的

    public FileEx(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int getChildSize() {
        return childSize;
    }

    public void setChildSize(int childSize) {
        this.childSize = childSize;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
