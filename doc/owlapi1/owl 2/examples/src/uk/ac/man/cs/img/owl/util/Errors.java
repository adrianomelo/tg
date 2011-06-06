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
 * Filename           $RCSfile: Errors.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/03/30 17:46:37 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.util;

import org.semanticweb.owl.io.owl_rdf.OWLRDFErrorConstants;
import org.semanticweb.owl.validation.OWLValidationConstants;

/** A basic class for an OWL ontology processor. The processor
    will parse ontologies, and spit out alternative renderings of the
    ontologies. Command synopsis is as follows:
    
    <pre>
    java uk.ac.man.cs.img.owl.util.Processor [options] input [output]
    </pre>

    where the options are:
    <pre>
    --abstract -a Produce OWL Abstract Syntax
    --tptp -t Produce TPTP
    --warn -w Report warnings & errors
    --renderer=className -r Use the given class for output.
    --impl=className -i Use the given class as the default implementation of OWLConnection.
    --validate[=lite|dl] -v Validate. If no level is given, validates as DL.
    --help -? Print help message
    </pre>

    The <CODE>input</CODE> should be a URL. If output is
    requested (<CODE></CODE> flags), then output will be written to
    <CODE>output</CODE> if present, stdout by default.

<pre>
$Id: Errors.java,v 1.1 2004/03/30 17:46:37 sean_bechhofer Exp $
</pre>

@author Sean K. Bechhofer
    */

public class Errors implements OWLValidationConstants {
    
    /** An array of all the possible error conditions that might occur */
    public static int[] allCodes = {
	OWLRDFErrorConstants.OTHER, 
	OWLRDFErrorConstants.UNTYPED_CLASS, 
	OWLRDFErrorConstants.UNTYPED_PROPERTY, 
	OWLRDFErrorConstants.UNTYPED_INDIVIDUAL, 
	OWLRDFErrorConstants.UNTYPED_ONTOLOGY, 
	OWLRDFErrorConstants.UNTYPED_DATATYPE, 
	OWLRDFErrorConstants.UNTYPED_URI, 
	OWLRDFErrorConstants.MALFORMED_LIST, 
	OWLRDFErrorConstants.INVERSE_FUNCTIONAL_DATA_PROPERTY, 
	OWLRDFErrorConstants.UNSPECIFIED_FUNCTIONAL_PROPERTY, 
	OWLRDFErrorConstants.STRUCTURE_SHARING, 
	OWLRDFErrorConstants.CYCLICAL_BNODES, 
	OWLRDFErrorConstants.MULTIPLE_DEFINITIONS, 
	OWLRDFErrorConstants.MALFORMED_RESTRICTION, 
	OWLRDFErrorConstants.MALFORMED_DESCRIPTION, 
	OWLRDFErrorConstants.UNUSED_TRIPLES, 
	OWLRDFErrorConstants.ILLEGAL_SUBPROPERTY, 
	OWLRDFErrorConstants.MALFORMED_IMPORT, 
	OWLRDFErrorConstants.RDF_PROPERTY, 
	OWLRDFErrorConstants.RDF_CLASS, 
	OWLRDFErrorConstants.MALFORMED_ALLDIFFERENT, 
	OWLRDFErrorConstants.ANONYMOUS_CLASS_CREATION, 
	OWLRDFErrorConstants.XML_PROBLEM, 
	OWLRDFErrorConstants.IO_ERROR, 
	OWLRDFErrorConstants.INVALID_URL, 
	OWLRDFErrorConstants.URL_IO_ERROR, 
	OWLRDFErrorConstants.UNTYPED_PROPERTY_DATA, 
	OWLRDFErrorConstants.UNTYPED_PROPERTY_OBJECT, 
	OWLRDFErrorConstants.SAME_AS_USED_FOR_CLASS, 
	OWLRDFErrorConstants.SAME_AS_USED_FOR_OBJECT_PROPERTY, 
	OWLRDFErrorConstants.SAME_AS_USED_FOR_DATA_PROPERTY, 
	UNKNOWN,
	INTERSECTION,
	UNION,
	COMPLEMENT,
	ZEROONECARDINALITY,
	CARDINALITY,
	ONEOF,
	DATATYPE,
	DATARANGE,
	SUBCLASS,
	EQUIVALENCE,
	DISJOINT,
	PARTIAL,
	COMPLETE,
	SUBPROPERTY,
	EQUIVALENTPROPERTY,
	INVERSE,
	TRANSITIVE,
	SYMMETRIC,
	FUNCTIONAL,
	INVERSEFUNCTIONAL,
	INDIVIDUALS,
	RELATEDINDIVIDUALS,
	INDIVIDUALDATA,
	SAMEINDIVIDUAL,
	DIFFERENTINDIVIDUAL,
	SEPARATIONVIOLATION,
	UNTYPEDINDIVIDUAL,
	COMPLEXTRANSITIVE,
	BUILTINREDEFINITION,
	OWLNAMESPACEUSED,
	EXPRESSIONINAXIOM,
	EXPRESSIONINRESTRICTION,
    };

    public static String pretty ( int l ) {
	if ( l == OWLRDFErrorConstants.OTHER ) {
	    return("OTHER");
	}
	if ( l == OWLRDFErrorConstants.UNTYPED_CLASS ) {
	    return("Untyped Class");
	}
	if ( l == OWLRDFErrorConstants.UNTYPED_PROPERTY ) {
	    return("Untyped Property");
	}
	if ( l == OWLRDFErrorConstants.UNTYPED_INDIVIDUAL ) {
	    return("Untyped Individual");
	}
	if ( l == OWLRDFErrorConstants.UNTYPED_ONTOLOGY ) {
	    return("Untyped Ontology");
	}
	if ( l == OWLRDFErrorConstants.UNTYPED_DATATYPE ) {
	    return("Untyped Datatype");
	}
	if ( l == OWLRDFErrorConstants.UNTYPED_URI ) {
	    return("Untyped URI");
	}
	if ( l == OWLRDFErrorConstants.MALFORMED_LIST ) {
	    return("Malformed List");
	}
	if ( l == OWLRDFErrorConstants.INVERSE_FUNCTIONAL_DATA_PROPERTY ) {
	    return("Inverse Functional Data Property");
	}
	if ( l == OWLRDFErrorConstants.UNSPECIFIED_FUNCTIONAL_PROPERTY ) {	
	    return("Unspecified Functional Property");
	}
	if ( l == OWLRDFErrorConstants.STRUCTURE_SHARING ) {
	    return("Structure Sharing");
	}
	if ( l == OWLRDFErrorConstants.CYCLICAL_BNODES ) {
	    return("Cyclical BNodes");
	}
	if ( l == OWLRDFErrorConstants.MULTIPLE_DEFINITIONS ) {
	    return("Multiple Definitions");
	}
	if ( l == OWLRDFErrorConstants.MALFORMED_RESTRICTION ) {
	    return("Malformed Restriction");
	}
	if ( l == OWLRDFErrorConstants.MALFORMED_DESCRIPTION ) {
	    return("Malformed Description");
	}
	if ( l == OWLRDFErrorConstants.UNUSED_TRIPLES ) {
	    return("Unused Triples");
	}
	if ( l == OWLRDFErrorConstants.ILLEGAL_SUBPROPERTY ) {
	    return("Illegal Sub Property");
	}
	if ( l == OWLRDFErrorConstants.MALFORMED_IMPORT ) { 
	    return("Malformed Import");
	} 
	if ( l == OWLRDFErrorConstants.RDF_PROPERTY ) { 
	    return("RDF Property Used");
	} 
	if ( l == OWLRDFErrorConstants.RDF_CLASS ) { 
	    return("RDF Class Used");
	} 
	if ( l == OWLRDFErrorConstants.MALFORMED_ALLDIFFERENT ) { 
	    return("Malformed AllDifferent");
	} 
	if ( l == OWLRDFErrorConstants.ANONYMOUS_CLASS_CREATION ) { 
	    return("Anonymous Class Creation");
	} 
	if ( l == OWLRDFErrorConstants.XML_PROBLEM ) { 
	    return("XML Problem");
	} 
	if ( l == OWLRDFErrorConstants.IO_ERROR ) { 
	    return("IO Error");
	} 
	if ( l == OWLRDFErrorConstants.INVALID_URL) { 
	    return("Invalid URL");
	} 
	if ( l == OWLRDFErrorConstants.URL_IO_ERROR ) { 
	    return("URL IO Error");
	} 
	if ( l == OWLRDFErrorConstants.UNTYPED_PROPERTY_DATA ) {
	    return("Untyped Data Property");
	}
	if ( l == OWLRDFErrorConstants.UNTYPED_PROPERTY_OBJECT ) {
	    return("Untyped Object Property");
	}
	if ( l == OWLRDFErrorConstants.SAME_AS_USED_FOR_CLASS ) {
	    return("SameAs with Class");
	}
	if ( l == OWLRDFErrorConstants.SAME_AS_USED_FOR_OBJECT_PROPERTY ) {
	    return("SameAs with ObjectProperty");
	}
	if ( l == OWLRDFErrorConstants.SAME_AS_USED_FOR_DATA_PROPERTY ) {
	    return("SameAs with DatatypeProperty");
	}
	if ( l == UNKNOWN ) { 
	    return("UNKNOWN");
	} 
	if ( l == INTERSECTION ) { 
	    return("And Used");
	} 
	if ( l == UNION ) {
	    return("Or Used");
	}
	if ( l == COMPLEMENT ) {
	    return("Not Used");
	}
	if ( l == ZEROONECARDINALITY ) {
	    return("0/1 Cardinality Used");
	}
	if ( l == CARDINALITY ) {
	    return("Cardinality > 1 Used");
	}
	if ( l == ONEOF ) {
	    return("Enumeration Used");
	}
	if ( l == DATATYPE ) {
	    return("Datatype Used");
	}
	if ( l == DATARANGE ) {
	    return("DataRange Used");
	}
	if ( l == SUBCLASS ) {
	    return("SubClass Used");
	}
	if ( l == EQUIVALENCE ) {
	    return("Equivalence Used");
	}
	if ( l == DISJOINT ) {
	    return("Disjoint Used");
	}
	if ( l == PARTIAL ) {
	    return("Partial Used");
	}
	if ( l == COMPLETE ) {
	    return("Complete Used");
	}
	if ( l == SUBPROPERTY ) {
	    return("SubProperty Used");
	}
	if ( l == EQUIVALENTPROPERTY ) {
	    return("EquivalentProperty Used");
	}
	if ( l == INVERSE ) {
	    return("Inverse Used");
	}
	if ( l == TRANSITIVE ) {
	    return("Transitive Property");
	}
	if ( l == SYMMETRIC ) {
	    return("Symmetric Property");
	}
	if ( l == FUNCTIONAL ) {
	    return("Functional Property");
	}
	if ( l == INVERSEFUNCTIONAL ) {
	    return("InverseFunctional Property");
	}
	if ( l == INDIVIDUALS ) {
	    return("Individuals Used");
	}
	if ( l == RELATEDINDIVIDUALS ) {
	    return("Related individuals Used");
	}
	if ( l == INDIVIDUALDATA ) {
	    return("Individual data Used");
	}
	if ( l == SAMEINDIVIDUAL ) {
	    return("SameIndividual Used");
	}
	if ( l ==  DIFFERENTINDIVIDUAL ) {
	    return("DifferentIndividual Used");
	}
	if ( l == SEPARATIONVIOLATION ) {
	    return("Namespace Separation Violated");
	}
	if ( l == UNTYPEDINDIVIDUAL ) {
	    return("Untyped Individual2");
	}
	if ( l == COMPLEXTRANSITIVE ) {
	    return("Complex role declared Transitive");
	}
	if ( l == BUILTINREDEFINITION ) {
	    return("Redefinition of built in vocabulary");
	}
	if ( l == OWLNAMESPACEUSED ) {
	    return("OWL Namespace Used");
	}
	if ( l == EXPRESSIONINAXIOM ) {
	    return("Expression Used in Axiom");
	}
	if ( l == EXPRESSIONINRESTRICTION ) {
	    return("Expression Used in Restriction");
	}
	return Integer.toString( l );
    }

}
