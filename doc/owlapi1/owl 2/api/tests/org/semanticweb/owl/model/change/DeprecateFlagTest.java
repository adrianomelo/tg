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
import org.semanticweb.owl.model.change.SetDeprecated;
import org.semanticweb.owl.model.OWLDeprecatableObject;

// Generated package name


/**
 * Test that deprecation flags are set ok.
 *
 * <br/>
 * Created: Wed Jan 22 15:50:41 2003
 *
 * @author Sean Bechhofer
 * @version $Id: DeprecateFlagTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class DeprecateFlagTest extends TestCase
{

    private OWLDataFactory factory;
    private OWLOntology ontology;
    private ChangeVisitor visitor;
    
    public static Test suite( OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv ) {
	
	TestSuite suite = new TestSuite ("DeprecateFlag");

	suite.addTest( new DeprecateFlagTest( "testDeprecateClass",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new DeprecateFlagTest( "testDeprecateObjectProperty",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new DeprecateFlagTest( "testDeprecateDataProperty",
					  fact,
					  o,
					  cv ) );
	return suite;
    }
    
    public DeprecateFlagTest( String name, 
			  OWLDataFactory fact,
			  OWLOntology o, 
			  ChangeVisitor cv )
    {
        super( name );
	this.factory = fact;
	this.ontology = o;
	this.visitor = cv;
    }
    
    public void testDeprecateClass() throws Throwable {
	/* Get a new class*/
	OWLDeprecatableObject odc = factory.getOWLClass( TestUtils.newURI() );
	/* Get a new change object */
	OntologyChange oc = new SetDeprecated( ontology,
					       odc,
					       true,
					       null );
	assertTrue( !odc.isDeprecated( ontology ) );

	/* Add it */
	oc.accept( visitor );
	assertTrue( odc.isDeprecated( ontology ) );
    }
    public void testDeprecateObjectProperty() throws Throwable {
	/* Get a new class*/
	OWLDeprecatableObject odc = 
	    factory.getOWLObjectProperty( TestUtils.newURI() );
	/* Get a new change object */
	OntologyChange oc = new SetDeprecated( ontology,
					       odc,
					       true,
					       null );
	assertTrue( !odc.isDeprecated( ontology ) );

	/* Add it */
	oc.accept( visitor );
	assertTrue( odc.isDeprecated( ontology ) );
    }
    public void testDeprecateDataProperty() throws Throwable {
	/* Get a new class*/
	OWLDeprecatableObject odc = 
	    factory.getOWLDataProperty( TestUtils.newURI() );
	/* Get a new change object */
	OntologyChange oc = new SetDeprecated( ontology,
					       odc,
					       true,
					       null );
	assertTrue( !odc.isDeprecated( ontology ) );

	/* Add it */
	oc.accept( visitor );
	assertTrue( odc.isDeprecated( ontology ) );
    }
    
} // DeprecateFlagTest



/*
 * ChangeLog
 * $Log: DeprecateFlagTest.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
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
