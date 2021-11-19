package cn.net.cloudthink.library.intercom;

import static cn.net.cloudthink.library.intercom.BLAidlAction.TAG;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.net.cloudthink.smartmirror.intercom.IIntercomCallback;
import cn.net.cloudthink.smartmirror.intercom.IIntercomService;

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/11/10 9:08
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: IntercomClient
 */
public abstract class IntercomClient {

    private IIntercomService mIntercomBinder = null;

    private final ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIntercomBinder = IIntercomService.Stub.asInterface(service);
            if (mIntercomBinder != null) {
                try {
                    mIntercomBinder.addCallback(mCallback);
                    service.linkToDeath(mRecipient, 0);
                } catch (RemoteException e) {
                    Log.e(TAG, "onServiceConnected ", e);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIntercomBinder = null;
        }
    };

    private final IIntercomCallback mCallback = new IIntercomCallback.Stub() {

        @Override
        public void onIntentAction(String action, String data) throws RemoteException {
            onHandleAction(action, data);
        }
    };

    private final IBinder.DeathRecipient mRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            if (mIntercomBinder != null) {
                mIntercomBinder.asBinder().unlinkToDeath(this, 0);
                mIntercomBinder = null;
            }
            onBinderDied();
        }
    };

    abstract void onHandleAction(String action, String data);

    abstract void onBinderDied();

    public void bindService(Context context, String packageName, String action) {
        if (mIntercomBinder == null) {
            Intent intent = new Intent(action);
            intent.setPackage(packageName);
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void unbindService(Context context) {
        if (mIntercomBinder != null) {
            try {
                mIntercomBinder.removeCallback(mCallback);
                context.unbindService(mConnection);
            } catch (RemoteException e) {
                Log.e(TAG, "unbind ", e);
            }
            mIntercomBinder = null;
        }
    }

    public void request(final String action, final String data) {
        mCachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mIntercomBinder != null) {
                        mIntercomBinder.requestAction(action, data);
                    } else {
                        Log.e(TAG, "mSpeechBinder is null: " + action);
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "requestAction: " + action, e);
                }
            }
        });
    }
}
