package com.paojiao.sdk.http;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/22 下午2:44
 */
public class Api {

    private static final String BASE_URL = "http://ng.sdk.paojiao.cn/";
    private static final String UC_BASE_URL = "http://uc.paojiao.cn/";//用户中心域名

    //用户登录接口
    public static final String LOGIN  = BASE_URL + "api/user/login.do";
    //快速登录
    public static final String QUICK_LOGIN = BASE_URL + "api/user/random.do";
    //注册
    public static final String REG = BASE_URL + "api/user/reg.do";
    //获取热点消息
    public static final String FLOAT_HOTSPOT = BASE_URL + "api/floatHotspot.do";
    //TOKEN有效性验证
    public static final String TOKEN_VERIFY = BASE_URL + "api/user/token.do";
    //支付
    public static final String PAY = BASE_URL + "pay/payGame.do";
    //用户中心
    public static final String UCENTER_URL = UC_BASE_URL + "wap/index.do";
    //游戏检查更新
    public static final String GAME_CHECK_VERSION  = BASE_URL + "api/checkUpdate.do";
    //启动统计接口,应用启动时调用
    public static final String STAT_URL = BASE_URL + "stat/boot.do";
    //退出统计接口,用于退出时调用
    public static final String EXIT_STAT_URL = BASE_URL + "stat/exit.do";
    //token鉴权地址
    public static final String INFO_URL = BASE_URL + "api/user/token.do";
    //找回密码
    public static final String RESET_PWD = UC_BASE_URL+ "wap/user/findPassword.do";
    //绑定手机
    public static final String BIND_MOBILE = UC_BASE_URL+ "wap/user/preBindMobile.do";
}
