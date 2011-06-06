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
 * Filename           $RCSfile: ClassAxiomRenderingVisitor.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/05/05 15:49:36 $
 *               by   $Author: dturi $
 ****************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import org.apache.log4j.Logger;
import org.kr.dl.dig.v1_1.ConceptPair;
import org.kr.dl.dig.v1_1.Concepts;
import org.kr.dl.dig.v1_1.TellsDocument;
import org.semanticweb.owl.model.OWLClassAxiomVisitor;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLSubClassAxiom;

/**
 * Renders OWL class axioms as DIG 1.1.
 * 
 * @author Daniele Turi
 * $Id: ClassAxiomRenderingVisitor.java,v 1.1 2004/05/05 15:49:36 dturi Exp $
 */
public class ClassAxiomRenderingVisitor implements OWLClassAxiomVisitor {

	/**
	 * Log4J Logger for this class
	 */
	public static final Logger logger = Logger
			.getLogger(ClassAxiomRenderingVisitor.class);
	TellsDocument.Tells tells;

	/**
	 * Creates a new visitor and sets <code>tells</code>.
	 * 
	 * @param tells
	 *            a {@link TellsDocument.Tells}.
	 */
	public ClassAxiomRenderingVisitor(TellsDocument.Tells tells) {
		this.tells = tells;
	}

	
	/**
	 * Adds 'disjoint' axioms to <code>tells</code> corresponding to
     * <code>axiom</code>.
     * @param axiom an {@link OWLDisjointClassesAxiom}.
	 * @see org.semanticweb.owl.model.OWLClassAxiomVisitor#visit(org.semanticweb.owl.model.OWLDisjointClassesAxiom)
	 */
	public void visit(OWLDisjointClassesAxiom axiom) throws OWLException {
		if (logger.isDebugEnabled()) {
			logger.debug("visit(OWLDisjointClassesAxiom axiom = " + axiom
					+ ") - start");
		}
		Object[] disjs = axiom.getDisjointClasses().toArray();
		for (int i = 0; i < disjs.length; i++) {
			for (int j = i + 1; j < disjs.length; j++) {
				OWLDescription desc1 = (OWLDescription) disjs[i];
				OWLDescription desc2 = (OWLDescription) disjs[j];
				Concepts concepts = tells.addNewDisjoint();
				ConceptsRenderingVisitor v = new ConceptsRenderingVisitor(
						concepts);
				desc1.accept(v);
				desc2.accept(v);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("visit(OWLDisjointClassesAxiom) - end");
		}
	}

	
    /**
     * Adds 'equalc' axioms to <code>tells</code> corresponding to
     * <code>axiom</code>.
     * @param axiom an {@link OWLEquivalentClassesAxiom}.
     * @see org.semanticweb.owl.model.OWLClassAxiomVisitor#visit(org.semanticweb.owl.model.OWLEquivalentClassesAxiom)
     */
	public void visit(OWLEquivalentClassesAxiom axiom) throws OWLException {
		if (logger.isDebugEnabled()) {
			logger.debug("visit(OWLEquivalentClassesAxiom axiom = " + axiom
					+ ") - start");
		}
		Object[] eqs = axiom.getEquivalentClasses().toArray();
		for (int i = 0; i < eqs.length; i++) {
			for (int j = i + 1; j < eqs.length; j++) {
				OWLDescription desc1 = (OWLDescription) eqs[i];
				OWLDescription desc2 = (OWLDescription) eqs[j];
				ConceptPair conceptPair = tells.addNewEqualc();
				ConceptPairRenderingVisitor v = new ConceptPairRenderingVisitor(
						conceptPair);
				desc1.accept(v);
				desc2.accept(v);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("visit(OWLEquivalentClassesAxiom) - end");
		}
	}

    /**
     * Adds an 'impliesc' axiom to <code>tells</code> corresponding to
     * <code>axiom</code>.
     * @param axiom an {@link OWLSubClassClassAxiom}.
     * @see org.semanticweb.owl.model.OWLClassAxiomVisitor#visit(org.semanticweb.owl.model.OWLSubClassClassAxiom)
     */
	public void visit(OWLSubClassAxiom axiom) throws OWLException {
		if (logger.isDebugEnabled()) {
			logger.debug("visit(OWLSubClassAxiom axiom = " + axiom
					+ ") - start");
		}
		ConceptPair conceptPair = tells.addNewImpliesc();
		ConceptPairRenderingVisitor v = new ConceptPairRenderingVisitor(
				conceptPair);
		axiom.getSubClass().accept(v);
		axiom.getSuperClass().accept(v);
		if (logger.isDebugEnabled()) {
			logger.debug("visit(OWLSubClassAxiom) - end");
		}
	}
}
