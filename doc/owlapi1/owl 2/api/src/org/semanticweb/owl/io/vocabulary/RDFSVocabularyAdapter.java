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

/**
 * Vocabulary for the current RDFS specification.
 *
 * @author Raphael Volz (volz@aifb.uni-karlsruhe.de)
 * @author Boris Motik (boris.motik@fzi.de)
 */
public class RDFSVocabularyAdapter extends RDFVocabularyAdapter {
    /** An instance of this class. */
    public static final RDFSVocabularyAdapter INSTANCE=new RDFSVocabularyAdapter();
    /** Namespace for the RDFS. */
    public static final String RDFS="http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Creates an instance of this class.
     */
	protected RDFSVocabularyAdapter() {
	}
    /**
     * Returns the name of the 'Resource' resource.
     */
    public String getResource() {
        return RDFS+"Resource";
    }
    /**
     * Returns the name of the 'Literal' resource.
     */
    public String getLiteral() {
        return RDFS+"Literal";
    }
    /**
     * Returns the name of the 'ContraintResource' resource.
     */
    public String getConstraintResource() {
        return RDFS+"ConstraintResource";
    }
    /**
     * Returns the name of the 'ConstraintProperty' resource.
     */
    public String getContraintProperty() {
        return RDFS+"ConstraintProperty";
    }
    /**
     * Returns the name of the 'Datatype' property.
     */
    public String getDatatype() {
        return RDFS+"Datatype";
    }
    /**
     * Returns the name of the 'domain' property.
     */
    public String getDomain() {
        return RDFS+"domain";
    }
    /**
     * Returns the name of the 'range' property.
     */
    public String getRange() {
        return RDFS+"range";
    }
    /**
     * Returns the name of the 'label' property.
     */
    public String getLabel() {
        return RDFS+"label";
    }
    /**
     * Returns the name of the 'comment' property
     */
    public String getComment() {
        return RDFS+"comment";
    }
    /**
     * Returns the name of the 'seeAlso' property
     */
    public String getSeeAlso() {
        return RDFS+"seeAlso";
    }
    /**
     * Returns the name of the 'isDefinedBy' property
     */
    public String getIsDefinedBy() {
        return RDFS+"isDefinedBy";
    }
    /**
     * Returns the name of the 'subClassOf' property.
     */
    public String getSubClassOf() {
        return RDFS+"subClassOf";
    }
    /**
     * Returns the name of the 'subPropertyOf' property.
     */
    public String getSubPropertyOf() {
        return RDFS+"subPropertyOf";
    }
    /**
     * Returns the name of the 'Concept' resource.
     */
    public String getClass_() {
        return RDFS+"Class";
    }
}
