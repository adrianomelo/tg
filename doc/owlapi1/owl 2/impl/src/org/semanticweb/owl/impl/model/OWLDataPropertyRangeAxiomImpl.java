/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;

/**
 * @author Evren Sirin
 */
public class OWLDataPropertyRangeAxiomImpl extends OWLPropertyAxiomImpl implements OWLDataPropertyRangeAxiom {
    private OWLDataProperty property;
    private OWLDataRange range;
    
    public OWLDataPropertyRangeAxiomImpl(OWLDataFactoryImpl factory, OWLDataProperty property, OWLDataRange range) {
        super(factory);

        this.property = property;
        this.range = range;
    }
    
    public OWLDataProperty getProperty() {
        return property;
    }
    
    public OWLDataRange getRange() {
        return range;
    }


    /* (non-Javadoc)
     * @see org.semanticweb.owl.impl.model.OWLObjectImpl#accept(org.semanticweb.owl.model.OWLObjectVisitor)
     */
    public void accept( OWLObjectVisitor visitor ) throws OWLException {
    	visitor.visit( this );
    }

    public void accept(OWLPropertyAxiomVisitor visitor) throws OWLException {
        visitor.visit(this);       
    }
    
    public boolean equals(Object o) {
		if (o instanceof OWLDataPropertyRangeAxiomImpl) {
			OWLDataPropertyRangeAxiomImpl pda = (OWLDataPropertyRangeAxiomImpl) o;
			if (pda.range.equals(range) && pda.property.equals(property)) return true; 
		}
		return false;
	}
     
     public int hashCode() {
		return super.hashCode() + "datapropertyrange".hashCode() + hashCode(property) + hashCode(range);
     }
}
