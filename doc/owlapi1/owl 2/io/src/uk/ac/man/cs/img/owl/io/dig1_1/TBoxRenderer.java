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
 * Filename           $RCSfile: TBoxRenderer.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/06/18 10:55:06 $
 *               by   $Author: dturi $
 ****************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kr.dl.dig.v1_1.TellsDocument.Tells;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;

/**
 * Extends {@link uk.ac.man.cs.img.owl.io.dig1_1.Renderer} 
 * by rendering only
 * the TBox and keeping a Map of <code>(URI, OWLDescription)</code> pairs.
 * This is used in the 
 * <a href="http://instancestore.man.ac.uk">instance Store</a>.
 * 
 * @author dturi $Id: TBoxRenderer.java,v 1.1 2004/06/18 10:55:06 dturi Exp $
 */
public class TBoxRenderer extends Renderer {

    private Map assertions = new HashMap();

    /**
     * Returns a Map with the role-free ABox assertions in the rendered
     * ontology.
     * 
     * @return a Map with the role-free ABox assertions in the rendered
     *         ontology.
     */
    public Map getAssertions() {
        return assertions;
    }

    /**
     * Invokes super.
     */
    public TBoxRenderer() {
        super();
    }

    void renderIndividual(OWLOntology ontology, OWLIndividual ind, Tells tells)
            throws OWLException {
        renderIndividual(ontology, ind);
    }

    void renderIndividual(OWLOntology ontology, OWLIndividual ind)
            throws OWLException {
        URI indReference = ind.getURI();
        Set descriptions = ind.getTypes(ontology);
        OWLDataFactory dataFactory = ontology.getOWLDataFactory();

        /* john hasAge 38 --> john : restriction(hasAge value(38)) */
        OWLDataValueRestriction restriction;
        Map dataValues = ind.getDataPropertyValues(ontology);
        OWLDataProperty nextDataProp;
        OWLDataValue nextDataValue;
        boolean isString = false;
        boolean isInteger = false;
        URI nextDataValueURI;
        String uriString;
        Set vals;
        for (Iterator it = dataValues.keySet().iterator(); it.hasNext();) {
            nextDataProp = (OWLDataProperty) it.next();
            vals = (Set) dataValues.get(nextDataProp);
            for (Iterator valIt = vals.iterator(); valIt.hasNext();) {
                nextDataValue = (OWLDataValue) valIt.next();
                restriction = dataFactory.getOWLDataValueRestriction(
                        nextDataProp, nextDataValue);
                descriptions.add(restriction);
            }
        }
        
        int descriptionsNumber = descriptions.size();
        if (descriptionsNumber > 0) {
            if (descriptionsNumber == 1) {
                assertions.put(indReference, (OWLDescription) descriptions
                        .iterator().next());
            } else {
                OWLDescription andDescriptions = dataFactory
                        .getOWLAnd(descriptions);
                assertions.put(indReference, andDescriptions);
            }
        }
    }

}
