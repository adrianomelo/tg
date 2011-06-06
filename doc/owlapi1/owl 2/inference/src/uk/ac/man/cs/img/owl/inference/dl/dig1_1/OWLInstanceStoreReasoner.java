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
 * Filename           $RCSfile: OWLInstanceStoreReasoner.java,v $
 * Revision           $Revision: 1.10 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/
package uk.ac.man.cs.img.owl.inference.dl.dig1_1;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kr.dl.dig.reasoner.ReasonerException;
import org.kr.dl.dig.reasoner.v1_1.TReasoner;
import org.kr.dl.dig.v1_1.Concept;
import org.semanticweb.owl.inference.OWLClassReasoner;
import org.semanticweb.owl.inference.OWLIndividualReasoner;
import org.semanticweb.owl.io.abstract_syntax.AbstractOWLParsingException;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.validation.SpeciesValidator;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;

import uk.ac.man.cs.img.instancestore.IndividualsIterator;
import uk.ac.man.cs.img.instancestore.InstanceStoreAssertionException;
import uk.ac.man.cs.img.instancestore.InstanceStoreCreationException;
import uk.ac.man.cs.img.instancestore.InstanceStoreDatabase;
import uk.ac.man.cs.img.instancestore.InstanceStoreFactory;
import uk.ac.man.cs.img.instancestore.InstanceStoreReasonerException;
import uk.ac.man.cs.img.instancestore.InstanceStoreRetrievalException;
import uk.ac.man.cs.img.instancestore.IteratorClosingException;
import uk.ac.man.cs.img.instancestore.OWLInstanceStore;
import uk.ac.man.cs.img.instancestore.dig.v1_1.DIGReasoner;
import uk.ac.man.cs.img.instancestore.dig.v1_1.DIGReasonerNullKBException;
import uk.ac.man.cs.img.instancestore.owl.OWLInstanceStoreAbstractProperties;
import uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor;
import uk.ac.man.cs.img.owl.io.dig1_1.DescriptionsRenderer;

/**
 * An OWL reasoner using a DIG 1.1 reasoner for TBox and an <a
 * href="http://instancestore.man.ac.uk">instance Store </a> (iS) for role-free
 * ABox.
 * <p>
 * Construted starting from an
 * {@link uk.ac.man.cs.img.instancestore.OWLInstanceStore}, passed either as an
 * object or in terms of properties (see the Javadoc of
 * {@link uk.ac.man.cs.img.instancestore.owl.OWLInstanceStoreAbstractProperties}
 * for an example). Inherits the TBox reasoner (accessible through the
 * corresponding getter) from the iS.
 * </p>
 * <p>
 * The ontology is inherited from the iS and has to be treated as a read-only
 * ontology. Therefore {@link #setOntology(OWLOntology)}throws
 * {@link uk.ac.man.cs.img.owl.inference.dl.dig1_1.OperationNotSupportedException}.
 * If the ontology contains individuals these will be separated out and asserted
 * into the iS, while the TBox is passed to the TBox reasoner. After that, new
 * individuals can be added only using the method
 * {@link #addIndividual(URI, OWLDescription)}and will be stored in the iS.
 * </p>
 * 
 * @author Daniele Turi
 * @version $Id: OWLInstanceStoreReasoner.java,v 1.5 2004/07/05 16:59:54 dturi
 *          Exp $
 */
public class OWLInstanceStoreReasoner implements OWLClassReasoner,
        OWLIndividualReasoner {

    /**
     * Log4J Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(OWLInstanceStoreReasoner.class);

    private OWLInstanceStore iS;

    /**
     * Creates a new <code>iS</code> using <code>iSProperties</code> and
     * initialises <code>owlOntology</code> using <code>iS</code>' ontology
     * if not <code>null</code>.
     * 
     * @param iSProperties
     * @throws InstanceStoreCreationException
     *             if an error occurs when creating <code>iS</code>.
     * @throws OWLException
     *             if an error occurs.
     */
    public OWLInstanceStoreReasoner(
            OWLInstanceStoreAbstractProperties iSProperties)
            throws InstanceStoreCreationException, OWLException {
        this(InstanceStoreFactory.getOWLInstance(iSProperties));
    }

    /**
     * Creates a new <code>iS</code> using <code>iSPropertiesFile</code> and
     * initialises <code>owlOntology</code> using <code>iS</code>' ontology
     * if not <code>null</code>.
     * 
     * @param iSPropertiesFile
     *            a File containing iS properties.
     * @throws InstanceStoreCreationException
     *             if an error occurs when creating <code>iS</code>.
     * @throws OWLException
     *             if an error occurs.
     */
    public OWLInstanceStoreReasoner(File iSPropertiesFile)
            throws InstanceStoreCreationException, OWLException {
        this(InstanceStoreFactory.getOWLInstance(iSPropertiesFile));
    }

    /**
     * Creates a new <code>iS</code> using <code>iSPropertiesStream</code>
     * and initialises <code>owlOntology</code> using <code>iS</code>'
     * ontology if not <code>null</code>.
     * 
     * @param iSPropertiesStream
     *            an {@link InputStream}containing iS properties.
     * @throws InstanceStoreCreationException
     *             if an error occurs when creating <code>iS</code>.
     * @throws OWLException
     *             if an error occurs.
     */
    public OWLInstanceStoreReasoner(InputStream iSPropertiesStream)
            throws InstanceStoreCreationException, OWLException {
        this(InstanceStoreFactory.getOWLInstance(iSPropertiesStream));
    }

    /**
     * Sets <code>iS</code> and initialises <code>owlOntology</code> using
     * <code>iS</code>' ontology if not <code>null</code>.
     * 
     * @param iS
     *            an {@link OWLInstanceStore}.
     * @throws OWLException
     *             if an error occurs.
     */
    public OWLInstanceStoreReasoner(OWLInstanceStore iS) throws OWLException {
        this.iS = iS;
        if (iS.getOntology() != null)
            owlOntology = iS.getOntology();
        iSOntology = new OWLInstanceStoreOntology();

        validator = new uk.ac.man.cs.img.owl.validation.SpeciesValidator();
        /* Quiet validator */
        validator.setReporter(new SpeciesValidatorReporter() {

            public void ontology(OWLOntology onto) {
            }

            public void done(String str) {
            }

            public void message(String str) {
            }

            public void explain(int l, String str) {
            }

            public void explain(int l, int code, String str) {
            }
        });
    }

    /* The ontology being reasoned over */
    private OWLOntology owlOntology;

    private OWLInstanceStoreOntology iSOntology;

    /* For rendering DIG concepts */
    private AbstractDescriptionRenderingVisitor renderer;

    /* For checking the species */
    private SpeciesValidator validator;

    /**
     * Operation not supported.
     * 
     * @throws OperationNotSupportedException.
     */
    public void setOntology(OWLOntology onto)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException("setOntology");
    }

    /**
     * Returns the {@link TReasoner}corresponding to the {@link DIGReasoner}of
     * the <code>iS</code>.
     * 
     * @return the {@link TReasoner}corresponding to the {@link DIGReasoner}of
     *         the <code>iS</code>.
     */
    public TReasoner getTBoxReasoner() {
        return getISReasoner().getTBoxReasoner();
    }

    /**
     * Returns the {@link DIGReasoner}of the <code>iS</code>.
     * 
     * @return the {@link DIGReasoner}of the <code>iS</code>.
     */
    public DIGReasoner getISReasoner() {
        return (DIGReasoner) iS.getReasoner();
    }

    /**
     * Returns <code>iSOntology</code>-- the {@link OWLInstanceStoreOntology}
     * wrapping the current {@link OWLOntology}.
     * 
     * @return the {@link OWLInstanceStoreOntology}wrapping the current
     *         {@link OWLOntology}.
     */
    public OWLOntology getOntology() {
        return iSOntology;
    }

    /**
     * Parses <code>description</code> into the corresponding
     * {@link OWLDescription}.
     * 
     * @param description
     *            a String containing a description in <a
     *            href="http://owl.man.ac.uk/2003/concrete/latest/">Abstract OWL
     *            syntax </a>.
     * @return the {@link OWLDescription}corresponding to the abstract OWL
     *         <code>description</code>.
     * @throws AbstractOWLParsingException
     *             wrapping exception raised while parsing.
     */
    public OWLDescription parseAbstractOWLDescription(String description)
            throws AbstractOWLParsingException {
        if (logger.isDebugEnabled()) {
            logger.debug("parseAbstractOWLDescription(String description = "
                    + description + ") - start");
        }

        try {
            OWLDescription returnOWLDescription = iS.getParser()
                    .parseDescription(description);
            if (logger.isDebugEnabled()) {
                logger.debug("parseAbstractOWLDescription(String) - end");
            }
            return returnOWLDescription;
        } catch (Exception e) {
            logger.error("parseAbstractOWLDescription(String description = "
                    + description + ")", e);

            throw new AbstractOWLParsingException(e);
        }
    }

    /**
     * Returns true if d1 is a subclass of d2.
     * 
     * @param d1
     *            an {@link OWLDescription}.
     * @param d2
     *            an {@link OWLDescription}.
     * @return true if d1 is a subclass of d2.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException}and
     *             {@link ReasonerException}.
     */
    public boolean isSubClassOf(OWLDescription d1, OWLDescription d2)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("isSubClassOf(OWLDescription d1 = " + d1
                    + ", OWLDescription d2 = " + d2 + ") - start");
        }

        try {
            boolean isSubsumption = getTBoxReasoner().subsumes(
                    DescriptionsRenderer.owlToDigConcept(d2),
                    DescriptionsRenderer.owlToDigConcept(d1),
                    getISReasoner().getKbURI());
            if (logger.isDebugEnabled()) {
                logger
                        .debug("isSubClassOf(OWLDescription, OWLDescription) - end");
            }
            return isSubsumption;
        } catch (DIGReasonerNullKBException e) {
            logger.error("isSubClassOf(OWLDescription d1 = " + d1
                    + ", OWLDescription d2 = " + d2 + ")", e);

            throw new OWLException(e);
        } catch (ReasonerException e) {
            logger.error("isSubClassOf(OWLDescription d1 = " + d1
                    + ", OWLDescription d2 = " + d2 + ")", e);

            throw new OWLException(e);
        }
    }

    /**
     * Returns true if d1 is equivalent to d2.
     * 
     * @param d1
     *            an {@link OWLDescription}.
     * @param d2
     *            an {@link OWLDescription}.
     * @return true if d1 is a subclass of d2.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException}and
     *             {@link ReasonerException}.
     */
    public boolean isEquivalentClass(OWLDescription d1, OWLDescription d2)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("isEquivalentClass(OWLDescription d1 = " + d1
                    + ", OWLDescription d2 = " + d2 + ") - start");
        }

        boolean isEquivalence = (isSubClassOf(d1, d2) & isSubClassOf(d2, d1));
        if (logger.isDebugEnabled()) {
            logger
                    .debug("isEquivalentClass(OWLDescription, OWLDescription) - end");
        }
        return isEquivalence;
    }

    /**
     * Returns true if <code>description</code> is consistent.
     * 
     * @param description
     *            an {@link OWLDescription}.
     * @return true if <code>description</code> is consistent.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException}and
     *             {@link ReasonerException}.
     */
    public boolean isConsistent(OWLDescription description) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("isConsistent(OWLDescription description = "
                    + description + ") - start");
        }

        try {
            boolean isConsistency = getTBoxReasoner().satisfiable(
                    DescriptionsRenderer.owlToDigConcept(description),
                    getISReasoner().getKbURI());
            if (logger.isDebugEnabled()) {
                logger.debug("isConsistent(OWLDescription) - end");
            }
            return isConsistency;
        } catch (DIGReasonerNullKBException e) {
            logger.error("isConsistent(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (ReasonerException e) {
            logger.error("isConsistent(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        }
    }

    /**
     * Returns true if the current ontology is consistent.
     * 
     * @return true if the current ontology is consistent.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException}and
     *             {@link ReasonerException}.
     */
    public boolean isConsistent() throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("isConsistent() - start");
        }

        Concept top = Concept.Factory.newInstance();
        top.addNewTop();
        try {
            boolean isConsistency = getTBoxReasoner().satisfiable(top,
                    getISReasoner().getKbURI());
            if (logger.isDebugEnabled()) {
                logger.debug("isConsistent() - end");
            }
            return isConsistency;
        } catch (DIGReasonerNullKBException e) {
            logger.error("isConsistent()", e);

            throw new OWLException(e);
        } catch (ReasonerException e) {
            logger.error("isConsistent()", e);

            throw new OWLException(e);
        }
    }

    /**
     * Returns the collection of (named) most specific superclasses of the
     * <code>description</code>.
     * 
     * @return the Set of {@link OWLClass}es which are direct supers of
     *         <code>description</code>.
     * @param description
     *            an {@link OWLDescription}.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException},
     *             {@link URISyntaxException}and {@link ReasonerException}.
     */
    public Set superClassesOf(OWLDescription description) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("superClassesOf(OWLDescription description = "
                    + description + ") - start");
        }

        try {
            Set supers = new HashSet();
            Set parentsNames = getTBoxReasoner().parentsNames(
                    DescriptionsRenderer.owlToDigConcept(description),
                    getISReasoner().getKbURI());
            for (Iterator iter = parentsNames.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                if (element.equals(TReasoner.TOP))
                    supers.add(owlOntology.getClass(new URI(
                            OWLVocabularyAdapter.INSTANCE.getThing())));
                else
                    supers.add(owlOntology.getClass(new URI(element)));
            }

            if (logger.isDebugEnabled()) {
                logger.debug("superClassesOf(OWLDescription) - end");
            }
            return supers;
        } catch (DIGReasonerNullKBException e) {
            logger.error("superClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (ReasonerException e) {
            logger.error("superClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (URISyntaxException e) {
            logger.error("superClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        }
    }

    /**
     * Returns the collection of (named) most general subclasses of the
     * <code>description</code>.
     * 
     * @return the Set of {@link OWLClass}es which are direct subs of
     *         <code>description</code>.
     * @param description
     *            an {@link OWLDescription}.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException},
     *             {@link URISyntaxException}and {@link ReasonerException}.
     */
    public Set subClassesOf(OWLDescription description) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("subClassesOf(OWLDescription description = "
                    + description + ") - start");
        }

        try {
            Set subs = new HashSet();
            Set childrenNames = getTBoxReasoner().childrenNames(
                    DescriptionsRenderer.owlToDigConcept(description),
                    getISReasoner().getKbURI());
            for (Iterator iter = childrenNames.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                if (element.equals(TReasoner.BOTTOM))
                    subs.add(owlOntology.getClass(new URI(
                            OWLVocabularyAdapter.INSTANCE.getNothing())));
                else
                    subs.add(owlOntology.getClass(new URI(element)));
            }

            if (logger.isDebugEnabled()) {
                logger.debug("subClassesOf(OWLDescription) - end");
            }
            return subs;
        } catch (DIGReasonerNullKBException e) {
            logger.error("subClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (ReasonerException e) {
            logger.error("subClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (URISyntaxException e) {
            logger.error("subClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        }
    }

    /**
     * Returns the collection of (named) ancestor classes of the
     * <code>description</code>.
     * 
     * @return the Set of {@link OWLClass}es which are ancestors of
     *         <code>description</code>.
     * @param description
     *            an {@link OWLDescription}.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException},
     *             {@link URISyntaxException}and {@link ReasonerException}.
     */
    public Set ancestorClassesOf(OWLDescription description)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("ancestorClassesOf(OWLDescription description = "
                    + description + ") - start");
        }

        try {
            Set ancestors = new HashSet();
            Set ancestorsNames = getTBoxReasoner().ancestorsNames(
                    DescriptionsRenderer.owlToDigConcept(description),
                    getISReasoner().getKbURI());
            for (Iterator iter = ancestorsNames.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                if (element.equals(TReasoner.TOP))
                    ancestors.add(owlOntology.getClass(new URI(
                            OWLVocabularyAdapter.INSTANCE.getThing())));
                else
                    ancestors.add(owlOntology.getClass(new URI(element)));
            }

            if (logger.isDebugEnabled()) {
                logger.debug("ancestorClassesOf(OWLDescription) - end");
            }
            return ancestors;
        } catch (DIGReasonerNullKBException e) {
            logger.error("ancestorClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (ReasonerException e) {
            logger.error("ancestorClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (URISyntaxException e) {
            logger.error("ancestorClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        }
    }

    /**
     * Returns the collection of (named) descendant classes of the
     * <code>description</code>.
     * 
     * @return the Set of {@link OWLClass}es which are descendants of
     *         <code>description</code>.
     * @param description
     *            an {@link OWLDescription}.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException},
     *             {@link URISyntaxException}and {@link ReasonerException}.
     */
    public Set descendantClassesOf(OWLDescription description)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("descendantClassesOf(OWLDescription description = "
                    + description + ") - start");
        }

        try {
            Set descendants = new HashSet();
            Set descendantsNames = getTBoxReasoner().descendantsNames(
                    DescriptionsRenderer.owlToDigConcept(description),
                    getISReasoner().getKbURI());
            for (Iterator iter = descendantsNames.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                if (element.equals(TReasoner.BOTTOM))
                    descendants.add(owlOntology.getClass(new URI(
                            OWLVocabularyAdapter.INSTANCE.getNothing())));
                else
                    descendants.add(owlOntology.getClass(new URI(element)));
            }

            if (logger.isDebugEnabled()) {
                logger.debug("descendantClassesOf(OWLDescription) - end");
            }
            return descendants;
        } catch (DIGReasonerNullKBException e) {
            logger.error("descendantClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (ReasonerException e) {
            logger.error("descendantClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (URISyntaxException e) {
            logger.error("descendantClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        }
    }

    /**
     * Returns the collection of (named) equivalent classes of the
     * <code>description</code>.
     * 
     * @return the Set of {@link OWLClass}es which are equivalents of
     *         <code>description</code>.
     * @param description
     *            an {@link OWLDescription}.
     * @throws OWLException
     *             wraps {@link DIGReasonerNullKBException},
     *             {@link URISyntaxException}and {@link ReasonerException}.
     */
    public Set equivalentClassesOf(OWLDescription description)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("equivalentClassesOf(OWLDescription description = "
                    + description + ") - start");
        }

        try {
            Set equivalents = new HashSet();
            Set equivalentsNames = getTBoxReasoner().equivalentsNames(
                    DescriptionsRenderer.owlToDigConcept(description),
                    getISReasoner().getKbURI());
            for (Iterator iter = equivalentsNames.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                if (element.equals(TReasoner.TOP))
                    equivalents.add(owlOntology.getClass(new URI(
                            OWLVocabularyAdapter.INSTANCE.getThing())));
                else if (element.equals(TReasoner.BOTTOM))
                    equivalents.add(owlOntology.getClass(new URI(
                            OWLVocabularyAdapter.INSTANCE.getNothing())));
                else
                    equivalents.add(owlOntology.getClass(new URI(element)));
            }

            if (logger.isDebugEnabled()) {
                logger.debug("equivalentClassesOf(OWLDescription) - end");
            }
            return equivalents;
        } catch (DIGReasonerNullKBException e) {
            logger.error("equivalentClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (ReasonerException e) {
            logger.error("equivalentClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (URISyntaxException e) {
            logger.error("equivalentClassesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        }
    }

    /**
     * Returns <code>true</code> if <code>i</code> is an instance of <code>d</code>.
     * Only works if <code>i</code> is an {@link OWLInstanceStoreIndividual},
     * otherwise it throws.
     * @param i an {@link OWLInstanceStoreIndividual}.
     * @param d an {@link OWLDescription}.
     * @throws OWLException if <code>i</code> is not an {@link OWLInstanceStoreIndividual},
     * @see org.semanticweb.owl.inference.OWLIndividualReasoner#isInstanceOf(org.semanticweb.owl.model.OWLIndividual,
     *      org.semanticweb.owl.model.OWLDescription)
     */
    public boolean isInstanceOf(OWLIndividual i, OWLDescription d)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("isInstanceOf(OWLIndividual i = " + i.getURI()
                    + ", OWLDescription d = " + d + ") - start");
        }

        if (!(i instanceof OWLInstanceStoreIndividual))
            throw new OWLException("Individual " + i.getURI()
                    + " not an OWLInstanceStoreIndividual");
        boolean result = isSubClassOf(((OWLInstanceStoreIndividual) i)
                .getDescription(), d);
        if (logger.isDebugEnabled()) {
            logger.debug("isInstanceOf(OWLIndividual, OWLDescription) - end");
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.semanticweb.owl.inference.OWLIndividualReasoner#instancesOf(org.semanticweb.owl.model.OWLDescription)
     */
    public Set instancesOf(OWLDescription description) throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("instancesOf(OWLDescription description = "
                    + description + ") - start");
        }

        try {
            IndividualsIterator individualsIterator = iS.retrieve(description);
            Set iSIndividuals = iStoreIndividualsToOWLIndividuals(individualsIterator);

            if (logger.isDebugEnabled()) {
                logger.debug("instancesOf(OWLDescription) - end");
            }
            return iSIndividuals;
        } catch (IteratorClosingException e) {
            logger.error("instancesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (InstanceStoreRetrievalException e) {
            logger.error("instancesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        } catch (URISyntaxException e) {
            logger.error("instancesOf(OWLDescription description = "
                    + description + ")", e);

            throw new OWLException(e);
        }
    }

    /**
     * Adds the assertion <code>(individual, description)</code> to iS via
     * omonymous method in {@link OWLInstanceStoreOntology}.
     * 
     * @param individual
     *            a URI.
     * @param description
     *            an OWLDescription.
     * @throws OWLException
     *             wraps {@link InstanceStoreAssertionException}.
     */
    public void addIndividual(URI individual, OWLDescription description)
            throws OWLException {
        if (logger.isDebugEnabled()) {
            logger.debug("addIndividual(URI individual = " + individual
                    + ", OWLDescription description = " + description
                    + ") - start");
        }

        iSOntology.addIndividual(individual, description);

        if (logger.isDebugEnabled()) {
            logger.debug("addIndividual(URI, OWLDescription) - end");
        }
    }

    /**
     * Transform the iS individuals in <code>individualsIterator</code> into a
     * Set of {@link OWLInstanceStoreReasoner.OWLInstanceStoreIndividual}s.
     * 
     * @param individualsIterator
     * @return the Set of
     *         {@link OWLInstanceStoreReasoner.OWLInstanceStoreIndividual}s
     *         corresponding to <code>individualsIterator</code>.
     * @throws OWLException
     *             if an error occurs while constructing an
     *             {@link OWLInstanceStoreReasoner.OWLInstanceStoreIndividual}.
     * @throws URISyntaxException
     *             if any of the individuals is not a URI.
     * @throws IteratorClosingException
     *             if an error occurs while closing
     *             <code>individualsIterator</code>.
     */
    Set iStoreIndividualsToOWLIndividuals(
            IndividualsIterator iSindividualsIterator) throws OWLException,
            URISyntaxException, IteratorClosingException {
        if (logger.isDebugEnabled()) {
            logger
                    .debug("iStoreIndividualsToOWLIndividuals(IndividualsIterator iSindividualsIterator = "
                            + iSindividualsIterator + ") - start");
        }

        Set owlIndividuals = new HashSet();
        while (iSindividualsIterator.hasNext())
            owlIndividuals.add(new OWLInstanceStoreIndividual(new URI(
                    iSindividualsIterator.nextIndividual()
                            .toStringRepresentation())));
        iSindividualsIterator.close();

        if (logger.isDebugEnabled()) {
            logger
                    .debug("iStoreIndividualsToOWLIndividuals(IndividualsIterator) - end");
        }
        return owlIndividuals;
    }

    public void release() throws SQLException, InstanceStoreReasonerException {
        if (logger.isDebugEnabled()) {
            logger.debug("release() - start");
        }

        ((InstanceStoreDatabase) iS.getBackingStore()).disconnect();
        iS.getReasoner().clearKB();

        if (logger.isDebugEnabled()) {
            logger.debug("release() - end");
        }
    }

    public class ExpressivenessOutOfScopeException extends OWLException {

        ExpressivenessOutOfScopeException(String m) {
            super(m);
        }
    }

    /**
     * Wraps the {@link OWLOntology}of the containing
     * {@link OWLInstanceStoreReasoner}. Most methods are delegated to the
     * wrapped ontology, but for methods involving individuals:
     * {@link #getIndividuals()}and {@link #getIndividual(URI)}are delegated
     * to <code>iS</code> and so is
     * {@link #addIndividual(URI, OWLDescription)}. Also, the ontology is not
     * mutable and {@link #getIndividualAxioms()}always throws an exception.
     * 
     * @author dturi $Id: OWLInstanceStoreReasoner.java,v 1.5 2004/07/05
     *         16:59:54 dturi Exp $
     */
    public class OWLInstanceStoreOntology implements OWLOntology {

        OWLInstanceStoreOntology() {
        }

        /**
         * Adds the assertion <code>(individual, description)</code> to iS.
         * 
         * @param individual
         *            a URI.
         * @param description
         *            an OWLDescription.
         * @throws OWLException
         *             wraps {@link InstanceStoreAssertionException}.
         */
        public void addIndividual(URI individual, OWLDescription description)
                throws OWLException {
            try {
                iS.addAssertion(individual, description);
            } catch (InstanceStoreAssertionException e) {
                logger.error("addIndividual(URI " + individual.toString()
                        + " )", e);
                throw new OWLException(e.getMessage());
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getClasses()
         */
        public Set getClasses() throws OWLException {
            return owlOntology.getClasses();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getClass(java.net.URI)
         */
        public OWLClass getClass(URI uri) throws OWLException {
            return owlOntology.getClass(uri);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getAnnotationProperties()
         */
        public Set getAnnotationProperties() throws OWLException {
            return owlOntology.getAnnotationProperties();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getAnnotationProperty(java.net.URI)
         */
        public OWLAnnotationProperty getAnnotationProperty(URI uri)
                throws OWLException {
            return owlOntology.getAnnotationProperty(uri);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getDataProperties()
         */
        public Set getDataProperties() throws OWLException {
            return owlOntology.getDataProperties();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getDataProperty(java.net.URI)
         */
        public OWLDataProperty getDataProperty(URI uri) throws OWLException {
            return owlOntology.getDataProperty(uri);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getObjectProperties()
         */
        public Set getObjectProperties() throws OWLException {
            return owlOntology.getObjectProperties();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getObjectProperty(java.net.URI)
         */
        public OWLObjectProperty getObjectProperty(URI uri) throws OWLException {
            return owlOntology.getObjectProperty(uri);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getIndividuals()
         */
        public Set getIndividuals() throws OWLException {
            try {
                IndividualsIterator individualsIterator = iS.retrieveAll();
                return iStoreIndividualsToOWLIndividuals(individualsIterator);
            } catch (IteratorClosingException e) {
                logger.error("OWLInstanceStoreOntology.getIndividuals", e);
                throw new OWLException(e.getMessage());
            } catch (InstanceStoreRetrievalException e) {
                logger.error("OWLInstanceStoreOntology.getIndividuals", e);
                throw new OWLException(e.getMessage());
            } catch (URISyntaxException e) {
                logger.error("OWLInstanceStoreOntology.getIndividuals", e);
                throw new OWLException(e.getMessage());
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getIndividual(java.net.URI)
         */
        public OWLIndividual getIndividual(URI uri) throws OWLException {
            try {
                if (!iS.isAsserted(uri))
                    return null;
            } catch (InstanceStoreRetrievalException e) {
                logger.error("OWLInstanceStoreOntology.getIndividual(URI) uri="
                        + uri.toString(), e);
                throw new OWLException(e.getMessage());
            }
            return new OWLInstanceStoreIndividual(uri);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getDatatypes()
         */
        public Set getDatatypes() throws OWLException {
            return owlOntology.getDatatypes();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getDatatype(java.net.URI)
         */
        public OWLDataType getDatatype(URI uri) throws OWLException {
            return owlOntology.getDatatype(uri);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getClassAxioms()
         */
        public Set getClassAxioms() throws OWLException {
            return owlOntology.getClassAxioms();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getPropertyAxioms()
         */
        public Set getPropertyAxioms() throws OWLException {
            return owlOntology.getPropertyAxioms();
        }

        /**
         * Throws {@link OperationNotSupportedException}.
         * 
         * @throws OperationNotSupportedException.
         */
        public Set getIndividualAxioms() throws OWLException {
            throw new OperationNotSupportedException("getIndividualAxiom");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getIncludedOntologies()
         */
        public Set getIncludedOntologies() throws OWLException {
            return owlOntology.getIncludedOntologies();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getPriorVersion()
         */
        public Set getPriorVersion() throws OWLException {
            return owlOntology.getPriorVersion();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getBackwardCompatibleWith()
         */
        public Set getBackwardCompatibleWith() throws OWLException {
            return owlOntology.getBackwardCompatibleWith();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getIncompatibleWith()
         */
        public Set getIncompatibleWith() throws OWLException {
            return owlOntology.getIncompatibleWith();
        }

        /**
         * Returns <code>false</code>.
         * 
         * @return <code>false</code>.
         */
        public boolean isMutable() throws OWLException {
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getLogicalURI()
         */
        public URI getLogicalURI() throws OWLException {
            return owlOntology.getLogicalURI();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLOntology#getPhysicalURI()
         */
        public URI getPhysicalURI() throws OWLException {
            return owlOntology.getPhysicalURI();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLNamedObject#getURI()
         */
        public URI getURI() throws OWLException {
            return owlOntology.getURI();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLObject#getContainedObjects()
         */
        public OWLObject[] getContainedObjects() throws OWLException {
            return owlOntology.getContainedObjects();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLObject#getOWLDataFactory()
         */
        public OWLDataFactory getOWLDataFactory() throws OWLException {
            return owlOntology.getOWLDataFactory();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLObject#getOWLConnection()
         */
        public OWLConnection getOWLConnection() throws OWLException {
            return owlOntology.getOWLConnection();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLObject#accept(org.semanticweb.owl.model.OWLObjectVisitor)
         */
        public void accept(OWLObjectVisitor visitor) throws OWLException {
            owlOntology.accept(visitor);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLObject#getMetadata()
         */
        public Map getMetadata() throws OWLException {
            return owlOntology.getMetadata();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLObject#getAnnotations()
         */
        public Set getAnnotations() throws OWLException {
            return owlOntology.getAnnotations();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLObject#getAnnotations(org.semanticweb.owl.model.OWLOntology)
         */
        public Set getAnnotations(OWLOntology o) throws OWLException {
            return owlOntology.getAnnotations(o);
        }

        public java.lang.Object clone() {
            return owlOntology.clone();
        }

    }

    /**
     * Ad-hoc implementation of {@link OWLIndividual}using the <code>iS</code>.
     * The (only) constructor takes a URI as argument and constructs an
     * OWLIndividual whose description is retrieved from <code>iS</code> at
     * construction time. The only ontology supported is that of the containing
     * {@link OWLInstanceStoreReasoner}.
     * 
     * @author dturi $Id: OWLInstanceStoreReasoner.java,v 1.5 2004/07/05
     *         16:59:54 dturi Exp $
     */
    public class OWLInstanceStoreIndividual implements OWLIndividual {

        URI uri;

        OWLDescription description;

        /**
         * Returns the description.
         * 
         * @return the description.
         */
        public OWLDescription getDescription() {
            return description;
        }

        /**
         * Sets the <code>uri</code> and initialises <code>description</code>
         * retrieving the assertion corresponding to the individual uri from
         * <code>iS</code>
         * 
         * @param uri
         * @throws OWLException
         *             wraps {@link InstanceStoreRetrievalException}which might
         *             be thrown, eg, if <code>uri</code> is not in
         *             <code>iS</code>.
         */
        public OWLInstanceStoreIndividual(URI uri) throws OWLException {
            this.uri = uri;
            try {
                description = iS.retrieveOWLDescription(uri);
            } catch (InstanceStoreRetrievalException e) {
                logger.error(
                        "OWLInstanceStoreOntology.OWLInstanceStoreIndividual(URI) uri="
                                + uri.toString(), e);
                throw new OWLException(e);
            }
        }

        /**
         * Returns <code>false</code>.
         * 
         * @see org.semanticweb.owl.model.OWLIndividual#isAnonymous()
         */
        public boolean isAnonymous() throws OWLException {
            return false;
        }

        /**
         * If <code>ontology</code> is equal to <code>iSOntology</code> then
         * it returns the {@link OWLDescription}associated to itself (as a
         * singleton Set). Otherwise it simply returns <code>null</code>.
         * 
         * @param ontology
         *            an OWLOntology -- really only <code>iSOntology</code>
         *            allowed.
         * @return the singleton Set consisting of <code>description</code>.
         * @see org.semanticweb.owl.model.OWLIndividual#getTypes(org.semanticweb.owl.model.OWLOntology)
         */
        public Set getTypes(OWLOntology ontology) throws OWLException {
            if (!ontology.equals(iSOntology))
                return null;
            HashSet set = new HashSet();
            set.add(description);
            return set;
        }

        /**
         * Throws OperationNotSupportedException.
         * 
         * @throws OperationNotSupportedException.
         */
        public Set getTypes(Set ontologies)
                throws OperationNotSupportedException {
            throw new OperationNotSupportedException("getTypes(Set)");
        }

        /**
         * Returns an empty Map.
         * 
         * @see org.semanticweb.owl.model.OWLIndividual#getObjectPropertyValues(org.semanticweb.owl.model.OWLOntology)
         */
        public Map getObjectPropertyValues(OWLOntology o) throws OWLException {
            return new HashMap();
        }

        /**
         * Returns an empty Map.
         * 
         * @see org.semanticweb.owl.model.OWLIndividual#getObjectPropertyValues(java.util.Set)
         */
        public Map getObjectPropertyValues(Set ontologies) throws OWLException {
            return new HashMap();
        }

        /**
         * Returns an empty Map.
         * 
         * @see org.semanticweb.owl.model.OWLIndividual#getDataPropertyValues(org.semanticweb.owl.model.OWLOntology)
         */
        public Map getDataPropertyValues(OWLOntology o) throws OWLException {
            return new HashMap();
        }

        /**
         * Returns an empty Map.
         * 
         * @see org.semanticweb.owl.model.OWLIndividual#getDataPropertyValues(java.util.Set)
         */
        public Map getDataPropertyValues(Set ontologies) throws OWLException {
            return new HashMap();
        }

        /**
         * Returns an empty Map.
         * 
         * @see org.semanticweb.owl.model.OWLIndividual#getIncomingObjectPropertyValues(java.util.Set)
         */
        public Map getIncomingObjectPropertyValues(Set ontologies)
                throws OWLException {
            return new HashMap();
        }

        /**
         * Returns an empty Map.
         * 
         * @see org.semanticweb.owl.model.OWLIndividual#getIncomingObjectPropertyValues(org.semanticweb.owl.model.OWLOntology)
         */
        public Map getIncomingObjectPropertyValues(OWLOntology o)
                throws OWLException {
            return new HashMap();
        }

        /**
         * Implementing specification.
         * 
         * @see org.semanticweb.owl.model.OWLEntity#accept(org.semanticweb.owl.model.OWLEntityVisitor)
         */
        public void accept(OWLEntityVisitor visitor) throws OWLException {
            visitor.visit(this);
        }

        /**
         * Implementing specification.
         * 
         * @see org.semanticweb.owl.model.OWLNamedObject#getURI()
         */
        public URI getURI() throws OWLException {
            return uri;
        }

        /**
         * Returns <code>null</code>.
         * 
         * @see org.semanticweb.owl.model.OWLObject#getContainedObjects()
         */
        public OWLObject[] getContainedObjects() throws OWLException {
            return null;
        }

        /**
         * Returns the data factory of the ontology.
         * 
         * @see org.semanticweb.owl.model.OWLObject#getOWLDataFactory()
         */
        public OWLDataFactory getOWLDataFactory() throws OWLException {
            return owlOntology.getOWLDataFactory();
        }

        /**
         * Returns the connection of the ontology.
         * 
         * @see org.semanticweb.owl.model.OWLObject#getOWLConnection()
         */
        public OWLConnection getOWLConnection() throws OWLException {
            return owlOntology.getOWLConnection();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.semanticweb.owl.model.OWLObject#clone()
         */
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                logger
                        .error(
                                "OWLInstanceStoreReasoner.OWLInstanceStoreIndividual.clone()",
                                e);
                return null; // Poor, but that is how other implementations are
                // done.
            }
        }

        /**
         * Implementing specification.
         * 
         * @see org.semanticweb.owl.model.OWLObject#accept(org.semanticweb.owl.model.OWLObjectVisitor)
         */
        public void accept(OWLObjectVisitor visitor) throws OWLException {
            visitor.visit(this);
        }

        /**
         * Returns <code>null</code>.
         * 
         * @see org.semanticweb.owl.model.OWLObject#getMetadata()
         */
        public Map getMetadata() throws OWLException {
            return null;
        }

        /**
         * Returns <code>null</code>.
         * 
         * @see org.semanticweb.owl.model.OWLObject#getAnnotations()
         */
        public Set getAnnotations() throws OWLException {
            return null;
        }

        /**
         * Returns <code>null</code>.
         * 
         * @see org.semanticweb.owl.model.OWLObject#getAnnotations(org.semanticweb.owl.model.OWLOntology)
         */
        public Set getAnnotations(OWLOntology o) throws OWLException {
            return null;
        }

        /**
         * Returns <code>iSOntology</code> as a singleton Set.
         * 
         * @see org.semanticweb.owl.model.OWLOntologyObject#getOntologies()
         */
        public Set getOntologies() throws OWLException {
            Set singleton = new HashSet();
            singleton.add(iSOntology);
            return singleton;
        }

        /**
         * Operation not supported.
         * 
         * @throws {@link OperationNotSupportedException}.
         */
        public Set getUsage(OWLOntology ontology)
                throws OperationNotSupportedException {
            throw new OperationNotSupportedException("getUsage");
        }

        /**
         * Operation not supported.
         * 
         * @throws {@link OperationNotSupportedException}.
         */
        public Set objectsUsed(OWLOntology ontology)
                throws OperationNotSupportedException {
            throw new OperationNotSupportedException("getUsage");
        }

		/* (non-Javadoc)
		 * @see org.semanticweb.owl.model.OWLIndividual#getAnonId()
		 */
		public URI getAnonId() {
			throw new RuntimeException("Not Implemented");
			//return null;
		}
    }
}