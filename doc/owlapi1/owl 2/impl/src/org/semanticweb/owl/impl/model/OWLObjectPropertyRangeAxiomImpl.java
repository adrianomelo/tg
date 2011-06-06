/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;

/**
 * @author Evren Sirin
 */
public class OWLObjectPropertyRangeAxiomImpl extends OWLPropertyAxiomImpl implements OWLObjectPropertyRangeAxiom {
    private OWLObjectProperty property;
    private OWLDescription range;
    
    public OWLObjectPropertyRangeAxiomImpl(OWLDataFactoryImpl factory, OWLObjectProperty property, OWLDescription range) {
        super(factory);

        this.property = property;
        this.range = range;
    }
    
    public OWLObjectProperty getProperty() {
        return property;
    }
    
    public OWLDescription getRange() {
        return range;
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
    
    public boolean equals(Object o) {
		if (o instanceof OWLObjectPropertyRangeAxiomImpl) {
			OWLObjectPropertyRangeAxiomImpl pda = (OWLObjectPropertyRangeAxiomImpl) o;
			if (pda.range.equals(range) && pda.property.equals(property)) return true; 
		}
		return false;
	}
     
     public int hashCode() {
		return super.hashCode() + "objectpropertyrange".hashCode() + hashCode(property) + hashCode(range);
     }
}
