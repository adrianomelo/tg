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

package org.semanticweb.owl.model; 
import java.net.URISyntaxException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLClass;
import java.net.URI;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * Basic test for operations on {@link OWLClass OWLClass}.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLClassTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class OWLClassTest extends TestCase 
{
    private OWLClass clazz;
    private OWLOntology ontology;

    public static Test suite( OWLDataFactory factory ) {
	try {
	    TestSuite suite = new TestSuite ("OWLClass");
	    /* Entity tests */
	    OWLClass clazz = factory.getOWLClass(TestUtils.newURI());
	    URI uri = TestUtils.newURI();
	    OWLOntology ontology = 
		factory.getOWLOntology(uri, uri);
	    
	    
	    suite.addTest( new OWLEntityTest( "testFactory",
					      clazz ) );
	    suite.addTest( new OWLNamedObjectTest( "testURI",
						   clazz ) );
	    suite.addTest( new OWLClassTest( "testAccessors",
					     ontology,
					     clazz ) );
	    return suite;
	} catch (OWLException ex) {
	}
	return null;
    }
    
    public OWLClassTest( String name, 
			 OWLOntology ontology,
			 OWLClass clazz )
    {
        super( name );
	this.ontology = ontology;
	this.clazz = clazz;
    }
    
    public void runTest() throws Throwable
    {
    }

    public void testAccessors() {
	try {
	    assertNotNull( clazz.getEnumerations( ontology ) );
	    assertNotNull( clazz.getEquivalentClasses( ontology ) );
	    assertNotNull( clazz.getSuperClasses( ontology ) );
	} catch (OWLException e) {
	    fail( e.getMessage() );
	}
    }


    
} // OWLClassTest



/*
 * ChangeLog
 * $Log: OWLClassTest.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/10/02 14:33:06  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.2  2003/05/08 07:54:35  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.1  2003/02/11 17:20:29  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.2  2003/02/10 09:23:06  seanb
 * Changes to cardinality, addition of property instances.
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 */
