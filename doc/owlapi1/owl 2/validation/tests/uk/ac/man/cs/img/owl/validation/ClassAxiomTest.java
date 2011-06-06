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

import uk.ac.man.cs.img.owl.validation.ValidatorLogger; import java.util.HashSet;



/**
 * Tests concerning the identification of species w.r.t. Class Axioms
 *
 * @author Sean Bechhofer
 * @version $Id: ClassAxiomTest.java,v 1.3 2005/06/10 12:20:34 sean_bechhofer Exp $
 */

public class ClassAxiomTest extends ValidationTest
{

    private OWLEntity[] entities;
    private SpeciesValidator sv;

    public ClassAxiomTest( String testName, 
			   OWLDataFactory factory ) {
	super( testName, factory );
    }

    /** Returns a TestSuite containing all the test cases. */ 
    
    public static Test suite( OWLDataFactory fact) {
	TestSuite suite = new TestSuite ("ClassAxiom");

 	suite.addTest( new ClassAxiomTest( "testDisjointClasses",
 						    fact) );
	suite.addTest( new ClassAxiomTest( "testEquivalentClassesLite",
 						    fact) );
  	suite.addTest( new ClassAxiomTest( "testSubClassesLite",
  						    fact) );
	suite.addTest( new ClassAxiomTest( "testEquivalentClassesDL",
 						    fact) );
  	suite.addTest( new ClassAxiomTest( "testSubClassesDL",
  						    fact) );
	return suite;
    }

    /**
     * Check that a Disjoint Axiom moves us out of OWL-Lite.
     *
     * @exception Throwable if an error occurs
     */
    public void testDisjointClasses() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );

	URI uri = new URI("http://example.org/test-disj"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;

	addEntities( ontology, changeVisitor );
	
	/* Relies on knowing about a particular implementation, or at
	 * least how to get an empty set.  */
	Set set = new HashSet(); //ListFactory.getSet();
	
	set.add( testClasses[0] );
	set.add( testClasses[1] );
	
	OWLClassAxiom axiom = factory.getOWLDisjointClassesAxiom( set );
	OntologyChange evt = new AddClassAxiom( ontology,
						axiom,
						null );
	evt.accept( changeVisitor );
	
	/* Ontology should no longer be OWL Lite */
	assertTrue( !sv.isOWLLite( ontology ) );
    }

    /**
     * Check that an Equivalent Axiom is ok if it only uses class names. 
     *
     * @exception Throwable if an error occurs
     */
    public void testEquivalentClassesLite() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );

	URI uri = new URI("http://example.org/test-eqs-lite"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	addEntities( ontology, changeVisitor );

	/* Relies on knowing about a particular implementation, or at
	 * least how to get an empty set.  */
	Set set = new HashSet(); //ListFactory.getSet();
	set.add( testClasses[0] );
	set.add( testClasses[1] );

	/* Now add an equivalent axiom */
	
	OWLClassAxiom axiom = factory.getOWLEquivalentClassesAxiom( set );
	OntologyChange evt = new AddClassAxiom( ontology,
						axiom,
						null );
	evt.accept( changeVisitor );	
	/* Ontology should still be OWL Lite */
	assertTrue( sv.isOWLLite( ontology ) );
    }

    /**
     * Check that an Equivalent Axiom is in DL if it uses an expression. 
     *
     * @exception Throwable if an error occurs
     */
    public void testEquivalentClassesDL() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );

	URI uri = new URI("http://example.org/test-eqs-dl"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	addEntities( ontology, changeVisitor );

	Set set = new HashSet(); //ListFactory.getSet();

	set.add( testClasses[0] );
	set.add( testOr );
	
	OWLClassAxiom axiom = factory.getOWLEquivalentClassesAxiom( set );
	OntologyChange evt = new AddClassAxiom( ontology,
						axiom,
						null );
	evt.accept( changeVisitor );	
	/* Ontology should not be OWL Lite */
	assertTrue( !sv.isOWLLite( ontology ) );
    }


    /**
     * Check that a SubClass Axiom is ok if it only uses class names. 
     *
     * @exception Throwable if an error occurs
     */
    public void testSubClassesLite() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );

	URI uri = new URI("http://example.org/test-subclass-lite"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	addEntities( ontology, changeVisitor );

	OWLClassAxiom axiom = factory.getOWLSubClassAxiom( testClasses[0],
							   testClasses[1] );
	OntologyChange evt = new AddClassAxiom( ontology,
						axiom,
						null );
	evt.accept( changeVisitor );	
	/* Ontology should still be OWL Lite */
	assertTrue( sv.isOWLLite( ontology ) );
    }

    /**
     * Check that a SubClass Axiom is in DL if it uses an expression. 
     *
     * @exception Throwable if an error occurs
     */
    public void testSubClassesDL() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );

	URI uri = new URI("http://example.org/test-subclass-dl"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	addEntities( ontology, changeVisitor );
	
	OWLClassAxiom axiom = factory.getOWLSubClassAxiom( testClasses[0], 
							   testOr );
	OntologyChange evt = new AddClassAxiom( ontology,
				 axiom,
				 null );
	evt.accept( changeVisitor );	
	/* Ontology should not be OWL Lite */
	assertTrue( !sv.isOWLLite( ontology ) );
    }

} // ClassAxiomTest



/*
 * ChangeLog
 * $Log: ClassAxiomTest.java,v $
 * Revision 1.3  2005/06/10 12:20:34  sean_bechhofer
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
 * Revision 1.5  2003/02/06 18:39:17  seanb
 * Further Validation Tests.
 *
 * Revision 1.4  2003/02/06 16:41:42  seanb
 * More enhancements to validation
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
