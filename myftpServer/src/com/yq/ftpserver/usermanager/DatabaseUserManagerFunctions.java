package com.yq.ftpserver.usermanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.util.StringUtils;

import com.yq.ftpserver.database.DataSource;
import com.yq.ftpserver.database.ResultList;
import com.yq.ftpserver.filesystem.dbfs.DatabaseFileSystemFactory;

public class DatabaseUserManagerFunctions {
	DataSource dataSource;
	
	public DatabaseUserManagerFunctions()
	{
		dataSource = DataSource.getDataSource();
	}
	
	public boolean doesExist(String username)
	{
		String sql = "select * from app_user where f_username='"
				+ username + "'";
		
		return !dataSource.isResultEmpty(sql);
	}

	public void save(User user) {
		if(!StringUtils.hasText(user.getName()))
            throw new NullPointerException("User name is null.");
		
		int enabled=user.getEnabled()?1:0;
		
		String homeDir=user.getHomeDirectory();
		if(!StringUtils.hasText(homeDir))
			homeDir=DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR;
			
		
		String sql = "insert into "+DatabaseUser.USER_TABLE_NAME
				+"(f_enabled,f_maxidletimesec,f_password,f_username,f_homedir)"
				+"values("+enabled+","+user.getMaxIdleTime()+",'"
				+user.getPassword()+"','"+user.getName()+"',"
				+homeDir+"')";
		
		dataSource.executeUpdate(sql);
	}

	public void delete(String username) {
		String sql = "delete from "+DatabaseUser.USER_TABLE_NAME
				+ " where f_username='"+username+"'";
		
		dataSource.executeUpdate(sql);
	}

	public String[] getAllUserNames() {
		String sql = "select f_username from "
				+DatabaseUser.USER_TABLE_NAME;
		ResultList resultList = dataSource.query(sql);
		
		String[] allUserNames = new String[resultList.size()];
		for(int i=0;i<allUserNames.length;i++)
			allUserNames[i] = (String)resultList.get(i).get("f_username");
		
		return allUserNames;
	}

	public User getUserByName(String username) {
		BaseUser user = new BaseUser();
		String sql = "select * from "+DatabaseUser.USER_TABLE_NAME
				+" where f_username='"+username+"'";
			
		Map result = dataSource.queryForUnique(sql);
		
		user.setName((String)result.get("f_username"));
		if((Boolean)result.get("f_enabled"))
			user.setEnabled(true);
		else
			user.setEnabled(false);
		
		String homedir = (String)result.get("f_homedir");
		if(!StringUtils.hasText(homedir))
			user.setHomeDirectory(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR);
		else
			user.setHomeDirectory(homedir);
		
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
		
        authorities.add(new ConcurrentLoginPermission(0,0));
        authorities.add(new TransferRatePermission(0, 0));

        user.setAuthorities(authorities);
        
        user.setMaxIdleTime((Integer)result.get("f_maxidletimesec"));
		return user;
	}
	
	
	public String getPasswordByUsername(String username)
	{
		String sql = "select * from "+DatabaseUser.USER_TABLE_NAME
				+" where f_username='"+username+"'";
		return (String)dataSource.query(sql, "f_password");
	}
}
