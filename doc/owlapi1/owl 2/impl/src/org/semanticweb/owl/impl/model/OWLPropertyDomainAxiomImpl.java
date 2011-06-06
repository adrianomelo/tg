/*
 * Created on Oct 29, 2004
 */
package org.semanticweb.owl.impl.model;

import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiomVisitor;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;

/**
 * @author Evren Sirin
 */
public class OWLPropertyDomainAxiomImpl extends OWLPropertyAxiomImpl implements OWLPropertyDomainAxiom {
    private OWLProperty property;
    private OWLDescription domain;
    
    public OWLPropertyDomainAxiomImpl(OWLDataFactoryImpl factory, OWLProperty property, OWLDescription domain) {
        super(factory);

        this.property = property;
        this.domain = domain;
    }
    
    public OWLProperty getProperty() {
        return property;
    }
    
    public OWLDescription getDomain() {
        return domain;
    }

    public void accept(OWLObjectVisitor visitor) throws OWLException {
        visitor.visit(this);        
    }

    public void accept(OWLPropertyAxiomVisitor visitor) throws OWLException {
        visitor.visit(this);       
    }
    
    public boolean equals(Object o) {
		if (o instanceof OWLPropertyDomainAxiomImpl) {
			OWLPropertyDomainAxiomImpl pda = (OWLPropertyDomainAxiomImpl) o;
			if (pda.domain.equals(domain) && pda.property.equals(property)) return true; 
		}
		return false;
	}
     
     public int hashCode() {
		return super.hashCode() + "domain".hashCode() + hashCode(property) + hashCode(domain);
     }
}
