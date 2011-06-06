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

package uk.ac.man.cs.img.owl.inference; // Generated package name

import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.inference.OWLConsistencyChecker; 

import uk.ac.man.cs.img.owl.validation.ValidatorLogger; 
import uk.ac.man.cs.img.owl.test.OWLTestParser;
import java.util.Map;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.semanticweb.owl.io.Parser;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.util.OWLConnection;
import org.apache.log4j.Logger;

import uk.ac.man.cs.img.owl.test.OWLTest;
import uk.ac.man.cs.img.owl.test.OWLTestVocabularyAdapter;

/**
 * Tests concerning the identification of species. 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLTestTest.java,v 1.3 2005/06/10 12:20:32 sean_bechhofer Exp $
 */

public class OWLTestTest extends TestCase
{
    static Logger logger = Logger.getLogger(OWLTestTest.class);

    private OWLTest test;
    private OWLConnection connection;
    private OWLConsistencyChecker checker;

    /* If set to true, the test will consider that a result of UNKNOWN is acceptable. */
    private boolean lax = false;

    public OWLTestTest( OWLConnection connection,
			OWLConsistencyChecker checker,
			OWLTest test,
			boolean lax ) {
	super("OWLTest " + test.getURI());
	this.test = test;
	this.connection = connection;
	this.checker = checker;
	this.lax = lax;
    }
    
    /**
     * Returns a TestSuite containing all the test cases. Should be
     * given a connection and the URI of file that lists Manifest
     * files. Each line in the file should contain a single URI which
     * is the URI of a Manifest for an OWL test. For any OWL
     * ontologies listed in the manifest, the test checks that the
     * ontology is indeed of the species that it claims to be.
     * @param connection an <code>OWLConnection</code> value
     * @param manifests a <code>String</code> value.
     * @param Set a set of <code>OWL Tests</code>
     * @param lax if set to true, the tests will pass if UNKNOWN is returned.
     
     * @return a <code>Test</code> value
     */
    public static Test suite( OWLConnection connection,
			      OWLConsistencyChecker checker,
			      Set tests,
			      boolean lax,
			      boolean datatypes,
			      boolean liteOnly) {

	TestSuite suite = new TestSuite ("OWLTest");
	
	try {
	    for (Iterator it = tests.iterator(); it.hasNext() ; ) {
		OWLTest test = (OWLTest) it.next();
		if ( test.getType().equals( OWLTestVocabularyAdapter.INSTANCE.getConsistencyTest() ) ||
		     test.getType().equals( OWLTestVocabularyAdapter.INSTANCE.getInconsistencyTest() ) ) {
		    Set levels = (Set) test.getDocumentLevels().get( test.getURI() );

		    if (datatypes || test.getDatatypes().isEmpty()) {
			if ( (liteOnly && levels!=null && levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLLite() ) ) ||
			     (levels!=null) && ( levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLLite() ) ||
						 levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLDL() ) ) ) {
			    suite.addTest( new OWLTestTest( connection, checker, test, lax ) );
			}
		    }
		}
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	return suite;
    }

    /**
     * 
     *
     * @exception Throwable if an error occurs
     */
    public void runTest() throws Throwable
    {

	for ( Iterator it = test.getDocuments().iterator();
	      it.hasNext(); ) {

	    String doc = ((String) it.next());

	    /* First off, make sure that the connection has *no*
	     * ontologies in it. We need to do this as a number of the
	     * tests use the same logical URI for ontologies -- this
	     * causes problems. By clearing them all out, we ensure that
	     * everything will be fine. Probably. */
	    
	    /* Note that we have to do this for each document, as
	     * parsing one of the test documents may drag in another
	     * via an import. Phew! */


	    dropAllOntologies();
	    
	    String role = (String) test.getDocumentRoles().get ( doc );
	    if ( role.equals(OWLTestVocabularyAdapter.INSTANCE.getInputDocument() ) ) {
		String type = test.getType();
		if ( type.equals( OWLTestVocabularyAdapter.INSTANCE.getConsistencyTest() ) ||
		     type.equals( OWLTestVocabularyAdapter.INSTANCE.getInconsistencyTest() ) ) {
		    /* We can deal with this .*/
		    
		    logger.debug(doc + "");
		    URI ontoURI = new URI( doc + "" );
		    
		    /* Parse the ontology */
		    Parser rdfParser = new OWLRDFParser();
		    /* Set the connection */
		    rdfParser.setConnection( connection );

// 		    OWLOntology onto = 
// 			connection.openOntologyPhysical( new URI(doc+"") );
// 		    OWLOntology onto = connection.createOWLOntology( doc+"", 
// 								     doc+"" );
		    URI physicalURIObject = null;
		    physicalURIObject = new URI( doc );
		    OWLOntology onto = 
			rdfParser.parseOntology(physicalURIObject);
		    assertNotNull ( onto );
		    
		    int result = checker.consistency( onto );
		    if (type.equals( OWLTestVocabularyAdapter.INSTANCE.getConsistencyTest() ) ) {
			/* Passes the test if we get CONSISTENT or UNKNOWN
			 * if we're being lax. Means that the
			 * trivial consistency checker will always pass
			 * the non-lax tests.*/
			assertTrue( doc+"" + " CONSISTENT", 
				    (result == checker.CONSISTENT) || 
				    (lax && result == checker.UNKNOWN) );
		    }
		    if (type.equals( OWLTestVocabularyAdapter.INSTANCE.getInconsistencyTest() ) ) {
			/* Passes the test if we get INCONSISTENT or UNKNOWN
			 * if we're being lax. Means that the
			 * trivial consistency checker will always pass
			 * the non-lax tests.*/
			assertTrue( lax?"lax ":"strict " + doc + " INCONSISTENT", 
				    (result == checker.INCONSISTENT) || 
				    (lax && result == checker.UNKNOWN) );
		    }
		}
	    }
	}
    }

    public void dropAllOntologies() throws OWLException {
	Set ontos = connection.getAllOntologies();
	for (Iterator ontoIt = ontos.iterator();
	     ontoIt.hasNext(); ) {
	    OWLOntology o = (OWLOntology) ontoIt.next();
	    connection.notifyOntologyDeleted( o );
	}
    }
   
} // OWLTestTest



/*
 * ChangeLog
 * $Log: OWLTestTest.java,v $
 * Revision 1.3  2005/06/10 12:20:32  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.8  2003/10/01 16:51:10  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.7  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.6  2003/09/11 15:34:30  bechhofers
 * Minor updates to consistency checking tests.
 *
 * Revision 1.5  2003/08/28 21:10:07  volzr
 * Consistent Usage of URI
 *
 * Revision 1.4  2003/08/28 10:29:04  bechhofers
 * Updating parser to improve validation. Addition of new consumer with
 * simple triple model.
 *
 * Revision 1.3  2003/06/16 13:04:26  seanb
 * Adding Tests
 *
 * Revision 1.2  2003/06/06 14:27:34  seanb
 * Utilities for processing tests.
 *
 * Revision 1.1  2003/06/03 17:03:33  seanb
 * Additional inference
 *
 * Revision 1.5  2003/05/06 14:25:48  seanb
 * Grabbing Manifest files
 *
 * Revision 1.4  2003/03/20 10:27:37  seanb
 * Changes to reflect movement of RDF parser.
 *
 * Revision 1.3  2003/02/18 18:43:38  seanb
 * Test improvements.
 *
 * Revision 1.2  2003/02/14 17:52:13  seanb
 * Improvements to Validation and updating data structures.
 *
 * Revision 1.1  2003/02/13 18:46:44  seanb
 * Improved validation and parsing.
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
