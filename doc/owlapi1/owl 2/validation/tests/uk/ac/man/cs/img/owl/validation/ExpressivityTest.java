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
import java.util.Set;
import javax.swing.event.ChangeEvent;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.change.AddClassAxiom;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.OntologyChange;
import uk.ac.man.cs.img.owl.validation.SpeciesValidator; 
import org.semanticweb.owl.impl.model.OWLOntologyImpl;

import uk.ac.man.cs.img.owl.validation.ValidatorLogger; 
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.change.AddEnumeration;
import org.semanticweb.owl.model.OWLNot;
import org.semanticweb.owl.model.change.AddSuperClass;
import java.util.HashSet;



/**
 * Tests concerning the identification of species w.r.t. Class Axioms
 *
 * @author Sean Bechhofer
 * @version $Id: ExpressivityTest.java,v 1.3 2005/06/10 12:20:35 sean_bechhofer Exp $
 */

public class ExpressivityTest extends ValidationTest
{
    
    public ExpressivityTest( String testName, 
			     OWLDataFactory factory ) {
	super( testName, factory );
    }

    /** Returns a TestSuite containing all the test cases. */ 
    
    public static Test suite( OWLDataFactory fact) {
	TestSuite suite = new TestSuite ("Expressivity");

 	suite.addTest( new ExpressivityTest( "testEnumeration",
 						    fact) );
 	suite.addTest( new ExpressivityTest( "testNegation",
 						    fact) );
 	suite.addTest( new ExpressivityTest( "testOr",
 						    fact) );
 	suite.addTest( new ExpressivityTest( "testObjectValueRestriction",
 						    fact) );
	return suite;
    }

    /**
     * Check that a class with an enumeration moves us out of OWL Lite. 
     *
     * @exception Throwable if an error occurs
     */
    public void testEnumeration() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );

	URI uri = new URI("http://example.org/test-enum"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	   implements the given interface, which it does do for our
	   simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	addEntities( ontology, changeVisitor );

	
	OntologyChange evt = new AddEnumeration( ontology,
						 testClasses[0],
						 testEnum,
						 null );
	evt.accept( changeVisitor );
	
	/* Ontology should no longer be OWL Lite */
	assertTrue( !sv.isOWLLite( ontology ) );
    }

    /**
     * Check that the use of negation moves us out of OWL Lite. 
     *
     * @exception Throwable if an error occurs
     */
    public void testNegation() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );
	
	URI uri = new URI("http://example.org/test-neg"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	   implements the given interface, which it does do for our
	   simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	addEntities( ontology, changeVisitor );

	OntologyChange evt = new AddSuperClass( ontology,
						testClasses[0],
						testNot,
						null );
	evt.accept( changeVisitor );
	
	/* Ontology should no longer be OWL Lite */
	assertTrue( !sv.isOWLLite( ontology ) );
    }

    /**
     * Check that the use of an Or moves us out of OWL Lite. 
     *
     * @exception Throwable if an error occurs
     */
    public void testOr() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );
	
	URI uri = new URI("http://example.org/test-or"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	   implements the given interface, which it does do for our
	   simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;	
	addEntities( ontology, changeVisitor );

	OntologyChange evt = new AddSuperClass( ontology,
						testClasses[0],
						testOr,
						null );
	evt.accept( changeVisitor );
	
	/* Ontology should no longer be OWL Lite */
	assertTrue( !sv.isOWLLite( ontology ) );
    }

    /**
     * Check that the use of an individual value restriction moves us
     * out of OWL Lite.
     *
     * @exception Throwable if an error occurs
     */
    public void testObjectValueRestriction() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );
	
	URI uri = new URI("http://example.org/test-ovr"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );

	/* This relies on us knowing that the ontology actually
	   implements the given interface, which it does do for our
	   simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;	
	addEntities( ontology, changeVisitor );

	OntologyChange evt = new AddSuperClass( ontology,
						testClasses[0],
						testObjectValueRestriction,
						null );
	evt.accept( changeVisitor );
	
	/* Ontology should no longer be OWL Lite */
	assertTrue( !sv.isOWLLite( ontology ) );
    }

} // ClassAxiomTest



/*
 * ChangeLog
 * $Log: ExpressivityTest.java,v $
 * Revision 1.3  2005/06/10 12:20:35  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/11/03 17:51:54  sean_bechhofer
 * Updates to validation tests.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.2  2003/05/19 12:01:19  seanb
 * no message
 *
 * Revision 1.1  2003/02/12 09:58:26  seanb
 * Moving Tests.
 *
 * Revision 1.2  2003/02/06 18:39:17  seanb
 * Further Validation Tests.
 *
 * Revision 1.1  2003/02/06 16:44:26  seanb
 * More validation tests
 *
 * Revision 1.3  2003/02/06 14:29:40  seanb
 * Enhancements to validation
 *
 * Revision 1.2  2003/02/06 10:28:40  seanb
 * Changes in DataFactory implementation.
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 */
