/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.model;

import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;

/**
 * @author Evren Sirin
 */
public interface OWLObjectPropertyInstance extends OWLObject, OWLOntologyObject {
    public OWLIndividual getSubject(); 
    public OWLObjectProperty getProperty();
    public OWLIndividual getObject();
}
