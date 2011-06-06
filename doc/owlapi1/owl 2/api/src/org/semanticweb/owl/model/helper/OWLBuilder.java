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
 * Filename           $RCSfile: OWLBuilder.java,v $
 * Revision           $Revision: 1.6 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/04/12 14:50:07 $
 *               by   $Author: matthewhorridge $
 * Created: Fri Oct 17 10:13:26 2003
 ****************************************************************/
package org.semanticweb.owl.model.helper;

import java.io.Serializable;
import java.net.URI; 
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet; 
import java.util.Iterator;
import java.util.Map;
import java.util.Set; 
import java.util.logging.Logger;

import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataEnumeration;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDeprecatableObject;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLRestriction;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.change.AddAnnotationInstance;
import org.semanticweb.owl.model.change.AddClassAxiom;
import org.semanticweb.owl.model.change.AddDataPropertyInstance;
import org.semanticweb.owl.model.change.AddDataPropertyRange;
import org.semanticweb.owl.model.change.AddDataType;
import org.semanticweb.owl.model.change.AddDomain;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.AddEnumeration;
import org.semanticweb.owl.model.change.AddEquivalentClass;
import org.semanticweb.owl.model.change.AddIndividualAxiom;
import org.semanticweb.owl.model.change.AddIndividualClass;
import org.semanticweb.owl.model.change.AddInverse;
import org.semanticweb.owl.model.change.AddObjectPropertyInstance;
import org.semanticweb.owl.model.change.AddObjectPropertyRange;
import org.semanticweb.owl.model.change.AddPropertyAxiom;
import org.semanticweb.owl.model.change.AddSuperClass;
import org.semanticweb.owl.model.change.AddSuperProperty;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.SetDeprecated;
import org.semanticweb.owl.model.change.SetFunctional;
import org.semanticweb.owl.model.change.SetInverseFunctional;
import org.semanticweb.owl.model.change.SetSymmetric;
import org.semanticweb.owl.model.change.SetTransitive;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;

/**
 * Builds abstract OWL ontologies (eg from parser input).
 *
 * @author <a href="mailto:dturi at cs.man.ac.uk">Daniele Turi</a>
 * @version $Id: OWLBuilder.java,v 1.6 2006/04/12 14:50:07 matthewhorridge Exp $
 */
public class OWLBuilder implements Serializable {

    final static public int SYMMETRIC = 11;
    final static public int FUNCTIONAL = 12;
    final static public int INVERSE_FUNCTIONAL = 13;
    final static public int TRANSITIVE = 14;

    static Logger logger = Logger.getLogger(OWLBuilder.class.getName());

    OWLOntology owlOntology;
    /**
     * Gets the current <code>OWLOntology</code>.
     *
     * @return the current <code>OWLOntology</code>.
     */
    public OWLOntology getOntology() {
	return owlOntology;
    } 

    ChangeVisitor changeVisitor;
    /**
     * Gets the current <code>ChangeVisitor</code>.
     *
     * @return the current <code>ChangeVisitor</code>.
     */
    public ChangeVisitor getChangeVisitor() {
	return changeVisitor;
    } 

    OWLDataFactory owlDataFactory;
    /**
     * Gets the current <code>OWLDataFactory</code>.
     *
     * @return the current <code>OWLDataFactory</code>.
     */
    public OWLDataFactory getDataFactory() {
	return owlDataFactory;
    } 

    OWLConnection connection;

    /**
     * Gets the current <code>OWLConnection</code>.
     *
     * @return the current <code>OWLConnection</code>.
     */
    public OWLConnection getConnection() {
	return connection;
    } 

    /**
     * Set the value of the OWLConnection value.
     * @param newConnection The new OWLConnection value.
     */
    public void setConnection(OWLConnection newConnection) {
	this.connection = newConnection;
    }
    
    /**
     * Creates a new <code>OWLBuilder</code> instance and sets a default
     * {@link OWLConnection} 
     * (to org.semanticweb.owl.impl.model.OWLConnectionImpl).
     */
    public OWLBuilder() {
	// 	Map parameters = new HashMap();
	// 	parameters.put(OWLManager.OWL_CONNECTION,
	// 		       "org.semanticweb.owl.impl.model.OWLConnectionImpl");
	try {
	    //	    connection = OWLManager.getOWLConnection(parameters);
	    /* Use the default provided by the OWLManager */
	    connection = OWLManager.getOWLConnection();
	} catch ( OWLException e ) {
	    System.err.println("Could not obtain connection");
	    System.exit(-1);
	}
    }

    /**
     * Creates a new <code>OWLBuilder</code> instance and sets the
     * connection property.
     */
    public OWLBuilder(OWLConnection connection) {
	this.connection = connection;
    }

    /**
     * Creates an ontology identified by <code>id</code> together
     * with an {@link OWLDataFactory} and a {@link ChangeVisitor}.
     *
     * @param id an <code>URI</code> value
     * @exception OWLException if an error occurs
     */
    public void createOntology(URI id, URI physicalURI) throws OWLException {
 	logger.fine("connection " + connection);
	owlDataFactory = connection.getDataFactory();
	if (id == null) 
	    id = URI.create("urn:defaultOntologyUri");
	owlOntology = owlDataFactory.getOWLOntology(id, physicalURI);
	changeVisitor = connection.getChangeVisitor(owlOntology);
    }

    /**
     * Returns the class corresponding to <code>id</code>
     * in the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the class corresponding to <code>id</code>
     * @exception OWLException if an error occurs
     */
    public OWLClass getClass(URI id) throws OWLException {
	return owlDataFactory.getOWLClass(id);
    }
    
    /**
     * Adds the class identified by <code>id</code>,
     * to the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the <code>OWLClass</code> corresponding to <code>id</code>.
     * @exception OWLException if an error occurs
     */
    public OWLClass addClass(URI id) throws OWLException {
	OWLClass oClass = getClass(id);
	new AddEntity(owlOntology, oClass, null).accept(changeVisitor);
	return oClass;
    }

    /**
     * Returns the individual corresponding to <code>id</code>
     * in the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the individual corresponding to <code>id</code>
     * @exception OWLException if an error occurs
     */
    public OWLIndividual getIndividual(URI id) throws OWLException {
        if(id != null) {
            return owlDataFactory.getOWLIndividual(id);
        }
        else {
            try {
                // We need an anon id.  Just randomly generate one from the system time
                URI uri = new URI("http://www.semanticweb.org/anon#" + System.nanoTime());
                return owlDataFactory.getAnonOWLIndividual(uri);
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Adds the individual identified by <code>id</code>,
     * to the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the <code>OWLIndividual</code> corresponding to <code>id</code>.
     * @exception OWLException if an error occurs
     */
    public OWLIndividual addIndividual(URI id) throws OWLException {
	OWLIndividual individual = getIndividual(id);
	AddEntity ae = new AddEntity(owlOntology, individual, null);
	ae.accept(changeVisitor);
	return individual;
    }

    /**
     * Adds the annotation instance with the parameter <code>subject</code> 
     * as subject and with property and content given by the
     * paameter <code>annComponent</code>.
     *
     * @param subject an <code>OWLObject</code> value
     * @param annComponent an <code>AnnotationComponent</code> value
     * @exception OWLException if an error occurs
     */
    public void addAnnotationInstance(OWLObject subject, 
				      AnnotationComponent annComponent) 
	throws OWLException 
    {
	new AddAnnotationInstance(owlOntology, subject, 
				  annComponent.getProperty(),
				  annComponent.getContent(), null)
	    .accept(changeVisitor);
    }

    /**
     * Adds the annotation instance with ontology itself 
     * as subject and with property and content given by the
     * paameter <code>annComponent</code>.
     *
     * @param annComponent an <code>AnnotationComponent</code> value
     * @exception OWLException if an error occurs
     */
    public void addOntologyAnnotationInstance(AnnotationComponent annComponent)
	throws OWLException 
    {
	addAnnotationInstance(owlOntology, annComponent);
    }

    /**
     * Returns the object property corresponding to <code>id</code>
     * in the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the object property corresponding to <code>id</code>
     * @exception OWLException if an error occurs
     */
    public OWLObjectProperty getObjectProperty(URI id) throws OWLException {
	return owlDataFactory.getOWLObjectProperty(id);
    }

    /**
     * Returns the annotation property corresponding to <code>id</code>
     * in the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the annotation property corresponding to <code>id</code>
     * @exception OWLException if an error occurs
     */
    public OWLAnnotationProperty getAnnotationProperty(URI id) 
	throws OWLException 
    {
	return owlDataFactory.getOWLAnnotationProperty(id);
    }

    /**
     * Adds the object property identified by <code>id</code>
     * to the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the added <code>OWLObjectProperty</code>.
     * @exception OWLException if an error occurs
     */
    public OWLObjectProperty addObjectProperty(URI id) throws OWLException
    {
        OWLObjectProperty entity = getObjectProperty(id);
	new AddEntity(owlOntology, entity, null).accept(changeVisitor);
	return entity;
    }

    /**
     * Adds the inverse object property identified by <code>inverseId</code>
     * to <code>property</code> (and to the current ontology).
     *
     * @param property an <code>OWLObjectProperty</code>.
     * @param inverseId the <code>URI</code> of the inverse property to add.
     * @return the added inverse <code>OWLObjectProperty</code>.
     * @exception OWLException if an error occurs
     */
    public OWLObjectProperty addInverse(OWLObjectProperty property, 
					URI inverseId) 
	throws OWLException
    {
        OWLObjectProperty inverse = addObjectProperty(inverseId);
	new AddInverse(owlOntology, property, inverse, null)
	    .accept(changeVisitor);
	return inverse;
    }

    /**
     * Returns the data property corresponding to <code>id</code>
     * in the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the data property corresponding to <code>id</code>
     * @exception OWLException if an error occurs
     */
    public OWLDataProperty getDataProperty(URI id) throws OWLException {
	return owlDataFactory.getOWLDataProperty(id);
    }

    /**
     * Adds the data property identified by <code>id</code>,
     * to the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the added <code>OWLDataProperty</code>.
     * @exception OWLException if an error occurs
     */
    public OWLDataProperty addDataProperty(URI id) throws OWLException {
	OWLDataProperty prop = getDataProperty(id);
	new AddEntity(owlOntology, prop, null).accept(changeVisitor);
	return prop;
    }

    /**
     * Deprecates <code>deprecatable</code>.
     *
     * @param deprecatable an <code>OWLDeprecatableObject</code> value
     * @exception OWLException if an error occurs
     */
    public void setDeprecatedObject(OWLDeprecatableObject deprecatable) 
	throws OWLException 
    {
	new SetDeprecated(owlOntology, deprecatable, true, null)
	    .accept(changeVisitor);
    }

    /**
     * Adds <code>description</code> to <code>individual</code>
     * in current ontology.
     *
     * @param description an <code>OWLDescription</code> value
     * @param individual an <code>OWLIndividual</code> value
     * @exception OWLException if an error occurs
     */
    public void addDescriptionToIndividual(OWLDescription description,
				     OWLIndividual individual) 
	throws OWLException 
    {
	new AddIndividualClass(owlOntology, individual, description, null)
	    .accept(changeVisitor);
    }

    /**
     * Asserts <code>individuals</code> are the same in
     * the current ontology.
     *
     * @param individuals a <code>Set</code> of {@link URI}s.
     * @exception OWLException if an error occurs
     */
    public void addSameIndividualsAxiom(Set individuals) 
	throws OWLException 
    {
	OWLSameIndividualsAxiom axiom = owlDataFactory
	    .getOWLSameIndividualsAxiom(toOWLIndividuals(individuals));
	new AddIndividualAxiom(owlOntology, axiom, null)
	    .accept(changeVisitor);
    }

    /**
     * Asserts <code>individuals</code> are different in
     * the current ontology.
     *
     * @param individuals a <code>Set</code> of {@link URI}s.
     * @exception OWLException if an error occurs
     */
    public void addDifferentIndividualsAxiom(Set individuals) 
	throws OWLException 
    {
	OWLDifferentIndividualsAxiom axiom = owlDataFactory
	    .getOWLDifferentIndividualsAxiom(toOWLIndividuals(individuals));
	new AddIndividualAxiom(owlOntology, axiom, null)
	    .accept(changeVisitor);
    }

    /**
     * Sets <code>property</code> to <code>type</code>.
     *
     * @param property an <code>OWLObjectProperty</code>.
     * @param type one of: {@link #SYMMETRIC}, {@link #FUNCTIONAL},
     * {@link #INVERSE_FUNCTIONAL}, {@link #TRANSITIVE}.
     * @exception OWLException if an error occurs
     */
    public void setPropertyType(OWLObjectProperty property, int type) 
	throws OWLException 
    {
	OntologyChange change = null;
	switch (type) {
	case SYMMETRIC:
	    change = new SetSymmetric(owlOntology, property, true, null);
	    break;
	case FUNCTIONAL:
	    change = new SetFunctional(owlOntology, property, true, null);
	    break;
	case INVERSE_FUNCTIONAL:
	    change = 
		new SetInverseFunctional(owlOntology, property, true, null);
	    break;
	case TRANSITIVE:
	    change = new SetTransitive(owlOntology, property, true, null);
	    break;
	default:
	    throw new RuntimeException
		("Property type " + type + " not supported");
	}
	change.accept(changeVisitor);
    }

    /**
     * Sets the OWLDataProperty <code>property</code> to functional.
     *
     * @param property an <code>OWLDataProperty</code> value
     * @exception OWLException if an error occurs
     */
    public void setDataPropertyFunctional(OWLDataProperty property)
	throws OWLException
    {
	new SetFunctional(owlOntology, property, true, null)
	    .accept(changeVisitor);
    }

    /**
     * Adds an enumeration of <code>individuals</code> to 
     * <code>oClass</code>.
     *
     * @param oClass an <code>OWLClass</code>.
     * @param individuals a <code>Set</code> of {@link OWLIndividual}s.
     * @exception OWLException if an error occurs
     */
    public void addEnumeration(OWLClass oClass, Set individuals) 
	throws OWLException 
    {
	OWLEnumeration oEnum = owlDataFactory
	    .getOWLEnumeration(toOWLIndividuals(individuals));
	new AddEnumeration(owlOntology, oClass, oEnum, null)
	    .accept(changeVisitor);
    }

    /**
     * Adds to the current ontology the axiom stating that
     * <code>oClass</code> is equivalent
     * to the conjunction of the {@link OWLDescription}s in 
     * <code>descriptions</code>.
     *
     * @param oClass an <code>OWLClass</code> value
     * @param descriptions a <code>Set</code> of {@link OWLDescription}s.
     * @exception OWLException if an error occurs
     */
    public void addEquivalentClass(OWLClass oClass, Set descriptions) 
	throws OWLException
    {
	int descriptionsCount = descriptions.size();
	if (descriptionsCount > 0) {
	    AddEquivalentClass equiv;
	    if (descriptionsCount == 1) {
		equiv = new AddEquivalentClass
		    (owlOntology, oClass, 
		     (OWLDescription)descriptions.iterator().next(), null);
	    }	
	    else {
		OWLDescription conjunction = 
		    owlDataFactory.getOWLAnd(descriptions);
		equiv = new AddEquivalentClass(owlOntology, oClass, 
					       conjunction, null);
	    }
	    equiv.accept(changeVisitor);
	}
    }

    /**
     * Adds to the current ontology the axiom stating that
     * the <code>oClass</code> is a subclass of
     * the conjunction of the {@link OWLDescription}s in 
     * <code>descriptions</code>.
     *
     * @param oClass an <code>OWLClass</code> value
     * @param descriptions a <code>Set</code> of {@link OWLDescription}s.
     * @exception OWLException if an error occurs
     */
    public void addSuperClass(OWLClass oClass, Set descriptions) 
	throws OWLException
    {
	int descriptionsCount = descriptions.size();
	if (descriptionsCount > 0) {
	    AddSuperClass sups;
	    if (descriptionsCount == 1) {
		sups = new AddSuperClass
		    (owlOntology, oClass, 
		     (OWLDescription)descriptions.iterator().next(), null);
	    }	
	    else {
		OWLDescription conjunction = 
		    owlDataFactory.getOWLAnd(descriptions);
		sups = new AddSuperClass(owlOntology, oClass, conjunction, 
					 null);
	    }
	    sups.accept(changeVisitor);
	}
    }

    /**
     * Adds the evident axiom for <code>disjuncts</code> to the ontology.
     *
     * @param disjuncts a <code>Set</code> of {@link OWLDescription}s.
     * @exception OWLException if an error occurs
     */
    public void addDisjointClassesAxiom(Set disjuncts) throws OWLException {
	addClassAxiom(owlDataFactory.getOWLDisjointClassesAxiom(disjuncts));
    }

    /**
     * Adds the evident axiom for <code>equivalents</code> to the ontology.
     *
     * @param equivalents a <code>Set</code> of {@link OWLDescription}s.
     * @exception OWLException if an error occurs
     */
    public void addEquivalentClassesAxiom(Set equivalents) throws OWLException 
    {
	addClassAxiom(owlDataFactory
		      .getOWLEquivalentClassesAxiom(equivalents));
    }

    /**
     * Adds an axiom stating that <code>description1</code> is
     * a subclass of <code>description1</code>.
     *
     * @param description1 an <code>OWLDescription</code> value
     * @param description2 an <code>OWLDescription</code> value
     * @exception OWLException if an error occurs
     */
    public void addSubClassAxiom(OWLDescription description1, 
				 OWLDescription description2) 
	throws OWLException 
    {
	addClassAxiom(owlDataFactory.getOWLSubClassAxiom(description1, 
							 description2));
    }

    /**
     * Adds <code>axiom</code> to the ontology.
     *
     * @param axiom an <code>OWLClassAxiom</code> value
     * @exception OWLException if an error occurs
     */
    public void addClassAxiom(OWLClassAxiom axiom) throws OWLException 
    {
	AddClassAxiom ae = new AddClassAxiom(owlOntology, axiom, null);
	ae.accept(changeVisitor);
    }

    /**
     * Add the data type corresponding to <code>id</code>
     * to the current ontology.
     *
     * @param id an <code>URI</code>
     * @return the added <code>OWLDataType</code>.
     * @exception OWLException if an error occurs
     */
    public OWLDataType addDataType(URI id) throws OWLException
    {
	OWLDataType datatype = owlDataFactory.getOWLConcreteDataType(id);
	new AddDataType(owlOntology, datatype, null).accept(changeVisitor);
	return datatype;
    }

    /**
     * Adds to the current ontology
     * an axiom stating that (the data property corresponding to) 
     * <code>subId</code> is a subproperty of 
     * (the data property corresponding to) <code>supId</code>.
     *
     * @param subId an <code>URI</code> value
     * @param supId an <code>URI</code> value
     * @exception OWLException if an error occurs
     */
    public void addDataSubPropertyAxiom(URI subId, URI supId) 
	throws OWLException 
    {
	OWLProperty subProp = getDataProperty(subId);
	OWLProperty supProp = getDataProperty(supId);
	addPropertyAxiom(owlDataFactory.getOWLSubPropertyAxiom(subProp, 
							       supProp));
    }

    /**
     * Adds to the current ontology the fact that 
     * <code>property</code> has 
     * <code>superProperty</code> as super property.
     *
     * @param property an <code>OWLProperty</code> value
     * @param superProperty an <code>OWLProperty</code> value
     * @exception OWLException if an error occurs
     */
    public void addSuperProperty(OWLProperty property, 
				 OWLProperty superProperty) 
	throws OWLException
    {
	new AddSuperProperty(owlOntology, property, superProperty, null)
	    .accept(changeVisitor);
    }

    /**
     * Adds to the current ontology
     * an axiom stating that (the object property corresponding to) 
     * <code>subId</code> is a subproperty of 
     * (the object property corresponding to) <code>supId</code>.
     *
     * @param subId an <code>URI</code> value
     * @param supId an <code>URI</code> value
     * @exception OWLException if an error occurs
     */
    public void addObjectSubPropertyAxiom(URI subId, URI supId) 
	throws OWLException 
    {
	OWLProperty subProp = getObjectProperty(subId);
	OWLProperty supProp = getObjectProperty(supId);
	addPropertyAxiom(owlDataFactory.getOWLSubPropertyAxiom(subProp, 
							       supProp));
    }

    /**
     * Adds <code>axiom</code> to the current ontology.
     *
     * @param axiom an <code>OWLPropertyAxiom</code> value
     * @exception OWLException if an error occurs
     */
    public void addPropertyAxiom(OWLPropertyAxiom axiom) throws OWLException 
    {
	new AddPropertyAxiom(owlOntology, axiom, null).accept(changeVisitor);
    }

    /**
     * Adds <code>domain</code> to <code>property</code>.
     *
     * @param property an <code>OWLProperty</code> value
     * @param domain an <code>OWLDescription</code> value
     * @exception OWLException if an error occurs
     */
    public void addPropertyDomain(OWLProperty property,
				  OWLDescription domain) 
	throws OWLException 
    {
	new AddDomain(owlOntology, property, domain, null)
	    .accept(changeVisitor);
    }

    /**
     * Adds <code>range</code> to <code>property</code>.
     *
     * @param property an <code>OWLObjectProperty</code> value
     * @param range an <code>OWLDescription</code> value
     * @exception OWLException if an error occurs
     */
    public void addObjectPropertyRange(OWLObjectProperty property,
				       OWLDescription range) 
	throws OWLException 
    {
	new AddObjectPropertyRange(owlOntology, property, range, null)
	    .accept(changeVisitor);
    }

    /**
     * Adds <code>range</code> to <code>property</code>.
     *
     * @param property an <code>OWLDataProperty</code> value
     * @param range an <code>OWLDataRange</code> value
     * @exception OWLException if an error occurs
     */
    public void addDataPropertyRange(OWLDataProperty property,
				     OWLDataRange range) 
	throws OWLException 
    {
	new AddDataPropertyRange(owlOntology, property, range, null)
	    .accept(changeVisitor);
    }

    /**
     * Adds the evident axiom for <code>equivalents</code> to the ontology.
     *
     * @param equivalents a <code>Set</code> of URIs corresponding
     * to {@link OWLObjectProperty}s.
     * @exception OWLException if an error occurs
     */
    public void addEquivalentObjectPropertiesAxiom(Set equivalents) 
	throws OWLException
    {
	new AddPropertyAxiom(owlOntology, owlDataFactory
			     .getOWLEquivalentPropertiesAxiom
			     (toOWLObjectProperties(equivalents)), null)
	    .accept(changeVisitor);
    }

    /**
     * Adds the evident axiom for <code>equivalents</code> to the ontology.
     *
     * @param equivalents a <code>Set</code> of URIs corresponding
     * to {@link OWLDataProperty}s.
     * @exception OWLException if an error occurs
     */
    public void addEquivalentDataPropertiesAxiom(Set equivalents) 
	throws OWLException
    {
	new AddPropertyAxiom(owlOntology, owlDataFactory
			     .getOWLEquivalentPropertiesAxiom
			     (toOWLDataProperties(equivalents)), null)
	    .accept(changeVisitor);
    }

    /**
     * Returns the <code>OWLDescription</code> corresponding to
     * the union of <code>descriptions</code>.
     *
     * @param descriptions a <code>Set</code> of {@link OWLDescription}s.
     * @return the <code>OWLDescription</code> corresponding to
     * the union of <code>descriptions</code>.
     * @exception OWLException if an error occurs
     */
    public OWLDescription unionOf(Set descriptions) throws OWLException {
	return owlDataFactory.getOWLOr(descriptions);
    }

    /**
     * Returns the <code>OWLDescription</code> corresponding to
     * the intersection of <code>descriptions</code>.
     *
     * @param descriptions a <code>Set</code> of {@link OWLDescription}s.
     * @return the <code>OWLDescription</code> corresponding to
     * the intersection of <code>descriptions</code>.
     * @exception OWLException if an error occurs
     */
    public OWLDescription intersectionOf(Set descriptions) throws OWLException 
    {
	return owlDataFactory.getOWLAnd(descriptions);
    }

    /**
     * Returns the <code>OWLDescription</code> corresponding to
     * the complement of <code>description</code>.
     *
     * @param description an {@link OWLDescription}.
     * @return the <code>OWLDescription</code> corresponding to
     * the complement of <code>description</code>.
     * @exception OWLException if an error occurs
     */
    public OWLDescription complementOf(OWLDescription description) 
	throws OWLException 
    {
	return owlDataFactory.getOWLNot(description);
    }

    /**
     * Returns the <code>OWLEnumeration</code> of
     * <code>individuals</code>.
     *
     * @param individuals a <code>Set</code> of URI's.
     * @return the <code>OWLEnumeration</code> of
     * <code>individuals</code>.
     * @exception OWLException if an error occurs
     */
    public OWLEnumeration oneOf(Set individuals) 
	throws OWLException 
    {
	return owlDataFactory.getOWLEnumeration(toOWLIndividuals(individuals));
    }

    /**
     * Returns the <code>Set</code> of {@link OWLIndividual}s corresponding
     * to <code>individualURIs</code> (in the current ontology).
     *
     * @param individualURIs a <code>Set</code> of URI's.
     * @return the <code>Set</code> of {@link OWLIndividual}s corresponding
     * to <code>individualURIs</code>
     * @exception OWLException if an error occurs
     */
    public Set toOWLIndividuals(Set individualURIs) throws OWLException {
	Set individuals = new HashSet();
	for (Iterator i = individualURIs.iterator(); i.hasNext(); )
	    individuals.add(getIndividual((URI)i.next()));
	return individuals;
    } 

    /**
     * Returns the <code>Set</code> of {@link OWLObjectProperty}s corresponding
     * to <code>objectPropertyURIs</code> (in the current ontology).
     *
     * @param objectPropertyURIs a <code>Set</code> of URI's.
     * @return the <code>Set</code> of {@link OWLObjectProperty}s corresponding
     * to <code>objectPropertyURIs</code>
     * @exception OWLException if an error occurs
     */
    public Set toOWLObjectProperties(Set objectPropertyURIs) 
	throws OWLException 
    {
	Set objectProperties = new HashSet();
	for (Iterator i = objectPropertyURIs.iterator(); i.hasNext(); )
	    objectProperties.add(getObjectProperty((URI)i.next()));
	return objectProperties;
    } 

    /**
     * Returns the <code>Set</code> of {@link URI}s corresponding
     * to <code>uriStrings</code>.
     *
     * @param uriStrings a <code>Set</code> of Strings.
     * @return the <code>Set</code> of {@link URI}s corresponding
     * to <code>uriStrings</code>.
     * @exception URISyntaxException if one of the uriStrings is not
     * a well-formed URI.
     * if one of the <code>uriStrings</code> is not well formed.
     */
    static public Set toURIs(Set uriStrings) 
	throws URISyntaxException
    {
	Set uris = new HashSet();
	String idString;
	for (Iterator i = uriStrings.iterator(); i.hasNext(); ) {
	    idString = (String)i.next();
	    uris.add(new URI(idString));
	}
	return uris;
    } 

    /**
     * Returns the <code>Set</code> of {@link OWLDataProperty}s corresponding
     * to <code>dataPropertyURIs</code> (in the current ontology).
     *
     * @param dataPropertyURIs a <code>Set</code> of URI's.
     * @return the <code>Set</code> of {@link OWLDataProperty}s corresponding
     * to <code>dataPropertyURIs</code>
     * @exception OWLException if an error occurs
     */
    public Set toOWLDataProperties(Set dataPropertyURIs) 
	throws OWLException 
    {
	Set dataProperties = new HashSet();
	for (Iterator i = dataPropertyURIs.iterator(); i.hasNext(); )
	    dataProperties.add(getDataProperty((URI)i.next()));
	return dataProperties;
    }

    /**
     * Adds the annotation property identified by <code>id</code>,
     * to the current ontology.
     *
     * @param id an <code>URI</code> value
     * @return the <code>OWLAnnotationProperty</code> 
     * corresponding to <code>id</code>.
     * @exception OWLException if an error occurs
     */
    public OWLAnnotationProperty addAnnotationProperty(URI id) 
	throws OWLException 
    {
	OWLAnnotationProperty annotationProperty = getAnnotationProperty(id);
	new AddEntity(owlOntology, annotationProperty, null)
	    .accept(changeVisitor);
	return annotationProperty;
    }

//     /**
//      * Adds the ontology property identified by <code>id</code>,
//      * to the current ontology.
//      *
//      * @param id an <code>URI</code> value
//      * @return the <code>OWLProperty</code> 
//      * corresponding to <code>id</code>.
//      * @exception OWLException if an error occurs
//      */
//     public OWLProperty addOntologyProperty(URI id) 
//     {
// 	OWLProperty ontologyProperty = getOntologyProperty(id);
// 	new AddEntity(owlOntology, ontologyProperty, null)
// 	    .accept(changeVisitor);
// 	return ontologyProperty;
//     }

    /**
     * Returns a restriction to the value <code>individual</code> 
     * for the property corresponding to  <code>propertyId</code>.
     *
     * @param propertyId an <code>URI</code> value
     * @param individual an <code>OWLIndividual</code> value
     * @return a restriction to the value <code>individual</code> 
     * for the property corresponding to  <code>propertyId</code>.
     * @exception OWLException if an error occurs
     */
    public OWLRestriction getObjectValueRestriction(URI propertyId, 
						    OWLIndividual individual)
	throws OWLException
    {
	return owlDataFactory.getOWLObjectValueRestriction
	    (getObjectProperty(propertyId), individual);
    }

    /**
     * Creates a <code>CardinalityComponent</code> with type <code>type</code>.
     *
     * @param type one of the constants in {@link CardinalityComponent}.
     * @return a <code>CardinalityComponent</code> with type <code>type</code>.
     */
    public CardinalityComponent createCardinalityComponent(int type) {
	return new CardinalityComponent(type);
    }

    /**
     * Creates a <code>DescriptionComponent</code> with 
     * description <code>description</code> and type <code>type</code>.
     *
     * @param type one of the constants in {@link DescriptionComponent}.
     * @return a <code>DescriptionComponent</code> with type <code>type</code>.
     */
    public DescriptionComponent 
	createDescriptionComponent(OWLDescription description, int type) 
    {
	return new DescriptionComponent(description, type);
    }

    /**
     * Returns the OWLRestriction for <code>property</code> corresponding
     * to the RestrictionComponent <code>component</code>.
     *
     * @param property an <code>OWLObjectProperty</code>.
     * @param component a <code>RestrictionComponent</code>.
     * @return the OWLRestriction for <code>property</code> corresponding
     * to the RestrictionComponent <code>component</code>.
     * @exception OWLException if an error occurs
     */
    public OWLRestriction getRestriction(OWLObjectProperty property, 
					 RestrictionComponent component)
	throws OWLException
    {
	if (component instanceof CardinalityComponent)
	    return getRestriction(property, (CardinalityComponent)component);
	if (component instanceof DescriptionComponent)
	    return getRestriction(property, (DescriptionComponent)component);
	if (component instanceof IndividualValueComponent)
	    return owlDataFactory.getOWLObjectValueRestriction
		(property, addIndividual
		 (((IndividualValueComponent)component).getValue())); 	     
	return null;
    }

    /**
     * Returns the OWLRestriction for <code>property</code> corresponding
     * to the CardinalityComponent <code>cComponent</code>.
     *
     * @param property an <code>OWLObjectProperty</code>.
     * @param cComponent a <code>CardinalityComponent</code> value
     * @return the OWLRestriction for <code>property</code> corresponding
     * to the CardinalityComponent <code>cComponent</code>.
     * @exception OWLException if an error occurs
     * @throws CardinalityNotSupportedError if the type of 
     * <code>cComponent</code>
     * is not one of {@link CardinalityComponent#MAX}, 
     * {@link CardinalityComponent#MIN}, or {@link CardinalityComponent#EQ}.
     */
    public OWLRestriction getRestriction(OWLObjectProperty property, 
					 CardinalityComponent cComponent)
	throws OWLException
    {
	switch (cComponent.getType()) {
	case CardinalityComponent.MAX:
	    return owlDataFactory.getOWLObjectCardinalityAtMostRestriction
		(property, cComponent.getCardinality()); 	     
	case CardinalityComponent.MIN:
	    return owlDataFactory.getOWLObjectCardinalityAtLeastRestriction
		(property, cComponent.getCardinality()); 	     
	case CardinalityComponent.EQ:
	    int cardinality = cComponent.getCardinality();
	    return owlDataFactory.getOWLObjectCardinalityRestriction
		(property, cardinality, cardinality); 	     
	default: throw new CardinalityNotSupportedError();
	}
    }

    /**
     * Returns the OWLRestriction for <code>property</code> corresponding
     * to the DescriptionComponent <code>dComponent</code>.
     *
     * @param property an <code>OWLObjectProperty</code>.
     * @param dComponent a <code>DescriptionComponent</code> value
     * @return the OWLRestriction for <code>property</code> corresponding
     * to the DescriptionComponent <code>dComponent</code>.
     * @exception OWLException if an error occurs
     * @throws DescriptionRestrictionNotSupportedError if the type of 
     * <code>dComponent</code>
     * is neither {@link DescriptionComponent#ALL} nor
     * {@link DescriptionComponent#SOME}.
     */
    public OWLRestriction getRestriction(OWLObjectProperty property, 
					 DescriptionComponent dComponent)
	throws OWLException
    {
	switch (dComponent.getType()) {
	case DescriptionComponent.ALL:
	    return owlDataFactory.getOWLObjectAllRestriction
		(property, dComponent.getDescription()); 	     
	case DescriptionComponent.SOME:
	    return owlDataFactory.getOWLObjectSomeRestriction
		(property, dComponent.getDescription()); 	     
	default: throw new DescriptionRestrictionNotSupportedError();
	}
    }
	
    /**
     * Returns the OWLRestriction for <code>property</code> corresponding
     * to the RestrictionComponent <code>component</code>.
     *
     * @param property an <code>OWLDataProperty</code> value
     * @param component a <code>RestrictionComponent</code>.
     * @return the OWLRestriction for <code>property</code> corresponding
     * to the RestrictionComponent <code>component</code>.
     * @exception OWLException if an error occurs
     */
    public OWLRestriction getRestriction(OWLDataProperty property, 
					 RestrictionComponent component)
	throws OWLException
    {
	if (component instanceof CardinalityComponent)
	    return getRestriction(property, (CardinalityComponent)component);
	if (component instanceof DataLiteralValueComponent)
	    return getRestriction(property, 
				  (DataLiteralValueComponent)component);
	if (component instanceof DataRangeComponent)
	    return getRestriction(property, 
				  (DataRangeComponent)component);
	return null;
    }

    /**
     * Returns the OWLRestriction for <code>property</code> corresponding
     * to the CardinalityComponent <code>card</code>.
     *
     * @param property an <code>OWLDataProperty</code> value
     * @param card a <code>CardinalityComponent</code> value
     * @return the OWLRestriction for <code>property</code> corresponding
     * to the CardinalityComponent <code>card</code>.
     * @exception OWLException if an error occurs
     */
    public OWLRestriction getRestriction(OWLDataProperty property, 
					 CardinalityComponent card)
	throws OWLException
    {
	switch (card.getType()) {
	case CardinalityComponent.MAX:
	    return owlDataFactory.getOWLDataCardinalityAtMostRestriction
		(property, card.getCardinality()); 	     
	case CardinalityComponent.MIN:
	    return owlDataFactory.getOWLDataCardinalityAtLeastRestriction
		(property, card.getCardinality()); 	     
	case CardinalityComponent.EQ:
	    int cardinality = card.getCardinality();
	    return owlDataFactory.getOWLDataCardinalityRestriction
		(property, cardinality, cardinality); 	     
	default: throw new CardinalityNotSupportedError();
	}
    }

    /**
     * Returns the OWLRestriction for <code>property</code> corresponding
     * to the DataRangeComponent <code>dRComponent</code>.
     *
     * @param property an <code>OWLDataProperty</code> value
     * @param dRComponent a <code>DataRangeComponent</code> value
     * @return an <code>OWLRestriction</code> value
     * @exception OWLException if an error occurs
     */
    public OWLRestriction getRestriction(OWLDataProperty property, 
					 DataRangeComponent dRComponent)
	throws OWLException
    {
	switch (dRComponent.getType()) {
	case DataRangeComponent.ALL:
	    return owlDataFactory.getOWLDataAllRestriction
		(property, dRComponent.getDataRange()); 	     
	case DataRangeComponent.SOME:
	    return owlDataFactory.getOWLDataSomeRestriction
		(property, dRComponent.getDataRange()); 	     
	default: throw new DataRangeRestrictionNotSupportedError();
	}
    }

    /**
     * Returns the OWLRestriction for <code>property</code> corresponding
     * to the DataLiteralValueComponent <code>component</code>.
     *
     * @param property an <code>OWLDataProperty</code> value
     * @param component a <code>DataLiteralValueComponent</code> value
     * @return an <code>OWLRestriction</code> value
     * @exception OWLException if an error occurs
     */
    public OWLRestriction getRestriction(OWLDataProperty property, 
					 DataLiteralValueComponent component)
	throws OWLException
    {
	return owlDataFactory.getOWLDataValueRestriction
		(property, component.getDataLiteral());
    }

    /**
     * Returns the <code>OWLDataValue</code> corresponding to the two 
     * arguments.
     *
     * @param uri an <code>URI</code> value
     * @param value an <code>Object</code> value
     * @return the <code>OWLDataValue</code> corresponding to the two 
     * arguments.
     * @exception OWLException if an error occurs
     */
    public OWLDataValue getConcreteData(URI uri, String lang, Object value)
	throws OWLException
    {
	return owlDataFactory.getOWLConcreteData(uri, lang, value);
    }

    /**
     * Returns the <code>OWLDataType</code> corresponding to <code>uri</code>.
     *
     * @param uri an <code>URI</code> value
     * @return an <code>OWLDataType</code> value
     * @exception OWLException if an error occurs
     */
    public OWLDataType getConcreteDataType(URI uri)
	throws OWLException
    {
	return owlDataFactory.getOWLConcreteDataType(uri);
    }

    /**
     * Returns the enumeration of the set of DataValue literals.
     *
     * @param literals a <code>Set</code> of {@link OWLDataValue}s.
     * @return an <code>OWLDataEnumeration</code> value
     * @exception OWLException if an error occurs
     */
    public OWLDataEnumeration getDataEnumeration(Set literals)
	throws OWLException
    {
	return owlDataFactory.getOWLDataEnumeration(literals);
    }
	
    /**
     * Adds the property value <code>propValue</code> to 
     * <code>subject</code>.
     *
     * @param subject an <code>OWLIndividual</code>.
     * @param propValue either an {@link ObjectPropertyValue} or
     * a {@link DataPropertyValue}.
     * @exception OWLException if an error occurs
     */
    public void addPropertyValue(OWLIndividual subject, 
				 PropertyValue propValue) 
	throws OWLException 
    {
	if (propValue instanceof ObjectPropertyValue) {
	    ObjectPropertyValue value = (ObjectPropertyValue)propValue;
	    new AddObjectPropertyInstance(owlOntology, subject, 
					  value.getProperty(), 
					  value.getIndividual(), 
					  null).accept(changeVisitor);
	}
	if (propValue instanceof DataPropertyValue) {
	    DataPropertyValue value = (DataPropertyValue)propValue;
	    new AddDataPropertyInstance(owlOntology, subject, 
					  value.getProperty(), 
					  value.getDataValue(), 
					  null).accept(changeVisitor);
	}
    }

    /**
     * Marker interface for data and object property values.
     */
    static public interface PropertyValue {}

    /**
     * Pair <code>OWLObjectProperty</code>, <code>OWLIndividual</code>.
     */
    static public class ObjectPropertyValue implements PropertyValue {

	OWLObjectProperty property;
	OWLIndividual individual;

	/**
	 * Creates a new <code>ObjectPropertyValue</code> instance
	 * and sets property and individual.
	 *
	 * @param property an <code>OWLObjectProperty</code> value
	 * @param individual an <code>OWLIndividual</code> value
	 */
	public ObjectPropertyValue(OWLObjectProperty property,
				   OWLIndividual individual) 
	{
	    this.property = property;
	    this.individual = individual;
	}

	/**
	 * Get the Property value.
	 * @return the Property value.
	 */
	public OWLObjectProperty getProperty() {
	    return property;
	}

	/**
	 * Get the Individual value.
	 * @return the Individual value.
	 */
	public OWLIndividual getIndividual() {
	    return individual;
	}

    }

    /**
     * Pair <code>OWLDataProperty</code>, <code>OWLIndividual</code>.
     */
    static public class DataPropertyValue implements PropertyValue {

	OWLDataProperty property;
	OWLDataValue dataValue;

	/**
	 * Creates a new <code>DataPropertyValue</code> instance
	 * and sets property and dataValue.
	 *
	 * @param property an <code>OWLDataProperty</code> value
	 * @param dataValue an <code>OWLDataValue</code> value
	 */
	public DataPropertyValue(OWLDataProperty property,
				 OWLDataValue dataValue) 
	{
	    this.property = property;
	    this.dataValue = dataValue;
	}

	/**
	 * Get the Property value.
	 * @return the Property value.
	 */
	public OWLDataProperty getProperty() {
	    return property;
	}

	/**
	 * Get the DataValue value.
	 * @return the DataValue value.
	 */
	public OWLDataValue getDataValue() {
	    return dataValue;
	}

    }

    /**
     * Encapsulates the properties of a cardinality component.
     */
    static public class CardinalityComponent implements RestrictionComponent {

	final static public int MAX = 1;
	final static public int MIN = 2;
	final static public int EQ = 3;
	
	int type;
	int cardinality;

	/**
	 * Creates a new <code>CardinalityComponent</code> instance and sets
	 * its <code>type</code> property.
	 *
	 * @param type one of {@link #MAX}, {@link #MIN}, {@link #EQ}.
	 */
	public CardinalityComponent(int type) {
	    this.type=type;
	}

	/**
	 * Converts <code>tokenValue</code> to <code>int</code>
	 * and assigns it to the <code>cardinality</code> property.
	 *
	 * @param tokenValue a <code>String</code> convertible into
	 * an <code>int</code>.
	 * @throws TokenNotNumberError if <code>tokenValue</code> 
	 * is not convertible to an <code>int</code>.
	 */
	public void setCardinality(String tokenValue) {
	    try {
		cardinality = new Integer(tokenValue).intValue();
	    } catch(NumberFormatException e) {
		throw new TokenNotNumberError(e);
	    }
	}
	
	/**
	 * Gets the <code>cardinality</code> property.
	 *
	 * @return the <code>cardinality</code> property.
	 */
	public int getCardinality() { return cardinality; }

	/**
	 * Gets the <code>type</code> property.
	 *
	 * @return the <code>type</code> property.
	 */
	public int getType() { return type; }

	class TokenNotNumberError extends Error {
	    TokenNotNumberError(Throwable e) { super(e); }
	}

    }

    /**
     * Encapsulates the properties of a data literal value component.
     */
    static public class DataLiteralValueComponent 
	implements RestrictionComponent
    {
	OWLDataValue dataLiteral;

	/**
	 * Creates a new <code>DataLiteralValueComponent</code> instance
	 * and sets its <code>dataLiteral</code> property.
	 *
	 * @param dataLiteral a <code>String</code> representing 
	 * a data literal.
	 */
	public DataLiteralValueComponent(OWLDataValue dataLiteral) {
	    this.dataLiteral = dataLiteral;
	}

	/**
	 * Gets the <code>dataLiteral</code> property.
	 *
	 * @return the <code>dataLiteral</code> property.
	 */
	public OWLDataValue getDataLiteral() { return dataLiteral; }
    }

    /**
     * Encapsulates the properties of an individual value component.
     */
    static public class IndividualValueComponent 
	implements RestrictionComponent
    {
	URI value;

	/**
	 * Creates a new <code>IndividualValueComponent</code> instance
	 * and sets its <code>value</code> property to 
	 * <code>individualId</code>
	 *
	 * @param individualId a <code>URI</code>.
	 */
	public IndividualValueComponent(URI individualId) {
	    this.value = individualId;
	}

	/**
	 * Gets the <code>value</code> property.
	 *
	 * @return the <code>value</code> property.
	 */
	public URI getValue() { return value; }
    }

    /**
     * Encapsulates the properties of a description component.
     */
    static public class DescriptionComponent implements RestrictionComponent
    {
	final static public int ALL = 1;
	final static public int SOME = 2;
	
	OWLDescription description;
	int type;

	/**
	 * Creates a new <code>DescriptionComponent</code> instance and sets
	 * its <code>description</code> and <code>type</code> properties.
	 *
	 * @param type either {@link #ALL} or {@link #SOME}.
	 */
	public DescriptionComponent(OWLDescription description, int type) {
	    this.description = description;
	    this.type = type;
	}

	/**
	 * Gets the <code>description</code> property.
	 *
	 * @return the <code>description</code> property.
	 */
	public OWLDescription getDescription() { return description; }

	/**
	 * Gets the <code>type</code> property.
	 *
	 * @return the <code>type</code> property.
	 */
	public int getType() { return type; }
	
    }

    /**
     * Encapsulates the properties of a dataRange component.
     */
    static public class DataRangeComponent implements RestrictionComponent
    {
	final static public int ALL = 1;
	final static public int SOME = 2;
	
	OWLDataRange dataRange;
	int type;

	/**
	 * Creates a new <code>DataRangeComponent</code> instance and sets
	 * its <code>dataRange</code> and <code>type</code> properties.
	 *
	 * @param type either {@link #ALL} or {@link #SOME}.
	 */
	public DataRangeComponent(OWLDataRange dataRange, int type) {
	    this.dataRange = dataRange;
	    this.type = type;
	}

	/**
	 * Gets the <code>dataRange</code> property.
	 *
	 * @return the <code>dataRange</code> property.
	 */
	public OWLDataRange getDataRange() { return dataRange; }

	/**
	 * Gets the <code>type</code> property.
	 *
	 * @return the <code>type</code> property.
	 */
	public int getType() { return type; }
	
    }

    /**
     * Encapsulates the property and the content of an annotation.
     */
    static public class AnnotationComponent
    {
	OWLAnnotationProperty property;
	Object content;

	/**
	 * Creates a new <code>AnnotationComponent</code> instance and sets
	 * its <code>property</code> and <code>content</code> properties.
	 *
	 */
	public AnnotationComponent(OWLAnnotationProperty property, 
				   Object content) 
	{
	    this.property = property;
	    this.content = content;
	}

	/**
	 * Gets the <code>property</code> property.
	 *
	 * @return the <code>property</code> property.
	 */
	public OWLAnnotationProperty getProperty() { return property; }

	/**
	 * Gets the <code>content</code> property.
	 *
	 * @return the <code>content</code> property.
	 */
	public Object getContent() { return content; }
	
    }
    
    /**
     * Marker interface for restriction components.
     */
    public interface RestrictionComponent {}

    class CardinalityNotSupportedError extends Error {
	CardinalityNotSupportedError() { super(); }
    }

    class DescriptionRestrictionNotSupportedError extends Error {
	DescriptionRestrictionNotSupportedError() { super(); }
    }

    class DataRangeRestrictionNotSupportedError extends Error {
	DataRangeRestrictionNotSupportedError() { super(); }
    }


}

