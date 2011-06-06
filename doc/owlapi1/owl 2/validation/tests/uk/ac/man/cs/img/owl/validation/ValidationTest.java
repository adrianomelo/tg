/*
 * Copyright (C) 2003 The University of Manchester 
 * Copyright (C) 2003 The University of Karlsruhe
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 ****************************************************************
 * Source code information
 * -----------------------
 * Filename           $RCSfile: ValidationTest.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:20 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.validation; // Generated package name

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLDataFactory;
import java.net.URI;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLNot;
import java.util.Set;
import java.util.HashSet;
import java.net.URISyntaxException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.change.AddIndividualClass;

/**
 *  A super class that contains useful functionality for use during
 *  validation tests.
 *
 *
 * Created: Thu Feb 06 17:29:30 2003
 *
 * @author Sean Bechhofer
 * @version $Id: ValidationTest.java,v 1.1.1.1 2003/10/14 17:10:20 sean_bechhofer Exp $
 */

public class ValidationTest extends TestCase 
{
    
    protected OWLDataFactory factory;

    /* Some collections for general use. */
    protected OWLClass[] testClasses;
    protected OWLIndividual[] testIndividuals;
    protected OWLObjectProperty[] testObjectProperties;
    protected OWLDataProperty[] testDataProperties;
    protected OWLOr testOr;
    protected OWLAnd testAnd;
    protected OWLNot testNot;
    protected OWLEnumeration testEnum;
    protected OWLObjectValueRestriction testObjectValueRestriction;
    protected static int N = 5;

    
    public ValidationTest( String testName, 
			   OWLDataFactory factory ) {
	super( testName );
	this.factory = factory;
    }

    public void setUp() {
	/* Build some sample classes and individuals for use later
	 * on. */ 
	try { 
	    testClasses = new OWLClass[N]; 
	    testIndividuals = new OWLIndividual[N];	    
	    testObjectProperties = new OWLObjectProperty[N]; 
	    testDataProperties = new OWLDataProperty[N]; 
	    
	    for (int i = 0 ; i < N; i++ ) {
		URI uri = new URI("http://example.org/test-onto#class" + i); 
		testClasses[i] = factory.getOWLClass( uri );
	    }
	    for (int i = 0 ; i < N; i++ ) {
		URI uri = new URI("http://example.org/test-onto#individual" + i); 
		testIndividuals[i] = factory.getOWLIndividual( uri );
	    }
	    for (int i = 0 ; i < N; i++ ) {
		URI uri = new URI("http://example.org/test-onto#oprop" + i); 
		testObjectProperties[i] = factory.getOWLObjectProperty( uri );
	    }
	    for (int i = 0 ; i < N; i++ ) {
		URI uri = new URI("http://example.org/test-onto#dprop" + i); 
		testDataProperties[i] = factory.getOWLDataProperty( uri );
	    }

	    Set set = new HashSet();
	    set.add( testClasses[0] );
	    set.add( testClasses[1] );
	    testOr = factory.getOWLOr( set );

	    set = new HashSet();
	    set.add( testClasses[2] );
	    set.add( testClasses[3] );
	    testAnd = factory.getOWLAnd( set );

	    testNot = factory.getOWLNot( testClasses[4] );
	    
	    set = new HashSet();
	    set.add( testIndividuals[0] );
	    set.add( testIndividuals[1] );
	    
	    testEnum = factory.getOWLEnumeration( set );
	    
	    testObjectValueRestriction =
		factory.getOWLObjectValueRestriction( testObjectProperties[0],
							  testIndividuals[0]);
	    
	} catch (URISyntaxException ex) {
	} catch (OWLException ex) {
	}
    }

    /* Add all the setup classes and individuals to the ontology */
    public void addEntities( OWLOntology ontology,
			     ChangeVisitor visitor ) throws OWLException {
	for (int i = 0 ; i < N; i++ ) {
	    OntologyChange evt = new AddEntity( ontology,
						testClasses[i],
						null );
	    evt.accept( visitor );
	    
	    evt = new AddEntity( ontology,
						testIndividuals[i],
						null );
	    /* Make sure they're all typed */
	    AddIndividualClass aic = 
		new AddIndividualClass( ontology, 
					testIndividuals[i], 
					factory.getOWLThing(),
					null );
	    aic.accept( visitor );
	    
	    evt.accept( visitor );
	}
    }    
} // ValidationTest



/*
 * ChangeLog
 * $Log: ValidationTest.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:20  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/05/19 12:01:19  seanb
 * no message
 *
 * Revision 1.3  2003/04/10 12:16:40  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.2  2003/02/19 16:01:06  seanb
 * Fixing broken tests.
 *
 * Revision 1.1  2003/02/12 09:58:26  seanb
 * Moving Tests.
 *
 * Revision 1.2  2003/02/07 18:42:35  seanb
 * no message
 *
 * Revision 1.1  2003/02/06 18:39:17  seanb
 * Further Validation Tests.
 *
 */
