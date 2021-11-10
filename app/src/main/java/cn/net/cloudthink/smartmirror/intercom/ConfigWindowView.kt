package cn.net.cloudthink.smartmirror.intercom

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * Created by Broadlink lvzhaoyang on 2020/10/19.
 * Email: zhaoyang.lv@broadlink.com.cn
 */

class ConfigWindowView : AbsFloatBase() {

    override fun create(bundle: Bundle?) {
        super.create(bundle)
        mViewMode = FULLSCREEN_TOUCHABLE

        requestFocus(true)

        inflate(R.layout.layout_intercom_config)

        val editText: EditText = findView(R.id.et_house_code)
        val code = SPUtils.getInstance().getString("HOUSE_CODE")
        if (!TextUtils.isEmpty(code)) {
            editText.setText(code)
        }

        findView<Button>(R.id.btn_cancel).setOnClickListener {
            remove()
        }

        findView<Button>(R.id.btn_save).setOnClickListener {
            val houseCode = editText.text.toString().trim();
            if (houseCode.length == 8) {
                SPUtils.getInstance().put("HOUSE_CODE", houseCode)
                remove()
            } else {
                ToastUtils.showShort("房号配置错误！")
            }
        }
    }
}