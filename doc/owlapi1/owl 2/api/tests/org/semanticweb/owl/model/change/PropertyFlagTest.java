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
import org.semanticweb.owl.model.change.SetFunctional;
import org.semanticweb.owl.model.change.SetInverseFunctional;
import org.semanticweb.owl.model.change.SetOneToOne;
import org.semanticweb.owl.model.change.SetSymmetric;
import org.semanticweb.owl.model.change.SetTransitive;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;

// Generated package name


/**
 * Test that property flags are set ok.
 *
 * <br/>
 * Created: Wed Jan 22 15:50:41 2003
 *
 * @author Sean Bechhofer
 * @version $Id: PropertyFlagTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class PropertyFlagTest extends TestCase
{

    private OWLDataFactory factory;
    private OWLOntology ontology;
    private ChangeVisitor visitor;
    
    public static Test suite( OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv ) {
	
	TestSuite suite = new TestSuite ("PropertyFlag");
	/* Entity tests */
	suite.addTest( new PropertyFlagTest( "testFunctional",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new PropertyFlagTest( "testInverseFunctional",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new PropertyFlagTest( "testOneToOne",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new PropertyFlagTest( "testSymmetric",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new PropertyFlagTest( "testTransitive",
					  fact,
					  o,
					  cv ) );
	return suite;
    }
    
    public PropertyFlagTest( String name, 
			  OWLDataFactory fact,
			  OWLOntology o, 
			  ChangeVisitor cv )
    {
        super( name );
	this.factory = fact;
	this.ontology = o;
	this.visitor = cv;
    }
    
    public void testFunctional() throws Throwable {
	/* Get a new object property */
	OWLObjectProperty prop = factory.getOWLObjectProperty( TestUtils.newURI() );
	/* Get a new change object */
	OntologyChange oc = new SetFunctional( ontology,
					       prop,
					       true,
					       null );
	assertTrue( !prop.isFunctional( ontology ) );

	/* Add it */
	oc.accept( visitor );
	assertTrue( prop.isFunctional( ontology ) );

	/* Now try with a data property */
	OWLDataProperty dprop = factory.getOWLDataProperty( TestUtils.newURI() );
	/* Get a new change object */
	oc = new SetFunctional( ontology,
					       dprop,
					       true,
					       null );
	assertTrue( !dprop.isFunctional( ontology ) );

	/* Add it */
	oc.accept( visitor );
	assertTrue( dprop.isFunctional( ontology ) );
    }
    public void testInverseFunctional() throws Throwable {
	/* Get a new object property */
	OWLObjectProperty prop = factory.getOWLObjectProperty( TestUtils.newURI() );
	/* Get a new change object */
	OntologyChange oc = new SetInverseFunctional( ontology,
						      prop,
						      true,
						      null );
	assertTrue( !prop.isInverseFunctional( ontology ) );

	/* Add it */
	oc.accept( visitor );
	assertTrue( prop.isInverseFunctional( ontology ) );
    }
    public void testOneToOne() throws Throwable {
	/* Get a new object property */
	OWLObjectProperty prop = factory.getOWLObjectProperty( TestUtils.newURI() );
	/* Get a new change object */
	OntologyChange oc = new SetOneToOne( ontology,
					     prop,
					     true,
					     null );
	assertTrue( !prop.isOneToOne( ontology ) );

	/* Add it */
	oc.accept( visitor );
	assertTrue( prop.isOneToOne( ontology ) );
    }
    public void testSymmetric() throws Throwable {
	/* Get a new object property */
	OWLObjectProperty prop = factory.getOWLObjectProperty( TestUtils.newURI() );
	/* Get a new change object */
	OntologyChange oc = new SetSymmetric( ontology,
					      prop,
					      true,
					      null );
	assertTrue( !prop.isSymmetric( ontology ) );
	
	/* Add it */
	oc.accept( visitor );
	assertTrue( prop.isSymmetric( ontology ) );
    }
    public void testTransitive() throws Throwable {
	/* Get a new object property */
	OWLObjectProperty prop = factory.getOWLObjectProperty( TestUtils.newURI() );
	/* Get a new change object */
	OntologyChange oc = new SetTransitive( ontology,
					       prop,
					       true,
					       null );
	assertTrue( !prop.isTransitive( ontology ) );

	/* Add it */
	oc.accept( visitor );
	assertTrue( prop.isTransitive( ontology ) );
    }

    
} // PropertyFlagTest



/*
 * ChangeLog
 * $Log: PropertyFlagTest.java,v $
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
