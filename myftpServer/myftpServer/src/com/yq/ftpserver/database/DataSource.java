package com.yq.ftpserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataSource {

	String username = "root";
	String password = "";
	String dburl = "jdbc:mysql://127.0.0.1:3306/ftpserver";

	String driver = "com.mysql.jdbc.Driver";

	Connection conn = null;
	Statement statement = null;

	private static DataSource dataSource = null;
	
	private DataSource(){};
	
	public static DataSource getDataSource()
	{
		if(dataSource==null)
		{
			dataSource = new DataSource();
			dataSource.connect();
		}
		return dataSource;
	}
	
	private void connect() {
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(dburl, username,
					password);
			if (!conn.isClosed())
				statement = conn.createStatement();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultList query(String sql) {
		try {
			ResultSet rs = statement.executeQuery(sql);
			ResultList resultList = new ResultList(rs);
			rs.close();
			return resultList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void execute(String sql){
		try {
			System.out.println("executing "+sql);
			statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public void close() {
		try {
			if (!statement.isClosed())
				statement.close();
			if (!(conn==null) && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args)
	{
		DataSource datasource = DataSource.getDataSource();
		datasource.connect();
		//List<String> names = datasource.query("select * from directory");
		//for(String name:names)
			//System.out.println(name);
		datasource.close();	
		
		
		String test = new String("1234");
		System.out.println(test.substring(0, test.length()-1));
	}
	
}
