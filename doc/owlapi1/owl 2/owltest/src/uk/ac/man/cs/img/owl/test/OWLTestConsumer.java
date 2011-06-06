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
 * Filename           $RCSfile: OWLTestConsumer.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:17 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.util.OWLConnection;

import org.xml.sax.SAXException;

import edu.unika.aifb.rdf.api.syntax.RDFConsumer;
import edu.unika.aifb.rdf.api.util.RDFConstants;

/**
 * Basic consumer that will handle parsing of OWL Test RDF Manifests
 * and will extract all the ontologies referred to and their claimed
 * species.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLTestConsumer.java,v 1.1.1.1 2003/10/14 17:10:17 sean_bechhofer Exp $
 */

public class OWLTestConsumer implements RDFConsumer {
    Logger logger = null;

    OWLTest test;

    /**
     */
    public OWLTestConsumer(OWLTest test) {
        this.test = test;
        logger = Logger.getLogger(OWLTestConsumer.class);
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#startModel(String)
     */
    public void startModel(String arg0) throws SAXException {
        // onto is created already, arg0 gives only the physicalURI of the onto 
        // [REVIEW] Use Connection instead to create onto object and get it from here.
        logger.debug("Start model " + arg0);
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#endModel()
     */
    public void endModel() throws SAXException {
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#statementWithResourceValue(String, String, String)
     */
    public void statementWithResourceValue(
        String subj,
        String pred,
        String obj)
        throws SAXException {
	/* Makes a lot of assumptions about the well-formedness of the tests....*/
	if ( pred.equals( OWLTestVocabularyAdapter.INSTANCE.getRDFType() ) ) {
	    if (OWLTestVocabularyAdapter.INSTANCE.getTestTypes().contains( obj ) ) {
		test.setURI( subj );
		test.setType( obj );
	    } 
	    if (obj.equals( OWLTestVocabularyAdapter.INSTANCE.getRDFXMLDocument() ) ) {
		logger.debug( "DOCUMENT!" + subj );
		test.getDocuments().add( subj );
	    }
	}


	if (OWLTestVocabularyAdapter.INSTANCE.getDocumentTypes().contains( pred ) ) {
	    test.getDocuments().add( obj );
	    test.getDocumentRoles().put( obj, pred );
	}

	if ( pred.equals( OWLTestVocabularyAdapter.INSTANCE.getLevel() ) ) {
	    Set levelSet = (Set) test.getDocumentLevels().get( subj );
	    if (levelSet == null) {
		levelSet = new HashSet();
		test.getDocumentLevels().put( subj, levelSet );
	    }
	    levelSet.add( obj );
	    //test.getDocumentLevels().put( subj, obj );
	}
	if ( pred.equals( OWLTestVocabularyAdapter.INSTANCE.getUsedDatatype() ) ) {
	    test.getDatatypes().add( obj );
	}
	if ( pred.equals( OWLTestVocabularyAdapter.INSTANCE.getSupportedDatatype() ) ) {
	    test.getDatatypes().add( obj );
	}
	if ( pred.equals( OWLTestVocabularyAdapter.INSTANCE.getNotSupportedDatatype() ) ) {
	    test.getDatatypes().add( obj );
	}
    }
    

    /**
 * Called when a statement with literal value is added to the model.
 *
 * @param subject URI of the subject resource
 * @param predicate             URI of the predicate resource
 * @param object                literal object value
 * @param parseType             the parse type of the literal
 * @param language              the language
 */
    public void statementWithLiteralValue(
        String subject,
        String predicate,
        String object,
        String parseType,
        String language)
        throws SAXException {
	/* Assumes everything's in the right order */
	if ( predicate.equals( OWLTestVocabularyAdapter.INSTANCE.getStatus() ) ) {
	    if ( test.getURI().equals( subject ) ) {
		test.setStatus( object );
	    }
	}
	if ( predicate.equals( OWLTestVocabularyAdapter.INSTANCE.getDescription() ) ) {
	    test.getDescriptions().put( subject, object );
	}
    }

    public void logicalURI(String arg0) throws SAXException {
    } 

    public void includeModel(String arg0, String arg1) throws SAXException {
    } 

    public void addModelAttribte(String arg0, String arg1)
        throws SAXException {
    }
    
} // OWLTestConsumer



/*
 * ChangeLog
 * $Log: OWLTestConsumer.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:17  sean_bechhofer
 * Initial Import
 *
 * Revision 1.6  2003/08/28 10:29:04  bechhofers
 * Updating parser to improve validation. Addition of new consumer with
 * simple triple model.
 *
 * Revision 1.5  2003/08/20 08:39:57  bechhofers
 * Alterations to tests.
 *
 * Revision 1.4  2003/06/11 16:50:29  seanb
 * Utility test processing classes for extracting, parsing and listing tests.
 *
 * Revision 1.3  2003/06/06 14:27:34  seanb
 * Utilities for processing tests.
 *
 * Revision 1.2  2003/02/17 18:24:43  seanb
 * Documentation update.
 *
 * Revision 1.1  2003/02/12 16:11:34  seanb
 * Adding OWL tests.
 *
 */
