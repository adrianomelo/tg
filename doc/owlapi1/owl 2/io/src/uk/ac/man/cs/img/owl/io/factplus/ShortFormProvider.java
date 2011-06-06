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
 * Filename           $RCSfile: ShortFormProvider.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:16 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.factplus; 
import java.net.URI;
import org.semanticweb.owl.model.OWLNamedObject;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * ShortFormProvider.java
 *
 *
 * Created: Wed Mar 19 09:09:30 2003
 *
 * @author Sean Bechhofer
 * @version $Id: ShortFormProvider.java,v 1.1.1.1 2003/10/14 17:10:16 sean_bechhofer Exp $
 */

public interface ShortFormProvider 
{
    public String shortForm( OWLNamedObject ono ) throws OWLException;
} // ShortFormProvider



/*
 * ChangeLog
 * $Log: ShortFormProvider.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/06/03 17:01:54  seanb
 * Additional inference
 *
 * Revision 1.1  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.1  2003/03/20 10:26:34  seanb
 * Adding Abstract Syntax Renderer
 *
 */
