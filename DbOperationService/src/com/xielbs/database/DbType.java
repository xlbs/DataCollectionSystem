package com.xielbs.database;

import java.util.EnumSet;
import java.util.Iterator;

public enum DbType {

	ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", "select * from dual"),
	SYBASE("sybase", "com.sybase.jdbc2.jdbc.SybDataSource", null),
	MYSQL_5("mysql_5","com.mysql.jdbc.Driver","SELECT 1"),
	MYSQL_8("mysql_8","com.mysql.cj.jdbc.Driver","SELECT 1"),
	SQLSERVICE("sqlService","","SELECT * from dual");

	private String name, driverClassName, validationQuery;

	private DbType(String name, String driverClassName, String validationQuery) {
		this.name = name;
		this.driverClassName = driverClassName;
		this.validationQuery = validationQuery;
	}
	
	public String getDriverClassName() {
		return driverClassName;
	}
	public String getName() {
		return name;
	}
	
	public String getValidationQuery() {
		return validationQuery;
	}
	
	public static DbType parse(String dbTypeName) {
		for (Iterator<DbType> iterator = EnumSet.allOf(DbType.class).iterator(); iterator.hasNext();) {
			DbType dbtype = iterator.next();
			if (dbtype.getName().equalsIgnoreCase(dbTypeName)) {
				return dbtype;
			}
		}
		return null;
	}
	
}
