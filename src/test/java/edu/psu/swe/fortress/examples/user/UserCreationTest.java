package edu.psu.swe.fortress.examples.user;


import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.AdminMgrFactory;
import org.apache.directory.fortress.core.DelAdminMgr;
import org.apache.directory.fortress.core.DelAdminMgrFactory;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.ReviewMgrFactory;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.OrgUnit.Type;
import org.apache.directory.fortress.core.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserCreationTest
{
    private static final String USER_ID = "test-user-1";
    private static final String USER_OU = "test-ou";

    private static AdminMgr adminMgr;
    private static ReviewMgr reviewMgr;
    private static DelAdminMgr delAdminMgr;
    private static OrgUnit orgUnit;


    @BeforeAll
    public static void initializeManagers() throws SecurityException
    {
        //managers created without sessions will not have ARBAC checks performed
        adminMgr = AdminMgrFactory.createInstance();
        delAdminMgr = DelAdminMgrFactory.createInstance();
        reviewMgr = ReviewMgrFactory.createInstance();
        
        //users must belong to an OU, so create the User OU first
        //OUs allow restricting what can be done (i.e. assign user to role) via ARBAC roles
        orgUnit = delAdminMgr.add( new OrgUnit( USER_OU, Type.USER ) );
        Assertions.assertEquals( USER_OU, orgUnit.getName(), "Org Unit name wrong" );
    }


    @Test
    public void createUser() throws SecurityException
    {
        //create a new user object, assigned to created OU
        User user = new User();
        user.setUserId( USER_ID );
        user.setOu( orgUnit.getName() );

        //use admin manager to create
        User newUser = adminMgr.addUser( user );
        Assertions.assertEquals( USER_OU, newUser.getOu() );
        Assertions.assertEquals( USER_ID, newUser.getUserId() );
        Assertions.assertNotNull( newUser.getInternalId() );
        
        //use review manager to find created user
        User createdUser = reviewMgr.readUser( new User(USER_ID) );
        Assertions.assertEquals( USER_OU, createdUser.getOu() );
        Assertions.assertEquals( USER_ID, createdUser.getUserId() );
        Assertions.assertNotNull( createdUser.getInternalId() );
    }


    @AfterAll
    public static void teardown() throws SecurityException
    {
        adminMgr.deleteUser( new User( USER_ID ) );
        delAdminMgr.delete( orgUnit );
    }
}