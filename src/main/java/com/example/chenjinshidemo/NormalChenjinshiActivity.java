package com.example.chenjinshidemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * 这就是一个普通的沉浸式页面：
 * 思路就是通过改变状态栏的颜色来达到沉浸式效果，并且次demo选择了白色沉浸式(比普通的麻烦)
 */
public class NormalChenjinshiActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_chenjinshi);
        immersiveNotificationBar(this);
    }

    /**
     * 设置沉浸式状态栏
     */
    public static void immersiveNotificationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//字体颜色改为黑色，非白色沉浸式状态栏不需要设置
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        immersiveNotificationBar(activity, 255);
    }

    /**
     * 设置沉浸式状态栏
     */
    public static void immersiveNotificationBar(Activity activity, int alpha) {
        String brand = Build.BRAND;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//解决华为手机等状态栏上面有一个蒙层问题
                try {
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(window.getDecorView(), Color.TRANSPARENT);  //改为透明
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            if ("vivo".equalsIgnoreCase(brand) || "OPPO".equals(brand)) {//oppo和vivo手机状态栏最好不要显示为纯白色，官方未给出改变字体颜色为黑色方法
                window.setStatusBarColor(Color.argb(alpha, 208, 208, 208));
            } else {
                window.setStatusBarColor(Color.argb(alpha, 255, 255, 255));
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int count = decorView.getChildCount();
            if (count > 0 && null != decorView.getChildAt(count - 1)) {
                decorView.getChildAt(count - 1).setBackgroundColor(calculateStatusColor(Color.WHITE, 30));
            } else {
                View statusView = createStatusBarView(activity, Color.WHITE, 30);
                decorView.addView(statusView);
            }
            setRootView(activity);
        }
    }

    private static View createStatusBarView(Activity activity, @ColorInt int color, int alpha) {
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStarusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        return statusBarView;
    }

    private static void setRootView(Activity activity) {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(true);
    }

    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    public static int getStarusBarHeight(Context context) {
        int statusBarHeight1 = -1;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

}
