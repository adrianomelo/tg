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
 * Filename           $RCSfile: ConverterServlet.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/20 13:16:12 $
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
import org.semanticweb.owl.io.Renderer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Basic Validation Servlet. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: ConverterServlet.java,v 1.3 2006/03/20 13:16:12 sean_bechhofer Exp $
 */

public class ConverterServlet extends HttpServlet 
{

    public ConverterServlet()
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
	    String format = request.getParameter("format");
	    if ( url!= null && 
		 !url.equals("") &&
		 format!=null &&
		 !format.equals("")) {
		try {
		    URI uri = new URI( url.trim() );
		    Renderer renderer = new org.semanticweb.owl.io.abstract_syntax.Renderer();
		    if ( format.equals("abstract") ) {
		    } else if ( format.equals("fact") ) {
			renderer = new uk.ac.man.cs.img.owl.io.fact.Renderer();
		    } else if ( format.equals("factplus") ) {
			renderer = new uk.ac.man.cs.img.owl.io.factplus.Renderer();
		    } else if ( format.equals("dig2.0") ) {
			renderer = new uk.ac.man.cs.img.owl.io.dig2_0.Renderer();
		    } else if ( format.equals("tptp") ) {
			renderer = new uk.ac.man.cs.img.owl.io.tptp.Renderer();
		    } else if ( format.equals("rdf") ) {
			renderer = new org.semanticweb.owl.io.owl_rdf.Renderer();
		    }
		    out.println( render( uri, renderer ) );
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
	out.println("<title>OWL Ontology Converter</title>"); 
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
	out.println("<h1>OWL Ontology Converter</h1>");
	out.println("<p>Paste the URL of an OWL-RDF ontology into the box below, select a format and hit return or press the Convert button. The servlet will then attempt to convert the ontology to the selected format. ");

	out.println("</p><p>No guarantees are provided as to the correctness of this converter.</p><p>Developed by Sean Bechhofer of the University of Manchester and Raphael Volz of the University of Karlsruhe.</p>"); 
	out.println("<hr/>");
    }
    
    protected void printForm( PrintWriter out ) {
	out.println("<form action=\"Converter\" method=\"get\">");
	out.println(" <table>");
	out.println("  <tr>");
	out.println("  <td><b>URL:</b></td>");
	out.println("  <td><input type=\"text\" size=\"80\" name=\"url\" value=\"\"/></td>");
	out.println("  <td><input type=\"submit\" value=\" Convert \"/></td>");
	out.println("  </tr>");
	out.println(" </table>");
	out.println(" <table>");
	out.println("  <tr>");
	out.println("   <td><input type=\"radio\" name=\"format\" value=\"abstract\" checked=\"checked\"/></td>");
	out.println("   <td><b>Abstract Syntax</b></td>");
	out.println("   </tr><tr>");
	
	out.println("   <td><input type=\"radio\" name=\"format\" value=\"fact\"/></td>");
	out.println("   <td><b>FaCT</b>. Produces output suitable for the FaCT reasoner. Note that this includes a special encoding for individuals which allows FaCT to use the \"cheating semantics\" for enumerations. Domain and range restrictions are also translated to general axioms.</td>");
	out.println("   </tr><tr>");
	
	out.println("   <td><input type=\"radio\" name=\"format\" value=\"factplus\"/></td>");
	out.println("   <td><b>FaCT++</b>. Output suitable for the new implementation of FaCT.</td>");
	out.println("   </tr><tr>");
	
	out.println("   <td><input type=\"radio\" name=\"format\" value=\"dig2.0\"/></td>");
	out.println("   <td><b>DIG 2.0</b>. <em>Experimental</em> output in DIG 2.0 XML.</td>");
	out.println("   </tr><tr>");
	
	out.println("   <td><input type=\"radio\" name=\"format\" value=\"tptp\"/></td>");
	out.println("   <td><b>TPTP</b>. Output suitable for FO theorem provers.</td>");
// 	out.println("   </tr><tr>");
	
// 	out.println("   <td><input type=\"radio\" name=\"format\" value=\"rdf\"/></td>");
// 	out.println("   <td>RDF</td>");

	out.println("   </tr>");
	
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
	out.println("<td><span style='font-size:xx-small;'>OWL Converter running on "
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

    protected String render( URI uri, Renderer renderer ) {
	try {
	    OWLConnection connection = OWLManager.getOWLConnection();

	    OWLRDFParser parser = new OWLRDFParser();
	    parser.setConnection( connection );

	    OWLOntology onto = 
		parser.parseOntology(uri);
	    Writer writer = new StringWriter();
	    renderer.renderOntology( onto, writer );
	    return( writer.toString() );
	} catch (Exception ex) {
	    return ( "Error: " + ex.getMessage() );
	}
    }

} // ConverterServlet



/*
 * ChangeLog
 * $Log: ConverterServlet.java,v $
 * Revision 1.3  2006/03/20 13:16:12  sean_bechhofer
 * Adding DIG2.0 to converter
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
 * Revision 1.4  2003/10/02 09:37:23  bechhofers
 * Cleaning up access to Connection. Addition of Servlet Test validator.
 *
 * Revision 1.3  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.2  2003/06/06 14:28:57  seanb
 * Adding new rendering
 *
 * Revision 1.1  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 *
 */
