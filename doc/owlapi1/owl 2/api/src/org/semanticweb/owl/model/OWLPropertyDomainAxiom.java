/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.model;

import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;

/**
 * @author Evren Sirin
 */
public interface OWLPropertyDomainAxiom extends OWLPropertyAxiom {
    public OWLProperty getProperty();
    public OWLDescription getDomain();
}
