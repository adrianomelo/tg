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
 * Filename         $RCSfile: Renderer.java,v $ 
 * Revision         $Revision: 1.5 $
 * Release status   $State: Exp $ 
 * Last modified on $Date: 2004/06/17 08:53:12 $ 
 *               by $Author: dturi $
 ******************************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.kr.dl.dig.v1_1.ConceptPair;
import org.kr.dl.dig.v1_1.RoleConceptPair;
import org.kr.dl.dig.v1_1.RolePair;
import org.kr.dl.dig.v1_1.TellsDocument;
import org.kr.dl.dig.v1_1.InstanceofDocument.Instanceof;
import org.kr.dl.dig.v1_1.RelatedDocument.Related;
import org.kr.dl.dig.v1_1.TellsDocument.Tells;
import org.kr.dl.dig.v1_1.ValueDocument.Value;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;

/**
 * Renders OWL ontologies as <a href="http://dig.sourceforge.net">DIG 1.1 </a>.
 * Note: This renderer does not do anything with imports.
 * 
 * @author Sean Bechhofer and Daniele Turi
 * @version $Id: Renderer.java,v 1.5 2004/06/17 08:53:12 dturi Exp $
 */
public class Renderer implements org.semanticweb.owl.io.Renderer {

    public static final String ANONYMOUS_INDIV = "urn:anonymous#";
    /**
     * Log4J Logger for this class
     */
    public static final Logger logger = Logger.getLogger(Renderer.class);

    public Renderer() {
    }

    /**
     * Empty implementation.
     */
    public void setOptions(Map map) {
    }

    /**
     * Empty implementation.
     * 
     * @return <code>null</code>.
     */
    public Map getOptions() {
        return null;
    }

    /**
     * Translates <code>ontology</code> into a {@link TellsDocument}.
     * 
     * @param ontology
     *            an {@link OWLOntology}.
     * @return the {@link TellsDocument}corresponding to <code>ontology</code>.
     * @throws NotExpressableInDIGException
     *             if <code>ontology</code> contains constructs not
     *             expressable in DIG 1.1.
     * @throws RendererException
     *             wraps any other {@link OWLException}.
     */
    public TellsDocument renderOntology(OWLOntology ontology)
            throws NotExpressableInDIGException, RendererException {
        try {
            TellsDocument tellsDoc = TellsDocument.Factory.newInstance();
            Tells tells = tellsDoc.addNewTells();
            for (Iterator it = uk.ac.man.cs.img.owl.io.dig1_0.Renderer
                    .orderedEntities(ontology.getClasses()).iterator(); it
                    .hasNext();) {
                renderClass(ontology, (OWLClass) it.next(), tells);
            }
            for (Iterator it = uk.ac.man.cs.img.owl.io.dig1_0.Renderer
                    .orderedEntities(ontology.getObjectProperties()).iterator(); it
                    .hasNext();) {
                renderObjectProperty(ontology, (OWLObjectProperty) it.next(),
                        tells);
            }
            for (Iterator it = uk.ac.man.cs.img.owl.io.dig1_0.Renderer
                    .orderedEntities(ontology.getDataProperties()).iterator(); it
                    .hasNext();) {
                renderDataProperty(ontology, (OWLDataProperty) it.next(), tells);
            }
            for (Iterator it = uk.ac.man.cs.img.owl.io.dig1_0.Renderer
                    .orderedEntities(ontology.getIndividuals()).iterator(); it
                    .hasNext();) {
                renderIndividual(ontology, (OWLIndividual) it.next(), tells);
            }
            for (Iterator it = uk.ac.man.cs.img.owl.io.dig1_0.Renderer
                    .orderedEntities(ontology.getDatatypes()).iterator(); it
                    .hasNext();) {
                renderDataType(ontology, (OWLDataType) it.next(), tells);
            }
            for (Iterator it = uk.ac.man.cs.img.owl.io.dig1_0.Renderer
                    .orderedEntities(ontology.getClassAxioms()).iterator(); it
                    .hasNext();) {
                renderClassAxiom((OWLClassAxiom) it.next(), tells);
            }
            for (Iterator it = uk.ac.man.cs.img.owl.io.dig1_0.Renderer
                    .orderedEntities(ontology.getPropertyAxioms()).iterator(); it
                    .hasNext();) {
                renderPropertyAxiom((OWLPropertyAxiom) it.next(), tells);
            }
            for (Iterator it = uk.ac.man.cs.img.owl.io.dig1_0.Renderer
                    .orderedEntities(ontology.getIndividualAxioms()).iterator(); it
                    .hasNext();) {
                renderIndividualAxiom((OWLIndividualAxiom) it.next(), tells);
            }
            return tellsDoc;
        } catch (OWLException ex) {
            logger.error(ex);
            throw new RendererException(ex.getMessage());
        }
    }

    /**
     * Renders <code>ontology</code> as a {@link TellsDocument}which is then
     * saved into <code>writer</code>.
     * 
     * @param ontology
     *            an {@link OWLOntology}.
     * @param writer
     *            the <code>Writer</code> where the rendered ontology is
     *            saved.
     * @throws NotExpressableInDIGException
     *             if <code>ontology</code> contains constructs not
     *             expressable in DIG 1.1.
     * @throws RendererException
     *             wraps any other {@link OWLException}.
     */
    public void renderOntology(OWLOntology ontology, Writer writer)
            throws NotExpressableInDIGException, RendererException {
        try {
            TellsDocument tellsDoc = renderOntology(ontology);
            tellsDoc.save(writer, prettyPrintOptions());
        } catch (IOException e) {
            logger.error(e);
            throw new RendererException(e.getMessage());
        }
    }

    void renderClass(OWLOntology ontology, OWLClass owlClass,
            TellsDocument.Tells tells) throws OWLException {
        String className = owlClass.getURI().toString();
        if (className.equals(OWLVocabularyAdapter.INSTANCE.getThing()))
            return;
        if (className.equals(OWLVocabularyAdapter.INSTANCE.getNothing()))
            return;
        tells.addNewDefconcept().setName(owlClass.getURI().toString());
        OWLDescription nextDescription;
        ConceptPair nextConceptPair;
        ConceptPairRenderingVisitor nextVisitor;
        for (Iterator it = owlClass.getEquivalentClasses(ontology).iterator(); it
                .hasNext();) {
            nextDescription = (OWLDescription) it.next();
            nextConceptPair = tells.addNewEqualc();
            nextConceptPair.addNewCatom().setName(owlClass.getURI().toString());
            nextVisitor = new ConceptPairRenderingVisitor(nextConceptPair);
            nextDescription.accept(nextVisitor);
        }
        for (Iterator it = owlClass.getSuperClasses(ontology).iterator(); it
                .hasNext();) {
            nextDescription = (OWLDescription) it.next();
            nextConceptPair = tells.addNewImpliesc();
            nextConceptPair.addNewCatom().setName(owlClass.getURI().toString());
            nextVisitor = new ConceptPairRenderingVisitor(nextConceptPair);
            nextDescription.accept(nextVisitor);
        }
        OWLDescription eqEnum;
        ConceptPair eqEnumPair;
        for (Iterator it = owlClass.getEnumerations(ontology).iterator(); it
                .hasNext();) {
            nextDescription = (OWLDescription) it.next();
            nextConceptPair = tells.addNewEqualc();
            nextConceptPair.addNewCatom().setName(owlClass.getURI().toString());
            nextVisitor = new ConceptPairRenderingVisitor(nextConceptPair);
            nextDescription.accept(nextVisitor);
        }
    }

    void renderIndividual(OWLOntology ontology, OWLIndividual ind, Tells tells)
            throws OWLException {
        /*
         * If the individual is anonymous and has any incoming properties, then
         * we do not wish to show it here -- it will be rendered during the
         * rendering of the thing that points to it.
         */
        /* This is all a bit tricky if we're doing DIG.... */
        if (ind.isAnonymous()) {
            Map m = ind.getIncomingObjectPropertyValues(ontology);
            if (!m.isEmpty()) {
                return;
            }
        }
        String indReference = null;
        if (ind.isAnonymous()) {
            indReference = anonymIndividual(ind);
        } else {
            indReference = ind.getURI().toString();
        }
        tells.addNewDefindividual().setName(indReference);
        OWLDescription nextDescription;
        Instanceof inst;
        for (Iterator it = ind.getTypes(ontology).iterator(); it.hasNext();) {
            nextDescription = (OWLDescription) it.next();
            inst = tells.addNewInstanceof();
            inst.addNewIndividual().setName(indReference);
            nextDescription.accept(new InstanceofRenderingVisitor(inst));
        }
        Map propertyValues = ind.getObjectPropertyValues(ontology);
        OWLObjectProperty nextProperty;
        OWLIndividual nextIndividual;
        Set vals;
        Related related;
        for (Iterator it = propertyValues.keySet().iterator(); it.hasNext();) {
            nextProperty = (OWLObjectProperty) it.next();
            vals = (Set) propertyValues.get(nextProperty);
            for (Iterator valIt = vals.iterator(); valIt.hasNext();) {
                related = tells.addNewRelated();
                related.addNewIndividual().setName(indReference);
                related.addNewRatom().setName(nextProperty.getURI().toString());
                nextIndividual = (OWLIndividual) valIt.next();
                if (nextIndividual.isAnonymous()) {
                    related.addNewIndividual().setName(
                            anonymIndividual(nextIndividual));
                } else {
                    related.addNewIndividual().setName(
                            nextIndividual.getURI().toString());
                }
            }
        }
        Map dataValues = ind.getDataPropertyValues(ontology);
        OWLDataProperty nextDataProp;
        OWLDataValue nextDataValue;
        boolean isString = false;
        boolean isInteger = false;
        URI nextDataValueURI;
        String uriString;
        for (Iterator it = dataValues.keySet().iterator(); it.hasNext();) {
            nextDataProp = (OWLDataProperty) it.next();
            vals = (Set) dataValues.get(nextDataProp);
            for (Iterator valIt = vals.iterator(); valIt.hasNext();) {
                nextDataValue = (OWLDataValue) valIt.next();
                /* If there's no URI given, assume it's a string */
                nextDataValueURI = nextDataValue.getURI();
                if (nextDataValueURI != null) {
                    uriString = nextDataValueURI.toString();
                    if (uriString
                            .equals(XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                    .getInteger())
                            || uriString
                                    .equals(XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                            .getPositiveInteger())
                            || uriString
                                    .equals(XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                            .getNegativeInteger())
                            || uriString
                                    .equals(XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                            .getNonNegativeInteger())
                            || uriString
                                    .equals(XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                            .getNonPositiveInteger())) {
                        isInteger = true;
                    } else if (uriString
                            .equals(XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                    .getString())) {
                        isString = true;
                    }
                } else
                    isString = true;
                /*
                 * At this point if neither is set, we give up as we've got a
                 * type we can't deal with.
                 */
                if (!(isString || isInteger)) {
                    throw new RendererException("Unknown Datatype: "
                            + nextDataValueURI);
                }
                Value value = tells.addNewValue();
                value.addNewIndividual().setName(indReference);
                /*
                 * There is a problem here -- by default attributes have
                 * integer range, but we're not necessarily handling that
                 * properly here.
                 */
                value.addNewAttribute().setName(
                        nextDataProp.getURI().toString());
                if (isInteger)
                    value.setIval(new BigInteger(nextDataValue.getValue()
                            .toString()));
                if (isString)
                    value.setSval(nextDataValue.getValue().toString());
            }
        }
        /* Don't do these for now! */
        // 	Map dataValues = ind.getDataPropertyValues();
        // 	for ( Iterator it = dataValues.keySet().iterator();
        // 	      it.hasNext(); ) {
        // 	    System.out.println();
        // 	    OWLDataProperty prop = (OWLDataProperty) it.next();
        // 		Set vals = (Set) dataValues.get(prop);
        // 		for (Iterator valIt = vals.iterator(); valIt.hasNext();
        // 		     ) {
        // 		    // System.out.println("QQ: " + ((OWLIndividual)
        // valIt.next()).getURI());
        //  		    OWLDataValue dtv = (OWLDataValue) valIt.next();
        // 		    visitor.reset();
        // 		    dtv.accept( visitor );
        //  		    System.out.println( " value(" +
        // 			      shortForm( prop.getURI() ) + " " +
        // 			      visitor.result() + ")" );
        // 		    if (valIt.hasNext()) {
        // 			System.out.println();
        // 		    }
        // 		}
        // // if (it.hasNext()) {
        // // System.out.println();
        // // }
        // 	    }
        // 	    System.out.println(")");
        // 	}
    }

    /**
     * Returns the String {@link #ANONYMOUS_INDIV}followed by the <code>individual</code>'s
     * hash code.
     * 
     * @param individual
     *            an {@link OWLIndividual}.
     * @return the String {@link #ANONYMOUS_INDIV}followed by the <code>individual</code>'s
     *         hash code.
     */
    static public String anonymIndividual(OWLIndividual individual) {
        return ANONYMOUS_INDIV + individual.hashCode();
    }

    void renderObjectProperty(OWLOntology ontology, OWLObjectProperty prop,
            Tells tells) throws OWLException {
        String propertyName = prop.getURI().toString();
        tells.addNewDefrole().setName(propertyName);
        if (prop.isTransitive(ontology))
            tells.addNewTransitive().addNewRatom().setName(propertyName);
        if (prop.isFunctional(ontology))
            tells.addNewFunctional().addNewRatom().setName(propertyName);
        if (prop.isInverseFunctional(ontology))
            tells.addNewFunctional().addNewInverse().addNewRatom().setName(
                    propertyName);
        if (prop.isSymmetric(ontology)) {
            RolePair rolePair = tells.addNewEqualr();
            rolePair.addNewRatom().setName(propertyName);
            rolePair.addNewInverse().addNewRatom().setName(propertyName);
        }
        OWLObjectProperty nextProperty;
        RolePair nextRolePair;
        for (Iterator it = prop.getInverses(ontology).iterator(); it.hasNext();) {
            nextRolePair = tells.addNewEqualr();
            nextRolePair.addNewRatom().setName(propertyName);
            nextProperty = (OWLObjectProperty) it.next();
            nextRolePair.addNewInverse().addNewRatom().setName(
                    nextProperty.getURI().toString());
        }
        for (Iterator it = prop.getSuperProperties(ontology).iterator(); it
                .hasNext();) {
            nextRolePair = tells.addNewImpliesr();
            nextRolePair.addNewRatom().setName(propertyName);
            nextProperty = (OWLObjectProperty) it.next();
            nextRolePair.addNewRatom()
                    .setName(nextProperty.getURI().toString());
        }
        OWLDescription nextDescription;
        RoleConceptPair nextRoleConceptPair;
        for (Iterator it = prop.getDomains(ontology).iterator(); it.hasNext();) {
            nextRoleConceptPair = tells.addNewDomain();
            nextRoleConceptPair.addNewRatom().setName(propertyName);
            nextDescription = (OWLDescription) it.next();
            nextDescription.accept(new RoleConceptPairRenderingVisitor(
                    nextRoleConceptPair));
        }
        for (Iterator it = prop.getRanges(ontology).iterator(); it.hasNext();) {
            nextRoleConceptPair = tells.addNewRange();
            nextRoleConceptPair.addNewRatom().setName(propertyName);
            nextDescription = (OWLDescription) it.next();
            nextDescription.accept(new RoleConceptPairRenderingVisitor(
                    nextRoleConceptPair));
        }
    }

    /**
     * Note: It is assumed that all data properties are treated as functional
     * in DIG, so no explicit directive is added in case <code>prop</code> is
     * functional.
     * 
     * @param ontology
     * @param prop
     * @param tells
     * @throws OWLException
     */
    void renderDataProperty(OWLOntology ontology, OWLDataProperty prop,
            Tells tells) throws OWLException {
        String propertyName = prop.getURI().toString();
        tells.addNewDefattribute().setName(propertyName);
        //		if (prop.isFunctional(ontology)) {
        //		}
        OWLDescription nextDescription;
        RoleConceptPair nextRoleConceptPair;
        for (Iterator it = prop.getDomains(ontology).iterator(); it.hasNext();) {
            nextRoleConceptPair = tells.addNewDomain();
            nextRoleConceptPair.addNewAttribute().setName(propertyName);
            nextDescription = (OWLDescription) it.next();
            nextDescription.accept(new RoleConceptPairRenderingVisitor(
                    nextRoleConceptPair));
        }
        for (Iterator it = prop.getRanges(ontology).iterator(); it.hasNext();) {
            /* Quick'n'dirty hack for concrete ranges. */
            try {
                OWLDataType ran = (OWLDataType) it.next();
                if (ran.getURI().toString()
                        .equals(
                                XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                        .getInteger())
                        || ran.getURI().toString().equals(
                                XMLSchemaSimpleDatatypeVocabulary.INSTANCE
                                        .getInt()))
                    tells.addNewRangeint().addNewAttribute().setName(
                            prop.getURI().toString());
                else if (ran.getURI().toString().equals(
                        XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getString()))
                    tells.addNewRangestring().addNewAttribute().setName(
                            prop.getURI().toString());
                else
                    throw new RendererException("Unsupported range: "
                            + prop.getURI() + "\n\t" + ran.getURI());
            } catch (ClassCastException ex) {
                throw new RendererException("Unsupported range: "
                        + prop.getURI());
            }
        }
    }

    void renderDataType(OWLOntology ontology, OWLDataType datatype, Tells tells)
            throws NotExpressableInDIGException {
        throw new NotExpressableInDIGException("Can't handle DataTypes");
    }

    void renderClassAxiom(OWLClassAxiom axiom, Tells tells) throws OWLException {
        axiom.accept(new ClassAxiomRenderingVisitor(tells));
    }

    void renderPropertyAxiom(OWLPropertyAxiom axiom, Tells tells)
            throws OWLException {
        axiom.accept(new PropertyAxiomRenderingVisitor(tells));
    }

    void renderIndividualAxiom(OWLIndividualAxiom axiom, Tells tells)
            throws NotExpressableInDIGException {
        throw new NotExpressableInDIGException("Can't handle IndividualAxioms");
    }

    /**
     * Renders the OWL ontology specified in the URI in the first argument as
     * DIG 1.1. If a file is specified as second argument then the rendering is
     * saved to that file, otherwise it is simply printed to standard out. Also
     * checks whether the rendered ontology is valid.
     * 
     * @param args
     *            the URI of the OWL ontology and, optionally, the file where
     *            the rendered ontology is to be stored.
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err
                        .println("Failed: needs physical URI of OWL ontology to be rendered.");
                System.err
                        .println("Optionally, provide name of file where the rendered ontology has to be written.");
                return;
            }
            URI owlOntologyUri = new URI(args[0]);
            OWLOntology ontology = null;
            OWLConnection connection = null;
            try {
                connection = OWLManager.getOWLConnection();
            } catch (OWLException e) {
                System.err.println("Could not obtain connection");
                System.exit(-1);
            }
            OWLRDFParser parser = new OWLRDFParser();
            parser.setConnection(connection);
            ontology = parser.parseOntology(owlOntologyUri);
            Renderer renderer = new Renderer();
            TellsDocument tells = renderer.renderOntology(ontology);
            tells.getTells().setUri(
                    "urn:just-to-validate/replace-me-with-kb-uri");
            if (!tells.validate())
                System.err.println("Translation not valid.");
            if (args.length > 1) {
                File file = new File(args[1]);
                XmlOptions prettyPrint = prettyPrintOptions();
                tells.save(file, prettyPrint);
                System.out.println("OWL ontology " + owlOntologyUri.toString()
                        + " translated to DIG 1.1 in file " + file.toString());
            } else {
                System.out.println("OWL ontology " + owlOntologyUri.toString()
                        + " translated to DIG 1.1:");
                System.out.println(tells.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return
     */
    public static XmlOptions prettyPrintOptions() {
        XmlOptions prettyPrint = new XmlOptions();
        prettyPrint.setSavePrettyPrint();
        prettyPrint.setUseDefaultNamespace();
        return prettyPrint;
    }
}