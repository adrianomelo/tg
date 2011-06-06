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
 * Filename           $RCSfile: OWLRDFErrorHandler.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/03/30 17:46:38 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.io.owl_rdf; 
import org.xml.sax.SAXException;

// Generated package name


/**
 * Error handlers for parsers. Error handlers handle different
 * conditions that may occur when parsing ontologies. Different error
 * handlers allow strict or lax handling of errors. For example, an
 * OWL Lite processor may choose to throw an error as soon as some OWL
 * Full construct is encountered. Alternatively, a species validator
 * may wish to allow errors to pass in order to be able to process the
 * entire ontology and provide more information to the user.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLRDFErrorHandler.java,v 1.2 2004/03/30 17:46:38 sean_bechhofer Exp $
 */

public interface OWLRDFErrorHandler 
{
    /**
     * An OWL Full construct has been encountered.
     * @param code an error code, which should ideally be a constant from {@link OWLRDFErrorConstants OWLRDFErrorConstants}.
     * @param message a <code>String</code> value
     * @exception SAXException if an error occurs
     */
    public void owlFullConstruct( int code, String message ) throws SAXException;

    /**
     * An OWL Full construct has been encountered.
     * @param code an error code, which should ideally be a constant from {@link OWLRDFErrorConstants OWLRDFErrorConstants}.
     * @param message a <code>String</code> value
     * @param obj an object that may relate to the error. 
     * @exception SAXException if an error occurs
     */
    public void owlFullConstruct( int code, String message, Object obj ) throws SAXException;

    /**
     * A parsing error has occurred. 
     *
     * @param message a <code>String</code> value
     * @exception SAXException if an error occurs
     */
    public void error( String message ) throws SAXException;

    /**
     * Some undesirable situation has occurred that merits a warning.
     *
     * @param message a <code>String</code> value
     * @exception SAXException if an error occurs
     */
    public void warning( String message ) throws SAXException;
    
} // OWLRDFErrorHandler



/*
 * ChangeLog
 * $Log: OWLRDFErrorHandler.java,v $
 * Revision 1.2  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:18  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/10/03 10:00:03  bechhofers
 * Refactoring of RDFErrorHandler:
 *  o Addition of error codes for OWL Full situations
 *
 * Revision 1.2  2003/08/28 10:29:04  bechhofers
 * Updating parser to improve validation. Addition of new consumer with
 * simple triple model.
 *
 * Revision 1.1  2003/05/29 09:07:28  seanb
 * Moving RDF error handler
 *
 * Revision 1.3  2003/02/19 10:15:09  seanb
 * Moving validation servlet to separate directory.
 *
 * Revision 1.2  2003/02/17 18:23:53  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.1  2003/02/13 18:45:49  seanb
 * Improved validation and parsing.
 *
 */
