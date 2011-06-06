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

package org.semanticweb.owl.io;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.semanticweb.owl.io.Renderer;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import uk.ac.man.cs.img.owl.test.OWLTest;
import uk.ac.man.cs.img.owl.test.OWLTestParser;

/**
 * Tests a renderer by simply trying to render an ontology. Will
 * succeed if the thing doesn't throw an error. 
 *
 * @author Sean Bechhofer
 * @version $Id: RendererTest.java,v 1.4 2005/06/10 12:20:32 sean_bechhofer Exp $
 */

public class RendererTest extends TestCase {

    private OWLOntology ontology;
    private Renderer renderer;
    
    public RendererTest(String testName, 
			OWLOntology ontology, 
			Renderer renderer ) {
	super(testName);
	this.ontology = ontology;
	this.renderer = renderer;
    }
    
    public void runTest() throws Throwable {
	testRendering();
    }

    public void testRendering() throws Exception {
	StringWriter sw = new StringWriter();
	renderer.renderOntology( ontology, sw );
	/* Just check to see if the thing is null. */
	assertTrue( sw.toString()!=null );
    }


    /* Returns a suite of tests testing the particular renderer given. */ 
    public static TestSuite suite( String name, Renderer renderer ){
	BasicConfigurator.configure();
        TestSuite suite = new TestSuite ( name );

	/** App should work on arbitrary OWLConnections */
	/* Bit dodgy this... */
	OWLConnection connection = null;
	try {
	    connection = OWLManager.getOWLConnection();
	} catch ( OWLException e ) {
	    System.err.println("Could not obtain connection:");
	    System.err.println( e.getMessage());
	    System.exit(-1);
	}

	OWLDataFactory fact = connection.getDataFactory();
	
	String testManifests = null;
	Set tests = new HashSet();
	OWLTestParser parser = new OWLTestParser();
		    
	try {

	    java.net.URL testManifestsURL = 
		ClassLoader.getSystemResource ( System.getProperty("tests.manifests") );
	    //	    java.net.URI ontoURI = new java.net.URI( ontoURL.toString() );
	    testManifests = System.getProperty("tests.manifests");
	    //  	    BufferedReader br = 
	    //  		new BufferedReader( new InputStreamReader ( new URL( testManifests ).openStream() ) );
 	    BufferedReader br = 
 		new BufferedReader( new InputStreamReader ( testManifestsURL.openStream() ) );
	    // 	    testManifests = System.getProperty("tests.manifests");
	    //  	    BufferedReader br = 
	    //  		new BufferedReader( new InputStreamReader ( new URL( testManifests ).openStream() ) );
 	    String f = null;
 	    while ( ( f=br.readLine() ) != null ) {
 		if ( !f.startsWith("#") ) {
		    OWLTest test = parser.parseTest( new URI(f) );
		    
		    for ( Iterator it = test.getDocuments().iterator();
			  it.hasNext(); ) {
			String doc = ((String) it.next());
			
			URI ontoURI = new URI( doc + "" );
			
			/* Parse the ontology */
			Parser rdfParser = new OWLRDFParser();
			/* This will probably not work....*/
			rdfParser.setConnection( connection );
			
			URI physicalURIObject = null;
			physicalURIObject = new URI( doc );
			OWLOntology onto = 
			    rdfParser.parseOntology(physicalURIObject);
			suite.addTest( new RendererTest( doc, onto, renderer ) );
		    }
		}
	    }
	} catch (Exception ex) {
	}
        return suite;
    }

} // RendererTest

/*
 * ChangeLog
 * $Log: RendererTest.java,v $
 * Revision 1.4  2005/06/10 12:20:32  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/07/12 15:29:17  sean_bechhofer
 * Changes to test/build. Resources used in tests are now picked up from the
 * classpath. This should produce a more consistent test/build and allow ant
 * to be run from alternative directories.
 *
 * Revision 1.2  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/10/01 16:51:10  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.2  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.1  2003/08/29 08:12:53  bechhofers
 * Further rendering tests.
 *
 *
 *
 */
