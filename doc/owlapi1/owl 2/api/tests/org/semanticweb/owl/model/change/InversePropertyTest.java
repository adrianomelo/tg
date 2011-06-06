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

package org.semanticweb.owl.model.change; 
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.TestUtils;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.change.AddDomain;
import org.semanticweb.owl.model.OWLDataType;
import java.net.URI;

// Generated package name


/**
 * Test that property flags are set ok.
 *
 * <br/>
 * Created: Wed Jan 22 15:50:41 2003
 *
 * @author Sean Bechhofer
 * @version $Id: InversePropertyTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class InversePropertyTest extends TestCase
{

    private OWLDataFactory factory;
    private OWLOntology ontology;
    private ChangeVisitor visitor;
    
    public static Test suite( OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv ) {
	
	TestSuite suite = new TestSuite ("SuperProperty");
	/* Entity tests */
	suite.addTest( new InversePropertyTest( "testInverseProperty",
					  fact,
					  o,
					  cv ) );
	return suite;
    }
    
    public InversePropertyTest( String name, 
			  OWLDataFactory fact,
				OWLOntology o, 
			  ChangeVisitor cv )
    {
        super( name );
	this.factory = fact;
	this.ontology = o;
	this.visitor = cv;
    }
    
    public void testInverseProperty() throws Throwable {
	/* Get a new object property */
	OWLObjectProperty prop1 = 
	    factory.getOWLObjectProperty( TestUtils.newURI() );
	OWLObjectProperty prop2 = 
	    factory.getOWLObjectProperty( TestUtils.newURI() );
	
	/* Get a new change object */
	OntologyChange oc = new AddInverse( ontology,
						  prop1,
						  prop2,
						  null );
	/* Add it */
	oc.accept( visitor );
	assertTrue( prop1.getInverses( ontology ).contains( prop2 ) );
	
    }
    
} // InversePropertyTest



/*
 * ChangeLog
 * $Log: InversePropertyTest.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/06/16 12:49:15  seanb
 * More tests
 *
 * Revision 1.2  2003/05/19 12:48:14  seanb
 * Individual -> Object
 *
 * Revision 1.1  2003/05/08 07:54:35  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.3  2003/04/10 12:16:40  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.2  2003/02/12 09:14:12  seanb
 * Moving Tests.
 *
 * Revision 1.1  2003/02/11 17:20:29  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.1  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
