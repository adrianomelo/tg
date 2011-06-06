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
 * Filename           $RCSfile: OWLTestExtracter2.java,v $
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
import org.apache.log4j.Logger;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.Reader;

import edu.unika.aifb.rdf.api.syntax.RDFConsumer;
import edu.unika.aifb.rdf.api.util.RDFConstants;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Comparator;

import gnu.getopt.Getopt;


/**
 * Utility that takes a collection of zipped files, looks for any
 * manifests in them, and then tries to extract information about the
 * tests described.
 *
 * @author <a href="mailto:seanb@cs.man.ac.uk">Sean Bechhofer</a>
 */

public class OWLTestExtracter2 implements RDFConsumer {

    static Logger logger = Logger.getLogger(OWLTestExtracter2.class);

    static final int LITE = 1;
    static final int DL = 2;
    static final int FULL = 3;

    static final String converter = "http://phoebus.cs.man.ac.uk:9999/OWL/Converter";
    static final String validater = "http://phoebus.cs.man.ac.uk:9999/OWL/Validator";

    static final String testURL = "http://www.w3.org/2002/03owlt/";
    static final String xsd = "http://www.w3.org/2001/XMLSchema#";

    Set tests;
    Map testTypes;
    Map testLevels;
    
    public OWLTestExtracter2() {
	tests = new TreeSet( new Comparator() {
		public int compare( Object o1, Object o2 ) {
		    if ((o1 instanceof String) && (o2 instanceof String)) {
			return ((String) o1).compareTo((String) o2);
		    } else {
			return -1;
		    }
		}
	    });
	
	testTypes = new HashMap();
	testLevels = new HashMap();
    }
    
    public Set getTests() {
	return tests;
    }

    public Map getTestTypes() {
	return testTypes;
    }

    public Map getTestLevels() {
	return testLevels;
    }

    /**
     * Returns a collection of documents, indexed according to their level.
     */
    public void parseTests( URI uri ) throws Exception {

// 	tests = new TreeSet( new Comparator() {
// 		public int compare( Object o1, Object o2 ) {
// 		    if ((o1 instanceof String) && (o2 instanceof String)) {
// 			return ((String) o1).compareTo((String) o2);
// 		    } else {
// 			return -1;
// 		    }
// 		}
// 	    });
	
// 	testTypes = new HashMap();
// 	testLevels = new HashMap();
	
	RDFParser parser = new RDFParser();
	InputSource source = new InputSource(new InputStreamReader(uri.toURL().openStream()));
	source.setSystemId( uri.toString() );
	
	parser.parse( source, this );
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#startModel(String)
     */
    public void startModel(String arg0) throws SAXException {
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#endModel()
     */
    public void endModel() throws SAXException {
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#statementWithResourceValue(String, String, String)
     */
    public void statementWithResourceValue(
        String subj,
        String pred,
        String obj)
        throws SAXException {
	if ( pred.equals( OWLTestVocabularyAdapter.INSTANCE.getRDFType() ) ) {
	    if (OWLTestVocabularyAdapter.INSTANCE.getTestTypes().contains( obj ) ) {
		tests.add( subj );
		testTypes.put( subj, obj );
	    } 
// 	    if (obj.equals( OWLTestVocabularyAdapter.INSTANCE.getRDFXMLDocument() ) ) {
// 		logger.debug( "DOCUMENT!" + subj );
// 		test.getDocuments().add( subj );
// 	    }
	}


// 	if (OWLTestVocabularyAdapter.INSTANCE.getDocumentTypes().contains( pred ) ) {
// 	    tests.add( subj );
// 	    testsLevels.put( subj, obj );
// 	}
	
	if ( pred.equals( OWLTestVocabularyAdapter.INSTANCE.getLevel() ) ) {
	    Set levelSet = (Set) testLevels.get( subj );
	    if (levelSet == null) {
		levelSet = new HashSet();
		testLevels.put( subj, levelSet );
	    }
	    levelSet.add( obj );
	}
	
    }
    /**
 * Called when a statement with literal value is added to the model.
 *
 * @param subject URI of the subject resource
 * @param predicate             URI of the predicate resource
 * @param object                literal object value
 * @param parseType             the parse type of the literal
 * @param language              the language
 */
    public void statementWithLiteralValue(
        String subject,
        String predicate,
        String object,
        String parseType,
        String language)
        throws SAXException {
    }

    public void logicalURI(String arg0) throws SAXException {
    } 

    public void includeModel(String arg0, String arg1) throws SAXException {
    } 

    public void addModelAttribte(String arg0, String arg1)
        throws SAXException {
    }

    public static void usage() {
	System.out.println( "Usage: OWLTestExtracter2 options* url" );
	System.out.println( "  -h HTML output" );
	System.out.println( "  -d Include tests with datatypes" );
	System.out.println( "  -o Include obsoleted tests" );
	System.out.println( "  -l (lite|dl|full)" );
	System.out.println( "  -t (positive|negative|consistent|inconsistent|all)" );
	System.out.println( "  -z url is URL of a zip file containing test manifests." );
	System.out.println( "  -? help" );
	
	System.exit( 0 );
    }

    public static void main( String[] args ) {
	BasicConfigurator.configure();

	try {
	    boolean html = false;
	    int requiredLevel = FULL;
	    String requiredType = null;
	    boolean datatypes = false;
	    boolean obsolete = false;
	    boolean zip = false;

	    int c;
	    String arg = null;
	    Getopt g = new Getopt("OWLTestExtracter2", args, "?dhl:t:oz");

	    while ((c = g.getopt()) != -1) {
		switch(c) {
		case '?':
		    usage();
		case 'd':
		    datatypes = true;
		    break;
		case 'h':
		    html = true;
		    break;
		case 'o':
		    obsolete = true;
		    break;
		case 'l':
		    arg = g.getOptarg();

		    if (arg.equals("lite")) {
			requiredLevel = LITE;
		    } else if (arg.equals("dl")) {
			requiredLevel = DL;
		    } else if (arg.equals("full")) {
			requiredLevel = FULL;
		    } else {
			usage();
		    }
		    break;
		case 't':
		    arg = g.getOptarg();
		    if ( arg.equals( "positive" ) ) {
			requiredType = OWLTestVocabularyAdapter.INSTANCE.getPositiveEntailmentTest(); 
		    } else if ( arg.equals( "negative" ) ) {
			requiredType = OWLTestVocabularyAdapter.INSTANCE.getNegativeEntailmentTest();
		    } else if ( arg.equals( "consistent" ) ) {
			requiredType = OWLTestVocabularyAdapter.INSTANCE.getConsistencyTest() ;
		    } else if ( arg.equals( "inconsistent" ) ) {
			requiredType = OWLTestVocabularyAdapter.INSTANCE.getInconsistencyTest();
		    } else if ( arg.equals( "all" ) ) {
			requiredType = null;
		    } else {
			usage();
		    }
		    break;
		case 'z':
		    zip = true;
		    break;
		}

	    }
	    int i = g.getOptind();
	    
// 	    System.out.println(args[i]);
// 	    System.out.println(requiredLevel);
// 	    System.out.println(requiredType);
// 	    System.out.println(html);
// 	    System.out.println(datatypes);
// 	    System.exit(0);

	    if (html) {
		System.out.println("<html>");
		System.out.println("<head>");
		System.out.println("<style>");
		System.out.println("body {");
		System.out.println(" font-family: helvetica;");
		System.out.println("}");
		System.out.println("a {");
		System.out.println(" text-decoration:none;");
		System.out.println(" font-weight:bold;");
		System.out.println(" color:#ooc;");
		System.out.println("}");
		System.out.println("</style>");
		System.out.println("</head>");
		System.out.println("<body>");
		//		System.out.println("<h1>Tests: " + args[i] + "</h1>");
		System.out.println("<h1>Tests</h1>");
		System.out.println("<table border='1'>");
		System.out.println("<tr><th>Name</th><th>Level/Type</th><th>Type/Name</th><th></th><th>Status</th><th>Comment</th></tr>");
	    }


	    OWLTestExtracter2 extracter = new OWLTestExtracter2();
	    OWLTestParser otp = new OWLTestParser();
	    
	    while ( i < args.length ) {
		if (zip) {
		    URL url = new URL( args[i] );
		    ZipInputStream zis = new ZipInputStream( url.openStream() );
		    ZipEntry ze = null;
		    
		    while ( (ze=zis.getNextEntry()) !=null ) {
			String name = ze.getName();
			if ( name.matches( ".*Manifest.*rdf" ) ) {
			    //		    OWLTestParser otp = new OWLTestParser();
			    URI manifestURI = new URI( testURL + name );
			    
			    //		    OWLTestExtracter2 extracter = new OWLTestExtracter2();
			    extracter.parseTests( manifestURI );
			}
		    }
		    i++;
		} else {
		    //		    OWLTestExtracter2 extracter = new OWLTestExtracter2();
		    URI manifestURI = new URI( args[i] );
		    extracter.parseTests( manifestURI );
		    i++;
		}
 	    }
	    
	    int positiveTests = 0;
	    int negativeTests = 0; 
	    int consistencyTests = 0;
	    int inconsistencyTests = 0;
	    
	    for ( Iterator it = extracter.getTests().iterator();
		  it.hasNext(); ) {
		
		String test = (String) it.next();
		
		Set levels = (Set) extracter.getTestLevels().get( test );
		//			String level = (String) extracter.getTestLevels().get( test );
		//			System.out.println( test + " : " + level );
		
		if (levels==null) {
		    levels = new HashSet();
		}
		
		int testLevel = FULL;
		if (levels.contains(OWLTestVocabularyAdapter.INSTANCE.getOWLLite())) {
		    testLevel = LITE;
		} else if (levels.contains(OWLTestVocabularyAdapter.INSTANCE.getOWLDL())) {
		    testLevel = DL;
		} else if (levels.equals(OWLTestVocabularyAdapter.INSTANCE.getOWLFull())) {
		    testLevel = FULL;
		}
		
		String type = (String) extracter.getTestTypes().get( test );

		if ( ( requiredType==null || type.equals( requiredType ) )  
		     && (testLevel <= requiredLevel ) ) {
		    URI turi = new URI( test );
		    
		    OWLTest otest = otp.parseTest( turi );
		    if ( (obsolete || !otest.getStatus().equals("OBSOLETED") ) 
			 && 
			 (datatypes || otest.getDatatypes().isEmpty() )){
			if ( type.equals( OWLTestVocabularyAdapter.INSTANCE.getPositiveEntailmentTest() ) ) {
			    positiveTests++;
			} else if ( type.equals( OWLTestVocabularyAdapter.INSTANCE.getNegativeEntailmentTest() ) ) {
			    negativeTests++;
			} else if ( type.equals( OWLTestVocabularyAdapter.INSTANCE.getConsistencyTest() ) ) {
		    consistencyTests++;
			} else if ( type.equals( OWLTestVocabularyAdapter.INSTANCE.getInconsistencyTest() ) ) {
			    inconsistencyTests++;
			} 
			printTest( otest, html );
		    }
		}
	    }
	    
	    
	    if (html) {
		System.out.println("</table>");
		System.out.println("<h2>Summary</h2>");
		System.out.println("<table>");
		System.out.println("<tr><td>Positive:</td><td>" + positiveTests + "</td></tr>" );
		System.out.println("<tr><td>Negative:</td><td>" + negativeTests + "</td></tr>" );
		System.out.println("<tr><td>Consistency:</td><td>" + consistencyTests + "</td></tr>" );
		System.out.println("<tr><td>Inconsistency:</td><td>" + inconsistencyTests + "</td></tr>" );
		System.out.println("</table>");
		System.out.println("</body>");
		System.out.println("</html>");
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static String shorten( String str ) {
	if ( str.startsWith( OWLTestVocabularyAdapter.INSTANCE.OTEST ) ) {
	    return "otest:" + str.substring( OWLTestVocabularyAdapter.INSTANCE.OTEST.length() );
	}
	if ( str.startsWith( OWLTestVocabularyAdapter.INSTANCE.RTEST ) ) {
	    return "rtest:" + str.substring( OWLTestVocabularyAdapter.INSTANCE.RTEST.length() );
	}
	if ( str.startsWith( xsd ) ) {
	    return "xsd:" + str.substring( xsd.length());
	}
	if ( str.startsWith( testURL ) ) {
	    return str.substring( testURL.length());
	}
	return str;
    }

    public static void printTest( OWLTest test, boolean html ) {
	if (!html) {
	    System.out.println( test.getURI() );
	} else {
	    
	    System.out.println("<tr>");
	    
	    System.out.println("<td valign='top'><a href='" + test.getURI() + "'>" 
			       + shorten( test.getURI() ) + "</a>");
	    System.out.println("</td>");
	    String type = test.getType();
	    
	    if ( type.startsWith( OWLTestVocabularyAdapter.INSTANCE.OTEST ) ) {
		type = "otest:" + type.substring( OWLTestVocabularyAdapter.INSTANCE.OTEST.length() );
	    }
	    
	    //String testLevel = (String) test.getDocumentLevels().get( test.getURI() );
	    Set testLevelSet = (Set) test.getDocumentLevels().get( test.getURI() );
	    String testLevel = null;
	    if (testLevelSet.contains(OWLTestVocabularyAdapter.INSTANCE.getOWLLite())) {
		testLevel = OWLTestVocabularyAdapter.INSTANCE.getOWLLite();
	    } else if (testLevelSet.contains(OWLTestVocabularyAdapter.INSTANCE.getOWLDL())) {
		testLevel = OWLTestVocabularyAdapter.INSTANCE.getOWLDL();
	    } else if (testLevelSet.contains(OWLTestVocabularyAdapter.INSTANCE.getOWLFull())) {
		testLevel = OWLTestVocabularyAdapter.INSTANCE.getOWLFull();
	    }
	    
	    if (testLevel==null) {
		testLevel = "none";
	    }
	    if ( testLevel.startsWith( OWLTestVocabularyAdapter.INSTANCE.OTEST ) ) {
		testLevel = "otest:" + testLevel.substring( OWLTestVocabularyAdapter.INSTANCE.OTEST.length() );
	    }
	    String status = (String) test.getStatus();
	    
	    System.out.println("<td valign='top'>" + testLevel + "</td>");
	    System.out.println("<td valign='top'>" + type + "</td>");
	    String col = "white";
	    String txt = "black";
	    if (status.equals("APPROVED")) {
		col = "green";
		txt = "white";
	    } else if (status.equals("PROPOSED")) {
		col = "yellow";
	    } else if (status.equals("OBSOLETED")) {
		col = "red";
		txt = "white";
	    }
	    System.out.println("<td  colspan='3'>");
	    for (Iterator it = test.getDatatypes().iterator(); it.hasNext(); ) {
		String doc = (String) it.next();
		System.out.println( shorten( doc ) );
	    }	    
	    System.out.println("</td>");
	    System.out.println("<td style='color:" + txt + ";background-color:" + col + "' valign='top'>" + status + "</td>");
	    String description = "";
	    try {
		description = (String) test.getDescriptions().get( test.getURI() );
	    } catch (ClassCastException ex) {
	    }
	    System.out.println("<td>"+ description + "</td>");
	    System.out.println("</tr>");
	    
	    
	    //	System.out.println("<td valign='top'><table>");
	    for (Iterator it = test.getDocuments().iterator(); it.hasNext(); ) {
		String doc = (String) it.next();
		System.out.println("<tr><td></td>");
		
		Set levels = (Set) test.getDocumentLevels().get( doc );
		/* Assumes there's at least one */
		String level = "";
		Iterator lit = levels.iterator();
		level = (String) lit.next();
		//		String level = (String) test.getDocumentLevels().get( doc );
		String role = (String) test.getDocumentRoles().get( doc );
		
		String vl = "Full";
		
		if ( level.startsWith( OWLTestVocabularyAdapter.INSTANCE.OTEST ) ) {
		    if ( level.equals( OWLTestVocabularyAdapter.INSTANCE.getOWLLite() ) ) {
			vl = "Lite";
		    } else if ( level.equals( OWLTestVocabularyAdapter.INSTANCE.getOWLDL() ) ) {
			vl = "DL";
		    }
		    
		    level = "otest:" + level.substring( OWLTestVocabularyAdapter.INSTANCE.OTEST.length() );
		}
		if ( role.startsWith( OWLTestVocabularyAdapter.INSTANCE.OTEST ) ) {
		    role = "otest:" + role.substring( OWLTestVocabularyAdapter.INSTANCE.OTEST.length() );
		}
		if ( role.startsWith( OWLTestVocabularyAdapter.INSTANCE.RTEST ) ) {
		    role = "rtest:" + role.substring( OWLTestVocabularyAdapter.INSTANCE.RTEST.length() );
		}
		
		System.out.println("<td valign='top'>" + role + "</td>");
		System.out.println("<td valign='top'><a href='" + doc + "'>" + shorten( doc )+ "</a></td>" );
		System.out.println("<td valign='top'>" + level + "</td>");
		
		System.out.println("<td><a href='" + validater + "?url=" + doc + "&level=" + vl + "'>validate</a></td>");
		System.out.println("<td><a href='" + converter + "?url=" + doc + "&format=abstract'>abstract</a></td>");
		System.out.println("<td><a href='" + converter + "?url=" + doc + "&format=tptp'>tptp</a></td>");
		System.out.println("</tr>");
	    }
	    //	System.out.println("</table></td>");
	    System.out.println("</tr>");
	}
    }


}// OWLTestExtracter2
