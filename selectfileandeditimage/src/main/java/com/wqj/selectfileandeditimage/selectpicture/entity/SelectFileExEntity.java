package com.wqj.selectfileandeditimage.selectpicture.entity;

import java.io.File;

/**
 * Created by snsoft on 2016/9/26.
 */

public class SelectFileExEntity {
    private int fileType;//1是图片，2是视频
    private boolean isSelected = false;
    private File file;
    private File thumbnailFile;//还是用file吧，那个Glide不能直接加载bitmap

    public SelectFileExEntity(int fileType, File file, File thumbnailFile) {
        this.fileType = fileType;
        this.file = file;
        this.thumbnailFile = thumbnailFile;
    }

    public SelectFileExEntity(File file) {
        this.file = file;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getThumbnailFile() {
        return thumbnailFile;
    }

    public void setThumbnailFile(File thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }
}
