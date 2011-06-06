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

package org.semanticweb.owl.impl.model; // Generated package name

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentPropertiesAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;
import org.semanticweb.owl.model.change.AddAllEntitiesStrategy;
import org.semanticweb.owl.model.change.AddAnnotationInstance;
import org.semanticweb.owl.model.change.AddClassAxiom;
import org.semanticweb.owl.model.change.AddDataPropertyInstance;
import org.semanticweb.owl.model.change.AddDataPropertyRange;
import org.semanticweb.owl.model.change.AddDataType;
import org.semanticweb.owl.model.change.AddDomain;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.AddEnumeration;
import org.semanticweb.owl.model.change.AddEquivalentClass;
import org.semanticweb.owl.model.change.AddImport;
import org.semanticweb.owl.model.change.AddIndividualAxiom;
import org.semanticweb.owl.model.change.AddIndividualClass;
import org.semanticweb.owl.model.change.AddInverse;
import org.semanticweb.owl.model.change.AddObjectPropertyInstance;
import org.semanticweb.owl.model.change.AddObjectPropertyRange;
import org.semanticweb.owl.model.change.AddPropertyAxiom;
import org.semanticweb.owl.model.change.AddSuperClass;
import org.semanticweb.owl.model.change.AddSuperProperty;
import org.semanticweb.owl.model.change.AddEquivalentClass;
import org.semanticweb.owl.model.change.AddEnumeration;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.ChangeVisitorAdapter;
import org.semanticweb.owl.model.change.EvolutionStrategy;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.OntologyChangeListener;
import org.semanticweb.owl.model.change.OntologyChangeSource;
import org.semanticweb.owl.model.change.RemoveAnnotationInstance;
import org.semanticweb.owl.model.change.RemoveClassAxiom;
import org.semanticweb.owl.model.change.RemoveDataPropertyInstance;
import org.semanticweb.owl.model.change.RemoveDataPropertyRange;
import org.semanticweb.owl.model.change.RemoveDataType;
import org.semanticweb.owl.model.change.RemoveDomain;
import org.semanticweb.owl.model.change.RemoveEntity;
import org.semanticweb.owl.model.change.RemoveEnumeration;
import org.semanticweb.owl.model.change.RemoveEquivalentClass;
import org.semanticweb.owl.model.change.RemoveImport;
import org.semanticweb.owl.model.change.RemoveIndividualAxiom;
import org.semanticweb.owl.model.change.RemoveIndividualClass;
import org.semanticweb.owl.model.change.RemoveInverse;
import org.semanticweb.owl.model.change.RemoveObjectPropertyInstance;
import org.semanticweb.owl.model.change.RemoveObjectPropertyRange;
import org.semanticweb.owl.model.change.RemovePropertyAxiom;
import org.semanticweb.owl.model.change.RemoveSuperClass;
import org.semanticweb.owl.model.change.RemoveSuperProperty;
import org.semanticweb.owl.model.change.SetDeprecated;
import org.semanticweb.owl.model.change.SetFunctional;
import org.semanticweb.owl.model.change.SetInverseFunctional;
import org.semanticweb.owl.model.change.SetLogicalURI;
import org.semanticweb.owl.model.change.SetOneToOne;
import org.semanticweb.owl.model.change.SetSymmetric;
import org.semanticweb.owl.model.change.SetTransitive;


/**
 * Represents an OWL ontology. An ontology comprises a number of
 * collections. Each ontology has a number of classes, properties and
 * individuals, along with a number of axioms asserting information
 * about those objects. 

 * This is a very simple implementation of an Ontology. Nothing
 * special in here -- just a basic data structure with minimal
 * functionality. 
 * 
 * The class implements the change visitor interface which allows
 * manipulation of the ontology.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLOntologyImpl.java,v 1.5 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public class OWLOntologyImpl
    extends OWLNamedObjectImpl
    implements OWLOntology, ChangeVisitor, OntologyChangeSource {

    protected HashMap classes;
    protected HashMap annotationProperties;
    protected HashMap dataProperties;
    protected HashMap objectProperties;
    protected HashMap individuals;
    protected HashMap datatypes;
    protected int anonymousIndividuals;
    protected Set classAxioms;
    protected Set propertyAxioms;
    protected Set individualAxioms;
    protected Set importedOntologies;
    protected boolean mutable;

    protected URI physicalURI;

    /* A visitor which gets handed off any change requests. This
     * allows us to use the visitoradapter. */

    protected ChangeVisitor visitorProxy;

    /* Things that are listening to changes in this ontology */
    protected Set changeListeners;

    static Logger logger = Logger.getLogger(OWLOntologyImpl.class);

    public OWLOntologyImpl(
        OWLDataFactoryImpl factory,
        URI logicalURI,
        URI physicalURI) {
        super(factory);
        this.uri = logicalURI; // LogicalURI
        this.physicalURI = physicalURI;
        this.classes = new HashMap(); // ListFactory.getSet();
        this.annotationProperties = new HashMap(); // ListFactory.getSet();
        this.dataProperties = new HashMap(); // ListFactory.getSet();
        this.objectProperties = new HashMap(); // ListFactory.getSet();
        this.individuals = new HashMap(); // ListFactory.getSet();
        this.datatypes = new HashMap(); // ListFactory.getSet();
	anonymousIndividuals = 0;
        this.classAxioms = ListFactory.getSet();
        this.propertyAxioms = ListFactory.getSet();
        this.individualAxioms = ListFactory.getSet();
        this.importedOntologies = ListFactory.getSet();
        this.mutable = true;
	/* Set up the visitor proxy */
	this.visitorProxy = getChangeVisitor();
	this.changeListeners = ListFactory.getSet();
    }

    /**
     * Processes changes in the change list.
     *
     * @param changeList                list of changes to the model
     */
    public void applyChanges(List changeList) throws OWLException {
        logger.info("Implement me!");
    }

    public URI getPhysicalURI() {
        return physicalURI;
    }

    public URI getLogicalURI() {
        return uri;
    }

    /** Returns a list of the classes of the ontology */
    public Set getClasses() {
        return ListFactory.getSet(classes.values());
    }

    /** Returns a list of the annotation properties of the ontology */
    public Set getAnnotationProperties() {
        return ListFactory.getSet(annotationProperties.values());
    }

    /** Returns a list of the datatype properties of the ontology
     * (i.e. those whose range is a concrete data type). */
    public Set getDataProperties() {
        return ListFactory.getSet(dataProperties.values());
    }

    /** Returns a list of the object properties of the ontology
     * (i.e. those whose range is a subset of the object domain. */
    public Set getObjectProperties() {
        return ListFactory.getSet(objectProperties.values());
    }

    /** Returns a list of the individuals in the ontology */
    public Set getIndividuals() {
        return ListFactory.getSet(individuals.values());
    }

    /** Returns a list of the classes of the ontology */
    public Set getDatatypes() {
        return ListFactory.getSet(datatypes.values());
    }

    /** Returns a list of class axioms in the ontology. These include
     * subclass axioms, disjointness axioms and equality axioms. */
    public Set getClassAxioms() {
        return ListFactory.getSet(classAxioms);
    }

    /** Returns a list of the property axioms in the ontology. These
     * include subproperty axioms and property equality axioms. */
    public Set getPropertyAxioms() {
        return ListFactory.getSet(propertyAxioms);
    }

    /** Returns a list of the individual axioms in the ontology. These
     * will include axioms asserting the equality and inequality of
     * individuals. */
    public Set getIndividualAxioms() {
        return ListFactory.getSet(individualAxioms);
    }

    /** Returns a list of the Ontologies imported by this Ontology. */
    public Set getIncludedOntologies() {
        return ListFactory.getSet(importedOntologies);
    }

    /* Returns a list of the Ontologies related via the
     * owl:priorVersion property */
    public Set getPriorVersion() {
	return ListFactory.getSet();
    };

    /* Returns a list of the Ontologies related via the
     * owl:backwardCompatibleWith property */
    public Set getBackwardCompatibleWith() {
	return ListFactory.getSet();
    }

    /* Returns a list of the Ontologies related via the
     * owl:incompatibleWith property */
    public Set getIncompatibleWith() {
	return ListFactory.getSet();
    }

    /**
     * Returns <code>true</code> if the ontology is mutable, i.e. can
     * be changed. If the ontology has been retrieved from a URI (for
     * example as an imported ontology), then the ontology will be
     * considered immutable.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isMutable() {
        return mutable;
    }

    public void accept(OWLObjectVisitor visitor) throws OWLException {
        visitor.visit(this);
    }

    /** Returns a {@link ChangeVisitor ChangeVisitor} that will apply
     * change events to this ontology. The visitor will also notify
     * any listeners that the change has occurred. */

    protected ChangeVisitor getChangeVisitor() {
        return new ChangeVisitorAdapter() {
            /* Visit methods for implementation of changes. */

            public void visit(AddEntity event) throws OWLException {
                /* Adds a new entity to the the ontology */
                /* Check that the event is appropriate */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Create an entity adder to deal with the addition */
                    logger.debug("Adding: " + event);

                    EntityAdder adder = new EntityAdder( event );
                    event.getEntity().accept(adder);
		    /* There is no need to propagate the event here as
		     * this will be done by the entity adder (if the
		     * object has really been added). */
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            public void visit(AddDataType event) throws OWLException {
                /* Adds a new entity to the the ontology */
                /* Check that the event is appropriate */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Create an entity adder to deal with the addition */
                    logger.debug("Adding: " + event);

		    datatypes.put( event.getDatatype().getURI(), 
				   event.getDatatype() );
		    notifyListeners( event );
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }
            
            public void visit(RemoveDataType event) throws OWLException {
				/* Check that the event is appropriate */
				if (event.getOntology() == OWLOntologyImpl.this) {
					/* Create an entity adder to deal with the addition */
					logger.debug("Removing: " + event);
					datatypes.remove(event.getDatatype().getURI());
					notifyListeners(event);
				} else {
					throw new OWLException("Inappropriate change event");
				}
			}

            public void visit(RemoveEntity event) throws OWLException {
                /* Adds a new entity to the the ontology */
                /* Check that the event is appropriate */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Create an entity remover to deal with the addition */
                    logger.debug("Removing: " + event.getEntity().getURI());

                    EntityRemover remover = new EntityRemover();
                    event.getEntity().accept(remover);
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
             * 
             *
             * @param event an <code>AddIndividualAxiom</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddIndividualAxiom event) throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    individualAxioms.add(event.getAxiom());
		    notifyListeners( event );
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }
            
            public void visit(RemoveIndividualAxiom event) throws OWLException {
                /* Removes an axiom from the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Remove from the appropriate collection */
                    individualAxioms.remove(event.getAxiom());
		    notifyListeners( event );
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
             * 
             *
             * @param event an <code>AddClassAxiom</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddClassAxiom event) throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Axiom: " + event.getAxiom());
                    classAxioms.add(event.getAxiom());
		    notifyListeners( event );
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /*** 
             * Remove Class Axiom from Ontology
             */
            public void visit(RemoveClassAxiom event) throws OWLException {
                /* Removes a class axiom from the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Remove from the appropriate collection */
                    logger.debug("Axiom: " + event.getAxiom());
                    OWLClassAxiom removeAxiom = event.getAxiom();
                    // axiom cannot be removed from classAxioms Set directly without checking its components (hashCode don't match)
                    // need to match removeAxiom with all current class axioms 
                    Set removeAxiomSet = new HashSet();
                    Iterator iter = classAxioms.iterator();
                    while (iter.hasNext()) {
                    	OWLClassAxiom axiom = (OWLClassAxiom) iter.next();
                    	// check for DisjointClasses 
                    	if ((axiom instanceof OWLDisjointClassesAxiom) && (removeAxiom instanceof OWLDisjointClassesAxiom)) {
                    		if (((OWLDisjointClassesAxiom) axiom).getDisjointClasses().equals(((OWLDisjointClassesAxiom) removeAxiom).getDisjointClasses())) {
                    			removeAxiomSet.add(axiom);
                    		}
                    	}
                    	
                    	// check for equivalent classes
                    	if ((axiom instanceof OWLEquivalentClassesAxiom) && (removeAxiom instanceof OWLEquivalentClassesAxiom)) {
                    		if (((OWLEquivalentClassesAxiom) axiom).getEquivalentClasses().equals(((OWLEquivalentClassesAxiom) removeAxiom).getEquivalentClasses())) {
                    			removeAxiomSet.add(axiom);
                    		}
                    	}
                    	
                    	// check for subclass axiom
                    	if ((axiom instanceof OWLSubClassAxiom) && (removeAxiom instanceof OWLSubClassAxiom)) {
                    		OWLSubClassAxiom subCAxiom = (OWLSubClassAxiom) axiom;
                    		OWLSubClassAxiom remSCAxiom = (OWLSubClassAxiom) removeAxiom;
                    		if (subCAxiom.getSubClass().equals(remSCAxiom.getSubClass()) && subCAxiom.getSuperClass().equals(remSCAxiom.getSuperClass())) {
                    			removeAxiomSet.add(axiom);
                    		}
                    	}
                    }
                    classAxioms.removeAll(removeAxiomSet);
                    
		    notifyListeners( event );
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }
            /** */
            
 /**
             * 
             *
             * @param event an <code>AddPropertyAxiom</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddPropertyAxiom event) throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    propertyAxioms.add(event.getAxiom());
		    notifyListeners( event );
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /** 
             * @param event an <code>RemovePropertyAxiom</code> value
             * @exception OWLException if an error occurs
             * 
             */
            public void visit(RemovePropertyAxiom event) throws OWLException {
                /* Removes an axiom from the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    
                /*** equivalent properties assertions are not made via axioms, 
                 *   rather by two super-property assertions
				*/
//                	/* Removes from the appropriate collection */
//                	OWLPropertyAxiom removeAxiom = event.getAxiom();
//                    // axiom cannot be removed from propertyAxioms Set directly without checking its components (hashCode don't match)
//                    // need to match removeAxiom with all current property axioms 
//                    Set removeAxiomSet = new HashSet();
//                    Iterator iter = propertyAxioms.iterator();
//                    while (iter.hasNext()) {
//                    	OWLPropertyAxiom axiom = (OWLPropertyAxiom) iter.next();
//                    	// only check for Equivalent Properties since Super Properties can be removed using their corresponding interfaces
//                    	if ((axiom instanceof OWLEquivalentPropertiesAxiom) && (removeAxiom instanceof OWLEquivalentPropertiesAxiom)) {
//                    		if (((OWLEquivalentPropertiesAxiom) axiom).getProperties().equals(((OWLEquivalentPropertiesAxiom) removeAxiom).getProperties())) {
//                    			removeAxiomSet.add(axiom);
//                    		}
//                    	}
//                    }
//                    propertyAxioms.removeAll(removeAxiomSet);
                	
                 /***
                  * instead for RemoveEquivalentPropertiesAxiom remove two corresponding 
                  * superproperty assertions for properties in axiom set 
                  */
//                	if (event.getAxiom() instanceof OWLEquivalentPropertiesAxiom) {
//                		Set propertySet = ((OWLEquivalentPropertiesAxiom) event.getAxiom()).getProperties();
//                		Object[] props = propertySet.toArray();
//                		// for now assume only two properties in set
//                		if (props.length==2) {
//                			RemoveSuperProperty change1 = new RemoveSuperProperty(event.getOntology(), (OWLProperty) props[0], (OWLProperty) props[1], null);
//                			visit(change1);
//                			RemoveSuperProperty change2 = new RemoveSuperProperty(event.getOntology(), (OWLProperty) props[1], (OWLProperty) props[0], null);
//                			visit(change2);
//                		}
//                	}
//                	else if (event.getAxiom() instanceof OWLSubPropertyAxiom) {
                		
//                		 axiom cannot be removed from propertyAxioms Set directly without checking its components (hashCode don't match)
                        // need to match removeAxiom with all current property axioms 
                		OWLPropertyAxiom removeAxiom  = (OWLPropertyAxiom) event.getAxiom();                		
                		Set removeAxiomSet = new HashSet();
                        Iterator iter = propertyAxioms.iterator();
                        while (iter.hasNext()) {
                        	
                        	OWLPropertyAxiom axiom = (OWLPropertyAxiom) iter.next();
                        	
                        	// remove equivalent property axiom 
                        	if (axiom instanceof OWLEquivalentPropertiesAxiom && removeAxiom instanceof OWLEquivalentPropertiesAxiom) {
                        		try {                        			
                        			OWLEquivalentPropertiesAxiom equpAxiom = (OWLEquivalentPropertiesAxiom) axiom;
                        			OWLEquivalentPropertiesAxiom remEPAxiom = (OWLEquivalentPropertiesAxiom) removeAxiom;
	                        		if (equpAxiom.getProperties().equals(remEPAxiom.getProperties())) {
	                        			removeAxiomSet.add(axiom);
	                        		}
                        		}
                        		catch (OWLException e) {
                        			e.printStackTrace();
                        		}
                        	}
                        	
                        	// remove subproperty axiom 
                        	if (axiom instanceof OWLSubPropertyAxiom && removeAxiom instanceof OWLSubPropertyAxiom) {
                        		try {                        			
                        			OWLSubPropertyAxiom subpAxiom = (OWLSubPropertyAxiom) axiom;
                        			OWLSubPropertyAxiom remSPAxiom = (OWLSubPropertyAxiom) removeAxiom;
	                        		if (subpAxiom.getSubProperty().equals(remSPAxiom.getSubProperty()) && subpAxiom.getSuperProperty().equals(remSPAxiom.getSuperProperty())) {
	                        			removeAxiomSet.add(axiom);
	                        		}
                        		}
                        		catch (OWLException e) {
                        			e.printStackTrace();
                        		}
                        	}
                        }
                        propertyAxioms.removeAll(removeAxiomSet);                		
//                	}
                	
                	
		    notifyListeners( event );
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
            	 * 
            	 *
            	 * @param event an <code>AddSuperClass</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(AddSuperClass event) throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
		    /* No need to do this now -- use OntologyHelper to
		     * get things as axioms. SKB */
                    //classAxioms.add(event.getAxiom());

                    logger.debug("Added: " + event);
                    try {
                        /* Try adding the description to the
                         * superclasses of the class. */
                        ((OWLClassImpl) event.getOWLClass()).addSuperClass(
                            OWLOntologyImpl.this,
                            event.getDescription());
                        
                        /* 01/31/05 Also try adding subclasses accordingly 
                         * Only for named classes now */
                        if (event.getDescription() instanceof OWLClassImpl) {                        	
                        	((OWLClassImpl) event.getDescription()).addSubClass(OWLOntologyImpl.this, event.getOWLClass());
                        }
                        
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getOWLClass().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
            	 * 
            	 *
            	 * @param event an <code>RemoveSuperClass</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(RemoveSuperClass event) throws OWLException {
                /* Removes a superclass */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Remove from the appropriate collection */
		    
                    logger.debug("Removed: " + event);
                    try {
                        /* Try removing the description. */
                        ((OWLClassImpl) event.getOWLClass()).removeSuperClass(
                            OWLOntologyImpl.this,
                            event.getDescription());
                        
                        /* 01/31/05: also do the same for subclasses accordingly
                         * only named classes for now
                         */
                        if (event.getDescription() instanceof OWLClassImpl) {
                        	((OWLClassImpl) event.getDescription()).removeSubClass(
                                    OWLOntologyImpl.this,
                                    event.getOWLClass());	
                        }                        
                        
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
			    + event.getOWLClass().getClass()
			    + " in "
			    + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
            	 * 
            	 *
            	 * @param event an <code>AddEquivalentClass</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(AddEquivalentClass event) throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        /* Try adding the description to the
                         * superclasses of the class. */
                        (
                            (OWLClassImpl) event
                                .getOWLClass())
                                .addEquivalentClass(
                            OWLOntologyImpl.this,
                            event.getDescription());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getOWLClass().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
            	 * 
            	 *
            	 * @param event an <code>RemoveEquivalentClass</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(RemoveEquivalentClass event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Remove from the appropriate collection */
                    logger.debug("Removed: " + event);
                    try {
                        /* Try removing the description. */
                        (
                            (OWLClassImpl) event
                                .getOWLClass())
                                .removeEquivalentClass(
                            OWLOntologyImpl.this,
                            event.getDescription());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getOWLClass().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
             * 
             *
             * @param event an <code>AddEnumeration</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddEnumeration event) throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        /* Try adding the enumeration to the
                         * superclasses of the class. */
                        ((OWLClassImpl) event.getOWLClass()).addEnumeration(
                            OWLOntologyImpl.this,
                            event.getEnumeration());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getOWLClass().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * 
             *
             * @param event an <code>RemoveEnumeration</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(RemoveEnumeration event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Remove from the appropriate collection */
                    logger.debug("Removed: " + event);
                    try {
                        /* Try removing the enumeration. */
                        ((OWLClassImpl) event.getOWLClass()).removeEnumeration(
                            OWLOntologyImpl.this,
                            event.getEnumeration());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getOWLClass().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
            	 * 
            	 *
            	 * @param event an <code>AddSuperProperty</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(AddSuperProperty event) throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
		    /* No need to do this now -- use OntologyHelper to
		     * get things as axioms. SKB */
                    //classAxioms.add(event.getAxiom());

                    logger.debug("Added: " + event);
                    try {
                        /* Try adding the description to the
                         * superclasses of the class. */
                        ((OWLPropertyImpl) event.getProperty()).addSuperProperty(
                            OWLOntologyImpl.this,
                            event.getSuperProperty());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
			    + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
        	 * @param event an <code>RemoveSuperProperty</code> value
        	 * @exception OWLException if an error occurs
        	 * 
        	 */
            public void visit(RemoveSuperProperty event) throws OWLException {
                /* Removes an axiom from the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Removes from the appropriate collection */
		    /* No need to do this now -- use OntologyHelper to
		     * get things as axioms. SKB */
                    //classAxioms.add(event.getAxiom());

                    logger.debug("Removed: " + event);
                    try {
                        /* Try removing the description to the
                         * superproperties of the property. */
                        ((OWLPropertyImpl) event.getProperty()).removeSuperProperty(
                            OWLOntologyImpl.this,
                            event.getSuperProperty());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
			    + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
            	 * 
            	 *
            	 * @param event an <code>AddIndividualClass</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(AddIndividualClass event) throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        /* Try adding the description to the
                         * classes of the individual. */
                        ((OWLIndividualImpl) event.getIndividual()).addType(
                            OWLOntologyImpl.this,
                            event.getDescription());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getIndividual().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
        	 * @param event an <code>RemoveIndividualClass</code> value
        	 * @exception OWLException if an error occurs
        	 * 
        	 */
        public void visit(RemoveIndividualClass event) throws OWLException {
            /* Removes an axiom from the the ontology */
            /* Check that the event is appropriate */
            if (event.getOntology() == OWLOntologyImpl.this) {
                /* Remove from the appropriate collection */
                logger.debug("Removed: " + event);
                try {
                    /* Try removing the description to the
                     * classes of the individual. */
                    ((OWLIndividualImpl) event.getIndividual()).removeType(
                        OWLOntologyImpl.this,
                        event.getDescription());
                    notifyListeners( event );
                } catch (ClassCastException ex) {
                    throw new OWLException(
                        "Trying to use unsuitable implementation class: "
                            + event.getIndividual().getClass()
                            + " in "
                            + OWLOntologyImpl.this);
                }
            } else {
                throw new OWLException("Inappropriate change event");
            }
        }
            
            /**
            	 * 
            	 *
            	 * @param event an <code>AddObjectPropertyInstance</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(AddObjectPropertyInstance event)
                throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        /* Try adding the reltationship to the
                         * relationships of the individual. */
                        (
                            (OWLIndividualImpl) event
                                .getSubject())
                                .addObjectPropertyValue(
                            OWLOntologyImpl.this,
                            event.getProperty(),
                            event.getObject());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getSubject().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
        	 * 
        	 *
        	 * @param event an <code>RemoveObjectPropertyInstance</code> value
        	 * @exception OWLException if an error occurs
        	 * 
        	 */
        public void visit(RemoveObjectPropertyInstance event)
            throws OWLException {
            /* Removes an axiom from the the ontology */
            /* Check that the event is appropriate */
            if (event.getOntology() == OWLOntologyImpl.this) {
                /* Add to the appropriate collection */
                logger.debug("Removed: " + event);
                try {
                    /* Try removing the relationship to the
                     * relationships of the individual. */
                    (
                        (OWLIndividualImpl) event
                            .getSubject())
                            .removeObjectPropertyValue(
                        OWLOntologyImpl.this,
                        event.getProperty(),
                        event.getObject());
		notifyListeners( event );
                } catch (ClassCastException ex) {
                    throw new OWLException(
                        "Trying to use unsuitable implementation class: "
                            + event.getSubject().getClass()
                            + " in "
                            + OWLOntologyImpl.this);
                }
            } else {
                throw new OWLException("Inappropriate change event");
            }
        }
            
            /**
            	 * 
            	 *
            	 * @param event an <code>AddDataPropertyInstance</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(AddDataPropertyInstance event)
                throws OWLException {
                /* Adds a new axiom to the the ontology */
                /* Check that the event is appropriate */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        /* Try adding the reltationship to the
                         * relationships of the individual. */
                        (
                            (OWLIndividualImpl) event
                                .getSubject())
                                .addDataPropertyValue(
                            OWLOntologyImpl.this,
                            event.getProperty(),
                            event.getObject());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getSubject().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
        	 * @param event an <code>RemoveDataPropertyInstance</code> value
        	 * @exception OWLException if an error occurs
        	 * 
        	 */
        public void visit(RemoveDataPropertyInstance event)
            throws OWLException {
            /* Removes an axiom from the the ontology */
            /* Check that the event is appropriate */
            if (event.getOntology() == OWLOntologyImpl.this) {
                /* Removes from the appropriate collection */
                logger.debug("Removed: " + event);
                try {
                    /* Try removing the relationship to the
                     * relationships of the individual. */
                    (
                        (OWLIndividualImpl) event
                            .getSubject())
                            .removeDataPropertyValue(
                        OWLOntologyImpl.this,
                        event.getProperty(),
                        event.getObject());
		notifyListeners( event );
                } catch (ClassCastException ex) {
                    throw new OWLException(
                        "Trying to use unsuitable implementation class: "
                            + event.getSubject().getClass()
                            + " in "
                            + OWLOntologyImpl.this);
                }
            } else {
                throw new OWLException("Inappropriate change event");
            }
        }
            
            /**
             * 
             *
             * @param event a <code>SetDeprecated</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(SetDeprecated event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLDeprecatableObjectImpl odc =
                            (OWLDeprecatableObjectImpl) event.getObject();
                        odc.setDeprecated(
                            OWLOntologyImpl.this,
                            event.isDeprecated());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getObject().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
            	 * 
            	 *
            	 * @param event an <code>AddAnnotationInstance</code> value
            	 * @exception OWLException if an error occurs
            	 */
            public void visit(AddAnnotationInstance event) throws OWLException {
                /* Adds an annotation to the the ontology */
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
		    /* No need to do this now -- use OntologyHelper to
		     * get things as axioms. SKB */
                    //classAxioms.add(event.getAxiom());

                    logger.debug("Added: " + event);
                    try {
			OWLAnnotationInstance annotationInstance = 
			    getOWLDataFactory().getOWLAnnotationInstance( event.getSubject(), 
									  event.getProperty(), 
									  event.getContent() );
                        ((OWLObjectImpl) event.getSubject()).addAnnotation(OWLOntologyImpl.this, 
									   annotationInstance);
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
					       "Trying to use unsuitable implementation class: "
					       + event.getSubject().getClass()
					       + " in "
					       + OWLOntologyImpl.this);
                    }
                } else {
                    throw new OWLException("Inappropriate change event");
                }
            }

            /**
        	 * @param event an <code>RemoveAnnotationInstance</code> value
        	 * @exception OWLException if an error occurs
        	 * 
        	 */
        public void visit(RemoveAnnotationInstance event) throws OWLException {
            /* Removes an annotation from the the ontology */
            /* Check that the event is appropriate */

            /* Do we also need to check that all the things used in the
             * axiom are in the ontology???? */
            if (event.getOntology() == OWLOntologyImpl.this) {
                /* Removes from the appropriate collection */
	    /* No need to do this now -- use OntologyHelper to
	     * get things as axioms. SKB */
                //classAxioms.add(event.getAxiom());
			logger.debug("Removed: " + event);
                try {
		OWLAnnotationInstance annotationInstance = 
		    getOWLDataFactory().getOWLAnnotationInstance( event.getSubject(), 
								  event.getProperty(), 
								  event.getContent() );
                    ((OWLObjectImpl) event.getSubject()).removeAnnotation(OWLOntologyImpl.this, 
								   annotationInstance);
		notifyListeners( event );
                } catch (ClassCastException ex) {
                    throw new OWLException(
				       "Trying to use unsuitable implementation class: "
				       + event.getSubject().getClass()
				       + " in "
				       + OWLOntologyImpl.this);
                }
            } else {
                throw new OWLException("Inappropriate change event");
            }
        }
            
            /**
             * 
             *
             * @param event a <code>SetFunctional</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(SetFunctional event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLPropertyImpl prop =
                            (OWLPropertyImpl) event.getProperty();
                        prop.setFunctional(
                            OWLOntologyImpl.this,
                            event.isFunctional());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * 
             *
             * @param event a <code>SetInverseFunctional</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(SetInverseFunctional event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLObjectPropertyImpl prop =
                            (OWLObjectPropertyImpl) event.getProperty();
                        prop.setInverseFunctional(
                            OWLOntologyImpl.this,
                            event.isInverseFunctional());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * 
             *
             * @param event a <code>SetSymmetric</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(SetSymmetric event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLObjectPropertyImpl prop =
                            (OWLObjectPropertyImpl) event.getProperty();
                        prop.setSymmetric(
                            OWLOntologyImpl.this,
                            event.isSymmetric());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * 
             *
             * @param event a <code>SetTransitive</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(SetTransitive event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLObjectPropertyImpl prop =
                            (OWLObjectPropertyImpl) event.getProperty();
                        prop.setTransitive(
                            OWLOntologyImpl.this,
                            event.isTransitive());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * 
             *
             * @param event a <code>SetOneToOne</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(SetOneToOne event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLObjectPropertyImpl prop =
                            (OWLObjectPropertyImpl) event.getProperty();
                        prop.setOneToOne(
                            OWLOntologyImpl.this,
                            event.isOneToOne());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * 
             *
             * @param event an <code>AddInverse</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddInverse event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLObjectPropertyImpl prop =
                            (OWLObjectPropertyImpl) event.getProperty();
                        prop.addInverse(OWLOntologyImpl.this, event.getInverse());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
					       "Trying to use unsuitable implementation class: "
					       + event.getProperty().getClass()
					       + " in "
					       + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * @param event an <code>RemoveInverse</code> value
             * @exception OWLException if an error occurs
             * 
             */
            public void visit(RemoveInverse event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Removed: " + event);
                    try {
                        OWLObjectPropertyImpl prop =
                            (OWLObjectPropertyImpl) event.getProperty();
                        prop.removeInverse(OWLOntologyImpl.this, event.getInverse());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
					       "Trying to use unsuitable implementation class: "
					       + event.getProperty().getClass()
					       + " in "
					       + OWLOntologyImpl.this);
                    }
                }
            }
            
            /**
             * 
             *
             * @param event an <code>AddDomain</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddDomain event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLPropertyImpl prop =
                            (OWLPropertyImpl) event.getProperty();
                        prop.addDomain(OWLOntologyImpl.this, event.getDomain());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * @param event an <code>RemoveDomain</code> value
             * @exception OWLException if an error occurs
             * 
             */
            public void visit(RemoveDomain event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Removes from the appropriate collection */
                    logger.debug("Removed: " + event);
                    try {
                        OWLPropertyImpl prop =
                            (OWLPropertyImpl) event.getProperty();
                        prop.removeDomain(OWLOntologyImpl.this, event.getDomain());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }
            
            /**
             * 
             *
             * @param event an <code>AddIndividualRange</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddObjectPropertyRange event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLObjectPropertyImpl prop =
                            (OWLObjectPropertyImpl) event.getProperty();
                        prop.addRange(OWLOntologyImpl.this, event.getRange());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * @param event an <code>RemoveIndividualRange</code> value
             * @exception OWLException if an error occurs
             * 
             */
            public void visit(RemoveObjectPropertyRange event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Removes from the appropriate collection */
                    logger.debug("Removed: " + event);
                    try {
                        OWLObjectPropertyImpl prop =
                            (OWLObjectPropertyImpl) event.getProperty();
                        prop.removeRange(OWLOntologyImpl.this, event.getRange());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }
            
            /**
             * 
             *
             * @param event an <code>AddDataRange</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddDataPropertyRange event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLDataPropertyImpl prop =
                            (OWLDataPropertyImpl) event.getProperty();
                        prop.addRange(OWLOntologyImpl.this, event.getRange());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * @param event an <code>RemoveDataRange</code> value
             * @exception OWLException if an error occurs
             * 
             */
            public void visit(RemoveDataPropertyRange event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Removes from the appropriate collection */
                    logger.debug("Removed: " + event);
                    try {
                        OWLDataPropertyImpl prop =
                            (OWLDataPropertyImpl) event.getProperty();
                        prop.removeRange(OWLOntologyImpl.this, event.getRange());
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getProperty().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }
            
            /**
             * 
             *
             * @param event a <code>AddImport</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(AddImport event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Added: " + event);
                    try {
                        OWLOntologyImpl onto =
                            (OWLOntologyImpl) event.getImportOntology();
                        OWLOntologyImpl.this.importedOntologies.add(onto);
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getOntology().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }

            /**
             * @param event a <code>RemoveImport</code> value
             * @exception OWLException if an error occurs
             * 
             */
            public void visit(RemoveImport event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Removed: " + event);
                    try {
                        OWLOntologyImpl onto =
                            (OWLOntologyImpl) event.getImportOntology();
                        OWLOntologyImpl.this.importedOntologies.remove(onto);
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getOntology().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }
            
            /**
             * 
             *
             * @param event a <code>SetLogicalURI</code> value
             * @exception OWLException if an error occurs
             */
            public void visit(SetLogicalURI event) throws OWLException {
                /* Check that the event is appropriate */

                /* Do we also need to check that all the things used in the
                 * axiom are in the ontology???? */
                if (event.getOntology() == OWLOntologyImpl.this) {
                    /* Add to the appropriate collection */
                    logger.debug("Change: " + event);
                    try {
			((OWLConnectionImpl) OWLOntologyImpl.this.getOWLConnection()).changeOntologyLogicalURI( OWLOntologyImpl.this, event.getURI() );
			OWLOntologyImpl.this.uri = event.getURI();
			notifyListeners( event );
                    } catch (ClassCastException ex) {
                        throw new OWLException(
                            "Trying to use unsuitable implementation class: "
                                + event.getOntology().getClass()
                                + " in "
                                + OWLOntologyImpl.this);
                    }
                }
            }



        };
    }

    protected class EntityAdder implements OWLEntityVisitor {

	protected OntologyChange cause;
	
	EntityAdder( OntologyChange oc ) {
	    cause = oc;
	}

        /* This is a little troublesome here. In order to be able to
           add the ontology to the entities, we need to know that
           they're actually implemented using our implementation. This
           brings up the issue of whether we really want to be able to
           support mix and match of alternative implementations. My
           guess is that's just too hard....*/

        public void visit(OWLClass entity) throws OWLException {
            if (!classes.containsKey(entity.getURI())) {
                classes.put(entity.getURI(), entity);
                try {
                    ((OWLEntityImpl) entity).addOntology(OWLOntologyImpl.this);
                } catch (ClassCastException ex) {
                    throw new OWLException(
                        "Trying to add unsuitable implementation class: "
                            + entity.getClass()
                            + " to "
                            + OWLOntologyImpl.this);
                }
		/* The change was enacted, so notify... */
		notifyListeners( cause );
            }
        }

        public void visit(OWLObjectProperty entity) throws OWLException {
            if (!objectProperties.containsKey(entity.getURI())) {
                objectProperties.put(entity.getURI(), entity);
                try {
                    ((OWLEntityImpl) entity).addOntology(OWLOntologyImpl.this);
                } catch (ClassCastException ex) {
                    throw new OWLException(
                        "Trying to add unsuitable implementation class: "
                            + entity.getClass()
                            + " to "
                            + OWLOntologyImpl.this);
                }
		/* The change was enacted, so notify... */
		notifyListeners( cause );
            }
        }
	
        public void visit(OWLAnnotationProperty entity) throws OWLException {
            if (!annotationProperties.containsKey(entity.getURI())) {
                annotationProperties.put(entity.getURI(), entity);
                try {
                    ((OWLEntityImpl) entity).addOntology(OWLOntologyImpl.this);
                } catch (ClassCastException ex) {
                    throw new OWLException(
                        "Trying to add unsuitable implementation class: "
                            + entity.getClass()
                            + " to "
                            + OWLOntologyImpl.this);
                }
		/* The change was enacted, so notify... */
		notifyListeners( cause );
            }
        }

        public void visit(OWLDataProperty entity) throws OWLException {
            if (!dataProperties.containsKey(entity.getURI())) {
                dataProperties.put(entity.getURI(), entity);
                try {
                    ((OWLEntityImpl) entity).addOntology(OWLOntologyImpl.this);
                } catch (ClassCastException ex) {
                    throw new OWLException(
                        "Trying to add unsuitable implementation class: "
                            + entity.getClass()
                            + " to "
                            + OWLOntologyImpl.this);
                }
		/* The change was enacted, so notify... */
		notifyListeners( cause );
            }
        }

        public void visit(OWLIndividual entity) throws OWLException {
	    if (!individuals.containsKey(entity.getURI())) {
		if ( entity.isAnonymous() ) {
		    anonymousIndividuals++;
		    // ANON use the anonid for the key
		    // individuals.put( new Integer(anonymousIndividuals), entity);
		    individuals.put( entity.getAnonId(), entity);
		    // ===
		} else {
		    individuals.put(entity.getURI(), entity);
		}
                try {
                    ((OWLEntityImpl) entity).addOntology(OWLOntologyImpl.this);
                } catch (ClassCastException ex) {
                    throw new OWLException(
                        "Trying to add unsuitable implementation class: "
                            + entity.getClass()
                            + " to "
                            + OWLOntologyImpl.this);
                }
		/* The change was enacted, so notify... */
		notifyListeners( cause );
            }
        }
    }

    protected class EntityRemover implements OWLEntityVisitor {

        public void visit(OWLClass entity) throws OWLException {
            /* Should check whether it should remove */
            if (classes.remove(entity.getURI()) == null) {
                throw new OWLException("Couldn't remove: " + entity.getURI());
            }
            try {
                if (!((OWLEntityImpl) entity)
                    .removeOntology(OWLOntologyImpl.this)) {
                    throw new OWLException("Couldn't remove: ");
                }
            } catch (ClassCastException ex) {
                throw new OWLException(
                    "Trying to remove unsuitable implementation class: "
                        + entity.getClass()
                        + " to "
                        + OWLOntologyImpl.this);
            }
        }

        public void visit(OWLObjectProperty entity) throws OWLException {
            /* Should check whether it should remove */
            if (objectProperties.remove(entity.getURI()) == null) {
                throw new OWLException("Couldn't remove: ");
            }
            try {
                if (!((OWLEntityImpl) entity)
                    .removeOntology(OWLOntologyImpl.this)) {
                    throw new OWLException("Couldn't remove: ");
                }
            } catch (ClassCastException ex) {
                throw new OWLException(
                    "Trying to remove unsuitable implementation class: "
                        + entity.getClass()
                        + " to "
                        + OWLOntologyImpl.this);
            }
        }

        public void visit(OWLAnnotationProperty entity) throws OWLException {
            /* Should check whether it should remove */
            if (annotationProperties.remove(entity.getURI()) == null) {
                throw new OWLException("Couldn't remove: ");
            }
            try {
                if (!((OWLEntityImpl) entity)
                    .removeOntology(OWLOntologyImpl.this)) {
                    throw new OWLException("Couldn't remove: ");
                }
            } catch (ClassCastException ex) {
                throw new OWLException(
                    "Trying to remove unsuitable implementation class: "
                        + entity.getClass()
                        + " to "
                        + OWLOntologyImpl.this);
            }
        }

        public void visit(OWLDataProperty entity) throws OWLException {
            /* Should check whether it should remove */
            if (dataProperties.remove(entity.getURI()) == null) {
                throw new OWLException("Couldn't remove: ");
            }
            try {
                if (!((OWLEntityImpl) entity)
                    .removeOntology(OWLOntologyImpl.this)) {
                    throw new OWLException("Couldn't remove: ");
                }
            } catch (ClassCastException ex) {
                throw new OWLException(
                    "Trying to remove unsuitable implementation class: "
                        + entity.getClass()
                        + " to "
                        + OWLOntologyImpl.this);
            }
        }

        public void visit(OWLIndividual entity) throws OWLException {
        
	    if ((individuals.remove(entity.getURI()) == null) &&
	    		(individuals.remove(entity.getAnonId()) == null)) {
				throw new OWLException("Couldn't remove: ");
			}
            try {
                if (!((OWLEntityImpl) entity)
                    .removeOntology(OWLOntologyImpl.this)) {
                    throw new OWLException("Couldn't remove: ");
                }
            } catch (ClassCastException ex) {
                throw new OWLException(
                    "Trying to remove unsuitable implementation class: "
                        + entity.getClass()
                        + " to "
                        + OWLOntologyImpl.this);
            }
        }

    }

    /**
     * getClass
     * 
     * Retrieves a class with a given uri
     * @param uri (URI)
     * @return OWLClass, null if there is no such thing
     */
    public OWLClass getClass(URI uri) {
        /* Not bothering to trap a ClassCastException */
        return (OWLClass) classes.get(uri);
    }
    /**
     * getAnnotationProperty
     * 
     * Retrieves an AnnotationProperty with a given uri
     * @param uri (String)
     * @return OWLAnnotationProperty, null if there is no such thing
     */
    public OWLAnnotationProperty getAnnotationProperty(URI uri) {
        /* Not bothering to trap a ClassCastException */
        return (OWLAnnotationProperty) annotationProperties.get(uri);
    }
    /**
     * getDataProperty
     * 
     * Retrieves a DataProperty with a given uri
     * @param uri (String)
     * @return OWLDataProperty, null if there is no such thing
     */
    public OWLDataProperty getDataProperty(URI uri) {
        /* Not bothering to trap a ClassCastException */
        return (OWLDataProperty) dataProperties.get(uri);
    }
    /**
       * getObjectProperty
       * 
       * Retrieves a ObjectProperty with a given uri
       * @param uri (URI)
       * @return OWLObjectProperty, null if there is no such thing
       */
    public OWLObjectProperty getObjectProperty(URI uri) {
        /* Not bothering to trap a ClassCastException */
        return (OWLObjectProperty) objectProperties.get(uri);
    }
    /**
     * getIndividual
     * 
     * Retrieves an individual with a given uri
     * @param uri (URI)
     * @return OWLDataProperty, returns null if it is there
     */
    public OWLIndividual getIndividual(URI uri) {
        /* Not bothering to trap a ClassCastException */
        return (OWLIndividual) individuals.get(uri);
    }

    /**
     * getDatatype
     * 
     * Retrieves a datatype with a given uri
     * @param uri (URI)
     * @return OWLClass, null if there is no such thing
     */
    public OWLDataType getDatatype(URI uri) {
        /* Not bothering to trap a ClassCastException */
        return (OWLDataType) datatypes.get(uri);
    }


//     /* Simple output for debugging purposes. */
//     public String toString() {
//         try {
//             org.semanticweb.owl.io.simple.Renderer renderer =
//                 new org.semanticweb.owl.io.simple.Renderer();
//             java.io.StringWriter sw = new java.io.StringWriter();
//             renderer.renderOntology(this, sw);
//             return sw.toString();
//         } catch (org.semanticweb.owl.io.RendererException ex) {
//             return this.getURI().toString();
//         }
//     }

    /***** Change Visitor events. Pass them all off to the proxy */
    public void visit( AddAnnotationInstance event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddEntity event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddDataType event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( RemoveDataType event) throws OWLException {
    	handleEvent( event );
    }
    public void visit( RemoveEntity event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddImport event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddIndividualAxiom event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddClassAxiom event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddPropertyAxiom event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddSuperClass event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddSuperProperty event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddIndividualClass event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddEquivalentClass event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddEnumeration event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddDomain event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddDataPropertyRange event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddObjectPropertyRange event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddInverse event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( SetFunctional event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( SetTransitive event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( SetSymmetric event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( SetInverseFunctional event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( SetOneToOne event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( SetDeprecated event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddObjectPropertyInstance event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( AddDataPropertyInstance event ) throws OWLException {
	handleEvent( event );
    }

    public void visit( RemoveClassAxiom event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( RemoveSuperClass event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( RemoveEquivalentClass event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( RemoveEnumeration event ) throws OWLException {
	handleEvent( event );
    }
    public void visit( SetLogicalURI event ) throws OWLException {
	handleEvent( event );
    }



    /* Local check that the event is appropriate for this
     * ontology. Redefinition of this can provide, for example,
     * ontologies that are guaranteed to be OWL Lite or OWL DL. */
    protected void filterEvent( OntologyChange event) throws OWLException {
    }

    /* In order to handle an event, we first analyse and see if there
     * are any additions that are necessary. We then pass it off to
     * the proxy for handling. */
    protected void handleEvent( OntologyChange event ) throws OWLException {
	/* Check that the event is ok. */
	filterEvent( event );
	
	/* Analyse the change and add new ones if necessary */
	EvolutionStrategy strategy = 
	    new AddAllEntitiesStrategy();
	List original = new ArrayList();
	original.add( event );
	List changes = strategy.computeChanges( original );
	/* Now enact all the changes (through the proxy) */
	for ( Iterator it = changes.iterator();
	      it.hasNext(); ) {
	    OntologyChange newOc = (OntologyChange) it.next();
	    newOc.accept( visitorProxy ); 
	}
    }

    /* Notify any listeners that something's happened. */
    protected void notifyListeners( OntologyChange event ) throws OWLException {
	for (Iterator it = changeListeners.iterator();
	     it.hasNext(); ) {
	    ((OntologyChangeListener) it.next()).ontologyChanged( event );
	}
    }

    public void addOntologyChangeListener( OntologyChangeListener listener ) throws OWLException {
	changeListeners.add( listener );
    }

    public void removeOntologyChangeListener( OntologyChangeListener listener ) throws OWLException {
	changeListeners.remove( listener );
    }

	/** Extension to OWL API 1.2 
	 * date: 09-01-04
	 *
	 */
	public void visit(RemoveDomain event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemoveDataPropertyRange event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemoveObjectPropertyRange event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemovePropertyAxiom event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemoveIndividualAxiom event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemoveDataPropertyInstance event) throws OWLException {
		handleEvent(event);	
	}
	public void visit(RemoveObjectPropertyInstance event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemoveSuperProperty event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemoveAnnotationInstance event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemoveImport event) throws OWLException {
		handleEvent(event);	
	}
	public void visit(RemoveIndividualClass event) throws OWLException {
		handleEvent(event);
	}
	public void visit(RemoveInverse event) throws OWLException {
		handleEvent(event);
	}

    
} // OWLOntology

/*
 * ChangeLog
 * $Log $
 * 
 * Date: 09-14-04 aditya_kalyanpur
 * added code for visit()->RemoveInverse
 * 
 * Date: 09-02-04 aditya_kalyanpur
 * added code for visit()->RemoveAnnotationInstance, RemovePropertyAxiom (handling only equivalent properties),
 * RemoveIndividualClass and RemoveImport
 * 
 * Date: 09-01-04 aditya_kalyanpur
 * added code for visit()->RemoveClassAxiom, RemoveDomain, RemoveDataPropertyRange, 
 * RemoveObjectPropertyRange, RemovePropertyAxiom, RemoveSuperProperty,
 * RemoveIndividualAxiom, RemoveDataPropertyInstance, RemoveObjectPropertyInstance
 * @author Aditya Kalyanpur (MINDSWAP)
 * 
 */
