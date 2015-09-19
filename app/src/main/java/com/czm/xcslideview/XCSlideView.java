package com.czm.xcslideview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.czm.utils.DensityUtil;

/**自定义侧滑View控件
 * Created by caizhiming on 2015/9/18.
 */
public class XCSlideView extends RelativeLayout{

    //侧滑方向-从哪侧滑出
    public static enum Positon {
        LEFT, RIGHT
    }
    private Context mContext;
    private Activity mActivity;
    private Scroller mScroller = null;
    //侧滑菜单布局View
    private View mMenuView;
    //底部蒙层View
    private View mMaskView;
    private int mMenuWidth = 0;
    //屏幕宽度
    private int mScreenWidth = 0;
    //是否在滑动中
    private boolean mIsMoving = false;
    //显示登录界面与否
    private boolean mShow = false;
    //滑动动画时间
    private int mDuration = 600;
    //缺省侧滑方向为左
    private Positon mPositon = Positon.LEFT;

    public XCSlideView(Context context) {
        this(context, null);
    }
    public XCSlideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public XCSlideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    /**
     * 创建侧滑菜单View
     */
    public static XCSlideView create(Activity activity) {
        XCSlideView view = new XCSlideView(activity);
        return view;
    }
    /**
     * 创建侧滑菜单View
     */
    public static XCSlideView create(Activity activity, Positon positon) {
        XCSlideView view = new XCSlideView(activity);
        view.mPositon = positon;
        return view;
    }
    private void init(Context context) {
        // TODO Auto-generated method stub
        mContext = context;
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);
        mScroller = new Scroller(context);
        mScreenWidth = DensityUtil.getScreenWidthAndHeight(context)[0];
        mMenuWidth = mScreenWidth * 7 / 9;

        attachToContentView((Activity) context, mPositon);
    }
    /**
     * 创建 蒙层View并添加到contentView中
     */
    private void attachToContentView(Activity activity, Positon positon) {
        mPositon = positon;
        ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup contentView = ((ViewGroup) contentFrameLayout.getChildAt(0));
        mMaskView = new View(activity);
        mMaskView.setBackgroundColor(mContext.getResources().getColor(R.color.mask_color));
        contentView.addView(mMaskView, contentView.getLayoutParams());
        mMaskView.setVisibility(View.GONE);
        mMaskView.setClickable(true);
        mMaskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow()) {
                    dismiss();
                }
            }
        });
    }
    /**
     * 设置侧滑菜单View的宽度
     */
    public void setMenuWidth(int width) {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.width = width;
        mMenuWidth = width;
        this.setLayoutParams(params);
    }
    /**
     * 设置侧滑菜单View，并添加到DectorView->LinearLayout->内容显示区域View中
     */
    public void setMenuView(Activity activity, View view) {
        mActivity = activity;
        mMenuView = view;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mMenuView, params);
        mMenuView.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mMenuWidth = mMenuView.getWidth();
                switch (mPositon) {
                    case LEFT:
                        XCSlideView.this.scrollTo(mScreenWidth, 0);
                        break;
                    case RIGHT:
                        XCSlideView.this.scrollTo(-mScreenWidth, 0);
                        break;
                }

            }
        });
        ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup contentView = contentFrameLayout;
        contentView.addView(this);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        switch (mPositon) {
            case LEFT:
                layoutParams.gravity = Gravity.LEFT;
                layoutParams.leftMargin = 0;
                break;
            case RIGHT:
                layoutParams.gravity = Gravity.RIGHT;
                layoutParams.rightMargin = 0;
                break;
        }
        TextView titleFrameLayout = (TextView) activity.findViewById(android.R.id.title);
        if( titleFrameLayout != null){
            layoutParams.topMargin = DensityUtil.getStatusBarHeight(mContext);
        }
        int flags =  mActivity.getWindow().getAttributes().flags;
        int flag = (flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(flag == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS){
            //说明状态栏使用沉浸式
            layoutParams.topMargin = DensityUtil.getStatusBarHeight(mContext);
        }
        this.setLayoutParams(layoutParams);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isShow()){
                    return true;
                }
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
    /**
     * 显示侧滑菜单View
     */
    public void show(){
        if(isShow() && !mIsMoving)
            return;
        switch (mPositon) {
            case LEFT:
                startScroll(mMenuWidth, -mMenuWidth, mDuration);
                break;
            case RIGHT:
                startScroll(-mMenuWidth, mMenuWidth, mDuration);
                break;
        }
        switchMaskView(true);
        mShow = true;
    }
    /**
     * 蒙层显示开关
     */
    private void switchMaskView(boolean bShow){
        if(bShow){
            mMaskView.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(mDuration);
            mMaskView.startAnimation(animation);
        }else{
            mMaskView.setVisibility(View.GONE);
        }
    }
    /**
     * 关闭侧滑菜单View
     */
    public void dismiss() {
        // TODO Auto-generated method stub
        if(!isShow() && !mIsMoving)
            return;
        switch (mPositon) {
            case LEFT:
                startScroll(XCSlideView.this.getScrollX(), mMenuWidth, mDuration);
                break;
            case RIGHT:
                startScroll(XCSlideView.this.getScrollX(), -mMenuWidth, mDuration);
                break;
        }
        switchMaskView(false);
        mShow = false;
    }
    public boolean isShow(){
        return mShow;
    }
    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 更新界面
            postInvalidate();
            mIsMoving = true; 
        } else {
            mIsMoving = false;
        }
        super.computeScroll();
    }
    /**
     * 拖动移动
     */
    public void startScroll(int startX, int dx,int duration){
        mIsMoving = true;
        mScroller.startScroll(startX,0,dx,0,duration);
        invalidate();
    }
}
