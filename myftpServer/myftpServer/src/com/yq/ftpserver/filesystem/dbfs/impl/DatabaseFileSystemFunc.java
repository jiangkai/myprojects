package com.yq.ftpserver.filesystem.dbfs.impl;

import java.util.List;

import org.apache.ftpserver.ftplet.User;

import com.yq.ftpserver.database.DataSource;
import com.yq.ftpserver.database.ResultList;
import com.yq.ftpserver.filesystem.dbfs.DatabaseFileSystemFactory;

public class DatabaseFileSystemFunc {
		
	DataSource dataSource = DataSource.getDataSource();
			
	public void createDirectoryForUser(User user,String path)
	{
		createDirectoryForUser(user.getName(),path);
	}
	
	public void createDirectoryForUser(String username,String path)
	{
		if(path.equals(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR))
			creatHomeDirectory(username);
		else
		{
			if(path.endsWith(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR))
				path = path.substring(0, path.length()-1);
			createDirectoryRecusively(username,path);
		}
	}
	
	public void createDirectoryRecusively(String username,String path)
	{
		//if(path.length()==0)
	}
	
	public void creatHomeDirectory(String username)
	{
		ResultList resultList = dataSource.query("select * from "
					+DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME+" where username='"+username+"'");
		if(resultList.isEmpty())
			dataSource.execute("insert into "+DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+" (name,username) values ('"
					+DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR+"','"
					+username+"')");
	}
	
	public void createDirectoryForParentDirectory(String username,String Directory,String path)
	{
		//TODO
	}
	
	private List<Integer> parsePath(String username,String path)
	{
		return null;
	}
	
	
	
	public List<String> getSubDirecories(User user,String directory)
	{
		//TODO
		return null;
	}
	
}
