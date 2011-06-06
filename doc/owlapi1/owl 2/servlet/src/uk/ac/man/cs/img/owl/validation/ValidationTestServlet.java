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
 * Filename           $RCSfile: ValidationTestServlet.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:19 $
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
/**
 * Basic Validation Servlet. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: ValidationTestServlet.java,v 1.1.1.1 2003/10/14 17:10:19 sean_bechhofer Exp $
 */

public class ValidationTestServlet extends HttpServlet 
{

    private OWLConnection connection = null;
    private OWLTestRunner runner = null;

    public ValidationTestServlet() throws ServletException 
    {
	BasicConfigurator.configure();
	System.setProperty("org.semanticweb.owl.util.OWLConnection",
			   "org.semanticweb.owl.impl.model.OWLConnectionImpl");
	runner = new OWLTestRunner();
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
	String url = request.getParameter("url");
	if ( url!= null && 
	     !url.equals("")) {
	    try {
		URI uri = new URI( url.trim() );
		out.println("<div class='box'>");
		runner.runTest( uri, out );
		out.println("</div>");
		out.println("<form action=\"TestValidator\" method=\"get\">");
		out.println(" <table>");
		out.println("  <tr>");
		out.println("  <td><b>URL:</b></td>");
		out.println("  <td><input type=\"text\" size=\"80\" name=\"url\" value=\"" + uri + "\"/></td>");
		out.println("  <td><input type=\"submit\" value=\" Validate \"/></td>");
		out.println("  </tr>");
		out.println(" </table>");
		out.println("</form>");
	    } catch ( Exception e ) {
		out.println( "Server Exception: " + e.getMessage() );
		printForm( out );
	    } catch ( Throwable e ) {
		out.println( "Server Exception: " + e.getMessage() );
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
	out.println("<title>OWL Validation Test Harness</title>"); 
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
	out.println("<h1>OWL Validation Test Harness</h1>");
	out.println("<p>Paste the URL of an OWL test manifest box below, hit return or press the Validate button. The servlet will then attempt to validate the ontologies named in the manifest.");
	out.println("</p><p>No guarantees are provided as to the correctness of this software.</p>"); 
	out.println("<hr/>");
    }
    
    protected void printForm( PrintWriter out ) {
	out.println("<form action=\"TestValidator\" method=\"get\">");
	out.println("<table>");
	out.println("<tr><td>");
	out.println(" <table>");
  	out.println("  <tr>");
	out.println("  <td><b>URL:</b></td>");
	out.println("  <td><input type=\"text\" size=\"80\" name=\"url\"/></td>");
	out.println("  <td><input type=\"submit\" value=\" Validate \"/></td>");
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

} // ValidationTestServlet



/*
 * ChangeLog
 * $Log: ValidationTestServlet.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/10/02 09:37:23  bechhofers
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
