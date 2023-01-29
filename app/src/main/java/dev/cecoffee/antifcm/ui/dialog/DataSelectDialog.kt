package dev.cecoffee.antifcm.ui.dialog

import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import dev.cecoffee.antifcm.hook.HookHandler.BypassSDK.appContext
import dev.cecoffee.antifcm.utils.factory.DataHandler

class DataSelectDialog {

    private var data: List<String> = listOf()

    private val dialog = MaterialDialog(ModuleApplication.appContext)
        .cancelable(true)
        .cancelOnTouchOutside(false)
        .title(text = "选择数据")
        .negativeButton(text = "取消") { dialog ->
            DataHandler.cancelHook = true
            DataHandler.dialogDisplayed = false
            Toast.makeText(appContext, "恢复原版登录", Toast.LENGTH_SHORT)
            dialog.dismiss()
        }
        .listItemsSingleChoice(items = data, initialSelection = 1) { dialog, index, text ->
            dialog.dismiss()
            DataHandler.dialogDisplayed = false
            DataHandler.selectedData = text as String // 选取对应数据
        }
        .positiveButton(text = "确定")

    fun showDataSelectDialog(data: List<String>) {
        this.data = data
        dialog.show()
        DataHandler.dialogDisplayed = true
    }

    fun dismissDataSelectDialog() {
        dialog.dismiss()
        DataHandler.dialogDisplayed = false
    }

}