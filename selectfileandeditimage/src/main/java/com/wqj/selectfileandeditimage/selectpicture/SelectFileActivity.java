package com.wqj.selectfileandeditimage.selectpicture;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.wqj.selectfileandeditimage.R;
import com.wqj.selectfileandeditimage.selectpicture.adapter.LvFileListAdapter;
import com.wqj.selectfileandeditimage.selectpicture.entity.FileEx;
import com.wqj.selectfileandeditimage.selectpicture.utils.PermissionUtils;
import com.wqj.selectfileandeditimage.selectpicture.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectFileActivity extends AppCompatActivity {

    private final String TAG = "SelectFileActivity";

    private String basePath = Environment.getExternalStorageDirectory().getPath();

    private RelativeLayout rlBack;
    private Button btnSelected;
    private Toolbar toolbar;
    private ListView lvFileList;
    private RelativeLayout rl_noFile;
    private TextView tv_noFile;

    private File file;
    private List<File> listSelectedFile;//已经选择的文件
    private List<FileEx> list;//当前显示的文件列表集合
    private LvFileListAdapter adapter;
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    //有权限了才会走这句
//                    Toast.makeText(SelectFileActivity.this, "有权限了", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.rl_back) {
                onBackPressed();
            } else if (id == R.id.btn_selected) {
                Intent intent = new Intent();
                intent.putExtra("fileList", (Serializable) listSelectedFile);
                setResult(RESULT_OK, intent);
                SelectFileActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        initUI();
        listSelectedFile = new ArrayList<File>();
//
        lvFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileEx fileEx = list.get(position);
                File fileTemp = fileEx.getFile();
                if (fileTemp.isDirectory()) {
                    refresh(fileTemp);
                } else {
                    if (fileEx.getIsSelected() != FileEx.SELECTED) {
                        list.get(position).setIsSelected(FileEx.SELECTED);
                        listSelectedFile.add(fileTemp);

                    } else {
                        list.get(position).setIsSelected(FileEx.NOTSELECTED);
                        listSelectedFile.remove(fileTemp);
                    }
                    btnSelected.setText("(" + listSelectedFile.size() + ")确定");
                    adapter.notifyDataSetChanged();
                }
            }
        });
        Log.i(TAG, Utils.px2dp(44, this) + "");
        if (Build.VERSION.SDK_INT < 23) {
            //Toast.makeText(this, "请在设置或安全中心开启文件读写权限", Toast.LENGTH_SHORT).show();
            // return;
        } else {
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, mPermissionGrant);
        }
        refresh(new File(basePath));

    }

    private void refresh(File file) {
        this.file = file;
        list = getNextFile(file);
        adapter = new LvFileListAdapter(list, this);
        lvFileList.setAdapter(adapter);
    }

    /**
     * 传入文件夹，返回改文件夹下的所有文件
     *
     * @param file
     * @return
     */
    private List<FileEx> getNextFile(File file) {
//        Log.i(TAG, "当前目录：" + file.getPath());
        // 判断sdcard是否存在,并且可以读写
        List<FileEx> list = new ArrayList<FileEx>();
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (file.isDirectory()) {//如果是文件夹
                File[] files = file.listFiles();
                if (files.length > 0) {
                    rl_noFile.setVisibility(View.GONE);
                    List<File> listFiles = fileSort(files);//排序
                    for (File f : listFiles) {
//                Log.i(TAG, "下级文件：" + f.getName());
                        FileEx fileEx = new FileEx(f);
                        fileEx.setLastModified(Utils.getModifiedFormat(f.lastModified()));
                        if (f.isDirectory()) {
                            fileEx.setDrawableId(R.mipmap.wjj);
                            fileEx.setChildSize(f.listFiles().length);
                        } else {
                            fileEx.setDrawableId(Utils.getPicByFileType(f.getName()));
                            fileEx.setFileSize(Utils.getSizeformat(f.length()));

                            for (File isSelectedFile : listSelectedFile) {
                                //判断这个文件是否已经被选中了
                                if (isSelectedFile.equals(f)) {
                                    fileEx.setIsSelected(FileEx.SELECTED);
                                    break;
                                }
                            }
                        }

                        list.add(fileEx);
                    }
                } else {
                    rl_noFile.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Toast.makeText(this, "SD卡不存在，或不可读写", Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    //给文件排序,卧槽，效率问题后面再说
    private List<File> fileSort(File[] files) {
        long start = System.currentTimeMillis();
        List<File> list1 = new ArrayList<File>();//放文件夹
        List<File> list2 = new ArrayList<File>();//放文件
        //分成两组再排序
        for (File file : files) {
            if (file.isDirectory()) {
                list1.add(file);
            } else {
                list2.add(file);
            }
        }
        //将集合转为数组，方便比较
        File[] files1 = list1.toArray(new File[]{});
        File[] files2 = list2.toArray(new File[]{});

        files1 = fileSortByName(files1);
        files2 = fileSortByName(files2);
        list1.clear();//利用已经创建好的集合，不用再创建新的集合了
        for (File file : files1) {
            list1.add(file);
        }
        for (File file : files2) {
            list1.add(file);
        }
        long end = System.currentTimeMillis();
        Log.i(TAG, "排序时间：" + (end - start));
        return list1;
    }

    private File[] fileSortByName(File[] files) {
        //排序规则：点、特殊符号、数字、字母
        int length = files.length;
        for (int i = 0; i < length; i++) {
            for (int k = 1; k < length - i; k++) {
                //将名字全部转化为小写再比较
                char[] name1s = files[k - 1].getName().toLowerCase().toCharArray();
                char[] name2s = files[k].getName().toLowerCase().toCharArray();
                if (compareNameChar(name1s, name2s)) {
                    File temp = files[k - 1];
                    files[k - 1] = files[k];
                    files[k] = temp;
                }
            }
        }
        return files;
    }

    //比较名字：如果排序后1还是在2前面就返回false,如果交换了位置就返回true;
    private boolean compareNameChar(char[] name1s, char[] name2s) {
        int length1 = name1s.length;
        int length2 = name2s.length;
        if (length1 > length2) {
            length1 = length2;
        }
        //遍历为最小的那个数组
        for (int i = 0; i < length1; i++) {
            if (name1s[i] != name2s[i]) {
                if (name1s[i] > name2s[i]) {//直接按照ASCII码比较
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (file.getPath().equals(basePath)) {
                onBackPressed();
            } else {
                refresh(file.getParentFile());
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }


    public <T extends View> T $(@IdRes int resId) {
        return (T) super.findViewById(resId);
    }

    private void initUI() {
        rlBack = $(R.id.rl_back);
        btnSelected = $(R.id.btn_selected);
        toolbar = $(R.id.toolbar);
        lvFileList = $(R.id.lv_fileList);
        tv_noFile = $(R.id.tv_noFile);
        rl_noFile = $(R.id.rl_noFile);

        rlBack.setOnClickListener(onClickListener);
        btnSelected.setOnClickListener(onClickListener);
    }


}
