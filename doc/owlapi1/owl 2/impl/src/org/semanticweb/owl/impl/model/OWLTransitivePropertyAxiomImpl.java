/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;

/**
 * @author Evren Sirin
 */
public class OWLTransitivePropertyAxiomImpl extends OWLPropertyAxiomImpl implements OWLTransitivePropertyAxiom {
    private OWLProperty property;
    
    public OWLTransitivePropertyAxiomImpl(OWLDataFactoryImpl factory, OWLProperty property) {
        super(factory);

        this.property = property;
    }
    
    public OWLProperty getProperty() {
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
    
    public String toString() {
        try {
            return "transtive(" + property.getURI() + ")";
        } catch(OWLException e) {
            return "transitive( ERROR )";
        }
    }
    
    public boolean equals(Object o) {
		if (o instanceof OWLTransitivePropertyAxiomImpl) {
			OWLTransitivePropertyAxiomImpl tpa = (OWLTransitivePropertyAxiomImpl) o;
			if (tpa.property.equals(property)) return true; 
		}
		return false;
	}
     
     public int hashCode() {
		return super.hashCode() + "transitive".hashCode() + hashCode(property);
     }
}
