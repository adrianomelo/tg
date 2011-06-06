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
 * Filename           $RCSfile: OWLObjectsUsedCollector.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/07/09 14:04:58 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model.helper; 
import org.semanticweb.owl.model.*;

import java.util.Set;
// Dubious import....
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

// Generated package name


/**
 * Collects all the OWLEntities that this entity uses directly within
 * the stated ontology.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLObjectsUsedCollector.java,v 1.1 2004/07/09 14:04:58 sean_bechhofer Exp $
 */

public class OWLObjectsUsedCollector extends OWLEntityVisitorAdapter
{
    static Logger logger = Logger.getLogger(OWLObjectsUsedCollector.class);

    private Set entities;
    private OWLOntology ontology;
    private OWLEntityCollector collector;
    
    public OWLObjectsUsedCollector( OWLOntology _ontology )
    {
	ontology = _ontology;
	collector = new OWLEntityCollector();
	reset();
    }

    /** Reset the collector */
    public void reset() {
	/* Requires an implementation class :-((( */
	entities = new HashSet();
    }

    /** Returns the entities collected */
    public Set entities() {
	return new HashSet( entities );
    }

    public void visit( OWLClass entity ) throws OWLException
    {
	collector.reset();
	/* Grab all the stuff about the class and add it. */
	Set thingsToLookIn = new HashSet();
	/* Grab any superclasses */
	thingsToLookIn.addAll( entity.getSuperClasses( ontology ) );
	/* Grab any equivalentclasses */
	thingsToLookIn.addAll( entity.getEquivalentClasses( ontology ) );
	/* Grab any enumerations */
	thingsToLookIn.addAll( entity.getEnumerations( ontology ) );
	/* Grab any annotations */
	thingsToLookIn.addAll( entity.getAnnotations( ontology ) );

	/* Now set the collector off to look in these things. */
	for (Iterator thingIt = thingsToLookIn.iterator();
	     thingIt.hasNext();) {
	    
	    OWLObject oo =
		    (OWLObject) thingIt.next();
	    logger.debug( "Looking at: " + oo );
	    oo.accept( collector );
	}
	entities = collector.entities();
    }

    public void visit( OWLObjectProperty entity ) throws OWLException {
	collector.reset();
	/* Grab any domains */
	Set thingsToLookIn = new HashSet();
	
	thingsToLookIn.addAll( entity.getDomains( ontology ) );
	/* Grab any ranges */
	thingsToLookIn.addAll( entity.getRanges( ontology ) );

	/* Grab any annotations */
	thingsToLookIn.addAll( entity.getAnnotations( ontology ) );
	/* Now set the collector off to look in these things. */
	for (Iterator thingIt = thingsToLookIn.iterator();
	     thingIt.hasNext();) {
	    OWLObject oo =
		(OWLObject) thingIt.next();
	    oo.accept( collector);
	}
	entities = collector.entities();
	/* Chuck in inverses */
	entities.addAll( entity.getInverses( ontology ) );
    }

    public void visit( OWLDataProperty entity ) throws OWLException {
	collector.reset();
	Set thingsToLookIn = new HashSet();
	/* Grab any domains */
	thingsToLookIn.addAll( entity.getDomains( ontology ) );
	/* Grab any annotations */
	thingsToLookIn.addAll( entity.getAnnotations( ontology ) );
	
	/* Now set the collector off to look in these things. */
	for (Iterator thingIt = thingsToLookIn.iterator();
	     thingIt.hasNext();) {
	    OWLObject oo =
		(OWLObject) thingIt.next();
	    oo.accept( collector );
	}
	entities = collector.entities();
    }

    public void visit( OWLIndividual entity ) throws OWLException {
	collector.reset();
	Set thingsToLookIn = new HashSet();
	/* Grab any superclasses */
	thingsToLookIn.addAll( entity.getTypes( ontology ) );
	
	/* Grab any annotations */
	thingsToLookIn.addAll( entity.getAnnotations( ontology ) );
	
	Map map = entity.getObjectPropertyValues( ontology );
	/* Add all the properties and property values */
	for (Iterator mit = map.keySet().iterator();
	     mit.hasNext(); ) {
	    Object k = mit.next();
	    thingsToLookIn.add( k );
	    thingsToLookIn.addAll( (Set) map.get( k ) );
	}
	/* Add all object properties used */
	thingsToLookIn.addAll( entity.getDataPropertyValues( ontology ).keySet() );
	/* Now set the collector off to look in these things. */
	for (Iterator thingIt = thingsToLookIn.iterator();
	     thingIt.hasNext();) {
	    OWLObject oo =
		(OWLObject) thingIt.next();
	    oo.accept( collector );
	}
	entities = collector.entities();
    }

    /* Don't need to do annotation props.  */

    
} // OWLObjectsUsedCollector



/*
 * ChangeLog
 * $Log: OWLObjectsUsedCollector.java,v $
 * Revision 1.1  2004/07/09 14:04:58  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
 *
 *
 */
