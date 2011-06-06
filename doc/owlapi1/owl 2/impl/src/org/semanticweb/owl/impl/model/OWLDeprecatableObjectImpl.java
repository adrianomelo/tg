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
 * Filename           $RCSfile: OWLDeprecatableObjectImpl.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:14 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.impl.model; 
import org.semanticweb.owl.model.OWLDeprecatableObject;
import org.semanticweb.owl.model.OWLOntology;

// Generated package name


/**
 * An interface that provides a set method for the deprecation flag.
 *
 *
 * Created: Mon Feb 10 14:01:11 2003
 *
 * @author Sean Bechhofer
 * @version $Id: OWLDeprecatableObjectImpl.java,v 1.1.1.1 2003/10/14 17:10:14 sean_bechhofer Exp $
 */

interface OWLDeprecatableObjectImpl extends OWLDeprecatableObject 
{
    public void setDeprecated( OWLOntology o, boolean b );

} // OWLDeprecatableObjectImpl



/*
 * ChangeLog
 * $Log: OWLDeprecatableObjectImpl.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.1  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 */
