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
 * Filename           $RCSfile: RACERConsistencyChecker.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/03/05 17:34:48 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.inference.dl; 
import java.util.Set;
import java.util.HashSet;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.inference.OWLConsistencyChecker;
import org.semanticweb.owl.model.change.OntologyChangeListener;

import uk.ac.man.cs.img.dig.reasoner.Reasoner;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.OntologyChangeSource;
import java.util.Map;
import java.util.HashMap;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.Iterator;
import uk.ac.man.cs.img.owl.io.dig1_0.RenderingVisitor;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import org.semanticweb.owl.validation.SpeciesValidator;
import org.semanticweb.owl.io.Renderer;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.List;
import org.semanticweb.owl.inference.OWLTaxonomyReasoner;
import org.semanticweb.owl.inference.OWLIndividualReasoner;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.helper.OntologyHelper;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
// Generated package name


/**
 * An OWL reasoner that uses a DIG reasoner to do the work.
 *
 * @author Sean Bechhofer
 * @version $Id: RACERConsistencyChecker.java,v 1.3 2004/03/05 17:34:48 sean_bechhofer Exp $
 */

public class RACERConsistencyChecker implements OWLConsistencyChecker, ReasonerProxy
{

    /* TODO: Adjust this to fit in with the OWLDLOntology. */

    /* A simple (and inefficient) OWL consistency checker that uses
       RACER (via DIG). This particular implementation will not track
       ontology updates in a sensible manner. Instead, it simply
       translates the entire ontology to the reasoner in one go. If
       any changes occur, it transmits it all over again. This is
       sufficient for the implementation of a simple checker
       though. */

    /* Not that the UNA and the encoding of oneOf may cause this to
     * produce duff answers. */


    static Logger logger = Logger.getLogger(RACERConsistencyChecker.class);


    /* The reasoner doing the reasoning */
    private Reasoner digReasoner;
    
    /* For rendering DIG concepts */
    private RenderingVisitor renderer;

    /* For checking the species */
    private SpeciesValidator validator;

    private OWLOntology ontology;

    /* Flags that indicate the current status of the ontology w.r.t
     * the statements that have been transmitted */

    private static int OK = 0;
    private static int NOTOK = 1;
    private static int UNKNOWN = 2;

    private int status; 

    /**
     * Create a new reasoning object. Expects a DIG reasoner that will
     * do the actual work of reasoning about the class structure.
     *
     * @param digReasoner a <code>Reasoner</code> value
     */
    public RACERConsistencyChecker( Reasoner digReasoner ) throws OWLException {
	this.digReasoner = digReasoner;
	this.renderer = new RenderingVisitor();
	this.validator = new uk.ac.man.cs.img.owl.validation.SpeciesValidator();
	/* Quiet validator */
	validator.setReporter(new SpeciesValidatorReporter() {
		public void ontology( OWLOntology onto ) {
		}
		
		public void done( String str ) {
		}
		
		public void message( String str ) {
		}
		
		public void explain( int l, String str ) {
		}

		public void explain( int l, int code, String str ) {
		}
	    });
	this.status = UNKNOWN;
    }

    private void initialiseReasoner() throws OWLException {
	/* Initialise the kb and make sure that top and bottom are in there. */
	tell( "<clearKB/>");
	tell( "<defconcept name=\"" + OWLVocabularyAdapter.INSTANCE.getThing() + "\"/>");
	tell( "<equalc><catom name=\"" + OWLVocabularyAdapter.INSTANCE.getThing() + "\"/><top/></equalc>");
	tell( "<defconcept name=\"" + OWLVocabularyAdapter.INSTANCE.getNothing() + "\"/>");
	tell( "<equalc><catom name=\"" + OWLVocabularyAdapter.INSTANCE.getNothing() + "\"/><bottom/></equalc>");
    }
    
    public void tell( String str ) throws OWLException {
	StringWriter sw = new StringWriter();
	sw.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
	sw.write( "<tells xmlns=\"http://dl.kr.org/dig/lang\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dl.kr.org/dig/lang dig.xsd\">" );
	sw.write( str );
	sw.write( "</tells>" );
	StringWriter response = new StringWriter();
	try {
	    digReasoner.request( new StringReader( sw.toString() ), response );
	} catch (Exception e) {
	    throw new OWLException( e.getMessage() );
	}
    }
    
    /* Send the ontology off to the DIG reasoner */
    private void transmitOntology() throws OWLException {
	initialiseReasoner();
	try {
	    /* Now tell the ontology to the reasoner. */
	    Renderer renderer =
		new uk.ac.man.cs.img.owl.io.dig1_0.Renderer();
	    StringWriter sw = new StringWriter();
	    renderer.renderOntology( ontology, sw );
	    
	    StringWriter response = new StringWriter();
	    digReasoner.request( new StringReader( sw.toString() ), response );
	} catch (Exception e) {
	    throw new OWLException( e.getMessage() );
	}
    }

    public int consistency( OWLOntology onto ) throws OWLException {
	ontology = onto;
	status = UNKNOWN;
	try {
	    checkStatus();
	    StringWriter sw = new StringWriter();
	    sw.write("<asks xmlns=\"http://dl.kr.org/dig/lang\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dl.kr.org/dig/lang dig.xsd\">");
	    
	    sw.write("<consistentKB id=\"q\"><top/></consistentKB>");
	    sw.write("</asks>");
	    
	    StringWriter response = new StringWriter();
	    try {
		digReasoner.request(new StringReader(sw.toString()), response);
	    } catch (Exception e) {
		throw new OWLException( e.getMessage() );
	    }
	    
	    
	    uk.ac.man.cs.img.dig.helper.Response serverResponse =
		new uk.ac.man.cs.img.dig.helper.Response(response.toString());
	    
	    logger.debug( response.toString() );
	    
	    org.w3c.dom.Element r =
		serverResponse.extractResponse("q");

	    if (r.getTagName().equals("true")) {
		    return CONSISTENT;
	    }		
	    if (r.getTagName().equals("false")) {
		    return INCONSISTENT;
	    }		
	    return UNKNOWN;
	} catch( OWLException ex ) {
	    /* You might want to explain a bit more here, but for
	     * simplicity's sake we just say "don't know" */
	    return UNKNOWN;
	}
    }

    /* Check the status of the ontology. If it's been changed, we need
     * to check its species. If that's ok, then we need to send it off
     * to the reasoner. */
    private void checkStatus() throws OWLException { 
	if (status==UNKNOWN) {
	    if (!validator.isOWLDL( ontology ) ) {
		status = NOTOK;
	    } else {
		/* The status was unknown (which means a change
		 * occurred), so we need to send it to the reasoner
		 * again. */
		status = OK;
		transmitOntology();
	    }
	}
	if (status==NOTOK) {
	    throw new ExpressivenessOutOfScopeException( "Ontology: " + ontology.getURI() + " is not OWL DL!\nThis reasoner can only reason about DL ontologies.");
	} 
    }

    private void notImplemented(String message) throws OWLException {
	throw new OWLException( message + ": Not yet implemented" );
    }

    public class ExpressivenessOutOfScopeException extends OWLException {
	ExpressivenessOutOfScopeException( String m ) {
	    super( m );
	}
    }

    public static void main( String[] args ) {
	try {
	    org.apache.log4j.BasicConfigurator.configure();
// 	    org.semanticweb.owl.util.OWLConnection connection = null;
// 	    Map parameters = new HashMap();
// 	    parameters.put(
// 			   org.semanticweb.owl.util.OWLManager.OWL_CONNECTION,
// 			   "org.semanticweb.owl.model.impl.OWLConnectionImpl");
// 	    try {
// 		connection = org.semanticweb.owl.util.OWLManager.getOWLConnection(parameters);
// 	    } catch ( OWLException e ) {
// 		System.err.println("Could not obtain connection");
// 		System.exit(-1);
// 	    }

	    java.net.URI uri = new java.net.URI( args[1] );
	    
// 	    OWLOntology onto = connection.createOWLOntology( uri,uri );
	    
	    /* Get hold of a reasoner */

	    Reasoner digReasoner = 
		new uk.ac.man.cs.img.dig.reasoner.impl.HTTPReasoner(args[0]);
	    
	    RACERConsistencyChecker reasoner = new RACERConsistencyChecker( digReasoner );

// 	    org.semanticweb.owl.io.owl_rdf.OWLRDFParser parser = 
// 		new org.semanticweb.owl.io.owl_rdf.OWLRDFParser();
// 	    org.semanticweb.owl.io.owl_rdf.OWLRDFErrorHandler handler = 
// 		new org.semanticweb.owl.io.owl_rdf.OWLRDFErrorHandler(){
// 		    public void owlFullConstruct( int code, 
// 						  String message ) 
// 			throws org.xml.sax.SAXException {
// 			System.out.println("FULL:    " + message);
// 		    }
// 		    public void error( String message ) 
// 			throws org.xml.sax.SAXException {
// 			throw new org.xml.sax.SAXException( message.toString() );
// 		    }
// 		    public void warning( String message ) 
// 			throws org.xml.sax.SAXException {
// 			System.out.println("WARNING: " + message);
// 		    }
// 		};
// 	    parser.setOWLRDFErrorHandler( handler );
	    
// 	    OWLOntology onto =
// 		parser.parseOntology(uri);

	    OWLOntology onto =
		OntologyHelper.getOntology( uri );
	    
	    System.out.println( "Checking Consistency..." );

	    int result = reasoner.consistency( onto );
	    System.out.println( ( result==CONSISTENT)?
				"\tConsistent":( result==INCONSISTENT?"\tInconsistent":"\tUnknown" ) );
	} catch ( ExpressivenessOutOfScopeException ex ) {
	    System.out.println( ex.getMessage() );
	} catch ( Exception ex ) {
	    System.out.println( ex.getMessage() );
	    //	    ex.printStackTrace();
	}
    }

    
} // RACERConsistencyChecker



/*
 * ChangeLog
 * $Log: RACERConsistencyChecker.java,v $
 * Revision 1.3  2004/03/05 17:34:48  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
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
 * Revision 1.9  2003/09/02 14:12:25  bechhofers
 * Fixing parse problem re annotations on annotation properties
 *
 * Revision 1.8  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.7  2003/07/09 13:58:30  bechhofers
 * docs update
 *
 * Revision 1.6  2003/07/08 18:01:07  bechhofers
 * Presentation Servlet and additional documentation regarding inference.
 *
 * Revision 1.5  2003/07/08 13:23:50  bechhofers
 * Further work on reasoning -- addition of taxonomy reasoners for classes.
 *
 * Revision 1.4  2003/06/25 16:04:57  bechhofers
 * Added removal events
 *
 * Revision 1.3  2003/06/19 13:33:32  seanb
 * Addition of construct checking.
 *
 * Revision 1.2  2003/06/16 12:50:41  seanb
 * Small change to reasoning
 *
 * Revision 1.1  2003/06/03 17:01:53  seanb
 * Additional inference
 *
 * Revision 1.3  2003/05/29 09:07:28  seanb
 * Moving RDF error handler
 *
 * Revision 1.2  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.1  2003/05/19 11:51:40  seanb
 * Implementation of reasoners
 *
 */
