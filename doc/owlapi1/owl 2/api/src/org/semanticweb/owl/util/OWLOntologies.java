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

package org.semanticweb.owl.util;


import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Contains utility functions for OWLOntologies.
 * @author <a href="mailto:volz@fzi.de">Raphael Volz</a>
 * @author <a href="mailto:motik@fzi.de">Boris Motik</a>
 */
public class OWLOntologies{

    private OWLOntologies() {
    }
    
    /**
     * Sorts given OWLOntologies into a list so that 
     * including OWLOntologies are after included OWLOntologies.
     *
     * @param owlontologies                        the set of OWLOntologies
     * @return                                  the list of sorted OWLontologies
     * @throws OWLException                    thrown if there is a problem
     */
    public static List topologicallySortOntologies(Set ontologies) throws OWLException {
        List result=new LinkedList();
        Set markSet=new HashSet(ontologies);
        int lastSize=-1;
        while (!markSet.isEmpty()) {
            OWLOntology oimodel=(OWLOntology)markSet.iterator().next();
            processPredecendents(oimodel,markSet,result);
            if (lastSize==result.size())
                throw new OWLException("OWL Ontology inclusion tree has a cycle, can't deal with that - FIX ME");
            lastSize=result.size();
        }
        return result;
    }
    private static void processPredecendents(OWLOntology oimodel,Set markSet,List result) throws OWLException  {
        markSet.remove(oimodel);
        Iterator iterator=oimodel.getIncludedOntologies().iterator();
        while (iterator.hasNext()) {
            OWLOntology includedOIModel=(OWLOntology)iterator.next();
            if (markSet.contains(includedOIModel))
                processPredecendents(includedOIModel,markSet,result);
        }
        result.add(oimodel);
    }
    /**
     * Finds all included ontologies.
     *
     * @param oimodel                   the ontologies
     * @return          Set { OWLOntology }                all included ontologies
     */
    public static Set getAllIncludedOntologies(OWLOntology ontology) throws OWLException {
        Set set=ontology.getIncludedOntologies();
        Set result=new HashSet(set);
        Iterator iterator=set.iterator();
        while (iterator.hasNext()) {
            OWLOntology currentOIModel=(OWLOntology)iterator.next();
            result.addAll(getAllIncludedOntologies(currentOIModel));
        }
        return result;
    }
}
