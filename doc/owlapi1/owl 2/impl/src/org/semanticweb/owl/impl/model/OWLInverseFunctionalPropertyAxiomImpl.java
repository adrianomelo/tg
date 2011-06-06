/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;

/**
 * @author Evren Sirin
 */
public class OWLInverseFunctionalPropertyAxiomImpl extends OWLPropertyAxiomImpl implements OWLInverseFunctionalPropertyAxiom {
    private OWLObjectProperty property;
    
    public OWLInverseFunctionalPropertyAxiomImpl(OWLDataFactoryImpl factory, OWLObjectProperty property) {
        super(factory);

        this.property = property;
    }
    
    public OWLObjectProperty getProperty() {
        return property;
    }

    /* (non-Javadoc)
     * @see org.semanticweb.owl.impl.model.OWLObjectImpl#accept(org.semanticweb.owl.model.OWLObjectVisitor)
     */
    public void accept(OWLObjectVisitor visitor) throws OWLException {
        visitor.visit(this);
    }

    /* (non-Javadoc)
     * @see org.semanticweb.owl.model.OWLPropertyAxiom#accept(org.semanticweb.owl.model.OWLPropertyAxiomVisitor)
     */
    public void accept(OWLPropertyAxiomVisitor visitor) throws OWLException {
        ((OWLObjectVisitor) visitor).visit(this);       
    }
}
