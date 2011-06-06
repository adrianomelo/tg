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
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.semanticweb.owl.io.Parser;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLConnection;
import org.apache.log4j.Logger;

import uk.ac.man.cs.img.owl.test.OWLTest;
import uk.ac.man.cs.img.owl.test.OWLTestVocabularyAdapter;
import java.io.Writer;
import java.net.URLEncoder;

/**
 * Tests concerning the identification of species. Dodgy bespoke class
 * that produces output suitable for mangling into the WG RDF-based
 * result set.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLTestForWG.java,v 1.3 2005/06/10 12:20:35 sean_bechhofer Exp $
 */

public class OWLTestForWG 
{
    static Logger logger = Logger.getLogger(OWLTestForWG.class);

    private SpeciesValidator sv;
    private String manifestURI;
    private String resultsURL;
    private OWLTest test;
    private OWLConnection connection;
    private PrintWriter rdfResults;
    private PrintWriter textResults;

    public OWLTestForWG( OWLConnection connection,
			 OWLTest test, 
			 Writer r,
			 Writer t,
			 String ru) {
	this.connection = connection;
	this.test = test;
	this.rdfResults = new PrintWriter( r );
	this.textResults = new PrintWriter( t );
	this.resultsURL = ru;
    }
    
    /**
     * Check that the files referred to are ok.
     *
     * @exception Throwable if an error occurs
     */
    public void runTest() throws Throwable
    {
	SpeciesValidator sv = new SpeciesValidator();
	
	SpeciesValidatorReporter rep = new SpeciesValidatorReporter() {

		public void message( String str ) {
		    textResults.println( "  " + str );
		}
    
		public void explain( int l, String str ) {
		    textResults.println( "  " + SpeciesValidator.level( l ) + ":\t" + str );
		}

		public void explain( int l, int code, String str ) {
		    textResults.println( "  " + SpeciesValidator.level( l ) + "[" + code + "]:\t" + str );
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

	String anchor = URLEncoder.encode( test.getURI() );
	textResults.println( "<a name=\"" + anchor + "\"></a>" );

	for ( Iterator it = test.getDocuments().iterator();
	      it.hasNext(); ) {
	    
	    String doc = ((String) it.next());
	    Set levels = (Set) test.getDocumentLevels().get( doc );
	    
	    URI ontoURI = new URI( doc + "" );
	    textResults.println( "++++ Checking " + test.getURI() + " ++++" );
	    
	    if (levels!=null ) {
		if ( levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLLite() ) ) {
		    textResults.println( " == " + doc + " Lite...");
		    boolean ok = true;
		    textResults.println(" Lite...");
		    ok = ok && sv.isOWLLite( ontoURI );
		    if ( ok ) {
			textResults.println( " " + doc + " OK");
		    } else {
			textResults.println( " " + doc + " BAD");
			pass = false;
		    }

		} else if ( levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLDL() ) ) {
		    textResults.println( " == " + doc + " DL...");
		    boolean ok = true;
		    textResults.println(" Not Lite...");
		    ok = ok && !sv.isOWLLite( ontoURI );
		    textResults.println(" DL...");
		    ok = ok && sv.isOWLDL( ontoURI );
		    if ( ok ) {
			textResults.println( " " + doc + " OK");
		    } else {
			textResults.println( " " + doc + " BAD");
			pass = false;
		    }
		} else if ( levels.contains( OWLTestVocabularyAdapter.INSTANCE.getOWLFull() ) ) {
		    textResults.println( " == " + doc + " Full...");
		    boolean ok = true;
		    textResults.println(" Not Lite...");
		    ok = ok && !sv.isOWLLite( ontoURI );
		    textResults.println(" Not DL...");
		    ok = ok && !sv.isOWLDL( ontoURI );
		    textResults.println(" Full...");
		    ok = ok && sv.isOWLFull( ontoURI  );
		    if ( ok ) {
			textResults.println( " " + doc + " OK");
		    } else {
			textResults.println( " " + doc + " BAD");
			pass = false;
		    }
		}
	    }
	}
	if (pass) {
	    pass( test.getURI() );
	} else {
	    fail( test.getURI() );
	}
    }

    private void pass( String uri ) {
	rdfResults.println("<results:TestRun>");
	rdfResults.println(" <results:system rdf:resource=\"#wonderweb\"/>");
	rdfResults.println(" <results:output rdf:resource=\"" + resultsURL + "#" + URLEncoder.encode( uri ) + "\"/>");
	rdfResults.println(" <rdf:type rdf:resource=\"http://www.w3.org/2002/03owlt/resultsOntology#PassingRun\"/>");
	rdfResults.println(" <results:test rdf:parseType=\"Resource\">");
	rdfResults.println("  <results:syntacticLevelTestFrom rdf:resource=\"" + uri + "\"/>");
	rdfResults.println(" </results:test>");
	rdfResults.println("</results:TestRun>");
    }

    private void fail( String uri ) {
	rdfResults.println("<results:TestRun>");
	rdfResults.println(" <results:system rdf:resource=\"#wonderweb\"/>");
	rdfResults.println(" <results:output rdf:resource=\"" + resultsURL + "#" + URLEncoder.encode( uri ) + "\"/>");
	rdfResults.println(" <rdf:type rdf:resource=\"http://www.w3.org/2002/03owlt/resultsOntology#FailingRun\"/>");
	rdfResults.println(" <results:test rdf:parseType=\"Resource\">");
	rdfResults.println("  <results:syntacticLevelTestFrom rdf:resource=\"" + uri + "\"/>");
	rdfResults.println(" </results:test>");
	rdfResults.println("</results:TestRun>");
    }
    
} // OWLTestForWG



/*
 * ChangeLog
 * $Log: OWLTestForWG.java,v $
 * Revision 1.3  2005/06/10 12:20:35  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
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
