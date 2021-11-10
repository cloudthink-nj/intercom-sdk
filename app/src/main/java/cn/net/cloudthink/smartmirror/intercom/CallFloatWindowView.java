package cn.net.cloudthink.smartmirror.intercom;

import android.os.Bundle;
import android.view.View;

/**
 * Created by Broadlink lvzhaoyang on 2020/10/15.
 * Email: zhaoyang.lv@broadlink.com.cn
 */
public class CallFloatWindowView extends AbsFloatBase {

    private int mCallType; // 管理机、门口机
    private boolean mCallIn; // 呼入 呼出

    public CallFloatWindowView(Bundle bundle) {
        super(bundle);
    }

    @Override
    public void create(Bundle bundle) {
        super.create(bundle);
        mCallType = bundle.getInt("CALL_TYPE");
        mCallIn = bundle.getBoolean("CALL_IN");
        mViewMode = FULLSCREEN_TOUCHABLE;
        inflate(R.layout.layout_intercom_call_ui);

        findView(R.id.btn_intercom_hangup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
            }
        });
    }

    @Override
    public void remove() {
        super.remove();
    }
}