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
 * Filename           $RCSfile: FixerServlet.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/04/16 16:29:49 $
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
import java.util.HashMap;
import java.util.Map;
import org.semanticweb.owl.io.abstract_syntax.Renderer;
import uk.ac.man.cs.img.owl.validation.ConstructChecker;
import java.util.Set;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import uk.ac.man.cs.img.owl.fixing.Fixer;

/**
 * Basic Fixing Servlet. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: FixerServlet.java,v 1.1 2004/04/16 16:29:49 sean_bechhofer Exp $
 */

public class FixerServlet extends HttpServlet 
{

    public FixerServlet()
    {
	BasicConfigurator.configure();
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
	if ( url!= null && 
	     !url.equals("")) {
	    try {
		URI uri = new URI( url.trim() );
		/* For validation */
		SpeciesValidator validator = new SpeciesValidator();
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
		validator.setReporter( reporter );
		
		/* Used to check then fix */
		Fixer fixer = new Fixer();

		StringWriter rw = new StringWriter();
		StringWriter mw = new StringWriter();
		final PrintWriter rpw = new PrintWriter( rw );
		final PrintWriter mpw = new PrintWriter( mw );
		fixer.setReportWriter( rpw );
		fixer.setMessageWriter( mpw );
		out.println( "<h1>OWL Patching Report</h1>" );
		out.println( "URI: <a href=\"" + uri + "\">" + uri + "</a>");
		int opti = 0;
		boolean done = false;
		/* Loop through the strategies until success or we run
		 * out of strategies */
		while ( !done && opti<Fixer.options.length ) {
		    Fixer.Strategy strategy = fixer.newStrategy( Fixer.options[opti] );
		    fixer.setStrategy( strategy );
		    int result = fixer.applyStrategy( uri, null );
		    if (result==0) {
			/* SUCCESS */
			done = true;
			out.println( "<h2>Details</h2>" );
			out.println("<p>Patching was successful. Options employed were:</p>");
			out.println("<ul>");
			if ( fixer.getStrategy().fixOthers ) {
			    out.println("<li><strong>Adding type triples</strong></li>");
			}
			if ( fixer.getStrategy().fixDC ) {
			    out.println("<li><strong>Adding Annotation type triples for DC vocabulary.</strong></li>");
			}
			if ( fixer.getStrategy().laxSchema ) {
			    out.println("<li><strong>Ignoring inclusion of OWL/RDF/RDF(S) schemas.</strong></li>");
			}
			if ( fixer.getStrategy().laxSameAs ) {
			    out.println("<li><strong>Handling inappropriate <code>owl:sameAs</code> use.</strong></li>");
			}
			out.println("</ul>");
			
			if ( fixer.getFixes().isEmpty() ) {
			    out.println("<p>No triples need to be added to the ontology.</p>" );
			} else {
			    out.println("Type triples that need to be added to the ontology are as follows.");
			    out.println("<ul>");
			    Set[] untyped = fixer.getUntyped();
			    if ( !untyped[Fixer.CLASSES].isEmpty() ) {
				out.println( "<li><strong>Classes:</strong>" );
				out.println("<ul>");
				for (Iterator it = untyped[Fixer.CLASSES].iterator(); it.hasNext(); ) {
				    Object obj = it.next();
				    out.println( "<li>" + obj + "</li>" );
				}
				out.println("</ul>");
				out.println("</li>");
			    }
			    if ( !untyped[Fixer.INDIVIDUALS].isEmpty() ) {
				out.println( "<li><strong>Individuals:</strong>" );
				out.println("<ul>");
				for (Iterator it = untyped[Fixer.INDIVIDUALS].iterator(); it.hasNext(); ) {
				    Object obj = it.next();
				    out.println( "<li>" + obj + "</li>" );
				}
				out.println("</ul>");
				out.println("</li>");
			    }
			    if ( !untyped[Fixer.DATATYPES].isEmpty() ) {
				out.println( "<li><strong>Datatypes:</strong>" );
				out.println("<ul>");
				for (Iterator it = untyped[Fixer.DATATYPES].iterator(); it.hasNext(); ) {
				    Object obj = it.next();
				    out.println( "<li>" + obj + "</li>" );
				}
				out.println("</ul>");
				out.println("</li>");
			    }
			    if ( !untyped[Fixer.OBJECT_PROPERTIES].isEmpty() ) {
				out.println( "<li><strong>Object Properties:</strong>" );
				out.println("<ul>");
				for (Iterator it = untyped[Fixer.OBJECT_PROPERTIES].iterator(); it.hasNext(); ) {
				    Object obj = it.next();
				    out.println( "<li>" + obj + "</li>" );
				}
				out.println("</ul>");
				out.println("</li>");
			    }
			    if ( !untyped[Fixer.DATATYPE_PROPERTIES].isEmpty() ) {
				out.println( "<li><strong>Datatype Properties:</strong>" );
				out.println("<ul>");
				for (Iterator it = untyped[Fixer.DATATYPE_PROPERTIES].iterator(); it.hasNext(); ) {
				    Object obj = it.next();
				    out.println( "<li>" + obj + "</li>" );
				}
				out.println("</ul>");
				out.println("</li>");
			    }
			    if ( !untyped[Fixer.ANNOTATION_PROPERTIES].isEmpty() ) {
				out.println( "<li><strong>Annotation Properties:</strong>" );
				out.println("<ul>");
				for (Iterator it = untyped[Fixer.ANNOTATION_PROPERTIES].iterator(); it.hasNext(); ) {
				    Object obj = it.next();
				    out.println( "<li>" + obj + "</li>" );
				}
				out.println("</ul>");
				out.println("</li>");
			    }
			    out.println("</ul>");
			    out.println("<p>RDF serialization of type triples:</p>" );
			    out.println("<div class='box'>");
			    out.println("<pre>");
			    for (Iterator it = fixer.getFixes().iterator();
				 it.hasNext(); ) {
				String str = (String) it.next();
				
				out.print( str.replaceAll("<","&lt;").replaceAll(">","&gt;") );
			    }
			    out.println("</pre>");
			    out.println("</div>");
			}
		    }
		    opti++;
		}
		if ( !done ) {
		    /* FAILURE */
		    out.println( "<h2>Details</h2>" );
		    out.println("<div class='box'>");
		    out.println("<pre>");
		    out.println("Sorry. The Patcher is unable to patch this ontology.");
		    out.println("</pre>");
		    out.println("</div>");
		}
		out.println("<hr/>");
		printForm( out, url );
	    } catch ( OWLException e ) {
		out.println("OWL Exception! " + e.getMessage() );
		printForm( out, url );
	    } catch ( URISyntaxException e ) {
		out.println("URI Syntax Exception! " + url );
		printForm( out, url );
	    } // end of try-catch
	} else {
	    printInfo( out );
	    printForm( out, "" );
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
	out.println("<title>OWL Ontology Patcher</title>"); 
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
	out.println(".box2 {");
	out.println("padding: 0px 20px 10px 20px;"); 
	out.println("border:1px solid #888;"); 
	out.println("background-color: #FFFF99;");
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
	out.println("<h1>OWL Ontology Patcher</h1>");
	out.println("<p>This service will try and produce a valid OWL DL ontology. It does");
	out.println("this by adopting a number of strategies including:</p>");
	out.println("");
	out.println("<ul>");
	out.println("");
	out.println("<li>Adding type triples where necessary.</li>");
	out.println("");
	out.println("<li>Ignoring import of the OWL,RDF or RDF(S) schema.</li>");
	out.println("");
	out.println("<li>Treating vocabulary \"misuse\" such as <code>owl:sameAs</code>");
	out.println("used for Classes or Properties.</li>");
	out.println("");
	out.println("</ul>");
	out.println("");
	out.println("<p>The service uses a number of heuristics. In");
	out.println("some situations, this could change the semantics of your ontology,");
	out.println("particularly if you are really interested in OWL Full reasoning.</p>");
    }
    
    protected void printForm( PrintWriter out, String uri ) {
	out.println("<form action=\"Patcher\" method=\"get\">");
	out.println("<table>");
	out.println("<tr><td>");
	out.println(" <table>");
  	out.println("  <tr>");
	out.println("  <td><b>URL:</b></td>");
	out.println("  <td><input type=\"text\" size=\"80\" name=\"url\" value=\"" + uri + "\"/></td>");
	out.println("  <td><input type=\"submit\" value=\" Fix \"/></td>");
	out.println("  </tr>");
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
	out.println("<td><span style='font-size:xx-small;'>OWL Patcher running on "
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

} // FixerServlet



/*
 * ChangeLog
 * $Log: FixerServlet.java,v $
 * Revision 1.1  2004/04/16 16:29:49  sean_bechhofer
 * Addition of fixer/patcher
 *
 *
 */
