package com.wqj.selectfileandeditimage.selectpicture.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wqj.selectfileandeditimage.R;
import com.wqj.selectfileandeditimage.selectpicture.entity.SelectFileExEntity;
import com.wqj.selectfileandeditimage.selectpicture.utils.Constant;

import java.util.List;

/**
 * Created by snsoft on 2016/9/23.
 */

public class GvPictureAdapter extends BaseAdapter {

    private final String TAG = "GvPictureAdapter";

    private List<SelectFileExEntity> list;
    private LayoutInflater lif;
    private Context context;
    private Button btn_selected;
    private int picW, picH;
    private int maxFileSize = -1;

    public GvPictureAdapter(Context context, List<SelectFileExEntity> list, Button btn_selected, int maxFileSize) {
        super();

        this.lif = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.btn_selected = btn_selected;
        this.maxFileSize = maxFileSize;
        picW = Constant.width / 3;
        picH = picW;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        ViewHolder vh = null;
        if (vh == null) {
            vh = new ViewHolder();
            view = lif.inflate(R.layout.item_gv_picture, null);
            vh.iv_image = (ImageView) view.findViewById(R.id.iv_item_gv_pic);
            vh.acb_image = (AppCompatCheckBox) view.findViewById(R.id.acb_pic);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        if (list.get(position).getThumbnailFile() == null) {
            Glide.with(context)
                    .load(R.mipmap.shipinsuoluetu)
                    .override(picW, picH)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(vh.iv_image);
        } else {
            Glide.with(context)
                    .load(list.get(position).getThumbnailFile())
                    .override(picW, picH)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(vh.iv_image);
        }
        if (list.get(position).isSelected()) {
            vh.acb_image.setChecked(true);
        }
        vh.acb_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                //选中
                Log.i(TAG, "maxFileSize=" + maxFileSize + ",Constant.list.size()=" + Constant.list.size());

                if (checked) {
                    if (maxFileSize == -1 || Constant.list.size() < maxFileSize) {
                        Constant.list.add(list.get(position).getFile());
                        Constant.thumbnailList.add(list.get(position).getThumbnailFile());
                    } else {
                        compoundButton.setChecked(false);
                        Log.i(TAG, "超出数量限制，不可选中");
                    }
                } else {
                    Log.i(TAG, "取消选中");
                    Constant.list.remove(list.get(position).getFile());
                    Constant.thumbnailList.remove(list.get(position).getThumbnailFile());
                }
                list.get(position).setSelected(checked);
                if (maxFileSize == -1) {
                    btn_selected.setText("确定(" + Constant.list.size() + ")");
                } else {
                    btn_selected.setText("确定(" + Constant.list.size() + "/" + maxFileSize + ")");
                }

            }
        });
        return view;
    }


    class ViewHolder {
        private ImageView iv_image;
        private AppCompatCheckBox acb_image;
    }
}
