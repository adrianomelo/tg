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
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.change.AddDataPropertyInstance;
import org.semanticweb.owl.model.change.AddObjectPropertyInstance;
import org.semanticweb.owl.model.OWLDataValue;
import java.util.Set;
import java.util.Map;

// Generated package name


/**
 * Test that property flags are set ok.
 *
 * <br/>
 * Created: Wed Jan 22 15:50:41 2003
 *
 * @author Sean Bechhofer
 * @version $Id: PropertyInstanceTest.java,v 1.3 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class PropertyInstanceTest extends TestCase
{

    private OWLDataFactory factory;
    private OWLOntology ontology;
    private ChangeVisitor visitor;
    
    public static Test suite( OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv ) {
	
	TestSuite suite = new TestSuite ("PropertyInstance");
	/* Entity tests */
	suite.addTest( new PropertyInstanceTest( "testObject",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new PropertyInstanceTest( "testData",
					  fact,
					  o,
					  cv ) );
	return suite;
    }
    
    public PropertyInstanceTest( String name, 
			  OWLDataFactory fact,
			  OWLOntology o, 
			  ChangeVisitor cv )
    {
        super( name );
	this.factory = fact;
	this.ontology = o;
	this.visitor = cv;
    }
    
    public void testObject() throws Throwable {
	/* Get a new object property */
	OWLIndividual i = factory.getOWLIndividual( TestUtils.newURI() );
	OWLIndividual j = factory.getOWLIndividual( TestUtils.newURI() );
	OWLObjectProperty prop = factory.getOWLObjectProperty( TestUtils.newURI() );
	
	/* Get a new change object */
	OntologyChange oc = 
	    new AddObjectPropertyInstance( ontology,
					       i,
					       prop,
					       j,
					       null );
	/* Add it */
	oc.accept( visitor );

	/* Check that the individual has been added */
	Map m = i.getObjectPropertyValues( ontology );
	Set s = (Set) m.get( prop );
	assertNotNull( s );
	assertTrue( s.contains( j ) );

	/* Check that the incoming relationship has been added */
	m = j.getIncomingObjectPropertyValues( ontology );
	s = (Set) m.get( prop );
	assertNotNull( s );
	assertTrue( s.contains( i ) );
    }

    public void testData() throws Throwable {
	/* Get a new object property */
	OWLIndividual i = factory.getOWLIndividual( TestUtils.newURI() );
	OWLDataValue datum = 
	    factory.getOWLConcreteData( new URI( "http://www.w3.org/2000/10/XMLSchema#string" ),
					null, 
					"Hello World");
	OWLDataProperty prop = factory.getOWLDataProperty( TestUtils.newURI() );
	
	/* Get a new change object */
	OntologyChange oc = 
	    new AddDataPropertyInstance( ontology,
					   i,
					   prop,
					   datum,
					   null );
	/* Add it */
	oc.accept( visitor );
    }

    
} // PropertyInstanceTest



/*
 * ChangeLog
 * $Log: PropertyInstanceTest.java,v $
 * Revision 1.3  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2003/11/20 12:58:09  sean_bechhofer
 * Addition of language handling in OWLDataValues.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/05/27 08:44:36  seanb
 * no message
 *
 * Revision 1.1  2003/05/19 12:48:14  seanb
 * Individual -> Object
 *
 * Revision 1.4  2003/04/10 12:16:40  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.3  2003/03/31 16:55:12  seanb
 * Various updates and fixes to parser.
 * Adding inverses, domain and range.
 *
 * Revision 1.2  2003/02/12 09:14:12  seanb
 * Moving Tests.
 *
 * Revision 1.1  2003/02/11 17:20:29  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.1  2003/02/10 17:09:01  seanb
 * Further tests. Adding logging to ChangeVisitorAdapter.
 *
 * Revision 1.1  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
