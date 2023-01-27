package dev.cecoffee.antifcm.ui.dialog

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import dev.cecoffee.antifcm.utils.factory.DataHandler

class DataSelectDialog {

    private var data: List<String> = listOf()

    private val dialog = MaterialDialog(ModuleApplication.appContext)
        .cancelable(true)
        .cancelOnTouchOutside(false)
        .title(text = "选择数据")
        .negativeButton(text = "取消") { dialog ->
            DataHandler.cancelHook = true
            dialog.dismiss()
        }
        .listItemsSingleChoice(items = data, initialSelection = 1) { dialog, index, text ->
            dialog.dismiss()
            DataHandler.selectedData = text as String // 选取对应数据
        }
        .positiveButton(text = "确定")

    fun showDataSelectDialog(data: List<String>) {
        this.data = data
        dialog.show()
    }

    fun dismissDataSelectDialog() {
        dialog.dismiss()
    }

}