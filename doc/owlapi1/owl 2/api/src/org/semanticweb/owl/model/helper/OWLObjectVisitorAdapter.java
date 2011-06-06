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

package org.semanticweb.owl.model.helper; // Generated package name

import org.semanticweb.owl.model.*;
import org.apache.log4j.Logger;

/**
 * Provides an empty implementation of the {@link OWLObjectVisitor OWLObjectVisitor} interface. Those wishing to implement a visitor over subsets of the data structure can extend this class and provide the methods they need. Default behaviour of this adapter is to do nothing except log the visit to an appropriate logger. 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLObjectVisitorAdapter.java,v 1.4 2006/03/28 16:14:45 ronwalf Exp $
 */

public abstract class OWLObjectVisitorAdapter implements OWLObjectVisitor
{

    static Logger logger = Logger.getLogger(OWLObjectVisitorAdapter.class);
    
    public void visit( OWLAnd node ) throws OWLException
    {
	logger.debug("Default And");
	return;
    }
    
    public void visit( OWLAnnotationInstance node ) throws OWLException
    {
	logger.debug("Default AnnotationInstance");
	return;
    }

    public void visit( OWLAnnotationProperty node ) throws OWLException
    {
	logger.debug("Default AnnotationProperty");
	return;
    }

    public void visit( OWLClass node ) throws OWLException
    {
	logger.debug("Default Class");
	return;
    }
    
    public void visit( OWLClassAxiom node ) throws OWLException
    {
	logger.debug("Default Class");
	return;
    }
    
    public void visit( OWLDataAllRestriction node ) throws OWLException
    {
	logger.debug("Default DataAll");
	return;
    }

    public void visit( OWLDataCardinalityRestriction node ) throws OWLException
    {
	logger.debug("Default DataCardinality");
	return;
    }
    
    public void visit( OWLDataEnumeration node ) throws OWLException
    {
	logger.debug("Default Data Enumeration");
	return;
    }
    
    public void visit( OWLDataProperty node ) throws OWLException
    {
	logger.debug("Default DataProperty");
	return;
    }
    
    public void visit(OWLDataPropertyInstance node) throws OWLException {
    	logger.debug("Default DataPropertyInstance");
    	return;
	}
    
    public void visit(OWLDataPropertyRangeAxiom node) throws OWLException {
    	logger.debug("Default DataPropertyRange");
    	return;
	}
    
    public void visit( OWLDataSomeRestriction node ) throws OWLException
    {
	logger.debug("Default DataSome");
	return;
    }
    
    public void visit( OWLDataType node ) throws OWLException
    {
	logger.debug("Default ConcreteType");
	return;
    }
    
    public void visit( OWLDataValue node ) throws OWLException
    {
	logger.debug("Default Concrete");
	return;
    }
    
    public void visit( OWLDataValueRestriction node ) throws OWLException
    {
	logger.debug("Default DataValue");
	return;
    }
    
    public void visit( OWLDifferentIndividualsAxiom node ) throws OWLException
    {
	logger.debug("Default DifferentIndividuals");
	return;
    }
    
    public void visit( OWLDisjointClassesAxiom node ) throws OWLException
    {
	logger.debug("Default DisjointClasses");
	return;
    }
    
    public void visit( OWLEnumeration node ) throws OWLException
    {
	logger.debug("Default Enumeration");
	return;
    }

//     public void visit( OWLAnonymousIndividual node ) throws OWLException
//     {
// 	logger.debug("Default AnonymousIndividual");
// 	return;
//     }
    
    public void visit( OWLEquivalentClassesAxiom node ) throws OWLException
    {
	logger.debug("Default EquivalentClasses");
	return;
    }
    
    public void visit( OWLEquivalentPropertiesAxiom node ) throws OWLException
    {
	logger.debug("Default EquivalentProperties");
	return;
    }
    
    public void visit( OWLFrame node ) throws OWLException
    {
	logger.debug("Default OWLFrame");
	return;
    }
    
    public void visit(OWLFunctionalPropertyAxiom node) throws OWLException {
		logger.debug("Default FunctionalPropertyAxiom");
		return;
	}
    
    public void visit( OWLIndividual node ) throws OWLException
    {
	logger.debug("Default OWLIndividual");
	return;
    }
    
    public void visit(OWLIndividualTypeAssertion node) throws OWLException {
		logger.debug("Default IndividualTypeAssertion");
		return;
	}
    
    public void visit(OWLInverseFunctionalPropertyAxiom node) throws OWLException {
		logger.debug("Default InverseFunctionalPropertyAxiom");
		return;
	}
    
    public void visit(OWLInversePropertyAxiom node) throws OWLException {
		logger.debug("Default InvserseProperty");
		return;
	}
    
    public void visit( OWLNot node ) throws OWLException
    {
	logger.debug("Default Not");
	return;
    }
    
    public void visit( OWLObjectAllRestriction node ) throws OWLException
    {
	logger.debug("Default IndividualAll");
	return;
    }
    
    public void visit( OWLObjectCardinalityRestriction node ) throws OWLException
    {
	logger.debug("Default IndividualCardinality");
	return;
    }
    
    public void visit( OWLObjectProperty node ) throws OWLException
    {
	logger.debug("Default ObjectProperty");
	return;
    }
    
    public void visit(OWLObjectPropertyInstance node) throws OWLException {
		logger.debug("Default ObjectPropertyInstance");
		return;

	}

	public void visit(OWLObjectPropertyRangeAxiom node) throws OWLException {
		logger.debug("Default ObjectPropertyRangeAxiom");
    		return;
	}

	public void visit( OWLObjectSomeRestriction node ) throws OWLException
    {
	logger.debug("Default IndividualSome");
	return;
    }

	public void visit( OWLObjectValueRestriction node ) throws OWLException
    {
	logger.debug("Default IndividualValue");
	return;
    }

	public void visit( OWLOntology node ) throws OWLException
    {
	logger.debug("Default Ontology");
	return;
    }

	public void visit( OWLOr node ) throws OWLException
    {
	logger.debug("Default Or");
	return;
    }

	public void visit(OWLPropertyDomainAxiom node) throws OWLException {
		logger.debug("Default PropertyDomainAxiom");
		return;
	}

	public void visit( OWLSameIndividualsAxiom node ) throws OWLException
    {
	logger.debug("Default SameIndividuals");
	return;
    }

	public void visit( OWLSubClassAxiom node ) throws OWLException
    {
	logger.debug("Default SubClass");
	return;
    }

	public void visit( OWLSubPropertyAxiom node ) throws OWLException
    {
	logger.debug("Default SubProperty");
	return;
    }

	public void visit(OWLSymmetricPropertyAxiom node) throws OWLException {
		logger.debug("Default SymmetricPropertyAxiom");
		return;
	}

	public void visit(OWLTransitivePropertyAxiom node) throws OWLException {
		logger.debug("Default TransitivePropertyAxiom");
		return;
	}
    
//     public void visit( OWLThing node ) throws OWLException
//     {
// 	return;
//     }
    
} // OWLObjectVisitorAdapter



/*
 * ChangeLog
 * $Log: OWLObjectVisitorAdapter.java,v $
 * Revision 1.4  2006/03/28 16:14:45  ronwalf
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
 * Revision 1.3  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/07/09 12:07:47  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.8  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.7  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.6  2003/04/10 12:10:56  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.5  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.4  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.3  2003/02/06 10:27:33  seanb
 * Replacing OWLThing and OWLNothing with specific instances of OWLClass.
 *
 * Revision 1.2  2003/01/29 16:10:52  seanb
 * Changes to support Anonymous Individuals.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 *
 */
