package com.xielbs.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DbUtils {

	public static List<String> dateStrings = Arrays.asList(new String[] { "data_date" });
	/**
	 * 根据map数据，表明，数据库类型生成sql
	 * @param map
	 * @param tableName
	 * @param dbType
	 * @return
	 */
	public static String orgnizeSql(Map map, String tableName, String dbType) {
		String sql = "insert into " + tableName + " (";
		for (Object key : map.keySet()) {
			sql += key + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ") values (";
		for (Object key : map.keySet()) {
			if (dateStrings.contains(key)
					&& dbType.equals(DbType.ORACLE.getName())) {
				sql += "to_date(?,'yyyy-mm-dd hh24:mi:ss'),";
			} else {
				sql += "?,";
			}
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ")";
		return sql;
	}
	/**
	 * 根据map，表名，数据库类型，主键生成更新语句
	 * @param map
	 * @param tableName
	 * @param idFields
	 * @param dbType
	 * @return
	 */
	public static String orgnizeUpdateSql(Map map, String tableName,
			List<String> idFields, String dbType) {
		String sql = "update " + tableName + " set ";
		for (Object key : map.keySet()) {
			if (idFields.contains(key)) {
				continue;
			}
			sql += key + "=?,";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += " where ";
		for (String key : idFields) {
			if (dateStrings.contains(key)
					&& dbType.equals(DbType.ORACLE.getName())) {
				sql += key + "=to_date(?,'yyyy-mm-dd hh24:mi:ss') and ";
			} else {
				sql += key + "=? and ";
			}
		}
		sql = sql.substring(0, sql.length() - 4);
		return sql;
	}

	public static String orgnizeIds(Collection nodeIds) {
		String ids = "";
		for (Object id : nodeIds) {
			if (!"undefined".equals(id)) {
				ids += "," + String.valueOf(id);
			}
		}
		if (!ids.equals("")) {
			ids = ids.substring(1);
		} else {
			return "-1";
		}
		return ids;
	}

	public static void closeQuietly(Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	}

	public static void closeQuietly(ResultSet rs) {
		if (rs == null)
			return;
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	}

	public static void closeQuietly(Statement stmt) {
		if (stmt == null)
			return;
		try {
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	}
}
