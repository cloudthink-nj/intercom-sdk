package cn.net.cloudthink.smartmirror.intercom;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.JsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NotificationUtils;
import com.blankj.utilcode.util.Utils;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.net.cloudthink.library.intercom.IRequestInterface;
import cn.net.cloudthink.library.intercom.IntercomServiceImpl;
import cn.net.cloudthink.library.intercom.BLAidlAction;

/**
 * Created by Broadlink lvzhaoyang on 2020/10/15.
 * Email: zhaoyang.lv@broadlink.com.cn
 */
public class IntercomService extends Service implements IRequestInterface {

    private CallFloatWindowView mCallUIView;
    private ConfigWindowView mConfigView;
    private IntercomServiceImpl mBinderImpl;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(getApplication());
        CrashUtils.init();
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);
        LogUtils.getConfig().setGlobalTag(BuildConfig.PROJECT_NAME);
//        startNotification();
        mBinderImpl = new IntercomServiceImpl(this);
    }

    private void startNotification() {

        Notification notification = NotificationUtils.getNotification(NotificationUtils.ChannelConfig.DEFAULT_CHANNEL_CONFIG, new Utils.Consumer<NotificationCompat.Builder>() {

            @Override
            public void accept(NotificationCompat.Builder builder) {
                builder.setContentTitle("思必驰语音助手");
                builder.setContentText("正在运行中...");
                builder.setWhen(System.currentTimeMillis());
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            }
        });
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinderImpl;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    private void showCallUIView() {
        if (mCallUIView != null) {
            mCallUIView.remove();
            mCallUIView = null;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("CALL_TYPE", 0);
        bundle.putBoolean("CALL_IN", true);
        mCallUIView = new CallFloatWindowView(bundle);
        mCallUIView.show();
    }

    // 可以自定义action， 对接处理
    @Override
    public void requestAction(String action, String data) {
        LogUtils.i(action + data);
        int code = 0;
        JSONObject obj = new JSONObject();
        if (action.equals(BLAidlAction.ACTION_SHOW_CONFIG)) {
            showConfigView();
        } else if (action.equals(BLAidlAction.ACTION_SET_CONFIG)) {
            // 后面会通过主程序自动配置家庭的楼栋单元房号等等
            // data {"building":8,"unit":2,"floor":3,"family":1,"familyNo":"0802-0301"}
            //setIntercomConfig()
        } else if (action.equals(BLAidlAction.ACTION_SHOW_DIALER)) {
            //showDialerView()
        } else if (action.equals(BLAidlAction.ACTION_GET_UNIT_DEVICE)) {
            // 获取单元机设备列表(多单元机情况) 异步回调方式
            //  obj.put("code", code);
            //  obj.put("data", list.toJsonString);
            //  mBinderImpl.sendCallbackAction(ACTION_CALLBACK_UNIT_DEVICE, obj.toString());
        } else if (action.equals(BLAidlAction.ACTION_CALL_UNIT_DEVICE)) {
            String id = JsonUtils.getString(data, "id");
            if (TextUtils.isEmpty(id)) {
                callUnitDevice(id);
            }
        } else if (action.equals(BLAidlAction.ACTION_GET_CENTER_DEVICE)) {
            // 同获取单元机情况
        } else if (action.equals(BLAidlAction.ACTION_CALL_CENTER_DEVICE)) {
            String id = JsonUtils.getString(data, "id");
            if (TextUtils.isEmpty(id)) {
                callCenterDevice(id);
            }
        } else if (action.equals(BLAidlAction.ACTION_EMERGENCY_HELP)) {
            int type = JsonUtils.getInt(data, "type");
            //code = emergencyHelp(type)
        } else if (action.equals(BLAidlAction.ACTION_CALL_ELEVATOR)) {
            int type = JsonUtils.getInt(data, "type");
            if (type == 0) {
                // 显示呼梯界面UI
            } else if (type == 1) {
                //目的模式
            } else if (type == 2) {
                // 外呼模式
            } else {

            }
        } else {

        }
    }

    private List<String> getUnitDevice() {
        //返回的list data 里 id: 设备标识， name: 设备名称(如1#单元机等等)
        return null;
    }

    private void callUnitDevice(String id) {
        // 拨打指定的单元机
    }

    private void callCenterDevice(String id) {
        // 拨打指定的管理机
    }

    public void showConfigView() {
        if (mConfigView == null || !mConfigView.getVisibility()) {
            mConfigView = new ConfigWindowView();
            mConfigView.show();
        }
    }

    // 当收到管理平台发来的消息时,把消息回调给客户端
    public void onReceiveCenterMessage(String message) {
        try {
            int code = 0;
            JSONObject obj = new JSONObject();
            obj.put("code", code);
            obj.put("data", message);
            mBinderImpl.sendCallbackAction(BLAidlAction.ACTION_CALLBACK_CENTER_MESSAGE, obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 当出现未接来电时, 把消息回调给客户端
    public void onMissedIntercom(String message) {
        try {
            int code = 0;
            JSONObject obj = new JSONObject();
            obj.put("code", code);
            obj.put("data", message);
            mBinderImpl.sendCallbackAction(BLAidlAction.ACTION_CALLBACK_MISS_INTERCOM, obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}