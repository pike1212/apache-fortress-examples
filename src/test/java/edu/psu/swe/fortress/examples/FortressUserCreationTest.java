package edu.psu.swe.fortress.examples;

import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.AdminMgrFactory;
import org.apache.directory.fortress.core.DelAdminMgr;
import org.apache.directory.fortress.core.DelAdminMgrFactory;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.OrgUnit.Type;
import org.apache.directory.fortress.core.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FortressUserCreationTest {

	private static final String USER_ID = "test-user-1";
	private static final String USER_OU = "test-ou";
	
	private AdminMgr adminMgr;
	private DelAdminMgr delAdminMgr;
	
	@Before
	public void initializeManagers() throws SecurityException{
		//managers created without sessions will not have ARBAC checks performed
		adminMgr = AdminMgrFactory.createInstance();
		delAdminMgr = DelAdminMgrFactory.createInstance();
	}
	
	@Test
	public void createUser() throws SecurityException{
		//users must belong to an OU, so create the User OU first
		//OUs allow restricting what can be done (i.e. assign user to role) via ARBAC roles
		OrgUnit orgUnit = delAdminMgr.add(new OrgUnit(USER_OU, Type.USER));
		Assert.assertEquals("Org Unit name wrong", USER_OU, orgUnit.getName());
		
		//create a new user object, assigned to created OU
		User user = new User();
		user.setUserId(USER_ID);
		user.setOu(orgUnit.getName());
		
		//user admin manager to create
		User newUser = adminMgr.addUser(user);
		Assert.assertEquals(USER_OU, newUser.getOu());
		Assert.assertEquals(USER_ID, newUser.getUserId());
		Assert.assertNotNull(newUser.getInternalId());
	}
}