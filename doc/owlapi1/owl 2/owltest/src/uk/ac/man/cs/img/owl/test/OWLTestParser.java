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
 * Filename           $RCSfile: OWLTestParser.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:17 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.test;

import java.util.HashMap;


import java.util.Map;
import java.net.URI;
import org.semanticweb.owl.io.ParserException;
import java.net.MalformedURLException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.unika.aifb.rdf.api.syntax.RDFParser;
import edu.unika.aifb.rdf.api.util.RDFManager;

import org.apache.log4j.BasicConfigurator;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.Reader;

/**
 * A simple RDF parser that will parse OWL Test Manifest files and
 * create OWLTest objects.
 *
 * @author <a href="mailto:seanb@cs.man.ac.uk">Sean Bechhofer</a>
 */

public class OWLTestParser {

    /**
     * Returns a collection of documents, indexed according to their level.
     */
    public OWLTest parseTest( URI uri )
	throws ParserException {

	OWLTest test = new OWLTest();
	test.setURI( uri.toString() );
	
	try {
	    RDFParser parser = new RDFParser();
	    InputSource source = new InputSource(new InputStreamReader(uri.toURL().openStream()));
	    source.setSystemId( uri.toString() );
	    OWLTestConsumer consumer = new OWLTestConsumer( test );
	    
	    parser.parse(source, consumer);
	} catch (SAXException e) {
	    throw new ParserException("Parsing failed due to malformed XML" , e);
	} catch (MalformedURLException e) {
	    throw new ParserException("URI is not a valid URL", e);
	} catch (IOException e) {		
	    throw new ParserException("Parsing failed due to IO Errors", e);
	} catch (Exception e) {
	    throw new ParserException("Parsing failed due to Errors", e);
	}
	
	return test;
    }
    
    public OWLTest parseTest( String base, Reader reader )
	throws ParserException {

	OWLTest test = new OWLTest();
	
	try {
	    RDFParser parser = new RDFParser();
	    InputSource source = new InputSource( reader );
	    source.setSystemId( base );
	    OWLTestConsumer consumer = new OWLTestConsumer( test );
	    
	    parser.parse(source, consumer);
	} catch (SAXException e) {
	    throw new ParserException("Parsing failed due to malformed XML" , e);
	} catch (MalformedURLException e) {
	    throw new ParserException("URI is not a valid URL", e);
	} catch (IOException e) {		
	    throw new ParserException("Parsing failed due to IO Errors", e);
	} catch (Exception e) {
	    throw new ParserException("Parsing failed due to Errors", e);
	}
	
	return test;
    }

    public static void main( String[] args ) {
	try {
	    BufferedReader br = 
		new BufferedReader( new InputStreamReader ( new URL( args[0] ).openStream() ) );
	    String f = null;
	    while ( ( f=br.readLine() ) != null ) {
		if ( !f.startsWith("#") ) {
		    
		    URI uri = new URI( f );
		    OWLTestParser parser = new OWLTestParser();
		    OWLTest test = parser.parseTest( uri );
		    if ( test.getType().equals( OWLTestVocabularyAdapter.INSTANCE.getInconsistencyTest() ) &&

			 !test.getDocumentLevels().get( test.getURI() ).equals( OWLTestVocabularyAdapter.INSTANCE.getOWLFull() ) ) {
			for (Iterator it = test.getDocuments().iterator(); it.hasNext() ; ) {
			    Object doc = it.next();
			    if ( test.getDocumentRoles().get( doc ).equals( OWLTestVocabularyAdapter.INSTANCE.getInputDocument() ) ) {
				System.out.println( doc );
			    }
			}
		    }
// 		    System.out.println("U: " + test.getURI());
// 		    System.out.println("T: " + test.getType());
// 		    for (Iterator it = test.getDocuments().iterator();
// 			 it.hasNext(); ) {
// 			String document = (String) it.next();
// 			System.out.println("   " + document);
// 			String level = (String) test.getDocumentLevels().get(document);
// 			if (level!=null) {
// 			    System.out.println( "   - " + level );
// 			}
// 			String role = (String) test.getDocumentRoles().get(document);
// 			if (role!=null) {
// 			    System.out.println( "   + " + role );
// 			}
// 		    }
// 		    System.out.println("==========================");
// 		    System.out.println();

		} // end of if ()
	    }	

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

}// OWLTestParser
