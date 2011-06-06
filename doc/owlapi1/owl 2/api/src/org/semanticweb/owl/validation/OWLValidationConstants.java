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
 * Filename           $RCSfile: OWLValidationConstants.java,v $
 * Revision           $Revision: 1.4 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/06/22 13:57:38 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.validation; // Generated package name


/**
 * Constant values used for validation. These include basic constants
 * that represent various language species, along with a number of
 * constants to represent the expressiveness used in an ontology.
 *
 * Created: Fri May 16 12:48:58 2003
 *
 * @author Sean Bechhofer
 * @version $Id: OWLValidationConstants.java,v 1.4 2004/06/22 13:57:38 sean_bechhofer Exp $
 */

public interface OWLValidationConstants 
{
    /* Values chosen here so that:
       LITE | LITE = LITE
       LITE | DL = DL
       LITE | FULL = FULL
       DL | DL = DL
       DL | FULL = FULL
    */

    /**
     * Constant representing OWL-Lite
     *
     */
    public static final int LITE = 0;  /* 0000 */
    /**
     * Constant representing OWL-DL.
     *
     */
    public static final int DL = 1;    /* 0001 */
    /**
     * Constant representing OWL-Full
     *
     */
    public static final int FULL = 3;  /* 0011 */


    /* A number of constants that represent the various constructs in
     * the language. These can then be used to determine whether
     * particular applications (such as reasoners) are able to deal
     * with ontologies */

    /* We don't know why this has happened */ 
    public static final int UNKNOWN = 1001;

    /* Class forming expressions */
    /** Class Intersection */
    public static final int INTERSECTION =  1002;
    /** Class Union. */
    public static final int UNION =  1003;
    /** Class Negation. */
    public static final int COMPLEMENT =  1004;
    /** Cardinality constraint of 0 or 1. */
    public static final int ZEROONECARDINALITY =  1005;
    /** Cardinality constraint > 1. */
    public static final int CARDINALITY =  1006;
    /** OneOf or Enumeration. Also signifies the use of an object hasValue. */
    public static final int ONEOF =  1007;
    /* HASVALUE considered the same as ONEOF */
    /** Datatype declared. */
    public static final int DATATYPE =  1008;
    /** construct. */
    public static final int DATARANGE =  1009;

    /* Class axioms */
    /** Subclass Axiom. */
    public static final int SUBCLASS =  1010;
    /** Equivalence Axiom. */
    public static final int EQUIVALENCE =  1011;
    /** Disjoint Axiom. */
    public static final int DISJOINT =  1012;
    /** Partial class definition. */
    public static final int PARTIAL =  1013;
    /** Complete class definition. */
    public static final int COMPLETE =  1014;

    /* Property axioms and assertions */
    /** Subproperty Axiom. */
    public static final int SUBPROPERTY =  1015;
    /** EquivalentProperty Axiom. */
    public static final int EQUIVALENTPROPERTY =  1016;
    /** Inverse Property. */
    public static final int INVERSE =  1017;
    /** Transitive Property. */
    public static final int TRANSITIVE =  1018;
    /** Symmetric Property. */
    public static final int SYMMETRIC =  1019;
    /** Functional Property. */
    public static final int FUNCTIONAL =  1020;
    /** Inverse Functional Property. */
    public static final int INVERSEFUNCTIONAL =  1021;

    /* Assertions relating to individuals */
    /** Individuals are present. */
    public static final int INDIVIDUALS =           1022;
    /** Individuals are related. */
    public static final int RELATEDINDIVIDUALS =    1023;
    /** Individuals have data attributes. */
    public static final int INDIVIDUALDATA =        1024;
    /** Same Individual Axiom. */
    public static final int SAMEINDIVIDUAL =        1025;
    /** Different Individuals Axiom. */
    public static final int DIFFERENTINDIVIDUAL =  1026;
    
    /* Stuff that pushes you out of DL (but isn't necessarily RDF errors. */
    /** Namespace separation is violated. */
    public static final int SEPARATIONVIOLATION =  1027;
    /** Individual given with no type. */
    public static final int UNTYPEDINDIVIDUAL =  1028;
    /** Complex property asserted as transitive. */
    public static final int COMPLEXTRANSITIVE =  1029;
    /** Redefinition of built in vocabulary. */
    public static final int BUILTINREDEFINITION =  1030;
    /** Objects asserted within OWL namespace. */
    public static final int OWLNAMESPACEUSED =  1031;

    /* Stuff that pushes you out of Lite but not DL */
    /** Expressions used in an axiom. */
    public static final int EXPRESSIONINAXIOM = 1032;
    /** Expressions used in a restriction. */
    public static final int EXPRESSIONINRESTRICTION = 1033;

    /* Restriction types */
    /** Some used . */
    public static final int SOME =  1034;
    /** All used. */
    public static final int ALL =  1035;
    
} // OWLValidationConstants

/*
 * ChangeLog
 * $Log: OWLValidationConstants.java,v $
 * Revision 1.4  2004/06/22 13:57:38  sean_bechhofer
 * Fixing problems with validation/expressivity checking code.
 *
 * Revision 1.3  2004/03/30 17:46:37  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.2  2004/03/05 17:34:48  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.1  2003/06/19 13:32:37  seanb
 * Addition of construct checking. Change to parser to do imports properly.
 *
 * Revision 1.1  2003/05/19 12:06:01  seanb
 * Constants for species levels.
 *
 */
