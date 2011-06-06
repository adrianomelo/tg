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
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Simple mapping based on a Properties object.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: PropertyBasedURIMapper.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $ 
 *
 */

public class PropertyBasedURIMapper implements URIMapper {

    private Properties map;
    
    public PropertyBasedURIMapper( Properties properties ) {
	map = properties;
    }
    /**
     * Maps the given URI.
     *
     * @param uri           
     * @return a uri
     */
    public URI mapURI(URI uri) {
	String mapped = map.getProperty( uri.toString() );
	if ( mapped!=null ) {
	    try {
		return new URI ( mapped );
	    } catch ( URISyntaxException ex ) {
	    }
	}
	return uri;
    }
}

