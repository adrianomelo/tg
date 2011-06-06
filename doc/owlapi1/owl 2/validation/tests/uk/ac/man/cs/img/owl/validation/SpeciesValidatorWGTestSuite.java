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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import java.net.URI;
import org.semanticweb.owl.model.OWLOntology;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import uk.ac.man.cs.img.owl.test.OWLTestParser;
import uk.ac.man.cs.img.owl.test.OWLTest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * A test suite that contains validation tests corresponding to the
 * WebOnt WG Tests. The system property tests.manifests is used to
 * find a list of test manifests to use.
 * 
 * The test suite can then be run using an appropriate JUnit test runner.
 *
 * @author Sean Bechhofer
 * @version $Id: SpeciesValidatorWGTestSuite.java,v 1.3 2005/06/10 12:20:35 sean_bechhofer Exp $
 */

public class SpeciesValidatorWGTestSuite extends TestCase 
{

    public SpeciesValidatorWGTestSuite (String name){
        super(name);
    }

    public static TestSuite suite(){
	//	PropertyConfigurator.configure("log4j.properties");
	BasicConfigurator.configure();
	TestSuite suite = new TestSuite ("SpeciesValidatorWG");

	try {

	    /** App should work on arbitrary OWLConnections */
	    OWLConnection connection = OWLManager.getOWLConnection();
	    
	    OWLDataFactory fact = connection.getDataFactory();
	    
	    String testManifests = null;
	    List tests = new ArrayList();
	    //	Set tests = new HashSet();
	    OWLTestParser parser = new OWLTestParser();
	    
	    testManifests = System.getProperty("tests.manifests");
 	    BufferedReader br = 
 		new BufferedReader( new InputStreamReader ( new URL( testManifests ).openStream() ) );
 	    String f = null;
 	    while ( ( f=br.readLine() ) != null ) {
 		if ( !f.startsWith("#") ) {
		    OWLTest test = parser.parseTest( new URI(f) );
		    tests.add( test );
 		} // end of if ()
 	    }	
	    suite.addTest(OWLTestTest.suite( connection, tests ));
	    
	} catch (Exception ex) {
	}
	
        return suite;
    }

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }

} // SpeciesValidatorWGTestSuite



/*
 * ChangeLog
 * $Log: SpeciesValidatorWGTestSuite.java,v $
 * Revision 1.3  2005/06/10 12:20:35  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2003/12/19 12:04:17  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1  2003/11/12 11:06:09  sean_bechhofer
 * Test Suite for WG tests
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.7  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.6  2003/08/28 14:27:14  bechhofers
 * Additional logging for tests.
 *
 * Revision 1.5  2003/06/20 14:07:51  seanb
 * Addition of some documentation. Minor tinkering.
 *
 * Revision 1.4  2003/06/06 14:27:34  seanb
 * Utilities for processing tests.
 *
 * Revision 1.3  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.2  2003/02/12 16:11:34  seanb
 * Adding OWL tests.
 *
 * Revision 1.1  2003/02/12 09:58:26  seanb
 * Moving Tests.
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
