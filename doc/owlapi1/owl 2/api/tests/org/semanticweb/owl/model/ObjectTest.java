/*
 * Copyright (C) 2003, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.semanticweb.owl.model; // Generated package name
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Test basic object methods.
 *
 *
 * @author Phillip Lord
 * @version $Id: ObjectTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class ObjectTest extends TestCase 
{
    private Object obj;

    public ObjectTest( String testName, Object obj )
    {
        super( testName );
        this.obj = obj;
    }

    /** Returns a TestSuite containing all the test cases. */ 

    public static Test suite( Object obj ) {
	TestSuite suite = new TestSuite ("Object");
	suite.addTest( new ObjectTest( "testObjectMethods",
				       obj ) );
	return suite;
    }
    
    public void runTest() throws Throwable

    {
        testObjectMethods();
    }
    
    public void testObjectMethods()
    {
        assertNotNull( obj );
        assertNotNull( obj.toString() );
        assertTrue( obj.equals( obj ) );
    }
} // ObjectTest



/*
 * ChangeLog
 * $Log: ObjectTest.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/02/11 17:20:29  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 *
 */
