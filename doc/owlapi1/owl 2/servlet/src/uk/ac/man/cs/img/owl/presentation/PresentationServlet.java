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
 * Filename           $RCSfile: PresentationServlet.java,v $
 * Revision           $Revision: 1.5 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/10/19 17:33:07 $
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
import org.semanticweb.owl.model.OWLNamedObject;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URLEncoder;
import org.apache.log4j.BasicConfigurator;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.semanticweb.owl.io.Renderer;
import uk.ac.man.cs.img.owl.io.html.OntologyRenderer;
import uk.ac.man.cs.img.owl.io.html.Linker;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Basic Servlet that provides a "javadoc-like" view on an ontology. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: PresentationServlet.java,v 1.5 2004/10/19 17:33:07 sean_bechhofer Exp $
 */

public class PresentationServlet extends HttpServlet 
{

    public PresentationServlet()
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
	response.setContentType("text/html");
	PrintWriter out = new PrintWriter(response.getWriter());
	
	OWLOntology ontology = null; 
	try {
	    if (request.getParameter("style") != null ) {
		/* simply write style information */
		response.setContentType("text/plain");
		out.println("body {");
		out.println("font-family: arial;");
		out.println("background-color: #EEEEEE;");
		out.println("margin: 10 10 10 10;");
		out.println("}");
		out.println("a {");
		out.println("text-decoration:none;");
		out.println("font-weight:bold;");
		out.println("color: #00c;");
		out.println("}");
		out.println("a.visited {");
		out.println("color: #00c;");
		out.println("}");
		out.println("a:hover {");
		out.println("background-color: #aaa;");
		out.println("color: #fff;");
		out.println("}");
		out.println(".yes {");
		out.println("font-weight: bold;");
		out.println("color: green;");
		out.println("}");
		out.println(".no {");
		out.println("font-weight: bold;");
		out.println("color: red;");
		out.println("}");
		out.println(".collection {");
		out.println("}");
		out.println(".item {");
		out.println("display: block;"); 
		out.println("}");
		out.println(".keyword {");
		out.println("color: #060;");
		out.println("}");
		out.println(".object {");
		out.println("}");
		out.println(".faint {");
		out.println("background-color: #FFFFFF;");
		out.println("color: #AAAAAA;");
		out.println("}");
		out.println(".box {");
		out.println("padding: 5px 5px 5px 5px;"); 
		out.println("border:1px solid #888;"); 
		out.println("background-color: #FFFFFF;");
		out.println("}");
	    } else if (request.getParameter("url") != null) {
		/* If the parameter sets the url then associate an
		 * ontology with the current session. */
 		HttpSession session = request.getSession(true);

		try {
		    
		    String url = request.getParameter("url");
		    URI uri = new URI( url.trim() );
		    
		    OWLConnection connection = 
			OWLManager.getOWLConnection();

		    OWLRDFParser parser = new OWLRDFParser();
		    parser.setConnection( connection );
		    
		    ontology = 
			parser.parseOntology(uri);
		    session.putValue( "ontology", ontology );		    
		    OntologyRenderer renderer = 
			new OntologyRenderer( ontology );
		    session.putValue( "renderer", renderer );
		    printFrame( ontology, out );
		    
		} catch (Exception ex) {
		    out.println( "Error: " + ex.getMessage() );
		}
	    } else if (request.getParameter("panel") != null) {
		HttpSession session = request.getSession(true);
		ontology = (OWLOntology) session.getValue( "ontology" );
		final OntologyRenderer renderer = (OntologyRenderer) session.getValue( "renderer" );

		
		String panel = request.getParameter("panel");
		if ( panel.equals("overview") ) {
		    printHeader( out );
		    out.println("<h1>Overview</h1>");
		    out.println("<div class='box'>");
		    out.println( "<div class='collection'>" ); 
		    out.println("<a class='item' href=\"Presentation?panel=object\" target=\"right\">Ontology</a>");
		    out.println("<a class='item' href=\"Presentation?panel=list&things=classes\" target=\"bottomleft\">Classes</a>");
		    out.println("<a class='item' href=\"Presentation?panel=list&things=objectProperties\" target=\"bottomleft\">Object Properties</a>");
		    out.println("<a class='item' href=\"Presentation?panel=list&things=dataProperties\" target=\"bottomleft\">Data Properties</a>");
		    out.println("<a class='item' href=\"Presentation?panel=list&things=annotationProperties\" target=\"bottomleft\">Annotation Properties</a>");
		    out.println("<a href=\"Presentation?panel=list&things=individuals\" target=\"bottomleft\">Individuals</a>");
		    out.println("</div>");
		    out.println("</div>");
		    printFooter( out );
		} else if ( panel.equals("list") ) {
		    printHeader( out );
		    String things = request.getParameter("things");
		    out.println("<div class='box'>");
		    Linker linker = new Linker() {
			    public String linkFor( OWLNamedObject ono ) throws OWLException {
				return "<a class='object' href=\"Presentation?panel=object&object=" + URLEncoder.encode(ono.getURI().toString()) + "\" target=\"right\">" + renderer.shortForm( ono.getURI() ) + "</a>";
			    }
			};
		    
		    if ( things == null || things.equals("classes") ) {
			out.println("<h1>Classes</h1>");
			renderer.listClasses( out, linker );
		    } else if ( request.getParameter("things").equals("objectProperties") ) {
			out.println("<h1>Object Properties</h1>");
			renderer.listObjectProperties( out, linker );
		    } else if ( request.getParameter("things").equals("dataProperties") ) {
			out.println("<h1>Data Properties</h1>");
			renderer.listDataProperties( out, linker );
		    } else if ( request.getParameter("things").equals("annotationProperties") ) {
			out.println("<h1>Annotation Properties</h1>");
			renderer.listAnnotationProperties( out, linker );
		    } else if ( request.getParameter("things").equals("individuals") ) {
			out.println("<h1>Individuals</h1>");
			renderer.listIndividuals( out, linker );
		    }
		    out.println("</div>");
		    
		    printFooter( out );
		} else if ( panel.equals("object") ) {
		    printHeader( out );
		    try {
			if (request.getParameter("object") == null ) {
			    out.println("<div class='box'>");
			    out.println("<h1>Ontology: &lt;" + ontology.getURI() + "&gt;</h1>" );
			    out.println("<h2>Imports</h2>");
			    for (Iterator it = ontology.getIncludedOntologies().iterator();
				 it.hasNext(); ) {
				OWLOntology imported = (OWLOntology) it.next();
				out.println("<a target=\"_top\" href=\"Presentation?url=" + 
					    imported.getURI() + "\">&lt;" + imported.getURI() + "&gt;</a><br>");
			    }
			    renderer.writeShortNames( out );
			    out.println("</div>");
			} else {
			    String uri = request.getParameter("object");
			    out.println("<div class='box'>");
			    Linker linker = new Linker() {
				    public String linkFor( OWLNamedObject ono ) throws OWLException {
					return "<a class='object' href=\"Presentation?panel=object&object=" + URLEncoder.encode(ono.getURI().toString()) + "\" target=\"right\">" + renderer.shortForm( ono.getURI() ) + "</a>";
				    }
				};
			    
			    renderer.renderURI( new URI( uri ), out, linker );
			    out.println("</div>");
			}
		    } catch (URISyntaxException ex) {
		    }
		    printFooter( out );
		}
	    } else {
		printHeader( out );
		out.println("<h1>OWL Ontology HTML Presentation</h1>");
		out.println("<p>Paste the URL of an OWL-RDF ontology into the box below and hit return. The servlet will then return an HTML rendering of the ontology. Clicking on classes or properties will take you to the definition of the given object. Note that the rendering provides a view of the <em>local</em> assertions in the given ontology. Any assertions, definitions, axioms etc. from imported ontologies will <strong>not</strong> be shown. Classes or properties that are used from imported ontologies are, however, shown.<p>");
		out.println("<p>Or try the <a href=\"Presentation?url=http://www.w3.org/2001/sw/WebOnt/guide-src/wine\">OWL Guide ontology</a></p>");
		out.println("<hr/>");
		out.println("<form action=\"Presentation\" method=\"put\">");
		out.println("<table>");
		out.println("<tr>");
		out.println("<td><b>URL:</b></td>");
		out.println("<td>");
		out.println("<input type=\"text\" size=\"80\" name=\"url\"/></td>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</form>");
		printFooter( out );
	    }
	} catch ( OWLException e ) {
	    out.println( "Error: " + e.getMessage() );
	}
	
    }

    protected void printHeader( PrintWriter out ) {
	out.println("<html>");
	out.println("<head>");
	out.println("<link REL=\"stylesheet\" TYPE=\"text/css\" HREF=\"Presentation?style\">");
	out.println("</head>");
	out.println("<body>");
    }

    protected void printFooter( PrintWriter out ) {
	out.println("</body>");
	out.println("</html>");
    }

    protected void printFrame( OWLOntology ontology, 
			       PrintWriter out ) throws OWLException {
	out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	out.println("<!DOCTYPE html");
	out.println("     PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
	out.println("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
	out.println("<html lang=\"en\">");
	
	out.println("<head>");
	out.println("<title>" + ontology.getURI() + "</title>");
	out.println("</head>");
	out.println("<frameset cols=\"20%,80%\">");
	out.println("<frameset rows=\"30%,70%\">");
	out.println("<frame src=\"Presentation?panel=overview\" name=\"topleft\">");
	out.println("<frame src=\"Presentation?panel=list\" name=\"bottomleft\">");
	out.println("</frameset>");
	out.println("<frame src=\"Presentation?panel=object\" name=\"right\">");
	out.println("</frameset>");
	out.println("</html>");
     }

} // PresentationServlet



/*
 * ChangeLog
 * $Log: PresentationServlet.java,v $
 * Revision 1.5  2004/10/19 17:33:07  sean_bechhofer
 * Cosmetic changes
 *
 * Revision 1.4  2004/07/09 12:07:48  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.3  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.2  2003/12/02 10:05:02  sean_bechhofer
 * Cosmetic changes
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/10/02 09:37:23  bechhofers
 * Cleaning up access to Connection. Addition of Servlet Test validator.
 *
 * Revision 1.4  2003/09/22 13:21:44  bechhofers
 * Fixes to rendering and some extra helper functions.
 *
 * Revision 1.3  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.2  2003/07/08 18:01:07  bechhofers
 * Presentation Servlet and additional documentation regarding inference.
 *
 * Revision 1.1  2003/07/05 16:58:14  bechhofers
 * Adding an HTML servlet-based renderer and some changes to the
 * inferencing classes.
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
