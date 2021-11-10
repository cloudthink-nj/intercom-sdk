package cn.net.cloudthink.smartmirror.intercom;

/**
 *  注意: 部分版本可能不支持AIDL中文注释
 *  通用返回状态码：
 */
import cn.net.cloudthink.smartmirror.intercom.IIntercomCallback;
interface IIntercomService {
   /**
     * 客户端获取可视对讲厂家英文名称
     *
     * @return 可视对讲厂家英文名称
     */
    String getBrandName();

    /**
     * 客户端获取AIDL接口版本 （适配接口升级）
     *
     * @return lib_base模块的versionCode
     */
    int getVersionCode();

    /**
     * 进入配置界面
     */
    int openConfigView();

    /**
     * 进入拨号盘界面
     */
    int openDialerView();

    /**
     * 呼叫单元机
     */
    int callUnitDevice();

    /**
     * 呼叫管理中心
     */
    int callManagement();

    /**
     * 紧急呼叫
     */
    int emergencyHelp(int alarmType);

    /**
     * 向设备发送呼梯命令。
     * @param type
     *            1.目的模式: 确切的当前楼层以及期望要到达的楼层
     *            2.外呼模式: 确切的当前楼层以及期望电梯运行的方向
     *        curFloor 当前楼层
     *        destFloor 目标楼层
     *        direction
     *            true：请求上行 false:请求下行
     * @return 呼梯执行状态，见通用状态码
     */
    int callElevator(int type, int curFloor, int destFloor, boolean direction);

    //回调
    void addCallback(IIntercomCallback cb);
    void removeCallback(IIntercomCallback cb);
    //功能接口
    void requestAction(String action, String data);
}