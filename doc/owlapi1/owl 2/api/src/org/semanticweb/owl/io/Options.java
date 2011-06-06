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

package org.semanticweb.owl.io; 
import java.util.Map;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * Interface for things that can have options set and unset. 
 *
 *
 * Created: Wed Jan 22 10:19:37 2003
 *
 * @author Sean Bechhofer
 * @version $Id: Options.java,v 1.4 2005/06/10 12:20:28 sean_bechhofer Exp $
 */

public interface Options 
{

    /**
     * Set options for the object.
     * @param options a <code>Map</code> value. Should contain a map from {@link String String}s to {@link Object Object}s.
     */
    public void setOptions( Map options ) throws OWLException;

    /**
     * 
     * Get options for the object.
     * @return a <code>Map</code> value. Contains a map from {@link String String}s to {@link Object Object}s.
     */
    public Map getOptions();
    
} // Options



/*
 * ChangeLog
 * $Log: Options.java,v $
 * Revision 1.4  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/10/19 17:13:03  sean_bechhofer
 * Adding exception throw to option setting.
 *
 * Revision 1.2  2004/03/05 17:34:48  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 */
