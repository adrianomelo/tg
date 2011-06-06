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
 * Filename           $RCSfile: PedroServlet.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/12/19 12:04:16 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.presentation; // Generated package name

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
import org.semanticweb.owl.io.Renderer;

import org.semanticweb.owl.inference.OWLTaxonomyReasoner;
import uk.ac.man.cs.img.owl.inference.NonInferencingTaxonomyReasoner;
import java.util.Iterator;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.model.OWLClass;
import java.util.Set;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Basic Validation Servlet. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: PedroServlet.java,v 1.2 2003/12/19 12:04:16 sean_bechhofer Exp $
 */

public class PedroServlet extends HttpServlet 
{

    public PedroServlet()
    {
	BasicConfigurator.configure();
	/* Set up the default connection */
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
	if (request.getParameter("url") == null) {
	    response.setContentType("text/html");
	    PrintWriter out = new PrintWriter(response.getWriter());
	    printHeader( out );
	    printInfo( out );
	    printForm( out );
	    printFooter( out );
	} else {
	    response.setContentType("text/plain");
	    PrintWriter out = new PrintWriter(response.getWriter());
	    
	    String url = request.getParameter("url");
	    String fragment = request.getParameter("fragment");
	    if ( url!= null && 
		 !url.equals("")) {
		try {
		    URI uri = new URI( url.trim() );
		    out.println( renderHierarchy( uri, 
						  ( fragment!=null &&
						    fragment.equals("yes") ) ) );
		} catch ( Exception e ) {
		    out.println( "Server Exception: " + e.getMessage() );
		} // end of try-catch
	    } else {
		out.println("No URL supplied!");
	    }
	}
    }

    protected void printHeader( PrintWriter out ) {
	out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	out.println("<!DOCTYPE html");
	out.println("     PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
	out.println("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
	out.println("<html lang=\"en\">");
	
	out.println("<head>");
	out.println("<title>OWL Ontology Pedro Servlet</title>"); 
  	out.println("<style type=\"text/css\">");
	out.println("body {");
	out.println("font-family: arial;");
	out.println("background-color: #DDDDEE;");
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
	out.println("<h1>OWL Ontology Pedro Servlet</h1>");
	out.println("<p>Paste the URL of an OWL-RDF ontology into the box below, select a format and hit return or press the Convert button. The servlet will return with a representation of the concept hierarchy in tab-delimited form. ");
	out.println("<hr/>");
    }
    
    protected void printForm( PrintWriter out ) {
	out.println("<form action=\"Pedro\" method=\"get\">");
	out.println(" <table>");
	out.println("  <tr>");
	out.println("  <td><b>URL:</b></td>");
	out.println("  <td><input type=\"text\" size=\"80\" name=\"url\" value=\"\"/></td>");
	out.println("  <td><input type=\"submit\" value=\" Convert \"/></td>");
	out.println("  </tr>");
	out.println(" </table>");
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
	out.println("<td><span style='font-size:xx-small;'>OWL Servlet running on "
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

    protected String renderHierarchy( URI uri, boolean fragment ) {
	try {
	    /* Will use the default implementation */

	    OWLConnection connection = OWLManager.getOWLConnection();
	    
	    OWLRDFParser parser = new OWLRDFParser();
	    parser.setConnection( connection );

	    OWLOntology ontology = 
		parser.parseOntology(uri);
	    
	    
	    Writer writer = new StringWriter();
	    PrintWriter pw = new PrintWriter( writer );
	    dumpHierarchy( ontology, pw, fragment );
	    return( writer.toString() );
	} catch (Exception ex) {
	    return ( "Error: " + ex.getMessage() );
	}
    }

    private void dumpHierarchy( OWLTaxonomyReasoner reasoner,
				PrintWriter pw, 
				OWLClass clazz, 
				boolean fragment,
				int level ) throws OWLException {
	for (int i=0; i<level; i++) {
	    pw.print("\t");
	    //	    System.out.print(" ");
	}
	if ( fragment ) {
	    pw.println( clazz.getURI().getFragment() );
	} else {
	    pw.println( clazz.getURI() );
	}	    
	//	System.out.println( clazz.getURI() );
	Set subs = reasoner.subClassesOf( clazz );
	
	for (Iterator sit = subs.iterator(); sit.hasNext(); ) {
	    for (Iterator innerIt = ((Set) sit.next()).iterator();
		 innerIt.hasNext(); ) { 
		OWLClass cl = (OWLClass) innerIt.next();
		dumpHierarchy( reasoner, pw, cl, fragment, level+1 );
	    }			
	}
    }
    
    private void dumpHierarchy( OWLOntology ontology,
				PrintWriter pw, 
				boolean fragment ) throws OWLException {
	try {
	    NonInferencingTaxonomyReasoner reasoner = 
		new NonInferencingTaxonomyReasoner();

	    reasoner.setOntology( ontology );
	    
	    OWLClass thing = ontology.getClass( new URI( OWLVocabularyAdapter.INSTANCE.getThing() ) );
	    dumpHierarchy( reasoner, pw, thing, fragment, 0 );
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

} // PedroServlet



/*
 * ChangeLog
 * $Log: PedroServlet.java,v $
 * Revision 1.2  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1  2003/11/18 18:09:35  sean_bechhofer
 * Addition of servlet for Pedro hierarchy
 *
 *
 *
 */
