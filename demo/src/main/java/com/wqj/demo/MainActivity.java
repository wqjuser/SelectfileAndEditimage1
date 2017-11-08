package com.wqj.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wqj.selectfileandeditimage.selectpicture.view.SelectFileGridViewEx;

public class MainActivity extends AppCompatActivity {
    private SelectFileGridViewEx mSelectFileGridViewEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSelectFileGridViewEx = (SelectFileGridViewEx) findViewById(R.id.pickFile);
        mSelectFileGridViewEx.init(this, 4);
        mSelectFileGridViewEx.setMenu(true, true, true, true, true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSelectFileGridViewEx.setIntent(requestCode, resultCode, data);
    }
}
