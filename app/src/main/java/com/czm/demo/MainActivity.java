package com.czm.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.czm.utils.DensityUtil;
import com.czm.xcslideview.R;
import com.czm.xcslideview.XCSlideView;

public class MainActivity extends Activity {

    private Context mContext;
    //屏幕宽度
    private int mScreenWidth = 0;
    private XCSlideView mSlideViewLeft;
    private XCSlideView mSlideViewRight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {
        mContext = this;
        initSlideView();
    }
    private void initSlideView() {

        mScreenWidth = DensityUtil.getScreenWidthAndHeight(mContext)[0];
        View menuViewLeft = LayoutInflater.from(mContext).inflate(R.layout.layout_slideview,null);
        mSlideViewLeft = XCSlideView.create(this, XCSlideView.Positon.LEFT);
        mSlideViewLeft.setMenuView(MainActivity.this, menuViewLeft);
        mSlideViewLeft.setMenuWidth(mScreenWidth * 7 / 9);
        Button left = (Button)findViewById(R.id.btn_left);
        left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!mSlideViewLeft.isShow())
                    mSlideViewLeft.show();
            }
        });
        menuViewLeft.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSlideViewLeft.isShow()) {
                    mSlideViewLeft.dismiss();
                }
            }
        });

        mSlideViewRight = XCSlideView.create(this, XCSlideView.Positon.RIGHT);
        View menuViewRight = LayoutInflater.from(mContext).inflate(R.layout.layout_slideview,null);
        mSlideViewRight.setMenuView(MainActivity.this, menuViewRight);
        mSlideViewRight.setMenuWidth(mScreenWidth * 7 / 9);
        Button right = (Button)findViewById(R.id.btn_right);
        right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!mSlideViewRight.isShow())
                    mSlideViewRight.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if(mSlideViewLeft.isShow()){
            mSlideViewLeft.dismiss();
            return ;
        }
        super.onBackPressed();
    }
}
