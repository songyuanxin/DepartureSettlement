package com.syx.constants;

/**
 * @author 宋远欣
 * @date 2022/3/21
 **/
public interface Contants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * RMI 远程方法调用
     */
    public static final String LOOKUP_RMI = "rmi://";

    /**
     * LDAP 远程方法调用
     */
    public static final String LOOKUP_LDAP = "ldap://";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功
     */
//    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;


    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = { "java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework.jndi" };

    //分界线----------------
    /**
     * 令牌
     */
    String TOKEN = "token";
    /**
     * 系统用户类型
     */
    String USER_ADMIN = "0";
    String USER_NORMAL = "1";
    /**
     * 返回检验码
     */
    String UNIQUE = "0";
    String NOT_UNIQUE = "1";
    /**
     * 有效状态
     */
    String STATUS_TRUE = "0";
    String STATUS_FALSE = "1";

    /**
     * 删除状态
     */
    String DEL_FALSE = "0";
    String DEL_TRUE = "1";
    /**
     * 菜单类型
     */
    String MENU_TYPE_M = "M";
    String MENU_TYPE_C = "C";
    String MENU_TYPE_F = "F";
    /**
     * 入库单状态 1未提交2待审核 3审核通过 4审核失败 5作废 6 入库成功
     */
    String STOCK_PURCHASE_STATUS_1 = "1";
    String STOCK_PURCHASE_STATUS_2 = "2";
    String STOCK_PURCHASE_STATUS_3 = "3";
    String STOCK_PURCHASE_STATUS_4 = "4";
    String STOCK_PURCHASE_STATUS_5 = "5";
    String STOCK_PURCHASE_STATUS_6 = "6";
    /**
     * 入库状态
     */
    String STOCK_STORAGE_0 = "0";
    String STOCK_STORAGE_1 = "1";
    /**
     * 默认预警值
     */
    Long DEFAULT_WARNING = 50L;

    /**
     * 排班状态
     */
    String SCHEDULING_FLAG_TRUE = "0";
    String SCHEDULING_FLAG_FALSE = "1";
    /**
     * 是否完善信息
     */
    String IS_FINAL_FALSE = "0";
    String IS_FINAL_TRUE = "1";
    /**
     * 挂号单状态
     */
    String REG_STATUS_0 = "0"; //待支付
    String REG_STATUS_1 = "1"; //待就诊
    String REG_STATUS_2 = "2"; //就诊中
    String REG_STATUS_3 = "3"; //就诊完成
    String REG_STATUS_4 = "4"; //已退号
    String REG_STATUS_5 = "5"; //已作废
    /**
     * 处方类型
     */
    String CO_TYPE_MEDICINES = "0";
    String CO_TYPE_CHECK = "1";
    /**
     * 支付单状态状态，0未支付,1已支付，2支付超时
     */
    String ORDER_STATUS_0 = "0";
    String ORDER_STATUS_1 = "1";
    String ORDER_STATUS_2 = "2";
    /**
     * 订单子项目支付状态
     * 0未支付，1已支付，2，已退费  3，已完成
     */
    String ORDER_DETAILS_STATUS_0 = "0";
    String ORDER_DETAILS_STATUS_1 = "1";
    String ORDER_DETAILS_STATUS_2 = "2";
    String ORDER_DETAILS_STATUS_3 = "3";
    /**
     * 检查状态  0 检查中   1检查完成
     */
    String RESULT_STATUS_0 = "0";
    String RESULT_STATUS_1 = "1";

    /**
     * 退费单状态，订单状态0未退费  1 退费成功 2退费失败
     */
    String ORDER_BACKFEE_STATUS_0 = "0";
    String ORDER_BACKFEE_STATUS_1 = "1";
    String ORDER_BACKFEE_STATUS_2 = "2";
    /**
     * 支付类型
     */
    String PAY_TYPE_0 = "0";//现金
    String PAY_TYPE_1 = "1";//支付宝

    /**
     * redis的字典前缀
     */
    String DICT_REDIS_PROFIX = "dict:";

    /**
     * 登陆状态  0 成功  1失败
     */
    String LOGIN_SUCCESS = "0";
    String LOGIN_ERROR = "1";
    /**
     * 登陆类型0系统用户1患者用户
     */
    String LOGIN_TYPE_SYSTEM = "0";
    String LOGIN_TYPE_PATIENT = "1";
    /**
     * ID前缀
     */
    String ID_PROFIX_CG = "CG";
    String ID_PROFIX_CH = "CH";
    String ID_PROFIX_CO = "CO";//病历
    String ID_PROFIX_ITEM = "ITEM";//病历详情
    String ID_PROFIX_ODC = "ODC";//收费订单前缀
    String ID_PROFIX_ODB="ODB";//退费订单前缀
    public static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"classpath:/META-INF/resources/", "classpath:/resources/","classpath:/static/", "classpath:/public/" };;
}
