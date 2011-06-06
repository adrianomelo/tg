/*
 * Copyright (C) 2005, University of Karlsruhe
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
import java.util.Map;

/**
 * Resolves a logical URI to connection parameters
 *  and a physical URI.
 *
 * @author Raphael Volz (volz@aifb.uni-karlsruhe.de)
 * @author Boris Motik (boris.motik@fzi.de)
 */
public interface LogicalURIResolver {
    /**
     * Resolves a logical URI to the parameters and a physical URI. If this instance cannot do the resolution, <code>null</code>
     * is returned.
     *
     * @param logicalURI            the logical URI
     * @return                      the parameters and the physical URI for the logical URI
     */
    ResultHolder resolveLogicalURI(URI logicalURI);

    /**
     * Holds the result if the resolution.
     */
    public static class ResultHolder {
        /** The connection parameters. */
        public Map m_connectionParameters;
        /** The physical URI. */
        public URI m_physicalURI;
    }
}

