package com.wqj.selectfileandeditimage.selectpicture;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wqj.selectfileandeditimage.R;
import com.wqj.selectfileandeditimage.editimage.EditImageActivity;
import com.wqj.selectfileandeditimage.editimage.utils.BitmapUtils;
import com.wqj.selectfileandeditimage.selectpicture.utils.Constant;
import com.wqj.selectfileandeditimage.selectpicture.utils.FileUtils;
import com.wqj.selectfileandeditimage.selectpicture.utils.PhotoBitmapUtils;
import java.io.File;

import static com.wqj.selectfileandeditimage.selectpicture.SelectFileExActivity.ACTION_REQUEST_EDITIMAGE;

/**
 * 图片查看
 */

public class ShowPictureActivity extends Activity {

    private final String TAG = "ShowPictureActivity";
    String filePath;
    private ImageView iv_showPicture;
    private TextView tvEdit, tvDone;
    private String outPath;
    private Bitmap mainBitmap;
    private int imageWidth, imageHeight;
    private LruCache<String, Bitmap> mMemoryCache;
    private Boolean isEdit = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_show_picture);
        init();
        Log.w("onCreate: ", Build.BRAND);
        if (Build.BRAND.equals("samsung")) {
            filePath = PhotoBitmapUtils.amendRotatePhoto(getIntent().getStringExtra("path"), this);
        } else {
            filePath = getIntent().getStringExtra("path");
        }
//        iv_showPicture.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                onBackPressed();
//            }
//        });
        tvEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ShowPictureActivity.this, "点击了编辑按钮", Toast.LENGTH_SHORT).show();
                editImageClick();
            }
        });
        //
        tvDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                } else {
                    Constant.newPath = filePath;
                }
                Intent intent = new Intent();
                intent.putExtra("result", filePath);
                ShowPictureActivity.this.setResult(RESULT_OK, intent);
                ShowPictureActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (filePath != null) {
//            Log.w("onStart: ", "看这里");
            iv_showPicture.setImageBitmap(BitmapFactory.decodeFile(filePath));
        }
    }

    private void init() {
        // TODO Auto-generated method stub
        iv_showPicture = (ImageView) findViewById(R.id.iv_showPicture);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        tvDone = (TextView) findViewById(R.id.tv_done);
        DisplayMetrics metric = getResources().getDisplayMetrics();
        imageWidth = metric.widthPixels;
        imageHeight = metric.heightPixels;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_EDITIMAGE://
                    handleEditorImage(data);
                    break;
            }
        }
    }

    private void handleEditorImage(Intent data) {
        isEdit = true;
        String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

        if (isImageEdit) {//图片被编辑
            Toast.makeText(this, "图片编辑完成，原图片不受影响", Toast.LENGTH_LONG).show();
        } else {//未编辑  还是用原来的图片
            newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
        }
//        Log.d("image is edit", isImageEdit + "");
        startLoadTask(newFilePath);
        Constant.newPath = "";
        Constant.newPath = newFilePath;
    }

    private void editImageClick() {
        File outputFile = FileUtils.genEditFile();
        EditImageActivity.start(this, filePath, outputFile.getAbsolutePath(), ACTION_REQUEST_EDITIMAGE);
    }

    private void startLoadTask(String path) {
        LoadImageTask task = new LoadImageTask();
        task.execute(path);
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.getSampledBitmap(params[0], imageWidth / 4, imageHeight / 4);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
//            addBitmapToMemoryCache("newPic", mainBitmap)
            iv_showPicture.setImageBitmap(mainBitmap);
        }
    }
}
