package dev.cecoffee.antifcm.utils.factory

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView


/**
 * 构造对话框
 * @param isUseBlackTheme 是否使用深色主题
 * @param initiate 对话框方法体
 */
fun Context.showDialog(isUseBlackTheme: Boolean = false, initiate: DialogBuilder.() -> Unit) =
    DialogBuilder(context = this, isUseBlackTheme).apply(initiate).show()

/**
 * 对话框构造器
 * @param context 实例
 * @param isUseBlackTheme 是否使用深色主题 - 对 AndroidX 风格无效
 */
class DialogBuilder(val context: Context, private val isUseBlackTheme: Boolean) {

    private var instanceAndroidX: androidx.appcompat.app.AlertDialog.Builder? = null // 实例对象
    private var instanceAndroid: android.app.AlertDialog.Builder? = null // 实例对象

    private var dialogInstance: Dialog? = null // 对话框实例
    private var customLayoutView: View? = null // 自定义布局


    /** 设置对话框不可关闭 */
    fun noCancelable() {
        runInSafe { instanceAndroid?.setCancelable(false) }
    }

    /** 设置对话框标题 */
    var title
        get() = ""
        set(value) {
            runInSafe { instanceAndroid?.setTitle(value) }
        }

    /** 设置对话框消息内容 */
    var msg
        get() = ""
        set(value) {
            runInSafe {
                instanceAndroid?.setMessage(value)
            }
        }

    /** 设置进度条对话框消息内容 */
    var progressContent
        get() = ""
        set(value) {
            if (customLayoutView == null)
                customLayoutView = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER or Gravity.START
                    addView(ProgressBar(context))
                    addView(View(context).apply { layoutParams = ViewGroup.LayoutParams(20.dp(context), 5) })
                    addView(TextView(context).apply {
                        tag = "progressContent"
                        text = value
                    })
                    setPadding(20.dp(context), 20.dp(context), 20.dp(context), 20.dp(context))
                }
            else customLayoutView?.findViewWithTag<TextView>("progressContent")?.text = value
        }

    /**
     * 设置对话框确定按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun confirmButton(text: String = "确定", callback: () -> Unit = {}) {
       runInSafe { instanceAndroid?.setPositiveButton(text) { _, _ -> callback() } }
    }

    /**
     * 设置对话框取消按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun cancelButton(text: String = "取消", callback: () -> Unit = {}) {
        runInSafe { instanceAndroid?.setNegativeButton(text) { _, _ -> callback() } }
    }

    /**
     * 设置对话框第三个按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun neutralButton(text: String = "更多", callback: () -> Unit = {}) {
        runInSafe { instanceAndroid?.setNeutralButton(text) { _, _ -> callback() } }
    }

    /** 取消对话框 */
    fun cancel() = dialogInstance?.cancel()

    /** 显示对话框 */
    internal fun show() =
        runInSafe {
            instanceAndroid?.create()?.apply {
                customLayoutView?.let { setView(it) }
                window?.setBackgroundDrawable(
                    GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        if (isUseBlackTheme) intArrayOf(0xFF2D2D2D.toInt(), 0xFF2D2D2D.toInt())
                        else intArrayOf(Color.WHITE, Color.WHITE)
                    ).apply {
                        shape = GradientDrawable.RECTANGLE
                        gradientType = GradientDrawable.LINEAR_GRADIENT
                        cornerRadius = 15.dpFloat(this@DialogBuilder.context)
                    })
                dialogInstance = this
            }?.show()
        }
}