/*
 * Copyright (C) 2005, University of Karlsruhe
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

package org.semanticweb.owl.util;


import java.net.URI;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.change.AddImport;
import org.semanticweb.owl.model.change.OntologyChange;


/**
 * A utility class for copying one ontology into another.
 *
 * @author Raphael Volz (volz@aifb.uni-karlsruhe.de)
 * @author Boris Motik (boris.motik@fzi.de)
 */
public class OntologyReplicator extends OntologyProcessor {
	
	
    static Logger logger = Logger.getLogger(OntologyReplicator.class);
    
    /** Phase of loading concepts. */
    public static final int PHASE_LOAD_CONCEPTS=1;
    /** Phase of copying concepts. */
    public static final int PHASE_COPY_CONCEPTS=2;
    /** Phase of loading properties. */
    public static final int PHASE_LOAD_PROPERTIES=3;
    /** Phase of copying properties. */
    public static final int PHASE_COPY_PROPERTIES=4;
    /** Phase of loading instances. */
    public static final int PHASE_LOAD_INSTANCES=5;
    /** Phase of copying instances. */
    public static final int PHASE_COPY_INSTANCES=6;

    /** The map of logical to physical URI mappings. */
    protected Map m_logicalToPhysicalURIs;
    /** The source OWL Ontology. */
    protected OWLOntology m_sourceOWLOntology;
    /** The target OWL Ontology. */
    protected OWLOntology m_targetOWLOntology;
    /** The target KAON connection. */
    protected OWLConnection m_targetOWLConnection;
    /** The list of events. */
    protected List m_changeEvents;
    /** The map of source to target models. */
    protected Map m_sourceToTarget;

    /**
     * Creates an instance of this class.
     *
     * @param sourceOWLOntology                 the source OWL Ontology
     * @param logicalToPhysicalURIs             the map of logical to physical URIs
     * @param targetKAONConnection              the target KAON connection
     */
    public OntologyReplicator(OWLOntology sourceOWLOntology,Map logicalToPhysicalURIs,OWLConnection targetKAONConnection) {
        m_sourceToTarget=new HashMap();
        m_logicalToPhysicalURIs=logicalToPhysicalURIs;
        m_sourceOWLOntology=sourceOWLOntology;
        m_targetOWLConnection=targetKAONConnection;
        m_changeEvents=new LinkedList();
    }
    /**
     * Starts the replication of the source model to the target model
     *
     * @return                                  the target model
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    public OWLOntology doReplication() throws OWLException,InterruptedException {
        try {
            setUpModelMappings();
            processConcepts();
            processProperties();
            processInstances();
            return m_targetOWLOntology;
        }
        finally {
            m_logicalToPhysicalURIs=null;
            m_sourceOWLOntology=null;
            m_targetOWLOntology=null;
            m_targetOWLConnection=null;
            m_changeEvents=null;
        }
    }
    /**
     * Establishes the map from source to target Ontologies.
     *
     * @throws OWLException      thrown it there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void setUpModelMappings() throws OWLException,InterruptedException {
        m_targetOWLOntology=createTargetOWLOntology(m_sourceOWLOntology);
        m_sourceToTarget.put(m_sourceOWLOntology,m_targetOWLOntology);
        Iterator iterator=OWLOntologies.getAllIncludedOntologies(m_sourceOWLOntology).iterator();
        while (iterator.hasNext()) {
            OWLOntology sourceOWLOntology=(OWLOntology)iterator.next();
            try {
                m_targetOWLConnection.getOntologyLogical(sourceOWLOntology.getLogicalURI());
            }
            catch (OWLException modelShouldBeReplicated) {
                OWLOntology targetOWLOntology=createTargetOWLOntology(sourceOWLOntology);
                m_sourceToTarget.put(sourceOWLOntology,targetOWLOntology);
            }
            checkInterrupted();
        }
        iterator=m_sourceToTarget.keySet().iterator();
        while (iterator.hasNext()) {
            OWLOntology sourceOWLOntology=(OWLOntology)iterator.next();
            OWLOntology targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceOWLOntology);
            Iterator inner=sourceOWLOntology.getIncludedOntologies().iterator();
            while (inner.hasNext()) {
                OWLOntology sourceIncludedOWLOntology=(OWLOntology)inner.next();
                OWLOntology targetIncludedOWLOntology=m_targetOWLConnection.getOntologyLogical(sourceIncludedOWLOntology.getLogicalURI());
                if (!targetOWLOntology.getIncludedOntologies().contains(targetIncludedOWLOntology))
		    logger.warn("Implement me!");
		//                    targetOWLOntology.applyChanges(Collections.singletonList(new AddImport(targetIncludedOWLOntology, sourceIncludedOWLOntology, null)));
                checkInterrupted();
            }
        }
    }
    /**
     * Creates a target model for given source OWL Ontology.
     *
     * @param sourceOWLOntology                     the source OWL Ontology
     * @return                                  the OWL Ontology generated for given source OWL Ontology
     * @throws OWLException                    thrown it there is an error
     */
    protected OWLOntology createTargetOWLOntology(OWLOntology sourceOWLOntology) throws OWLException {
        URI logicalURI=sourceOWLOntology.getLogicalURI();
        URI targetPhysicalURI=(URI)m_logicalToPhysicalURIs.get(logicalURI);
        OWLOntology targetOWLOntology=m_targetOWLConnection.createOntology(targetPhysicalURI,logicalURI);
        /* Map attributes=sourceOWLOntology.getAttributes();
        Iterator keys=attributes.keySet().iterator();
        while (keys.hasNext()) {
            String key=(String)keys.next();
            String value=(String)attributes.get(key);
            targetOWLOntology.setAttribute(key,value);
        }
        targetOWLOntology.setAttribute("OWLOntology.replicatedFromPhysicalURI",sourceOWLOntology.getPhysicalURI());
       */
        return targetOWLOntology;
    }
    /**
     * Processes all concepts.
     *
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void processConcepts() throws OWLException,InterruptedException {
        Set concepts=loadConcepts();
        flushChanges();
        copyConcepts(concepts);
        flushChanges();
    }
    /**
     * Processes all properties.
     *
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void processProperties() throws OWLException,InterruptedException {
        Set properties=loadProperties();
        flushChanges();
        copyProperties(properties);
        flushChanges();
    }
    /**
     * Processes all instances.
     *
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void processInstances() throws OWLException,InterruptedException {
        Set instances=loadInstances();
        flushChanges();
        copyInstances(instances);
        flushChanges();
    }
    /**
     * Loads the concepts.
     *
     * @return                                  the set of concepts
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected Set loadConcepts() throws OWLException,InterruptedException {
    	/*
        Set concepts=m_sourceOWLOntology.getConcepts();
        processElements(concepts,m_sourceOWLOntology,OWLOntology.LOAD_CONCEPT_BASICS | OWLOntology.LOAD_SUPER_CONCEPTS,new ObjectProcessor() {
            public void processLoadedObjects(Set objects) throws OWLException,InterruptedException {
                Iterator iterator=objects.iterator();
                while (iterator.hasNext()) {
                    Concept sourceConcept=(Concept)iterator.next();
                    OWLOntology targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceConcept.getSourceOWLOntology());
                    if (targetOWLOntology!=null) {
                        Concept targetConcept=targetOWLOntology.getConcept(sourceConcept.getURI());
                        applyChange(new AddEntity(targetOWLOntology,null,targetConcept));
                    }
                    checkInterrupted();
                }
                flushChanges();
            }
        },PHASE_LOAD_CONCEPTS);
        return concepts;
        */
        logger.warn("Implement me!") ;
        return null;
    
    }
    /**
     * Loads the properties.
     *
     * @return                                  the set of properties
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected Set loadProperties() throws OWLException,InterruptedException {
      /*
        Set properties=m_sourceOWLOntology.getProperties();
        processElements(properties,m_sourceOWLOntology,OWLOntology.LOAD_PROPERTY_BASICS | OWLOntology.LOAD_SUPER_PROPERTIES | OWLOntology.LOAD_PROPERTY_DOMAINS | OWLOntology.LOAD_PROPERTY_RANGES,new ObjectProcessor() {
            public void processLoadedObjects(Set objects) throws OWLException,InterruptedException {
                Iterator iterator=objects.iterator();
                while (iterator.hasNext()) {
                    Property sourceProperty=(Property)iterator.next();
                    OWLOntology targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceProperty.getSourceOWLOntology());
                    if (targetOWLOntology!=null) {
                        Property targetProperty=targetOWLOntology.getProperty(sourceProperty.getURI());
                        applyChange(new AddEntity(targetOWLOntology,null,targetProperty));
                        if (sourceProperty.isAttribute())
                            applyChange(new SetPropertyIsAttribute(targetOWLOntology,null,targetProperty,true));
                        if (sourceProperty.isSymmetric())
                            applyChange(new SetPropertySymmetric(targetOWLOntology,null,targetProperty,true));
                        if (sourceProperty.isTransitive())
                            applyChange(new SetPropertyTransitive(targetOWLOntology,null,targetProperty,true));
                    }
                    checkInterrupted();
                }
                flushChanges();
            }
        },PHASE_LOAD_PROPERTIES);
     */
     logger.warn("Implement me!");
     return null;
    }
    /**
     * Loads the instances.
     *
     * @return                                  the set of instances
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected Set loadInstances() throws OWLException,InterruptedException {
        /*
        Set instances=m_sourceOWLOntology.getInstances();
        processElements(instances,m_sourceOWLOntology,OWLOntology.LOAD_INSTANCE_BASICS | OWLOntology.LOAD_INSTANCE_PARENT_CONCEPTS | OWLOntology.LOAD_INSTANCE_FROM_PROPERTY_VALUES,new ObjectProcessor() {
            public void processLoadedObjects(Set objects) throws OWLException,InterruptedException {
                Iterator iterator=objects.iterator();
                while (iterator.hasNext()) {
                    Instance sourceInstance=(Instance)iterator.next();
                    OWLOntology targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceInstance.getSourceOWLOntology());
                    if (targetOWLOntology!=null) {
                        Instance targetInstance=targetOWLOntology.getInstance(sourceInstance.getURI());
                        applyChange(new AddEntity(targetOWLOntology,null,targetInstance));
                    }
                    checkInterrupted();
                }
                flushChanges();
            }
        },PHASE_LOAD_INSTANCES);
        return instances; 
        */
        logger.debug("Implement me!");
        return null;
    }
    /**
     * Copies the information about the concepts.
     *
     * @param concepts                          the set of concepts
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void copyConcepts(Set concepts) throws OWLException,InterruptedException {
    	/*
        processElements(concepts,null,0,new ObjectProcessor() {
            public void processLoadedObjects(Set objects) throws OWLException,InterruptedException {
                Iterator iterator=objects.iterator();
                while (iterator.hasNext()) {
                    Concept sourceConcept=(Concept)iterator.next();
                    Concept targetConcept=m_targetOWLOntology.getConcept(sourceConcept.getURI());
                    Iterator superConcepts=sourceConcept.getSuperConcepts().iterator();
                    while (superConcepts.hasNext()) {
                        Concept sourceSuperConcept=(Concept)superConcepts.next();
                        OWLOntology targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceConcept.getSuperSubConceptOWLOntology(sourceSuperConcept));
                        if (targetOWLOntology!=null) {
                            Concept targetSuperConcept=m_targetOWLOntology.getConcept(sourceSuperConcept.getURI());
                            applyChange(new AddSubConcept(targetOWLOntology,null,targetSuperConcept,targetConcept));
                        }
                        checkInterrupted();
                    }
                    checkInterrupted();
                }
                flushChanges();
            }
        },PHASE_COPY_CONCEPTS);
    */
        logger.debug("Implement me!");
    }
    /**
     * Copies the information about the properties.
     *
     * @param properties                        the set of properties
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void copyProperties(Set properties) throws OWLException,InterruptedException {
    	/*
        processElements(properties,null,0,new ObjectProcessor() {
            public void processLoadedObjects(Set objects) throws OWLException,InterruptedException {
                Iterator iterator=objects.iterator();
                while (iterator.hasNext()) {
                    Property sourceProperty=(Property)iterator.next();
                    Property targetProperty=m_targetOWLOntology.getProperty(sourceProperty.getURI());
                    OWLOntology targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceProperty.getInversePropertyOWLOntology());
                    if (targetOWLOntology!=null && targetProperty.getInverseProperty()==null) {
                        Property targetInverseProperty=m_targetOWLOntology.getProperty(sourceProperty.getInverseProperty().getURI());
                        applyChange(new SetInverseProperties(targetOWLOntology,null,targetProperty,targetInverseProperty));
                        flushChanges();
                    }
                    Iterator superProperties=sourceProperty.getSuperProperties().iterator();
                    while (superProperties.hasNext()) {
                        Property sourceSuperProperty=(Property)superProperties.next();
                        targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceProperty.getSuperSubPropertyOWLOntology(sourceSuperProperty));
                        if (targetOWLOntology!=null) {
                            Property targetSuperProperty=m_targetOWLOntology.getProperty(sourceSuperProperty.getURI());
                            applyChange(new AddSubProperty(targetOWLOntology,null,targetSuperProperty,targetProperty));
                        }
                        checkInterrupted();
                    }
                    Iterator domainConcepts=sourceProperty.getDomainConcepts().iterator();
                    while (domainConcepts.hasNext()) {
                        Concept sourceDomainConcept=(Concept)domainConcepts.next();
                        targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceProperty.getDomainConceptOWLOntology(sourceDomainConcept));
                        if (targetOWLOntology!=null) {
                            Concept targetDomainConcept=m_targetOWLOntology.getConcept(sourceDomainConcept.getURI());
                            applyChange(new AddPropertyDomain(targetOWLOntology,null,targetProperty,targetDomainConcept));
                            int minimumCardinality=sourceProperty.getMinimumCardinality(sourceDomainConcept);
                            if (minimumCardinality!=0)
                                applyChange(new SetMinimumCardinality(targetOWLOntology,null,targetProperty,targetDomainConcept,minimumCardinality));
                            int maximumCardinality=sourceProperty.getMaximumCardinality(sourceDomainConcept);
                            if (maximumCardinality!=Integer.MAX_VALUE)
                                applyChange(new SetMaximumCardinality(targetOWLOntology,null,targetProperty,targetDomainConcept,maximumCardinality));
                        }
                        checkInterrupted();
                    }
                    Iterator rangeConcepts=sourceProperty.getRangeConcepts().iterator();
                    while (rangeConcepts.hasNext()) {
                        Concept sourceRangeConcept=(Concept)rangeConcepts.next();
                        targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceProperty.getRangeConceptOWLOntology(sourceRangeConcept));
                        if (targetOWLOntology!=null) {
                            Concept targetRangeConcept=m_targetOWLOntology.getConcept(sourceRangeConcept.getURI());
                            applyChange(new AddPropertyRange(targetOWLOntology,null,targetProperty,targetRangeConcept));
                        }
                        checkInterrupted();
                    }
                    checkInterrupted();
                }
                flushChanges();
            }
        },PHASE_COPY_PROPERTIES);
        */
        logger.warn("Implement me!");
    }
    /**
     * Copies the information about the instances.
     *
     * @param instances                         the set of instances
     * @throws OWLException                    thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void copyInstances(Set instances) throws OWLException,InterruptedException {
    	/*
        processElements(instances,null,0,new ObjectProcessor() {
            public void processLoadedObjects(Set objects) throws OWLException,InterruptedException {
                Iterator iterator=objects.iterator();
                while (iterator.hasNext()) {
                    Instance sourceInstance=(Instance)iterator.next();
                    Instance targetInstance=m_targetOWLOntology.getInstance(sourceInstance.getURI());
                    Iterator parentConcepts=sourceInstance.getParentConcepts().iterator();
                    while (parentConcepts.hasNext()) {
                        Concept sourceConcept=(Concept)parentConcepts.next();
                        OWLOntology targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourceInstance.getConceptInstanceOWLOntology(sourceConcept));
                        if (targetOWLOntology!=null) {
                            Concept targetConcept=m_targetOWLOntology.getConcept(sourceConcept.getURI());
                            applyChange(new AddInstanceOf(targetOWLOntology,null,targetConcept,targetInstance));
                        }
                        checkInterrupted();
                    }
                    Iterator propertyInstances=sourceInstance.getFromPropertyInstances().iterator();
                    while (propertyInstances.hasNext()) {
                        PropertyInstance sourcePropertyInstance=(PropertyInstance)propertyInstances.next();
                        OWLOntology targetOWLOntology=(OWLOntology)m_sourceToTarget.get(sourcePropertyInstance.getSourceOWLOntology());
                        if (targetOWLOntology!=null) {
                            Property targetProperty=m_targetOWLOntology.getProperty(sourcePropertyInstance.getProperty().getURI());
                            Instance targetSourceInstance=m_targetOWLOntology.getInstance(sourcePropertyInstance.getSourceInstance().getURI());
                            Object targetTargetValue;
                            if (sourcePropertyInstance.getTargetValue() instanceof Instance)
                                targetTargetValue=m_targetOWLOntology.getInstance(((Instance)sourcePropertyInstance.getTargetValue()).getURI());
                            else
                                targetTargetValue=sourcePropertyInstance.getTargetValue();
                            applyChange(new AddPropertyInstance(targetOWLOntology,null,targetProperty,targetSourceInstance,targetTargetValue));
                        }
                        checkInterrupted();
                    }
                    checkInterrupted();
                }
                flushChanges();
            }
        },PHASE_COPY_INSTANCES);
        */
        logger.warn("Implement me!");
    }
    /**
     * Adds an event to be processed.
     *
     * @param changeEvent                       the event
     * @throws OWLException                    thrown if there is an error
     */
    protected void applyChange(OntologyChange changeEvent) throws OWLException {
        m_changeEvents.add(changeEvent);
    }
    /**
     * Flushes the changes to the OWL Ontology.
     *
     * @throws OWLException                    thrown if there is an error
     */
    protected void flushChanges() throws OWLException {
	logger.warn("Removed Stuff! SKB");
//         if (!m_changeEvents.isEmpty()) {
//             m_targetOWLConnection.applyChanges(m_changeEvents);
//             m_changeEvents.clear();
//         }
    }
}
