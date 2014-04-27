package com.yq.ftpserver.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSource {

	private final Logger LOG = LoggerFactory.getLogger(DataSource.class);

	String username;
	String password;
	String dburl;

	String driver;
	
	
	Connection conn = null;
	Statement statement = null;

	private static DataSource dataSource = null;

	private DataSource() throws IOException {
		Properties prop = new Properties();
		InputStream in = getClass().getResourceAsStream("/test/resources/myFtpServer.properties"); 
		prop.load(in);
		
		username = prop.getProperty("myftpserver.jdbc.username");
		password = prop.getProperty("myftpserver.jdbc.password");
		dburl = prop.getProperty("myftpserver.jdbc.url");
		driver = prop.getProperty("myftpserver.jdbc.driverClassName");
	};

	public static DataSource getDataSource() {
		if (dataSource == null) {
			try {
				dataSource = new DataSource();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataSource.connect();
		}
		return dataSource;
	}

	private void connect() {
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(dburl, username,
					password);
			System.out.println("connnect to " + dburl + " OK!");
			if (!conn.isClosed())
				statement = conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isResultEmpty(String sql) {
		ResultList resultList = query(sql);
		return resultList.isEmpty();
	}

	public Object query(String sql, String objName) {
		ResultList rl = query(sql);
		if (rl.isEmpty())
			throw new IllegalArgumentException(objName + " dose not exist");

		return rl.get(0).get(objName);
	}

	public Map queryForUnique(String sql) {
		ResultList rl = query(sql);
		return rl.get(0);
	}
	
	
	public ResultList query(String sql) {
		try {
			System.out.println("execute query:" + sql);
			ResultSet rs = statement.executeQuery(sql);

			ResultList resultList = new ResultList(rs);
			rs.close();
			return resultList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void executeUpdate(String sql) {
		try {
			System.out.println("execute update:" + sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (!statement.isClosed())
				statement.close();
			if (!(conn == null) && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		DataSource datasource = DataSource.getDataSource();
		datasource.connect();
		// List<String> names = datasource.query("select * from directory");
		// for(String name:names)
		// System.out.println(name);
		datasource.close();

		String test = new String("1234");
		System.out.println(test.substring(0, test.length() - 1));
	}

}
