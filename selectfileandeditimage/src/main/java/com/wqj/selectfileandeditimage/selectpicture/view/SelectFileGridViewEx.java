package com.wqj.selectfileandeditimage.selectpicture.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wqj.selectfileandeditimage.R;
import com.wqj.selectfileandeditimage.selectpicture.AudioRecorderActivity;
import com.wqj.selectfileandeditimage.selectpicture.SelectFileActivity;
import com.wqj.selectfileandeditimage.selectpicture.SelectFileExActivity;
import com.wqj.selectfileandeditimage.selectpicture.ShowPictureActivity;
import com.wqj.selectfileandeditimage.selectpicture.adapter.GvPictureMinAdapter;
import com.wqj.selectfileandeditimage.selectpicture.utils.Constant;
import com.wqj.selectfileandeditimage.selectpicture.utils.FileUtils;
import com.wqj.selectfileandeditimage.selectpicture.utils.PermissionUtils;
import com.wqj.selectfileandeditimage.selectpicture.utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbb on 2016/9/28.
 */

public class SelectFileGridViewEx extends GridView {

    private final String TAG = "SelectFileGridViewEx";

    private final int REQUEST_CODE_PIC = 100;//选择图片
    private final int REQUEST_CODE_CAMERA = 101;//拍照
    private final int REQUEST_CODE_VIDEO = 102;//选择视频
    private final int REQUEST_CODE_RECORDER = 103;//录像
    private final int REQUEST_CODE_FILE = 104;//选择文件
    private final int REQUEST_CODE_VOICE = 105;// 是调用录音机
    private final int REQUEST_CODE_NOEDITPICTURE = 106;// 不编辑图片返回
    private final int REQUEST_CODE_EDITEDPICTURE = 107;// 编辑完成图片返回
    ArrayList<File> listFiles;
    List<Bitmap> listBitmap;
    private String[] itemNames2 = {"文件", "图片", "拍照", "视频", "录像", "录音"};
    private int[] rs2 = new int[]{R.mipmap.wenjiantubiao, R.mipmap.tupiantubiao, R.mipmap.paizhaotubiao, R.mipmap.shipintubiao, R.mipmap.luxiangtubiao, R.mipmap.luyinex};
    private int[] requestCodes2 = {REQUEST_CODE_FILE, REQUEST_CODE_PIC, REQUEST_CODE_CAMERA, REQUEST_CODE_VIDEO, REQUEST_CODE_RECORDER, REQUEST_CODE_VOICE};
    private List<String> itemNames;
    private List<Integer> resourcesIds;
    private List<Integer> requestCodes;
    private DialogMenuEx dialogMenuEx;
    private OnClickListener itemOnClick;

    private Activity activity;
    private Context context;
    private int maxFileSize = -1;//选择文件的数量限制，-1代表没有限制
    private boolean isAdd = false;
    private int imageWidth, imageHeight;
    private String photoPath;//照片保存路径
    private Uri photoURI = null;
    private String outPath = "";
    private Bitmap mainBitmap;
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA://拍照权限
                    break;
                case PermissionUtils.CODE_RECORD_AUDIO://录音
                    break;
            }
        }
    };

    public SelectFileGridViewEx(Context context) {
        super(context);
        this.context = context;
        initData();
    }

    public SelectFileGridViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initData();
    }

    public SelectFileGridViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initData();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private void initData() {
        setNumColumns(4);
        setStretchMode(STRETCH_COLUMN_WIDTH);
        setHorizontalSpacing(1);
        setVerticalSpacing(1);
        itemNames = new ArrayList<String>();
        resourcesIds = new ArrayList<Integer>();
        requestCodes = new ArrayList<Integer>();
        DisplayMetrics metric = new DisplayMetrics();
        imageWidth = metric.widthPixels;
        imageHeight = metric.heightPixels;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        Constant.width = metric.widthPixels; // 屏幕宽度（像素）
        Constant.height = metric.heightPixels; // 屏幕高度（像素）
        listFiles = new ArrayList<File>();
        listBitmap = new ArrayList<Bitmap>();
        refreshGV();
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (maxFileSize == -1 || listFiles.size() < maxFileSize) {
                    //如果满足上面的条件，才能继续添加文件
                    if (i == listFiles.size()) {
                        // 说明点击的是最后一个，就是添加的按钮
                        if (itemNames.size() == 1) {
                            //如果只有一个，直接弹出功能
                            selectFunction(requestCodes.get(0));
                        } else {
                            dialogMenuEx = new DialogMenuEx(activity, itemOnClick, itemNames, resourcesIds, requestCodes, 4, false);
                            dialogMenuEx.show();
                        }
                    }
                }
            }
        });
//
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (i < listFiles.size()) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(
                            activity);
                    adb.setTitle(R.string.delete);
                    adb.setMessage("确认删除？");
                    adb.setPositiveButton(R.string.determine,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    // TODO Auto-generated method stub
                                    if (maxFileSize == -1 || listFiles.size() < maxFileSize) {
                                        //说明有那个添加的按钮
                                        listFiles.remove(i);
                                        listBitmap.remove(i);//从显示的图片中移除
                                        listBitmap.remove(listBitmap.size() - 1);//从显示的图片中移除加号
                                        refreshGV();
                                    } else {
                                        listFiles.remove(i);
                                        listBitmap.remove(i);//从显示的图片中移除
                                        refreshGV();
                                    }

                                }
                            });
                    adb.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.cancel();
                                }
                            });
                    adb.show();

                }
                return false;
            }
        });
        itemOnClick = new OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                int tag = (Integer) view.getTag();
                selectFunction(tag);
                dialogMenuEx.dismiss();
            }
        };
    }

    private void selectFunction(int tag) {
        switch (tag) {
            case REQUEST_CODE_FILE:
                //文件
                Intent intentFile = new Intent(activity, SelectFileActivity.class);
                activity.startActivityForResult(intentFile, REQUEST_CODE_FILE);
                break;
            case REQUEST_CODE_PIC:
                //选择图片
                Intent intentImage = new Intent(activity, SelectFileExActivity.class);
                intentImage.putExtra("fileType", 0);
                int maxImage = maxFileSize;
                if (maxFileSize != -1) {
                    maxImage = maxFileSize - listFiles.size();
                }
                intentImage.putExtra("maxFileSize", maxImage);
                activity.startActivityForResult(intentImage, REQUEST_CODE_PIC);
                break;
            case REQUEST_CODE_CAMERA:
                // 拍照
                PermissionUtils.requestPermission(activity, PermissionUtils.CODE_CAMERA, mPermissionGrant);
                doTakePhoto();
                break;
            case REQUEST_CODE_VIDEO:
                //视频文件
                Intent intentVideo = new Intent(activity, SelectFileExActivity.class);
                intentVideo.putExtra("fileType", 1);
                int maxVideo = maxFileSize;
                if (maxFileSize != -1) {
                    maxVideo = maxFileSize - listFiles.size();
                }
                intentVideo.putExtra("maxFileSize", maxVideo);
                activity.startActivityForResult(intentVideo, REQUEST_CODE_VIDEO);
                break;
            case REQUEST_CODE_RECORDER:
                //录像
                if (Build.VERSION.SDK_INT < 23) {
//                    Toast.makeText(activity, "请在设置或安全中心开启摄像头访问权限", Toast.LENGTH_SHORT).show();
//                    return;
                } else {
                    PermissionUtils.requestPermission(activity, PermissionUtils.CODE_CAMERA, mPermissionGrant);
                }
                Intent intentRecorder = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                activity.startActivityForResult(intentRecorder, REQUEST_CODE_RECORDER);
                break;
            case REQUEST_CODE_VOICE:
                //录音
                PermissionUtils.requestPermission(activity, PermissionUtils.CODE_RECORD_AUDIO, mPermissionGrant);
                Intent intentVoice = new Intent(context, AudioRecorderActivity.class);
                activity.startActivityForResult(intentVoice, REQUEST_CODE_VOICE);
                break;
            default:
                break;
        }
    }

    private void refreshGV() {
// 用户选择的图片加入之前先移除最后一个（那个添加的按钮），添加图片后，再加入
        // 将添加的按钮放到GridView中
        if (maxFileSize == -1 || listFiles.size() < maxFileSize) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.shangchuan);
            listBitmap.add(Utils.getImageBitmap(bmp));
        }
        GvPictureMinAdapter gap = new GvPictureMinAdapter(context, listBitmap);
        setAdapter(gap);
    }

    public ArrayList<File> getFileList() {
        return listFiles;
    }

    public void init(Activity activity) {
        this.activity = activity;
        for (String str : itemNames2) {
            itemNames.add(str);
        }
        for (Integer id : rs2) {
            resourcesIds.add(id);
        }
        for (Integer code : requestCodes2) {
            requestCodes.add(code);
        }
    }

    public void init(Activity activity, int maxFileSize) {
        this.activity = activity;
        this.maxFileSize = maxFileSize;
        for (String str : itemNames2) {
            itemNames.add(str);
        }
        for (Integer id : rs2) {
            resourcesIds.add(id);
        }
        for (Integer code : requestCodes2) {
            requestCodes.add(code);
        }
    }

    public void setMenu(boolean file, boolean picture, boolean photo, boolean video, boolean videoRecord, boolean voiceRecord) {
        itemNames.clear();
        resourcesIds.clear();
        requestCodes.clear();
        if (file) {
            itemNames.add("文件");
            resourcesIds.add(R.mipmap.wenjiantubiao);
            requestCodes.add(REQUEST_CODE_FILE);
        }
        if (picture) {
            itemNames.add("图片");
            resourcesIds.add(R.mipmap.tupiantubiao);
            requestCodes.add(REQUEST_CODE_PIC);
        }
        if (photo) {
            itemNames.add("拍照");
            resourcesIds.add(R.mipmap.paizhaotubiao);
            requestCodes.add(REQUEST_CODE_CAMERA);
        }
        if (video) {
            itemNames.add("视频");
            resourcesIds.add(R.mipmap.shipintubiao);
            requestCodes.add(REQUEST_CODE_VIDEO);
        }
        if (videoRecord) {
            itemNames.add("录像");
            resourcesIds.add(R.mipmap.luxiangtubiao);
            requestCodes.add(REQUEST_CODE_RECORDER);
        }
        if (voiceRecord) {
            itemNames.add("录音");
            resourcesIds.add(R.mipmap.luyinex);
            requestCodes.add(REQUEST_CODE_VOICE);
        }

    }

    //这样可以的，但是一旦开始设置的文件过多，就会导致内存溢出。后续修改
    public void setImages(ArrayList<File> list) {
        if (list != null && list.size() > 0) {
            listFiles = list;
            int picW = (int) (Constant.width * 0.25);
            listBitmap.remove(listBitmap.size() - 1);
            for (File file : list) {
                Glide.with(context)
                        .load(file)
                        .asBitmap()
                        .override(picW, picW)
                        .centerCrop()//CenterCrop：等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                //先移除加号，然后将新的图片加入到集合，如果已经全部加载了，就刷新GridView
                                //这个图片处理应该使用Glide
                                listBitmap.add(resource);
                                if (listBitmap.size() == listFiles.size()) {
                                    refreshGV();
                                }

                            }
                        });
            }
        }
    }

    public void setVoice(ArrayList<File> list) {
        if (list != null && list.size() > 0) {
            listFiles = list;
            Bitmap bitmap = Utils.getImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.yuyin));
            int size = list.size();
            listBitmap.remove(listBitmap.size() - 1);
            for (int i = 0; i < size; i++) {
                listBitmap.add(bitmap);
            }

            refreshGV();
        }
    }

    public void setVideo(ArrayList<File> list) {
        if (list != null && list.size() > 0) {
            listFiles = list;
            Bitmap bitmap = Utils.getImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.shipin));
            int size = list.size();
            listBitmap.remove(listBitmap.size() - 1);
            for (int i = 0; i < size; i++) {
                listBitmap.add(bitmap);
            }
            refreshGV();
        }
    }

    public void setFile(ArrayList<File> list) {
        if (list != null && list.size() > 0) {
            listFiles = list;
            Bitmap bitmap = Utils.getImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.wenjian));
            int size = list.size();
            listBitmap.remove(listBitmap.size() - 1);
            for (int i = 0; i < size; i++) {
                listBitmap.add(bitmap);
            }
            refreshGV();
        }
    }

    public void setIntent(int requestCode, int resultCode, final Intent intent) {
//        Log.w("看返回的结果码", String.valueOf(resultCode));
        if (resultCode == activity.RESULT_OK) {
//            下面这句不能写在这里，必须写在里面，否则，如果从别的页面返回到调用了SelectFileGridViewEx的页面，会导致那个加号被移除
//            listBitmap.remove(listBitmap.size() - 1);
//            Log.w("看返回的请求码", String.valueOf(requestCode));
            switch (requestCode) {
                case REQUEST_CODE_PIC:
                    //选择图片
                    listBitmap.remove(listBitmap.size() - 1);
                    List<File> list = (List<File>) intent.getSerializableExtra("list");
                    Log.i(TAG, "总共选中了" + list.size() + "个文件");
                    for (File file : list) {
                        Log.i(TAG, "文件名：" + file.getName());
                        listFiles.add(file);
                        listBitmap.add(Utils.getImageBitmap(BitmapFactory.decodeFile(file.getPath())));
                    }
                    refreshGV();
                    break;
                case REQUEST_CODE_CAMERA:
                    //拍照后展示图片询问是否编辑
                    listBitmap.remove(listBitmap.size() - 1);
                    if (photoURI != null) {//拍摄成功
                        photoPath = photoURI.getPath();
                        Intent intent1 = new Intent(activity, ShowPictureActivity.class);
                        intent1.putExtra("path", photoPath);
                        activity.startActivityForResult(intent1, REQUEST_CODE_EDITEDPICTURE);
                    }
                    break;
                case REQUEST_CODE_EDITEDPICTURE:
//                    listBitmap.remove(listBitmap.size() - 1);
//                    Log.w("setIntent: ", "为什么不走啊");
                    setImage();
                    break;
                case REQUEST_CODE_VIDEO:
                    //选择视频
                    listBitmap.remove(listBitmap.size() - 1);
                    List<File> listVideo = (List<File>) intent.getSerializableExtra("list");
                    List<File> thumbnailList = (List<File>) intent.getSerializableExtra("thumbnailList");
                    for (File file : listVideo) {
                        //添加所有的视频文件到文件集合
                        listFiles.add(file);
                    }
                    Log.i(TAG, "总共选中了" + listVideo.size() + "个文件");
                    for (File file : thumbnailList) {
                        //添加所有的视频缩略图到缩略图集合
                        if (file == null) {
                            listBitmap.add(Utils.getImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.shipinsuoluetu)));
                        } else {
                            listBitmap.add(Utils.getImageBitmap(BitmapFactory.decodeFile(file.getPath())));
                        }
                    }
                    refreshGV();
                    break;
                case REQUEST_CODE_RECORDER:
                    //摄像
                    listBitmap.remove(listBitmap.size() - 1);
                    Uri uriRecorder = intent.getData();
                    String pathRecorder = null;
                    Cursor cursor = activity.getContentResolver().query(uriRecorder, null, null,
                            null, null);
                    cursor.moveToFirst();
                    pathRecorder = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    Log.i(TAG, "拍摄视频uri：" + uriRecorder.toString());
                    Log.i(TAG, "拍摄视频文件路径：" + pathRecorder);
                    listFiles.add(new File(pathRecorder));
                    Bitmap bmp2 = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.luxiang);
                    listBitmap.add(Utils.getImageBitmap(bmp2));
                    refreshGV();
                    break;
                case REQUEST_CODE_FILE:
                    //文件
                    listBitmap.remove(listBitmap.size() - 1);
                    List<File> fileList = (List<File>) intent.getSerializableExtra("fileList");
                    for (File file : fileList) {
                        listFiles.add(file);
                        Bitmap bmp3 = BitmapFactory.decodeResource(getResources(),
                                R.mipmap.wenjian);
                        listBitmap.add(Utils.getImageBitmap(bmp3));
                    }
                    refreshGV();
                    break;
                case REQUEST_CODE_VOICE:
                    listBitmap.remove(listBitmap.size() - 1);
                    listFiles.add((File) intent.getSerializableExtra("voice"));
                    Bitmap bmpVoice = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.yuyin);
                    listBitmap.add(Utils.getImageBitmap(bmpVoice));
                    refreshGV();
                    break;
            }

        } else {
//            initData();
        }
    }

    /**
     * 拍摄照片
     */
    private void doTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = FileUtils.genEditFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
            }
        }
    }

    public void setImage() {
        Log.w("setImage: ", Constant.newPath);
        Bitmap bm = BitmapFactory.decodeFile(Constant.newPath);
        Bitmap bm1 = Utils.getimage(Constant.newPath);
        listFiles.add(Utils.BitmapFilesave(bm1, Constant.newPath));
        listBitmap.add(Utils.getImageBitmap(bm1));
        bm1.recycle();
        refreshGV();
    }

}
