package dev.cecoffee.antifcm.utils.factory

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication.Companion.appContext


class DataSelectDialog {
    fun showDataSelectDialog(data: List<String>) {
        MaterialDialog(appContext).show {
            cancelable(false)
            cancelOnTouchOutside(false)
            title(text = "选择数据")
            negativeButton(text = "取消") { dialog ->
                //TODO 取消注入
            }
            listItemsSingleChoice(items = data, initialSelection = 1) { dialog, index, text ->
                //TODO 使用对应数据注入
            }
            positiveButton(text = "确定")
        }
    }

}