package com.lee.basedialog.dialog

import android.content.Context
import com.lee.basedialog.R
import com.lee.library.dialog.core.BaseAlertDialog

/**
 * @author jv.lee
 * @date 2020/9/30
 * @description
 */
class BaseAlertDialogImpl(context: Context) : BaseAlertDialog(context) {
    override fun buildViewId(): Int {
        return R.layout.dialog_alert
    }

    override fun bindView() {

    }

}