#### 最新版本

模块|JPush
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/JPush.svg)](https://jitpack.io/#like5188/JPush)

## 功能介绍

1、极光推送工具类。

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

在Module的gradle中加入：
```groovy
    defaultConfig {
        ...
        manifestPlaceholders = [
                JPUSH_PKGNAME: applicationId,
                JPUSH_APPKEY : "appKey", //JPush 上注册的包名对应的 Appkey.
                JPUSH_CHANNEL: "developer-default", //暂时填写默认值即可.
        ]
    }
    dependencies {
        implementation 'cn.jiguang.sdk:jpush:3.5.4'// JPush SDK 开发包。

        // 点击通知的事件和自定义消息等等都是通过LiveDataBus来发送的。所以添加LiveDataBus的引用
        implementation 'com.github.like5188.LiveDataBus:livedatabus:1.2.2'
        kapt 'com.github.like5188.LiveDataBus:livedatabus_compiler:1.2.2'

        implementation 'com.github.like5188:JPush:版本号'
    }
```

2、初始化。
```java
    JPushUtils.getInstance().init(this)
    JPushUtils.getInstance().debug(true)
```

3、使用方法参考JPushUtils工具类。

4、接收自定义消息、接收通知点击事件、接收用setAddActionsStyle()方法为自定义的通知按钮的点击事件。
```java
    // 注册LiveDataBus
    LiveDataBus.register(this, this);
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
    fun onNotificationButtonClicked(actionExtra: String) {
       Log.d("MainActivity", "onNotificationButtonClicked $actionExtra")
    }
```