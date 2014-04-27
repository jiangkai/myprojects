package com.yq.ftpserver.usermanager;

import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UserManagerFactory;

public class DatabaseUserManagerFactory implements UserManagerFactory {

	private String adminName;
	private DatabaseUserManagerFunctions userManageFuncs;
	private PasswordEncryptor passwordEncryptor;

	public DatabaseUserManagerFactory() {
		adminName = "admin";
		userManageFuncs = new DatabaseUserManagerFunctions();
		passwordEncryptor = new ClearTextPasswordEncryptor();
	}

	@Override
	public UserManager createUserManager() {
		DatabaseUserManager usermanager = new DatabaseUserManager(adminName,
				passwordEncryptor, userManageFuncs);
		return usermanager;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public DatabaseUserManagerFunctions getUserManageFuncs() {
		return userManageFuncs;
	}

	public void setUserManageFuncs(DatabaseUserManagerFunctions userManageFuncs) {
		this.userManageFuncs = userManageFuncs;
	}

	public PasswordEncryptor getPasswordEncryptor() {
		return passwordEncryptor;
	}

	public void setPasswordEncryptor(PasswordEncryptor passwordEncryptor) {
		this.passwordEncryptor = passwordEncryptor;
	}
}
