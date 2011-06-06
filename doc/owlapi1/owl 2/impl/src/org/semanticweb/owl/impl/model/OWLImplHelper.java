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

package org.semanticweb.owl.impl.model;
import java.util.Map;
import org.semanticweb.owl.model.OWLOntology;
import java.util.Set;
import java.util.Iterator;

import org.apache.log4j.Logger;

// Generated package name

/**
 *
 * Created: Wed Jan 22 14:21:47 2003
 *
 * @author Sean Bechhofer
 * @version $Id: OWLImplHelper.java,v 1.3 2005/06/10 12:20:31 sean_bechhofer Exp $
 */

public class OWLImplHelper {

    static Logger logger = Logger.getLogger(OWLImplHelper.class);

    /** Given a Map that maps ontologies to Sets, will return the set
     * that is the value for the given ontology. Note that this
     * returns the *actual* set, not a copy. */
    static Set getAppropriateSet(Map map, OWLOntology o) {
        try {
            Set s = (Set) map.get(o);
            if (s == null) {
                s = ListFactory.getSet();
                map.put(o, s);
            }
            return s;
        } catch (ClassCastException ex) {
            return ListFactory.getSet();
        }
    }

    /** Given a Map that maps ontologies to Maps, will return the set
     * that is the value for the given ontology. Note that this
     * returns the *actual* map, not a copy. */
    static Map getAppropriateMap(Map map, OWLOntology o) {
        try {
            Map m = (Map) map.get(o);
            if (m == null) {
                m = ListFactory.getMap();
                map.put(o, m);
            }
            logger.debug("YY");
            return m;
        } catch (ClassCastException ex) {
            logger.debug("XX");
            return ListFactory.getMap();
        }
    }

    /** Given a map that maps ontologies to sets, will return a set
    consisting of the union of all the sets. */
    static Set unionValues(Map map) {
        Set result = ListFactory.getSet();
        for (Iterator it = map.values().iterator(); it.hasNext();) {
            Set s = (Set) it.next();
            result.addAll(s);
        }
        return result;
    }

    /** Given a map that maps ontologies to sets, will return a set
    consisting of the union of all the sets for the given keys. */
    static Set selectValues(Map map, Set ontos) {
        Set result = ListFactory.getSet();
        for (Iterator it = ontos.iterator(); it.hasNext();) {
            /* For each ontology, grab the set that it maps to and
             * then add that to resulting collection.  */
            OWLOntology onto = (OWLOntology) it.next();
            Set s = getAppropriateSet(map, onto);
            result.addAll(s);
        }
        return result;
    }

    /** Given a map that maps ontologies to maps (that map objects to
    	sets), will return a new map which consisting of the union of
    all the maps. */
    static Map unionMaps(Map map) {
        Map result = ListFactory.getMap();
        for (Iterator it = map.values().iterator(); it.hasNext();) {
            Map m = (Map) it.next();
            for (Iterator keyIt = m.keySet().iterator(); keyIt.hasNext();) {
                Object key = keyIt.next();
                Set s = null;
                if (!result.containsKey(key)) {
                    /* If the result doesn't yet have an entry for the
                     * key, put a new one it */
                    s = ListFactory.getSet();
                    result.put(key, s);
                } else {
                    /* Otherwise, get the one that's in there already */
                    s = (Set) result.get(key);
                }
                /* Now get the values that are in the particular map
                 * for this key. */
                Set vals = (Set) m.get(key);

                logger.debug(m.get(key));

                /* Add all those values to the set in the result map. */
                s.addAll(vals);
            }
        }
        return result;
    }

    static Map selectMaps(Map map, Set ontologies) {
        Map result = ListFactory.getMap();
        for (Iterator it = ontologies.iterator(); it.hasNext();) {
            /* Get the map that corresponds to the next ontology */
            Map m = (Map) map.get(it.next());
            // 	for ( Iterator it = map.values().iterator();
            // 	      it.hasNext(); ) {
            //	    Map m = (Map) it.next();
            if (m != null) {
                for (Iterator keyIt = m.keySet().iterator();
                    keyIt.hasNext();
                    ) {
                    Object key = keyIt.next();
                    Set s = null;
                    if (!result.containsKey(key)) {
                        /* If the result doesn't yet have an entry for the
                         * key, put a new one it */
                        s = ListFactory.getSet();
                        result.put(key, s);
                    } else {
                        /* Otherwise, get the one that's in there already */
                        s = (Set) result.get(key);
                    }
                    /* Now get the values that are in the particular map
                     * for this key. */
                    Set vals = (Set) m.get(key);

                    logger.debug(m.get(key));

                    /* Add all those values to the set in the result map. */
                    s.addAll(vals);
                }
            }
        }
        return result;
    }

} // OWLImplHelper

/*
 * ChangeLog
 * $Log: OWLImplHelper.java,v $
 * Revision 1.3  2005/06/10 12:20:31  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/01/26 07:24:01  digitalis
 * Change in connection.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.4  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.3  2003/02/17 18:23:54  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.2  2003/02/06 16:42:20  seanb
 * Tweaking of class implementation.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
