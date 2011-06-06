/*
 * Copyright (C) 2003, University of Manchester
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

package org.semanticweb.owl.impl.model; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

// Generated package name


/**
 * Factory for collection types.
 *
 *
 * @author Phillip Lord
 * @version $Id: ListFactory.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class ListFactory 
{
    public static Map getMap()
    {
        return new HashMap();
    }
    
    public static Map getMap( Map map )
    {
        return new HashMap( map );
    }
    
    public static List getList()
    {
        return new ArrayList();
    }
    
    public static List getList( List list )
    {
        return new ArrayList( list );
    }

    public static Set getSet()
    {
        return new HashSet();
    }

    public static Set getSet( Collection c )
    {
        return new HashSet( c );
    }
    
    public static Collection getCollection()
    {
        return new ArrayList();
    }

} // ListFactory



/*
 * ChangeLog
 * $Log: ListFactory.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 *
 */
