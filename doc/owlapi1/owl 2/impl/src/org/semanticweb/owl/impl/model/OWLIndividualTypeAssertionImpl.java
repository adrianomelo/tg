/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualTypeAssertion;
import org.semanticweb.owl.model.OWLObjectVisitor;

/**
 * @author Evren Sirin
 */
public class OWLIndividualTypeAssertionImpl extends OWLObjectImpl implements
    OWLIndividualTypeAssertion {

    private OWLIndividual individual;
    private OWLDescription description;
    
    
    /**
     * @param arg0
     */
    public OWLIndividualTypeAssertionImpl(OWLDataFactoryImpl factory, OWLIndividual individual, OWLDescription description) {
        super(factory);
        
        this.individual = individual;
        this.description = description;
    }

    /* (non-Javadoc)
     * @see org.semanticweb.owl.model.OWLObject#accept(org.semanticweb.owl.model.OWLObjectVisitor)
     */
    public void accept(OWLObjectVisitor visitor) throws OWLException {
        visitor.visit(this);        
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.owlapi.OWLIndividualClassAxiom#getIndividual()
     */
    public OWLIndividual getIndividual() {
        return individual;
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.owlapi.OWLIndividualClassAxiom#getDescription()
     */
    public OWLDescription getType() {
        return description;
    }
    
    public boolean equals(Object obj) {
		if (super.equals(obj)) {
			OWLIndividualTypeAssertionImpl p = (OWLIndividualTypeAssertionImpl) obj;
			if (this.description.equals(p.description) 
					&& this.individual.equals(p.individual)) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return super.hashCode() + description.hashCode() + individual.hashCode();
	}

}
