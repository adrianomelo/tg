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
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLException;
import java.net.URI;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLDataRange;

// Generated package name

/**
 * An {@link OWLProperty OWLProperty} whose range is some concrete data type.
 *
 *
 * @author Sean Bechhofer
 * @version $Id $
 */

public class OWLDataPropertyImpl extends OWLPropertyImpl implements OWLDataProperty
{
    public OWLDataPropertyImpl( OWLDataFactoryImpl factory, URI uri) {
	super( factory );
	this.uri = uri;
    }

    /** Add a Range. */
    boolean addRange( OWLOntology ontology, 
		      OWLDataRange type ) {
	return OWLImplHelper.getAppropriateSet( ranges, ontology ).add( type ); 
    }

    /** Remove a Range. */
    boolean removeRange( OWLOntology ontology, 
		      OWLDataRange type ) {
	return OWLImplHelper.getAppropriateSet( ranges, ontology ).remove( type ); 
    }
    /** */
    
    public void accept( OWLObjectVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }    

    public void accept( OWLEntityVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    
}// OWLDataProperty
 

/*
 * ChangeLog
 * $Log: OWLDataPropertyImpl.java,v $
 * Revision 1.4  2005/06/10 12:20:31  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/10/25 18:01:10  aditkal
 * Bringing code up to date
 *
 * 09-01-04 aditya_kalyanpur
 * Added removeRange(..)
 * 
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.4  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.3  2003/04/10 12:13:06  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.2  2003/03/21 13:55:41  seanb
 * Added domain/range handling.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */

