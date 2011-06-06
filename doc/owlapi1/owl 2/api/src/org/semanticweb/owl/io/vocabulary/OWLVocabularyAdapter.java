/*
 * Copyright (C) 2005, University of Karlsruhe
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

package org.semanticweb.owl.io.vocabulary;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLException;

import edu.unika.aifb.rdf.api.util.RDFConstants;

/**
 * Vocabulary of the OWL specification as of
 * 03/02/03. 
 * 
 * @since 04.02.2003
 * @author <a href="mailto:volz@fzi.de">Raphael Volz</a>
 * 
 */
public class OWLVocabularyAdapter extends RDFSVocabularyAdapter {

    public static final OWLVocabularyAdapter INSTANCE =
        new OWLVocabularyAdapter();

    /** Namespace for OWL. */
    public static final String OWL = "http://www.w3.org/2002/07/owl#";

    public String getAllDifferent() {
        return OWL + "AllDifferent";
    }
    public String getAnnotationProperty() {
        return OWL + "AnnotationProperty";
    }
    public String getClass_() {
        return OWL + "Class";
    }
    public String getDatatypeProperty() {
        return OWL + "DatatypeProperty";
    }
    public String getDataRange() {
        return OWL + "DataRange";
    }
    public String getDeprecatedClass() {
        return OWL + "DeprecatedClass";
    }
    public String getDeprecatedProperty() {
        return OWL + "DeprecatedProperty";
    }
    public String getFunctionalProperty() {
        return OWL + "FunctionalProperty";
    }
    public String getInverseFunctionalProperty() {
        return OWL + "InverseFunctionalProperty";
    }
    public String getNothing() {
        return OWL + "Nothing";
    }
    public String getObjectProperty() {
        return OWL + "ObjectProperty";
    }
    public String getOntology() {
        return OWL + "Ontology";
    }
    public String getRestriction() {
        return OWL + "Restriction";
    }
    public String getSymmetricProperty() {
        return OWL + "SymmetricProperty";
    }
    public String getRoot() {
        return getThing();
    }
    public String getThing() {
        return OWL + "Thing";
    }
    public String getAllValuesFrom() {
        return OWL + "allValuesFrom";
    }
    public String getBackwardCompatibleWith() {
        return OWL + "backwardCompatibleWith";
    }

    public String getCardinality() {
        return OWL + "cardinality";
    }
    public String getComplementOf() {
        return OWL + "complementOf";
    }
     public String getDifferentFrom() {
         return OWL + "differentFrom";
     }
    public String getDisjointWith() {
        return OWL + "disjointWith";
    }
    public String getDistinctMembers() {
        return OWL + "distinctMembers";
    }
    public String getHasValue() {
        return OWL + "hasValue";
    }
    public String getImports() {
        return OWL + "imports";
    }
    public String getIncompatibleWith() {
        return OWL + "incompatibleWith";
    }
    public String getIntersectionOf() {
        return OWL + "intersectionOf";
    }
    public String getInverseOf() {
        return OWL + "inverseOf";
    }
    public String getMaxCardinality() {
        return OWL + "maxCardinality";
    }
    public String getMinCardinality() {
        return OWL + "minCardinality";
    }
    public String getOneOf() {
        return OWL + "oneOf";
    }
    public String getOnProperty() {
        return OWL + "onProperty";
    }
    public String getPriorVersion() {
        return OWL + "priorVersion";
    }
    public String getSameAs() {
        return OWL + "sameAs";
    }
    /* Added by SKB */
    /* And removed again :-) 
       http://lists.w3.org/Archives/Public/www-webont-wg/2003Jan/0543.html
    */
//     public String getSameClassAs() {
//         return OWL + "sameClassAs";
//     }
//     public String getSamePropertyAs() {
//         return OWL + "samePropertyAs";
//     }
    /****************/
    public String getEquivalentClass() {
        return OWL + "equivalentClass";
    }
    public String getSameIndividualAs() {
        return OWL + "sameIndividualAs";
    }
    public String getEquivalentProperty() {
        return OWL + "equivalentProperty";
    }
    public String getSomeValuesFrom() {
        return OWL + "someValuesFrom";
    }
    public String getTransitive() {
        return OWL + "TransitiveProperty";
    }
    public String getUnionOf() {
        return OWL + "unionOf";
    }
    public String getVersionInfo() {
        return OWL + "versionInfo";
    }

    HashSet reservedVocabulary = null;

    public Set getReservedVocabulary() {
        if (reservedVocabulary == null) {
            reservedVocabulary = new HashSet();
            reservedVocabulary.add(INSTANCE.getAllValuesFrom());
            reservedVocabulary.add(INSTANCE.getAllDifferent());
            reservedVocabulary.add(INSTANCE.getAnnotationProperty());
            reservedVocabulary.add(INSTANCE.getBackwardCompatibleWith());
            reservedVocabulary.add(INSTANCE.getCardinality());
            reservedVocabulary.add(INSTANCE.getClass_());
            reservedVocabulary.add(INSTANCE.getComment());
            reservedVocabulary.add(INSTANCE.getComplementOf());
            reservedVocabulary.add(INSTANCE.getConstraintResource());
            reservedVocabulary.add(INSTANCE.getContraintProperty());
            reservedVocabulary.add(INSTANCE.getDatatypeProperty());
            reservedVocabulary.add(INSTANCE.getDataRange());
            reservedVocabulary.add(INSTANCE.getDeprecatedClass());
            reservedVocabulary.add(INSTANCE.getDeprecatedProperty());
            reservedVocabulary.add(INSTANCE.getDifferentFrom());
            reservedVocabulary.add(INSTANCE.getDisjointWith());
            reservedVocabulary.add(INSTANCE.getDistinctMembers());
//             try {
//                 reservedVocabulary.add(INSTANCE.getDocumentation());
//             } catch (OWLException e) {
//                 // Shouldn't happen
//             }
            reservedVocabulary.add(INSTANCE.getDomain());
            reservedVocabulary.add(INSTANCE.getEquivalentClass());
            reservedVocabulary.add(INSTANCE.getEquivalentProperty());
            reservedVocabulary.add(INSTANCE.getFunctionalProperty());
            reservedVocabulary.add(INSTANCE.getHasValue());
            reservedVocabulary.add(INSTANCE.getImports());
            reservedVocabulary.add(INSTANCE.getIncompatibleWith());
            reservedVocabulary.add(INSTANCE.getIntersectionOf());
            reservedVocabulary.add(INSTANCE.getInstanceOf());
            reservedVocabulary.add(INSTANCE.getInverseFunctionalProperty());
            reservedVocabulary.add(INSTANCE.getInverseOf());
            reservedVocabulary.add(INSTANCE.getLabel());
            reservedVocabulary.add(INSTANCE.getLiteral());
            reservedVocabulary.add(INSTANCE.getMaxCardinality());
            reservedVocabulary.add(INSTANCE.getMinCardinality());
            reservedVocabulary.add(INSTANCE.getObjectProperty());
            reservedVocabulary.add(INSTANCE.getOneOf());
            reservedVocabulary.add(INSTANCE.getOnProperty());
            reservedVocabulary.add(INSTANCE.getOntology());
            reservedVocabulary.add(INSTANCE.getPriorVersion());
            reservedVocabulary.add(INSTANCE.getProperty());
            reservedVocabulary.add(INSTANCE.getRange());
            reservedVocabulary.add(INSTANCE.getResource());
            reservedVocabulary.add(INSTANCE.getRestriction());
            reservedVocabulary.add(INSTANCE.getSameAs());
//             reservedVocabulary.add(INSTANCE.getSameClassAs());
//             reservedVocabulary.add(INSTANCE.getSamePropertyAs());
            reservedVocabulary.add(INSTANCE.getSameIndividualAs());
            reservedVocabulary.add(INSTANCE.getSomeValuesFrom());
            reservedVocabulary.add(INSTANCE.getSubClassOf());
            reservedVocabulary.add(INSTANCE.getSubPropertyOf());
            reservedVocabulary.add(INSTANCE.getSymmetricProperty());
            reservedVocabulary.add(INSTANCE.getTransitive());
            reservedVocabulary.add(INSTANCE.getUnionOf());
            reservedVocabulary.add(INSTANCE.getVersionInfo());
            reservedVocabulary.add(RDFConstants.RDF_ABOUT);
            reservedVocabulary.add(RDFConstants.RDF_BAG);
            reservedVocabulary.add(RDFConstants.RDF_DESCRIPTION);
            reservedVocabulary.add(RDFConstants.RDF_ID);
            reservedVocabulary.add(RDFConstants.RDF_OBJECT);
            reservedVocabulary.add(RDFConstants.RDF_PARSE_TYPE);
            reservedVocabulary.add(RDFConstants.RDF_PREDICATE);
            reservedVocabulary.add(RDFConstants.RDF_PROPERTY);
            reservedVocabulary.add(RDFConstants.RDF_RDF);
            reservedVocabulary.add(RDFConstants.RDF_RESOURCE);
            reservedVocabulary.add(RDFConstants.RDF_REST);
            reservedVocabulary.add(RDFConstants.RDF_STATEMENT);
            reservedVocabulary.add(RDFConstants.RDF_SUBJECT);
            reservedVocabulary.add(RDFConstants.RDF_TYPE);
            reservedVocabulary.add(RDFConstants.RDF_LIST);
            reservedVocabulary.add(RDFConstants.RDF_FIRST);
            reservedVocabulary.add(RDFConstants.RDF_NIL);
            reservedVocabulary.add(
                XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getDatatypes());
        }
        return reservedVocabulary;
    }

    HashSet annotationProperties = null;

    public Set getAnnotationProperties() {
        if (annotationProperties == null) {
            annotationProperties = new HashSet();
	    annotationProperties.add( INSTANCE.getVersionInfo());
	    annotationProperties.add( INSTANCE.getLabel());
	    annotationProperties.add( INSTANCE.getComment());
	    annotationProperties.add( INSTANCE.getSeeAlso());
	    annotationProperties.add( INSTANCE.getIsDefinedBy());
	}
	return annotationProperties;
    }

    HashSet ontologyProperties = null;

    public Set getOntologyProperties() {
	if (ontologyProperties == null) {
	    ontologyProperties = new HashSet();
	    ontologyProperties.add( INSTANCE.getImports() ) ;
	    ontologyProperties.add( INSTANCE.getPriorVersion() ) ;
	    ontologyProperties.add( INSTANCE.getBackwardCompatibleWith() )  ;
	    ontologyProperties.add( INSTANCE.getIncompatibleWith() );
	}
	return ontologyProperties;
    }

    public static void main(String args[]) {
        OWLVocabularyAdapter a = OWLVocabularyAdapter.INSTANCE;
        System.out.println(
            "// Parser Constants created by OWLVocabularyAdapter");
        System.out.println(
            "final static int CLASS_CODE = " + a.getClass_().hashCode() + ";");
        System.out.println(
            "final static int ALLDIFFERENT_CODE ="
                + a.getAllDifferent().hashCode()
                + ";");
        System.out.println(
            "final static int RESTRICTION_CODE ="
                + a.getRestriction().hashCode()
                + ";");
        System.out.println(
            "final static int OBJECTPROPERTY_CODE ="
                + a.getObjectProperty().hashCode()
                + ";");
        System.out.println(
            "final static int DATATYPEPROPERTY_CODE ="
                + a.getDatatypeProperty().hashCode()
                + ";");
        System.out.println(
            "final static int TRANSITIVEPROPERTY_CODE ="
                + a.getTransitive().hashCode()
                + ";");
        System.out.println(
            "final static int SYMMETRICPROPERTY_CODE ="
                + a.getSymmetricProperty().hashCode()
                + ";");
        System.out.println(
            "final static int FUNCTIONALPROPERTY_CODE ="
                + a.getFunctionalProperty().hashCode()
                + ";");
        System.out.println(
            "final static int INVERSEFUNCTIONALPROPERTY_CODE ="
                + a.getInverseFunctionalProperty().hashCode()
                + ";");
        System.out.println(
            "final static int ONTOLOGY_CODE ="
                + a.getOntology().hashCode()
                + ";");
        System.out.println(
            "final static int DEPRECATEDCLASS_CODE ="
                + a.getDeprecatedClass().hashCode()
                + ";");
        System.out.println(
            "final static int DEPRECATEDPROPERTY_CODE ="
                + a.getDeprecatedProperty().hashCode()
                + ";");

        System.out.println(
            "final static int PROPERTY_CODE ="
                + a.getProperty().hashCode()
                + ";");
        System.out.println(
            "final static int TYPE_CODE ="
                + a.getInstanceOf().hashCode()
                + ";");
        System.out.println(
            "final static int DOMAIN_CODE =" + a.getDomain().hashCode() + ";");
        System.out.println(
            "final static int RANGE_CODE =" + a.getRange().hashCode() + ";");
        System.out.println(
            "final static int SUBCLASSOF_CODE ="
                + a.getSubClassOf().hashCode()
                + ";");
        System.out.println(
            "final static int SUBPROPERTYOF_CODE ="
                + a.getSubPropertyOf().hashCode()
                + ";");
        System.out.println(
            "final static int EQUIVALENTCLASS_CODE ="
                + a.getEquivalentClass().hashCode()
                + ";");
        System.out.println(
            "final static int EQUIVALENTPROPERTY_CODE ="
                + a.getEquivalentProperty().hashCode()
                + ";");
        System.out.println(
            "final static int DISJOINTWITH_CODE ="
                + a.getDisjointWith().hashCode()
                + ";");
        System.out.println(
            "final static int SAMEINDIVIDUALAS_CODE ="
                + a.getSameIndividualAs().hashCode()
                + ";");
        System.out.println(
            "final static int SAMEAS_CODE =" + a.getSameAs().hashCode() + ";");
        System.out.println(
            "final static int DIFFERENTFROM_CODE ="
                + a.getDifferentFrom().hashCode()
                + ";");
        System.out.println(
            "final static int DISTINCTMEMBERS_CODE ="
                + a.getDistinctMembers().hashCode()
                + ";");
        System.out.println(
            "final static int UNIONOF_CODE ="
                + a.getUnionOf().hashCode()
                + ";");
        System.out.println(
            "final static int INTERSECTIONOF_CODE ="
                + a.getIntersectionOf().hashCode()
                + ";");
        System.out.println(
            "final static int COMPLEMENTOF_CODE ="
                + a.getComplementOf().hashCode()
                + ";");
        System.out.println(
            "final static int ONEOF_CODE =" + a.getOneOf().hashCode() + ";");
        System.out.println(
            "final static int ONPROPERTY_CODE ="
                + a.getOnProperty().hashCode()
                + ";");
        System.out.println(
            "final static int ALLVALUESFROM_CODE ="
                + a.getAllValuesFrom().hashCode()
                + ";");
        System.out.println(
            "final static int SOMEVALUESFROM_CODE ="
                + a.getSomeValuesFrom().hashCode()
                + ";");
        System.out.println(
            "final static int HASVALUE_CODE ="
                + a.getHasValue().hashCode()
                + ";");
        System.out.println(
            "final static int MINCARDINALITY_CODE ="
                + a.getMinCardinality().hashCode()
                + ";");
        System.out.println(
            "final static int MAXCARDINALITY_CODE ="
                + a.getMaxCardinality().hashCode()
                + ";");
        System.out.println(
            "final static int CARDINALITY_CODE ="
                + a.getCardinality().hashCode()
                + ";");
        System.out.println(
            "final static int INVERSEOF_CODE ="
                + a.getInverseOf().hashCode()
                + ";");
        System.out.println(
            "final static int IMPORTS_CODE ="
                + a.getImports().hashCode()
                + ";");
        System.out.println(
            "final static int VERSIONINFO_CODE ="
                + a.getVersionInfo().hashCode()
                + ";");
        System.out.println(
            "final static int PRIORVERSION_CODE ="
                + a.getPriorVersion().hashCode()
                + ";");
        System.out.println(
            "final static int BACKWARDCOMPATIBLE_CODE ="
                + a.getBackwardCompatibleWith().hashCode()
                + ";");
        System.out.println(
            "final static int INCOMPATIBLEWITH_CODE ="
                + a.getIncompatibleWith().hashCode()
                + ";");

    }
}
