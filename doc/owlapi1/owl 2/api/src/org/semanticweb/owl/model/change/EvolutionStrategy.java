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
 * Filename           $RCSfile: EvolutionStrategy.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:09 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model.change; 
import org.semanticweb.owl.model.OWLException;


import java.util.List;

// Generated package name


/**
 * Computes the required changes according to a particular change
 * strategy. For example, adding an axiom to an ontology may cause all
 * atomic entities referred to in the axiom to be added to the
 * ontology.
 *
 * Created: Fri Feb 14 08:54:59 2003
 *
 * @author Sean Bechhofer
 * @version $Id: EvolutionStrategy.java,v 1.1.1.1 2003/10/14 17:10:09 sean_bechhofer Exp $
 */

public interface EvolutionStrategy 
{
    /* Compute a list of atomic changes given the list */
    public List computeChanges( List list ) throws OWLException;

} // EvolutionStrategy



/*
 * ChangeLog
 * $Log: EvolutionStrategy.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 */
