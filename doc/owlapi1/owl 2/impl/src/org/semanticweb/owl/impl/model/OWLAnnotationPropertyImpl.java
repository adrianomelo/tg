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

package org.semanticweb.owl.impl.model; // Generated package name

import java.util.Set;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLProperty;
import java.net.URI;
import org.semanticweb.owl.model.OWLException;
import java.util.Map;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLDescription;

/**
 * An {@link OWLProperty OWLProperty} whose range is a class (a
 * collection of domain objects).
 *
 * @author Sean Bechhofer
 * @version $Id: OWLAnnotationPropertyImpl.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $ 
 */

public class OWLAnnotationPropertyImpl extends OWLPropertyImpl implements OWLAnnotationProperty
{
    public OWLAnnotationPropertyImpl( OWLDataFactoryImpl factory, URI uri ) {
	super( factory );
	this.uri = uri;
    }

    public void accept( OWLObjectVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    public void accept( OWLEntityVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }


}// OWLAnnotationProperty


/*
 * ChangeLog
 * $Log: OWLAnnotationPropertyImpl.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.1  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 *
 * 
 */

