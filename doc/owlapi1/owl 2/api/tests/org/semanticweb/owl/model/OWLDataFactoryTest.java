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
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectTest;
import java.util.List;
import java.net.URI;
import org.semanticweb.owl.model.OWLClass;
import java.net.URISyntaxException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;
import org.semanticweb.owl.model.OWLFrame;
import org.semanticweb.owl.model.OWLDataType;
import java.util.Set;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * Test {@link OWLDataFactory OWLDataFactory} methods. 
 *
 * @author Phillip Lord
 * @version $Id: OWLDataFactoryTest.java,v 1.3 2006/04/10 10:27:25 matthewhorridge Exp $
 */

public class OWLDataFactoryTest extends ObjectTest
{
    protected OWLDataFactory factory;

    /** Returns a TestSuite containing all the test cases. */ 

    public static Test suite( OWLDataFactory fact) {
	TestSuite suite = new TestSuite ("OWLDataFactory");
	suite.addTest( ObjectTest.suite( fact ) );
	suite.addTest( new OWLDataFactoryTest( "testCreationMethods",
					       fact) );
	suite.addTest( new OWLDataFactoryTest( "testThing",
					       fact) );
	suite.addTest( new OWLDataFactoryTest( "testNothing",
					       fact) );
	suite.addTest( new OWLDataFactoryTest( "testDuplication",
					       fact) );
	suite.addTest( new OWLDataFactoryTest( "testDifferent",
					       fact) );
	return suite;
    }

    /** Empty sets for use in creation tests */
    public Set getNewSet() {
	return new java.util.HashSet();
    }

    public OWLDataFactoryTest( String name, 
			       OWLDataFactory factory )
    {
        super( name, factory );
        this.factory = factory;
    }

    public OWLDataFactoryTest( OWLDataFactory factory ) {
	this( factory.getClass().getName(), factory );
    }


    /* Expose factory for subclasses */
    protected OWLDataFactory getFactory()
    {
	return factory;
    }

    protected OWLClass getNewClass() throws OWLException {
	return factory.getOWLClass(TestUtils.newURI());
    }

    protected OWLObjectProperty getNewObjectProperty() throws OWLException {
	return factory.getOWLObjectProperty(TestUtils.newURI());
    }

    protected OWLDataProperty getNewDataProperty() throws OWLException {
	return factory.getOWLDataProperty(TestUtils.newURI());
    }

    protected OWLIndividual getNewIndividual() throws OWLException {
	return factory.getOWLIndividual(TestUtils.newURI());
    }

    protected OWLDataType getNewConcreteDataType() throws OWLException {
	try {
	    URI uri = new URI( "http://www.w3.org/2000/10/XMLSchema#string" );
	    return factory.getOWLConcreteDataType( uri );
	} catch (URISyntaxException ex) {
	    return null;
	}
    }
    
    public void runTest() throws Throwable
    {
        // test the Object interface. 
        super.runTest();
        // test Creation Methods for entities.
        testCreationMethods();
    }

    /** Tests that basic creation methods all return non-null values */
    public void testCreationMethods() throws Throwable {
	URI uri;
	OWLObject oo;

	/* Classes */
	uri = TestUtils.newURI();
	OWLClass clazz = getFactory().getOWLClass( uri );
	assertNotNull( clazz );

	/* Properties */
	uri = TestUtils.newURI();
	OWLObjectProperty oprop = getFactory().getOWLObjectProperty( uri );
	assertNotNull( oprop );

	uri = TestUtils.newURI();
	OWLDataProperty dprop = getFactory().getOWLDataProperty( uri );
	assertNotNull( dprop );

	/* Individuals */
	uri = TestUtils.newURI();
	OWLIndividual ind = getFactory().getOWLIndividual( uri );
	assertNotNull( ind );

	oo = getFactory().getOWLThing();
	assertNotNull( oo );
	assertNotNull( ((OWLClass) oo).getURI() );

	oo = getFactory().getOWLNothing();
	assertNotNull( oo );
	assertNotNull( ((OWLClass) oo).getURI() );

	oo = getFactory().getOWLDisjointClassesAxiom( getNewSet() );
	assertNotNull( oo );

	oo = getFactory().getOWLEquivalentClassesAxiom( getNewSet() );
	assertNotNull( oo );

	oo = getFactory().getOWLSubClassAxiom( getNewClass(), 
					       getNewClass() );
	assertNotNull( oo );

	oo = getFactory().getOWLSubPropertyAxiom( getNewObjectProperty(), 
						  getNewObjectProperty() );
	assertNotNull( oo );

	oo = getFactory().getOWLFrame( getNewSet(), getNewSet() );
	assertNotNull( oo);

 	oo = getFactory().getOWLAnd( getNewSet() );
 	assertNotNull( oo );

 	oo = getFactory().getOWLOr( getNewSet() );
 	assertNotNull( oo );

	oo = getFactory().getOWLNot( getNewClass() );
	assertNotNull( oo );
	assertNotNull( ((OWLNot) oo).getOperand() );

	oo = 
	    getFactory().getOWLObjectCardinalityRestriction( getNewObjectProperty(),
								 0,
								 0 );
	assertNotNull( oo );

	oo = 
	    getFactory().getOWLDataCardinalityRestriction( getNewDataProperty(),
							   0,
							   0 );
	assertNotNull( oo );

	oo = 
	    getFactory().getOWLObjectSomeRestriction( getNewObjectProperty(),
							  getNewClass() );
	assertNotNull( oo );

	oo = 
	    getFactory().getOWLDataSomeRestriction( getNewDataProperty(),
							  getNewConcreteDataType() );
	assertNotNull( oo );

	oo = 
	    getFactory().getOWLObjectAllRestriction( getNewObjectProperty(),
							  getNewClass() );
	assertNotNull( oo );

	oo = 
	    getFactory().getOWLDataAllRestriction( getNewDataProperty(),
							  getNewConcreteDataType() );
	assertNotNull( oo );


    }

    public void testThing() throws Throwable {
	/* Thing */
	assertSame( getFactory().getOWLThing(), 
		    getFactory().getOWLThing() );
	
	/* Check that Thing is the same as asking for Class thing */
	try {
	    URI uri = new URI( OWLVocabularyAdapter.INSTANCE.getThing() );
	    OWLObject oo = getFactory().getOWLClass( uri );
	    assertSame( getFactory().getOWLThing(), oo );
	} catch (Exception ex) {
	    fail(ex.getMessage());
	}
    }

    public void testNothing() throws Throwable {
	/* Thing */
	assertSame( getFactory().getOWLNothing(), 
		    getFactory().getOWLNothing() );
	
	/* Check that Thing is the same as asking for Class thing */
	try {
	    URI uri = new URI( OWLVocabularyAdapter.INSTANCE.getNothing() );
	    OWLObject oo = getFactory().getOWLClass( uri );
	    assertSame( getFactory().getOWLNothing(), oo );
	} catch (Exception ex) {
	    fail(ex.getMessage());
	}
    }
	    
    public void testDuplication() throws Throwable {
	URI uri;
	OWLObject oo;

	/* Classes */
	uri = TestUtils.newURI();
	OWLClass clazz1 = getFactory().getOWLClass( uri );
	OWLClass clazz2 = getFactory().getOWLClass( uri );
	assertSame( clazz1, clazz2 );

	/* Properties */
	uri = TestUtils.newURI();
	OWLObjectProperty oprop1 = getFactory().getOWLObjectProperty( uri );
	OWLObjectProperty oprop2 = getFactory().getOWLObjectProperty( uri );
	assertSame( oprop1, oprop2 );

	uri = TestUtils.newURI();
	OWLDataProperty dprop1 = getFactory().getOWLDataProperty( uri );
	OWLDataProperty dprop2 = getFactory().getOWLDataProperty( uri );
	assertSame( dprop1, dprop2 );

	/* Individuals */
	uri = TestUtils.newURI();
	OWLIndividual ind1 = getFactory().getOWLIndividual( uri );
	OWLIndividual ind2 = getFactory().getOWLIndividual( uri );
	assertSame( ind1, ind2 );
    }

    /** Test that the factory keeps the namespaces separate, even when
     * the URIs are the same. */
    public void testDifferent() throws Throwable {
	URI uri;
	OWLObject oo;

	/* Classes */
	uri = TestUtils.newURI();

	OWLClass clazz = getFactory().getOWLClass( uri );
	OWLObjectProperty oprop = getFactory().getOWLObjectProperty( uri );
	OWLDataProperty dprop = getFactory().getOWLDataProperty( uri );
	OWLIndividual ind = getFactory().getOWLIndividual( uri );
	
	/* Can't seem to find this. JUnit version problem? */
// 	assertNotSame( clazz, oprop );
// 	assertNotSame( clazz, dprop );
// 	assertNotSame( clazz, ind );

// 	assertNotSame( oprop, dprop );
// 	assertNotSame( oprop, ind );

// 	assertNotSame( dprop, ind );
    }

    
} // OWLDataFactoryTest


/*
 * ChangeLog
 * $Log: OWLDataFactoryTest.java,v $
 * Revision 1.3  2006/04/10 10:27:25  matthewhorridge
 * Removed
 * oo = getFactory().getOWLIndividual(null);
 * assertNotNull( oo );
 * because passing in null for the getOWLIndividual method is no longer valid.
 *
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.6  2003/05/19 12:01:19  seanb
 * no message
 *
 * Revision 1.5  2003/04/10 12:16:40  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.4  2003/03/31 16:55:12  seanb
 * Various updates and fixes to parser.
 * Adding inverses, domain and range.
 *
 * Revision 1.3  2003/02/14 17:52:13  seanb
 * Improvements to Validation and updating data structures.
 *
 * Revision 1.2  2003/02/12 09:14:12  seanb
 * Moving Tests.
 *
 * Revision 1.1  2003/02/11 17:20:29  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.5  2003/02/10 09:23:06  seanb
 * Changes to cardinality, addition of property instances.
 *
 * Revision 1.4  2003/02/06 11:14:51  seanb
 * Moved Vocabulary adapters into owl.io.vocabulary and renamed adaptor
 * to adapter.
 *
 * Revision 1.3  2003/02/06 10:27:33  seanb
 * Replacing OWLThing and OWLNothing with specific instances of OWLClass.
 *
 * Revision 1.2  2003/01/29 16:10:52  seanb
 * Changes to support Anonymous Individuals.
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 *
 */
