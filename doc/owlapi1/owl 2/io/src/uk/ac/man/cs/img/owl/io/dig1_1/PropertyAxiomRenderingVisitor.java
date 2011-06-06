/*
 * Copyright (C) 2003 The University of Manchester 
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
 * Filename           $RCSfile: PropertyAxiomRenderingVisitor.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import org.kr.dl.dig.v1_1.RolePair;
import org.kr.dl.dig.v1_1.TellsDocument;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLEquivalentPropertiesAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;

/**
 * Renders OWL property axioms as DIG 1.1.
 * @author Daniele Turi
 * $Id: PropertyAxiomRenderingVisitor.java,v 1.2 2006/03/28 16:14:45 ronwalf Exp $
 */
public class PropertyAxiomRenderingVisitor implements OWLPropertyAxiomVisitor {

	TellsDocument.Tells tells;

	/**
	 * Creates a new visitor and sets <code>tells</code>.
	 * 
	 * @param tells
	 *            a {@link TellsDocument.Tells}.
	 */
	public PropertyAxiomRenderingVisitor(TellsDocument.Tells tells) {
		this.tells = tells;
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) throws OWLException {
	    // TODO Auto-generated method stub
	    
	}

	/**
	 * Adds 'equalr' axioms to <code>tells</code> corresponding to
	 * <code>axiom</code>.
	 * The addition of each axiom happens only if the two properties 
	 * involved have the same type.
	 * @param axiom an {@link OWLEquivalentPropertiesAxiom}.
	 * 
	 * @see org.semanticweb.owl.model.OWLPropertyAxiomVisitor#visit(org.semanticweb.owl.model.OWLEquivalentPropertiesAxiom)
	 */
	public void visit(OWLEquivalentPropertiesAxiom axiom) throws OWLException {
		Object[] eqs = axiom.getProperties().toArray();
		for (int i = 0; i < eqs.length; i++) {
			for (int j = i + 1; j < eqs.length; j++) {
				OWLProperty prop1 = (OWLProperty) eqs[i];
				OWLProperty prop2 = (OWLProperty) eqs[j];
				RolePair rolePair = tells.addNewEqualr();
				if (prop1 instanceof OWLObjectProperty
						&& prop2 instanceof OWLObjectProperty) {
					rolePair.addNewRatom().setName(prop1.getURI().toString());
					rolePair.addNewRatom().setName(prop2.getURI().toString());
				} else if (prop1 instanceof OWLDataProperty
						&& prop2 instanceof OWLDataProperty) {
					rolePair.addNewAttribute().setName(
							prop1.getURI().toString());
					rolePair.addNewAttribute().setName(
							prop2.getURI().toString());
				}
			}
		}
	}

	public void visit(OWLFunctionalPropertyAxiom axiom) throws OWLException {
	    // TODO Auto-generated method stub
	    
	}

	public void visit(OWLInverseFunctionalPropertyAxiom axiom) throws OWLException {
	    // TODO Auto-generated method stub
	    
	}

	public void visit(OWLInversePropertyAxiom axiom) throws OWLException {
	    // TODO Auto-generated method stub
	    
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) throws OWLException {
	    // TODO Auto-generated method stub
	    
	}

	public void visit(OWLPropertyDomainAxiom axiom) throws OWLException {
	    // TODO Auto-generated method stub
	    
	}

	/**
	 * Adds an 'impliesr' axiom to <code>tells</code> corresponding to
	 * <code>axiom</code>.
	 * The addition of ethe axiom happens only if the two properties 
	 * involved have the same type.
	 * @param axiom an {@link OWLSubPropertyAxiom}.
	 * @see org.semanticweb.owl.model.OWLPropertyAxiomVisitor#visit(org.semanticweb.owl.model.OWLSubPropertyAxiom)
	 */
	public void visit(OWLSubPropertyAxiom axiom) throws OWLException {
        RolePair rolePair = tells.addNewImpliesr();
        OWLProperty sub = axiom.getSubProperty();
        OWLProperty sup = axiom.getSuperProperty();
        if (sub instanceof OWLObjectProperty
                && sup instanceof OWLObjectProperty) {
            rolePair.addNewRatom().setName(sub.getURI().toString());
            rolePair.addNewRatom().setName(sup.getURI().toString());
        } else if (sub instanceof OWLDataProperty
                && sup instanceof OWLDataProperty) {
            rolePair.addNewAttribute().setName(
                    sub.getURI().toString());
            rolePair.addNewAttribute().setName(
                    sup.getURI().toString());
        }
	}

	public void visit(OWLSymmetricPropertyAxiom axiom) throws OWLException {
	    // TODO Auto-generated method stub
	    
	}

	public void visit(OWLTransitivePropertyAxiom axiom) throws OWLException {
	    // TODO Auto-generated method stub
	    
	}
}
