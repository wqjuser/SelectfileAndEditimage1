package com.wqj.selectfileandeditimage.selectpicture.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wqj.selectfileandeditimage.R;

import java.util.List;

public class GvPictureMinAdapter extends BaseAdapter {

    private final String TAG = "GvPictureAdapter";

    private List<Bitmap> list;
    private LayoutInflater lif;

    public GvPictureMinAdapter(Context context, List<Bitmap> list) {
        super();

        this.lif = LayoutInflater.from(context);
        this.list = list;

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
    public View getView(int position, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh = null;
        if (vh == null) {
            vh = new ViewHolder();
            view = lif.inflate(R.layout.item_gv_min_picture, null);
            vh.iv_image = (ImageView) view.findViewById(R.id.iv_item_gv_pic);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.iv_image.setImageBitmap(list.get(position));
        return view;
    }

    class ViewHolder {
        private ImageView iv_image;
    }

}
