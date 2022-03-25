package com.xlbs.ssh;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @ClassName: SSHChannel
 * @Description: TODO
 * @Author: xielbs
 * @Date: 2020/1/6 16:35
 * @Version 1.0
 */
public class SSHChannel {

    private Session session;

    private Channel channel;

    /**
     *
     * @param localPort 本地映射端口 建议mysql 3306 redis 6379
     * @param sshHost 跳板机地址
     * @param sshPort 跳板机端口(一般为22)
     * @param sshUserName 跳板机登入用户名
     * @param sshPassWord 跳板机登入密码
     * @param remoteHost 目标主机地址
     * @param remotePort 目标主机端口
     */
    public void goSSH(int localPort, String sshHost, int sshPort,
                      String sshUserName, String sshPassWord,
                      String remoteHost, int remotePort, String type) {
        try {
            JSch jsch = new JSch();
            // 登陆跳板机
            session = jsch.getSession(sshUserName, sshHost, sshPort);
            session.setPassword(sshPassWord);
            session.setConfig("StrictHostKeyChecking", "no");
            // 保持活跃状态 - 30s 发送一次心跳
            session.setServerAliveInterval(30);
//            session.setServerAliveCountMax(86400);
            session.setTimeout(86400);
            session.connect();
            // 建立通道
            channel = session.openChannel("session");
            channel.connect();
            // 通过ssh连接到mysql机器
            int localForwardPort = session.setPortForwardingL(localPort, remoteHost, remotePort);
            System.out.println(type+"映射通道建立成功，映射端口号："+localForwardPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭
     */
    public void close() {
        if (session != null && session.isConnected() ) {
            session.disconnect();
        }

        if (channel != null && session.isConnected() ) {
            channel.disconnect();
        }
    }

}
