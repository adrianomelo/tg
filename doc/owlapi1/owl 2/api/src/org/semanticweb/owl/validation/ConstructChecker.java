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
 * Filename           $RCSfile: ConstructChecker.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/03/30 17:46:37 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.validation;

/**
 * Given an ontology, provides an analysis of the constructs used
 * within that ontology, e.g. unions or oneof or a relationship
 * between two individuals. This gives a finer grained notion of the
 * expressivity of an ontology than is provided by the species
 * validator. This may be useful when using external reasoning
 * processes that are able to handle a subset of the OWL language
 * which does not directly correspond to an OWL subspecies: for
 * example a <em>SHIN</em> reasoner, which would be able to handle OWL
 * DL as long as <code>one-of</code> is not present.
 *
 * @author Sean Bechhofer
 * @version
 */
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import java.util.Set;

public interface ConstructChecker 
{
    /** Returns a set of Integers representing the constructs used within an
     * ontology. Constants taken from {@link OWLValidationConstants OWLValidationConstants}. */
    public Set constructsUsed( OWLOntology onto ) throws OWLException;

}// ConstructChecker
