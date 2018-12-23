package edu.psu.swe.fortress.examples;

import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.AdminMgrFactory;
import org.apache.directory.fortress.core.DelAdminMgr;
import org.apache.directory.fortress.core.DelAdminMgrFactory;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.OrgUnit.Type;
import org.apache.directory.fortress.core.model.PermObj;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PermissionCreationTest
{
    private static final String PERM_OU = "test-perm-ou";
    private static final String PERM_OBJ = "test.object-1";
    private static final String PERM_OPERATION = "operation-1";
    private static final String PERM_OBJECT_ID = "object-id-1";
    private static final String PERM_ABSTRACT_NAME = PERM_OBJ + "." + PERM_OPERATION;

    private static AdminMgr adminMgr;
    private static DelAdminMgr delAdminMgr;
    private static OrgUnit orgUnit;

    @BeforeAll
    public static void initializeManagers() throws SecurityException
    {
        //managers created without sessions will not have ARBAC checks performed
        adminMgr = AdminMgrFactory.createInstance();
        delAdminMgr = DelAdminMgrFactory.createInstance();

        //permission objects must belong to an OU, so create the Perm OU first
        //OUs allow restricting what can be done (i.e. assign permission to role) via ARBAC roles
        orgUnit = delAdminMgr.add( new OrgUnit( PERM_OU, Type.PERM ) );
        Assertions.assertEquals( PERM_OU, orgUnit.getName(), "Org Unit name wrong" );
    }

    @Test
    public void createPermissionOperation() throws SecurityException
    {
        //create a new perm object object, assigned to created OU
        PermObj createdObj = adminMgr.addPermObj( new PermObj( PERM_OBJ, PERM_OU ) );
        Assertions.assertEquals( PERM_OBJ, createdObj.getObjName() );
        Assertions.assertEquals( PERM_OU, createdObj.getOu() );

        //create an operation for the object
        Permission createdPermission = adminMgr.addPermission( new Permission( PERM_OBJ, PERM_OPERATION ) );
        Assertions.assertEquals( PERM_OBJ, createdPermission.getObjName() );
        Assertions.assertEquals( PERM_OPERATION, createdPermission.getOpName() );
        Assertions.assertEquals( PERM_ABSTRACT_NAME, createdPermission.getAbstractName() );
        Assertions.assertNotNull( createdPermission.getInternalId() );

        //permission can also be created with an object id, this explodes the number or permissions, 
        //but allows finer grained control over objects. An object can be any id in your system.
        Permission createdPermissionWithObjectId = adminMgr.addPermission( new Permission( PERM_OBJ, PERM_OPERATION, PERM_OBJECT_ID ) );
        Assertions.assertEquals( PERM_OBJ, createdPermissionWithObjectId.getObjName() );
        Assertions.assertEquals( PERM_OPERATION, createdPermissionWithObjectId.getOpName() );
        Assertions.assertEquals( PERM_OBJECT_ID, createdPermissionWithObjectId.getObjId() );
        Assertions.assertNotNull( createdPermission.getInternalId() );
    }
    
    @AfterAll
    public static void teardown() throws SecurityException
    {
        adminMgr.deletePermission( new Permission( PERM_OBJ, PERM_OPERATION ) );
        adminMgr.deletePermObj( new PermObj( PERM_OBJ ) );
        delAdminMgr.delete( orgUnit );
    }
}
