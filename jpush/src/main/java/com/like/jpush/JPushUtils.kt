package com.like.jpush

import android.app.Notification
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import cn.jpush.android.api.*
import com.like.jpush.TagAliasOperatorHelper.*
import com.like.livedatabus.LiveDataBus

class JPushUtils private constructor() {

    private object Holder {
        val instance = JPushUtils()
    }

    companion object {
        const val TAG_CLICK_NOTIFICATION = "TAG_CLICK_NOTIFICATION"
        const val TAG_RECEIVE_CUSTOM_MESSAGE = "TAG_RECEIVE_CUSTOM_MESSAGE"
        const val TAG_CLICK_NOTIFICATION_BUTTON = "TAG_CLICK_NOTIFICATION_BUTTON"

        fun getInstance() = Holder.instance

        /**
         * 处理自定义按钮点击事件
         */
        fun onMultiActionClicked(actionExtra: String) {
            LiveDataBus.post(TAG_CLICK_NOTIFICATION_BUTTON, actionExtra)
        }

        /**
         * 处理收到的自定义消息
         */
        fun onMessage(customMessage: CustomMessage) {
            LiveDataBus.post(TAG_RECEIVE_CUSTOM_MESSAGE, customMessage)
        }

        /**
         * 处理打开通知
         */
        fun onNotifyMessageOpened(message: NotificationMessage) {
            LiveDataBus.post(TAG_CLICK_NOTIFICATION, message)
        }
    }

    private lateinit var mContext: Context

    fun init(context: Context) {
        mContext = context.applicationContext
        JPushInterface.init(mContext)
    }

    /**
     * 该接口需在 init 接口之前调用，避免出现部分日志没打印的情况。多进程情况下建议在自定义的 Application 中 onCreate 中调用。
     * debug 为 true 则会打印 debug 级别的日志，false 则只会打印 warning 级别以上的日志
     */
    fun debug(debug: Boolean) {
        JPushInterface.setDebugMode(debug)
    }

    /**
     * 停止接收推送。
     *
     * 1、收不到推送消息
     * 2、极光推送所有的其他 API 调用都无效，不能通过 JPushInterface.init 恢复，需要调用 resumePush 恢复。
     */
    fun stopPush() {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        if (!JPushInterface.isPushStopped(mContext)) {
            JPushInterface.stopPush(mContext)
        }
    }

    /**
     * 重新接收推送，会接收到缓存的推送
     */
    fun resumePush() {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        if (JPushInterface.isPushStopped(mContext)) {
            JPushInterface.resumePush(mContext)
        }
    }

    fun getRegistrationID(): String {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        return JPushInterface.getRegistrationID(mContext)
    }

    /**
     * 设置通知提示方式 - 基础属性
     *
     * @param id                        通知样式编号，大于0。在 Portal 上发送通知时，首先选择推送平台为 Android，然后展开“可选设置”，开发者可指定当前要推送的通知的样式编号。
     * @param statusBarDrawable         状态栏显示的通知图标
     * @param notificationFlags         消失方式。[Notification.FLAG_AUTO_CANCEL]等等
     * @param notificationDefaults      提示方式。[Notification.DEFAULT_SOUND]、[Notification.DEFAULT_VIBRATE]等等
     * @param developerArg
     */
    fun setStyleBasic(
            @IntRange(from = 1, to = 1000) id: Int,
            @DrawableRes statusBarDrawable: Int = 0,
            notificationFlags: Int = 16,
            notificationDefaults: Int = -2,
            developerArg: String = "developerArg0"
    ) {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        val builder = BasicPushNotificationBuilder(mContext)
        builder.statusBarDrawable = statusBarDrawable
        builder.notificationFlags = notificationFlags
        builder.notificationDefaults = notificationDefaults
        builder.developerArg0 = developerArg
        JPushInterface.setPushNotificationBuilder(id, builder)
    }

    /**
     * 设置通知栏样式 - 定义通知栏Layout
     *
     * @param id                        通知样式编号，大于0。在 Portal 上发送通知时，首先选择推送平台为 Android，然后展开“可选设置”，开发者可指定当前要推送的通知的样式编号。
     * @param layout                    自定义布局id
     * @param layoutIconId              自定义布局中放置icon的ImageView控件的id
     * @param layoutTitleId             自定义布局中title的TextView控件的id
     * @param layoutContentId           自定义布局中content的TextView控件的id
     * @param layoutIconDrawable        自定义布局中icon的Drawable资源id
     * @param statusBarDrawable         状态栏显示的通知图标
     * @param notificationFlags         消失方式。[Notification.FLAG_AUTO_CANCEL]等等
     * @param notificationDefaults      提示方式。[Notification.DEFAULT_SOUND]、[Notification.DEFAULT_VIBRATE]等等
     * @param developerArg
     */
    fun setStyleCustom(
            @IntRange(from = 1, to = 1000) id: Int,
            @LayoutRes layout: Int,
            @IdRes layoutIconId: Int,
            @IdRes layoutTitleId: Int,
            @IdRes layoutContentId: Int,
            @DrawableRes layoutIconDrawable: Int = 0,
            @DrawableRes statusBarDrawable: Int = 0,
            notificationFlags: Int = 16,
            notificationDefaults: Int = -2,
            developerArg: String = "developerArg0"
    ) {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        val builder = CustomPushNotificationBuilder(mContext, layout, layoutIconId, layoutTitleId, layoutContentId)
        builder.statusBarDrawable = statusBarDrawable
        builder.layoutIconDrawable = layoutIconDrawable
        builder.notificationFlags = notificationFlags
        builder.notificationDefaults = notificationDefaults
        builder.developerArg0 = developerArg
        JPushInterface.setPushNotificationBuilder(id, builder)
    }

    /**
     * 为自定义通知添加按钮。注意：是覆盖操作
     *
     * 用户点击了通知栏中自定义的按钮。（SDK 3.0.0 以上版本支持）
     * 使用普通通知的开发者不需要配置此 receiver action。
     * 只有开发者使用了 MultiActionsNotificationBuilder 构建携带按钮的通知栏的通知时，可通过该 action 捕获到用户点击通知栏按钮的操作，并自行处理。
     *
     * @param id                    通知样式编号，大于0。在 Portal 上发送通知时，首先选择推送平台为 Android，然后展开“可选设置”，开发者可指定当前要推送的通知的样式编号。
     * @param drawableIdResArray    按钮图片资源id
     * @param nameArray             按钮文字
     * @param extraArray            扩展数据，用于判断点击了哪个按钮
     */
    fun setAddActionsStyle(
            @IntRange(from = 1, to = 1000) id: Int,
            drawableIdResArray: IntArray,
            nameArray: Array<String>,
            extraArray: Array<String>
    ) {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        if (drawableIdResArray.isEmpty() || drawableIdResArray.size != nameArray.size || drawableIdResArray.size != extraArray.size) {
            throw IllegalArgumentException("drawableIdResArray nameArray extraArray is invalid")
        }
        val builder = MultiActionsNotificationBuilder(mContext)
        for (i in drawableIdResArray.indices) {
            builder.addJPushAction(drawableIdResArray[i], nameArray[i], extraArray[i])
        }
        JPushInterface.setPushNotificationBuilder(id, builder)
    }

    /**
     * 设置别名。这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置
     *
     * 为安装了应用程序的用户，取个别名来标识。以后给该用户 Push 消息时，就可以用此别名来指定。
     * 每个用户只能指定一个别名。
     *
     * @param alias 有效的别名组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|
     * alias 命名长度限制为 40 字节。（判断长度需采用 UTF-8 编码）
     */
    fun setAlias(alias: String) {
        if (!isValidAlias(alias)) {
            return
        }
        onAliasAction(ACTION_SET, alias)
    }

    /**
     * 查询别名
     */
    fun getAlias() {
        onAliasAction(ACTION_GET)
    }

    /**
     * 删除别名
     */
    fun deleteAlias() {
        onAliasAction(ACTION_DELETE)
    }

    private fun onAliasAction(action: Int, alias: String? = null) {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        val tagAliasBean = TagAliasBean()
        tagAliasBean.action = action
        sequence++
        tagAliasBean.alias = alias
        tagAliasBean.isAliasAction = true
        TagAliasOperatorHelper.getInstance().handleAction(mContext, sequence, tagAliasBean)
    }

    private fun isValidAlias(alias: String): Boolean {
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(mContext, "alias cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!ExampleUtil.isValidTagAndAlias(alias)) {
            Toast.makeText(mContext, "Invalid format", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * 设置标签。这个接口是覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。
     *
     * 为安装了应用程序的用户，打上标签。其目的主要是方便开发者根据标签，来批量下发 Push 消息。
     * 可为每个用户打多个标签。
     *
     * 设置 tags 时，如果其中一个 tag 无效，则整个设置过程失败
     *
     * @param tags  每次调用至少设置一个 tag，覆盖之前的设置，不是新增
     * 有效的标签组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|
     * 每个 tag 命名长度限制为 40 字节，最多支持设置 1000 个 tag，且单次操作总长度不得超过 5000 字节。（判断长度需采用 UTF-8 编码）
     * 单个设备最多支持设置 1000 个 tag。App 全局 tag 数量无限制。
     */
    fun setTags(tags: Set<String>) {
        if (!isValidTags(tags)) {
            return
        }
        onTagAction(ACTION_SET, tags)
    }

    /**
     * 新增标签
     */
    fun addTags(tags: Set<String>) {
        if (!isValidTags(tags)) {
            return
        }
        onTagAction(ACTION_ADD, tags)
    }

    /**
     * 删除标签
     */
    fun deleteTags(tags: Set<String>) {
        if (!isValidTags(tags)) {
            return
        }
        onTagAction(ACTION_DELETE, tags)
    }

    /**
     * 查询指定 tag 与当前用户绑定的状态
     */
    fun checkTag(tags: Set<String>) {
        onTagAction(ACTION_CHECK, tags)
    }

    /**
     * 查询所有标签
     */
    fun getAllTags() {
        onTagAction(ACTION_GET)
    }

    /**
     * 清除标签
     */
    fun cleanTags() {
        onTagAction(ACTION_CLEAN)
    }

    private fun onTagAction(action: Int, tags: Set<String>? = null) {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        val tagAliasBean = TagAliasBean()
        tagAliasBean.action = action
        sequence++
        tagAliasBean.tags = tags
        tagAliasBean.isAliasAction = false
        TagAliasOperatorHelper.getInstance().handleAction(mContext, sequence, tagAliasBean)
    }

    private fun isValidTags(tags: Set<String>): Boolean {
        if (tags.isEmpty()) {
            Toast.makeText(mContext, "tag cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        tags.forEach {
            if (!ExampleUtil.isValidTagAndAlias(it)) {
                Toast.makeText(mContext, "Invalid format", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    /**
     * 设置手机号码。该接口会控制调用频率，频率为 10s 之内最多 3 次
     *
     * @param mobileNumber    手机号码。如果传 null 或空串则为解除号码绑定操作。
     * 限制：只能以 “+” 或者 数字开头；后面的内容只能包含 “-” 和数字。
     */
    fun setMobileNumber(mobileNumber: String?) {
        if (!::mContext.isInitialized) throw UnsupportedOperationException("you must call init(Context) first")
        if (TextUtils.isEmpty(mobileNumber)) {
            Toast.makeText(mContext, "will clear last set phone number", Toast.LENGTH_SHORT).show()
        }
        if (!ExampleUtil.isValidMobileNumber(mobileNumber)) {
            Toast.makeText(mContext, "Invalid format", Toast.LENGTH_SHORT).show()
            return
        }
        sequence++
        TagAliasOperatorHelper.getInstance().handleAction(mContext, sequence, mobileNumber)
    }

}
