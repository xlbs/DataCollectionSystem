package com.xlbs.ssh;

/**
 * @ClassName: SSHConstant
 * @Description: TODO
 * @Author: xielbs
 * @Date: 2020/1/7 15:22
 * @Version 1.0
 */
public class SSHConstant {

    /**
     * 跳板机地址
     */
    public static final String SSH_HOST = "120.77.175.218";

    /**
     * 跳板机端口
     */
    public static final int SSH_PORT = 22;

    /**
     * 跳板机登入用户名
     */
    public static final String SSH_USERNAME = "root";

    /**
     * 跳板机登入密码
     */
    public static final String SSH_PASSWORD = "Kzy20190702";


    /**
     * mysql目标主机地址
     */
    public static final String MYSQL_REMOTE_HOST = "rm-wz9xi9vhtw9vb7hie.mysql.rds.aliyuncs.com";

    /**
     * mysql目标主机端口
     */
    public static final int MYSQL_REMOTE_PORT = 3306;

    /**
     * mysql将要映射的本地端口
     */
    public static final int MYSQL_LOCAL_PORT = 3307;


    /**
     * redis目标主机地址
     */
    public static final String REDIS_REMOTE_HOST = "r-wz96814ca977e0c4.redis.rds.aliyuncs.com";

    /**
     * redis目标主机端口
     */
    public static final int REDIS_REMOTE_PORT = 6379;

    /**
     * redis将要映射的本地端口
     */
    public static final int REDIS_LOCAL_PORT = 6378;


}
