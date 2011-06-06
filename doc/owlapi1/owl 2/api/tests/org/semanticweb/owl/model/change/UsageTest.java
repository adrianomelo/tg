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
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.TestUtils;
import org.semanticweb.owl.model.change.AddEntity;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

// Generated package name


/**
 * Test that when entities are added, the usage functions work
 * properly.
 *
 * <br/> <strong>WARNING:</strong> These tests are not particularly
 * comprehensive.
 *
 * Created: Wed Jan 22 15:50:41 2003
 *
 * @author Sean Bechhofer
 * @version $Id: UsageTest.java,v 1.5 2006/03/28 16:14:45 ronwalf Exp $
 */

public class UsageTest extends TestCase
{

    /* More tests are needed here */

    private OWLDataFactory factory;
    private OWLOntology ontology;
    private ChangeVisitor visitor;

    public static Test suite( OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv ) {

	TestSuite suite = new TestSuite ("Usage");
	/* Entity tests */
	suite.addTest( new UsageTest( "testClassSuperAndEquiv",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new UsageTest( "testClassIntersectionSuper",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new UsageTest( "testClassEnumeration",
					  fact,
					  o,
					  cv ) );
	suite.addTest( new UsageTest( "testBothWays",
					  fact,
					  o,
					  cv ) );
	return suite;
    }
    
    public UsageTest( String name, 
		      OWLDataFactory fact,
		      OWLOntology o, 
		      ChangeVisitor cv )
    {
        super( name );
	this.factory = fact;
	this.ontology = o;
	this.visitor = cv;
    }
    
    public void testClassSuperAndEquiv() throws Throwable {
	/* Get some new classes. */
	OWLClass class1 = factory.getOWLClass( TestUtils.newURI() );
	OWLClass class2 = factory.getOWLClass( TestUtils.newURI() );
	OWLClass class3 = factory.getOWLClass( TestUtils.newURI() );
	
	OntologyChange oc = 
	    new AddEntity( ontology, 
			   class1,
			   null );
	
	oc.accept( visitor );
	oc = new AddEntity( ontology, 
			    class2,
			    null );
	oc.accept( visitor );
	oc = new AddEntity( ontology, 
			    class3,
			    null );
	oc.accept( visitor );
	
	/* Get a new change object */
	oc = new AddSuperClass( ontology,
				class1,
				class2,
				null );

	/* class1 shouldn't use class2 or class3 */
	assertFalse( class2.getUsage( ontology ).contains( class1 ) );
	assertFalse( class3.getUsage( ontology ).contains( class1 ) );

	assertFalse( class1.objectsUsed( ontology ).contains( class2 ) );
	assertFalse( class1.objectsUsed( ontology ).contains( class3 ) );
	
	/* Add it */
	oc.accept( visitor );
	/* class1 should use class2 but not class3 */
	assertTrue( class2.getUsage( ontology ).contains( class1 ) );
	assertFalse( class3.getUsage( ontology ).contains( class1 ) );

	assertTrue( class1.objectsUsed( ontology ).contains( class2 ) );
	assertFalse( class1.objectsUsed( ontology ).contains( class3 ) );
	
	/* Get a new change object */
	oc = new AddEquivalentClass( ontology,
				     class1,
				     class3,
				     null );
	/* Add it */
	oc.accept( visitor );

	/* class1 should now use class3 */
	assertTrue( class2.getUsage( ontology ).contains( class1 ) );
	assertTrue( class3.getUsage( ontology ).contains( class1 ) );

	assertTrue( class1.objectsUsed( ontology ).contains( class2 ) );
	assertTrue( class1.objectsUsed( ontology ).contains( class3 ) );


	/* Now remove the assertions */

	/* Get a new change object */
	oc = new RemoveSuperClass( ontology,
				class1,
				class2,
				null );
	/* Remove it */
	oc.accept( visitor );
	/* class1 should use class3 but not class2 */
	assertFalse( class2.getUsage( ontology ).contains( class1 ) );
	assertTrue( class3.getUsage( ontology ).contains( class1 ) );

	assertFalse( class1.objectsUsed( ontology ).contains( class2 ) );
	assertTrue( class1.objectsUsed( ontology ).contains( class3 ) );
	
	/* Get a new change object */
	oc = new RemoveEquivalentClass( ontology,
					class1,
					class3,
					null );
	/* Remove it */
	oc.accept( visitor );

	/* class1 should now use neither */
	assertFalse( class2.getUsage( ontology ).contains( class1 ) );
	assertFalse( class3.getUsage( ontology ).contains( class1 ) );

	assertFalse( class1.objectsUsed( ontology ).contains( class2 ) );
	assertFalse( class1.objectsUsed( ontology ).contains( class3 ) );

    }

    public void testClassIntersectionSuper() throws Throwable {
	/* Get some new classes. */
	OWLClass class1 = factory.getOWLClass( TestUtils.newURI() );
	OWLClass class2 = factory.getOWLClass( TestUtils.newURI() );
	OWLClass class3 = factory.getOWLClass( TestUtils.newURI() );
	
	OntologyChange oc = 
	    new AddEntity( ontology, 
			   class1,
			   null );
	
	oc.accept( visitor );
	oc = new AddEntity( ontology, 
			    class2,
			    null );
	oc.accept( visitor );
	oc = new AddEntity( ontology, 
			    class3,
			    null );
	oc.accept( visitor );

	/* class1 shouldn't use class2 or class3 */
	assertFalse( class2.getUsage( ontology ).contains( class1 ) );
	assertFalse( class3.getUsage( ontology ).contains( class1 ) );

	assertFalse( class1.objectsUsed( ontology ).contains( class2 ) );
	assertFalse( class1.objectsUsed( ontology ).contains( class3 ) );
	
	Set ops = new HashSet();
	ops.add( class2 );
	ops.add( class3 );
	
	OWLAnd and = factory.getOWLAnd( ops );
	/* Get a new change object */
	oc = new AddSuperClass( ontology,
				class1,
				and,
				null );
	/* Add it */
	oc.accept( visitor );

	/* class1 should use class2 and class3 */
	assertTrue( class2.getUsage( ontology ).contains( class1 ) );
	assertTrue( class3.getUsage( ontology ).contains( class1 ) );

	assertTrue( class1.objectsUsed( ontology ).contains( class2 ) );
	assertTrue( class1.objectsUsed( ontology ).contains( class3 ) );
	
	/* Now remove the assertion */

	/* Get a new change object */
	oc = new RemoveSuperClass( ontology,
				class1,
				and,
				null );
	/* Remove it */
	oc.accept( visitor );
	/* class1 should use neither */
	assertFalse( class2.getUsage( ontology ).contains( class1 ) );
	assertFalse( class3.getUsage( ontology ).contains( class1 ) );

	assertFalse( class1.objectsUsed( ontology ).contains( class2 ) );
	assertFalse( class1.objectsUsed( ontology ).contains( class3 ) );
    }

    public void testClassEnumeration() throws Throwable {
	/* Get some new classes. */
	OWLClass class1 = factory.getOWLClass( TestUtils.newURI() );

	OWLIndividual ind1 = factory.getOWLIndividual( TestUtils.newURI() );
	OWLIndividual ind2 = factory.getOWLIndividual( TestUtils.newURI() );

	OntologyChange oc = new AddEntity( ontology, 
					   ind1,
					   null );
	oc.accept( visitor );
	
	oc = new AddEntity( ontology, 
			    ind2,
			    null );
	oc.accept( visitor );
	
	Set inds = new HashSet();
	inds.add( ind1 );
	inds.add( ind2 );
	OWLEnumeration enum_ = factory.getOWLEnumeration( inds );


	/* class1 should use neither individual */
	assertFalse( ind1.getUsage( ontology ).contains( class1 ) );
	assertFalse( ind1.getUsage( ontology ).contains( class1 ) );

	assertFalse( class1.objectsUsed( ontology ).contains( ind1 ) );
	assertFalse( class1.objectsUsed( ontology ).contains( ind2 ) );


	/* Get a new change object */
	oc = new AddEnumeration( ontology,
				 class1,
				 enum_,
				 null );
	/* Add it */
	oc.accept( visitor );

	/* class1 should use both individuals */
	assertTrue( ind1.getUsage( ontology ).contains( class1 ) );
	assertTrue( ind2.getUsage( ontology ).contains( class1 ) );

	assertTrue( class1.objectsUsed( ontology ).contains( ind1 ) );
	assertTrue( class1.objectsUsed( ontology ).contains( ind2 ) );

	/* Get a new change object */
	oc = new RemoveEnumeration( ontology,
				    class1,
				    enum_,
				    null );
	/* Remove it */
	oc.accept( visitor );
	
	/* class1 should use neither individual */
	assertFalse( ind1.getUsage( ontology ).contains( class1 ) );
	assertFalse( ind2.getUsage( ontology ).contains( class1 ) );

	assertFalse( class1.objectsUsed( ontology ).contains( ind1 ) );
	assertFalse( class1.objectsUsed( ontology ).contains( ind2 ) );
    }

    public void testBothWays() throws Throwable {
	/* We should maybe remove the hard-coding of the ontology name. */
	java.net.URL ontoURL = ClassLoader.getSystemResource ( "spy.owl" );
	java.net.URI ontoURI = new java.net.URI( ontoURL.toString() );
	OWLOntology onto = 
	    org.semanticweb.owl.model.helper.OntologyHelper.getOntology ( ontoURI );

	/* Grab everything. */
	Set allObjects = new HashSet();
	allObjects.addAll( onto.getClasses() );
	allObjects.addAll( onto.getObjectProperties() );
	allObjects.addAll( onto.getDataProperties() );
	allObjects.addAll( onto.getIndividuals() );

	for (Iterator it = allObjects.iterator();
	     it.hasNext(); ) {
	    OWLEntity entity = (OWLEntity) it.next();
	    /* Check that for each object that the entity claims to
	     * use, the object thinks the entity uses it. */ 
	    for (Iterator it2 = entity.objectsUsed( onto ).iterator(); 
		 it2.hasNext(); ) {
		OWLEntity used = (OWLEntity) it2.next();
		/* The entity should be in the usage collection */
		assertTrue( used.getUsage( onto ).contains( entity ) );
	    }
	}

	for (Iterator it = allObjects.iterator();
	     it.hasNext(); ) {
	    OWLEntity entity = (OWLEntity) it.next();
	    /* Check that for each entity that this entity claims to
	     * be used by, if it's an entity, then the entity thinks
	     * it uses it. You what?? */ 
	    for (Iterator it2 = entity.getUsage( onto ).iterator(); 
		 it2.hasNext(); ) {
		try {
		    OWLEntity user = (OWLEntity) it2.next();
		    /* The entity should be in the usage collection */
		    assertTrue( user.objectsUsed( onto ).contains( entity ) );
		} catch (ClassCastException ex ) {
		    /* Just ignore.... */
		}
	    }
	}

    }
    
    
} // AddEntityTest



/*
 * ChangeLog
 * $Log: UsageTest.java,v $
 * Revision 1.5  2006/03/28 16:14:45  ronwalf
 * Merging mindswap changes to OWLApi.
 * Rough summary:
 * * 1.5 compatibility (rename enum variables)
 * * An option to turn on and off importing in OWLConsumer
 * * Bug fix to allow DataRange in more areas
 * * Giving Anonymous individuals an identifier
 *   * New factory method - getAnonOWLIndividual
 *   * getOWLIndividual no longer accepts 'null'
 *   * added getAnonId() and isAnon() to OWLIndividual
 * * Some work on the RDF serializer, but we have a complete rewrite in
 *   Swoop that I think is better (more flexible, results easier to read)
 * * Added Transitive, Functional, InverseFunctional, Inverse, and
 *   Symmetric PropertyAxioms (not sure why, will check)
 * * Added .equals and .hashcode for all OWLObjects
 * * Added a RemoveDataType change
 * * Patches to OntologyImpl for Entity removal
 * * Added OWLIndividualTypeAssertion
 * * Added OWL(Object|Data)Property(Domain|Range)Axiom
 * * Added OWL(Object|Data)PropertyInstance
 * * Added subclass index to OWLClassImpl (and getSubClasses(...) for
 *   OWLClass)
 * * Changes for Entity renaming
 *
 * Revision 1.4  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/07/12 15:29:17  sean_bechhofer
 * Changes to test/build. Resources used in tests are now picked up from the
 * classpath. This should produce a more consistent test/build and allow ant
 * to be run from alternative directories.
 *
 * Revision 1.2  2004/07/09 14:04:58  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
 *
 * Revision 1.1  2004/07/09 12:07:47  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
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
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
