/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;

/**
 * @author Evren Sirin
 */
public class OWLInversePropertyAxiomImpl extends OWLPropertyAxiomImpl implements OWLInversePropertyAxiom {
    private OWLObjectProperty property;
    private OWLObjectProperty inverse;
    
    public OWLInversePropertyAxiomImpl(OWLDataFactoryImpl factory, OWLObjectProperty property, OWLObjectProperty inverse) {
        super(factory);

        this.property = property;
        this.inverse = inverse;
    }
    
    public OWLObjectProperty getProperty() {
        return property;
    }

    public OWLObjectProperty getInverseProperty() {
        return inverse;
    }

    public void accept(OWLObjectVisitor visitor) throws OWLException {
        visitor.visit(this);
    }

    /* (non-Javadoc)
     * @see org.semanticweb.owl.model.OWLPropertyAxiom#accept(org.semanticweb.owl.model.OWLPropertyAxiomVisitor)
     */
    public void accept(OWLPropertyAxiomVisitor visitor) throws OWLException {
        visitor.visit(this);       
    }
    
    public boolean equals(Object o) {
		if (o instanceof OWLInversePropertyAxiomImpl) {
			OWLInversePropertyAxiomImpl pda = (OWLInversePropertyAxiomImpl) o;
			if (pda.inverse.equals(inverse) && pda.property.equals(property)) return true; 
		}
		return false;
	}
     
     public int hashCode() {
		return super.hashCode() + "inverse".hashCode() + hashCode(property) + hashCode(inverse);
     }
}
