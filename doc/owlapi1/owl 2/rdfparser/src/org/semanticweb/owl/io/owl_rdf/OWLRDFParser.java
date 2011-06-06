/*
 * Copyright (C) 2003, University of Karslruhe
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

package org.semanticweb.owl.io.owl_rdf;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

import org.semanticweb.owl.model.OWLOntology;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.unika.aifb.rdf.api.syntax.RDFParser;
import edu.unika.aifb.rdf.api.util.RDFManager;
import org.apache.log4j.Logger;
import org.semanticweb.owl.io.ParserException;
import org.semanticweb.owl.io.Parser;
import org.semanticweb.owl.model.OWLException;

import org.apache.log4j.BasicConfigurator;
import java.util.HashMap;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.util.URIMapper;
import java.net.URLConnection;


/**
 * Parser for the normative XML/RDF Syntax.
 * based on the vocabulary established 03.02.03 by
 * the W3C WebOnt WG.
 * 
 * @since 05.02.2003
 * @author <a href="mailto:volz@fzi.de">Raphael Volz</a>
 * 
 */
public class OWLRDFParser implements Parser, OWLRDFErrorConstants {

    OWLOntology onto;
    Map options = new HashMap();

    OWLRDFErrorHandler errorHandler;
    OWLConsumer consumer;

    /* Shouldn't really have to do this -- it's a bit messy. */
    RDFParser parser;

    /* Provides access to an implementation -- needed for creation of
     * the ontology objects that will be returned. */
    OWLConnection connection;
    boolean importing = true;
    
    static Logger logger = Logger.getLogger(OWLRDFParser.class);

    /** Set the connection used to create new ontologies. If no
     * connection is supplied, the parser will choose an
     * implementation as specified by the system property
     * <code>org.semanticweb.owl.util.OWLConnection</code>. If this is
     * unset and no connection is supplied, an error will be
     * raised. */

    public void setConnection( OWLConnection conn ) {
	this.connection = conn;
    }

    public OWLConnection getConnection() {
	return connection;
    }

    public void setOWLRDFErrorHandler( OWLRDFErrorHandler handler ) {
	this.errorHandler = handler;
    }

    protected void setConsumer( OWLConsumer consumer ) {
	this.consumer = consumer;
    }

    /* To provide parser callback access... */
    public boolean isAnonymousNodeURI( String uri ) {
	return parser.isAnonymousNodeURI( uri );
    }

    public OWLRDFParser() throws OWLException {
	/* Try and get a default connection implementation. */
	//	connection = OWLManager.getOWLConnection();
    }

    /** Parse the ontology from the given Reader. The given physical
     * URI should be used as the base of the ontology.
     */
    public OWLOntology parseOntology( Reader reader, URI physicalURI) throws ParserException {
	/* The assumption here is that the Reader already knows what
	 * it's doing w.r.t. character encoding. */
	if (connection==null) {
	    String message = "Parser has no connection set!";
	    throw new ParserException( message );
	}
	try {
	    OWLOntology onto = connection.createOntology( physicalURI, 
							  physicalURI );
	    parseOntology( onto,
			   reader,
			   physicalURI );
	} catch (ParserException ex) {
	    throw ex;
	} catch (OWLException ex) {
	    throw new ParserException( ex.getMessage() );
	}
	return onto;
    }

    public OWLOntology parseOntology(URI uri) throws ParserException {
	URI theRealURIToRetrieve = uri;
	try {
	    
	    if (options.containsKey( "uriMapper" ) ) {
		logger.debug( "Mapper found" );
		/* The parser has some information about how to map
		 * URIs */
		try {
		    URIMapper mapper = (URIMapper) options.get( "uriMapper" );
		    if ( mapper!=null ) {
			logger.debug( "Mapper non-null" );
			theRealURIToRetrieve = mapper.mapURI( uri );
			logger.debug( "Mapped URI: " + theRealURIToRetrieve.toString() );
			if ( theRealURIToRetrieve == null ) {
			    logger.debug( "Mapped URI null!" );
			    theRealURIToRetrieve = uri;
			}
		    }
		} catch (ClassCastException ex) {
		    /* Ignore this for now */
		}
	    }
	    
	    /* Trying to make sure that we handle content
	     * properly. Problems are caused when URIs don't tell us
	     * what encoding they're using.  */
	    //URLConnection connection = uri.toURL().openConnection();	    
	    /* Now use the URI that might have been mapped */
	    URLConnection connection = theRealURIToRetrieve.toURL().openConnection();
	    connection.addRequestProperty("Accept", "application/rdf+xml, application/xml; q=0.5, text/xml; q=0.3, */*; q=0.2");
	    String contentEncoding = connection.getContentEncoding();
	    /* Use UTF-8 by default if nothing has been
	     * specified. We're assuming pretty much that the server
	     * is telling us the appropriate information about the
	     * encoding. This should (?) at least allow us to handle
	     * ASCII... Note quite sure what happens here with file:
	     * URLs where the encoding is something else.... It
	     * probably breaks.  :-( */
	    if (contentEncoding == null) {
		contentEncoding = "UTF-8";
	    }
	    InputStreamReader isr = 
		new InputStreamReader( connection.getInputStream(),
				       contentEncoding );
	    /* Now we parse it, passing in the reader, but also the original uri */
	    return parseOntology( isr, uri );
	    //	    return parseOntology(new InputStreamReader(uri.toURL().openStream()), uri);
	} catch (MalformedURLException e) {
	    throw new ParserException("URI is not a valid URL: ", e, INVALID_URL);
	} catch (IOException e) {
	    throw new ParserException("IO troubles when reading from URL: " + 
				      theRealURIToRetrieve.toString(), e, IO_ERROR);
	}
    }

    /** Locally accessible parse method used during import parsing */
    protected void parseOntology(OWLOntology ontology, Reader reader, URI uri)
	throws ParserException {
	/* Should we set the physical URI here?? */
	onto = ontology;
	parser = new RDFParser();
	InputSource source = new InputSource(reader);
	try {
	    source.setSystemId(uri.toString());
	    if ( consumer==null ) {
		/* If we haven't got one set, create a default one. */
		consumer = new OWLConsumer(onto);
		/* Also set importing variable for consumer, default = true */
		consumer.setImporting(this.importing);
		
		/* Bit nasty -- need to tell the consumer
		 * about the parser in order to be able to do
		 * the check anonymous nodes call-back. This
		 * should probably happen in the parse method
		 * somewhere..
		 */
		consumer.setOWLRDFParser( this );
		if ( errorHandler!=null ) {
		    consumer.setOWLRDFErrorHandler( errorHandler );
		}
	    }
	    /* Now set up consumer options. */
	    if (options.containsKey("ignoreAnnotationContent")) {
		try {
		    Boolean bool = (Boolean) options.get("ignoreAnnotationContent");
		    if (bool!=null) {
			consumer.setIgnoreAnnotationContent( bool.booleanValue() );
		    } 
		} catch (ClassCastException ex) {
		    /* Ignore */
		}
	    }
	    if (options.containsKey("ignoreSchemaImports")) {
		try {
		    Boolean bool = (Boolean) options.get("ignoreSchemaImports");
		    if (bool!=null) {
			consumer.setIgnoreSchemaImports( bool.booleanValue() );
		    } 
		} catch (ClassCastException ex) {
		    /* Ignore */
		}
	    }
	    if (options.containsKey("fixSameAs")) {
		try {
		    Boolean bool = (Boolean) options.get("fixSameAs");
		    if (bool!=null) {
			consumer.setFixSameAs( bool.booleanValue() );
		    } 
		} catch (ClassCastException ex) {
		    /* Ignore */
		}
	    }
// 	    if (options.containsKey("laxThingHandling")) {
// 		try {
// 		    Boolean bool = (Boolean) options.get("laxThingHandling");
// 		    if (bool!=null) {
// 			consumer.setLaxThingHandling( bool.booleanValue() );
// 		    } 
// 		} catch (ClassCastException ex) {
// 		    /* Ignore */
// 		}
// 	    }
	} catch (OWLException e) {
	    throw new ParserException( e.getMessage() );
	}
	    
	try {
	    parser.parse(source, consumer);
	} catch (MalformedOWLConstructRDFException e) {
	    /* Specific error handling. */
	    onto = null;
	    consumer = null;
	    parser = null;
	    source = null;
	    throw new ParserException( e.getMessage() );
	} catch (OWLFullConstructRDFException e) {
	    /* Specific error handling. */
	    onto = null;
	    consumer = null;
	    parser = null;
	    source = null;
	    throw new ParserException( e.getMessage() );
	} catch (SAXException e) {
	    onto = null;
	    consumer = null;
	    parser = null;
	    source = null;
	    //e.printStackTrace();
	    throw new ParserException("Parsing failed due to XML Problem: " + e.getMessage(), e, XML_PROBLEM);
	} catch (IOException e) {		
	    throw new ParserException("Parsing failed due to IO Errors: ", e, IO_ERROR);
	}
	/* Reset the consumer to ensure it's available for garbage
	 * collection. */
	consumer = null;
    }
    
    /** Locally accessible parse method used during import parsing */
    protected void parseOntology(OWLOntology ontology, URI uri)
	throws ParserException {
	try {
	    URI theRealURIToRetrieve = uri;
	    if (options.containsKey( "uriMapper" ) ) {
		logger.debug( "Mapper found" );
		/* The parser has some information about how to map
		 * URIs */
		try {
		    URIMapper mapper = (URIMapper) options.get( "uriMapper" );
		    if ( mapper!=null ) {
			logger.debug( "Mapper non-null" );
			theRealURIToRetrieve = mapper.mapURI( uri );
			logger.debug( "Mapped URI: " + theRealURIToRetrieve.toString() );
			if ( theRealURIToRetrieve == null ) {
			    logger.debug( "Mapped URI null!" );
			    theRealURIToRetrieve = uri;
			}
		    }
		} catch (ClassCastException ex) {
		    /* Ignore this for now */
		}
	    }
	    parseOntology(ontology, new InputStreamReader(theRealURIToRetrieve.toURL().openStream()), uri);
	} catch (MalformedURLException e) {
	    throw new ParserException("URI is not a valid URL: ", e, INVALID_URL);
	} catch (IOException e) {
	    throw new ParserException("IO troubles when reading from URL: " + uri.toString(), e, URL_IO_ERROR);
	}
    }
    
    /**
     * @see org.semanticweb.owl.io.Options#setOptions(Map)
     */
    public void setOptions(Map options) {
	this.options = options;
    }
    
    /**
     * @see org.semanticweb.owl.io.Options#getOptions()
     */
    public Map getOptions() {
	return options;
    }
    
    public static void main( String[] args ) {
	try {
	    BasicConfigurator.configure();
	    OWLConnection connection = null;
	    try {
		connection = OWLManager.getOWLConnection();
	    } catch ( OWLException e ) {
		System.err.println("Could not obtain connection:");
		System.err.println( e.getMessage());
		System.exit(-1);
	    }

	    OWLRDFParser parser = new OWLRDFParser();
	    OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		    public void owlFullConstruct( int code, 
						  String message ) throws SAXException {
			System.out.println("FULL:    " + message);
		    }
		    public void owlFullConstruct( int code, 
						  String message,
						  Object obj ) throws SAXException {
			System.out.println("FULL:    " + message);
		    }
		    public void error( String message ) throws SAXException {
			throw new SAXException( message.toString() );
		    }
		    public void warning( String message ) throws SAXException {
			System.out.println("WARNING: " + message);
		    }
		};
	    parser.setOWLRDFErrorHandler( handler );

	    URI uri = new URI( args[0] );
	    OWLOntology onto = 
		connection.createOntology( uri, 
					   uri );
	    parser.parseOntology(onto, uri);
	    
	} catch ( Exception ex ) {
	    ex.printStackTrace();
	}
    }
    
    /**
     * Aditya 07/03/05: Added setting for turning on/off the loading/parsing 
     * of imported ontologies as well. 
     * @param importing
     */
    public void setImporting(boolean importing) {
    	this.importing = importing;
    }

}
