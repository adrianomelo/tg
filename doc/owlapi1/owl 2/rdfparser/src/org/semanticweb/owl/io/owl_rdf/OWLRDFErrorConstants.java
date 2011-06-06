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
 * Filename           $RCSfile: OWLRDFErrorConstants.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/03/30 17:46:38 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.io.owl_rdf; // Generated package name


/**
 * Constants representing the various OWL Full things that can occur
 * during parsing. This allows us to pass more information back to the
 * error handler during the parse, giving the handler the opportunity
 * to patch things up if desired. For example, a parser could choose
 * to ignore errors concerning untyped classes and simply add the
 * required triples.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLRDFErrorConstants.java,v 1.3 2004/03/30 17:46:38 sean_bechhofer Exp $
 */

public interface OWLRDFErrorConstants 
{

    /* These are chosen so that they don't overlap with any of the
     * OWLValidation Constants. Thus we can use a mixture of the two
     * for error reporting. */

    public static final int OTHER = 2001;
    public static final int UNTYPED_CLASS = 2002;
    public static final int UNTYPED_PROPERTY = 2003;
    public static final int UNTYPED_INDIVIDUAL = 2004;
    public static final int UNTYPED_ONTOLOGY = 2005;
    public static final int UNTYPED_DATATYPE = 2006;
    public static final int UNTYPED_URI = 2007;
    public static final int MALFORMED_LIST = 2008;
    public static final int INVERSE_FUNCTIONAL_DATA_PROPERTY = 2009;
    public static final int UNSPECIFIED_FUNCTIONAL_PROPERTY = 2010;
    public static final int STRUCTURE_SHARING = 2011;
    public static final int CYCLICAL_BNODES = 2012;
    public static final int MULTIPLE_DEFINITIONS = 2013;
    public static final int MALFORMED_RESTRICTION = 2014;
    public static final int MALFORMED_DESCRIPTION = 2015;
    public static final int UNUSED_TRIPLES = 2016;
    public static final int ILLEGAL_SUBPROPERTY = 2017;
    public static final int MALFORMED_IMPORT = 2018;
    public static final int RDF_PROPERTY = 2019;
    public static final int RDF_CLASS = 2020;
    public static final int MALFORMED_ALLDIFFERENT = 2021;
    public static final int ANONYMOUS_CLASS_CREATION = 2022;

    /* Indicates that an untyped property was found that's probably a
     * data property */
    public static final int UNTYPED_PROPERTY_DATA = 2023;
    /* Indicates that an untyped property was found that's probably an
     * object property */
    public static final int UNTYPED_PROPERTY_OBJECT = 2024;

    /* Incorrect use of the owl:sameAs */
    public static final int SAME_AS_USED_FOR_CLASS = 2026;
    public static final int SAME_AS_USED_FOR_OBJECT_PROPERTY = 2027;
    public static final int SAME_AS_USED_FOR_DATA_PROPERTY = 2028;

    public static final int XML_PROBLEM = 2030;
    public static final int IO_ERROR = 2031;
    public static final int INVALID_URL = 2032;
    public static final int URL_IO_ERROR = 2033;

} // OWLRDFErrorConstants



/*
 * ChangeLog
 * $Log: OWLRDFErrorConstants.java,v $
 * Revision 1.3  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.2  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:18  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/10/03 10:00:03  bechhofers
 * Refactoring of RDFErrorHandler:
 *  o Addition of error codes for OWL Full situations
 *
 */
