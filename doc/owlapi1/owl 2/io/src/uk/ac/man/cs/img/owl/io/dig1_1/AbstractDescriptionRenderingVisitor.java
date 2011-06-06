/*
 * Copyright (C) 2003 The University of Manchester
 * 
 * Modifications to the initial code base are copyright of their respective
 * authors, or their employers as appropriate. Authorship of the modifications
 * may be determined from the ChangeLog placed at the end of this file.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 ****************************************************************************** 
 * Source code information
 * Filename         $RCSfile: AbstractDescriptionRenderingVisitor.java,v $ 
 * Revision         $Revision: 1.4 $
 * Release status   $State: Exp $ 
 * Last modified on $Date: 2004/06/03 14:51:13 $ 
 *               by $Author: dturi $
 ******************************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import java.math.BigInteger;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.kr.dl.dig.v1_1.Concept;
import org.kr.dl.dig.v1_1.Concepts;
import org.kr.dl.dig.v1_1.Individuals;
import org.kr.dl.dig.v1_1.IntequalsDocument;
import org.kr.dl.dig.v1_1.Named;
import org.kr.dl.dig.v1_1.NumRoleConceptPair;
import org.kr.dl.dig.v1_1.RoleConceptPair;
import org.kr.dl.dig.v1_1.StringequalsDocument;
import org.kr.dl.dig.v1_1.StringequalsDocument.Stringequals;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitor;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLFrame;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNot;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLOr;

/**
 * Abstract superclass for a family of rendering visitors for OWL descriptions.
 * Each concrete subclass needs to implement the <code>addNewXXX()</code>
 * methods.
 * 
 * @author Daniele Turi and Sean Bechhofer $Id:
 *         AbstractDescriptionRenderingVisitor.java,v 1.1 2004/05/05 15:49:36
 *         dturi Exp $
 */
public abstract class AbstractDescriptionRenderingVisitor
        implements
            OWLDescriptionVisitor {

    /**
     * Log4J Logger for this class
     */
    public static final Logger logger = Logger
            .getLogger(AbstractDescriptionRenderingVisitor.class);

    /**
     * Adds a new 'catom' with name given by <code>owlClass</code>.
     * 
     * @param owlClass
     *            an {@link OWLClass}.
     * @throws OWLException.
     */
    public void visit(OWLClass owlClass) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLClass owlClass = " + owlClass + ") - start");
        }
        
        String className = owlClass.getURI().toString();
        if (className.equals(OWLVocabularyAdapter.INSTANCE.getThing())) {
            addNewTop();
            return;
        }
        if (className.equals(OWLVocabularyAdapter.INSTANCE.getNothing())) {
            addNewBottom();
            return;
        }
        Named catom = addNewCatom();
        catom.setName(className);

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLClass) - end");
        }
    }

    /**
     * Adds a new 'and' and populates it using the operands of <code>owlAnd</code>
     * and a corresponding {@link ConceptsRenderingVisitor}.
     * 
     * @param owlAnd
     *            {@link OWLAnd}.
     * @throws OWLException.
     */
    public void visit(OWLAnd owlAnd) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLAnd owlAnd = " + owlAnd + ") - start");
        }

        Concepts andConcept = addNewAnd();
        ConceptsRenderingVisitor v = new ConceptsRenderingVisitor(andConcept);
        for (Iterator it = owlAnd.getOperands().iterator(); it.hasNext();) {
            OWLDescription desc = (OWLDescription) it.next();
            desc.accept(v);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLAnd) - end");
        }
    }

    /**
     * Adds a new 'or' and populates it using the operands of <code>owlOr</code>
     * and a corresponding {@link ConceptsRenderingVisitor}.
     * 
     * @param owlOr
     *            {@link OWLOr}.
     * @throws OWLException.
     */
    public void visit(OWLOr owlOr) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLOr or = " + owlOr + ") - start");
        }

        Concepts orConcept = addNewOr();
        ConceptsRenderingVisitor v = new ConceptsRenderingVisitor(orConcept);
        for (Iterator it = owlOr.getOperands().iterator(); it.hasNext();) {
            OWLDescription desc = (OWLDescription) it.next();
            desc.accept(v);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLOr) - end");
        }
    }

    /**
     * Adds a new 'not' and populates it using the operand of <code>owlNot</code>
     * and a corresponding {@link ConceptRenderingVisitor}.
     * 
     * @param owlNot
     *            {@link OWLNot}.
     * @throws OWLException.
     */
    public void visit(OWLNot owlNot) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLNot not = " + owlNot + ") - start");
        }

        Concept notConcept = addNewNot();
        ConceptRenderingVisitor v = new ConceptRenderingVisitor(notConcept);
        OWLDescription desc = (OWLDescription) owlNot.getOperand();
        desc.accept(v);

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLNot) - end");
        }
    }

    /**
     * Adds a new 'iset' and populates it using the individuals in <code>enumeration</code>.
     * 
     * @param enumeration
     *            {@link OWLEnumeration}.
     * @throws OWLException.
     */
    public void visit(OWLEnumeration enumeration) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLEnumeration enumeration = " + enumeration
                    + ") - start");
        }

        Individuals individuals = addNewIset();
        OWLIndividual indiv;
        for (Iterator it = enumeration.getIndividuals().iterator(); it
                .hasNext();) {
            indiv = (OWLIndividual) it.next();
            individuals.addNewIndividual().setName(indiv.toString());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLEnumeration) - end");
        }
    }

    /**
     * Adds a new 'some' and populates it using <code>restriction</code> and
     * a corresponding {@link RoleConceptPairRenderingVisitor}.
     * 
     * @param restriction
     *            {@link OWLObjectSomeRestriction}.
     * @throws OWLException.
     */
    public void visit(OWLObjectSomeRestriction restriction) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLObjectSomeRestriction restriction = "
                    + restriction + ") - start");
        }

        RoleConceptPair roleConceptPair = addNewSome();
        roleConceptPair.addNewRatom().setName(
                restriction.getObjectProperty().getURI().toString());
        restriction.getDescription().accept(
                new RoleConceptPairRenderingVisitor(roleConceptPair));

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLObjectSomeRestriction) - end");
        }
    }

    /**
     * Adds a new 'all' and populates it using <code>restriction</code> and a
     * corresponding {@link RoleConceptPairRenderingVisitor}.
     * 
     * @param restriction
     *            {@link OWLObjectAllRestriction}.
     * @throws OWLException.
     */
    public void visit(OWLObjectAllRestriction restriction) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLObjectAllRestriction restriction = "
                    + restriction + ") - start");
        }

        RoleConceptPair roleConceptPair = addNewAll();
        roleConceptPair.addNewRatom().setName(
                restriction.getObjectProperty().getURI().toString());
        RoleConceptPairRenderingVisitor v = new RoleConceptPairRenderingVisitor(
                roleConceptPair);
        restriction.getDescription().accept(v);

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLObjectAllRestriction) - end");
        }
    }

    /**
     * Renders OWL's <code>hasValue</code> as DIG's <code>some restriction.getObjectProperty() iset restriction.getIndividual()</code>
     * 
     * @param restriction
     *            {@link OWLObjectValueRestriction}.
     * @throws OWLException.
     */
    public void visit(OWLObjectValueRestriction restriction)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLObjectValueRestriction restriction = "
                    + restriction + ") - start");
        }

        RoleConceptPair roleConceptPair = addNewSome();
        roleConceptPair.addNewRatom().setName(
                restriction.getObjectProperty().getURI().toString());
        roleConceptPair.addNewIset().addNewIndividual().setName(
                restriction.getIndividual().getURI().toString());

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLObjectValueRestriction) - end");
        }
    }

    /**
     * Depending on whether <code>restriction</code> is 'atmost', 'atleast',
     * or 'exactly' it either adds a new 'atmost', or a new 'atleast', or a
     * conjunction of the two and then populates it using <code>restriction</code>
     * and a corresponding {@link RoleConceptPairRenderingVisitor}.
     * 
     * @param restriction
     *            {@link OWLObjectCardinalityRestriction}.
     * @throws OWLException.
     */
    public void visit(OWLObjectCardinalityRestriction restriction)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLObjectCardinalityRestriction restriction = "
                    + restriction + ") - start");
        }

        if (restriction.isExactly()) {
            Concepts concepts = addNewAnd();
            NumRoleConceptPair numRoleConceptPair = concepts.addNewAtmost();
            addAtMost(restriction, numRoleConceptPair);
            numRoleConceptPair = concepts.addNewAtleast();
            addAtLeast(restriction, numRoleConceptPair);
        } else if (restriction.isAtMost()) {
            NumRoleConceptPair numRoleConceptPair = addNewAtmost();
            addAtMost(restriction, numRoleConceptPair);
        } else if (restriction.isAtLeast()) {
            NumRoleConceptPair numRoleConceptPair = addNewAtleast();
            addAtLeast(restriction, numRoleConceptPair);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("visit(OWLObjectCardinalityRestriction) - end");
        }
    }

    /**
     * @param restriction
     * @param numRoleConceptPair
     * @throws OWLException
     */
    protected void addAtLeast(OWLObjectCardinalityRestriction restriction,
            NumRoleConceptPair numRoleConceptPair) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger
                    .debug("addAtLeast(OWLObjectCardinalityRestriction restriction = "
                            + restriction
                            + ", NumRoleConceptPair numRoleConceptPair = "
                            + numRoleConceptPair + ") - start");
        }

        int atLeast = restriction.getAtLeast();
        numRoleConceptPair.setNum(BigInteger.valueOf(atLeast));
        numRoleConceptPair.addNewRatom().setName(
                restriction.getObjectProperty().getURI().toString());
        numRoleConceptPair.addNewTop();

        if (logger.isDebugEnabled()) {
            logger
                    .debug("addAtLeast(OWLObjectCardinalityRestriction, NumRoleConceptPair) - end");
        }
    }

    /**
     * @param restriction
     * @param numRoleConceptPair
     * @throws OWLException
     */
    protected void addAtMost(OWLObjectCardinalityRestriction restriction,
            NumRoleConceptPair numRoleConceptPair) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger
                    .debug("addAtMost(OWLObjectCardinalityRestriction restriction = "
                            + restriction
                            + ", NumRoleConceptPair numRoleConceptPair = "
                            + numRoleConceptPair + ") - start");
        }

        int atMost = restriction.getAtMost();
        numRoleConceptPair.setNum(BigInteger.valueOf(atMost));
        numRoleConceptPair.addNewRatom().setName(
                restriction.getObjectProperty().getURI().toString());
        numRoleConceptPair.addNewTop();

        if (logger.isDebugEnabled()) {
            logger
                    .debug("addAtMost(OWLObjectCardinalityRestriction, NumRoleConceptPair) - end");
        }
    }

    /**
     * Always throws NotExpressableInDIGException. The problem here is that
     * there isn't really an expression in DIG that corresponds to a data
     * cardinality restriction. Ideally we want DIG to allow unqualified
     * cardinality restrictions.
     * 
     * @throws NotExpressableInDIGException
     *             always.
     */
    public void visit(OWLDataCardinalityRestriction restriction)
            throws NotExpressableInDIGException {
        throw new NotExpressableInDIGException(
                "Can't handle Data cardinality restriction. ");
    }

    /**
     * Always throws NotExpressableInDIGException. The problem here is that
     * there isn't really an expression in DIG that corresponds to a data all
     * restriction. In some cases we could translate to a local range but it
     * would be rather unpleasant bespoke code. Instead, we choose to throw an
     * exception here.
     * 
     * @throws NotExpressableInDIGException
     *             always.
     */
    public void visit(OWLDataAllRestriction node)
            throws NotExpressableInDIGException, OWLException {
        throw new NotExpressableInDIGException(
                "Can't handle DataAll restriction\n\t"
                        + node.getDataProperty().getURI() + " "
                        + node.getDataType());
    }

    /**
     * Always throws NotExpressableInDIGException. The problem here is that
     * there isn't really an expression in DIG that corresponds to a data some
     * restriction. In some cases we could translate to a local range but it
     * would be rather unpleasant bespoke code. Instead, we choose to throw an
     * exception here.
     * 
     * @throws NotExpressableInDIGException
     *             always.
     */
    public void visit(OWLDataSomeRestriction node)
            throws NotExpressableInDIGException, OWLException {
        throw new NotExpressableInDIGException(
                "Can't handle DataSome restriction\n\t"
                        + node.getDataProperty().getURI() + " "
                        + node.getDataType());
    }

    /**
     * Always throws NotExpressableInDIGException.
     * 
     * @throws NotExpressableInDIGException
     *             always.
     */
    public void visit(OWLDataValueRestriction node)
            throws NotExpressableInDIGException, OWLException {
        java.net.URI type = node.getValue().getURI();
        if (type == null) {
            stringEquals(node);
        } else if (type
                .toString()
                .equals(
                        org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                .getString())) {
            stringEquals(node);
        } else if (type
                .toString()
                .equals(
                        org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                .getInteger())
                || type.toString().equals(
                        XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInt())) {
            IntequalsDocument.Intequals intequals = addNewIntequals();
            intequals.addNewAttribute().setName(
                    node.getDataProperty().getURI().toString());
            intequals.setVal(new BigInteger(node.getValue().getValue().toString()));
        } else {
            throw new NotExpressableInDIGException(
                    " Can't handle DataValue restriction\n\t"
                            + node.getDataProperty().getURI() + " "
                            + node.getValue());
        }
    }

    void stringEquals(OWLDataValueRestriction node) throws OWLException {
        Stringequals stringequals = addNewStringequals();
        stringequals.addNewAttribute().setName(
                node.getDataProperty().getURI().toString());
        stringequals.setVal(node.getValue().getValue().toString());
    }

    /**
     * Always throws NotExpressableInDIGException.
     * 
     * @throws NotExpressableInDIGException
     *             always.
     */
    public void visit(OWLFrame node) throws NotExpressableInDIGException {
        throw new NotExpressableInDIGException("Can't handle OWLFrames");
    }

    /**
     * Adds <code>top</code>.
     * 
     * @return the {@link XmlObject} corresponding to <code>top</code>.
     */
    public abstract XmlObject addNewTop();
    
    /**
     * Adds <code>bottom</code>.
     * 
     * @return the {@link XmlObject} corresponding to <code>bottom</code>.
     */
    public abstract XmlObject addNewBottom();
    
    /**
     * Adds a new <code>catom</code>.
     * 
     * @return the {@link Named}resulting from adding a new <code>catom</code>.
     */
    public abstract Named addNewCatom();

    /**
     * Adds a new <code>and</code>.
     * 
     * @return the {@link Concepts}resulting from adding a new <code>and</code>.
     */
    public abstract Concepts addNewAnd();

    /**
     * Adds a new <code>or</code>.
     * 
     * @return the {@link Concepts}resulting from adding a new <code>or</code>.
     */
    public abstract Concepts addNewOr();

    /**
     * Adds a new <code>not</code>.
     * 
     * @return the {@link Concept}resulting from adding a new <code>not</code>.
     */
    public abstract Concept addNewNot();

    /**
     * Adds a new <code>iset</code>.
     * 
     * @return the {@link Individuals}resulting from adding a new <code>iset</code>.
     */
    public abstract Individuals addNewIset();

    /**
     * Adds a new <code>some</code>.
     * 
     * @return the {@link RoleConceptPair}resulting from adding a new <code>some</code>.
     */
    public abstract RoleConceptPair addNewSome();

    /**
     * Adds a new <code>all</code>.
     * 
     * @return the {@link RoleConceptPair}resulting from adding a new <code>all</code>.
     */
    public abstract RoleConceptPair addNewAll();

    /**
     * Adds a new <code>atmost</code>.
     * 
     * @return the {@link NumRoleConceptPair}resulting from adding a new
     *         <code>atmost</code>.
     */
    public abstract NumRoleConceptPair addNewAtmost();

    /**
     * Adds a new <code>atleast</code>.
     * 
     * @return the {@link NumRoleConceptPair}resulting from adding a new
     *         <code>atleast</code>.
     */
    public abstract NumRoleConceptPair addNewAtleast();

    /**
     * Adds a new <code>stringequals</code>.
     * 
     * @return the {@link StringequalsDocument.Stringequals}resulting from
     *         adding a new <code>stringequals</code>.
     */
    public abstract StringequalsDocument.Stringequals addNewStringequals();

    /**
     * Adds a new <code>intequals</code>.
     * 
     * @return the {@link IntequalsDocument.Intequals}resulting from adding a
     *         new <code>intequals</code>.
     */
    public abstract IntequalsDocument.Intequals addNewIntequals();
}