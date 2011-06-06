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
 * Filename           $RCSfile: OntologyChangeEntityCollector.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package org.semanticweb.owl.model.change; 
import java.util.Iterator;
import java.util.Set;
import org.semanticweb.owl.model.helper.OWLEntityCollector;
import org.semanticweb.owl.model.OWLException;
import java.util.HashSet;

import org.apache.log4j.Logger;

// Generated package name


/**
 * A ChangeVisitor that looks at the change and determines the
 * entities that are used or referred to within the change. 
 *
 * Created: Fri Feb 14 09:53:10 2003
 *
 * @author Sean Bechhofer
 * @version $Id: OntologyChangeEntityCollector.java,v 1.2 2006/03/28 16:14:45 ronwalf Exp $
 */

public class OntologyChangeEntityCollector extends ChangeVisitorAdapter 
{
    static Logger logger = Logger.getLogger(OntologyChangeEntityCollector.class);

    Set entities;
    OWLEntityCollector collector;
    
    public OntologyChangeEntityCollector() {
	collector = new OWLEntityCollector();
    }
    
    public void reset() {
	entities = new HashSet();
    }
	
    public Set getEntities() {
	return new HashSet( entities );
    }
    
    public void visit( AddAnnotationInstance event ) throws OWLException {
    	collector.reset();
    	event.getSubject().accept(collector);
    	event.getProperty().accept(collector);
    	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
    }
	
    public void visit( AddIndividualAxiom event ) throws OWLException {
	/* Collect all the entites out of the axiom */
	collector.reset();
	event.getAxiom().accept( collector );

	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
    }

    public void visit( AddClassAxiom event ) throws OWLException {
	/* Collect all the entites out of the axiom */
	collector.reset();
	event.getAxiom().accept( collector );
	    
	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
    }
	
    public void visit( AddPropertyAxiom event ) throws OWLException {
	/* Collect all the entites out of the axiom */
	collector.reset();
	event.getAxiom().accept( collector );
	    
	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
    }

    public void visit( AddSuperClass event ) throws OWLException {
	/* Collect all the entities out of the axiom */
	collector.reset();
	event.getDescription().accept( collector );
	    
	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
	entities.add( event.getOWLClass() );
    }

    public void visit( AddSuperProperty event ) throws OWLException {
	/* Collect all the entities out of the axiom */
	entities.add( event.getProperty() );
	entities.add( event.getSuperProperty() );
    }

    public void visit( AddIndividualClass event ) throws OWLException {
	/* Collect all the entites out of the axiom */
	collector.reset();
	event.getDescription().accept( collector );
	    
	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
	entities.add( event.getIndividual() );
    }

    /**
     * 
     *
     * @param event an <code>AddEquivalentClass</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddEquivalentClass event ) throws OWLException 	{
	/* Collect all the entites out of the axiom */
	collector.reset();
	event.getDescription().accept( collector );
	    
	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
	entities.add( event.getOWLClass() );
    }
	
    /**
     * 
     *
     * @param event an <code>AddEnumeration</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddEnumeration event ) throws OWLException {
	/* Collect all the entites out of the axiom */
	collector.reset();
	event.getEnumeration().accept( collector );
	    
	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
	entities.add( event.getOWLClass() );
    }

    public void visit( AddDomain event ) throws OWLException {
	/* Collect all the entites out of the axiom */
	collector.reset();
	event.getDomain().accept( collector );
	    
	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
	entities.add( event.getProperty() );
    }

    public void visit( AddDataPropertyRange event ) throws OWLException {
	/* Collect all the entites out of the axiom */
	entities.add( event.getProperty() );
    }

    public void visit( AddObjectPropertyRange event ) throws OWLException {
	/* Collect all the entites out of the axiom */
	collector.reset();
	event.getRange().accept( collector );
	    
	/* Now add an event for each of the entities found. */
	for ( Iterator it = collector.entities().iterator();
	      it.hasNext(); ) {
	    entities.add( it.next() );
	}
	entities.add( event.getProperty() );
    }

    public void visit( AddInverse event ) throws OWLException {
	entities.add( event.getProperty() );
	entities.add( event.getInverse() );
    }

    public void visit( SetFunctional event ) throws OWLException {
	entities.add( event.getProperty() );
    }
    public void visit( SetTransitive event ) throws OWLException {
	entities.add( event.getProperty() );
    } 
    public void visit( SetSymmetric event ) throws OWLException {
	entities.add( event.getProperty() );
    }
    public void visit( SetInverseFunctional event ) throws OWLException {
	entities.add( event.getProperty() );
    }
    public void visit( SetOneToOne event ) throws OWLException {
	entities.add( event.getProperty() );
    }
    public void visit( SetDeprecated event ) throws OWLException {
	entities.add( event.getObject() );
    }
    public void visit( AddObjectPropertyInstance event ) throws OWLException{
	entities.add( event.getSubject() );
	entities.add( event.getProperty() );
	entities.add( event.getObject() );
    } 
    public void visit( AddDataPropertyInstance event ) throws OWLException{
	entities.add( event.getProperty() );
	entities.add( event.getSubject() );
    } 
    public void visit( AddImport event ) throws OWLException {
	/* Used to add all the entities from the imported
	 * ontology. This is not actually necessary. */
// 	/* Collect all the entities out of the imported ontology. */
// 	collector.reset();
// 	/* Get the imported ontology */ 
// 	event.getImportOntology().accept( collector );
	
// 	/* Now add an event for each of the entities found. */
// 	for ( Iterator it = collector.entities().iterator();
// 	      it.hasNext(); ) {
// 	    entities.add( it.next() );
// 	}
    }							      
    
} // OntologyChangeEntityCollector



/*
 * ChangeLog
 * $Log: OntologyChangeEntityCollector.java,v $
 * Revision 1.2  2006/03/28 16:14:45  ronwalf
 * Merging mindswap changes to OWLApi.
 * Rough summary:
 * * 1.5 compatibility (rename enum variables)
 * * An option to turn on and off importing in OWLConsumer
 * * Bug fix to allow DataRange in more areas
 * * Giving Anonymous individuals an identifier
 *   * New factory method - getAnonOWLIndividual
 *   * getOWLIndividual no longer accepts 'null'
 *   * added getAnonId() and isAnon() to OWLIndividual
 * * Some work on the RDF serializer, but we have a complete rewrite in
 *   Swoop that I think is better (more flexible, results easier to read)
 * * Added Transitive, Functional, InverseFunctional, Inverse, and
 *   Symmetric PropertyAxioms (not sure why, will check)
 * * Added .equals and .hashcode for all OWLObjects
 * * Added a RemoveDataType change
 * * Patches to OntologyImpl for Entity removal
 * * Added OWLIndividualTypeAssertion
 * * Added OWL(Object|Data)Property(Domain|Range)Axiom
 * * Added OWL(Object|Data)PropertyInstance
 * * Added subclass index to OWLClassImpl (and getSubClasses(...) for
 *   OWLClass)
 * * Changes for Entity renaming
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.6  2003/05/19 12:48:35  seanb
 * Individual -> Object
 *
 * Revision 1.5  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.4  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.3  2003/02/18 18:44:07  seanb
 * Further improvements to parsing. Addition of Validation Servlet.
 *
 * Revision 1.2  2003/02/17 18:23:54  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.1  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 */
