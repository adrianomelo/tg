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
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/03/30 17:46:36 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.io; 
import java.net.URI;

// Generated package name


/**
 * A number of renderers rely on being able to print out shortened
 * forms of URLs, in general using namespace definitions. This
 * interface encapsulates that functinoality.
 *
 *

 * @author Sean Bechhofer
 * @version $Id: ShortFormProvider.java,v 1.1 2004/03/30 17:46:36 sean_bechhofer Exp $
 */

public interface ShortFormProvider 
{
    public String shortForm( URI uri );
} // ShortFormProvider



/*
 * ChangeLog
 * $Log: ShortFormProvider.java,v $
 * Revision 1.1  2004/03/30 17:46:36  sean_bechhofer
 * Changes to parser to support better validation.
 *
 *
 */
