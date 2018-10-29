package com.droi.tool.mousetouch;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MyService extends AccessibilityService {
    private static final String TAG = "mouseT";

    float x = 0,y = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        Log.d(TAG, "onServiceConnected");
    }

    public  static AccessibilityService instance = null;
    //定义浮动窗口布局
    private LinearLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    private ImageButton mFloatView;
    private boolean isClick;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "onCreate");
        createFloatView();
    }

    private void createFloatView()
    {


        wmParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 152;
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.alert_window_menu, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatView = (ImageButton) mFloatLayout.findViewById(R.id.alert_window_imagebtn);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener()
        {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isClick = false;
                        x = event.getRawX();
                        y = event.getRawY();
                        Log.d(TAG, "------ACTION_DOWN---x="+x+"---y="+y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Log.d(TAG, "------ACTION_MOVE---x="+event.getRawX()+"---y="+event.getRawY());
                        if ((x - event.getRawX()) > 20
                                || (event.getRawX() - x) > 20
                                || (y - event.getRawY()) > 20
                                || (event.getRawY() - y) > 20
                                ) {
                            Log.d(TAG, "------ACTION_MOVE");
                            isClick = true;
                            // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            wmParams.x = (int) event.getRawX()
                                    - mFloatView.getMeasuredWidth() / 2;
                            // 减25为状态栏的高度
                            wmParams.y = (int) event.getRawY()
                                    - mFloatView.getMeasuredHeight() / 2;
                            // 刷新
                            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                            return true;
                        }
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "------ACTION_UP");
                        return isClick;// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。
                    default:
                        break;
                }
                return false;
            }
        });

        mFloatView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "------onClick");
                //TODO home
                startMouseInput();
            }
        });

        /*mFloatView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "------onClick");
                //TODO home
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        });

        mFloatView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "------onLongClick");
                //TODO home
                if( !isClick ){
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                }
                return true;
            }
        });*/
    }


    private void startMouseInput() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Instrumentation mInst = new Instrumentation();
                //键盘
                //mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_FOCUS); //按键事件
                //鼠标
                mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x+30, y, 0)); //触摸按下
                mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x+30, y, 0)); //触摸抬起
            }

        }).start();
    }

}
