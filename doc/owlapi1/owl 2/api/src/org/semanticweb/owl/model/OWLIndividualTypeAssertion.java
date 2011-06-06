/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.model;

import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Evren Sirin
 */
public interface OWLIndividualTypeAssertion extends OWLObject {
    public OWLIndividual getIndividual(); 
    public OWLDescription getType();
}
