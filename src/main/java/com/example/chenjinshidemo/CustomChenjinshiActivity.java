package com.example.chenjinshidemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * 这个页面我们可以随意的设置自定义状态栏
 * 思路如下：
 * 1.页面设置为全屏且显示状态栏，状态栏为透明
 * 2.拿到手机状态栏的高度(可以有出入，不过没关系)，页面布局顶部预留一个view，高度设置为状态栏高度
 * 3.设置顶部view的颜色，随意修改达到沉浸式，甚至可以添加图片都无所谓
 */
public class CustomChenjinshiActivity extends Activity {
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_chenjinshi);
        //todo:第一步：全屏且显示透明状态栏
        fullScreen(this);
        //todo:第二步，设置布局中的顶部view高度为状态栏高度(或者自己想着顶部直接弄成顶天的图片也可以)
        View view = findViewById(R.id.top_status_bar);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.height = getStarusBarHeight(this);
        view.setLayoutParams(params);
        //todo：第三步，设置状态栏的颜色，随意修改,我是在xml文件中设置的，代码就不修改了

        //todo:第四步，状态栏字体想变成黑色，但是魅族等手机会自己吸取状态栏后面view的颜色，然后自动修改状态栏字体的颜色问题，比如在这个页面设置为了黑色字体，但是当打开下一个页面再回来颜色就又变为白色了
        //todo:分析这个问题：我们可以采取欺骗系统的方式来达到改变字体颜色的目的，首先onstop的时候我们先将状态栏的颜色设置为白色，onWindowFocusChanged中在设置为需要的颜色
        //这里mView就是这个作用
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mView) {
            mView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (null == mView) {
                mView = findViewById(R.id.status_view);
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.setBackgroundColor(Color.TRANSPARENT);
                    }
                });
            } else {
                mView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//解决华为手机等状态栏上面有一个蒙层问题
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getStarusBarHeight(Context context) {
        int statusBarHeight1 = -1;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    public void toThreeActivity(View view) {
        Intent intent = new Intent(this, FragmentsActivity.class);
        startActivity(intent);
    }
}
