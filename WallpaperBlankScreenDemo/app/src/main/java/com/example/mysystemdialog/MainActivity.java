package com.example.mysystemdialog;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private DesktopLayout mWindowViewLayout;
    Button mClosetBtn = null;
    Button mDrawBtn = null;
    // 声明屏幕的宽高
    float x, y;
    int top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayoutParams =  createWindowManager(false,false);
        createDesktopLayout();
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDesk();
            }
        });
    }
    /**
     * 创建悬浮窗体Layout
     */
    private void createDesktopLayout() {
        mWindowViewLayout = new DesktopLayout(this);
        mClosetBtn = mWindowViewLayout.findViewById(R.id.button2);
        mClosetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDesk();
                closeOnlyShowDesk();
            }
        });
        mDrawBtn = mWindowViewLayout.findViewById(R.id.button_draw);
        mDrawBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOnlyShowDesk();
                showDrawDesk();
            }
        });
        Button wallpaper1 = mWindowViewLayout.findViewById(R.id.set_wallpaper_1);
        wallpaper1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaper(1);
            }
        });
        Button wallpaper2 = mWindowViewLayout.findViewById(R.id.set_wallpaper_2);
        wallpaper2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaper(2);
            }
        });
        mWindowViewLayout.setOnTouchListener(onTouchListener);
    }
    void setWallpaper(int index ) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {
            wallpaperManager.setBitmap(getBitmapByIndex(index));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    Bitmap getBitmapByIndex(int index) {
        if (index == 1) {
            return BitmapFactory.decodeResource(getResources(),R.mipmap.a);
        }
        return BitmapFactory.decodeResource(getResources(),R.mipmap.b);
    }


    OnTouchListener onTouchListener = new OnTouchListener() {
        float mTouchStartX;
        float mTouchStartY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 获取相对屏幕的坐标，即以屏幕左上角为原点
            x = event.getRawX();
            y = event.getRawY() - top; // 25是系统状态栏的高度
            Log.i("testx", "startX" + mTouchStartX + "====startY"
                    + mTouchStartY);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 获取相对View的坐标，即以此View左上角为原点
                    mTouchStartX = event.getX();
                    mTouchStartY = event.getY();
                    Log.i("testx", "startX" + mTouchStartX + "====startY"
                            + mTouchStartY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 更新浮动窗口位置参数
                    mLayoutParams.x = (int) (x - mTouchStartX);
                    mLayoutParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(v, mLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:

                    // 更新浮动窗口位置参数
                    mLayoutParams.x = (int) (x - mTouchStartX);
                    mLayoutParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(v, mLayoutParams);

                    // 可以在此记录最后一次的位置

                    mTouchStartX = mTouchStartY = 0;
                    break;
            }
            return true;
        }
    };
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Rect rect = new Rect();
        // /取得整个视图部分,注意，如果要设置标题样式，这个必须出现在标题样式之后，否则会出错
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        top = rect.top;//状态栏的高度，所以rect.height,rect.width分别是系统的高度的宽度

        Log.i("top",""+top);
    }

    /**
     * 显示DesktopLayout
     */
    private void showDesk() {
        mWindowManager.addView(mWindowViewLayout, mLayoutParams);
       // finish();
    }
    DrawLayout mDrawLayout;
    private  void closeDrawDesk() {
        mWindowManager.removeView(mDrawLayout);;
    }
    Path mCachePath = null;
    private void showDrawDesk() {
        mDrawLayout = new DrawLayout(this);
        mWindowManager.addView(mDrawLayout, createWindowManager(true,false));
        Button stopDrawBtn = (Button)mDrawLayout.findViewById(R.id.button2);
        if (DrawLayout.sTransparent) {
            stopDrawBtn.setVisibility(View.GONE);
        }
        stopDrawBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCachePath = mDrawLayout.getPath();
                closeDrawDesk();
                if (mCachePath!= null) {
                    showOnlyShowDesk(mCachePath);
                }
            }
        });

    }
    DrawLayout mOnlyShowLayout = null;
    private void showOnlyShowDesk(Path path) {
        mOnlyShowLayout = new DrawLayout(this);
        mOnlyShowLayout.setPath(path);
        mOnlyShowLayout.removeAllViews();
        mWindowManager.addView(mOnlyShowLayout, createWindowManager(true,true));

    }
    private void closeOnlyShowDesk() {
       if (mOnlyShowLayout!= null) {
           mWindowManager.removeView(mOnlyShowLayout);
           mOnlyShowLayout = null;
       }

    }

    OnTouchListener onTouchDrawListener = new OnTouchListener() {
        float mTouchStartX;
        float mTouchStartY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 获取相对屏幕的坐标，即以屏幕左上角为原点
            x = event.getRawX();
            y = event.getRawY() - top; // 25是系统状态栏的高度
            Log.i("testx", "startX" + mTouchStartX + "====startY"
                    + mTouchStartY);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 获取相对View的坐标，即以此View左上角为原点
                    mTouchStartX = event.getX();
                    mTouchStartY = event.getY();
                    Log.i("testx", "startX" + mTouchStartX + "====startY"
                            + mTouchStartY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 更新浮动窗口位置参数
                    mLayoutParams.x = (int) (x - mTouchStartX);
                    mLayoutParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(v, mLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:

                    // 更新浮动窗口位置参数
                    mLayoutParams.x = (int) (x - mTouchStartX);
                    mLayoutParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(v, mLayoutParams);

                    // 可以在此记录最后一次的位置

                    mTouchStartX = mTouchStartY = 0;
                    break;
            }
            return true;
        }
    };
    /**
     * 关闭DesktopLayout
     */
    private void closeDesk() {
        mWindowManager.removeView(mWindowViewLayout);
        //finish();
    }

    /**
     * 设置WindowManager
     */
    private  WindowManager.LayoutParams  createWindowManager(boolean isDraw,boolean onlyShow) {
        WindowManager.LayoutParams  mLayoutParams ;
        // 取得系统窗体
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        // 窗体的布局样式
        mLayoutParams = new WindowManager.LayoutParams();

        // 设置窗体显示类型——TYPE_APPLICATION_OVERLAY
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        if (isDraw && onlyShow) {
            mLayoutParams.flags =  mLayoutParams.flags  | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        // 设置显示的模式
       mLayoutParams.format = PixelFormat.RGBA_8888;

        // 设置对齐的方法
        if (isDraw) {
            mLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        } else {
            mLayoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        }

        // 设置窗体宽度和高度
        mLayoutParams.width = isDraw ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = isDraw ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;
        return mLayoutParams;
    }

}