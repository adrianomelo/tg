/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;

/**
 * @author Evren Sirin
 */
public class OWLSymmetricPropertyAxiomImpl extends OWLPropertyAxiomImpl implements OWLSymmetricPropertyAxiom {
    private OWLObjectProperty property;
    
    public OWLSymmetricPropertyAxiomImpl(OWLDataFactoryImpl factory, OWLObjectProperty property) {
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

    public void accept(OWLPropertyAxiomVisitor visitor) throws OWLException {
        visitor.visit(this);       
    }
    
    public boolean equals(Object o) {
		if (o instanceof OWLSymmetricPropertyAxiomImpl) {
			OWLSymmetricPropertyAxiomImpl spa = (OWLSymmetricPropertyAxiomImpl) o;
			if (spa.property.equals(property)) return true; 
		}
		return false;
	}
     
     public int hashCode() {
		return super.hashCode() + "symmetric".hashCode() + hashCode(property);
     }
}
