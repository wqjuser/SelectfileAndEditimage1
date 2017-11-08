package com.wqj.selectfileandeditimage.selectpicture.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by snsoft on 2016/8/26.
 */
public class DialogMenuEx extends Dialog {
    private final String TAG = "DialogMenuEx";
    private Context context;
    //    private Bitmap[] bitmaps;
    private List<String> itemNames;
    private List<Integer> rs;
    private List<Integer> requestCodes;
    private View.OnClickListener itemsOnClick;
    private int n = 4;//一行显示几个
    private int itemHeight = 0;
    private int width, height;
    private boolean isShowLine = true;//是否显示分割线

    public DialogMenuEx(Context context, View.OnClickListener itemsOnClick,
                        List<String> itemNames, boolean isShowLine) {
//        super(context, android.R.style.Theme_Holo_DialogWhenLarge);
        super(context);
        this.context = context;
        this.itemNames = itemNames;
        this.itemsOnClick = itemsOnClick;
        this.isShowLine = isShowLine;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    /**
     * @param context      上下文
     * @param itemsOnClick 点击事件
     * @param itemNames    菜单上显示的文字
     * @param rs           菜单上显示的图片
     * @param n            每行显示几个菜单
     */
    public DialogMenuEx(Context context, View.OnClickListener itemsOnClick,
                        List<String> itemNames, List<Integer> rs, List<Integer> requestCodes, int n, boolean isShowLine) {
//        super(context, R.style.quick_option_dialog);
        super(context);

        this.context = context;
        this.itemNames = itemNames;
        this.itemsOnClick = itemsOnClick;
        this.rs = rs;
        this.requestCodes = requestCodes;
        this.n = n;
        this.isShowLine = isShowLine;
        //设置dialog没有标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        itemHeight = width / n;
        Log.i(TAG, "width=" + width + ",height=" + height);
        init();//必须在onCreate方法前执行，否则会导致宽度失效
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM); //显示在底部
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = width; //设置宽度为手机屏幕宽度
        //lp.y = 20;//设置dialog距离屏幕下边缘20个单位长度
        this.getWindow().setAttributes(lp);
        //设置背景色？有这句时才能去掉dialog的边距，否则dialog距离屏幕边缘有16个dp的距离
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //设置出现动画，从下面出现
        this.getWindow().setWindowAnimations(android.R.style.Animation_InputMethod);

    }

    private void init() {
////        View view = LayoutInflater.from(context).inflate(R.layout.dialog, null);
//        View view =new View(context);
//        LinearLayout linearLayout = new LinearLayout(context);//(LinearLayout) view.findViewById(R.id.linearLayout);
//        linearLayout.setLayoutParams(new LinearLayout.LayoutParams());
        if (rs != null) {
            LinearLayout ll_Man = new LinearLayout(context);
//            LinearLayout.LayoutParams paramsMain = (LinearLayout.LayoutParams) ll_Man.getLayoutParams();
            ll_Man.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            ll_Man.setMinimumHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            ll_Man.setOrientation(LinearLayout.VERTICAL);
            int line = 1;
            int size = itemNames.size();
            if (size % n == 0) {
                line = size / n;
            } else {
                line = size / n + 1;
            }
            Log.i(TAG, "行数=" + line);
            for (int i = 0; i < line; i++) {
                LinearLayout ll_line = new LinearLayout(context);
                ll_line.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < n; j++) {
                    RelativeLayout rl_item = new RelativeLayout(context);
                    LinearLayout.LayoutParams paramsItem = new LinearLayout.LayoutParams(0, itemHeight, 1);
                    paramsItem.setMargins(0, 0, 0, 10);
                    rl_item.setLayoutParams(paramsItem);
                    if ((i * n + j) < rs.size()) {
                        ImageView ivItem = new ImageView(context);
                        ivItem.setId(i * n + j + 1);//id不能设为0，第一次时i*n+j=0,所以不起作用
                        RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        ivParams.addRule(RelativeLayout.CENTER_IN_PARENT);
// 代码设置在某控件的下面    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(35, 35);
//                           lp.addRule(RIGHT_OF,R.id.singletweet_toolgeo);
//                           relativelayout.addView(imageview, lp);
//      代码设置居中      RelativeLayout.LayoutParams rlp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//                        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性
//                        relativeLayout.addView(progressBar,rlp);
                        ivItem.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), rs.get(i * n + j)));
                        rl_item.addView(ivItem, ivParams);

                        TextView tvItem = new TextView(context);
                        RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        tvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        tvParams.addRule(RelativeLayout.BELOW, ivItem.getId());
                        tvItem.setLayoutParams(tvParams);
                        tvItem.setText(itemNames.get(i * n + j));
                        rl_item.addView(tvItem);
                        rl_item.setTag(requestCodes.get(i * n + j));// 设置tag用于区分是哪个按钮，方便设置点击事件
                        rl_item.setOnClickListener(itemsOnClick);//设置点击事件
                        Log.i(TAG, "n=" + n + "，j=" + j);
                    }
                    ll_line.addView(rl_item);
                    if (isShowLine && j < (n - 1) && (i * n + j) < rs.size()) {//必须设置了显示格子，然后每行只有下标0,1,2的Item后面显示细线，并且细线前的item不是空的
                        Log.i(TAG, "竖线一个");
                        Log.i(TAG, "i*n+j=" + (i * n + j));
                        View view = new View(context);
                        view.setBackgroundColor(Color.parseColor("#C8C8C8"));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
                        ll_line.addView(view, params);// 添加下面的那条线

                    }
                }

                ll_Man.addView(ll_line);
                if (isShowLine && i < (line - 1)) {
                    Log.i(TAG, "横线一个");
                    View view = new View(context);
                    view.setBackgroundColor(Color.parseColor("#C8C8C8"));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    ll_Man.addView(view, params);// 添加下面的那条线
                }
            }
//            linearLayout.addView(ll_Man);
            this.setContentView(ll_Man);
        }
    }

}
