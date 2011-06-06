/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;

/**
 * @author Evren Sirin
 */
public class OWLFunctionalPropertyAxiomImpl extends OWLPropertyAxiomImpl implements OWLFunctionalPropertyAxiom {
    private OWLProperty property;
    
    public OWLFunctionalPropertyAxiomImpl(OWLDataFactoryImpl factory, OWLProperty property) {
        super(factory);

        this.property = property;
    }
    
    public OWLProperty getProperty() {
        return property;
    }

    public void accept( OWLObjectVisitor visitor ) throws OWLException {
    	visitor.visit( this );
    }

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLPropertyAxiom#accept(org.semanticweb.owl.model.OWLPropertyAxiomVisitor)
	 */
    public void accept(OWLPropertyAxiomVisitor visitor) throws OWLException {
        visitor.visit(this);       
    }
    
    public boolean equals(Object desc) {
		if (desc instanceof OWLFunctionalPropertyAxiomImpl) {
			OWLFunctionalPropertyAxiomImpl fpa = (OWLFunctionalPropertyAxiomImpl) desc;
			if (fpa.property.equals(property)) return true; 
		}
		return false;
	}
     
     public int hashCode() {
		return super.hashCode() + "functional".hashCode() + hashCode(property);
     }
}
