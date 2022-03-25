package com.xlbs.service;

import com.xlbs.ssh.SSHChannel;
import com.xlbs.ssh.SSHConstant;

/**
 * @ClassName: Start
 * @Description: TODO
 * @Author: xielbs
 * @Date: 2020/1/7 15:06
 * @Version 1.0
 */
public class SSHService {

    public static void main(String[] args) {
        SSHChannel ssh = new SSHChannel();
        ssh.goSSH(SSHConstant.MYSQL_LOCAL_PORT, SSHConstant.SSH_HOST,
                SSHConstant.SSH_PORT, SSHConstant.SSH_USERNAME, SSHConstant.SSH_PASSWORD,
                SSHConstant.MYSQL_REMOTE_HOST, SSHConstant.MYSQL_REMOTE_PORT,
                "MySql"
        );

//        ssh.goSSH(SSHConstant.REDIS_LOCAL_PORT, SSHConstant.SSH_HOST,
//                SSHConstant.SSH_PORT, SSHConstant.SSH_USERNAME, SSHConstant.SSH_PASSWORD,
//                SSHConstant.REDIS_REMOTE_HOST, SSHConstant.REDIS_REMOTE_PORT,
//                "Redis"
//        );
        System.out.println("SSH映射通道建立成功！");
    }


}
