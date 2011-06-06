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

package org.semanticweb.owl.impl.model; // Generated package name

import java.util.Set;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLException;
import java.util.Map;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLDescription;
import java.util.Iterator;
/**
 * A Property in an OWL ontology. 
 * 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLPropertyImpl.java,v 1.4 2005/06/10 12:20:32 sean_bechhofer Exp $ 
 */

public abstract class OWLPropertyImpl extends OWLEntityImpl implements OWLProperty, OWLDeprecatableObjectImpl 
{
    //    private Map equivalentProperties;
    private Map superProperties;
    private Map domains;
    protected Map ranges;

    /* Set that determines properties of the property. If an ontology
     * occurs in this set, then the ontology asserts that the property
     * holds. */
    private Set functional;
    private Set deprecated;

    public OWLPropertyImpl( OWLDataFactoryImpl factory ) {
	super( factory );
	this.superProperties = ListFactory.getMap();
	this.domains = ListFactory.getMap();
	//	this.equivalentProperties = ListFactory.getMap();
	this.ranges = ListFactory.getMap();
	this.functional = ListFactory.getSet();
	this.deprecated = ListFactory.getSet();
    }

    public Set getSuperProperties() {
	return OWLImplHelper.unionValues( superProperties );
    }

    public Set getSuperProperties( OWLOntology  o ) {
	return OWLImplHelper.getAppropriateSet( superProperties, o );
    }

    public Set getSuperProperties( Set ontos ) {
	return OWLImplHelper.selectValues( superProperties, ontos );
    }

    /** Add a super property. */
    boolean addSuperProperty( OWLOntology ontology, 
		       OWLProperty superProperty ) {
	return OWLImplHelper.getAppropriateSet( superProperties, ontology ).add( superProperty ); 
    }

    /** Remove a super property. */
    boolean removeSuperProperty( OWLOntology ontology, 
		       OWLProperty superProperty ) {
	return OWLImplHelper.getAppropriateSet( superProperties, ontology ).remove( superProperty ); 
 }
    
//     public Set getEquivalentProperties() {
// 	return OWLImplHelper.unionValues( equivalentProperties );
//     }

//     public Set getEquivalentProperties( OWLOntology  o ) {
// 	return OWLImplHelper.getAppropriateSet( equivalentProperties, o );
//     }
    

    public Set getDomains() {
	return OWLImplHelper.unionValues( domains );
    }

    public Set getDomains( OWLOntology  o ) {
	return OWLImplHelper.getAppropriateSet( domains, o );
    }

    public Set getDomains( Set ontos ) {
	return OWLImplHelper.selectValues( domains, ontos );
    }

    /** Add a domain. */
    boolean addDomain( OWLOntology ontology, 
		       OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( domains, ontology ).add( description ); 
    }

    /** Remove a domain. */
    boolean removeDomain( OWLOntology ontology, 
		       OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( domains, ontology ).remove( description ); 
    }
    /** */
    
    public Set getRanges() {
	return OWLImplHelper.unionValues( ranges );
    }

    public Set getRanges( OWLOntology  o ) {
	return OWLImplHelper.getAppropriateSet( ranges, o );
    }

    public Set getRanges( Set ontos ) {
	return OWLImplHelper.selectValues( ranges, ontos );
    }

    public boolean isFunctional() {
	/* A property is functional if some ontology says it is. */
	return !functional.isEmpty(); 
    }

    public boolean isFunctional( OWLOntology o ) {
	return functional.contains( o );
    }

    public boolean isFunctional( Set ontos ) {
	for (Iterator it = ontos.iterator();
	     it.hasNext(); ) {
	    if (functional.contains( it.next() ) ) {
		return true;
	    }
	} 
	return false;
    }

    void setFunctional( OWLOntology o, boolean b ) {
	if ( b ) {
	    functional.add( o );
	} else {
	    if ( functional.contains( o ) ) {
		functional.remove ( o );
	    }
	}
    }

    public boolean isDeprecated( OWLOntology o ) {
	return deprecated.contains( o );
    }

    public void setDeprecated( OWLOntology o, boolean b ) {
	if ( b ) {
	    deprecated.add( o );
	} else {
	    if ( deprecated.contains( o ) ) {
		deprecated.remove ( o );
	    }
	}
    }

}// OWLProperty


/*
 * ChangeLog
 * $Log: OWLPropertyImpl.java,v $
 * Revision 1.4  2005/06/10 12:20:32  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/10/25 18:01:10  aditkal
 * Bringing code up to date
 *
 * 09-01-04 aditya_kalyanpur
 * Added removeDomain(..), removeSuperProperty(..)
 * 
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.7  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.6  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.5  2003/03/26 19:04:02  rvolz
 * *** empty log message ***
 *
 * Revision 1.4  2003/03/21 13:55:41  seanb
 * Added domain/range handling.
 *
 * Revision 1.3  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.2  2003/02/07 18:43:11  seanb
 * New change events
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */

