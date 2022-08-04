# 可视对讲通用解决方案

## 可视对讲App Demo(app-demo目录)

需要实现IIntercomService.aidl中的接口 若需要扩展方法，双方约定好action以及json结构即可
Service的Action 必须和demo一致

```java
public interface AidlAction {
    /* *
     * 打开配置界面
     * data 为null
     */
    String ACTION_SHOW_CONFIG = "action_show_config";

    /* *
     * 配置房号、管理机号参数
     * data 为 {"building":8,"unit":2,"floor":3,"room":1,"familyNum":"08020301","manageNum":"99990001","extendNum":1}
     */
    String ACTION_CONFIG_ADDRESS = "action_config_address";

    /* *
     * 配置网络参数
     * data 为 {"ip":"192.168.201.10", "gateway":"192.168.201.1", "mask":"255.255.224.0","dns":"8.8.8.8", "server":"192.168.200.10"}
     */
    String ACTION_CONFIG_NETWORK = "action_config_network";

    /* *
     * 配置电梯控制
     * data 为 {"number":2,"ipAddrs":["192.168.201.253","192.168.201.254"]}
     */
    String ACTION_CONFIG_ELEVATOR = "action_config_elevator";

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
     * 呼叫管理机
     * 只有一台的话，直接进入呼叫管理机界面，无需返回多台管理机数据
     * 多台管理机的话，返回所有管理机数据，并让用户选择，再次带上id进行呼叫管理机请求
     * 管理机  data为 null 或者 {"id":""}
     */
    String ACTION_CALL_CENTER_DEVICE = "action_call_center_device";

    /* *
     * 异步返回管理机数据 "[{"id":"","name":""},{"id":"","name":""}]"
     */
    String ACTION_CALLBACK_CENTER_DEVICE = "action_callback_center_device";

    /* *
     * 紧急呼叫
     * 1: 门磁提醒
     * 2: 燃气报警
     * 3: 红外提醒
     * 4: SOS报警
     * 5: 水浸报警
     * 6: 烟感报警
     * 7: CO报警
     * 8: 逗留提醒
     * 9: 水阀提醒
     * 紧急呼叫  data为{"type":0, "defenceId": 0-7, "name":"SOS报警"}
     */
    String ACTION_EMERGENCY_HELP = "action_emergency_help";

    /* *
     * 预约电梯
     * 向设备发送呼梯命令。
     * data: {"type":0, "destFloor":"", "direction":""}
     * type: 0.打开呼梯界面 data: {"type":0}
     *       1和2 打开呼梯界面，并执行对应动作
     *       1.目的模式: 确切的当前楼层以及期望要到达的楼层 data: {"type":1, "destFloor":"1"}
     *       2.外呼模式: 确切的当前楼层以及期望电梯运行的方向 data: {"type":2, "direction":"true/false"}
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

