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
 * Filename           $RCSfile: OWLDeprecatableObject.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:08 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model; // Generated package name


/**
 * A super interface representing those things that can be
 * deprecated. Deprecating has no semantic import (in terms of the
 * model theory), but may effect the way that applications deal with
 * classes.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLDeprecatableObject.java,v 1.1.1.1 2003/10/14 17:10:08 sean_bechhofer Exp $
 */

public interface OWLDeprecatableObject 
{
    public boolean isDeprecated( OWLOntology ontology ) throws OWLException;
    
} // OWLDeprecatableObject



/*
 * ChangeLog
 * $Log: OWLDeprecatableObject.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.2  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.1  2003/02/10 09:23:06  seanb
 * Changes to cardinality, addition of property instances.
 *
 */
