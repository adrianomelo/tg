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
 * Filename           $RCSfile: MalformedOWLConstructRDFException.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:17 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.io.owl_rdf; // Generated package name


/**
 * This exception represents a condition where we have encountered a
 * badly constructed OWL expression. For example, a Restriction has
 * not been given an onProperty. Extends {@link org.xml.sax.SAXException org.xml.sax.SAXException} in order to allow it to be thrown in the Parsers. 
 *
 *
 * Created: Thu Feb 13 14:55:48 2003
 *
 * @author Sean Bechhofer
 * @version $Id: MalformedOWLConstructRDFException.java,v 1.1.1.1 2003/10/14 17:10:17 sean_bechhofer Exp $
 */

public class MalformedOWLConstructRDFException extends org.xml.sax.SAXException 
{

    public MalformedOWLConstructRDFException( String message ) {
	super( message );
    }
    
} // MalformedOWLConstructException



/*
 * ChangeLog
 * $Log: MalformedOWLConstructRDFException.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:17  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/03/20 10:25:48  seanb
 * Moving RDF parser to owl_rdf package
 *
 * Revision 1.2  2003/02/17 18:23:53  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.1  2003/02/13 18:45:49  seanb
 * Improved validation and parsing.
 *
 */
