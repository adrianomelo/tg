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
 * Revision           $Revision: 1.4 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/12/15 12:58:27 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.util;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.io.Renderer;
import org.semanticweb.owl.io.Parser;

import gnu.getopt.LongOpt;
import gnu.getopt.Getopt;
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
 $Id: Fixer.java,v 1.4 2004/12/15 12:58:27 sean_bechhofer Exp $
 </pre>

 * @author Sean K. Bechhofer
 * @version 1.0
 */
public class Fixer implements OWLValidationConstants {

    /* Modes of operation */

    private static OWLVocabularyAdapter owlV = OWLVocabularyAdapter.INSTANCE;
    private static RDFVocabularyAdapter rdfV = RDFVocabularyAdapter.INSTANCE;
    private static RDFSVocabularyAdapter rdfsV = RDFSVocabularyAdapter.INSTANCE;
    private static String DC = "http://purl.org/dc/elements/1.1";

    /**
     * Describe <code>main</code> method here.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args) {
	BasicConfigurator.configure();
	
	// abcdefghijklmnopqrstuvwxyz?
	//                            

	LongOpt[] longopts = new LongOpt[10];

	longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, '?');
	longopts[1] = new LongOpt("output", LongOpt.REQUIRED_ARGUMENT, null, 'o');
	longopts[2] = new LongOpt("quiet", LongOpt.NO_ARGUMENT, null, 'q');
	longopts[3] = new LongOpt("report", LongOpt.NO_ARGUMENT, null, 'r');
	longopts[4] = new LongOpt("strategy", LongOpt.NO_ARGUMENT, null, 's');
	longopts[5] = new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v');

	
	Getopt g = new Getopt("", args, "?o:qrs:v", longopts);
	int c;
	String arg;
	String output = null;
	boolean report = false;
	boolean quiet = false;
	boolean verbose = false;
	int strat = -1;
	
	while ((c = g.getopt()) != -1) {
	    switch(c) {
	    case '?':
		usage();
		System.exit(0);
	    case 's':
		strat = Integer.parseInt( g.getOptarg() );
		break;
	    case 'q':
		quiet = true;
		break;
	    case 'v':
		verbose = true;
		break;
	    case 'r':
		report = true;
		break;
	    case 'o':
		output = g.getOptarg();
		break;
	    }
	}
	
	int i = g.getOptind();
	URI uri = null;
	
	try { 
		
	    if (args.length > i ) {
		uri = new URI( args[i] );
	    } else {
		System.err.println("No URI specified!");
		usage();
		System.exit( 1 );
	    }
		
// 	    /* For validation */
// 	    SpeciesValidator validator = new SpeciesValidator();
// 	    SpeciesValidatorReporter reporter = new SpeciesValidatorReporter() {
// 		    public void message( String str ) {
// 		    }
// 		    public void explain( int l, int c, String str ) {
// 		    }
// 		    public void ontology( OWLOntology ontology ) {
// 		    }
// 		    public void done( String level ) {
// 		    }
// 		};
// 	    validator.setReporter( reporter );

	    /* Used to check then fix */
	    uk.ac.man.cs.img.owl.fixing.Fixer fixer = 
		new uk.ac.man.cs.img.owl.fixing.Fixer();
	    if ( report ) {
		fixer.setReportWriter( new PrintWriter( System.out, true ) );
	    }
	    if ( !quiet ) {
		fixer.setMessageWriter( new PrintWriter( System.out, true ) );
	    }

	    if ( strat > -1 ) {
		/* A particular strategy was requested */
		uk.ac.man.cs.img.owl.fixing.Fixer.Strategy strategy = fixer.newStrategy( uk.ac.man.cs.img.owl.fixing.Fixer.options[strat] );
		fixer.setStrategy( strategy );
		int result = fixer.applyStrategy( uri, 
						  output );
		if (result==0 || result== 1) {
		    System.exit( result );
		}
	    } else {
		int opti = 0;
		
		/* Loop through the strategies until success or we run
		 * out of strategies */
		while ( opti<uk.ac.man.cs.img.owl.fixing.Fixer.options.length ) {
		    uk.ac.man.cs.img.owl.fixing.Fixer.Strategy strategy = fixer.newStrategy( uk.ac.man.cs.img.owl.fixing.Fixer.options[opti] );
		    fixer.setStrategy( strategy );
		    int result = fixer.applyStrategy( uri, 
						      output );
		    if (result==0) {
			if (verbose) {
			    System.out.print( uri );
			    System.out.print( "," );
			    System.out.print( "1" );
			    System.out.print( "," );
			    System.out.print( fixer.getFixes().size() );
			    System.out.print( "," );
			    System.out.print( strategy.fixOthers?"1":"0");
			    System.out.print( "," );
			    System.out.print( strategy.fixDC?"1":"0");
			    System.out.print( "," );
			    System.out.print( strategy.laxSchema?"1":"0");
			    System.out.print( "," );
			    System.out.print( strategy.laxSameAs?"1":"0");
			    System.out.println();
			}
			System.exit( result );
		    }
		    opti++;
		}
	    }
	    /* We were unable to fix. */
	    if ( verbose ) {
		System.out.print( uri );
		System.out.print( "," );
		System.out.print( "0" );
		System.out.println();
	    }
	    fixer.message( "***** FIX FAILED *****" );
	    System.exit( 1 );
	} catch (Exception ex) {
	    System.err.println( ex.getMessage() );
	    //	    ex.printStackTrace();
	    System.out.print( uri );
	    System.out.print( ",0,0,0,0,0,0,1" );
	    System.out.println();
	    System.exit( 1 );
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
    
}
