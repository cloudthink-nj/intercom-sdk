# 可视对讲通用解决方案

[![](https://www.jitpack.io/v/icloudthink/intercom-sdk.svg)](https://www.jitpack.io/#icloudthink/intercom-sdk)
```groovy
// add in your root build.gradle at the end of repositories
allprojects {
    repositories { 
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
implementation 'com.github.icloudthink:intercom-sdk:$latest_version'
```

## 可视对讲App Demo(app-demo目录)
需要实现IIntercomService.aidl中的接口
建议通话界面采用悬浮窗方案，不会有两个APK activity跳转情况
若需要扩展方法，双方约定好action以及json结构即可

```java
public interface AidlAction {
    /* *
     * 打开配置界面
     * data 为null
     */
    String ACTION_SHOW_CONFIG = "action_show_config";

    /* *
     * 配置参数
     * data 为 {"building":8,"unit":2,"floor":3,"room":1,"familyNum":"0802-0301"}
     */
    String ACTION_SET_CONFIG = "action_set_config";

    /* *
     * 打开拨号界面
     * data 为null
     */
    String ACTION_SHOW_DIALER = "action_show_dialer";

    /* *
     * 获取单元机数据
     * data 为null
     */
    String ACTION_GET_UNIT_DEVICE = "action_get_unit_device";

    /* *
     * 异步返回单元机数据 "[{"id":"","name":""},{"id":"","name":""}]
     */
    String ACTION_CALLBACK_UNIT_DEVICE = "action_callback_unit_device";

    /* *
     * 呼叫单元机
     * 呼叫前会先请求ACTION_GET_UNIT_DEVICE获取单元机设备信息
     * 若获取单元机设备信息返回有数据，则传入返回data里的id
     * 单元机  data为{"id":""}
     */
    String ACTION_CALL_UNIT_DEVICE = "action_call_unit_device";

    /* *
     * 获取管理机数据
     * data 为null
     */
    String ACTION_GET_CENTER_DEVICE = "action_get_center_device";

    /* *
     * 异步返回管理机数据 "[{"id":"","name":""},{"id":"","name":""}]"
     */
    String ACTION_CALLBACK_CENTER_DEVICE = "action_callback_center_device";

    /* *
     * 呼叫管理机
     * 呼叫前会先请求ACTION_GET_CENTER_DEVICE获取管理机设备信息，
     * 若获取管理机设备信息返回有数据，则传入返回data里的id
     * 管理机  data为{"id":""}
     */
    String ACTION_CALL_CENTER_DEVICE = "action_call_center_device";

    /* *
     * 紧急呼叫
     * 紧急呼叫  data为{"type":0}
     * 0: 手动报警面板
     * 1: 烟雾报警器
     * 2: 燃气报警器
     * 3: 红外报警器
     */
    String ACTION_EMERGENCY_HELP = "action_emergency_help";

    /* *
     * 预约电梯
     * 向设备发送呼梯命令。
     * data: {"type":0, "currFloor":"", "destFloor":"", "direction":""}
     * type: 0.打开呼梯界面 data: {"type":0}
     *       1.目的模式: 确切的当前楼层以及期望要到达的楼层 data: {"type":1, "currFloor":"5", "destFloor":"B1"}
     *       2.外呼模式: 确切的当前楼层以及期望电梯运行的方向 data: {"type":2, "currFloor":"5", "direction":"true/false"}
     *            curFloor: 当前楼层
     *            destFloor: 目标楼层
     *            direction: true：请求上行 false:请求下行
     */
    String ACTION_CALL_ELEVATOR = "action_call_elevator";

    /* *
     * 通过IIntercomCallback 异步返回物业公告通知数据 jsonString
     */
    String ACTION_CALLBACK_CENTER_MESSAGE = "action_callback_center_message";

    /* *
     * 通过IIntercomCallback 异步返回未接来电 jsonString
     */
    String ACTION_CALLBACK_MISS_INTERCOM = "action_callback_miss_intercom";
}
```
### 异步回调
```aidl
interface IIntercomCallback {
    //回调
    void onIntentAction(String action, String data);
}
```

