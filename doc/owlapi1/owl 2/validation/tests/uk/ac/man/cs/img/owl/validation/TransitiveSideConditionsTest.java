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
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.change.AddClassAxiom;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.AddSuperClass;
import org.semanticweb.owl.model.change.AddSuperProperty;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.SetTransitive;
import org.semanticweb.owl.model.change.SetFunctional;
import uk.ac.man.cs.img.owl.validation.SpeciesValidator; 
import org.semanticweb.owl.impl.model.OWLOntologyImpl;

import uk.ac.man.cs.img.owl.validation.ValidatorLogger; 


/**
 * Tests concerning the identification of species w.r.t. Class Axioms
 *
 * @author Sean Bechhofer
 * @version $Id: TransitiveSideConditionsTest.java,v 1.3 2005/06/10 12:20:35 sean_bechhofer Exp $
 */

public class TransitiveSideConditionsTest extends ValidationTest
{

    private OWLEntity[] entities;
    private SpeciesValidator sv;

    public TransitiveSideConditionsTest( String testName, 
			   OWLDataFactory factory ) {
	super( testName, factory );
    }

    /** Returns a TestSuite containing all the test cases. */ 
    
    public static Test suite( OWLDataFactory fact) {
	TestSuite suite = new TestSuite ("ClassAxiom");

 	suite.addTest( new TransitiveSideConditionsTest( "testTransitive",
 						    fact) );
	/* Commented out for now -- don't do this....*/
//  	suite.addTest( new TransitiveSideConditionsTest( "testTransitiveHard",
//  						    fact) );
	return suite;
    }

    /**
     * Check that the use of a transitive property in a cardinality
     * constraint is disallowed in DL/Lite.
     *
     * @exception Throwable if an error occurs
     */
    public void testTransitive() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );
	
	URI uri = new URI("http://example.org/test-trans"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	
	addEntities( ontology, changeVisitor );

	/* Add a description using a cardinality */
	OWLDescription desc = factory.getOWLObjectCardinalityAtMostRestriction( testObjectProperties[0],
										   1 );
	OntologyChange evt = new AddSuperClass( ontology,
						testClasses[0],
						desc,
						null );
	evt.accept( changeVisitor );
	assertTrue( sv.isOWLLite( ontology ) );

	/* Now make the property transitive */
	
	evt = new SetTransitive( ontology,
				 testObjectProperties[0],
				 true,
				 null );
	evt.accept( changeVisitor );
	
	/* Ontology should no longer be DL. */
	assertTrue( "Transitive side conditions violated but still DL!", !sv.isOWLDL( ontology ));
    }

    /**
     * Check that the use of a transitive property in a cardinality
     * constraint is disallowed in DL/Lite.
     *
     * @exception Throwable if an error occurs
     */
    public void testTransitiveHard() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	sv.setReporter( new ValidatorLogger() );
	
	URI uri = new URI("http://example.org/test-trans-hard"); 
	OWLOntology ontology = factory.getOWLOntology( uri, uri );
	assertTrue( sv.isOWLLite( ontology ) );
	/* This relies on us knowing that the ontology actually
	implements the given interface, which it does do for our
	simple implementation */ 
	ChangeVisitor changeVisitor = ((OWLOntologyImpl) ontology)/*.getChangeVisitor()*/;
	
	addEntities( ontology, changeVisitor );
	
	OntologyChange evt = new SetFunctional( ontology,
						testObjectProperties[0],
						true,
						null );
	evt.accept( changeVisitor );

	evt = new AddSuperProperty( ontology, 
				    testObjectProperties[1],
				    testObjectProperties[0],
				    null );
	evt.accept( changeVisitor );
	
	assertTrue( sv.isOWLLite( ontology ) );

	/* Now make the property transitive */
	
	evt = new SetTransitive( ontology,
				 testObjectProperties[1],
				 true,
				 null );
	evt.accept( changeVisitor );
	
	/* Ontology should no longer be DL. */
	assertTrue( "Transitive side conditions violated but still DL!", !sv.isOWLDL( ontology ));
    }

} // TransitiveSideConditionsTest



/*
 * ChangeLog
 * $Log: TransitiveSideConditionsTest.java,v $
 * Revision 1.3  2005/06/10 12:20:35  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/11/03 17:51:54  sean_bechhofer
 * Updates to validation tests.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:20  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.4  2003/09/10 09:01:29  bechhofers
 * Updates to parser to address bnode validation issues, in particular the
 * use of bnodes within equivalence/disjoint axioms and structure sharing.
 *
 * Revision 1.3  2003/06/06 14:27:34  seanb
 * Utilities for processing tests.
 *
 * Revision 1.2  2003/05/19 12:01:19  seanb
 * no message
 *
 * Revision 1.1  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 *
 */
