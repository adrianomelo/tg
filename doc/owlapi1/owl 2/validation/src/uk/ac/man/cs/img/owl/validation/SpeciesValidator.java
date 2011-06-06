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

package uk.ac.man.cs.img.owl.validation; 

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.helper.OntologyHelper;
import java.util.Set;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import org.semanticweb.owl.model.OWLNamedObject;
import java.io.Writer;
import java.io.Reader;
import java.io.PrintWriter;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import java.util.Map;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import java.net.URI;
import java.util.HashMap;
import org.semanticweb.owl.util.OWLManager;
import org.apache.log4j.BasicConfigurator;
import org.semanticweb.owl.io.simple.Renderer;
import java.io.StringWriter;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFSVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFVocabularyAdapter;
import org.semanticweb.owl.io.ParserException;
//import org.semanticweb.owl.io.MalformedOWLConstructRDFException;
//import org.semanticweb.owl.io.OWLFullConstructRDFException;
import org.semanticweb.owl.io.owl_rdf.OWLRDFErrorHandler;
import org.semanticweb.owl.io.owl_rdf.OWLRDFErrorConstants;
import org.xml.sax.SAXException;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.abstract_syntax.ObjectRenderer;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class provides functionality for identifying the particular
 * species that an ontology belongs to. See the {@link uk.ac.man.cs.img.owl.validation} documentation for further information.
 *
 * @author Sean Bechhofer
 * @version $Id: SpeciesValidator.java,v 1.13 2005/06/10 12:20:34 sean_bechhofer Exp $
 */

public class SpeciesValidator implements org.semanticweb.owl.validation.SpeciesValidator, org.semanticweb.owl.validation.OWLValidationConstants
{

    static Logger logger = Logger.getLogger(SpeciesValidator.class);
    static Logger resultLogger = Logger.getLogger(SpeciesValidator.class.getName() + "Results");
    static Logger mainLogger = Logger.getLogger(SpeciesValidator.class.getName() + "Main");

    private SpeciesValidatorReporter reporter;
    private Map options;

    protected static final int OTHER = 7; /* 0111 */

    private OWLConnection connection;

    /** Set the connection (e.g. the implementation that the validator
    will choose to use when constructing ontologies. */ 

    public void setConnection( OWLConnection connection ) {
	this.connection = connection;
    }

    private OWLOntology ontology;
    private Set allOntologies;

    private Set reservedVocabulary;

    /* Those things from the reserved vocabulary that can be defined
     * as classes. rdf:Statement, rdf:Seq, rdf:Bag, and rdf:Alt */
    private Set classOnlyVocabulary;

    /* Those things from the reserved vocabulary that can be defined
     * as classes. rdf:subject, rdf:predicate, rdf:object, and all the
     * container membership properties, i.e., rdf:_1, rdf:_2, etc. */
    private Set propertyOnlyVocabulary;

    /** Indicates whether the separation of names for classes,
     * individuals and properties has been observed */
    private boolean namespacesSeparated;

    /** Indicates whether the conditions regarding the usage and
     * redefinition of elements from the RDF, RDFS and OWL
     * vocabularies have been observed. */
    private boolean correctOWLUsage;

    private boolean correctOWLNamespaceUsage;

    /** Indicates whether all individuals have at least one explicit type. */
    private boolean individualsTyped;

    /* Indicates whether classAxioms from a particular species are
     * used. */
    private int classAxiomLevel;

    /* Indicates whether propertyAxioms from a particular species are
     * used. */
    private int propertyAxiomLevel;

    /* Indicates the level of expressivity used in expressions within
     * the ontology. */
    private int expressivityLevel;

    /* Indicates whether syntactic constraints have been violated
     * which pushes us into a particular level, e.g. untyped URIs =>
     * Full */
    private int syntaxLevel;

    /* Collections of URIs taken from the ontology.*/
    private Set allURIs;

    private Set classURIs;
    private Set individualURIs;
    private Set objectPropertyURIs;
    private Set dataPropertyURIs;
    private Set annotationPropertyURIs;

    /* There may still be some nasty corner cases that slip through
     * the net here.... */
    private Set datatypeURIs;

    private ObjectRenderer objectRenderer;

    private OWLRDFParser parser;

    /** Create a new validator. Will report to stdout by default. 
     * 
     *
     */
    public SpeciesValidator() throws OWLException {
	/* Sets up a default reporter that writes to stdout. */
	setReporter(new SpeciesValidatorReporter() {
		public void ontology( OWLOntology onto ) {
		    //System.out.println( onto.getURI() );
		}

		public void done( String str ) {
		    //		    System.out.println( str );
		}

		public void message( String str ) {
		    System.out.println( str );
		}
		
		public void explain( int l, int code, String str ) {
		    System.out.println( level( l ) + 
					" [" + readableCode( code ) + "]:\t" + 
					str );
		    //		    System.out.println( level( l ) + ":\t" + str );
		}
	    });
	reservedVocabulary
	    = OWLVocabularyAdapter.INSTANCE.getReservedVocabulary();

	classOnlyVocabulary 
	    = new HashSet();
	classOnlyVocabulary.add( OWLVocabularyAdapter.INSTANCE.getStatement() );
	classOnlyVocabulary.add( OWLVocabularyAdapter.INSTANCE.getSeq() );
	classOnlyVocabulary.add( OWLVocabularyAdapter.INSTANCE.getBag() );
	classOnlyVocabulary.add( OWLVocabularyAdapter.INSTANCE.getAlt() );

	propertyOnlyVocabulary 
	    = new HashSet();
	propertyOnlyVocabulary.add( RDFVocabularyAdapter.INSTANCE.getSubject() ) ;
	propertyOnlyVocabulary.add( RDFVocabularyAdapter.INSTANCE.getPredicate() );
	propertyOnlyVocabulary.add( RDFVocabularyAdapter.INSTANCE.getObject() );
	
	options = new HashMap();

	/* Get a default connection */
	connection = OWLManager.getOWLConnection();

	parser = new OWLRDFParser();
	/* Tell the parser to ignore annotation content. This is not
	 * needed for validation. */
	Map options = new HashMap();
	options.put("ignoreAnnotationContent", new Boolean( true ) );
	//	parser.setOptions( options );
    }

    /** Set the reporter that this speciesValidator will use. By
     * default, the validator will write to stdout. If you want to
     * stop this happening, set the reporter to null */
    public void setReporter( SpeciesValidatorReporter reporter ) {
        this.reporter = reporter;
    }

    /**
     * Provide an explanation as to why the validator considers the
     * ontology to be in a particular species.
     *
     * @param l an <code>int</code> value
     * @param str a <code>String</code> value
     */
    protected void explain( int l, int code, String str ) {
	if ( reporter!=null ) {
	    reporter.explain( l, code, str );
	}
    }

    /**
     * Write a message. 
     *
     * @param str a <code>String</code> value
     */
    protected void message( String str ) {
	if ( reporter!=null ) {
	    reporter.message( str );
	}
    }

    
    protected static String level( int l ) {
	if ( l==LITE ) {
	    return "OWL-Lite";
	} else if ( l==DL ) {
	    return "OWL-DL  ";
	} else if ( l==FULL ) {
	    return "OWL-Full";
	} else {
	    return "OTHER   ";
	}

    }

    /**
     * Set the ontology that the validator will work with. Note that
     * this performs some initialisation. This particular
     * implementation does not track ontology changes, so if the
     * ontology is changed before validation takes place, the results
     * may not be as expected.
     *
     *
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param checkImport if true, grab the imports closure and check
     * the species of any imported ontologies. If false, just look
     * here. Allows us to catch situations where an ontology is
     * imported that has a higher expressivity, but the classes
     * involved in that aren't explicitly used in the importer.
     */
    private int species( OWLOntology ontology, boolean checkImport ) 
    {
	int result = LITE;
	
	try {
	    this.ontology = ontology;
	    
	    logger.info( "Validating: " 
			 + (checkImport?"[imports] ":"")
			 + ontology.getURI() );
	    
	    if ( reporter!=null ) {
		reporter.ontology( ontology );
	    }
	    
	    /* Find the import closure */
	    this.allOntologies = OntologyHelper.importClosure( ontology );
	    
	    /* Do some initial processing */
	    
	    if (checkImport) {
		for (Iterator it = allOntologies.iterator();
		     it.hasNext(); ) {
		    OWLOntology importee = (OWLOntology) it.next();
		    logger.info( "  " + 
 				 importee.getURI().toString() );
		    SpeciesValidator sv = new SpeciesValidator();
		    sv.setReporter( reporter );
		    int importeeLevel = sv.species( importee, false );
		    logger.info( "Import: " + level( importeeLevel ) );
		    result = result | importeeLevel ;
		}
	    } else {
		gatherURIs();
		
		for ( Iterator it = classURIs.iterator();
		      it.hasNext(); ) {
		    logger.debug("C: " + it.next());
		}
		for ( Iterator it = individualURIs.iterator();
		      it.hasNext(); ) {
		    logger.debug("I: " + it.next());
		}
	    
		/* Set up all the variables */
		this.namespacesSeparated = true;
		this.correctOWLUsage = true;
		this.correctOWLNamespaceUsage = true;
		this.individualsTyped = true;
		this.classAxiomLevel = FULL;
		this.propertyAxiomLevel = FULL;
		this.expressivityLevel = FULL;
	    
		/* A helper used when reporting stuff. */
		objectRenderer = new ObjectRenderer( ontology );
	    
		/* Now do all the relevant checks */
		checkNamespaceSeparation();
		checkCorrectOWLUsage();
		checkCorrectOWLNamespaceUsage();
		/* This should be done during parsing */
		//	    checkIndividualTyping();
		checkClassAxioms();
		checkPropertyAxioms();
		checkExpressivity();
	    
		if ( !correctOWLNamespaceUsage ) {
		    /* If there are things in the OWL namespace, we're in
		     * OTHER. See
		     * http://lists.w3.org/Archives/Public/www-webont-wg/2003Feb/0157.html */
		    /* This doesn't seem right though. I think it's actually
		     * the case that any RDF document is an OWL FULL
		     * document. See Section 1.3 of the Overview. */
		    //	    result = OTHER; 	    
		    result = FULL; 
		}
		else if ( !namespacesSeparated || 
			  !correctOWLUsage ||
			  !individualsTyped ) {
		    /* If namespaces aren't separated, or redefinitions have
		     * occurred, or individuals aren't all explicitly typed, we're in Full */
		
		    result = FULL;
		} else {
		    /* Otherwise, it's the highest level that's used for
		     * classes, properties and expressivity */
		    result = ( classAxiomLevel | 
			       propertyAxiomLevel |
			       expressivityLevel );
		}
	    
		if ( reporter!=null ) {
		    reporter.done( level( result ) );
		}
	    }
	} catch (OWLException e) {
	    result = FULL;
	    reporter.explain( FULL, UNKNOWN,  "Exception occurred: " + e.getMessage() );
	}
	logger.info( "Result: " + level( result ) );

	return result;
    }
    
    /**
     * Set options for this validator
     * @param options a <code>Map</code> value. Should contain a map from {@link String String}s to {@link String String}s.
     */
    public void setOptions( Map opts ) {
	this.options.putAll( opts );
	//	options = new HashMap( options );
    };

    /**
     * 
     * Get options for this validator
     * @return a <code>Map</code> value. Contains a map from {@link String String}s to {@link String String}s.
     */
    public Map getOptions() {
	return options;
    }

    /** Record the fact that some particular syntax has been noticed. */
    private void setSyntaxLevel( int l ) {
	syntaxLevel = l;
    }

    
    
    /**
     * Parse an ontology from a given URI.
     *
     * @param handler an <code>OWLRDFErrorHandler</code> value
     * @param uri an <code>URI</code> value
     * @return an <code>OWLOntology</code> value
     * @exception ParserException if an error occurs
     * @exception OWLException if an error occurs
     */
    private OWLOntology parseFromURI( OWLRDFErrorHandler handler, 
				      URI uri ) throws ParserException, OWLException {
	parser.setOptions( options );
	parser.setConnection( connection );
	
	/* Error handler for the parser */
	parser.setOWLRDFErrorHandler( handler );
	
	//	OWLOntology onto = connection.createOWLOntology( uri,uri );
	OWLOntology onto = parser.parseOntology(uri);
	//	message( onto.toString() );
	return onto;
    }

    /**
     * Parse an ontology from a given Reader using the URI as the physical URI.
     *
     * @param handler an <code>OWLRDFErrorHandler</code> value
     * @param uri an <code>URI</code> value
     * @return an <code>OWLOntology</code> value
     * @exception ParserException if an error occurs
     * @exception OWLException if an error occurs
     */
    private OWLOntology parseFromURI( OWLRDFErrorHandler handler, 
				      Reader r,
				      URI uri ) throws ParserException, OWLException {
	parser.setOptions( options );
	parser.setConnection( connection );
	
	/* Error handler for the parser */
	parser.setOWLRDFErrorHandler( handler );
	
	//	OWLOntology onto = connection.createOWLOntology( uri,uri );
	OWLOntology onto = parser.parseOntology(r, uri);
	//	message( onto.toString() );
	return onto;
    }

    /**
     * Returns <code>true</code> if the ontology obtained by parsing
     * the URI is in OWL Lite. Will report findings to the reporter as
     * it goes. Note that the inner workings of the validator assume
     * that the ontology has <strong>not</strong> already been parsed.
     *
     * @param uri an <code>URI</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isOWLLite( URI uri ) {
	boolean result = false;
	try {
	    /* Handler that's strict about OWLFullExceptions */
	    syntaxLevel = LITE;
	    OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		    public void owlFullConstruct( int code,
						  String message ) throws SAXException {
			/* Doesn't throw an error, but keeps going.... */
			setSyntaxLevel( FULL );
			explain( FULL, code, message );
			//throw new OWLFullConstructRDFException( message );
		    }
		    public void owlFullConstruct( int code,
						  String message,
						  Object obj ) throws SAXException {
			this.owlFullConstruct( code, message );
		    }
		    public void error( String message ) throws SAXException {
			throw new SAXException( message.toString() );
		    }
		    public void warning( String message ) throws SAXException {
			message( message.toString() );
		    }
		};
	    OWLOntology o = parseFromURI( handler, uri );
	    int species = species( o,
				   true ) | syntaxLevel;
	    result = ( species == LITE );
	    releaseOntology( o );
	} catch ( ParserException ex) {
	    int code = UNKNOWN;
	    if (ex.getCode()!=0) {
		code = ex.getCode();
	    }
	    explain( OTHER, code, ex.getMessage() );
	} catch ( OWLException ex) {
	    explain( OTHER, UNKNOWN, ex.getMessage() );
	}
	resultLogger.info( uri + " <" + (result?"":"Not ") + "Lite>" );
	return result;
    }
    
    /**
     * Returns <code>true</code> if the ontology obtained by parsing
     * the URI is in OWL DL. Will report findings to the reporter as
     * it goes. Note that the inner workings of the validator assume
     * that the ontology has <strong>not</strong> already been parsed.
     *
     * @param uri an <code>URI</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isOWLDL( URI uri ) {
	boolean result = false;
	try {
	    /* Handler that's strict about OWLFullExceptions */
	    syntaxLevel = LITE;
	    OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		    public void owlFullConstruct( int code,
						  String message ) throws SAXException {
			/* Doesn't throw an error, but keeps going.... */
			setSyntaxLevel( FULL );
			explain( FULL, code, message );
			//throw new OWLFullConstructRDFException( message );
		    }
		    public void owlFullConstruct( int code,
						  String message,
						  Object obj ) throws SAXException {
			this.owlFullConstruct( code, message );
		    }
		    public void error( String message ) throws SAXException {
			throw new SAXException( message.toString() );
		    }
		    public void warning( String message ) throws SAXException {
			message( message.toString() );
		    }
		};
	    OWLOntology o = parseFromURI( handler, uri );
	    int species = species( o,
				   true ) | syntaxLevel;
	    result = ( species ==DL || species == LITE );
	    releaseOntology( o );
	} catch ( ParserException ex) {
	    int code = UNKNOWN;
	    if (ex.getCode()!=0) {
		code = ex.getCode();
	    }
	    explain( OTHER, code, ex.getMessage() );
	} catch ( OWLException ex) {
	    explain( OTHER, UNKNOWN, ex.getMessage() );
	}
	resultLogger.info( uri + " <" + (result?"":"Not ") + "DL>" );
	return result;
    }

    /**
     * Returns <code>true</code> if the ontology obtained by parsing
     * the URI is in OWL Full. Will report findings to the reporter as
     * it goes. Note that the inner workings of the validator assume
     * that the ontology has <strong>not</strong> already been parsed.
     *
     * @param uri an <code>URI</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isOWLFull( URI uri ) {
	/* An ontology is OWL Full if:

	1) There are OWL Full constructs used in the syntax,
           e.g. things have not been explicitly typed

	or

	2) The expressivity is Full. 

	*/

	boolean result = false;
	try {
	    /* Handler that doesn't care about OWLFullExceptions */
	    syntaxLevel = LITE;
	    OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		    public void owlFullConstruct( int code,
						  String message ) throws SAXException {
			/* We know that there's some syntactic Full
			 * stuff going on, but we don't necessarily
			 * want to throw an exception as there may be
			 * stuff that comes up later that pushed us
			 * out of Full, e.g. malformed RDF. */
			setSyntaxLevel( FULL );
			explain( FULL, code, message );
		    }
		    public void owlFullConstruct( int code,
						  String message,
						  Object obj ) throws SAXException {
			this.owlFullConstruct( code, message );
		    }
		    public void error( String message ) throws SAXException {
			throw new SAXException( message );
		    }
		    public void warning( String message ) throws SAXException {
			message( message.toString() );
		    }
		};
	    OWLOntology o = parseFromURI( handler, uri );
	    int species = species( o,
				   true ) | syntaxLevel;
	    result = ( species == DL || 
		       species == LITE || 
		       species == FULL);
	    releaseOntology( o );
	} catch ( ParserException ex) {
	    int code = UNKNOWN;
	    if (ex.getCode()!=0) {
		code = ex.getCode();
	    }
	    explain( OTHER, code, ex.getMessage() );
	} catch ( OWLException ex) {
	    explain( OTHER, UNKNOWN, ex.getMessage() );
	}
	resultLogger.info( uri + " <" + (result?"":"Not ") + "Full>" );
	return result;

    }


    /*************** Reading from Readers ************/

    /**
     * Returns <code>true</code> if the ontology obtained by parsing
     * the URI is in OWL Lite. Will report findings to the reporter as
     * it goes. Note that the inner workings of the validator assume
     * that the ontology has <strong>not</strong> already been parsed.
     *
     * @param uri an <code>URI</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isOWLLite( Reader r, URI uri ) {
	boolean result = false;
	try {
	    /* Handler that's strict about OWLFullExceptions */
	    syntaxLevel = LITE;
	    OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		    public void owlFullConstruct( int code,
						  String message ) throws SAXException {
			/* Doesn't throw an error, but keeps going.... */
			setSyntaxLevel( FULL );
			explain( FULL, code, message );
			//throw new OWLFullConstructRDFException( message );
		    }
		    public void owlFullConstruct( int code,
						  String message,
						  Object obj ) throws SAXException {
			this.owlFullConstruct( code, message );
		    }
		    public void error( String message ) throws SAXException {
			throw new SAXException( message.toString() );
		    }
		    public void warning( String message ) throws SAXException {
			message( message.toString() );
		    }
		};
	    OWLOntology o = parseFromURI( handler, r, uri );
	    int species = species( o,
				   true ) | syntaxLevel;
	    result = ( species == LITE );
	    releaseOntology( o );
	} catch ( ParserException ex) {
	    int code = UNKNOWN;
	    if (ex.getCode()!=0) {
		code = ex.getCode();
	    }
	    explain( OTHER, code, ex.getMessage() );
	} catch ( OWLException ex) {
	    explain( OTHER, UNKNOWN, ex.getMessage() );
	}
	resultLogger.info( uri + " <" + (result?"":"Not ") + "Lite>" );
	return result;
    }
    
    /**
     * Returns <code>true</code> if the ontology obtained by parsing
     * the URI is in OWL DL. Will report findings to the reporter as
     * it goes. Note that the inner workings of the validator assume
     * that the ontology has <strong>not</strong> already been parsed.
     *
     * @param uri an <code>URI</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isOWLDL( Reader r, URI uri ) {
	boolean result = false;
	try {
	    /* Handler that's strict about OWLFullExceptions */
	    syntaxLevel = LITE;
	    OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		    public void owlFullConstruct( int code,
						  String message ) throws SAXException {
			/* Doesn't throw an error, but keeps going.... */
			setSyntaxLevel( FULL );
			explain( FULL, code, message );
			//throw new OWLFullConstructRDFException( message );
		    }
		    public void owlFullConstruct( int code,
						  String message,
						  Object obj ) throws SAXException {
			this.owlFullConstruct( code, message );
		    }
		    public void error( String message ) throws SAXException {
			throw new SAXException( message.toString() );
		    }
		    public void warning( String message ) throws SAXException {
			message( message.toString() );
		    }
		};
	    OWLOntology o = parseFromURI( handler, r, uri );
	    int species = species( o,
				   true ) | syntaxLevel;
	    result = ( species ==DL || species == LITE );
	    releaseOntology( o );
	} catch ( ParserException ex) {
	    int code = UNKNOWN;
	    if (ex.getCode()!=0) {
		code = ex.getCode();
	    }
	    explain( OTHER, code, ex.getMessage() );
	} catch ( OWLException ex) {
	    explain( OTHER, UNKNOWN, ex.getMessage() );
	}
	resultLogger.info( uri + " <" + (result?"":"Not ") + "DL>" );
	return result;
    }

    /**
     * Returns <code>true</code> if the ontology obtained by parsing
     * the URI is in OWL Full. Will report findings to the reporter as
     * it goes. Note that the inner workings of the validator assume
     * that the ontology has <strong>not</strong> already been parsed.
     *
     * @param uri an <code>URI</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isOWLFull( Reader r, URI uri ) {
	/* An ontology is OWL Full if:

	1) There are OWL Full constructs used in the syntax,
           e.g. things have not been explicitly typed

	or

	2) The expressivity is Full. 

	*/

	boolean result = false;
	try {
	    /* Handler that doesn't care about OWLFullExceptions */
	    syntaxLevel = LITE;
	    OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		    public void owlFullConstruct( int code,
						  String message ) throws SAXException {
			/* We know that there's some syntactic Full
			 * stuff going on, but we don't necessarily
			 * want to throw an exception as there may be
			 * stuff that comes up later that pushed us
			 * out of Full, e.g. malformed RDF. */
			setSyntaxLevel( FULL );
			explain( FULL, code, message );
		    }
		    public void owlFullConstruct( int code,
						  String message,
						  Object obj ) throws SAXException {
			this.owlFullConstruct( code, message );
		    }
		    public void error( String message ) throws SAXException {
			throw new SAXException( message );
		    }
		    public void warning( String message ) throws SAXException {
			message( message.toString() );
		    }
		};
	    OWLOntology o = parseFromURI( handler, r, uri );
	    int species = species( o,
				   true ) | syntaxLevel;
	    result = ( species == DL || 
		       species == LITE || 
		       species == FULL);
	    releaseOntology( o );
	} catch ( ParserException ex) {
	    int code = UNKNOWN;
	    if (ex.getCode()!=0) {
		code = ex.getCode();
	    }
	    explain( OTHER, code, ex.getMessage() );
	} catch ( OWLException ex) {
	    explain( OTHER, UNKNOWN, ex.getMessage() );
	}
	resultLogger.info( uri + " <" + (result?"":"Not ") + "Full>" );
	return result;

    }

    /*************** End reading from Readers ********/

    /**
     * Returns <code>true</code> if the ontology is OWL-Lite. Will
     * report findings to the reporter as it goes.
     *
     *
     * @param ontology an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     * @exception OWLException if an error occurs
     */
    public boolean isOWLLite( OWLOntology ontology ) throws OWLException {
	return species( ontology, true ) == LITE;
    }

    /**
     * Returns <code>true</code> if the ontology is OWL-DL. Will
     * report findings to the reporter as it goes.
     *
     * @param ontology an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     * @exception OWLException if an error occurs
     */
    public boolean isOWLDL( OWLOntology ontology ) throws OWLException {
	int species = species( ontology, true );
	return ( species==LITE || species == DL );
    }
    
    /**
     * Returns <code>true</code> if the ontology is OWL-Full. Will
     * report findings to the reporter as it goes.
     *
     * @param ontology an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     * @exception OWLException if an error occurs
     */
    public boolean isOWLFull( OWLOntology ontology ) throws OWLException {
	int species = species( ontology, true );
	return ( species==LITE || species == DL || species == FULL );
    }    
    
    /**
     * Gather togther all the URIs that are used by this ontology. 
     *
     */
    private void gatherURIs() throws OWLException {
	/* Initialise the collections. */
	this.classURIs = new HashSet();
	this.individualURIs = new HashSet();
	this.objectPropertyURIs = new HashSet();
	this.dataPropertyURIs = new HashSet();
	this.annotationPropertyURIs = new HashSet();
	this.datatypeURIs = new HashSet();
	this.allURIs = new HashSet();
	
	/* Collect together all the URIs */
	for ( Iterator it = allOntologies.iterator();
	      it.hasNext(); ) {
	    OWLOntology onto = (OWLOntology) it.next();
	    for ( Iterator cit = onto.getClasses().iterator();
		  cit.hasNext(); ) {
		OWLNamedObject entity = (OWLNamedObject) cit.next();
		classURIs.add(entity.getURI());
		allURIs.add(entity.getURI());
	    }
	    for ( Iterator cit = onto.getIndividuals().iterator();
		  cit.hasNext(); ) {
		OWLNamedObject entity = (OWLNamedObject) cit.next();
		individualURIs.add(entity.getURI());
		allURIs.add(entity.getURI());
	    }
	    for ( Iterator cit = onto.getObjectProperties().iterator();
		  cit.hasNext(); ) {
		OWLNamedObject entity = (OWLNamedObject) cit.next();
		objectPropertyURIs.add(entity.getURI());
		allURIs.add(entity.getURI());
	    }
	    for ( Iterator cit = onto.getDataProperties().iterator();
		  cit.hasNext(); ) {
		OWLNamedObject entity = (OWLNamedObject) cit.next();
		dataPropertyURIs.add(entity.getURI());
		allURIs.add(entity.getURI());
	    }
	    for ( Iterator cit = onto.getAnnotationProperties().iterator();
		  cit.hasNext(); ) {
		OWLNamedObject entity = (OWLNamedObject) cit.next();
		annotationPropertyURIs.add(entity.getURI());
		allURIs.add(entity.getURI());
	    }
	    for ( Iterator cit = onto.getDatatypes().iterator();
		  cit.hasNext(); ) {
		OWLDataType entity = (OWLDataType) cit.next();
		datatypeURIs.add(entity.getURI());
		allURIs.add(entity.getURI());
	    }
	}
    }

    /** Check that namespace separation has been correctly obeyed. In
     * other words, no URI has been used as both an individual and
     * class name, or property and class name etc. */
    private void checkNamespaceSeparation() {

	/* Check that the collections are all disjoint */
	for ( Iterator it = classURIs.iterator();
	      it.hasNext(); ) {
	    Object u = it.next();
	    if ( individualURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Class+Individual" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Class and Individual");
	    } else if ( objectPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Class+ObjectProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Class and ObjectProperty");

	    } else if ( dataPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Class+DataProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Class and DataProperty");
	    } else if ( annotationPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Class+AnnotationProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Class and AnnotationProperty");
	    } else if ( datatypeURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Class+Datatype" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Class and Datatype");
	    }
	}

	for ( Iterator it = individualURIs.iterator();
	      it.hasNext(); ) {
	    Object u = it.next();
	    if ( objectPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Individual+ObjectProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Individual and Property");
	    } else if ( dataPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Individual+DataProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Individual and DataProperty");
	    } else if ( annotationPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Individual+AnnotationProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Individual and AnnotationProperty");
	    } else if ( datatypeURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " Individual+Datatype" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as Individual and Datatype");
	    }
	}

	for ( Iterator it = objectPropertyURIs.iterator();
	      it.hasNext(); ) {
	    Object u = it.next();
	    if ( dataPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " ObjectProperty+DataProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as ObjectProperty and DataProperty");
	    } else if ( annotationPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " ObjectProperty+AnnotationProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as ObjectProperty and AnnotationProperty");
	    } else if ( datatypeURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " ObjectProperty+Datatype" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as ObjectProperty and Datatype");
	    }
	}

	for ( Iterator it = dataPropertyURIs.iterator();
	      it.hasNext(); ) {
	    Object u = it.next();
	    if ( annotationPropertyURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " DataProperty+AnnotationProperty" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as DataProperty and AnnotationProperty");
	    } else if ( datatypeURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " DataProperty+Datatype" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as DataProperty and Datatype");
	    }
	}

	for ( Iterator it = annotationPropertyURIs.iterator();
	      it.hasNext(); ) {
	    Object u = it.next();
	    if ( datatypeURIs.contains( u ) ) {
		namespacesSeparated = false;
		logger.info( u + " AnnotationProperty+Datatype" );
		explain( FULL, SEPARATIONVIOLATION, u + "\t used as AnnotationProperty and Datatype");
	    }
	}
	//	namespacesSeparated = true;
	return;
    }

    /** Check the level of class axioms. Involves checking whether
     * things like disjoint axioms have been used. */
    private void checkClassAxioms() throws OWLException {
	    
	classAxiomLevel = LITE;
	
	/* Grab all the axioms and check their level */
	for ( Iterator it = allOntologies.iterator();
	      it.hasNext(); ) {
	    OWLOntology onto = (OWLOntology) it.next();
	    for ( Iterator axit = onto.getClassAxioms().iterator();
		  axit.hasNext(); ) {
		OWLObject oo =
		    (OWLObject) axit.next();

		logger.debug( "Got Class Axiom: " + oo );

		SpeciesValidatorVisitor visitor =
		    new SpeciesValidatorVisitor( this, objectRenderer );
		try {
		    oo.accept( visitor );
		    
		    classAxiomLevel = classAxiomLevel | visitor.getLevel();
		} catch ( OWLException ex ) {
		    classAxiomLevel = OTHER;
		}
	    }
	}
    };


    /** Check that all individuals have been given at least one
     * type. */
    private void checkIndividualTyping() throws OWLException {
	/* Grab all the individuals and check their typing */
	Set allIndividuals = new HashSet();
	for ( Iterator it = allOntologies.iterator();
	      it.hasNext(); ) {
	    OWLOntology onto = (OWLOntology) it.next();
	    for ( Iterator indit = onto.getIndividuals().iterator();
		  indit.hasNext(); ) {
		allIndividuals.add( indit.next() );
	    }
	}
	
	for ( Iterator it = allIndividuals.iterator();
	      it.hasNext(); ) {

	    OWLIndividual i =
		(OWLIndividual) it.next();
	    
	    if ( i.getTypes( allOntologies ).size() == 0 ) {
		individualsTyped = false;
		explain( FULL, UNTYPEDINDIVIDUAL, "Individual with no explicit type: " + i.getURI() );
	    }
	}
    }

    private void checkPropertyAxioms() throws OWLException {
	/* There isn't really anything to do here. Property axioms can
	 * be in all species */
	propertyAxiomLevel = LITE;
    };

    private void checkExpressivity() throws OWLException {
	/* Here, we need to look at all the expressions used anywhere
	 * within the ontology and check their level. */

	/* For each ontology, we need to check everything within it */
	expressivityLevel = LITE;
	ExpressionValidatorVisitor evv =
	    new ExpressionValidatorVisitor( this, objectRenderer );

	for ( Iterator it = allOntologies.iterator();
	      it.hasNext(); ) {
	    OWLOntology onto = (OWLOntology) it.next();
	    for ( Iterator cit = onto.getClasses().iterator();
		  cit.hasNext(); ) {
		OWLClass clazz = (OWLClass) cit.next();
		if ( !clazz.getEnumerations( onto ).isEmpty() ) {
		    /* We're in DL. */
		    expressivityLevel = expressivityLevel | DL;
		    explain( DL, ONEOF, "Enumeration used: " + clazz.getURI() );
		}
		for ( Iterator superit = 
			  clazz.getSuperClasses( onto ).iterator();
		      superit.hasNext(); ) {
		    /* Check the expressivity of any superclasses */

		    /* Tricky bit here -- if there's an intersection
		     * used at the top level, we're still ok for
		     * LITE. This is *not* currently catered for, so
		     * we will get some stuff wrong.  */

		    /* No - this *isn't* right for partial
		     * descriptions (supers). Intersections can't be
		     * used here if we're Lite -- they only occur the
		     * the Lite mapping when used in complete
		     * descriptions (equivalences). Thanks to Jos de
		     * Bruijn for pointing this one out. 

		     * SKB 19/08/04 */

		    OWLDescription description = 
			(OWLDescription) superit.next();
		    evv.reset();
		    // evv.setTopLevelDescription( true );
		    try {
			description.accept( evv );
			expressivityLevel = expressivityLevel | evv.getLevel();
		    } catch (OWLException ex) {
			explain( OTHER, UNKNOWN, ex.getMessage() );
			expressivityLevel = OTHER;
		    }
		}
		for ( Iterator superit = 
			  clazz.getEquivalentClasses( onto ).iterator();
		      superit.hasNext(); ) {
		    /* Check the expressivity of any equivalences */
		    /* This is tricky, as these expressions *can* be
		     * intersections, as long as they're intersections
		     * of Lite constructs. This is the only place that
		     * it can happen in Lite. */
		    OWLDescription description = 
			(OWLDescription) superit.next();
		    evv.reset();
		    evv.setTopLevelDescription( true );
		    try {
			description.accept( evv );
			expressivityLevel = expressivityLevel | evv.getLevel();
		    } catch (OWLException ex) {
			explain( OTHER, UNKNOWN, ex.getMessage() );
			expressivityLevel = OTHER;
		    }
		}
	    }
	    for ( Iterator iit = onto.getObjectProperties().iterator();
		  iit.hasNext(); ) {
		OWLObjectProperty op = (OWLObjectProperty) iit.next();
		logger.debug(op.getURI());

		for ( Iterator dit = 
			  op.getDomains( onto ).iterator();
		      dit.hasNext(); ) {
		    /* Check the expressivity of any equivalences */
		    OWLDescription description = 
			(OWLDescription) dit.next();
		    evv.reset();
		    try {
			description.accept( evv );
			expressivityLevel = expressivityLevel | evv.getLevel();
		    } catch (OWLException ex) {
			explain( OTHER, UNKNOWN, ex.getMessage() );
			expressivityLevel = OTHER;
		    }
		}
		for ( Iterator dit = 
			  op.getRanges( onto ).iterator();
		      dit.hasNext(); ) {
		    /* Check the expressivity of any equivalences */
		    OWLDescription description = 
			(OWLDescription) dit.next();
		    evv.reset();
		    try {
			description.accept( evv );
			expressivityLevel = expressivityLevel | evv.getLevel();
		    } catch (OWLException ex) {
			explain( OTHER, UNKNOWN, ex.getMessage() );
			expressivityLevel = OTHER;
		    }
		}
	    }

	    for ( Iterator iit = onto.getDataProperties().iterator();
		  iit.hasNext(); ) {
		OWLDataProperty dp = (OWLDataProperty) iit.next();
		logger.debug(dp.getURI());

		for ( Iterator dit = 
			  dp.getDomains( onto ).iterator();
		      dit.hasNext(); ) {
		    /* Check the expressivity of any equivalences */
		    OWLDescription description = 
			(OWLDescription) dit.next();
		    evv.reset();
		    try {
			description.accept( evv );
			expressivityLevel = expressivityLevel | evv.getLevel();
		    } catch (OWLException ex) {
			explain( OTHER, UNKNOWN, ex.getMessage() );
			expressivityLevel = OTHER;
		    }
		}
		for ( Iterator dit = 
			  dp.getRanges( onto ).iterator();
		      dit.hasNext(); ) {
		    /* Check the expressivity of any equivalences */
		    OWLDataRange description = 
			(OWLDataRange) dit.next();
		    evv.reset();
		    try {
			description.accept( evv );
			expressivityLevel = expressivityLevel | evv.getLevel();
		    } catch (OWLException ex) {
			explain( OTHER, UNKNOWN, ex.getMessage() );
			expressivityLevel = OTHER;
		    }
		}
	    }

	    for ( Iterator iit = onto.getIndividuals().iterator();
		  iit.hasNext(); ) {
		OWLIndividual ind = (OWLIndividual) iit.next();
		logger.debug(ind.getURI());

		for ( Iterator typeit = 
			  ind.getTypes( onto ).iterator();
		      typeit.hasNext(); ) {
		    /* Check the expressivity of any equivalences */
		    OWLDescription description = 
			(OWLDescription) typeit.next();
		    logger.debug(" -> " + description);
		    evv.reset();
		    try {
			description.accept( evv );
			expressivityLevel = expressivityLevel | evv.getLevel();
		    } catch (OWLException ex) {
			explain( OTHER, UNKNOWN, ex.getMessage() );
			expressivityLevel = OTHER;
		    }
		}
	    }
	}
       
	Set complexProperties = evv.getComplexProperties();
	/* Gather all the properties that are known to be functional
	 * or inverse functional */
	for ( Iterator it = allOntologies.iterator();
	      it.hasNext(); ) {
	    OWLOntology onto = (OWLOntology) it.next(); 
	    for ( Iterator pit = onto.getObjectProperties().iterator();
		  pit.hasNext(); ) {
		OWLObjectProperty prop = (OWLObjectProperty) pit.next();
		if (prop.isFunctional( onto ) || prop.isInverseFunctional( onto )) {
		    complexProperties.add( prop );
		}
	    }
	}

	/* We aren't doing everything yet as we still need to grab
	 * those that have complex superproperties. */
	
	/* Now check to see if they've been said to be transitive, in
	 * which case we're in FULL.  */
	for ( Iterator pit = complexProperties.iterator();
	      pit.hasNext(); ) {
	    OWLObjectProperty prop = (OWLObjectProperty) pit.next();
	    for ( Iterator it = allOntologies.iterator();
		  it.hasNext(); ) {
		OWLOntology onto = (OWLOntology) it.next(); 
		if ( prop.isTransitive( onto ) ) {
		    expressivityLevel = FULL;
		    explain( FULL, COMPLEXTRANSITIVE, "Complex property " + 
			     prop.getURI() + 
			     " asserted to be transitive.");
		}
	    }
	}
    }

    private void checkCorrectOWLUsage() throws OWLException {
	/* Check that nothing in the OWL vocabulary has been redefined. */
	/* This was too strong. We are actually allowed to say that
	 * some things e.g. Bag, Set, Alt and Statement are
	 * classes. Also some other stuff in the RDF vocabulary. SKB.*/
	for ( Iterator it = classURIs.iterator();
	      it.hasNext(); ) {
	    URI uri = (URI) it.next();
	    if ( uri!=null && reservedVocabulary.contains(uri.toString())) {
		if ( !classOnlyVocabulary.contains( uri.toString() ) ) {
		    /* It's a redefinition of something that we can't redefine. */
		    correctOWLUsage = false;
		    logger.info( uri.toString() + " " );
		    explain( FULL, BUILTINREDEFINITION, "Redefinition of: " + uri.toString());
		}
	    }
	}
	for ( Iterator it = individualURIs.iterator();
	      it.hasNext(); ) {
	    URI uri = (URI) it.next();
	    if ( uri!=null && reservedVocabulary.contains(uri.toString())) {
		/* It's a redefinition of something that we can't redefine. */
		correctOWLUsage = false;
		logger.info( uri.toString() + " " );
		explain( FULL, BUILTINREDEFINITION, "Redefinition of: " + uri.toString());
	    }
	}
	for ( Iterator it = objectPropertyURIs.iterator();
	      it.hasNext(); ) {
	    URI uri = (URI) it.next();
	    if ( uri!=null && reservedVocabulary.contains(uri.toString())) {
		if ( !propertyOnlyVocabulary.contains( uri.toString() ) ) {
		/* It's a redefinition of something that we can't redefine. */
		    correctOWLUsage = false;
		    logger.info( uri.toString() + " " );
		    explain( FULL, BUILTINREDEFINITION, "Redefinition of: " + uri.toString());
		}
	    }
	}
	for ( Iterator it = dataPropertyURIs.iterator();
	      it.hasNext(); ) {
	    URI uri = (URI) it.next();
	    if ( uri!=null && reservedVocabulary.contains(uri.toString())) {
		if ( !propertyOnlyVocabulary.contains( uri.toString() ) ) {
		    /* It's a redefinition of something that we can't redefine. */
		    correctOWLUsage = false;
		    logger.info( uri.toString() + " " );
		    explain( FULL, BUILTINREDEFINITION, "Redefinition of: " + uri.toString());
		}
	    }
	}
	for ( Iterator it = datatypeURIs.iterator();
	      it.hasNext(); ) {
	    URI uri = (URI) it.next();
	    if ( uri!=null && reservedVocabulary.contains(uri.toString()) ||
		 /* Nasty. Need to check that thing/nothing aren't
		  * redefined as datatypes. */
		 uri.toString().equals(OWLVocabularyAdapter.INSTANCE.getThing()) ||
		 uri.toString().equals(OWLVocabularyAdapter.INSTANCE.getNothing())) {
		/* It's a redefinition of something that we can't redefine. */
		correctOWLUsage = false;
		logger.info( uri.toString() + " " );
		explain( FULL, BUILTINREDEFINITION, "Redefinition of: " + uri.toString());
	    }
	}
    }

    private void checkCorrectOWLNamespaceUsage() throws OWLException {
	/* Check that nothing's been defined in the OWL namespace. */
	for ( Iterator it = allURIs.iterator();
	      it.hasNext(); ) {
	    URI uri = (URI) it.next();
	    if (uri!=null) {
		String str = uri.toString();
		if ( str.startsWith( OWLVocabularyAdapter.INSTANCE.OWL ) && 
		     !str.equals( OWLVocabularyAdapter.INSTANCE.getThing() ) &&
		     !str.equals( OWLVocabularyAdapter.INSTANCE.getNothing() ) &&
		     /* Added check for built ins */
		     !OWLVocabularyAdapter.INSTANCE.getAnnotationProperties().contains( str ) ) {
		    correctOWLNamespaceUsage = false;
		    logger.info( uri.toString() );
		    explain( FULL, OWLNAMESPACEUSED, uri.toString() + " in OWL Namespace" );
		}
	    }
	}
    }

    /* Returns a readable string corresponding to the error or
     * validation code */
    public static String readableCode( int code ) {
	switch( code ) {
	case OWLRDFErrorConstants.OTHER:
	    return "Unknown RDF Error";
	case OWLRDFErrorConstants.UNTYPED_CLASS:
	    return "Untyped Class";
	case OWLRDFErrorConstants.UNTYPED_PROPERTY:
	    return "Untyped Property";
	case OWLRDFErrorConstants.UNTYPED_INDIVIDUAL:
	    return "Untyped Individual";
	case OWLRDFErrorConstants.UNTYPED_ONTOLOGY:
	    return "Untyped Ontology";
	case OWLRDFErrorConstants.UNTYPED_DATATYPE:
	    return "Untyped Datatype";
	case OWLRDFErrorConstants.UNTYPED_URI:
	    return "Untyped URI";
	case OWLRDFErrorConstants.MALFORMED_LIST:
	    return "Malformed List";
	case OWLRDFErrorConstants.INVERSE_FUNCTIONAL_DATA_PROPERTY:
	    return "Inverse Functional Data Property";
	case OWLRDFErrorConstants.UNSPECIFIED_FUNCTIONAL_PROPERTY:	
	    return "Unspecified Functional Property";
	case OWLRDFErrorConstants.STRUCTURE_SHARING:
	    return "Structure Sharing";
	case OWLRDFErrorConstants.CYCLICAL_BNODES:
	    return "Cyclical BNodes";
	case OWLRDFErrorConstants.MULTIPLE_DEFINITIONS:
	    return "Multiple Definitions";
	case OWLRDFErrorConstants.MALFORMED_RESTRICTION:
	    return "Malformed Restriction";
	case OWLRDFErrorConstants.MALFORMED_DESCRIPTION:
	    return "Malformed Description";
	case OWLRDFErrorConstants.UNUSED_TRIPLES:
	    return "Unused Triples";
	case OWLRDFErrorConstants.ILLEGAL_SUBPROPERTY:
	    return "Illegal Sub Property";
	case OWLRDFErrorConstants.MALFORMED_IMPORT: 
	    return "Malformed Import";
	case OWLRDFErrorConstants.UNTYPED_PROPERTY_DATA:
	    return "Untyped Data Property";
	case OWLRDFErrorConstants.UNTYPED_PROPERTY_OBJECT:
	    return "Untyped Object Property";
	case OWLRDFErrorConstants.SAME_AS_USED_FOR_CLASS:
	    return "SameAs used for Class";
	case OWLRDFErrorConstants.SAME_AS_USED_FOR_OBJECT_PROPERTY:
	    return "SameAs used for Object Property";
	case OWLRDFErrorConstants.SAME_AS_USED_FOR_DATA_PROPERTY:
	    return "SameAs used for Data Property";
	case UNKNOWN:
	    return "Unknown";
	case INTERSECTION:
	    return "Intersection";
	case UNION:
	    return "Union";
	case COMPLEMENT:
	    return "Complement";
	case ZEROONECARDINALITY:
	    return "0/1 Cardinality";
	case CARDINALITY:
	    return "Cardinality";
	case ONEOF:
	    return "One Of";
	case DATATYPE:
	    return "DataType";
	case DATARANGE:
	    return "DataRange";
	case SUBCLASS:
	    return "SubClass";
	case EQUIVALENCE:
	    return "Equivalence";
	case DISJOINT:
	    return "Disjoint";
	case PARTIAL:
	    return "Partial";
	case COMPLETE:
	    return "Complete";
	case SUBPROPERTY:
	    return "SubProperty";
	case EQUIVALENTPROPERTY:
	    return "EquivalentProperty";
	case INVERSE:
	    return "Inverse";
	case TRANSITIVE:
	    return "Transitive";
	case SYMMETRIC:
	    return "Symmetric";
	case FUNCTIONAL:
	    return "Functional";
	case INVERSEFUNCTIONAL:
	    return "InverseFunctional";
	case INDIVIDUALS:
	    return "Individuals";
	case RELATEDINDIVIDUALS:
	    return "RelatedIndividuals";
	case INDIVIDUALDATA:
	    return "IndividualData";
	case SAMEINDIVIDUAL:
	    return "SameIndividual";
	case DIFFERENTINDIVIDUAL:
	    return "DifferentIndividuals";
	case SEPARATIONVIOLATION:
	    return "Name Separation Violated";
	case UNTYPEDINDIVIDUAL:
	    return "Untyped Individual";
	case COMPLEXTRANSITIVE:
	    return "Complex Transitive Property";
	case BUILTINREDEFINITION:
	    return "Redefinition of Builtin Vocabulary";
	case OWLNAMESPACEUSED:
	    return "Definition in OWL Namespace";
	case EXPRESSIONINAXIOM:
	    return "Expression used in Axiom";
	case EXPRESSIONINRESTRICTION:
	    return "Expression used in Restriction";
	}	
	return "---";
    }

    /** Tell the connection we're done with the ontology. */
    public void releaseOntology( OWLOntology o ) throws OWLException {
	connection.notifyOntologyDeleted( o );
    }

    public static void main(String[] args) {
	    
	try {
	    if (args.length < 1) {
		/* -v verbose. Will print out reasons */
		/* -q quiet.  No output. Primarily used for
                    performance testing. */
		System.out.println("Usage: SpeciesValidator [-l|-d|-f] [-v] [-q] url");
		System.exit(1);
	    }

	    int species = FULL;
	    boolean quiet = false;
	    boolean verbose = false;

	    for (int i=0; i< args.length-1; i++ ) {
		if ( args[i].equals("-l") ) {
		    species = LITE;
		} else if ( args[i].equals("-d") ) {
		    species = DL;
		} else if ( args[i].equals("-f") ) {
		    species = FULL;
		} else if ( args[i].equals("-q") ) {
		    quiet = true;
		} else if ( args[i].equals("-v") ) {
		    verbose = true;
		}
	    }
	    
	    BasicConfigurator.configure();
	    
	    URI uri = new URI(args[args.length-1]);

	    //	    mainLogger.info( uri.toString() );
	    //	    System.out.println( uri.toString() );
	    
	    SpeciesValidator sv = new SpeciesValidator();

	    if ( !verbose ) {
		/* Give a report that doesn't report anything */
		sv.setReporter(new SpeciesValidatorReporter() {
			public void ontology( OWLOntology onto ) {
			}
			
			public void done( String str ) {
			}
			
			public void message( String str ) {
			}
			
			public void explain( int l, int code, String str ) {
			}
		    });
	    }
	    boolean b;
	    switch (species) {
	    case LITE:
		b = sv.isOWLLite( uri );
		//		System.out.println( "Lite:\t" + (b?"YES":"NO"));
		//		mainLogger.info( "Lite:\t" + (b?"YES":"NO"));
		if ( !quiet ) {
		    System.out.println( "OWL-Lite:\t" + (b?"YES":"NO") );
		}
		break;
	    case DL:
		b = sv.isOWLDL( uri );
		//		mainLogger.info( "DL:\t" + (b?"YES":"NO"));
		//		System.out.println( "DL:\t" + (b?"YES":"NO"));
		if ( !quiet ) {
		    System.out.println( "OWL-DL  :\t" + (b?"YES":"NO") );
		}
		break;
	    case FULL:
		b = sv.isOWLFull( uri );
		//		System.out.println( "Full:\t" + (b?"YES":"NO"));
		//		mainLogger.info( "Full:\t" + (b?"YES":"NO"));
		if ( !quiet ) {
		    System.out.println( "OWL-Full:\t" + (b?"YES":"NO") );
		}
		break;
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

} // SpeciesValidator

/*
 * ChangeLog
 * $Log: SpeciesValidator.java,v $
 * Revision 1.13  2005/06/10 12:20:34  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.12  2004/11/03 17:35:43  sean_bechhofer
 * Changes to make sure that ontologies are disposed with after validation
 *
 * Revision 1.11  2004/10/19 17:33:49  sean_bechhofer
 * Fixed bug in Lite validation.
 *
 * Revision 1.10  2004/07/09 12:07:49  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.9  2004/04/16 16:28:44  sean_bechhofer
 * Addition of fixer/patcher
 *
 * Revision 1.8  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.7  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.6  2004/02/05 11:51:52  sean_bechhofer
 * Validation efficienty improvements -- ignoring annotation data content.
 *
 * Revision 1.5  2004/01/20 17:33:38  sean_bechhofer
 * Additional datatype namespace separation tests.
 *
 * Revision 1.4  2003/12/19 12:04:17  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.3  2003/11/18 17:51:05  sean_bechhofer
 * Allowing use of built-ins as annotation props.
 *
 * Revision 1.2  2003/11/18 13:51:13  sean_bechhofer
 * Bug fix -- wasn't checking for annotation properties.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.33  2003/10/03 10:00:04  bechhofers
 * Refactoring of RDFErrorHandler:
 *  o Addition of error codes for OWL Full situations
 *
 * Revision 1.32  2003/10/02 14:33:06  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.31  2003/10/02 09:37:24  bechhofers
 * Cleaning up access to Connection. Addition of Servlet Test validator.
 *
 * Revision 1.30  2003/10/01 16:51:10  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.29  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.28  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.27  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.26  2003/08/28 14:27:13  bechhofers
 * Additional logging for tests.
 *
 * Revision 1.25  2003/08/28 10:29:04  bechhofers
 * Updating parser to improve validation. Addition of new consumer with
 * simple triple model.
 *
 * Revision 1.24  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.23  2003/08/20 08:39:57  bechhofers
 * Alterations to tests.
 *
 * Revision 1.22  2003/06/19 13:33:33  seanb
 * Addition of construct checking.
 *
 * Revision 1.21  2003/05/29 09:07:28  seanb
 * Moving RDF error handler
 *
 * Revision 1.20  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.19  2003/05/19 12:04:21  seanb
 * Changes to ChangeVisitor philosophy in OntologyImpl
 *
 * Revision 1.18  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.17  2003/05/08 07:54:35  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.16  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.15  2003/04/07 17:15:11  seanb
 * no message
 *
 * Revision 1.14  2003/03/31 16:55:12  seanb
 * Various updates and fixes to parser.
 * Adding inverses, domain and range.
 *
 * Revision 1.13  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.12  2003/03/20 14:11:23  seanb
 * Minor Fix to validator.
 *
 * Revision 1.11  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.10  2003/02/19 10:15:09  seanb
 * Moving validation servlet to separate directory.
 *
 * Revision 1.9  2003/02/18 18:44:07  seanb
 * Further improvements to parsing. Addition of Validation Servlet.
 *
 * Revision 1.8  2003/02/17 18:24:07  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.7  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 * Revision 1.6  2003/02/12 16:14:56  seanb
 * Enhancement of validation.
 *
 * Revision 1.5  2003/02/11 17:18:30  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.4  2003/02/11 14:11:52  seanb
 * Added main routine.
 *
 * Revision 1.3  2003/02/06 16:41:42  seanb
 * More enhancements to validation
 *
 * Revision 1.2  2003/02/06 14:29:26  seanb
 * Enhancements to validation
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 */
