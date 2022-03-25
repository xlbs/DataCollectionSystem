package com.xielbs.database;

import java.io.File;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import com.xielbs.exception.DataOperationException;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接池
 * @author tanker
 */
public class RemoteConnection {

	private static final Logger log = LoggerFactory.getLogger(RemoteConnection.class);
	
	private DruidDataSource dds;

	private String dbId;

	public RemoteConnection(File configFile, String cfgName) {
		dds = instanceAliDSFromConfig(configFile, cfgName);
	}
	
//	private String dbType;

//	public String getDbType() {
//		return dbType;
//	}
//
//	public void setDbType(String dbType) {
//		this.dbType = dbType;
//	}

	public Connection getConnection() throws DataOperationException {
		if(dds == null){
			throw new DataOperationException(DataOperationException.TYPE_CONNECT_FAILURE,dbId+"：数据库连接始化失败",null);
		}
		try{
//			int busynums = dds.getActiveCount();
//			if(busynums>20){
//				PoolCheckThread.getInstance().setCurPoolSize(busynums);
//				PoolCheckThread.getInstance().setMaxPoolSize(dds.getCreateCount());
//				log.warn("当前数据库连接池总连接数:" + dds.getCreateCount() + ",使用数:" + busynums);
//			}
			return dds.getConnection();
		}catch(Exception e){
			e.printStackTrace();
			log.error(dbId+"：数据库连接始化失败,", e);
			throw new DataOperationException(DataOperationException.TYPE_CONNECT_FAILURE,"从"+dbId+"连接池获取连接失败",e);
		}
	}

	public static boolean checkConfig(File configFile,String cfgName){
		Config cfg = Config.getInstance(configFile);
		DbType dbType = DbType.parse(cfg.getValue(cfgName,SystemConstant.ITEM_KEY_DB_TYPE));
		String dbName = cfg.getValue(cfgName, SystemConstant.ITEM_KEY_DB_NAME);
		String ipPort = cfg.getValue(cfgName, SystemConstant.ITEM_KEY_IP_PORT);
		String username = cfg.getValue(cfgName,SystemConstant.ITEM_KEY_USERNAME);
		if (dbType != null && dbName != null && ipPort != null && username != null) {
			return true;
		}
		return false;
	}
	
	private DruidDataSource instanceAliDSFromConfig(File configFile,String cfgName) {
		Config cfg = Config.getInstance(configFile);
		DbType dbType = DbType.parse(cfg.getValue(cfgName,SystemConstant.ITEM_KEY_DB_TYPE));
		String dbName = cfg.getValue(cfgName, SystemConstant.ITEM_KEY_DB_NAME);
		String charSet = cfg.getValue(cfgName, SystemConstant.ITEM_KEY_CHARSET);
		String driverClassName = cfg.getValue(cfgName,SystemConstant.ITEM_KEY_DRIVER_CLASS);
		String username = cfg.getValue(cfgName,SystemConstant.ITEM_KEY_USERNAME);
		String password = cfg.getValue(cfgName,SystemConstant.ITEM_KEY_PASSWORD);
		String ipPort = cfg.getValue(cfgName, SystemConstant.ITEM_KEY_IP_PORT);
		String maxPoolSize = cfg.getValue(cfgName,SystemConstant.ITEM_KEY_MAX_POOL_SIZE);
		String initPoolSize = cfg.getValue(cfgName,SystemConstant.ITEM_KEY_INIT_POOL_SIZE);

		if(dbType==null || dbName==null || ipPort==null) {
			log.error("处理连接池数出现异常：");
			return null;
		}

		this.dbId = dbType.getName()+":"+ipPort+":"+dbName;

		int maxSize = 10;
		int initSize = 5;
		try{
			maxSize = maxPoolSize == null ? 30 : Integer.valueOf(maxPoolSize);
			if(maxSize>30){
				maxSize = 30;
			}
			if(maxSize<3){
				maxSize = 3;
			}
			initSize = initPoolSize == null ? 5 : Integer.valueOf(initPoolSize);
			if(initSize < 0 || initSize > maxSize){
				initSize = maxSize/2;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("处理连接池数出现异常：", e);
		}

		DruidDataSource dds = new DruidDataSource();
		if (dbType!=null && (null == driverClassName || driverClassName.trim().length() == 0)) {
			driverClassName = dbType.getDriverClassName();
		}
		dds.setDriverClassName(driverClassName);
		dds.setDbType(dbType.getName());
		dds.setUrl(composeURL(dbType, dbName, charSet, ipPort));
		dds.setUsername(username);
		dds.setPassword(password);
		dds.setMaxActive(maxSize);
		dds.setMinIdle(initSize);
		dds.setInitialSize(initSize);
		dds.setPoolPreparedStatements(true);
		dds.setMaxPoolPreparedStatementPerConnectionSize(20);
		dds.setTestWhileIdle(true);
		dds.setValidationQuery(dbType.getDriverClassName());
		// 当连接池用完时客户端调用getConnection()后等待获取新连接的时间,超时后将抛出SQLException,如设为0则无限期等待.单位毫秒,默认为0
		dds.setMaxWait(10*1000);
		// 隔多少毫秒检查所有连接池中的空闲连接,默认为0表示不检查;
		dds.setTimeBetweenEvictionRunsMillis(60000);
        // 配置一个连接在池中最小生存的时间，单位是毫秒;
		dds.setMinEvictableIdleTimeMillis(30000);
		return dds;
	}

	protected String composeURL(DbType dbType, String dbName,String charSet, String ipPort) {
//		this.dbType = dbType.getName();
		String url = null;
		switch (dbType) {
			case ORACLE:
				String[] ipPorts = ipPort.split(",");
				if (ipPorts.length == 1) {
					url = "jdbc:oracle:thin:@" + ipPorts[0] + ":" + dbName;
				} else {
					url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST = ";
					for (String ip_port : ipPorts) {
						String[] ip_port_arr = ip_port.split(":");
						url = url + "(ADDRESS=(PROTOCOL=TCP)(HOST="+ ip_port_arr[0] + ")(PORT =" + ip_port_arr[1]+ "))";
					}
					url = url+ "(LOAD_BALANCE=yes))(CONNECT_DATA =(SERVICE_NAME = "+ dbName + ")))";
				}
				break;
			case SYBASE:
				url = "jdbc:sybase:Tds:" + ipPort + "/" + dbName + charSet;
				break;
			case MYSQL_5:
			case MYSQL_8:
				url = "jdbc:mysql://" + ipPort + "/" + dbName +"?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
				break;
			default:
				break;
		}
		return url;
	}


	/**
	 * 从数据库连接池获取连接
	 * @param tryNum   重试次数
	 * @return
	 * @throws DataOperationException
	 */
	public Connection getConnection(int tryNum) throws DataOperationException {
		DataOperationException de = null;
		Connection c=null;
		for(int i = 0; i < tryNum; i++){
			try{
				c = getConnection();
				break;
			}catch(DataOperationException e){
				try {
					TimeUnit.MILLISECONDS.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				de = e;
			}
		}
		if (de == null) {
			return c;
		}
		throw de;
	}




}
