 /*
 * OntoCompo - Composition and Modules for Ontology Engineering
 * cin - UFPE - Brasil
 * Institut National de Recherche en Informatique et en Automatique - INRIA
 *  Copyright (C) 2008 Camila Bezerra (kemylle@gmail.com)
Fred Freitas (fred@cin.ufpe.br)
J�r�me Euzenat (jerome.euzenat@inrialpes.fr)
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package br.ufpe.cin.ontocompo.infra.extractor.loader;

import java.net.URI;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;

import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

/**
 * <B>This class is responsible for interactions between the ontology and the
 * system by providing to load entities like classes,properties and axioms. </B>
 * @author Camila Bezerra
 * @date Dec 27, 2007
 */
public class OntologyLoader {

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private URI uriOntology;

    /**
     * Creates a new instance of OntologyLoader
     * @param uri physical path ontology
     */
    public OntologyLoader(URI uri) {
        ontology = loadOntology(uri);

    }

    private OWLOntology loadOntology(URI uri) {
        setManager(OWLManager.createOWLOntologyManager());

        try {
            ontology = getManager().loadOntologyFromPhysicalURI(uri);
        } catch (Exception ex) {
            System.out.println("The ontology could not be created: " + ex.getMessage());
            ex.printStackTrace();
        }
        return ontology;
    }

    /**
     * Get the classes from the ontology
     * @return classes list
     */
    public List<OWLClass> getClasses() {
        List<OWLClass> classes = new ArrayList<OWLClass>();

        for (OWLClass cls : getOntology().getReferencedClasses()) {
            if (!cls.toString().contains("DomainConcept") && !cls.toString().contains("Equivalent1") && !cls.toString().contains("Equivalent2")) {
                classes.add(cls);
            }
        }

        return classes;
    }

     /**
     * Get the object properties from the given class
     * @paramater cls the given class
     * @return  list of properties
     */
    public List<OWLObjectProperty> getObjectPropertiesFromClass(OWLClass classz) {
        List<OWLObjectProperty> list = new ArrayList<OWLObjectProperty>();
        for (OWLObjectProperty obj : getOntology().getReferencedObjectProperties()) {
            for (Iterator it = this.getDomainProperty(obj).iterator(); it.hasNext();) {
                OWLDescription des = (OWLDescription) it.next();
                if (des.toString().equals(classz.toString())) {
                    list.add(obj);
                }
            }
        }
        return list;
    }

    /**
     * Get objectProperties from the ontology
     * @return objectProperties list
     */
    public List<OWLObjectProperty> getObjectProperties() {
        List<OWLObjectProperty> objectProperties = new ArrayList<OWLObjectProperty>();
        for (OWLObjectProperty obj : getOntology().getReferencedObjectProperties()) {

            objectProperties.add(obj);

        }
        return objectProperties;
    }

    /**
     * Get the dataproperties from the ontology
     * @return dataproperties list
     */
    public List<OWLDataProperty> getDataProperties() {
        List<OWLDataProperty> dataProperties = new ArrayList<OWLDataProperty>();
        for (OWLDataProperty obj : getOntology().getReferencedDataProperties()) {
            dataProperties.add(obj);
        }
        return dataProperties;
    }

    /**
     * Get the subclasses from the class
     * @param classz the class
     * @return subclasses set
     */
    public Set<OWLDescription> getSubClasses(OWLClass classz) {
        Set<OWLDescription> classes = new HashSet<OWLDescription>();

        for (OWLDescription cls : classz.getSubClasses(getOntology())) {
            if (!cls.toString().contains("DomainConcept") && !cls.toString().contains("ObjectSomeValueFrom") && !cls.toString().contains("ObjectAllValuesFrom") && !cls.toString().contains("ObjectHasValue") && !cls.toString().contains("Equivalent1") && !cls.toString().contains("Equivalent2")) {
                classes.add(cls);
            }
        }

        return classes;

    }

    /**
     * Get the superclasses from the class
     * @param classz the class
     * @return superclasses list
     */
    public Set<OWLDescription> getSuperClasses(OWLClass classz) {
        Set<OWLDescription> classes = new HashSet<OWLDescription>();

        for (OWLDescription cls : classz.getSuperClasses(getOntology())) {
            if (!cls.toString().contains("DomainConcept") && !cls.toString().contains("ObjectSomeValueFrom") && !cls.toString().contains("ObjectAllValuesFrom") && !cls.toString().contains("ObjectHasValue") && !cls.toString().contains("1") && !cls.toString().contains("2")) {
                classes.add(cls);
            }
        }

        return classes;
    }

     /**
     * Get the disjount classes from the given class
     * @param classz the given class
     * @return classes list of disjount classes
     */
    public Set<OWLDescription> getDisjountClasses(OWLClass classz) {
        Set<OWLDescription> classes = new HashSet<OWLDescription>();
        for (OWLDescription cls : classz.getDisjointClasses(getOntology())) {
            if (!cls.toString().contains("DomainConcept") && !cls.toString().contains("ObjectSomeValueFrom") && !cls.toString().contains("ObjectAllValuesFrom") && !cls.toString().contains("ObjectHasValue") && !cls.toString().contains("1") && !cls.toString().contains("2")) {
                classes.add(cls);
            }
        }
        return classes;
    }

    /**
     * Get the subObjectProperties from the property
     * @param prop the objectproperty
     * @return subobjectproperties set
     */
    public Set<OWLObjectPropertyExpression> getSubObjectProperties(OWLEntity prop) {
        return ((OWLObjectProperty) prop).getSubProperties(getOntology());

    }

    /**
     * Get the superObjectProperties from the property
     * @param prop the objectproperty
     * @return superObjectProperties set
     */
    public Set<OWLObjectPropertyExpression> getSuperObjectProperties(OWLEntity prop) {
        return ((OWLObjectProperty) prop).getSuperProperties(getOntology());

    }

    /**
     * Get the subDataProperties from the property
     * @param prop the dataproperty
     * @return subDataProperties set
     */
    public Set<OWLDataPropertyExpression> getSubDataProperties(OWLEntity prop) {
        return ((OWLDataProperty) prop).getSubProperties(getOntology());

    }

     /**
     * Verify if the given property is functional
     * @param prop the given property
     * @return true if is functional, Otherwise returns false.
     */
    public boolean isFunctionalProperty(OWLProperty prop) {
        if (prop instanceof OWLObjectProperty) {
            return ((OWLObjectProperty) prop).isFunctional(ontology);
        } else {
            return ((OWLDataProperty) prop).isFunctional(ontology);
        }

    }

    public boolean isInverseFunctionalProperty(OWLObjectProperty prop) {
        return prop.isInverseFunctional(ontology);

    }

    public boolean isSymmetricProperty(OWLObjectProperty prop) {
        return prop.isSymmetric(ontology);

    }

    public boolean isTransitiveProperty(OWLObjectProperty prop) {
        return prop.isTransitive(ontology);

    }

    /**
     * Get the superDataProperties from the property
     * @param prop the dataproperty
     * @return superDataProperties set
     */
    public Set<OWLDataPropertyExpression> getSuperDataProperties(OWLEntity prop) {
        return ((OWLDataProperty) prop).getSuperProperties(getOntology());

    }

    /**
     * Get the domain from the property
     * @param property the objectproperty
     * @return domain
     */
    public Set<OWLDescription> getDomainProperty(OWLObjectProperty property) {
        return property.getDomains(getOntology());

    }

    /**
     * Get the range from the property
     * @param property the objectproperty
     * @return range property
     */
    public Set<OWLDescription> getRangeProperty(OWLObjectProperty property) {
        return property.getRanges(getOntology());

    }

    /**
     * Get the range from the property
     * @param property the dataproperty
     * @return range property
     */
    public Set<OWLDataRange> getRangeProperty(OWLDataProperty property) {
        return property.getRanges(getOntology());

    }

    /**
     * Get the domain from the property
     * @param property the dataproperty
     * @return domain property
     */
    public Set<OWLDescription> getDomainProperty(OWLDataProperty property) {
        return property.getDomains(getOntology());

    }

    /**
     * Get axioms from the class
     * @param classz the class
     * @return axioms list
     */
    public Set<OWLClassAxiom> getClassAxioms(OWLClass classz) {
        return getOntology().getAxioms(classz);
    }
    
    public Set<OWLObjectPropertyAxiom> getPropertyAxioms(OWLObjectProperty prop){
        return getOntology().getAxioms(prop);
    }

    public Set<OWLIndividual> getIndividuals() {

        return getOntology().getReferencedIndividuals();

    }

    public Set<OWLDescription> getClassFromIndividual(OWLIndividual ind) {
        return ind.getTypes(getOntology());
    }

    /**
     * Get comments from the entity
     * @param entity the entity
     * @return string comments
     */
    public String getComments(OWLEntity entity) {

    //    List<String> comments = new ArrayList<String>();
        String output = "";
        for (OWLAnnotation annotation : entity.getAnnotations(getOntology(), OWLRDFVocabulary.RDFS_COMMENT.getURI())) {
            if (annotation.isAnnotationByConstant()) {
                OWLConstant val = annotation.getAnnotationValueAsConstant();
                //   if (!val.isTyped()) {

                if (val.getLiteral().contains(".")) {
                    int index = 0;
                    while (index < val.getLiteral().length()) {
                        int endIndex = val.getLiteral().indexOf(".", index);
                        if (endIndex != -1) {
                            output = output + val.getLiteral().substring(index, endIndex) + "\n";
                        }
                        index = endIndex + 1;
                    }

                } else {
                    output = output + val.getLiteral() + "\n";
                }

            }
        //    }
        }

        return output;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public void setManager(OWLOntologyManager manager) {
        this.manager = manager;
    }

    public URI getUriOntology() {
        uriOntology = getOntology().getURI();
        return uriOntology;
    }

    public void setUriOntology(URI uriOntology) {
        this.uriOntology = uriOntology;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }
}
