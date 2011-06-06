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

package uk.ac.man.cs.img.owl.validation; // Generated package name

import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.event.ChangeEvent;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.OntologyChange;
import uk.ac.man.cs.img.owl.validation.SpeciesValidator; 
import uk.ac.man.cs.img.owl.validation.ValidatorLogger; 
import org.semanticweb.owl.impl.model.OWLOntologyImpl;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.change.AddIndividualClass;



/**
 * Tests that documents with unseparated namespaces are correctly
 * identified as OWL Full.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: NamespaceSeparationTest.java,v 1.3 2005/06/10 12:20:35 sean_bechhofer Exp $
 */

public class NamespaceSeparationTest extends TestCase
{

    private OWLDataFactory factory;
    private OWLEntity[] entities;
    private SpeciesValidator sv;

    /**
     * 
     *
     * @param testName a <code>String</code> value
     * @param factory an <code>OWLDataFactory</code> value
     */
    public NamespaceSeparationTest( String testName, 
				    OWLDataFactory factory ) {
	super( testName );
	this.factory = factory;
	
    }

    /**
     * Returns a TestSuite containing all the test cases.
     * @param fact an <code>OWLDataFactory</code> value
     * @return a <code>Test</code> value
     */
    public static Test suite( OWLDataFactory fact) {
	TestSuite suite = new TestSuite ("NamespaceSeparation");

 	suite.addTest( new NamespaceSeparationTest( "testSeparationLite1",
 						    fact) );
 	suite.addTest( new NamespaceSeparationTest( "testSeparationLite2",
 						    fact) );
	suite.addTest( new NamespaceSeparationTest( "testSeparationFull",
						    fact) );
	return suite;
    }


    /**
     * Test that an ontology with a single class is in OWL-Lite
     *
     * @exception Throwable if an error occurs
     */
    public void testSeparationLite1() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );

	URI uri = new URI("http://example.org/test-onto-sep-lite-1"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	
	/* Add a class */
	uri = new URI("http://example.org/test-onto#Class1"); 
	TestUtils.addClass( ontology, factory, changeVisitor, uri );
	/* Ontology should be Lite */
	assertTrue( sv.isOWLLite( ontology ) );
    }

    /**
     * Test that an ontology with two distinct classes is in OWL-Lite.
     *
     * @exception Throwable if an error occurs
     */
    public void testSeparationLite2() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );
	
	URI uri = new URI("http://example.org/test-onto-sep-lite-2"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
       
	
	/* Add a class */
	uri = new URI("http://example.org/test-onto#name1"); 
	OWLClass clazz = TestUtils.addClass( ontology, factory, changeVisitor, uri );

	/* Add an individual */
	uri = new URI("http://example.org/test-onto#name2"); 
	/* Make sure it has a class */
	OWLIndividual ind = 
	    TestUtils.addIndividual( ontology, factory, changeVisitor, uri );
	AddIndividualClass aic = 
	    new AddIndividualClass( ontology, 
				    ind, 
				    factory.getOWLThing(), 
				    null );
	aic.accept( changeVisitor );
	
	/* Ontology should be Lite */
	assertTrue( sv.isOWLLite( ontology ) );
    }

    /**
     * 
     *
     */
    public void setUp() {
	try {
	    sv = new SpeciesValidator();
	    sv.setReporter( new ValidatorLogger() );
	    
	    URI uri = null;
	    try {
		uri = new URI("http://example.org/test#name"); 
	    } catch (URISyntaxException ex) {
		/* Ignore */
	    }
	    entities = new OWLEntity[4];
	    
	    entities[0] = factory.getOWLClass( uri );
	    entities[1] = factory.getOWLIndividual( uri );
	    entities[2] = factory.getOWLObjectProperty( uri );
	    entities[3] = factory.getOWLDataProperty( uri );
	} catch (OWLException ex) {
	}
    }

    private void testSeparation(int j, int[] indices) throws Throwable
    {
	URI uri = new URI("http://example.org/test-onto" + j); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	/* This relies on us knowing that the ontology actually
	   implements the given interface, which it does do for our
	   simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
      

	/* Add the required objects */
	for (int i=0; i<indices.length; i++) {
	    OntologyChange evt = new AddEntity( ontology,
						entities[i],
						null );
	    evt.accept( changeVisitor );
	    
	}

	/* Ontology should be Full */
	assertTrue( !sv.isOWLLite( ontology ) );
	assertTrue( !sv.isOWLDL( ontology ) );
	assertTrue( sv.isOWLFull( ontology ) );
    }

    /**
     * Test that ontologies with unseparated namespaces are in OWL-Full.
     *
     * @exception Throwable if an error occurs
     */
    public void testSeparationFull() throws Throwable
    {
	setUp();
	int[][] entityIndices = {{0,1}, {0,2}, {0,3},
				 {1,2}, {1,3}, {2,3},
				 {0,1,2}, {0,2,3}, {1,2,3},
				 {0,1,2,3}};
	for ( int i = 0; i<entityIndices.length; i++ ) {
	    testSeparation(i, entityIndices[i]);
	}
    }


} // NamespaceSeparationTest



/*
 * ChangeLog
 * $Log: NamespaceSeparationTest.java,v $
 * Revision 1.3  2005/06/10 12:20:35  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/11/03 17:51:54  sean_bechhofer
 * Updates to validation tests.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.3  2003/05/19 12:01:19  seanb
 * no message
 *
 * Revision 1.2  2003/02/19 16:01:06  seanb
 * Fixing broken tests.
 *
 * Revision 1.1  2003/02/12 09:58:26  seanb
 * Moving Tests.
 *
 * Revision 1.4  2003/02/07 18:42:35  seanb
 * no message
 *
 * Revision 1.3  2003/02/06 16:41:42  seanb
 * More enhancements to validation
 *
 * Revision 1.2  2003/02/06 10:28:40  seanb
 * Changes in DataFactory implementation.
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 */
