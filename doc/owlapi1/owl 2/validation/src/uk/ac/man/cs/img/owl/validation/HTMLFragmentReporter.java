/*
 * Copyright (C) 2003, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package uk.ac.man.cs.img.owl.validation; 
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import org.semanticweb.owl.model.OWLOntology;
import org.apache.log4j.Logger;
import java.util.Date;
import java.io.PrintWriter;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * A Validator reporter that writes HTML fragments.
 *
 * @author Sean Bechhofer
 * @version $Id: HTMLFragmentReporter.java,v 1.3 2005/06/10 12:20:34 sean_bechhofer Exp $
 */

public class HTMLFragmentReporter implements SpeciesValidatorReporter 
{
    static Logger logger = Logger.getLogger(ValidatorLogger.class);

    private PrintWriter writer;

    public HTMLFragmentReporter( PrintWriter writer )
    {
	this.writer = writer;
    }
    
    public void message( String str ) {
	writer.println("<p>");
	writer.println(str);
	writer.println("</p>");
    }
    
    public void explain( int l, int code, String str ) {
	writer.println("<p>");
	writer.println("<strong>" + SpeciesValidator.level( l ) + "</strong>");
	writer.println(str);
	writer.println("</p>");	
    }

    /* Inform the reporter of the ontology that's being validated */
    public void ontology( OWLOntology ontology ) {
	try {
	    writer.println("<h1>" + ontology.getURI() + "</h1>");
	} catch (OWLException ex) {
	    writer.println("<h1>Unknown URI</h1>");
	}
	    
	writer.println( "<p>" + new Date().toString() + "</p>");
    }

    /* Inform the reporter that validation has finished with the given
     * result */
    public void done( String level ) {
	writer.println( "<p>Validation complete! <b>" + level + "</b></p>");
    }
    
} // ValidatorLogger



/*
 * ChangeLog
 * $Log: HTMLFragmentReporter.java,v $
 * Revision 1.3  2005/06/10 12:20:34  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/05/08 07:54:35  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.1  2003/02/06 16:44:00  seanb
 * Enhancement of validation.
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 */
