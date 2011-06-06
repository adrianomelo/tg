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
import org.semanticweb.owl.model.change.RemoveEntity;
import org.semanticweb.owl.model.change.AddEntity;

// Generated package name


/**
 * Test that entities are removed in a sensible fashion.
 *
 * <br/>
 * Created: Wed Jan 22 15:50:41 2003
 *
 * @author Sean Bechhofer
 * @version $Id: RemoveEntityTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class RemoveEntityTest extends TestCase
{

    private OWLDataFactory factory;
    private OWLOntology ontology;
    private ChangeVisitor visitor;

    public static Test suite( OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv ) {

	TestSuite suite = new TestSuite ("RemoveEntity");
	/* Entity tests */
	suite.addTest( new RemoveEntityTest( "testRemoveClass",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new RemoveEntityTest( "testRemoveIndividual",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new RemoveEntityTest( "testRemoveObjectProperty",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new RemoveEntityTest( "testRemoveDataProperty",
					  fact,
					  o,
					  cv ) );
	return suite;
    }
    
    public RemoveEntityTest( String name, 
			  OWLDataFactory fact,
			  OWLOntology o, 
			  ChangeVisitor cv )
    {
        super( name );
	this.factory = fact;
	this.ontology = o;
	this.visitor = cv;
    }
    
    public void testRemoveClass() throws Throwable {
	/* Get a new class */
	OWLEntity entity = factory.getOWLClass( TestUtils.newURI() );
	/* Get a new change object */
	AddEntity ae = new AddEntity( ontology,
				      entity,
				      null );
	/* Add it */
	ae.accept( visitor );

	assertTrue( ontology.getClasses().contains( entity ) );
	assertTrue( entity.getOntologies().contains( ontology ) );

	/* Get a new change object */
	RemoveEntity re = new RemoveEntity( ontology,
				      entity,
				      null );
	/* Remove it */
	re.accept( visitor );

	assertTrue( !ontology.getClasses().contains( entity ) );
	assertTrue( !entity.getOntologies().contains( ontology ) );
    }

    public void testRemoveIndividual() throws Throwable {
	/* Get a new individual */
	OWLEntity entity = factory.getOWLIndividual( TestUtils.newURI() );
	/* Get a new change object */
	AddEntity ae = new AddEntity( ontology,
				      entity,
				      null );
	/* Add it */
	ae.accept( visitor );

	assertTrue( ontology.getIndividuals().contains( entity ) );
	assertTrue( entity.getOntologies().contains( ontology ) );

	/* Get a new change object */
	RemoveEntity re = new RemoveEntity( ontology,
				      entity,
				      null );
	/* Remove it */
	re.accept( visitor );
	
	assertTrue( !ontology.getIndividuals().contains( entity ) );
	assertTrue( !entity.getOntologies().contains( ontology ) );
    }

    public void testRemoveObjectProperty() throws Throwable {
	/* Get a new individual */
	OWLEntity entity = factory.getOWLObjectProperty( TestUtils.newURI() );
	/* Get a new change object */
	AddEntity ae = new AddEntity( ontology,
				      entity,
				      null );
	/* Add it */
	ae.accept( visitor );

	assertTrue( ontology.getObjectProperties().contains( entity ) );
	assertTrue( entity.getOntologies().contains( ontology ) );

	/* Get a new change object */
	RemoveEntity re = new RemoveEntity( ontology,
				      entity,
				      null );
	/* Remove it */
	re.accept( visitor );
	
	assertTrue( !ontology.getObjectProperties().contains( entity ) );
	assertTrue( !entity.getOntologies().contains( ontology ) );
    }

    public void testRemoveDataProperty() throws Throwable {
	/* Get a new individual */
	OWLEntity entity = factory.getOWLDataProperty( TestUtils.newURI() );
	/* Get a new change object */
	AddEntity ae = new AddEntity( ontology,
				      entity,
				      null );
	/* Add it */
	ae.accept( visitor );
	
	assertTrue( ontology.getDataProperties().contains( entity ) );
	assertTrue( entity.getOntologies().contains( ontology ) );

	/* Get a new change object */
	RemoveEntity re = new RemoveEntity( ontology,
				      entity,
				      null );
	/* Remove it */
	re.accept( visitor );
	
	assertTrue( !ontology.getDataProperties().contains( entity ) );
	assertTrue( !entity.getOntologies().contains( ontology ) );
    }
    
} // RemoveEntityTest



/*
 * ChangeLog
 * $Log: RemoveEntityTest.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/02/11 17:20:29  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
