package com.like.jpush.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.NotificationMessage
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus_annotations.BusObserver
import com.like.jpush.JPushUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FlowEventBus.register(this)
        JPushUtils.getInstance().init(this)
        JPushUtils.getInstance().debug(true)
        JPushUtils.getInstance().setAddActionsStyle(
            1,
            intArrayOf(R.drawable.jpush_ic_richpush_actionbar_back, R.drawable.jpush_ic_richpush_actionbar_divider),
            arrayOf("按钮1", "按钮2"),
            arrayOf("extra1", "extra2")
        )
    }

    fun setAlias(view: View?) {
        JPushUtils.getInstance().setAlias("alias1")
    }

    fun setAlias1(view: View?) {
        JPushUtils.getInstance().setAlias("alias2")
    }

    fun deleteAlias(view: View?) {
        JPushUtils.getInstance().deleteAlias()
    }

    fun getAlias(view: View?) {
        JPushUtils.getInstance().getAlias()
    }

    fun setTags(view: View?) {
        val tags = HashSet<String>()
        tags.add("tag1")
        tags.add("tag2")
        JPushUtils.getInstance().setTags(tags)
    }

    fun setTags1(view: View?) {
        val tags = HashSet<String>()
        tags.add("20")
        JPushUtils.getInstance().setTags(tags)
    }

    fun addTags(view: View?) {
        val tags = HashSet<String>()
        tags.add("tag2")
        tags.add("tag3")
        JPushUtils.getInstance().addTags(tags)
    }

    fun addTags1(view: View?) {
        val tags = HashSet<String>()
        tags.add("tag1")
        JPushUtils.getInstance().addTags(tags)
    }

    fun deleteTags(view: View?) {
        val tags = HashSet<String>()
        tags.add("tag2")
        JPushUtils.getInstance().deleteTags(tags)
    }

    fun cleanTags(view: View?) {
        JPushUtils.getInstance().cleanTags()
    }

    fun getAllTags(view: View?) {
        JPushUtils.getInstance().getAllTags()
    }

    fun checkTagBindState(view: View?) {
        JPushUtils.getInstance().checkTag(setOf("tag2"))
    }

    fun stopPush(view: View?) {
        JPushUtils.getInstance().stopPush()
    }

    fun resumePush(view: View?) {
        JPushUtils.getInstance().resumePush()
    }

    // 接收自定义消息
    @BusObserver([JPushUtils.TAG_RECEIVE_CUSTOM_MESSAGE])
    fun onReceiveCustomMessage(customMessage: CustomMessage) {
        Log.d("MainActivity", "onReceiveCustomMessage $customMessage")
    }

    // 接收通知点击事件
    @BusObserver([JPushUtils.TAG_CLICK_NOTIFICATION])
    fun onNotificationClicked(message: NotificationMessage) {
        Log.d("MainActivity", "onNotificationClicked $message")
    }

    // 接收用setAddActionsStyle()方法为自定义的通知按钮的点击事件
    @BusObserver([JPushUtils.TAG_CLICK_NOTIFICATION_BUTTON])
    fun onNotificationButtonClicked(actionExtra: String?) {
        Log.d("MainActivity", "onNotificationButtonClicked $actionExtra")
    }
}