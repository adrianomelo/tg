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
 * Filename           $RCSfile: Processor.java,v $
 * Revision           $Revision: 1.8 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/10/19 17:29:48 $
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
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.BasicConfigurator;
import org.semanticweb.owl.validation.OWLValidationConstants;
import uk.ac.man.cs.img.owl.validation.SpeciesValidator;
import org.semanticweb.owl.io.owl_rdf.OWLRDFErrorHandler;
import uk.ac.man.cs.img.owl.validation.ConstructChecker;
import org.xml.sax.SAXException;
import org.semanticweb.owl.util.URIMapper;
import org.semanticweb.owl.util.PropertyBasedURIMapper;
import java.util.Properties;
import java.net.URISyntaxException;
import java.io.FileInputStream;
import java.io.IOException;

/** A basic class for an OWL ontology processor. The processor
    will parse ontologies, and spit out alternative renderings of the
    ontologies. Command synopsis is as follows:
    
    <pre>
    java uk.ac.man.cs.img.owl.util.Processor [options] input [output]
    </pre>

    where the options are:
    <pre>
    --abstract -a Produce OWL Abstract Syntax
    --tptp -t Produce TPTP
    --warn -w Report warnings & errors
    --renderer=className -r Use the given class for output.
    --impl=className -i Use the given class as the default implementation of OWLConnection.
    --validate[=lite|dl] -v Validate. If no level is given, validates as DL.
    --help -? Print help message
    </pre>

    The <CODE>input</CODE> should be a URL. If output is
    requested (<CODE></CODE> flags), then output will be written to
    <CODE>output</CODE> if present, stdout by default.

<pre>
$Id: Processor.java,v 1.8 2004/10/19 17:29:48 sean_bechhofer Exp $
</pre>

@author Sean K. Bechhofer
    */

public class Processor implements OWLValidationConstants {
    
    public static void main(String[] args) {
	
	OWLRDFParser rdfParser = null;
	Parser parser = null;
	OWLOntology onto = null;
	Renderer renderer = null;
	LongOpt[] longopts = new LongOpt[11];
	boolean warnings = false;
	boolean constructs = false;
	boolean noImports = false;
	String uriMapping = "";

	BasicConfigurator.configure();
	
	// abcdefghijklmnopqrstuvwxyz?
	// x       i      x x x xx   x 
	longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, '?');
	longopts[1] = new LongOpt("abstract", LongOpt.NO_ARGUMENT, null, 'a');
	longopts[2] = new LongOpt("tptp", LongOpt.NO_ARGUMENT, null, 't');
	longopts[3] = new LongOpt("validate", LongOpt.OPTIONAL_ARGUMENT, null, 'v');
	longopts[4] = new LongOpt("warn", LongOpt.NO_ARGUMENT, null, 'w');
	longopts[5] = new LongOpt("renderer", LongOpt.REQUIRED_ARGUMENT, null, 'r');
	longopts[6] = new LongOpt("impl", LongOpt.REQUIRED_ARGUMENT, null, 'i');
	longopts[7] = new LongOpt("parser", LongOpt.REQUIRED_ARGUMENT, null, 'p');
	longopts[8] = new LongOpt("noimports", LongOpt.REQUIRED_ARGUMENT, null, 'n');
	longopts[9] = new LongOpt("constructs", LongOpt.OPTIONAL_ARGUMENT, null, 'c');
	longopts[10] = new LongOpt("uriMapping", LongOpt.REQUIRED_ARGUMENT, null, 'u');

	Getopt g = new Getopt("", args, "?acwtv::r:i:p:nu:", longopts);
	int c;
	String arg;

	int validation = -1;
	
	while ((c = g.getopt()) != -1) {
	    switch(c) {
	    case '?':
		usage();
		System.exit(0);
	    case 'n':
		/* Don't import OWL/RDF/RDFS*/
		noImports = true;
		break;
	    case 'a':
		/* Write warnings to stdout rather than stderr */
		renderer = new org.semanticweb.owl.io.abstract_syntax.Renderer();
		break;
	    case 'c':
		/* Report on constructs */
		constructs = true;
		break;
	    case 't':
		/* Render tptp */
		renderer = new uk.ac.man.cs.img.owl.io.tptp.Renderer();
		break;
	    case 'r':
		/* Use the given class for rendernig */
		String renderingClass = g.getOptarg();
		try {
		    renderer = (Renderer) ClassLoader.getSystemClassLoader().loadClass(renderingClass).newInstance();
		} catch (Exception ex) {
		    System.err.println("Cannot create renderer " + 
				       renderingClass + "\n" + ex.getMessage() );
		    usage();
		    System.exit(0);
		}
		break;
	    case 'u':
		/* Use the given class for rendernig */
		uriMapping = g.getOptarg();
		break;
	    case 'i':
		/* Use the given class for implementation */
		String implementationClass = g.getOptarg();
		System.setProperty("org.semanticweb.owl.util.OWLConnection",
				   implementationClass);
		break;
	    case 'p':
		/* Use the given class for parsing */
		String parsingClass = g.getOptarg();
		try {
		    parser = (Parser) ClassLoader.getSystemClassLoader().loadClass(parsingClass).newInstance(); 
		} catch (Exception ex) {
		    System.err.println("Cannot create parser " + 
				       parsingClass + "\n" + ex.getMessage() );
		    usage();
		    System.exit(0);
		}
		break;
	    case 'w':
		/* Parse oil text */
		warnings = true;
		break;
	    case 'v':
		arg = g.getOptarg();
		if ( arg==null || arg.equals("dl") ) {
		    validation = DL;
		} else if ( arg.equals("lite") ) {
		    validation = LITE;
		} else if ( arg.equals("full") ) {
		    validation = FULL;
		} else {
		    usage();
		    System.exit(0);
		}
	    }
	}
	
	int i = g.getOptind();
	
	try {
	    

	    URI uri = null;
	    
	    if (args.length > i ) {
		uri = new URI( args[i] );
	    } else {
		System.out.println("No URI specified!");
		usage();
		System.exit(0);
	    }

	    URIMapper mapper = null;

	    if ( !uriMapping.equals("") ) {
		try {
		    Properties props = new Properties();
		    props.load( new FileInputStream( uriMapping ) );
		    //		    props.list( System.out );
		    mapper = new PropertyBasedURIMapper( props );
		    
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	    }


	    if (parser==null) {
		/* Use the RDF Parser */
		rdfParser = new OWLRDFParser();
		
		if ( validation==LITE || 
		     validation == DL ||
		     validation == FULL ) {
		    SpeciesValidator sv = new SpeciesValidator();
		    Map options = new HashMap();
		    options.put("uriMapper", mapper );
		    if ( noImports ) {
			options.put("ignoreSchemaImports", new Boolean( true ) );
		    }
		    sv.setOptions( options );
		    if ( validation==LITE ) {
			System.out.println( "OWL-Lite:\t" + (sv.isOWLLite( uri )?"YES":"NO") );
		    } else if ( validation==DL ) {
			System.out.println( "OWL-DL  :\t" + (sv.isOWLDL( uri )?"YES":"NO") );
		    } else if ( validation==FULL ) {
			System.out.println( "OWL-Full  :\t" + (sv.isOWLFull( uri )?"YES":"NO") );
		    }
		    System.exit(0);
		}
		
		
		if (warnings) {
		    OWLRDFErrorHandler handler = new OWLRDFErrorHandler(){
			    public void owlFullConstruct( int code,
							  String message ) throws SAXException {
			    }
			    public void owlFullConstruct( int code,
							  String message,
							  Object obj ) throws SAXException {
			    }
			    public void error( String message ) throws SAXException {
				throw new SAXException( message.toString() );
			    }
			    public void warning( String message ) throws SAXException {
				System.out.println("WARNING: " + message);
			    }
			};
		    rdfParser.setOWLRDFErrorHandler( handler );
		}
		parser = rdfParser;
	    }

	    /* Will use default implementation class as specified. */

	    parser.setConnection( OWLManager.getOWLConnection() );
	    if ( mapper != null ) {
		Map options = new HashMap();
		options.put("uriMapper", mapper );
		parser.setOptions( options );
	    }

	    onto = parser.parseOntology(uri);

	    if ( constructs ) {
		System.out.println( "Constructs Used:" );
		System.out.println( "================" );
		ConstructChecker cc = new ConstructChecker();
		for (Iterator it = ConstructChecker.used( cc.constructsUsed( onto ) ).iterator(); 
		     it.hasNext(); ) {
		    System.out.println( "\t" + it.next() );
		} 
	    }
	    if (renderer!=null) {
		Writer writer = new StringWriter();
		renderer.renderOntology( onto, writer );
		System.out.println( writer.toString() );
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static void usage() {
	System.out.println("usage: Processor [options] URI");
	System.out.println("options are:");
	System.out.println("\t--abstract -a \n\t\tProduce OWL Abstract Syntax");
	System.out.println("\t--tptp -t \n\t\tProduce TPTP");
	System.out.println("\t--warn -w \n\t\tReport warnings & errors");
	System.out.println("\t--renderer=className -r \n\t\tUse the given class for output.");
	System.out.println("\t--parser=className -p \n\t\tUse the given class for parsing (validation and warning may not work).");
	System.out.println("\t--impl=className -i \n\t\tUse the given class as the default implementation of OWLConnection.");
	System.out.println("\t--validate[=lite|dl] -v \n\t\tValidate. If no level is given, validates as DL.");
	System.out.println("\t--help -? \n\t\tPrint this message");

    }
}
