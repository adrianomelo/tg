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

package org.semanticweb.owl.model; // Generated package name

import java.util.List;
import java.util.Set;

import org.semanticweb.owl.model.change.ChangeVisitor;
import java.net.URI;

/**
 * Represents an OWL ontology. An ontology comprises a number of
 * collections. Each ontology has a number of classes, properties and
 * individuals, along with a number of axioms asserting information
 * about those objects.
 *
 * @author Sean Bechhofer 
 * @version $Id: OWLOntology.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $ 
 */


public interface OWLOntology extends OWLNamedObject
{
	
    //    public ChangeVisitor getChangeVisitor() throws OWLException;	
	/**
     * Processes changes in the change list.
     *
     * @param changeList                list of changes to the model
     */
    //    void applyChanges(List changeList) throws OWLException;
	
    /** Returns a list of the classes of the ontology */
    public Set getClasses() throws OWLException;

	/**
     * getClass
     * 
     * Retrieves a class with a given uri
     * @param uri (String)
     * @return OWLClass, null if there is no such thing
     */
    public OWLClass getClass(URI uri) throws OWLException;


    /** Returns a list of the annotation properties of the ontology */
    public Set getAnnotationProperties() throws OWLException;
    
    /**
     * getAnnotationProperty
     * 
     * Retrieves an Annotation Property with a given uri
     * @param uri (String)
     * @return OWLAnnotationProperty, null if there is no such thing
     */
    public OWLAnnotationProperty getAnnotationProperty(URI uri) throws OWLException;
  
    /** Returns a list of the datatype properties of the ontology
     * (i.e. those whose range is a concrete data type). */
    public Set getDataProperties() throws OWLException;
    
    /**
     * getDataProperty
     * 
     * Retrieves a DataProperty with a given uri
     * @param uri (String)
     * @return OWLDataProperty, null if there is no such thing
     */
    public OWLDataProperty getDataProperty(URI uri) throws OWLException;
    
  
    /** Returns a list of the object properties of the ontology
     * (i.e. those whose range is a subset of the object domain. */
    public Set getObjectProperties() throws OWLException;
  
  /**
     * getObjectProperty
     * 
     * Retrieves a ObjectProperty with a given uri
     * @param uri (String)
     * @return OWLObjectProperty, null if there is no such thing
     */
    public OWLObjectProperty getObjectProperty(URI uri) throws OWLException;
    
    /** Returns a list of the individuals in the ontology */
    public Set getIndividuals() throws OWLException;
   
    /**
     * getIndividual
     * 
     * Retrieves an individual with a given uri
     * @param uri (String)
     * @return OWLDataProperty, returns null if it is there
     */
    public OWLIndividual getIndividual(URI uri) throws OWLException;


    /** Returns a list of the classes of the ontology */
    public Set getDatatypes() throws OWLException;

	/**
     * getClass
     * 
     * Retrieves a class with a given uri
     * @param uri (String)
     * @return OWLClass, null if there is no such thing
     */
    public OWLDataType getDatatype(URI uri) throws OWLException;
    
    /** Returns a list of class axioms in the ontology. These include
     * subclass axioms, disjointness axioms and equality axioms. */
    public Set getClassAxioms() throws OWLException;

    /** Returns a list of the property axioms in the ontology. These
     * include subproperty axioms and property equality axioms. */
    public Set getPropertyAxioms() throws OWLException;

    /** Returns a list of the individual axioms in the ontology. These
     * will include axioms asserting the equality and inequality of
     * individuals. */
    public Set getIndividualAxioms() throws OWLException;

    /** Returns a list of the Ontologies imported by this Ontology. 
     * @return Set{OWLOntology} */
    public Set getIncludedOntologies() throws OWLException;

    /* Returns a list of the Ontologies related via the
     * owl:priorVersion property */
    public Set getPriorVersion() throws OWLException;

    /* Returns a list of the Ontologies related via the
     * owl:backwardCompatibleWith property */
    public Set getBackwardCompatibleWith() throws OWLException;

    /* Returns a list of the Ontologies related via the
     * owl:incompatibleWith property */
    public Set getIncompatibleWith() throws OWLException;

    /**
     * Returns <code>true</code> if the ontology is mutable, i.e. can
     * be changed. If the ontology has been retrieved from a URI (for
     * example as an imported ontology), then the ontology will be
     * considered immutable.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isMutable() throws OWLException;
    
    /**
	 * getLogicalURI
	 * 
	 * Identifies this ontology uniquely (enforced within one
	 * OWLConnection)
	 * 
	 * @return String representation of the URI
	 */
	public URI getLogicalURI() throws OWLException;
	
	  /**
	 * getLogicalURI
	 * 
	 * Physical URI of the ontology. 
	 * @return String representaion of the URI, could be null (e.g.
	 * not stored yet or in a database)
	 */
	public URI getPhysicalURI() throws OWLException;
	
	

}// OWLOntology


/*
 * ChangeLog
 * $Log $
 * 
 */


