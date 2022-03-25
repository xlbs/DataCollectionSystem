package com.xielbs.serviceinterface;

import com.xielbs.exception.DataOperationException;

import java.util.List;
import java.util.Map;

public interface I_SQL_DataOperat {
	
	final int batchSize = 500;
	/**
	 * 以sql语句保存
	 * @param sql
	 * @return
	 * @throws DataOperationException
	 */
	public boolean savesql(String sql) throws DataOperationException;
	/**
	 * 通过sql语句进行删除
	 * @param sql
	 * @return
	 * @throws DataOperationException
	 */
	public boolean deleteSql(String sql) throws DataOperationException;
	/**
	 * 通过sql语句查询，返回list<map> map为一行记录的key-value
	 * @param sql
	 * @return
	 * @throws DataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> querySql(String sql) throws DataOperationException;
	/**
	 * 更新sql
	 * @param sql
	 * @return
	 * @throws DataOperationException
	 */
	public boolean updateSql(String sql) throws DataOperationException;
	/**
	 * 保存或者更新
	 * @param sql
	 * @return
	 * @throws DataOperationException
	 */
	public boolean saveOrUpdateSql(String sql) throws DataOperationException;
	/**
	 * 获取最大id
	 * @param tableName
	 * @param id_name
	 * @return
	 */
	public int getMaxId(String tableName, String id_name);
	/**
	 * 通过PreparedStatement传参数进行查询
	 * @param ps
	 * @param params
	 * @return
	 * @throws DataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> queryPreparedStatementSql(String ps, List<Object> params) throws DataOperationException;
	/**
	 * 获取sql查询的第一个结果
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map get(String sql);
	/**
	 * 保存可传参数的sql
	 * @param map
	 * @param psSql
	 * @return
	 * @throws DataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public boolean save(Map map, String psSql)throws DataOperationException;

	/**
	 * 批量保存可传参数的同一类型sql，如果有重复记录也会处理
	 * @param sql
	 * @param objects
	 * @param idFields
	 * @return  返回重复主键数和失败数量
	 * @throws DataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public int[] save(String sql, List<Map> objects, List<String> idFields) throws DataOperationException;
	/**
	 * 更新可传参数的sql
	 * @param map
	 * @param psSql
	 * @return
	 * @throws DataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public boolean update(Map map, String psSql) throws DataOperationException;
	/**
	 * 批量更新可传参数的统一类型sql
	 * @param psSql
	 * @param objects
	 * @param idFields
	 * @return
	 * @throws DataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public boolean update(String psSql, List<Map> objects, List<String> idFields) throws DataOperationException;
	/**
	 * 批处理sql
	 * @param sqls
	 * @return
	 * @throws DataOperationException
	 */
	public boolean execTranSqls(List<String> sqls) throws DataOperationException;
}
