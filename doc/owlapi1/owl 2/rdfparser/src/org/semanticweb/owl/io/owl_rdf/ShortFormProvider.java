package org.semanticweb.owl.io.owl_rdf;

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
 * Revision           $Revision: 1.4 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/12/15 13:00:04 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

import java.net.URI;

/**
 * ShortFormProvider.java
 *
 *
 * @author Sean Bechhofer
 * @deprecated Use {@link org.semanticweb.owl.io.ShortFormProvider org.semanticweb.owl.io.ShortFormProvider} instead.
 * @version $Id: ShortFormProvider.java,v 1.4 2004/12/15 13:00:04 sean_bechhofer Exp $
 */

public interface ShortFormProvider// extends org.semanticweb.owl.io.abstract_syntax.ShortFormProvider 
{
	public String shortForm( URI uri );
}

/* ChangeLog
 * $Log: ShortFormProvider.java,v $
 * Revision 1.4  2004/12/15 13:00:04  sean_bechhofer
 * Adjustment of ShortFormProviders
 *
 * Revision 1.3  2004/11/03 17:34:22  sean_bechhofer
 * Removing reference to class in abstract_syntax package
 *
 * Revision 1.2  2004/10/25 18:01:31  aditkal
 * Bringing code up to date
 *
 * Revision 1.1  2004/10/25 13:22:59  adityak
 * Adding ShortFormProvider
*/
