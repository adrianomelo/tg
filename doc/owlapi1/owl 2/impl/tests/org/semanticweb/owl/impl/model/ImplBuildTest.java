/*
 * Copyright (C) 2005, University of Manchester
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

package org.semanticweb.owl.impl.model; // Generated package name
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;

import org.semanticweb.owl.impl.model.OWLDataFactoryImpl;
import org.semanticweb.owl.model.OWLObjectTest;
import org.semanticweb.owl.model.OWLClassTest;
import org.semanticweb.owl.model.OWLIndividualTest;
import org.semanticweb.owl.model.OWLDataPropertyTest;
import org.semanticweb.owl.model.OWLObjectPropertyTest;
import org.semanticweb.owl.model.OWLDataFactoryTest;
//import org.semanticweb.owl.model.RDFParserTest;
import org.semanticweb.owl.model.helper.OntologyHelperTest;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.model.helper.OWLObjectVisitorAdapter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owl.impl.model.OWLOntologyImpl;
import org.semanticweb.owl.model.change.AddEntityTest;
import org.semanticweb.owl.model.change.AddAnnotationTest;
import org.semanticweb.owl.model.change.RemoveEntityTest;
import org.semanticweb.owl.model.change.PropertyFlagTest;
import org.semanticweb.owl.model.change.DeprecateFlagTest;
import org.semanticweb.owl.model.change.SuperPropertyTest;
import org.semanticweb.owl.model.change.InversePropertyTest;
import org.semanticweb.owl.model.change.PropertyInstanceTest;
import org.semanticweb.owl.model.change.ClassDefinitionTest;
import org.semanticweb.owl.model.change.ClassDefinitionAndAxiomTest;
import org.semanticweb.owl.model.change.UsageTest;
import org.semanticweb.owl.model.TestUtils;

/**
 * Regression test harness for basic implementation.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: ImplBuildTest.java,v 1.4 2005/06/10 12:20:32 sean_bechhofer Exp $
 */
public class ImplBuildTest extends TestCase {

	public ImplBuildTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("ImplBuild");

 		org.apache.log4j.BasicConfigurator.configure();

		OWLConnection connection = null;
		try {
		    Map parameters = new HashMap();
		    parameters.put(
				   OWLManager.OWL_CONNECTION,
				   "org.semanticweb.owl.impl.model.OWLConnectionImpl");
		    
		    connection = OWLManager.getOWLConnection(parameters);
		} catch ( OWLException e ) {
		    System.err.println("Could not obtain connection:");
		    System.err.println( e.getMessage());
		    System.exit(-1);
		}
		OWLDataFactory fact = connection.getDataFactory();
		//	OWLObjectVisitorAdapter visitor = new OWLObjectVisitorAdapter();
		suite.addTest(OWLDataFactoryTest.suite(fact));
		suite.addTest(OWLClassTest.suite(fact));
		suite.addTest(OWLIndividualTest.suite(fact));
		suite.addTest(OWLDataPropertyTest.suite(fact));
		suite.addTest(OWLObjectPropertyTest.suite(fact));
		String rdfSource = null;
		try {
		    rdfSource = System.getProperty("rdf.test");
		} catch (Exception ex) {
		}
		/* Removed to break dependency. This needs to go back
		 * in somewhere though. */
		//		suite.addTest(RDFParserTest.suite( connection, rdfSource ));
		/* Test the change visitor stuff */
		try {
		    OWLOntologyImpl ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    
		    suite.addTest(
				  AddEntityTest.suite(fact, ontoImpl, ontoImpl/*.getChangeVisitor()*/));

		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());

		    suite.addTest(
				  AddAnnotationTest.suite(fact, ontoImpl, ontoImpl/*.getChangeVisitor()*/));
		    
		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(
				  RemoveEntityTest.suite(
							 fact,
							 ontoImpl,
							 ontoImpl/*.getChangeVisitor()*/));
		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(
				  ClassDefinitionTest.suite(
							 fact,
							 ontoImpl,
							 ontoImpl/*.getChangeVisitor()*/));
		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(
				  ClassDefinitionAndAxiomTest.suite(
								    fact,
								    ontoImpl,
								    ontoImpl/*.getChangeVisitor()*/));
		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(
				  PropertyFlagTest.suite(
							 fact,
							 ontoImpl,
							 ontoImpl/*.getChangeVisitor()*/));
		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(
				  SuperPropertyTest.suite(
							 fact,
							 ontoImpl,
							 ontoImpl/*.getChangeVisitor()*/));
		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(
				  InversePropertyTest.suite(
							 fact,
							 ontoImpl,
							 ontoImpl/*.getChangeVisitor()*/));
		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(
				  DeprecateFlagTest.suite(
							 fact,
							 ontoImpl,
							 ontoImpl/*.getChangeVisitor()*/));
		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(
				  PropertyInstanceTest.suite(
							 fact,
							 ontoImpl,
							 ontoImpl/*.getChangeVisitor()*/));

		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(OntologyHelperTest.suite(fact,
							   ontoImpl,
							   ontoImpl/*.getChangeVisitor()*/));

		    ontoImpl =
			(OWLOntologyImpl) fact.getOWLOntology(
							      TestUtils.newURI(),
							      TestUtils.newURI());
		    suite.addTest(UsageTest.suite(fact,
						   ontoImpl,
						   ontoImpl/*.getChangeVisitor()*/));
		    
		} catch (OWLException ex) {
		}
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
} // ImplBuildTest

/* 
 * ChangeLog
 * $Log: ImplBuildTest.java,v $
 * Revision 1.4  2005/06/10 12:20:32  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/07/09 12:07:48  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.2  2004/03/30 17:46:37  sean_bechhofer
 * Changes to parser to support better validation.
 *
 *
 */
