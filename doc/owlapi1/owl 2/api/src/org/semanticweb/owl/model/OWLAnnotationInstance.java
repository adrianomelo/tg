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
 * Filename           $RCSfile: OWLAnnotationInstance.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/07/09 12:07:45 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model; // Generated package name


/**
 * OWLAnnotationInstance.java
 *
 *
 * Created: Thu May 08 17:36:07 2003
 *
 * @author Sean Bechhofer
 * @version $Id: OWLAnnotationInstance.java,v 1.2 2004/07/09 12:07:45 sean_bechhofer Exp $
 */

public interface OWLAnnotationInstance extends OWLOntologyObject, OWLObject 
{
    public OWLObject getSubject() throws OWLException;
    public OWLAnnotationProperty getProperty() throws OWLException;
    /** This is either an individual, a URI reference or a data literal */
    public Object getContent() throws OWLException;
} // OWLAnnotationInstance



/*
 * ChangeLog
 * $Log: OWLAnnotationInstance.java,v $
 * Revision 1.2  2004/07/09 12:07:45  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 */
