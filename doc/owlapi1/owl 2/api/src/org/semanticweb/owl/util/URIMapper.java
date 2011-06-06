/*
 * Copyright (C) 2005, University of Manchester
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


import java.net.URI;

/**
 * Provides a mapping of URIs. This allows, for example, an
 * application to load ontologies from cached local filestore without
 * having to worry about imports/xml:base etc.

 * This is a short term solution to the problem that really should be
 * addressed in a refactoring of the connection/management
 * structure. SKB 07/10/04
 * @author Sean Bechhofer
 * @version $Id: URIMapper.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $ 
 *
 */

public interface URIMapper {
    /**
     * Maps the given URI.
     *
     * @param uri           
     * @return a uri
     */
    URI mapURI(URI uri);
}

