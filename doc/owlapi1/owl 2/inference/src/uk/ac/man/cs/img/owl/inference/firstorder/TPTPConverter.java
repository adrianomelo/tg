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
 * Filename           $RCSfile: TPTPConverter.java,v $
 * Revision           $Revision: 1.4 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/03/30 17:46:37 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.inference.firstorder; // Generated package name

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.log4j.BasicConfigurator;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.io.owl_rdf.OWLRDFErrorHandler;
import org.xml.sax.SAXException;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFSVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLNamedObject;
import org.semanticweb.owl.model.OWLNot;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.AddIndividualClass;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.helper.OntologyHelper;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import uk.ac.man.cs.img.owl.io.tptp.Renderer;
import uk.ac.man.cs.img.owl.test.OWLTest;
import uk.ac.man.cs.img.owl.test.OWLTestParser;
import uk.ac.man.cs.img.owl.test.OWLTestVocabularyAdapter;
import java.io.FileWriter;

/**
 *  Given the URL of the manifest of an OWL Test, attempts to produce
 *  a TPTP file that can be used to verify the test.
 *
 * @author Sean Bechhofer
 * @version $Id: TPTPConverter.java,v 1.4 2004/03/30 17:46:37 sean_bechhofer Exp $
 */

public class TPTPConverter
{

    static final String owlTestURL = "http://www.w3.org/2002/03owlt/";

    public TPTPConverter() {
    }

    public static void main( String[] args ) {
	BasicConfigurator.configure();
	try {
	    convert( args[0] );
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static void convert( String uriString ) throws Exception {
	URI url = new URI( uriString );
	OWLTestParser otp = new OWLTestParser();
	
	OWLTest test = otp.parseTest( url );
	String testURI = test.getURI().toString();

	Set levels = (Set) test.getDocumentLevels().get( testURI );

	if (testURI.startsWith( owlTestURL ) ) {
	    testURI = testURI.substring( owlTestURL.length());
	}
	if (testURI.endsWith( "#test" ) ) {
	    testURI = testURI.substring( 0, testURI.length() - 5 );
	}
	testURI = testURI.replace('/','-');
	
	String fname = testURI + ".tptp";
	FileWriter fw = new FileWriter( fname );
	
	
	if (levels==null) {
	    levels = new HashSet();
	}
	PrintWriter headerWriter = new PrintWriter( fw );

 	headerWriter.println("% --------------------------------------------------------------------------");
// 	headerWriter.println("% File     : ");
// 	headerWriter.println("% Domain   : ");
// 	headerWriter.println("% Problem  : ");
// 	headerWriter.println("% Version  : ");
// 	headerWriter.println("% English  : ");
// 	headerWriter.println("");
// 	headerWriter.println("% Refs     : ");
// 	headerWriter.println("% Source   : ");
// 	headerWriter.println("% Names    : ");
// 	headerWriter.println("");
// 	headerWriter.println("% Status   : ");
// 	headerWriter.println("% Rating   : ");
// 	headerWriter.println("% Syntax   : ");
// 	headerWriter.println("");
// 	headerWriter.println("% Comments : ");

 	headerWriter.println("% TPTP Translation of OWL Test");
	headerWriter.println("% ");
	headerWriter.println("% Test URL: " + test.getURI());
	headerWriter.println("% ");

	SimpleDateFormat sdf = 
	    new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	headerWriter.println("% Translated: " + sdf.format(new Date()));
	headerWriter.println("% ");

	String description = 
	    (String) test.getDescriptions().get( test.getURI() );
	
	if ( description!=null ) {
	    headerWriter.println("% Test Description:");
	    headerWriter.print("% ");
	    for (int i=0; i<description.length(); i++) {
		char c = description.charAt( i );
		if ( c!='\n') {
		    headerWriter.print( c );
		} else {
		    headerWriter.println();
		    headerWriter.print("% ");
		}
	    }
	}

	headerWriter.println();

	if (levels.contains(OWLTestVocabularyAdapter.INSTANCE.getOWLLite())) {
	    headerWriter.println("% Test-level: OWL-Lite ");
	} else if (levels.contains(OWLTestVocabularyAdapter.INSTANCE.getOWLDL())) {
	    headerWriter.println("% Test-level: OWL-DL ");
	} else if (levels.equals(OWLTestVocabularyAdapter.INSTANCE.getOWLFull())) {
	    /* This shouldn't happen */
	    headerWriter.println("% Test-level: OWL-Full ");
	}

	headerWriter.println("% ");

	
 	headerWriter.println("% --------------------------------------------------------------------------");

	try {
	    if (test.getType().equals( OWLTestVocabularyAdapter.INSTANCE.getInconsistencyTest() ) || test.getType().equals( OWLTestVocabularyAdapter.INSTANCE.getConsistencyTest() ) ) {
		
		//		FileWriter fw = new FileWriter( fname );
		/* Find out what the file is and render it to the output. */
		for (Iterator it = test.getDocuments().iterator(); it.hasNext() ; ) {
		    String doc = (String) it.next();
		    if ( test.getDocumentRoles().get( doc ).equals( OWLTestVocabularyAdapter.INSTANCE.getInputDocument() ) ) {
			OWLOntology ontology = getOntology( doc );
			Renderer renderer = new Renderer();
			renderer.setNegate( false );
			renderer.renderOntology( ontology, fw );
		    }
		}
		fw.close();
	    } else if (test.getType().equals( OWLTestVocabularyAdapter.INSTANCE.getPositiveEntailmentTest() ) || test.getType().equals( OWLTestVocabularyAdapter.INSTANCE.getNegativeEntailmentTest() ) ) {
		//		FileWriter fw = new FileWriter( fname );
		/* Find out what the file is and render it to the output. */
		/* Lists to record stuff that gets passed into the renderers. */
		List names = new ArrayList();
		List strings = new ArrayList();
		List integers = new ArrayList();
		List others = new ArrayList();
		List types = new ArrayList();
		
		for (Iterator it = test.getDocuments().iterator(); it.hasNext() ; ) {
		    String doc = (String) it.next();
		    /* Premises gets renderered normally. */
		    
		    
		    if ( test.getDocumentRoles().get( doc ).equals( OWLTestVocabularyAdapter.INSTANCE.getPremiseDocument() ) ) {
			OWLOntology ontology = getOntology( doc );
			Renderer renderer = new Renderer();
			renderer.setNegate( false );
			/* Pass in the namespace list */
			renderer.renderOntology( ontology, fw, names, strings, integers, others, types );
		    }
		    /* Conclusions gets negated */
		    if ( test.getDocumentRoles().get( doc ).equals( OWLTestVocabularyAdapter.INSTANCE.getConclusionDocument() ) ) {
			OWLOntology ontology = getOntology( doc );
			Renderer renderer = new Renderer();
			renderer.setNegate( true );
			/* Again we pass in the namespace list which
			 * ensures that things get rendered
			 * consistently. */
			renderer.renderOntology( ontology, fw, names, strings, integers, others, types );
		    }
		}
		fw.close();
	    }
	} catch ( Renderer.BigCardinalityException ex ) {
	    fw.close();
	    /* Write a message on the file.  */
	    fw = new FileWriter( fname );
	    PrintWriter pw = new PrintWriter( fw );
	    pw.println("% BigCardinality");
	    pw.println("% This file is empty as the source KB involved");
	    pw.println("% a large cardinality expression.");
	    pw.println("% " + ex.getMessage() );
	    fw.close();
	}
    }

    public static OWLOntology getOntology( String uriString ) throws Exception {
	URI uri = new URI( uriString ) ;
	boolean neg = false;
	OWLRDFParser parser = new OWLRDFParser();
	parser.setConnection( OWLManager.getOWLConnection() );

	OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		public void owlFullConstruct( int code,
					      String message ) throws SAXException {
		    System.err.println( "WARNING -- OWL Full: " + message );
		}
		public void owlFullConstruct( int code,
					      String message,
					      Object obj ) throws SAXException {
		    System.err.println( "WARNING -- OWL Full: " + message );
		}
		public void error( String message ) throws SAXException {
		    throw new SAXException( "error: " + message );
		}
		public void warning( String message ) throws SAXException {
		    //message( message.toString() );
		}
	    };
	parser.setOWLRDFErrorHandler( handler );
	OWLOntology onto = // connection.createOWLOntology( uri,uri );
	    parser.parseOntology(uri);
	return onto;
    }

} // GatherTests




/*
 * ChangeLog
 * $Log: TPTPConverter.java,v $
 * Revision 1.4  2004/03/30 17:46:37  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.3  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.2  2003/12/03 17:40:09  sean_bechhofer
 * Removing unnecessary imports
 *
 * Revision 1.1  2003/11/28 10:27:27  sean_bechhofer
 * Converter for owl tests.
 *
 * Revision 1.10  2003/10/10 16:14:59  bechhofers
 * no message
 *
 * Revision 1.9  2003/10/03 10:00:04  bechhofers
 * Refactoring of RDFErrorHandler:
 *  o Addition of error codes for OWL Full situations
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
 * Revision 1.6  2003/09/22 13:21:44  bechhofers
 * Fixes to rendering and some extra helper functions.
 *
 * Revision 1.5  2003/08/28 21:10:07  volzr
 * Consistent Usage of URI
 *
 * Revision 1.4  2003/08/28 10:29:04  bechhofers
 * Updating parser to improve validation. Addition of new consumer with
 * simple triple model.
 *
 * Revision 1.3  2003/08/20 08:39:57  bechhofers
 * Alterations to tests.
 *
 * Revision 1.2  2003/06/11 16:50:29  seanb
 * Utility test processing classes for extracting, parsing and listing tests.
 *
 * Revision 1.1  2003/06/06 14:27:34  seanb
 * Utilities for processing tests.
 *
 * Revision 1.1  2003/05/06 14:25:48  seanb
 * Grabbing Manifest files
 *
 *
 */
