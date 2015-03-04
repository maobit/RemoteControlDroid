package com.qhe.pcontroldroid.utils;

public class CommandId {
	// 给系统管理的命令定义ID
	public static final int SYSTEM_CONNECT = 1;
	public static final int SYSTEM_LOCK = 2;
	public static final int SYSTEM_LOGOUT = 3;
	public static final int SYSTEM_STANDBY = 4;
	public static final int SYSTEM_SLEEP = 5;
	public static final int SYSTEM_REBOOT = 6;
	public static final int SYSTEM_SHUTDWON = 7;

    public static final String[] COMMAND_ARRAY = {"", "", "锁定", "注销", "待机", "休眠", "重启", "关机" };

    // 共享屏幕，截图传输图片
    public static final int SHARE_SCREEN = 9;
}
