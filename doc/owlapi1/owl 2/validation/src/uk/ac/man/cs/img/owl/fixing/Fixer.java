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
 * Filename           $RCSfile: Fixer.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/04/16 16:28:44 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.fixing;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.io.Renderer;
import org.semanticweb.owl.io.Parser;

import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.io.owl_rdf.OWLRDFErrorConstants;
import java.io.*;
import java.util.HashMap;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.semanticweb.owl.validation.OWLValidationConstants;
import uk.ac.man.cs.img.owl.validation.SpeciesValidator;
import org.semanticweb.owl.validation.SpeciesValidatorReporter; 
import org.semanticweb.owl.io.owl_rdf.OWLRDFErrorHandler;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFSVocabularyAdapter;
import org.xml.sax.SAXException;
import org.semanticweb.owl.model.helper.*; 

/**
 * A tool that attempts to "fix" OWL Full ontologies. If untyped
 * resource are present, will attempt to add the triples required.
 
 <pre>
 java uk.ac.man.cs.img.owl.util.Fixer [options] input [output]
 </pre>

 where the options are:
 <pre>
 --output -o output file
 --help -? Print help message
 </pre>

 The <CODE>input</CODE> should be a URL. If output is
 requested (<CODE></CODE> flags), then output will be written to
 <CODE>output</CODE> if present, stdout by default.

 <pre>
 $Id: Fixer.java,v 1.1 2004/04/16 16:28:44 sean_bechhofer Exp $
 </pre>

 * @author Sean K. Bechhofer
 * @version 1.0
 */
public class Fixer implements OWLValidationConstants {

    public static int CLASSES = 0;
    public static int DATATYPES = 1;
    public static int INDIVIDUALS = 2;
    public static int OBJECT_PROPERTIES = 3;
    public static int DATATYPE_PROPERTIES = 4;
    public static int ANNOTATION_PROPERTIES = 5;

    private static int UNTYPED_COUNT = 6;

    private URI uri;
    private boolean result;
    private int level;

    /* Represents the results of the validation phase. */
    private boolean owlFull;
    
    private boolean schemaImportUsed;
    private boolean dcUsed;
    //    private Set fixes;

    private Set[] untyped;
    
    private OWLOntology ontology;
    private SpeciesValidator speciesValidator;
    
    /* The strategy to employ */
    private Strategy strategy;

    /* Modes of operation */

    private static OWLVocabularyAdapter owlV = OWLVocabularyAdapter.INSTANCE;
    private static RDFVocabularyAdapter rdfV = RDFVocabularyAdapter.INSTANCE;
    private static RDFSVocabularyAdapter rdfsV = RDFSVocabularyAdapter.INSTANCE;
    private static String DC = "http://purl.org/dc/elements/1.1";

    private PrintWriter reportWriter;
    private PrintWriter messageWriter;
    
    public static boolean options[][] = {
	// 	{ false, true, false, false }
	/* fixDC, fixOthers, laxSchema, laxSameAs */
	{ false, false, false, false },
	{ true, false, false, false },
	{ false, true, false, false },
	{ true, true, false, false }, 
	{ false, false, true, false },
	{ false, false, false, true},
	{ false, true, true, false },
	{ false, true, false, true} 
    };

    /**
     * Creates a new <code>Fixer</code> instance.
     *
     * @exception OWLException if an error occurs
     */
    public Fixer() throws OWLException {
	//	fixes = new HashSet();
	resetFixes();

	strategy = new Strategy();
	/* For validation */
	speciesValidator = new SpeciesValidator();
	SpeciesValidatorReporter reporter = new SpeciesValidatorReporter() {
		public void message( String str ) {
		}
		public void explain( int l, int c, String str ) {
		}
		public void ontology( OWLOntology ontology ) {
		}
		public void done( String level ) {
		}
	    };
	speciesValidator.setReporter( reporter );
	reportWriter = null;
    }


    public Set[] getUntyped() {
	return untyped;
    }

    private void resetFixes() {
	untyped = new Set[UNTYPED_COUNT];
	for (int i=0; i< UNTYPED_COUNT; i++) {
	    untyped[i] = new HashSet();
	}
    }

    /**
     * Describe <code>setStrategy</code> method here.
     *
     * @param s a <code>Strategy</code> value
     */
    public void setStrategy( Strategy s ) {
	strategy = s;
    }

    /**
     * Describe <code>getStrategy</code> method here.
     *
     * @return a <code>Strategy</code> value
     */
    public Strategy getStrategy() {
	return strategy;
    }

    public void setReportWriter( PrintWriter pw ) {
	reportWriter = pw;
    }

    public void setMessageWriter( PrintWriter pw ) {
	messageWriter = pw;
    }

    /**
     * Describe <code>status</code> method here.
     *
     * @return a <code>String</code> value
     */
    public String status() {
	StringBuffer sb = new StringBuffer();
	sb.append( "================================================\n" );
	sb.append( strategy.toString() );
	sb.append( "\t" + this.getFixes().size() + " fixes to be applied\n" );
	sb.append( "================================================" );
	return sb.toString();
    }

    /**
     * Set the URI to validate
     * @param uri an <code>URI</code> value
     */
    public void setURI( URI uri ) {
	this.uri = uri;
    }

    /**
     * Describe <code>getURI</code> method here.
     *
     * @return an <code>URI</code> value
     */
    public URI getURI() {
	return uri;
    }


    /**
     * Describe <code>getDCUsed</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getDCUsed() {
	return dcUsed;
    }
    
    /**
     * Describe <code>parse</code> method here.
     *
     * @exception OWLException if an error occurs
     */
    public void parse() throws OWLException {
	owlFull = false;
	//	fixes = new HashSet();
	resetFixes();
	
	OWLRDFParser parser = new OWLRDFParser();
	parser.setConnection( OWLManager.getOWLConnection() );

	Map options = new HashMap();

	if ( strategy.laxSchema ) {
	    options.put("ignoreSchemaImports", new Boolean( true ) );
	}
	if ( strategy.laxSameAs ) {
	    options.put("fixSameAs", new Boolean( true ) );
	}

	parser.setOptions( options );

	/* Error handler */
	OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
		public void owlFullConstruct( int code,
					      String message ) throws SAXException {
		    /* Doesn't throw an error, but keeps going.... */
		    //		    message( code + "\n" + message );

		    /* Can't do anything.... */
		    owlFull = true;
		    //throw new OWLFullConstructRDFException( message );
		}
		public void owlFullConstruct( int code,
					      String message,
					      Object obj ) throws SAXException {
		    /* Doesn't throw an error, but keeps going.... */
		    //		    message( code + "\n" + message + "\n" + obj );
		    owlFull = true;
		    if ( !applyFix( code, message, obj ) ) {
			/* We couldn't fix it */
		    }
		    //throw new OWLFullConstructRDFException( message );
		}
		public void error( String message ) throws SAXException {
		    throw new SAXException( message.toString() );
		}
		public void warning( String message ) throws SAXException {
		    /* Fixer.this.warning( message ); */
		}
	    };
	/* Set error handler for the parser */
	parser.setOWLRDFErrorHandler( handler );
	
	/* Parse the ontology */
	ontology = parser.parseOntology( uri );
    }

    /**
     * Describe <code>warning</code> method here.
     *
     * @param str a <code>String</code> value
     */
    public void warning ( String str ) {
	message( "WARNING: " + str );
    }

    /**
     * Describe <code>owlFull</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public boolean owlFull() {
	return owlFull;
    }

    /* The fixer thinks it might have a fix */
    /**
     * Describe <code>fixable</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public boolean fixable() {
	return !getFixes().isEmpty();
    }
    
    /**
     * Describe <code>getFixes</code> method here.
     *
     * @return a <code>Set</code> value
     */
    public Set getFixes() {
	Set fs = new HashSet();
	String triple = null;
	for (Iterator it = untyped[CLASSES].iterator(); it.hasNext(); ) {
	    Object obj = it.next();
	    triple = typeTriple( owlV.getClass_(), obj );
	    if ( triple!=null ) { 
		fs.add( triple );
	    }
	}
	for (Iterator it = untyped[INDIVIDUALS].iterator(); it.hasNext(); ) {
	    Object obj = it.next();
	    triple = typeTriple( owlV.getThing(), obj );
	    if ( triple!=null ) { 
		fs.add( triple );
	    }
	}
	for (Iterator it = untyped[DATATYPES].iterator(); it.hasNext(); ) {
	    Object obj = it.next();
	    triple = typeTriple( rdfsV.getDatatype(), obj );
	    if ( triple!=null ) { 
		fs.add( triple );
	    }
	}
	for (Iterator it = untyped[OBJECT_PROPERTIES].iterator(); it.hasNext(); ) {
	    Object obj = it.next();
	    triple = typeTriple( owlV.getObjectProperty(), obj );
	    if ( triple!=null ) { 
		fs.add( triple );
	    }
	}
	for (Iterator it = untyped[DATATYPE_PROPERTIES].iterator(); it.hasNext(); ) {
	    Object obj = it.next();
	    triple = typeTriple( owlV.getDatatypeProperty(), obj );
	    if ( triple!=null ) { 
		fs.add( triple );
	    }
	}
	for (Iterator it = untyped[ANNOTATION_PROPERTIES].iterator(); it.hasNext(); ) {
	    Object obj = it.next();
	    triple = typeTriple( owlV.getAnnotationProperty(), obj );
	    if ( triple!=null ) { 
		fs.add( triple );
	    }
	}
	return fs;
    }

    public String summary() {
	StringBuffer sb = new StringBuffer();
	sb.append( "\t=== Type Triples Required ===\n" );
	if ( !untyped[CLASSES].isEmpty() ) {
	    sb.append( "\tClasses:\n" );
	    for (Iterator it = untyped[CLASSES].iterator(); it.hasNext(); ) {
		Object obj = it.next();
		sb.append( "\t" + obj + "\n" );
	    }
	}
	if ( !untyped[INDIVIDUALS].isEmpty() ) {
	    sb.append( "\tIndividuals:\n" );
	    for (Iterator it = untyped[INDIVIDUALS].iterator(); it.hasNext(); ) {
		Object obj = it.next();
		sb.append( "\t" + obj + "\n" );
	    }
	}
	if ( !untyped[DATATYPES].isEmpty() ) {
	    sb.append( "\tDatatypes:\n" );
	    for (Iterator it = untyped[DATATYPES].iterator(); it.hasNext(); ) {
		Object obj = it.next();
		sb.append( "\t" + obj + "\n" );
	    }
	}
	if ( !untyped[OBJECT_PROPERTIES].isEmpty() ) {
	    sb.append( "\tObject Properties:\n" );
	    for (Iterator it = untyped[OBJECT_PROPERTIES].iterator(); it.hasNext(); ) {
		Object obj = it.next();
		sb.append( "\t" + obj + "\n" );
	    }
	}
	if ( !untyped[DATATYPE_PROPERTIES].isEmpty() ) {
	    sb.append( "\tDatatype Properties:\n" );
	    for (Iterator it = untyped[DATATYPE_PROPERTIES].iterator(); it.hasNext(); ) {
		Object obj = it.next();
		sb.append( "\t" + obj + "\n" );
	    }
	}
	if ( !untyped[ANNOTATION_PROPERTIES].isEmpty() ) {
	    sb.append( "\tAnnotation Properties:\n" );
	    for (Iterator it = untyped[ANNOTATION_PROPERTIES].iterator(); it.hasNext(); ) {
		Object obj = it.next();
		sb.append( "\t" + obj + "\n" );
	    }
	}
	return sb.toString();
    }
    
    /**
     * Describe <code>checkSchemaImports</code> method here.
     *
     * @exception OWLException if an error occurs
     */
    public void checkSchemaImports() throws OWLException {
	/* Often the case that ontologies import the RDF or OWL
	 * namespaces. No need! */
	Set allOntologies = OntologyHelper.importClosure( ontology );
	schemaImportUsed = false;
	for (Iterator it = allOntologies.iterator();
	     it.hasNext(); ) {
	    OWLOntology imported = (OWLOntology) it.next();
	    String url = imported.getURI().toString();
	    /* AAARgh */
	    String url2 = url + "#";
	    //	    System.out.println( "IMPORT: " + url );
	    if ( ( (url.equals( OWLVocabularyAdapter.OWL )) ||
		   (url.equals( RDFVocabularyAdapter.RDF )) ||
		   (url.equals( RDFSVocabularyAdapter.RDFS )) ||
		   (url2.equals( OWLVocabularyAdapter.OWL )) ||
		   (url2.equals( RDFVocabularyAdapter.RDF )) ||
		   (url2.equals( RDFSVocabularyAdapter.RDFS )) ) ) {
		schemaImportUsed = true;
		message( "IMPORT: " + url );
	    }
	}
    }

    /** Returns true of its rdf:_1, rdf:_2 etc.... */
    private boolean containerMembershipProperty( Object obj ) {
	String str = obj.toString();
	//	System.out.println( str );
	String frag = str.substring( str.indexOf('#') + 1 );
	//	System.out.println( frag );
	return frag.matches("_\\d+");
    }
    
    /** Returns true of its rdf:_1, rdf:_2 etc.... */
    private boolean seqBagOrAlt( Object obj ) {
	String str = obj.toString();
	//	System.out.println( str );
	String frag = str.substring( str.indexOf('#') + 1 );
	//	System.out.println( frag );
	return frag.equals("Bag") || frag.equals("Alt") || frag.equals("Seq");
    }
    
    
    /** Attempts to apply a fix for the given problem. If successful,
     * returns true and adds the fix to the collection of fixes. */
    private  boolean applyFix( int code,
			       String message,
			       Object obj ) {

	if ( obj!=null && 
	     ( obj.toString().startsWith( OWLVocabularyAdapter.OWL ) ||
	       ( obj.toString().startsWith( RDFVocabularyAdapter.RDF ) &&
		 !containerMembershipProperty( obj ) &&
		 !seqBagOrAlt( obj ) ) ||
	       obj.toString().startsWith( RDFSVocabularyAdapter.RDFS ) ) ) {
	    /* We know how to deal with this -- don't import the schema.... */
	    return false;
	} 

	/* Simply put, we can solve the missing type things by banging
	 * in an extra triple. */
	String triple = null;
	
	switch ( code ) {
	case OWLRDFErrorConstants.UNTYPED_CLASS:
	    if ( strategy.fixOthers ) {
		if (obj!=null) {
		    untyped[CLASSES].add( obj );
		    return true;
		}
// 		triple = typeTriple( owlV.getClass_(), obj );
// 		if ( triple!=null ) { 
// 		    fixes.add( triple );
// 		    return true;
// 		}
	    }
	    return false;
	case OWLRDFErrorConstants.UNTYPED_DATATYPE:
	    if ( strategy.fixOthers ) {
		if (obj!=null) {
		    untyped[DATATYPES].add( obj );
		    return true;
		}
// 		triple = typeTriple( rdfsV.getDatatype(), obj );
// 		if ( triple!=null ) { 
// 		    fixes.add( triple );
// 		    return true;
// 		}
	    }
	    return false;
	case OWLRDFErrorConstants.UNTYPED_ONTOLOGY:
// 	    if ( typeOthers ) {
// 		triple = typeTriple( owlV.getOntology(), obj );
// 		if ( triple!=null ) { 
// 		    fixes.add( triple );
// 		    return true;
// 		}
// 	    }
	    return false;
	case OWLRDFErrorConstants.UNTYPED_PROPERTY_OBJECT:
	    //	    System.out.println( "xx:" + obj );
	    if ( strategy.fixOthers ) {
		if (obj!=null) {
		    untyped[OBJECT_PROPERTIES].add( obj );
		    return true;
		}
// 		triple = typeTriple( owlV.getObjectProperty(), obj );
// 		if ( triple!=null ) { 
// 		    fixes.add( triple );
// 		    return true;
// 		}
	    }
	    return false;
	case OWLRDFErrorConstants.UNTYPED_PROPERTY_DATA:
	    /* If we're trying to fix DC, then make them annotation
	     * properties. */
	    if ( obj.toString().startsWith( DC ) && strategy.fixDC ) {
		//		System.out.println( "DC: " + uri );
		dcUsed = true;
		/* Guesses that DC properties are annotation
		 * properties */
		if (obj!=null) {
		    untyped[ANNOTATION_PROPERTIES].add( obj );
		    return true;
		}
// 		triple = typeTriple( owlV.getAnnotationProperty(), obj );
// 		if ( triple!=null ) { 
// 		    fixes.add( triple );
// 		    return true;
// 		}
	    }
	    if ( strategy.fixOthers ) {
		if (obj!=null) {
		    untyped[DATATYPE_PROPERTIES].add( obj );
		    return true;
		}
// 		triple = typeTriple( owlV.getDatatypeProperty(), obj );
// 		if ( triple!=null ) { 
// 		    fixes.add( triple );
// 		    return true;
// 		}
	    }
	    return false;
	case OWLRDFErrorConstants.UNTYPED_INDIVIDUAL:
	    /* If it's Thing or Nothing, then try and make it a
	     * Class. If this doesn't work then we're definitely
	     * knackered. */
	    if ( (obj!=null) && 
		 (obj.toString().equals( owlV.getThing() ) ||
		  obj.toString().equals( owlV.getNothing() ) ) ) {
		if (obj!=null) {
		    untyped[CLASSES].add( obj );
		    return true;
		}
// 		triple = typeTriple( owlV.getClass_(), obj );
// 		if ( triple!=null ) { 
// 		    fixes.add( triple );
// 		    return true;
// 		}
	    }
	    if ( strategy.fixOthers ) {
		if (obj!=null) {
		    untyped[INDIVIDUALS].add( obj );
		    return true;
		}
// 		triple = typeTriple( owlV.getThing(), obj );
// 		if ( triple!=null ) { 
// 		    fixes.add( triple );
// 		    return true;
// 		}
	    }
	    return false;
	}
	return false;
    }
    
    /* Produce a type triple of the appropriate format */
    private String typeTriple( String type, 
			       Object obj ) {
	StringBuffer sb = new StringBuffer();
	URI objURI = null;
	if (obj!=null) {
	    try {
		objURI = (URI) obj;
	    } catch (ClassCastException ex) {
		/* Couldn't fix it. */
		return null;
	    }
	    if (objURI == null) {
		/* Couldn't fix it. Not enough information. */
		return null;
	    }
	    /* All very dodgy.... */
	    String str = objURI.toString();
	    str = str.replaceAll( "&", "&amp;" );

	    sb.append( " <rdf:Description rdf:about='"+ str +"'>\n" );
	    sb.append( "  <rdf:type rdf:resource='" + type + "'/>\n" );
	    sb.append( " </rdf:Description>\n");
	    return sb.toString();
	}
	return null;
    }
    

    /**
     * Apply the current strategy. 
     *
     * @param uri an <code>URI</code> value
     * @param output a <code>String</code> value
     * @return an <code>int</code> value
     * @exception OWLException if an error occurs
     * @exception IOException if an error occurs
     */
    public int applyStrategy( URI uri,
			      String output ) throws OWLException, IOException {
	Map options = new HashMap();
	//	fixes = new HashSet();
	resetFixes();
	
	options.put("ignoreSchemaImports", new Boolean( strategy.laxSchema ) );
	options.put("fixSameAs", new Boolean( strategy.laxSameAs ) );
	speciesValidator.setOptions( options );
	
	message( "Validating" );
	speciesValidator.setConnection( OWLManager.getOWLConnection() );
	if ( speciesValidator.isOWLDL( uri ) ) {
	    message( "Validation Succeeded" );
	    message( strategy.toString() );
	    report( uri, strategy, null, false );
	    return 0;
	}
	
	message( "Fixing" );		   
	setURI( uri );
	parse();
	
	if ( fixable() ) {
	    message( "Fixer thinks it can fix " + uri );
	    File outputFile = null;
	    
	    if (output==null) {
		/* No output specified so we'll use a temporary file */
		outputFile = File.createTempFile( "fixer", ".owl" );
		//			    message( "Writing (Temporary) File " + outputFile);
		outputFile.deleteOnExit();
	    } else {
		outputFile = new File( output );
		//message( "Writing File " + outputFile);
	    }
	    
	    FileWriter fw = new FileWriter( outputFile );
	    PrintWriter pw = new PrintWriter( fw );;
	    
	    pw.print("<?xml version='1.0' encoding='ISO-8859-1'?>\n");
	    pw.print("<rdf:RDF xmlns:owl='http://www.w3.org/2002/07/owl#'\n");
	    pw.print(" xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n");
	    pw.print(" xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#'\n");
	    pw.print(" xmlns:xsd='http://www.w3.org/2000/10/XMLSchema#'>\n");
	    pw.print(" <owl:Ontology rdf:about=''>\n");
	    pw.print("  <owl:imports>\n");
	    pw.print("    <owl:Ontology rdf:about='" + getURI() + "'/>\n");
	    pw.print("  </owl:imports>\n");
	    pw.print(" </owl:Ontology>\n");
	    pw.print("\n");
	    
	    for (Iterator it = getFixes().iterator();
		 it.hasNext(); ) {
		pw.print( it.next() );
	    }
	    pw.print("\n");
	    pw.print("</rdf:RDF>\n");
	    pw.print("\n");
	    fw.close();
	    
	    message ( "Validating " + outputFile.toURI() );
	    speciesValidator.setConnection( OWLManager.getOWLConnection() );
	    if ( speciesValidator.isOWLDL( outputFile.toURI() ) ) {
		message( "***** FIX SUCCEEDED *****" );
		message( "Fixer Options:" );
		message( status() );
		message( "Validation Options:" );
		message( "\tSchema Ignore\t" + (strategy.laxSchema?"YES":"NO") );
		message( "\tSameAs fix\t" + (strategy.laxSameAs?"YES":"NO") );
		message( "***** FIX SUCCEEDED *****" );
		report( uri, strategy, output, true );
		return 0;
	    } else {
		message( "Fix Failed" );
	    }
	} else {
	    message ( "Fixer does not think it can fix" );
	}
	return -1;
    }
			       
    /**
     * Describe <code>message</code> method here.
     *
     * @param message a <code>String</code> value
     */
    public void message( String message ) {
	if ( messageWriter!=null ) {
	    messageWriter.println( message );
	}
    }

    /**
     * Describe <code>report</code> method here.
     *
     * @param message a <code>String</code> value
     */
    public void report( String message ) {
	if ( reportWriter!=null ) {
	    reportWriter.println( message );
	}
    }

    /**
     * Describe <code>report</code> method here.
     *
     * @param uri an <code>URI</code> value
     * @param strategy a <code>Strategy</code> value
     * @param output a <code>String</code> value
     * @param added a <code>boolean</code> value
     */
    public void report( URI uri, Strategy strategy, 
			String output, boolean added ) {
	if ( reportWriter!=null ) {
	    reportWriter.println( "Validation of " + uri + " was successful" );
	    reportWriter.println( strategy.toString() );
	    if (added) {
		//		reportWriter.println( "\tExtra triples were necessary.");
		reportWriter.println( summary() );
		if (output!=null) {
		    reportWriter.println( "\tThese were written to\n\t\t" + output );
		}
	    }
	}
    }
    
    /**
     * Describe <code>usage</code> method here.
     *
     */
    public static void usage() {
	System.out.println("usage: Fixer [options] URI");
	
	System.out.println("\t--help -? \n\t\tPrint this message");
	System.out.println("\t--lax -l \n\t\tIf validation fails, try and parse with lax options.");
	System.out.println("\t--fix -f \n\t\tIf validation fails, try and fix and then check the fixed ontology.");
	System.out.println("\t--output -o \n\t\tWrite fixed ontology to file");
    }

    public Strategy newStrategy( boolean[] bs ) {
	Strategy st = new Strategy();
	st.fixDC = bs[0];
	st.fixOthers = bs[1];
	st.laxSchema = bs[2];
	st.laxSameAs = bs[3];
	return st;
    }


    /**
     * Encapsulates the various options that the fixer can employ.
     *
     */
    public class Strategy {
	public boolean fixDC;
	public boolean fixOthers;
	public boolean laxSchema;
	public boolean laxSameAs;

	private Strategy() {
	    fixDC = false;
	    fixOthers = false;
	    laxSchema = false;
	    laxSameAs = false;
	}
	
	private Strategy( boolean[] bs ) {
	    fixDC = bs[0];
	    fixOthers = bs[1];
	    laxSchema = bs[2];
	    laxSameAs = bs[3];
	}

	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append( "\tUntyped Fix: \t" + (fixOthers?"YES":"NO") + "\n");
	    sb.append( "\tDC Fix: \t" + (fixDC?"YES":"NO") + "\n");
	    sb.append( "\tSchema Fix: \t" + (laxSchema?"YES":"NO") + "\n");
	    sb.append( "\tSameAsFix: \t" + (laxSameAs?"YES":"NO") + "\n");
	    return sb.toString();
	}
    }

}
