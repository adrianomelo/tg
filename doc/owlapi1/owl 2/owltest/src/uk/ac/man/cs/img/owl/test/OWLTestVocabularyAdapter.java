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
 * Filename           $RCSfile: OWLTestVocabularyAdapter.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:17 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.test;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLException;

import edu.unika.aifb.rdf.api.util.RDFConstants;

/**
 * Vocabulary of the OWL/RDF test specification as of
 * 03/02/03. 
 * 
 * @since 04.02.2003
 * @author Sean Bechhofer
 * 
 */
public class OWLTestVocabularyAdapter {
    
    public static final OWLTestVocabularyAdapter INSTANCE =
        new OWLTestVocabularyAdapter();
    
    /** Namespaces. */
    protected static final String OWL = "http://www.w3.org/2002/07/owl#";
    protected static final String RDF  ="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    protected static final String RDFS ="http://www.w3.org/2000/01/rdf-schema#";
    protected static final String RTEST ="http://www.w3.org/2000/10/rdf-tests/rdfcore/testSchema#";
    protected static final String OTEST="http://www.w3.org/2002/03owlt/testOntology#";
	
    public String getRDFXMLDocument() {
        return RTEST + "RDF-XML-Document";
    }
    public String getRDFType() {
        return RDF + "type";
    }
    public String getLevel() {
        return OTEST + "level";
    }
    public String getStatus() {
        return RTEST + "status";
    }
    public String getOWLLite() {
        return OTEST + "Lite";
    }
    public String getOWLDL() {
        return OTEST + "DL";
    }
    public String getOWLFull() {
        return OTEST + "Full";
    }
    public String getNotOwlFeatureTest() {
        return OTEST + "NotOwlFeatureTest";
    }
    public String getPositiveEntailmentTest() {
        return OTEST + "PositiveEntailmentTest";
    }
    public String getNegativeEntailmentTest() {
        return OTEST + "NegativeEntailmentTest";
    }
    public String getTrueTest() {
        return OTEST + "TrueTest";
    }
    public String getOWLforOWLTest() {
        return OTEST + "OWLforOWLTest";
    }
    public String getConsistencyTest() {
        return OTEST + "ConsistencyTest";
    }
    public String getInconsistencyTest() {
        return OTEST + "InconsistencyTest";
    }
    public String getImportEntailmentTest() {
        return OTEST + "ImportEntailmentTest";
    }
    public String getImportLevelTest() {
        return OTEST + "ImportLevelTest";
    }
    public String getInputDocument() {
        return RTEST + "inputDocument";
    }
    public String getPremiseDocument() {
        return RTEST + "premiseDocument";
    }
    public String getConclusionDocument() {
        return RTEST + "conclusionDocument";
    }
    public String getDescription() {
        return RTEST + "description";
    }
    public String getImportedPremiseDocument() {
        return OTEST + "importedPremiseDocument";
    }
    public String getUsedDatatype() {
        return OTEST + "usedDatatype";
    }
    public String getSupportedDatatype() {
        return OTEST + "supportedDatatype";
    }
    public String getNotSupportedDatatype() {
        return OTEST + "notSupportedDatatype";
    }

    private HashSet testTypes;

    public Set getTestTypes() {
        if (testTypes == null) {
            testTypes = new HashSet();
            testTypes.add( INSTANCE.getNotOwlFeatureTest() );
	    testTypes.add( INSTANCE.getPositiveEntailmentTest() );
	    testTypes.add( INSTANCE.getNegativeEntailmentTest() );
	    testTypes.add( INSTANCE.getTrueTest() );
	    testTypes.add( INSTANCE.getOWLforOWLTest() );
	    testTypes.add( INSTANCE.getConsistencyTest() );
	    testTypes.add( INSTANCE.getInconsistencyTest() );
	    testTypes.add( INSTANCE.getImportEntailmentTest() );
	    testTypes.add( INSTANCE.getImportLevelTest() );
	}
	return testTypes;
    }

    private HashSet documentTypes;

    public Set getDocumentTypes() {
        if (documentTypes == null) {
            documentTypes = new HashSet();
            documentTypes.add( INSTANCE.getInputDocument() );
            documentTypes.add( INSTANCE.getPremiseDocument() );
            documentTypes.add( INSTANCE.getConclusionDocument() );
            documentTypes.add( INSTANCE.getImportedPremiseDocument() );
	}
	return documentTypes;
    }
    
}
