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
 * Filename           $RCSfile: Validator.java,v $
 * Revision           $Revision: 1.3 $
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
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.semanticweb.owl.validation.OWLValidationConstants;
import uk.ac.man.cs.img.owl.validation.SpeciesValidator;
import org.semanticweb.owl.validation.SpeciesValidatorReporter; 
import org.semanticweb.owl.io.owl_rdf.OWLRDFErrorHandler;
import org.xml.sax.SAXException;

/** Harness to run validation. Takes a URI and attempts to validate as
 * OWL DL. If required, detailed error reports can be generated
 * describing exactly *why* the ontology is not in DL.
    
    <pre>
    java uk.ac.man.cs.img.owl.util.Validator [options] input [output]
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
$Id: Validator.java,v 1.3 2004/12/15 12:58:27 sean_bechhofer Exp $
</pre>

@author Sean K. Bechhofer
    */

public class Validator implements OWLValidationConstants {

    private URI uri;
    private boolean result;
    private int level;
    private Set codes;
    
    public Validator() {
    }

    /** Set the URI to validate */
    public void setURI( URI uri ) {
	this.uri = uri;
    }

    /** Set the required level of validation */
    public void setLevel( int level ) {
	this.level = level;
    }

    /** Get the result of validation */
    public boolean getResult() {
	return result;
    }

    /** Get the condition codes encountered during the processing */
    public Set getCodes() {
	return codes;
    }

    /** Get a summary of the results */
    public String getSummary() {
	StringBuffer sb = new StringBuffer();
	sb.append( uri );
	for (int i=0; i < Errors.allCodes.length; i++) {
	    sb.append( "," );
	    boolean found = false;
	    for (Iterator it = getCodes().iterator();
		 it.hasNext();  ) {
		int code = ((Integer) it.next()).intValue();
		if (code == Errors.allCodes[i]) {
		    found = true;
		}
	    }
	    sb.append( found?"1":"0" );
	}
	return sb.toString();
    }

    /** Get the headers for condition codes */
    public String getHeaders() {
	StringBuffer sb = new StringBuffer();
	sb.append( "URI" );
	for (int i=0; i < Errors.allCodes.length; i++) {
	    sb.append( "," );
	    sb.append( Errors.pretty( Errors.allCodes[i] ) );
	}
	return sb.toString();
    }

    /** Do the validation */
    public void validate() throws OWLException {
	result = false;
	codes = new TreeSet();
	SpeciesValidator sv = new SpeciesValidator();
	SpeciesValidatorReporter rep = new SpeciesValidatorReporter() {
		public void message( String str ) {
		}
		public void explain( int l, int c, String str ) {
		    //System.out.println( l + " " + c + " " + str );
		    if (c==UNKNOWN) {
			System.out.println( str );
		    }
		    if (c==OWLRDFErrorConstants.OTHER) {
			System.out.println( str );
		    }
		    if (l > level) {
			/* If it's a bad level, record the code */
			codes.add( new Integer(c) );
		    }
		}
		public void ontology( OWLOntology ontology ) {
		}
		public void done( String level ) {
		}
	    };
	sv.setReporter( rep );	
	if ( level == LITE ) {
	    result = sv.isOWLLite( uri );
	} else if ( level == DL ) {
	    result = sv.isOWLDL( uri );
	} else if ( level == FULL ) {
	    result = sv.isOWLFull( uri );
	}
    }

    
    public static void main(String[] args) {
	

	Parser parser = null;
	OWLOntology onto = null;
	Renderer renderer = null;
	boolean verbose = false;
	int level = FULL;

	BasicConfigurator.configure();
	
	// abcdefghijklmnopqrstuvwxyz?
	//            x  x     xx    x

	LongOpt[] longopts = new LongOpt[6];

	longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, '?');
	longopts[1] = new LongOpt("output", LongOpt.REQUIRED_ARGUMENT, null, 'o');
	longopts[2] = new LongOpt("level", LongOpt.REQUIRED_ARGUMENT, null, 'l');
	longopts[3] = new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v');
	longopts[4] = new LongOpt("urls", LongOpt.REQUIRED_ARGUMENT, null, 'u');
	longopts[5] = new LongOpt("check", LongOpt.NO_ARGUMENT, null, 'c');
	Getopt g = new Getopt("", args, "?o:l:u:vc", longopts);
	int c;
	String arg;
	String output = null;
	String urls = null;
	boolean justCheckURLs = false;

	int validation = -1;
	
	while ((c = g.getopt()) != -1) {
	    switch(c) {
	    case '?':
		usage();
		System.exit( 1 );
	    case 'c':
		justCheckURLs = true;
	    case 'o':
		/* Use the given file for output */
		output = g.getOptarg();
		break;
	    case 'u':
		/* Collection of URLS for checking */
		urls = g.getOptarg();
		break;
	    case 'l':
		arg = g.getOptarg();
		if ( arg==null || arg.equals("dl") ) {
		    level = DL;
		} else if ( arg.equals("lite") ) {
		    level = LITE;
		} else if ( arg.equals("full") ) {
		    level = FULL;
		} else {
		    usage();
		    System.exit(0);
		}
		break;
	    case 'v':
		verbose = true;
	    }
	}
	
	int i = g.getOptind();
	
	try {
	    if ( urls == null ) {
		URI uri = null;
		
		if (args.length > i ) {
		    uri = new URI( args[i] );
		} else {
		    System.err.println("No URI specified!");
		    usage();
		    System.exit(1);
		}
		
		Validator v = new Validator();
		v.setURI( uri );
		v.setLevel( level );
		v.validate();
		PrintWriter out = null;
		if ( output!=null ) {
		    out = new PrintWriter( new FileWriter( output ) ) ;
		} else {
		    out = new PrintWriter( System.out );
		}
		if (verbose) {
		    out.println( uri );
		    for (Iterator it = v.getCodes().iterator();
			 it.hasNext();  ) {
			int code = ((Integer) it.next()).intValue();
			out.println( Errors.pretty( code ) );
		    }
		} else {
		    out.print( v.getSummary() );
		}
		out.println();
		out.flush();
		
		if (v.getResult()) {
		    System.exit( 0 );
		} else {
		    System.exit( 1 );
		} 
	    } else {
		PrintWriter out = null;
		if ( output!=null ) {
		    out = new PrintWriter( new FileWriter( output, true ) ) ;
		} else {
		    out = new PrintWriter( System.out );
		}
		Validator v = new Validator();
		v.setLevel( level );
		out.println( v.getHeaders() );
		BufferedReader br = 
		    new BufferedReader( new FileReader ( urls ) );
		String f = null;
		while ( ( f=br.readLine() ) != null ) {
			if ( !f.startsWith("#") ) {
			    try {
				URI uri = new URI( f );
				System.err.println( uri );
				if (!justCheckURLs) {
				    v.setURI( uri );
				    v.validate();
				    //	if (!v.getResult()) {
				    out.println( v.getSummary() );
				    // }
				}
			    } catch (Exception ex) {
				out.println( f + "," + ex.getMessage() );
			    }
			}
		    } 
		out.flush();
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
    
    public static void usage() {
	System.out.println("usage: Validator [options] URI");
	System.out.println("options are:");
	System.out.println("\t--output=file -p \n\t\tOutput File");
	System.out.println("\t--help -? \n\t\tPrint this message");
	System.out.println("\t--level=(dl|lite|full) -l \n\t\tValidation Level");
    }

    public static String level( int l ) {
	switch (l) {
	case SpeciesValidator.LITE:
	    return "Lite";
	case SpeciesValidator.DL:
	    return "DL  ";
	case SpeciesValidator.FULL:
	    return "Full";
	}
	return "----";
    }

}
