/*
 * Copyright (C) 2003, University of Karlsruhe
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


import java.util.Map;

/**
 * Implemented by each KAON API implementation to resolve the physicalURI to the default parameters.
 *
 * @author Raphael Volz (volz@aifb.uni-karlsruhe.de)
 * @author Boris Motik (boris.motik@fzi.de)
 */
public interface PhysicalURIToDefaultParametersResolver {
    /**
     * Called by the KAONManager to attempt the resolution for given physical URI. If the resolution is successful,
     * non-null value should be returned.
     *
     * @param physicalURI           the physical URI
     * @param contextParameters     the parameters that are used for missing elements (may be <code>null</code>)
     * @return                      the default parameters for accessing the physical URI, or <code>null</code> if the parameters cannot be resolved
     */
    Map getDefaultParametersForPhysicalURI(String physicalURI,Map contextParameters);
}
