/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import java.util.Set;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLObjectVisitor;

/**
 * @author Evren Sirin
 */
public class OWLObjectPropertyInstanceImpl extends OWLObjectImpl implements OWLObjectPropertyInstance {
    private OWLIndividual subject; 
    private OWLObjectProperty property;
    private OWLIndividual object;
    
    public OWLObjectPropertyInstanceImpl(OWLDataFactoryImpl factory, OWLIndividual subject, OWLObjectProperty property, OWLIndividual object) {
        super(factory);
        
        this.subject = subject;
        this.property = property;
        this.object = object;
    }

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
    public OWLObjectProperty getProperty() {
        return property;
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.owlapi.OWLObjectPropertyInstance#getObject()
     */
    public OWLIndividual getObject() {
        return object;
    }

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLOntologyObject#getOntologies()
	 */
	public Set getOntologies() throws OWLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			OWLObjectPropertyInstanceImpl p = (OWLObjectPropertyInstanceImpl) obj;
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
