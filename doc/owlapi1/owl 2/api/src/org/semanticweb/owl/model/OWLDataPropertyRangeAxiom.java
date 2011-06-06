/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.model;

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLPropertyAxiom;

/**
 * @author Evren Sirin
 */
public interface OWLDataPropertyRangeAxiom extends OWLPropertyAxiom {
    public OWLDataProperty getProperty();
    public OWLDataRange getRange();
}
