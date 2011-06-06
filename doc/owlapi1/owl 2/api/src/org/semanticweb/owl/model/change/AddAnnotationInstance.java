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
 * Filename           $RCSfile: AddAnnotationInstance.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:09 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model.change; 
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLAnnotationInstance;

// Generated package name


/**
 * AddAnnotationInstance.java
 *
 *
 * Created: Fri May 09 15:39:12 2003
 *
 * @author Sean Bechhofer
 * @version $Id: AddAnnotationInstance.java,v 1.1.1.1 2003/10/14 17:10:09 sean_bechhofer Exp $
 */

public class AddAnnotationInstance extends OntologyChange
{
    private OWLObject subject;
    private OWLAnnotationProperty property;
    private Object content;

    public AddAnnotationInstance( OWLOntology ontology,
				  OWLObject subject,
				  OWLAnnotationProperty property,
				  Object content,
				  OntologyChange cause)
    {
	super( ontology, cause );
	this.subject = subject;
	this.property = property;
	this.content = content;
    }

    public OWLObject getSubject() {
	return subject;
    }
    public OWLAnnotationProperty getProperty() {
	return property;
    }
    public Object getContent() {
	return content;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

} // AddAnnotationInstance



/*
 * ChangeLog
 * $Log: AddAnnotationInstance.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 */
