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
 * Filename           $RCSfile: OWLIndividualReasoner.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:08 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.inference; 
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLDescription;
import java.util.Set;
import org.semanticweb.owl.model.OWLIndividual;

// Generated package name


/**
 * A Reasoner for OWL Individuals. Note that although some of the
 * functionality here can be provided by an {@link OWLClassReasoner OWLClassReasoner} via the use of a one-of, it may be useful to have this as
 * a separate interface.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLIndividualReasoner.java,v 1.1.1.1 2003/10/14 17:10:08 sean_bechhofer Exp $
 */

public interface OWLIndividualReasoner extends OWLReasoner
{
    /** Returns true if the given individual is an instance of the
     * given description */
    
    public boolean isInstanceOf( OWLIndividual i, OWLDescription d ) throws
	OWLException;
    
    /** Returns the individuals which are instances of the given
     * description */
    public Set instancesOf( OWLDescription d ) throws OWLException;

} // OWLIndividualReasoner



/*
 * ChangeLog
 * $Log: OWLIndividualReasoner.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/07/08 18:01:07  bechhofers
 * Presentation Servlet and additional documentation regarding inference.
 *
 * Revision 1.2  2003/07/05 14:16:44  bechhofers
 * Small change to reasoning interfaces.
 *
 * Revision 1.1  2003/06/20 14:07:51  seanb
 * Addition of some documentation. Minor tinkering.
 *
 * Revision 1.4  2003/06/19 13:32:37  seanb
 * Addition of construct checking. Change to parser to do imports properly.
 *
 * Revision 1.3  2003/05/29 09:07:28  seanb
 * Moving RDF error handler
 *
 * Revision 1.2  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.1  2003/05/19 11:32:26  seanb
 * Interfaces for reasoning and inference.
 *
 */
