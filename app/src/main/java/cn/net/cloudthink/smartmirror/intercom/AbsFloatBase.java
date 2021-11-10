package cn.net.cloudthink.smartmirror.intercom;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_TOAST;

public abstract class AbsFloatBase {

    public static final int FULLSCREEN_TOUCHABLE = 1;
    public static final int FULLSCREEN_NOT_TOUCHABLE = 2;
    public static final int WRAP_CONTENT_TOUCHABLE = 3;
    public static final int WRAP_CONTENT_NOT_TOUCHABLE = 4;

    private WindowManager.LayoutParams mLayoutParams;

    private View mInflate;
    private WindowManager mWindowManager;
    private boolean mAdded;
    //设置隐藏时是否是INVISIBLE
    private boolean mInvisibleNeed = false;
    private boolean mRequestFocus = false;
    public boolean mKeepScreenOn = true;
    public int mViewMode = WRAP_CONTENT_NOT_TOUCHABLE;
    public int mGravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
    public Handler mHandler = new Handler(Looper.getMainLooper());

    public AbsFloatBase() {
        this(null);
    }

    public AbsFloatBase(@Nullable Bundle bundle) {
        create(bundle);
    }

    @CallSuper
    public void create(@Nullable Bundle bundle) {
        mWindowManager = (WindowManager) Utils.getApp().getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 设置隐藏View的方式是否为Invisible，默认为Gone
     *
     * @param invisibleNeed 是否是Invisible
     */
    public void setInvisibleNeed(boolean invisibleNeed) {
        mInvisibleNeed = invisibleNeed;
    }

    /**
     * 悬浮窗是否需要获取焦点，通常获取焦点后，悬浮窗可以和软键盘发生交互，被覆盖的应用失去焦点。
     * 例如：游戏将失去背景音乐
     *
     * @param requestFocus
     */
    public void requestFocus(boolean requestFocus) {
        mRequestFocus = requestFocus;
    }


    @CallSuper
    public synchronized void show() {
        if (mInflate == null)
            throw new IllegalStateException("FloatView can not be null");

        if (mAdded) {
            mInflate.setVisibility(View.VISIBLE);
            return;
        }

        getLayoutParam(mViewMode);

        mInflate.setVisibility(View.VISIBLE);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mWindowManager.addView(mInflate, mLayoutParams);
                    mAdded = true;
                } catch (Exception e) {
                    LogUtils.e("添加悬浮窗失败: " + e.getMessage());
                    ToastUtils.showShort("添加悬浮窗失败！！！！！！请检查悬浮窗权限");
                }
            }
        });
    }

    @CallSuper
    public void hide() {
        if (mInflate != null) {
            mInflate.setVisibility(View.INVISIBLE);
        }
    }

    @CallSuper
    public void gone() {
        if (mInflate != null) {
            mInflate.setVisibility(View.GONE);
        }
    }

    @CallSuper
    public void remove() {
        if (mInflate != null && mWindowManager != null) {
            if (mInflate.isAttachedToWindow()) {
                mWindowManager.removeView(mInflate);
            }
            mAdded = false;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @CallSuper
    protected View inflate(@LayoutRes int layout) {
        mInflate = View.inflate(Utils.getApp(), layout, null);
        return mInflate;
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int id) {
        if (mInflate != null) {
            return (T) mInflate.findViewById(id);
        }
        return null;
    }


    /**
     * 获取可见性
     *
     * @return visibility
     */
    public boolean getVisibility() {
        if (mAdded && mInflate != null && mInflate.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 改变可见性
     */
    public void toggleVisibility() {
        if (mInflate != null) {
            if (getVisibility()) {
                if (mInvisibleNeed) {
                    hide();
                } else {
                    gone();
                }
            } else {
                show();
            }
        }
    }

    //获取悬浮窗LayoutParam
    private void getLayoutParam(int mode) {
        switch (mode) {
            case FULLSCREEN_TOUCHABLE:
                mLayoutParams = getFloatLayoutParam(true, true);
                break;

            case FULLSCREEN_NOT_TOUCHABLE:
                mLayoutParams = getFloatLayoutParam(true, false);
                break;

            case WRAP_CONTENT_NOT_TOUCHABLE:
                mLayoutParams = getFloatLayoutParam(false, false);
                break;

            case WRAP_CONTENT_TOUCHABLE:
                mLayoutParams = getFloatLayoutParam(false, true);
                break;
        }

        if (mRequestFocus) {
            mLayoutParams.flags = mLayoutParams.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }

        if (mKeepScreenOn) {
            mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        }

        mLayoutParams.gravity = mGravity;
    }

    private WindowManager.LayoutParams getFloatLayoutParam(boolean fullScreen, boolean touchAble) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            layoutParams.type = TYPE_TOAST;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.packageName = AppUtils.getAppPackageName();

        layoutParams.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;

        //Focus会占用屏幕焦点，导致游戏无声
        if (touchAble) {
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }

        if (fullScreen) {
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

//        layoutParams.format = PixelFormat.TRANSPARENT;

        return layoutParams;
    }
}
