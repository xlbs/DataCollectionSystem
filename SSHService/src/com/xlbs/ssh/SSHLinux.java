package com.xlbs.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * @ClassName: SSHLinux
 * @Description: TODO
 * @Author: xielbs
 * @Date: 2020/1/7 18:07
 * @Version 1.0
 */
public class SSHLinux {

    private static final Logger log = Logger.getLogger(SSHLinux.class);

    private static String  DEFAULT_CHART="UTF-8";

    public static Connection login(String ip, String userName, String userPwd){
        boolean flg=false;
        Connection conn = null;
        try {
            conn = new Connection(ip);
            // 连接
            conn.connect();
            // 认证
            flg = conn.authenticateWithPassword(userName, userPwd);
            if(flg){
                log.info("=========登录成功========="+conn);
                return conn;
            }
        } catch (IOException e) {
            log.error("=========登录失败========="+e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }


    public static String execute(Connection conn,String cmd){
        String result="";
        try {
            if(conn !=null){
                //打开一个会话
                Session session= conn.openSession();
                //执行命令
                session.execCommand(cmd);
                result=processStdout(session.getStdout(),DEFAULT_CHART);
                //如果为得到标准输出为空，说明脚本执行出错了
                if(StringUtils.isBlank(result)){
                    log.info("得到标准输出为空,链接conn:"+conn+",执行的命令："+cmd);
                    result = processStdout(session.getStderr(),DEFAULT_CHART);
                }else{
                    log.info("执行命令成功,链接conn:"+conn+",执行的命令："+cmd);
                }
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            log.info("执行命令失败,链接conn:"+conn+",执行的命令："+cmd+"  "+e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    private static String processStdout(InputStream in, String charset){
        InputStream  stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout,charset));
            String line=null;
            while((line=br.readLine()) != null){
                buffer.append(line+"\n");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("解析脚本出错："+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("解析脚本出错："+e.getMessage());
            e.printStackTrace();
        }
        return buffer.toString();
    }



}
