/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyInstance;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectVisitor;

/**
 * @author Evren Sirin
 */
public class OWLDataPropertyInstanceImpl extends OWLObjectImpl implements OWLDataPropertyInstance {
    private OWLIndividual subject; 
    private OWLDataProperty property;
    private OWLDataValue object;
    
    public OWLDataPropertyInstanceImpl(OWLDataFactoryImpl factory, OWLIndividual subject, OWLDataProperty property, OWLDataValue object) {
        super(factory);
        
        this.subject = subject;
        this.property = property;
        this.object = object;
    }

    /* (non-Javadoc)
     * @see org.semanticweb.owl.impl.model.OWLObjectImpl#accept(org.semanticweb.owl.model.OWLObjectVisitor)
     */
    public void accept( OWLObjectVisitor visitor ) throws OWLException {
    	visitor.visit( this );
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.owlapi.OWLObjectPropertyInstance#getSubject()
     */
    public OWLIndividual getSubject() {
        return subject;
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.owlapi.OWLObjectPropertyInstance#getProperty()
     */
    public OWLDataProperty getProperty() {
        return property;
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.owlapi.OWLObjectPropertyInstance#getObject()
     */
    public OWLDataValue getObject() {
        return object;
    }

    public boolean equals(Object obj) {
    		if (super.equals(obj)) {
    			OWLDataPropertyInstanceImpl p = (OWLDataPropertyInstanceImpl) obj;
    			if (this.subject.equals(p.subject) 
    					&& this.property.equals(p.property) 
    					&& this.object.equals(p.object)) {
    				return true;
    			}
    		}
    		return false;
    }
    
    public int hashCode() {
    		return super.hashCode() + subject.hashCode() + property.hashCode() + object.hashCode();
    }
    
}
