package com.wqj.selectfileandeditimage.selectpicture.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wqj.selectfileandeditimage.R;
import com.wqj.selectfileandeditimage.selectpicture.entity.FileEx;

import java.util.List;

/**
 * Created by snsoft on 2016/12/12.
 */

public class LvFileListAdapter extends BaseAdapter {

    private List<FileEx> list;
    private LayoutInflater lif;
    private Context context;

    public LvFileListAdapter(List<FileEx> list, Context context) {
        this.list = list;
        this.lif = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = lif.inflate(R.layout.item_lv_file, null);
            vh.iv_filePic = (ImageView) convertView.findViewById(R.id.iv_filePic);
            vh.iv_isSelected = (ImageView) convertView.findViewById(R.id.iv_isSelected);
            vh.tv_fileName = (TextView) convertView.findViewById(R.id.tv_fileName);
            vh.tv_lastModified = (TextView) convertView.findViewById(R.id.tv_lastModified);
            vh.tv_countOrSize = (TextView) convertView.findViewById(R.id.tv_countOrSize);
            vh.rl_selected = (RelativeLayout) convertView.findViewById(R.id.rl_selected);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        FileEx fileEx = list.get(position);
        vh.tv_fileName.setText(fileEx.getFile().getName());
        vh.tv_lastModified.setText(fileEx.getLastModified());
        if (fileEx.getFile().isDirectory()) {
            vh.tv_countOrSize.setText(fileEx.getChildSize() + "项");
        } else {
            vh.tv_countOrSize.setText(fileEx.getFileSize());
        }
        Glide.with(context)
                .load(fileEx.getDrawableId())
                .into(vh.iv_filePic);
        int ids = R.mipmap.by;
        switch (fileEx.getIsSelected()) {
            case FileEx.SELECTED://选中
                ids = R.mipmap.xuanzhong;
                break;
            case FileEx.SECTION://部分选中
                ids = R.mipmap.qy;
                break;
            default:
                ids = R.mipmap.by;
                break;
        }
        Glide.with(context)
                .load(ids)
                .into(vh.iv_isSelected);

        if (fileEx.getFile().isDirectory()) {
            vh.iv_isSelected.setVisibility(View.GONE);
        } else {
            vh.iv_isSelected.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_filePic, iv_isSelected;
        //文件名称，文件最后修改时间，文件夹下一级文件个数或文件的文件大小
        private TextView tv_fileName, tv_lastModified, tv_countOrSize;
        private RelativeLayout rl_selected;
    }
}
