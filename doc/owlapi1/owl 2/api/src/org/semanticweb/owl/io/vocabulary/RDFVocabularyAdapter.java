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

import edu.unika.aifb.rdf.api.util.RDFConstants;

/**
 * Vocabulary for current RDF things.
 *
 * @author Raphael Volz (volz@aifb.uni-karlsruhe.de)
 * @author Boris Motik (boris.motik@fzi.de)
 */
public class RDFVocabularyAdapter extends VocabularyAdapter {
    /** An instance of this class. */
    public static final RDFVocabularyAdapter INSTANCE=new RDFVocabularyAdapter();
    /** Namespace for the RDF. */
    public static final String RDF="http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * Creates an instance of this class.
     */
    protected RDFVocabularyAdapter() {
    }
    /**
     * Returns the name of the 'Property' resource.
     */
    public String getProperty() {
        return RDF+"Property";
    }

    /**
    * Returns the name of the 'instanceOf' property.
    */
    public String getInstanceOf() {
        return RDF+"type";
    }

    /**
    * Returns the name of the 'Bag' property.
    */
    public String getBag() {
        return RDF+"Bag";
    }

    /**
    * Returns the name of the 'Alt' property.
    */
    public String getAlt() {
        return RDF+"Alt";
    }

    /**
    * Returns the name of the 'Seq' property.
    */
    public String getSeq() {
        return RDF+"Seq";
    }

    /**
    * Returns the name of the 'Statement' property.
    */
    public String getStatement() {
        return RDF+"Statement";
    }

    /**
    * Returns the name of the 'subject' property.
    */
    public String getSubject() {
        return RDF+"subject";
    }
    /**
    * Returns the name of the 'predicate' property.
    */
    public String getPredicate() {
        return RDF+"predicate";
    }
    /**
    * Returns the name of the 'object' property.
    */
    public String getObject() {
        return RDF+"object";
    }

    public String getNil() {
	return RDFConstants.RDF_NIL;
    }

}
