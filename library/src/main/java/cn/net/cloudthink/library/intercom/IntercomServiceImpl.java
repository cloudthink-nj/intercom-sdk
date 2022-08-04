package cn.net.cloudthink.library.intercom;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import org.json.JSONException;
import org.json.JSONObject;

import cn.net.cloudthink.smartmirror.intercom.IIntercomCallback;
import cn.net.cloudthink.smartmirror.intercom.IIntercomService;

import static cn.net.cloudthink.library.intercom.BLAidlAction.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/6/3 10:16
 * @Email: zhaoyang.lv@broadlink.com.cn
 */
public class IntercomServiceImpl extends IIntercomService.Stub {
    private final IRequestInterface mListener;
    private final ExecutorService mSingleThreadPool = Executors.newSingleThreadExecutor();

    public IntercomServiceImpl(IRequestInterface listen) {
        mListener = listen;
    }

    protected final RemoteCallbackList<IIntercomCallback> mCallbackList = new RemoteCallbackList<>();

    @Override
    public String getBrandName() throws RemoteException {
        return null;
    }

    @Override
    public int getVersionCode() throws RemoteException {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public int openConfigView() throws RemoteException {
        mListener.requestAction(ACTION_SHOW_CONFIG, null);
        return 0;
    }

    @Override
    public int openDialerView() throws RemoteException {
        mListener.requestAction(ACTION_SHOW_DIALER, null);
        return 0;
    }

    @Override
    public int callUnitDevice() throws RemoteException {
        mListener.requestAction(ACTION_CALL_UNIT_DEVICE, null);
        return 0;
    }

    @Override
    public int callManagement() throws RemoteException {
        mListener.requestAction(ACTION_CALL_CENTER_DEVICE, null);
        return 0;
    }

    @Override
    public int emergencyHelp(int alarmType) throws RemoteException {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", alarmType);
            mListener.requestAction(ACTION_EMERGENCY_HELP, obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int callElevator(int type, int curFloor, int destFloor, boolean direction) throws RemoteException {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", type);
//            obj.put("curFloor", curFloor);
            obj.put("destFloor", destFloor);
            obj.put("direction", direction);
            mListener.requestAction(ACTION_CALL_ELEVATOR, obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void addCallback(IIntercomCallback cb) throws RemoteException {
        mCallbackList.register(cb);
    }

    @Override
    public void removeCallback(IIntercomCallback cb) throws RemoteException {
        mCallbackList.unregister(cb);
    }

    @Override
    public void requestAction(String action, String data) throws RemoteException {
        mListener.requestAction(action, data);
    }

    public void sendCallbackAction(final String action, final String data) {
        mSingleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int n = mCallbackList.beginBroadcast();
                    for (int i = 0; i < n; i++) {
                        IIntercomCallback callback = mCallbackList.getBroadcastItem(i);
                        callback.onIntentAction(action, data);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } finally {
                    mCallbackList.finishBroadcast();
                }
            }
        });
    }
}
