package com.yq.ftpserver.usermanager;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;

public class DatabaseUserManager extends AbstractUserManager {

	/**
	 * app_user's ddl:
	 * CREATE TABLE `app_user` (
		  `f_id` bigint(20) NOT NULL AUTO_INCREMENT,
		  `f_enabled` bit(1) NOT NULL,
		  `f_maxidletimesec` int,
		  `f_password` varchar(255) DEFAULT NULL,
		  `f_username` varchar(255) NOT NULL,
		  `f_homedir` varchar(255) DEFAULT NULL,
		  PRIMARY KEY (`f_id`),
		  UNIQUE KEY `f_username` (`f_username`)
		) ENGINE=MyISAM AUTO_INCREMENT=43 DEFAULT CHARSET=gbk;
	 */
	private DatabaseUserManagerFunctions userManageFuncs;

	public DatabaseUserManager(String adminName,
			PasswordEncryptor passwordEncryptor) {
		super(adminName, passwordEncryptor);
		userManageFuncs = new DatabaseUserManagerFunctions();
	}

	public DatabaseUserManager(String adminName,
			PasswordEncryptor passwordEncryptor,
			DatabaseUserManagerFunctions userManageFuncs) {
		this(adminName, passwordEncryptor);
	}

	@Override
	public User getUserByName(String username){
		if(doesExist(username))
		{
			User user = userManageFuncs.getUserByName(username);
			return user;
		}
		return null;
	}

	@Override
	public String[] getAllUserNames() throws FtpException {
		return userManageFuncs.getAllUserNames();
	}

	@Override
	public void delete(String username) throws FtpException {
		if(doesExist(username))
			userManageFuncs.delete(username);
	}

	@Override
	public void save(User user) throws FtpException {
		userManageFuncs.save(user);
	}

	@Override
	public boolean doesExist(String username){
		return userManageFuncs.doesExist(username);
	}

	@Override
	public User authenticate(Authentication authentication)
			throws AuthenticationFailedException{
	       if (authentication instanceof UsernamePasswordAuthentication) {
	            UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

	            String user = upauth.getUsername();
	            String password = upauth.getPassword();

	            if (user == null) {
	                throw new AuthenticationFailedException("Authentication failed");
	            }

	            if (password == null) {
	                password = "";
	            }

	            String storedPassword = userManageFuncs.getPasswordByUsername(user);

	            if (storedPassword == null) {
	                // user does not exist
	                throw new AuthenticationFailedException("Authentication failed");
	            }

	            if (getPasswordEncryptor().matches(password, storedPassword)) {
	                return getUserByName(user);
	            } else {
	                throw new AuthenticationFailedException("Authentication failed");
	            }

	        } else if (authentication instanceof AnonymousAuthentication) {
	            if (doesExist("anonymous")) {
	                return getUserByName("anonymous");
	            } else {
	                throw new AuthenticationFailedException("Authentication failed");
	            }
	        } else {
	            throw new IllegalArgumentException(
	                    "Authentication not supported by this user manager");
	        }
	}

}
