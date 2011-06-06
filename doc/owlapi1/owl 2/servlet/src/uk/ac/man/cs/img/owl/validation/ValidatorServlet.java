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
 * Filename           $RCSfile: ValidatorServlet.java,v $
 * Revision           $Revision: 1.7 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2005/09/22 10:00:27 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.validation; // Generated package name

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import uk.ac.man.cs.img.owl.validation.SpeciesValidator;
import java.io.StringWriter;
import org.semanticweb.owl.model.OWLOntology;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.log4j.BasicConfigurator;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import java.io.StringWriter;
import java.io.Writer;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.semanticweb.owl.io.abstract_syntax.Renderer;
import uk.ac.man.cs.img.owl.validation.ConstructChecker;
import java.util.Set;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Basic Validation Servlet. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: ValidatorServlet.java,v 1.7 2005/09/22 10:00:27 sean_bechhofer Exp $
 */

public class ValidatorServlet extends HttpServlet 
{

    public ValidatorServlet()
    {
	BasicConfigurator.configure();
	System.setProperty("org.semanticweb.owl.util.OWLConnection",
			   "org.semanticweb.owl.impl.model.OWLConnectionImpl");
    }

    /* We'll allow both POST and GET. */
    
    public void doGet( HttpServletRequest request,
		       HttpServletResponse response ) 
	throws IOException, ServletException {
	doRequest(request, response);
    }
    
    public void doPost(HttpServletRequest request,
		       HttpServletResponse response) 
	throws IOException, ServletException {
	doRequest(request, response);
    }
    
    public void doRequest(HttpServletRequest request,
			  HttpServletResponse response) 
	throws IOException, ServletException 
    {  
	response.setContentType("text/html;charset=utf-8");
	PrintWriter out = new PrintWriter(response.getWriter());
	printHeader( out );
	boolean printAbstract = true;
	boolean printLisp = false;
	boolean printConstructs = false;
	String url = request.getParameter("url");
	String abstractForm = request.getParameter("abstract");
	String constructs = request.getParameter("constructs");
	String lispForm = request.getParameter("fact");
	String rdf = request.getParameter("rdf");
	printAbstract = (abstractForm!=null 
			 && abstractForm.equals("yes"));
	printConstructs = (constructs!=null 
			 && constructs.equals("yes"));
	printLisp = (lispForm!=null 
		     && lispForm.equals("yes"));
	URI uri = null;
	/* Used if we're reading from a reader */
	Reader reader = null;
	URI physicalURI = null;
	try {
	    if (url != null && !url.equals("")) {
		/* If we've got a url, use that */
		uri = new URI( url.trim() );
	    } else {
		/* otherwise, try and create a reader on the rdf */
		
		if (rdf != null && !rdf.equals("")) {
		    reader = new StringReader( rdf );
		    physicalURI = new URI( "urn:wonderwebValidator" );
		}
	    }
	} catch ( URISyntaxException e ) {
	    out.println("URI Syntax Exception! " + url );
	} // end of try-catch


	if ( uri!= null || reader !=null ) { 
	    /* We've got _something_ to play with. */
	    try {
		OWLConnection connection = OWLManager.getOWLConnection();
		SpeciesValidator sv = new SpeciesValidator();
		sv.setConnection( connection );
		StringWriter lw = new StringWriter();
		StringWriter dw = new StringWriter();
		StringWriter fw = new StringWriter();
		StringWriter rw = new StringWriter();
		StringWriter mw = new StringWriter();
		final PrintWriter lpw = new PrintWriter( lw );
		final PrintWriter dpw = new PrintWriter( dw );
		final PrintWriter fpw = new PrintWriter( fw );
		final PrintWriter rpw = new PrintWriter( rw );
		final PrintWriter mpw = new PrintWriter( mw );
		/* Give a reporter. */
		sv.setReporter(new SpeciesValidatorReporter() {
			public void ontology( OWLOntology onto ) {
			}
			
			public void done( String str ) {
			}
			
			public void message( String str ) {
			    mpw.println( "<li>" + str + "</li>" );
			}
			
			public void explain( int l, String str ) {
			    switch (l) {
			    case SpeciesValidator.LITE:
				lpw.println( "<li>" + str + "</li>");
				break;
			    case SpeciesValidator.DL:
				dpw.println( "<li>" + str + "</li>");
				break;
			    case SpeciesValidator.FULL:
				fpw.println( "<li>" + str + "</li>");
				break;
			    case SpeciesValidator.OTHER:
				rpw.println( "<li>" + str + "</li>");
				break;
			    }
			}

			public void explain( int l, int code, String str ) {
			    switch (l) {
			    case SpeciesValidator.LITE:
				lpw.println( "<li>" + str + "</li>");
				break;
			    case SpeciesValidator.DL:
				dpw.println( "<li>" + str + "</li>");
				break;
			    case SpeciesValidator.FULL:
				fpw.println( "<li>" + str + "</li>");
				break;
			    case SpeciesValidator.OTHER:
				rpw.println( "<li>" + str + "</li>");
				break;
			    }
			}
		    });
		String level = request.getParameter("level");
		boolean ok = false;
		int l = SpeciesValidator.OTHER;
		try {
		    if ( level.equals( "Lite" ) ) {
			if ( uri != null ) {
			    ok = sv.isOWLLite( uri );
			} else {
			    ok = sv.isOWLLite( reader, physicalURI );
			}
			l = SpeciesValidator.LITE;
		    } else if ( level.equals( "DL" ) ) {
			if ( uri != null ) {
			    ok = sv.isOWLDL( uri );
			} else {
			    ok = sv.isOWLDL( reader, physicalURI );
			}			    
			l = SpeciesValidator.DL;
		    } else  if ( level.equals( "Full" ) ) {
			if ( uri != null ) {
			    ok = sv.isOWLFull( uri );
			} else {
			    ok = sv.isOWLFull( reader, physicalURI );
			}
			l = SpeciesValidator.FULL;
		    } else {
			l = 999;
		    }
		    out.println( "<h1>OWL Species Validation Report</h1>" );
		    if ( uri != null ) {
			out.println( "URI: <a href=\"" + uri + "\">" + uri + "</a>");
		    };
		    if ( l!=999 ) {
			/* Print out messages as appropriate */
			if ( (l < SpeciesValidator.LITE) && !lw.toString().equals("")) {
			    out.println( "<h3>OWL Lite</h3>");
			    out.println( "<ul>");
			    out.println( lw.toString() );
			    out.println( "</ul>");
			} // end of if ()
			if ( (l < SpeciesValidator.DL) && !dw.toString().equals("")) {
			    out.println( "<h3>OWL DL</h3>");
			    out.println( "<ul>");
			    out.println( dw.toString() );
			    out.println( "</ul>");
			} // end of if ()
			if ( (l < SpeciesValidator.FULL) && !fw.toString().equals("")) {
			    out.println( "<h3>OWL Full</h3>");
			    out.println( "<ul>");
			    out.println( fw.toString() );
			    out.println( "</ul>");
			} // end of if ()
			if ( (l < SpeciesValidator.OTHER) && !rw.toString().equals("")) {
			    out.println( "<h3>OTHER</h3>");
			    out.println( "<ul>");
			    out.println( rw.toString() );
			    out.println( "</ul>");
			} // end of if ()
			if ( !mw.toString().equals("")) {
			    out.println( "<h3>Additional Messages</h3>");
			    out.println( "<ul>");
			    out.println( mw.toString() );
			    out.println( "</ul>");
			} // end of if ()
			out.println("<h2>Conclusion</h1>");
			out.println( "<p><strong>" + level + "</strong>: " + 
				     ((ok)?"<span class=\"yes\">YES</span>":"<span class=\"no\">NO</span>"));
			out.println("<a href=\"why.html\">Why?</a></p>");
			out.println("<hr/>");
		    }
		    
		    
		    connection = OWLManager.getOWLConnection();

		    OWLRDFParser parser = new OWLRDFParser();
		    parser.setConnection( connection );

		    OWLOntology onto = null;
		    if ( uri != null ) {
			onto = 
			    parser.parseOntology(uri);
		    } else {
			/* Have to renew the reader. */
			reader = new StringReader( rdf );
			onto =
			    parser.parseOntology( reader, 
						  physicalURI );
		    }
		    
		    if ( printConstructs ) {
			out.println("<h2>Constructs Used</h2>");
			out.println("<div class='box'>");
			out.println("<pre>");
			out.println( constructs( onto ) );
			out.println("</pre>");
			out.println("</div>");
			out.println("<hr/>");
		    }
		    if ( printAbstract ) {
			out.println("<h2>Abstract Syntax Form</h2>");
			out.println("<div class='box'>");
			out.println("<pre>");
			out.println( abstractForm( onto ));
			out.println("</pre>");
			out.println("</div>");
			out.println("<hr/>");
		    }

		    if ( printLisp ) {
			out.println("<div class='box'>");
			out.println("<pre>");
			out.println( lispForm( onto ) );
			out.println("</pre>");
			out.println("</div>");
			out.println("<hr/>");
		    }

		    out.println("<form action=\"Validator\" method=\"post\">");
		    out.println(" <table>");
		    out.println("  <tr>");
		    out.println("  <tr>");
		    out.println("  <td><b>RDF:</b></td>");
		    out.println("  <td><textarea rows=\"12\" cols=\"80\" name=\"rdf\">" + ((rdf!=null)?rdf:"") + "</textarea></td><td></td>");
		    out.println("  </tr>");

		    out.println("  <td><b>URL:</b></td>");
		    out.println("  <td><input type=\"text\" size=\"80\" name=\"url\" value=\"" + ((uri!=null)?uri.toString():"") + "\"/></td>");
		    out.println("  <td><input type=\"submit\" value=\" Validate \"/></td>");
		    out.println("  </tr>");
		    out.println(" </table>");
		    out.println(" <table>");
		    out.println("  <tr>");
		    out.println("   <td><input type=\"radio\" name=\"level\" value=\"None\"" + (l==999?"checked=\"checked\"":"") +"/></td>");
		    out.println("   <td>None</td>");
		    out.println("   </tr><tr>");

		    out.println("   <td><input type=\"radio\" name=\"level\" value=\"Lite\"" + (l==SpeciesValidator.LITE?"checked=\"checked\"":"") +"/></td>");
		    out.println("   <td>OWL Lite</td>");
		    out.println("   </tr><tr>");
		    
		    out.println("   <td><input type=\"radio\" name=\"level\" value=\"DL\"" + (l==SpeciesValidator.DL?"checked=\"checked\"":"") + "/></td>");
		    out.println("   <td>OWL DL</td>");
		    out.println("   </tr><tr>");
		    
		    out.println("   <td><input type=\"radio\" name=\"level\" value=\"Full\"" + (l==SpeciesValidator.FULL?"checked=\"checked\"":"")+ "/></td>");
		    out.println("   <td>OWL Full</td>");
		    out.println("   </tr><tr>");
		    
		    out.println("   <td><input type=\"checkbox\" name=\"constructs\" value=\"yes\"" + (printConstructs?"checked=\"checked\"":"")+ "/></td>");
		    out.println("   <td>Show Constructs Used</td>");
		    out.println("   </tr><tr>");
		    
		    out.println("   <td><input type=\"checkbox\" name=\"abstract\" value=\"yes\"" + (printAbstract?"checked=\"checked\"":"")+ "/></td>");
		    out.println("   <td>Show Abstract Form</td>");
		    out.println("   </tr>");
		    out.println(" </table>");
		    out.println("</form>");
		} catch ( Exception e ) {
		    out.println( "Server Exception: " + e.getMessage() );
		    printForm( out );
		} // end of try-catch
	    } catch ( OWLException e ) {
		out.println("OWL Exception! " + e.getMessage() );
		printForm( out );
	    } // end of try-catch
	} else {
	    printInfo( out );
	    printForm( out );
	}
	printFooter( out );
    }

    protected void printHeader( PrintWriter out ) {
	out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	out.println("<!DOCTYPE html");
	out.println("     PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
	out.println("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
	out.println("<html lang=\"en\">");
	
	out.println("<head>");
	out.println("<title>WonderWeb OWL Ontology Validator</title>"); 
  	out.println("<style type=\"text/css\">");
	out.println("body {");
	out.println("font-family: arial;");
	out.println("background-color: #EEEEEE;");
	out.println("margin: 40 40 40 40;");
	out.println("}");
	out.println(".yes {");
	out.println("font-weight: bold;");
	out.println("color: green;");
	out.println("}");
	out.println(".no {");
	out.println("font-weight: bold;");
	out.println("color: red;");
	out.println("}");
	out.println(".faint {");
	out.println("background-color: #FFFFFF;");
	out.println("color: #AAAAAA;");
	out.println("}");
	out.println(".box {");
	out.println("padding: 0px 20px 10px 20px;"); 
	out.println("border:1px solid #888;"); 
	out.println("background-color: #FFFFFF;");
	out.println("}");
// 	out.println("a {");
// 	out.println("background-color: yellow;");
// 	out.println("}");
  	out.println("</style>");
	out.println("</head>");
	out.println("<body>");
    }

    protected void printFooter( PrintWriter out ) {
	printHost( out );
	out.println("</body>");
	out.println("</html>");
    }

    
    
    protected void printInfo(PrintWriter out) {
	out.println("<h1>WonderWeb OWL Ontology Validator</h1>");
	out.println("<p>Paste the URL of an OWL-RDF ontology into the box below, select a language species and hit return or press the Validate button. Alternatively, paste RDF into the textarea. This should be a complete RDF file, including headers. The servlet will then attempt to validate the ontology against the selected species. Any constructs found which relate to particular species of <a href=\"http://www.w3.org/2001/sw/WebOnt\">OWL</a> will be reported. ");
// 	out.println("<p>The following rules are used to determine the species.");
// 	out.println("<ul>");
// 	out.println("");
// 	out.println("<li><strong>Namespace Separation</strong>. An ontology is OWL-Full if");
// 	out.println("any URI-reference is declared to be more than one of");
// 	out.println("<code>Class</code>,");
// 	out.println("<code>Individual</code>, <code>ObjectProperty</code> or <code>DataProperty</code>.</li>");
// 	out.println("");
// 	out.println("<li><strong>Correct OWL Usage</strong>. An ontology is OWL-Full if any");
// 	out.println("of the reserved parts of the OWL, RDF or RDF(S) vocabularies have been");
// 	out.println("redefined.");
// 	out.println("</li>");
// 	out.println("");
// 	out.println("<li><strong>OWL Namespace Usage</strong>. An ontology is not in any of the OWL dialects if the URI of any <code>Class</code>,");
// 	out.println("<code>Individual</code>, <code>ObjectProperty</code> or <code>DataProperty</code> is in the OWL namespace.");
// 	out.println("</li>");
// 	out.println("");
// 	out.println("<li><strong>Class Axioms</strong>. An ontology is (at least) OWL-DL if");
// 	out.println("it contains any <code>equivalentClass</code> or");
// 	out.println("<code>rdfs:subClassOf</code> axioms that refer to anything other than");
// 	out.println("class names or restrictions.");
// 	out.println("</li>");
// 	out.println("");
// 	out.println("<li><strong>Expressivity</strong>. An ontology is (at least) OWL-DL if");
// 	out.println("it uses any of <code>oneOf</code>, <code>disjointWith</code>,");
// 	out.println("<code>unionOf</code>, <code>intersectionOf</code>,");
// 	out.println("<code>complementOf</code> or <code>hasValue</code>.</li>");
// 	out.println("");
// 	out.println("<li><strong>Cardinality</strong>. An ontology is (at least) OWL-DL if");
// 	out.println("it uses any cardinalities other than 0 or 1 in a <code>minCardinality</code>,");
// 	out.println("<code>maxCardinality</code> or <code>cardinality</code>");
// 	out.println("expression.</li>");
// 	out.println("</ul>");
// 	out.println("</p>");
	out.println("<p>In addition, if requested, the validator will return a description of the classes,");
	out.println("properties and individuals in the ontology in terms of the OWL Abstract Syntax.</p>"); 

	out.println("<p>Note that if the ontology given is not OWL DL, then the Abstract Syntax description");
	out.println("shown <strong>may not</strong> accurately reflect the contents of the original ontology.</p>"); 

	out.println("</p><p>No guarantees are provided as to the correctness of this validator.</p><p>Developed by Sean Bechhofer of the University of Manchester and Raphael Volz of the University of Karlsruhe as part of the EU IST Project <a href='http://wonderweb.semanticweb.org'>WonderWeb</a>.</p>"); 
	out.println("<hr/>");
    }
    
    protected void printForm( PrintWriter out ) {
	out.println("<form action=\"Validator\" method=\"post\">");
	out.println("<table>");
	out.println("<tr><td>");
	out.println(" <table>");
  	out.println("  <tr>");
	out.println("  <td><b>RDF:</b></td>");
	out.println("  <td><textarea rows=\"12\" cols=\"80\" name=\"rdf\"></textarea></td><td></td>");
	out.println("  </tr>");

  	out.println("  <tr>");
	out.println("  <td><b>URL:</b></td>");
	out.println("  <td><input type=\"text\" size=\"80\" name=\"url\"/></td>");
	out.println("  <td><input type=\"submit\" value=\" Validate \"/></td>");
	out.println("  </tr>");
	out.println(" </table>");
	out.println(" <table>");
	out.println("  <tr>");
	out.println("   <td><input type=\"radio\" name=\"level\" value=\"None\"/></td>");
	out.println("   <td>None</td>");
	out.println("   </tr><tr>");
	
	out.println("   <td><input type=\"radio\" name=\"level\" value=\"Lite\" checked=\"checked\"/></td>");
	out.println("   <td>OWL Lite</td>");
	out.println("   </tr><tr>");

	out.println("   <td><input type=\"radio\" name=\"level\" value=\"DL\"/></td>");
	out.println("   <td>OWL DL</td>");
	out.println("   </tr><tr>");

	out.println("   <td><input type=\"radio\" name=\"level\" value=\"Full\"/></td>");
	out.println("   <td>OWL Full</td>");
	out.println("   </tr><tr>");
	
	out.println("   <td><input type=\"checkbox\" name=\"constructs\" value=\"yes\"/></td>");
	out.println("   <td>Show Constructs Used</td>");
	out.println("   </tr><tr>");
	
	out.println("   <td><input type=\"checkbox\" name=\"abstract\" value=\"yes\" checked=\"checked\"/></td>");
	out.println("   <td>Show Abstract Form</td>");
	out.println("   </tr>");
	out.println(" </table>");
	out.println("</td></tr>");
	out.println("</table>");
	out.println("</form>");
    }

    protected void printHost( PrintWriter out ) {
	String host;
	try {
	    host = InetAddress.getLocalHost().getHostName() + " [" +
		InetAddress.getLocalHost().getHostAddress() + "] ";
	} catch (UnknownHostException e) {
	    host = "unknown";
	}

	out.println("<table>");
	out.println("<tr>");
	out.println("<td><span style='font-size:xx-small;'>OWL Validator running on "
		    + host 
		    + " under " 
		    + getServletContext().getServerInfo() + "</span></td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td><span style='font-size:xx-small;'>&copy; University of Manchester, 2003, &copy; University of Karlsruhe, 2003</span></td>");
	out.println("</tr>");
	out.println("</table>");
    }

    protected void printHelp( PrintWriter out ) {
	
    }

    protected String constructs( OWLOntology onto ) {
	String result = "";
	try {
	    ConstructChecker checker
		= new ConstructChecker();
	    for (Iterator it = ConstructChecker.used( checker.constructsUsed( onto ) ).iterator(); it.hasNext(); ) {
		String s = (String) it.next();
		result = result + s + "<br/>";
	    }
	    return result;
	} catch (Exception ex) {
	    return ( "Error: " + ex.getMessage() );
	}
    }

    protected String abstractForm( OWLOntology onto) {
	try {
	    Renderer renderer = new Renderer();
	    Writer writer = new StringWriter();
	    renderer.renderOntology( onto, writer );
	    String results = writer.toString();
	    /* Now we have to make sure that < and > get dealt with
	     * properly. */
	    results = results.replaceAll("<","&lt;");
	    results = results.replaceAll(">","&gt;");
	    return( results );
	} catch (Exception ex) {
	    return ( "Error: " + ex.getMessage() );
	}
    }

    protected String lispForm( OWLOntology onto ) {
	try {
	    uk.ac.man.cs.img.owl.io.fact.Renderer renderer = 
		new uk.ac.man.cs.img.owl.io.fact.Renderer();
	    Writer writer = new StringWriter();
	    renderer.renderOntology( onto, writer );
	    return( writer.toString() );
	} catch (Exception ex) {
	    return ( "Error: " + ex.getMessage() );
	}
    }

} // ValidatorServlet



/*
 * ChangeLog
 * $Log: ValidatorServlet.java,v $
 * Revision 1.7  2005/09/22 10:00:27  sean_bechhofer
 * Minor change to the front page wording.
 *
 * Revision 1.6  2004/07/09 12:07:48  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.5  2004/06/22 13:57:38  sean_bechhofer
 * Fixing problems with validation/expressivity checking code.
 *
 * Revision 1.4  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.3  2004/01/08 11:13:46  sean_bechhofer
 * Fixing "lost" URIs due to <> bracketing.
 *
 * Revision 1.2  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.11  2003/10/02 09:37:23  bechhofers
 * Cleaning up access to Connection. Addition of Servlet Test validator.
 *
 * Revision 1.10  2003/10/01 16:51:09  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.9  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.8  2003/06/20 14:07:51  seanb
 * Addition of some documentation. Minor tinkering.
 *
 * Revision 1.7  2003/06/06 14:28:57  seanb
 * Adding new rendering
 *
 * Revision 1.6  2003/05/27 08:45:11  seanb
 * Adding some explanation
 *
 * Revision 1.5  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.4  2003/03/21 14:13:57  seanb
 * no message
 *
 * Revision 1.3  2003/03/20 10:27:01  seanb
 * Additions to Validation Servlet.
 *
 * Revision 1.2  2003/02/21 16:11:56  seanb
 * Updates to servlet
 *
 * Revision 1.1  2003/02/19 10:15:09  seanb
 * Moving validation servlet to separate directory.
 *
 * Revision 1.1  2003/02/18 18:44:07  seanb
 * Further improvements to parsing. Addition of Validation Servlet.
 *
 */
