package com.droi.tool.mousetouch;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mouseT";

    @BindView(R.id.button_accessibility)
    Button buttonAccessibility;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        intent = new Intent(this,MyService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,100);
            }
        }
    }

    @OnClick(R.id.button_accessibility)
    public void onViewClicked() {
        startService(intent);
        Log.d(TAG,"onViewClicked()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    /*WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.type = WindowManager.LayoutParams.TYPE_PHONE;
                    params.format = PixelFormat.RGBA_8888;
                    windowManager.addView(view,params);*/
                    Toast.makeText(this,"ACTION_MANAGE_OVERLAY_PERMISSION权限已被授权", Toast.LENGTH_SHORT).show();;
                }else {
                    Toast.makeText(this,"ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
