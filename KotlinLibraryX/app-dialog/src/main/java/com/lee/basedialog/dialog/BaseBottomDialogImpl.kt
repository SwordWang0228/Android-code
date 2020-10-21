package com.lee.basedialog.dialog

import android.content.Context
import com.lee.basedialog.R
import com.lee.library.dialog.core.BaseBottomDialog

/**
 * @author jv.lee
 * @date 2020/9/30
 * @description bottom样式 Dialog
 */
class BaseBottomDialogImpl(context: Context) : BaseBottomDialog(context, 256) {
    override fun buildViewId(): Int {
        return R.layout.dialog_bottom
    }

    override fun bindView() {

    }

}