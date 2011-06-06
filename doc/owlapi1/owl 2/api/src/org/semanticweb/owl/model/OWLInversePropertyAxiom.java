/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.model;

import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;

/**
 * @author Evren Sirin
 */
public interface OWLInversePropertyAxiom extends OWLPropertyAxiom {
    public OWLObjectProperty getProperty();
    public OWLObjectProperty getInverseProperty();
}
