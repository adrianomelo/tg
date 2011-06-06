/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.model;

import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;

/**
 * @author Evren Sirin
 */
public interface OWLSymmetricPropertyAxiom extends OWLPropertyAxiom {
    public OWLObjectProperty getProperty();
}
