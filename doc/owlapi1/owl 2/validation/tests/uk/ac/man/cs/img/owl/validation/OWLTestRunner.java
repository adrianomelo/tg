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
import java.util.Collection;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.man.cs.img.owl.validation.SpeciesValidator; 
import org.semanticweb.owl.validation.SpeciesValidatorReporter; 

import uk.ac.man.cs.img.owl.validation.ValidatorLogger; 
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.semanticweb.owl.io.Parser;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.util.OWLConnection;
import org.apache.log4j.Logger;

import uk.ac.man.cs.img.owl.test.OWLTest;
import uk.ac.man.cs.img.owl.test.OWLTestParser;

import uk.ac.man.cs.img.owl.test.OWLTestVocabularyAdapter;
import java.io.Writer;
import java.net.URLEncoder;
import org.apache.log4j.BasicConfigurator;
import java.io.OutputStreamWriter;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;

/**
 * Tests concerning the identification of species. Dodgy bespoke class
 * that produces output suitable for mangling into the WG RDF-based
 * result set.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLTestRunner.java,v 1.4 2005/06/10 12:20:35 sean_bechhofer Exp $
 */

public class OWLTestRunner 
{
    static Logger logger = Logger.getLogger(OWLTestRunner.class);

    private OWLConnection connection;
    private PrintWriter htmlResults;
    
    public OWLTestRunner( OWLConnection connection ) {
	this.connection = connection;
    }

    public OWLTestRunner() {
	/* If no connection given, tries the default one. */
	try {
	    connection = OWLManager.getOWLConnection();
	} catch ( OWLException e ) {
	    connection = null;
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
    
    /**
     * Check that the files referred to are ok.
     *
     * @exception Throwable if an error occurs
     */
    public void runTest( URI testURL, Writer h ) throws Throwable
    {
	/* Parse the test */
	htmlResults = new PrintWriter( h );
	
	OWLTestParser parser = new OWLTestParser();
	OWLTest test = parser.parseTest( testURL );

	SpeciesValidator sv = new SpeciesValidator();
	sv.setConnection( connection );

	SpeciesValidatorReporter rep = new SpeciesValidatorReporter() {

		public void message( String str ) {
		    htmlResults.println( "<li>" + str + "</li>");
		}
		
		public void explain( int l, String str ) {
		    htmlResults.println( "<li>" + SpeciesValidator.level( l ) + ":\t" + str + "</li>");
		}

		public void explain( int l, int code, String str ) {
		    htmlResults.println( "<li>" + SpeciesValidator.level( l ) + ":\t" + str + "</li>");
		}

		/* Inform the reporter of the ontology that's being validated */
		public void ontology( OWLOntology ontology ) {
		}
		
		/* Inform the reporter that validation has finished with the given
		 * result */
		public void done( String level ) {
		}
	    };
	sv.setReporter( rep );	
	boolean pass = true;

	htmlResults.println( "<h2>Test: <a href='" + 
			     test.getURI() + "'>" + 
			     test.getURI() + "</a></h2>" );
	
	for ( Iterator it = test.getDocuments().iterator();
	      it.hasNext(); ) {
	    
	    String doc = ((String) it.next());
	    Set levels = (Set) test.getDocumentLevels().get( doc );
	    
	    URI ontoURI = new URI( doc + "" );
	    htmlResults.println("<hr/>");
	    
	    if (levels!=null ) {
		if ( levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLLite() ) ) {
		    htmlResults.println( "<h3>Ontology: <a href='" + 
					 doc + "'>" + 
					 doc + "</a> Lite</h3>");
		    htmlResults.println("<p><pre>");
		    
		    boolean ok = true;
		    htmlResults.println("Checking Lite");
		    htmlResults.println("</p><ul>");

		    dropAllOntologies();
		    ok = ok && sv.isOWLLite( ontoURI );
		    htmlResults.println("</ul><p>");
		    
		    if ( ok ) {
			htmlResults.println( doc + " OK</p>");
		    } else {
			htmlResults.println( doc + " BAD</p>");
			pass = false;
		    }
		    htmlResults.println("</pre></p>");
		} else if ( levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLDL() ) ) {
		    htmlResults.println( "<h3>Ontology: <a href='" + 
					 doc + "'>" + 
					 doc + "</a> DL</h3>");
		    htmlResults.println("<p><pre>");

		    boolean ok = true;
		    htmlResults.println("Checking Not Lite");
		    htmlResults.println("</p><ul>");

		    dropAllOntologies();
		    ok = ok && !sv.isOWLLite( ontoURI );
		    htmlResults.println("</ul><p>");
		    htmlResults.println("Checking DL");
		    htmlResults.println("</p><ul>");

		    dropAllOntologies();
		    ok = ok && sv.isOWLDL( ontoURI );
		    htmlResults.println("</ul><p>");
		    if ( ok ) {
			htmlResults.println( doc + " OK</p>");
		    } else {
			htmlResults.println( doc + " BAD</p>");
			pass = false;
		    }
		    htmlResults.println("</pre></p>");

		} else if ( levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLFull() ) ) {
		    htmlResults.println( "<h3>Ontology: <a href='" + 
					 doc + "'>" + 
					 doc + "</a> Full</h3>");
		    htmlResults.println("<p><pre>");

		    boolean ok = true;

		    htmlResults.println("Checking Not Lite");
		    htmlResults.println("</p><ul>");

		    dropAllOntologies();
		    ok = ok && !sv.isOWLLite( ontoURI );
		    htmlResults.println("</ul><p>");
		    htmlResults.println("Checking Not DL");
		    htmlResults.println("</p><ul>");

		    dropAllOntologies();
		    ok = ok && !sv.isOWLDL( ontoURI );
		    htmlResults.println("</ul><p>");

		    htmlResults.println("Checking Full");
		    htmlResults.println("</p><ul>");

		    dropAllOntologies();
		    ok = ok && sv.isOWLFull( ontoURI );
		    htmlResults.println("</ul><p>");

		    if ( ok ) {
			htmlResults.println( doc + " OK</p>");
		    } else {
			htmlResults.println( doc + " BAD</p>");
			pass = false;
		    }
		    htmlResults.println("</pre></p>");
		}
	    }
	}
	htmlResults.println("<hr/>");

	if (pass) {
	    pass( test.getURI() );
	} else {
	    fail( test.getURI() );
	}
    }

    private void pass( String uri ) {
	htmlResults.println("<p><a href='" + uri + "'>" + uri + "</a>: <strong><span style='color:green'>Pass</span></strong></p>");
    }
    
    private void fail( String uri ) {
	htmlResults.println("<p><a href='" + uri + "'>" + uri + "</a>: <strong><span style='color:red'>Fail</span></strong></p>");
    }
    
    public static void main(String[] args){
	BasicConfigurator.configure();
	try {
	    Writer htmlReport = new OutputStreamWriter( System.out );

	    PrintWriter pw = new PrintWriter( htmlReport );
	    pw.println("<html>");
	    pw.println("<body>");

	    /** App should work on arbitrary OWLConnections */
	    OWLConnection connection = OWLManager.getOWLConnection();
	    
	    OWLDataFactory fact = connection.getDataFactory();
	    
	    OWLTestRunner otr = new OWLTestRunner( connection );
	    otr.runTest( new URI(args[0]), htmlReport );
	    
	    pw.println("</body>");
	    pw.println("</html>");
	    
	    htmlReport.close();
	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
    }





} // OWLTestRunner



/*
 * ChangeLog
 * $Log: OWLTestRunner.java,v $
 * Revision 1.4  2005/06/10 12:20:35  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.2  2003/12/19 12:04:17  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/10/02 14:33:06  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.1  2003/10/02 09:37:24  bechhofers
 * Cleaning up access to Connection. Addition of Servlet Test validator.
 *
 * Revision 1.2  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.1  2003/09/11 15:33:42  bechhofers
 * Bespoke tests to provide WG compatible output.
 *
 * Revision 1.9  2003/08/28 14:27:13  bechhofers
 * Additional logging for tests.
 *
 * Revision 1.8  2003/08/28 10:29:04  bechhofers
 * Updating parser to improve validation. Addition of new consumer with
 * simple triple model.
 *
 * Revision 1.7  2003/06/16 13:04:26  seanb
 * Adding Tests
 *
 * Revision 1.6  2003/06/06 14:27:34  seanb
 * Utilities for processing tests.
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
