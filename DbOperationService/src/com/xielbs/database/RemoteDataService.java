package com.xielbs.database;


import com.xielbs.exception.DataOperationException;
import com.xielbs.imp.Imp_SQL_DataOperat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RemoteDataService {

	private static final Logger log = LoggerFactory.getLogger(RemoteDataService.class);

	private static String DATABASE_MYSQL = "database_mysql";

	private static String DATABASE_ORACLE = "database_oracle";
	
	private static Map<String, RemoteConnection> remoteConnections = new LinkedHashMap<String, RemoteConnection>();

	private static RemoteConnection mysqlConn, oracleConn;
	
	static {
		getDefaultConnection();
	}

	public static RemoteConnection getOracleConn() {
		oracleConn = getRemoteConnection(DATABASE_ORACLE);
		return oracleConn;
	}

	public static RemoteConnection getMysqlConn() {
		mysqlConn = getRemoteConnection(DATABASE_MYSQL);
		return mysqlConn;
	}

	/**
	 * 返回第一个配置信息的连接池
	 * 
	 * @return
	 */
	public static RemoteConnection getDefaultConnection() {
//		if(oracleConn ==null){
//			try {
//				oracleConn = getRemoteConnection(DATABASE_ORACLE);
//				if(oracleConn == null){
//					log.warn("获取获取ORACLE链接失败......");
//				}else {
//					log.info("获取获取ORACLE链接成功");
//				}
//			} catch (Exception e) {
//				log.error("获取ORACLE链接出现异常：", e);
//			}
//		}
		if(mysqlConn ==null){
			try {
				mysqlConn = getRemoteConnection(DATABASE_MYSQL);
				if(mysqlConn == null){
					log.warn("获取获取MYSQL链接失败......");
				}else {
					log.info("获取获取MYSQL链接成功");
				}
			} catch (Exception e) {
				log.error("获取MYSQL链接出现异常：", e);
			}
		}
		if(remoteConnections.isEmpty()){
			return null;
		}
		return remoteConnections.values().iterator().next();
	}

	public static RemoteConnection getRemoteConnection(String dataBaseName) {
		RemoteConnection rconn = remoteConnections.get(dataBaseName);
		if (rconn == null) {
			File cf = new File(SystemConstant.SYSTEM_CONFIG_PATH);
			if(cf.exists() && RemoteConnection.checkConfig(cf, dataBaseName)){
				rconn = new RemoteConnection(cf, dataBaseName);
				remoteConnections.put(dataBaseName, rconn);
			}
		}
		return rconn;
	}

	public static void main(String[] args) {
//		RemoteDataService.getMysqlConn();
		Imp_SQL_DataOperat queryDao = new Imp_SQL_DataOperat();
//		queryDao.setDataService(RemoteDataService.getDefaultConnection());

		List<Map> rs = null;
		try {
			for(int i=0;i<30;i++){
				rs = queryDao.querySql("select * from s_priv");
				System.out.println(rs.size());
			}
		} catch (DataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
