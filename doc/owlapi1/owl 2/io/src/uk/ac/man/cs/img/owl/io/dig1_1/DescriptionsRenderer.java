/*
 * Copyright (C) 2003 The University of Manchester 
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
 * Filename           $RCSfile: DescriptionsRenderer.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/06/24 15:35:51 $
 *               by   $Author: dturi $
 ****************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import org.apache.log4j.Logger;

import java.util.ArrayList;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;
import org.kr.dl.dig.v1_1.Concept;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;


/**
 * Utility class for translating {@link org.semanticweb.owl.model.OWLDescription}s
 * to DIG 1.1 {@link Concept}s.
 * @author dturi 
 * $Id: DescriptionsRenderer.java,v 1.1 2004/06/24 15:35:51 dturi Exp $
 */
public class DescriptionsRenderer {
    
    /**
     * Log4J Logger for this class
     */
    public static final Logger logger = Logger
        .getLogger(DescriptionsRenderer.class);
    
    /**
     * Translates <code>owlDescription</code> to a DIG 1.1 {@link Concept}.
     * 
     * @param owlDescription
     *            the <code>OWLDescription</code> to be translated to DIG.
     * @return the translated <code>owlDescription</code> as a
     *         {@link Concept}.
     * @throws OWLException
     *             if an exception is raised by either the DIG renderer.
     */
    static public Concept owlToDigConcept(OWLDescription owlDescription)
            throws OWLException {
        Concept concept = Concept.Factory.newInstance();
        ConceptRenderingVisitor rendererVisitor = new ConceptRenderingVisitor(
                concept);
        owlDescription.accept(rendererVisitor);

        if (logger.isDebugEnabled()) {
            logger
                    .debug("owlToDigDescription() - Concept concept = "
                            + concept);
            XmlOptions validateOptions = new XmlOptions();
            ArrayList errorList = new ArrayList();
            validateOptions.setErrorListener(errorList);
            boolean isValid = concept.validate(validateOptions);
            if (!isValid) {
                logger.warn("concept not valid.");
                for (int i = 0; i < errorList.size(); i++) {
                    XmlError error = (XmlError) errorList.get(i);
                    logger.warn("Message: " + error.getMessage());
                    logger.warn("Location of invalid XML: "
                            + error.getCursorLocation().xmlText());
                }
            }
        }
        return concept;
    }

}
