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

import org.semanticweb.owl.model.OWLException;

/**
 * VocabularyAdapter
 * 
 * This class defines shortcuts to the RDF names
 * in an ontology language.
 * A particular vocabulary adapter should extend
 * this class and overwrite methods.
 * 
 * @since 04.02.2003
 * @author <a href="mailto:volz@fzi.de">Raphael Volz</a>
 * 
 */
public class VocabularyAdapter {
    /** Global instance of this adapter. */
    public static final VocabularyAdapter 
    INSTANCE=new VocabularyAdapter();

    /**
     * Creates an instance of this class.
     */
    protected VocabularyAdapter() {
    }
//     /**
//      * Returns the name of the 'Resource' resource.
//      */
//     public String getResource() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'Literal' resource.
//      */
//     public String getLiteral() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'ContraintResource' resource.
//      */
//     public String getConstraintResource() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'ConstraintProperty' resource.
//      */
//     public String getContraintProperty() throws  OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'domain' property.
//      */
//     public String getDomain() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'range' property.
//      */
//     public String getRange() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'label' property.
//      */
//     public String getLabel() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'comment' property
//      */
//     public String getComment() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'subClassOf' property.
//      */
//     public String getSubClassOf() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'subPropertyOf' property.
//      */
//     public String getSubPropertyOf() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'Property' resource.
//      */
//     public String getProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'inverse' resource.
//      */
//     public String getInverse() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'symmetric' resource.
//      */
//     public String getSymmetric() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'transitive' resource.
//      */
//     public String getTransitive() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//      * Returns the name of the 'Class' resource. The _ at the end of the name is to avoid conflicts with <code>Object.getClass()</code>.
//      */
//     public String getClass_() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     /**
//     * Returns the name of the 'instanceOf' property.
//     */
//     public String getInstanceOf() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getKAONLabel() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getLanguageURI(String language) throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getInLanguage() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getLanguage() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getLexicalEntry() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getRoot() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getStem() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getDocumentation() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getSynonym() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getReferences() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getValue() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getAllDifferentFrom() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
   
//     public String getDatatypeProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getDeprecatedClass() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getDeprecatedProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getFunctionalProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getInverseFunctionalProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getNothing() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getObjectProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getOntology() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getRestriction() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getSymmetricProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getThing() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getAllValuesFrom() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getBackwardCompatibleWith() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
    
//     public String getCardinality() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getComplementOf() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getDifferentFrom() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getDisjointWith() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getDistinctMembers() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getHasValue() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getImports() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getIncompatibleWith() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getIntersectionOf() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getInverseOf() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getMaxCardinality() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getMinCardinality() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getOneOf() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getOnProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getPriorVersion() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getSameAs() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getEquivalentClass() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
//     public String getSameIndividualAs() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getEquivalentProperty() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getSomeValuesFrom() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getUnionOf() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     }
//     public String getVersionInfo() throws OWLException {
//         throw new OWLException("RDF resource not known");
//     } 
}
