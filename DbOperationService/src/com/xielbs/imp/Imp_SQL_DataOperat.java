package com.xielbs.imp;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import com.xielbs.exception.DataOperationException;
import com.xielbs.serviceinterface.I_SQL_DataOperat;

import com.xielbs.database.DbUtils;
import com.xielbs.database.RemoteConnection;
import com.xielbs.database.RemoteDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Imp_SQL_DataOperat implements I_SQL_DataOperat {

	private static Logger log = LoggerFactory.getLogger(Imp_SQL_DataOperat.class);

	private RemoteConnection dataService;

	public Imp_SQL_DataOperat() {
		dataService = RemoteDataService.getMysqlConn();
	}

	public Imp_SQL_DataOperat(RemoteConnection dataService) {
		this.dataService = dataService;
	}

	public RemoteConnection getDataService() {
		return dataService;
	}

	public void setDataService(RemoteConnection dataService) {
		this.dataService = dataService;
	}

	public boolean deleteSql(String sql) throws DataOperationException {
		log.debug(sql);
		Connection conn = dataService.getConnection();
		PreparedStatement stat = null;
		boolean flag = true;
		try {
			stat = conn.prepareStatement(sql);
			stat.execute();
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stat);
		} catch (SQLException e) {
			flag = false;
			throw new DataOperationException(DataOperationException.TYPE_EXESQL,e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stat);
		}
		return flag;
	}

	public List<Map> querySql(String sql) throws DataOperationException {
		log.debug(sql);
		Date date = new Date();
		List<Map> values = new ArrayList<Map>();
		Connection conn = dataService.getConnection();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = conn.prepareStatement(sql);
			rs = stat.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (null != rs && rs.next()) {
				Map map = new LinkedHashMap();
				for (int i = 1; i <= columnCount; i++) {
					int type = rsmd.getColumnType(i);
					Object value = null;
					//循环获取ResultSet中的数据的部分代码
					switch (type) {
					case Types.TIMESTAMP: // 如果是时间戳类型
					case Types.DATE:
						try{
							value = rs.getTimestamp(i);
						}catch (Exception e) {
							// TODO: handle exception
							value = rs.getObject(i);
						}
					    break;
					case Types.TIME: // 如果是时间类型
					    value = rs.getTime(i);
					    break;
					default: //日期类型可以使用getObject()来获取
					    value = rs.getObject(i);
					    break;
					}
					map.put(rsmd.getColumnLabel(i), value);
				}
				values.add(map);
			}
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		} catch (SQLException e) {
			throw new DataOperationException(DataOperationException.TYPE_EXESQL,e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		}
		log.debug("查询处理耗时："+(new Date().getTime() - date.getTime())+"ms");
		return values;
	}

	@Override
	public boolean savesql(String sql) throws DataOperationException {
		log.debug(sql);
		Connection conn = dataService.getConnection();
		PreparedStatement stat = null;
		boolean flag = true;
		try {
			stat = conn.prepareStatement(sql);
			stat.execute();
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		} catch (SQLException e) {
			flag = false;
			throw new DataOperationException(DataOperationException.TYPE_EXESQL,e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		}
		return flag;
	}

	@Override
	public boolean updateSql(String sql) throws DataOperationException {
		log.debug(sql);
		Connection conn = dataService.getConnection();
		PreparedStatement stat = null;
		boolean flag = true;
		try {
			stat = conn.prepareStatement(sql);
			stat.execute();
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		} catch (SQLException e) {
			flag = false;
			throw new DataOperationException(DataOperationException.TYPE_EXESQL,e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		}
		return flag;
	}

	@SuppressWarnings("rawtypes")
	public int getMaxId(String tableName, String id_name) {
		String sql = "select max(" + id_name + ") s from " + tableName;
		int size = 0;
		try {
			List<?> s = querySql(sql);
			size = Integer.valueOf(String.valueOf(((Map) s.iterator().next()).get("s")));
		} catch (DataOperationException e) {
			e.printStackTrace();
		}
		return size;
	}

	@Override
	public boolean saveOrUpdateSql(String sql) throws DataOperationException {
		log.debug(sql);
		Connection conn = dataService.getConnection();
		PreparedStatement stat = null;
		boolean flag = true;
		try {
			stat = conn.prepareStatement(sql);
			stat.execute();
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		} catch (SQLException e) {
			flag = false;
			throw new DataOperationException(DataOperationException.TYPE_EXESQL,e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		}
		return flag;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map get(String sql) {
		List<Map> data;
		try {
			data = querySql(sql);
			if (data != null && data.size() > 0) {
				return data.iterator().next();
			}
		} catch (DataOperationException e) {
			e.printStackTrace();
		}
		return new LinkedHashMap();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map> queryPreparedStatementSql(String psSql, List<Object> params)throws DataOperationException {
		log.debug(psSql);
		List<Map> values = new ArrayList<Map>();
		Connection conn = dataService.getConnection();
		PreparedStatement stat = null;
		ResultSet rs = null;
		int parameterIndex = 1;
		try {
			stat = conn.prepareStatement(psSql);
			for (Object param : params) {
				stat.setObject(parameterIndex, param);
				parameterIndex++;
			}
			rs = stat.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (null != rs && rs.next()) {
				Map map = new LinkedHashMap();
				for (int i = 1; i <= columnCount; i++) {
					int type = rsmd.getColumnType(i);
					Object value = null;
					//循环获取ResultSet中的数据的部分代码
					switch (type) {
					case Types.TIMESTAMP: // 如果是时间戳类型
					case Types.DATE:
						try{
							value = rs.getTimestamp(i);
						}catch (Exception e) {
							// TODO: handle exception
							value = rs.getObject(i);
						}
					    break;
					case Types.TIME: // 如果是时间类型
					    value = rs.getTime(i);
					    break;
					default: //日期类型可以使用getObject()来获取
					    value = rs.getObject(i);
					    break;
					}
					map.put(rsmd.getColumnLabel(i), value);
				}
				values.add(map);
			}
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		} catch (SQLException e) {
			throw new DataOperationException(DataOperationException.TYPE_BATCH_EXCEPTION,"查询异常",e);
		}
		return values;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xielbs.cl7107.dataservice.I_SQL_DataOperat#save(java.util.Map,
	 * java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean save(Map map, String psSql) throws DataOperationException {
		log.debug(psSql);
		Connection conn = dataService.getConnection();
		PreparedStatement stat = null;
		boolean rs = false;
		int parameterIndex = 1;
		try {
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(psSql);
			for (Object key : map.keySet()) {
				stat.setObject(parameterIndex, map.get(key));
				parameterIndex++;
			}
			rs = stat.execute();
			conn.commit();
			return true;
		} catch (Exception e) {
			rs = false;
		} finally {
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		}
		return rs;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xielbs.cl7107.dataservice.I_SQL_DataOperat#save(java.util.Map,
	 * java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	private Throwable save(Map map, String psSql,Connection conn)/* throws DataOperationException */{
		PreparedStatement stat = null;
		int parameterIndex = 1;
		try {
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(psSql);
			for (Object key : map.keySet()) {
				stat.setObject(parameterIndex, map.get(key));
				parameterIndex++;
			}
			stat.execute();
			conn.commit();
			return null;
		} catch (SQLException e) {
			return e;
		} catch(Throwable t){
			return t;
		}finally {
			DbUtils.closeQuietly(stat);
		}
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public int[] save(String sql, List<Map> objects, List<String> idFeilds)throws DataOperationException {
		log.debug(sql);
		long l = System.currentTimeMillis();
		Connection connection = dataService.getConnection(3);
		PreparedStatement ps = null;
		int count = 0;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sql);
			for (Map map : objects) {
				int parameterIndex = 1;
				for (Object key : map.keySet()) {
					ps.setObject(parameterIndex, map.get(key));
					parameterIndex++;
				}
				ps.addBatch();
				if (++count % batchSize == 0) {
					ps.executeBatch();
					ps.clearBatch();
				}
			}
			if (count % batchSize != 0) {
				ps.executeBatch();
				ps.clearBatch();
			}
			connection.commit();
			log.debug(objects.size() + "条记录存储时间" + (System.currentTimeMillis() - l) + "ms");
		} catch (SQLException e) {
			log.debug("批处理异常"+e.getMessage());
//			try {
//				connection.rollback();
//				connection.setAutoCommit(true);
//				DbUtils.closeQuietly(ps);
//				DbUtils.closeQuietly(connection);
//				if (e instanceof BatchUpdateException) {
//					l = System.currentTimeMillis();
//					int devideIndex = objects.size() / 2;
//					List<Map> valsExists = new ArrayList<Map>();
//					List<Map> valsErrors = new ArrayList<Map>();
//					if(devideIndex <=100){
//						iteratorSave(valsExists, valsErrors, sql, objects, batchSize, 0, objects.size());
//					}else{
//						save(valsExists,valsErrors, sql, objects, batchSize, 0,devideIndex);
//						save(valsExists,valsErrors, sql, objects, batchSize, devideIndex,objects.size());
//					}
//					String tableName = sql.substring(12,sql.indexOf("("));
//					if (valsExists.size() > 0) {
//						String updateSql = DbUtils.orgnizeUpdateSql(valsExists.get(0), tableName, idFeilds,dataService.getDbType());
//						this.update(updateSql, valsExists, idFeilds);
//					}
//					if(valsExists.size()>objects.size()/2)
//						log.warn(tableName +"," + valsExists.size()+"+"+valsErrors.size() + "条重复+失败/共" + objects.size()+ "条处理时间" + (System.currentTimeMillis() - l)+ "ms");
//					if(objects.size()>50 && valsExists.size()==objects.size()){
//						int mc=0;
//						for(Map map:objects){
//							mc++;
//							if(mc>10)break;
//							log.warn(tableName+","+map);
//						}
//					}
//					int uc = valsExists.size();
//					int ec = valsErrors.size();
//					valsExists.clear();
//					valsErrors.clear();
//					return new int[]{uc,ec};
//				}else{
//					log.error("非批处理异常,"+e);
//					throw new DataOperationException(DataOperationException.TYPE_EXESQL,"数据库异常:" + e.getMessage(),e);
//				}
//			}catch(DataOperationException e2){
//				throw e2;
//			}catch (Exception e1) {
//				e1.printStackTrace();
//				throw new DataOperationException(DataOperationException.TYPE_EXESQL,"数据库异常:" + e.getMessage(),e);
//			}
		} finally {
			DbUtils.closeQuietly(ps);
			DbUtils.closeQuietly(connection);
		}
		return new int[]{0,0};
	}

	/**
	 * 二分法存库，10万条重复记录2分钟以内处理，越少越快
	 * 
	 * @param ps
	 * @param objects
	 * @param connection
	 * @param batchSize
	 * @param start
	 * @param end
	 * @return
	 * @throws DataOperationException
	 */
	@SuppressWarnings("rawtypes")
	private boolean save(List<Map> valsExists,List<Map> valsErrors,String sql, List<Map> objects,int batchSize, int start, int end) throws DataOperationException {
		int rangSize = objects.size() / (2 * 2 * 2 * 2);
		if ((end - start) <= rangSize || rangSize == 0) {
			iteratorSave(valsExists,valsErrors, sql, objects, batchSize, start, end);
			return true;
		}
		Connection connection = dataService.getConnection(3);
		int devideIndex = (end - start) / 2 + start;
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sql);
			int count = 0;
			for (int index = start; index < end; index++) {
				Map map = objects.get(index);
				int parameterIndex = 1;
				for (Object param : map.values()) {
					ps.setObject(parameterIndex, param);
					parameterIndex++;
				}
				ps.addBatch();
				if (++count % batchSize == 0) {
					ps.executeBatch();
					ps.clearBatch();
				}
			}
			if (count % batchSize != 0) {
				ps.executeBatch();
				ps.clearBatch();
			}
			connection.commit();
		} catch (Exception e) {
			try {
				connection.rollback();
				connection.setAutoCommit(true);
				DbUtils.closeQuietly(ps);
				DbUtils.closeQuietly(connection);
				save(valsExists,valsErrors, sql, objects, batchSize, start, devideIndex);
				save(valsExists,valsErrors, sql, objects, batchSize, devideIndex, end);
			}catch(DataOperationException e2){
				throw e2;
			} catch (Exception e1) {
				throw new DataOperationException(DataOperationException.TYPE_EXESQL,"数据库异常:" + e1.getMessage(),e1);
			}
		} finally {
			DbUtils.closeQuietly(ps);
			DbUtils.closeQuietly(connection);
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public void iteratorSave(List<Map> valsExists,List<Map> valsError, String sql,List<Map> objects, int batchSize, int start, int end) throws DataOperationException {
		log.debug(sql);
		Connection conn = dataService.getConnection(3);
		try{
			for (int index = start; index < end; index++) {
				Throwable t = save(objects.get(index), sql,conn);
				if (t!=null) {
					if(t instanceof SQLException){
						SQLException se = (SQLException)t;
						if("23000".equals(se.getSQLState())){
							valsExists.add(objects.get(index));
							log.debug("重复主键:"+sql);
							log.debug("data:"+objects.get(index));
						}else{
							valsError.add(objects.get(index));
							log.error("SQL:"+sql);
							log.error("ErrorData:"+objects.get(index));
							log.error("ErrorData:SQLState="+se.getSQLState()+",ErrorCode="+se.getErrorCode()+",Message="+se.getMessage());
						}
					}else{
						log.error("非SQLException做重复主键处理,"+t.getMessage());
						valsError.add(objects.get(index));
					}
				}
			}
		}finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			DbUtils.closeQuietly(conn);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean update(Map map, String psSql) throws DataOperationException {
		log.info(psSql);
		Connection conn = dataService.getConnection();
		PreparedStatement stat = null;
		boolean rs = false;
		int parameterIndex = 1;
		try {
			stat = conn.prepareStatement(psSql);
			for (Object param : map.values()) {
				stat.setObject(parameterIndex, param);
				parameterIndex++;
			}
			rs = stat.execute();
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		} catch (Exception e) {
			rs = false;
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(stat);
			DbUtils.closeQuietly(conn);
		}
		return rs;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean update(String updateSql, List<Map> objects,List<String> idFields) throws DataOperationException {
		Connection connection = dataService.getConnection();
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(updateSql);
			int count = 0;
			for (Map map : objects) {
				int parameterIndex = 1;
				for (Object key : map.keySet()) {
					if (idFields.contains(key)) {
						continue;
					}
					ps.setObject(parameterIndex, map.get(key));
					parameterIndex++;
				}
				for (String key : idFields) {
//					if (dataService.getDbType().contains("mysql")) {
//						ps.setObject(parameterIndex, map.get(key));
//					} else {
						ps.setObject(parameterIndex, map.get(key));
//					}
					parameterIndex++;
				}
				ps.addBatch();
				if (++count % batchSize == 0) {
					ps.executeBatch();
				}
			}
			if (count % batchSize != 0) {
				ps.executeBatch();
			}
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DbUtils.closeQuietly(ps);
			DbUtils.closeQuietly(connection);
		}
		return true;
	}

	@Override
	public boolean execTranSqls(List<String> sqls) throws DataOperationException {
		Connection connection = dataService.getConnection();
		List<PreparedStatement> pss = new ArrayList<PreparedStatement>();
		try {
				connection.setAutoCommit(false);
				for(String sql : sqls){
					System.out.println(sql);
					PreparedStatement	ps = connection.prepareStatement(sql);
					pss.add(ps);
					ps.execute();
				}
				connection.commit();
				return true;
			}catch (Exception e) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}finally{
				for(PreparedStatement ps :pss){
					DbUtils.closeQuietly(ps);
				}
				DbUtils.closeQuietly(connection);
			}
		return false;
	}
}
