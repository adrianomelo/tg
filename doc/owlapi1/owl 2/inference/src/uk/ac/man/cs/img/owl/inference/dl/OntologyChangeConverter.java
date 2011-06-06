/*
 * Copyright (C) 2003 The University of Manchester 
 * Copyright (C) 2003 The University of Karlsruhe
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 ****************************************************************
 * Source code information
 * -----------------------
 * Filename           $RCSfile: OntologyChangeConverter.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:15 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.inference.dl; 
import org.semanticweb.owl.model.change.ChangeVisitorAdapter;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.change.AddSuperClass;
import uk.ac.man.cs.img.owl.io.dig1_0.RenderingVisitor;
import org.semanticweb.owl.model.change.AddEquivalentClass;
import org.semanticweb.owl.model.change.AddEnumeration;
import org.semanticweb.owl.model.change.AddDomain;
import org.semanticweb.owl.model.change.AddDataPropertyRange;
import org.semanticweb.owl.model.change.AddObjectPropertyRange;
import org.semanticweb.owl.model.change.RemoveEntity;
import org.semanticweb.owl.model.change.AddImport;
import org.semanticweb.owl.model.change.AddClassAxiom;
import org.semanticweb.owl.model.change.AddPropertyAxiom;
import org.semanticweb.owl.model.change.AddSuperProperty;
import org.semanticweb.owl.model.change.AddIndividualClass;
import org.semanticweb.owl.model.change.AddInverse;
import org.semanticweb.owl.model.change.SetFunctional;
import org.semanticweb.owl.model.change.SetTransitive;
import org.semanticweb.owl.model.change.SetSymmetric;
import org.semanticweb.owl.model.change.SetInverseFunctional;
import org.semanticweb.owl.model.change.SetOneToOne;
import org.semanticweb.owl.model.change.SetDeprecated;
import org.semanticweb.owl.model.change.AddObjectPropertyInstance;
import org.semanticweb.owl.model.change.AddDataPropertyInstance;
import org.semanticweb.owl.model.change.AddIndividualAxiom;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.change.AddDataType;
import org.semanticweb.owl.model.change.AddAnnotationInstance;

// Generated package name


/**
 * A change visitor adapter that will take any ontology addition and
 * produce the appropriate DIG messages to represent that change.
 * 
 * @author Sean Bechhofer
 * @version $Id: OntologyChangeConverter.java,v 1.1.1.1 2003/10/14 17:10:15 sean_bechhofer Exp $
 */

public class OntologyChangeConverter extends ChangeVisitorAdapter 
{

    static Logger logger = Logger.getLogger(OntologyChangeConverter.class);

    private RenderingVisitor renderer;

    private ReasonerProxy reasonerProxy;

    public OntologyChangeConverter( ReasonerProxy rP )
    {
	this.renderer = new RenderingVisitor();
	this.reasonerProxy = rP;
    }
    
    /* Tell the reasoner something */
    private void tell(String str) throws OWLException {
	reasonerProxy.tell( str );
	logger.debug( str );
    }

    public void visit( AddAnnotationInstance event ) throws OWLException {
	/* Doesn't make a difference */
    }

    public void visit(AddEntity event) throws OWLException {
	EntityAdder adder = new EntityAdder();
	event.getEntity().accept(adder);
    }

    public void visit( AddDataType event ) throws OWLException {
	throw new OWLException( "Can't deal with data types!!" );
    }

    public void visit( RemoveEntity event ) throws OWLException {
	throw new OWLException( "Can't deal with removals!!" );
    }

    public void visit( AddImport event ) throws OWLException {
	throw new OWLException( "Can't yet deal with imports!" );
    }

    public void visit( AddIndividualAxiom event ) throws OWLException {
	throw new OWLException( "Can't deal with individual axioms!" );
    }

    public void visit( AddClassAxiom event ) throws OWLException {
	renderer.reset();
	event.getAxiom().accept( renderer );
	tell( renderer.result() + "\n" );
    }

    public void visit( AddPropertyAxiom event ) throws OWLException {
	renderer.reset();
	event.getAxiom().accept( renderer );
	tell( renderer.result() + "\n" );
    }

    public void visit( AddSuperClass event ) throws OWLException {
	String t = "<impliesc>\n";
	renderer.reset();
	event.getOWLClass().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	renderer.reset();
	event.getDescription().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	t = t + "</impliesc>\n";
	tell( t );
    }

    public void visit( AddSuperProperty event ) throws OWLException {
	String t = "<impliesr>\n";
	renderer.reset();
	event.getProperty().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	renderer.reset();
	event.getSuperProperty().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	t = t + "</impliesr>\n";
	tell( t );
    }

    public void visit( AddIndividualClass event ) throws OWLException {
	String t = "<instanceof>\n";
	renderer.reset();
	event.getIndividual().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	renderer.reset();
	event.getDescription().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	t = t + "</instanceof>\n";
	tell( t );
    }

    public void visit( AddEquivalentClass event ) throws OWLException {
	String t = "<equalc>\n";
	renderer.reset();
	event.getOWLClass().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	renderer.reset();
	event.getDescription().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	t = t + "</equalc>\n";
	tell( t );
    }

    public void visit( AddEnumeration event ) throws OWLException {
	String t = "<equalc>\n";
	renderer.reset();
	event.getOWLClass().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	renderer.reset();
	event.getEnumeration().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	t = t + "</equalc>\n";
	tell( t );
     }

    public void visit( AddDomain event ) throws OWLException {
	String t = "<domain>\n";
	renderer.reset();
	event.getProperty().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	renderer.reset();
	event.getDomain().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	t = t + "</domain>\n";
	tell( t );
    }

    public void visit( AddDataPropertyRange event ) throws OWLException
    {
	throw new OWLException( "Can't deal with data property range!!" );
    }

    public void visit( AddObjectPropertyRange event ) throws OWLException
    {
	String t = "<range>\n";
	renderer.reset();
	event.getProperty().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	renderer.reset();
	event.getRange().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	t = t + "</range>\n";
	tell( t );
    }

    public void visit( AddInverse event ) throws OWLException {
	String t = "<equalr>\n";
	renderer.reset();
	event.getProperty().accept( renderer );
	t = t + " " + renderer.result() + "\n";
	renderer.reset();
	t = t + "  <inverse>\n";
	event.getInverse().accept( renderer );
	t = t + "  " + renderer.result() + "\n";
	t = t + "  </inverse>\n";
	t = t + "</equalr>\n";
	tell( t );
    }

    public void visit( SetFunctional event ) throws OWLException {
	if ( event.isFunctional() ) {
	    String t = "<functional>\n";
	    renderer.reset();
	    event.getProperty().accept( renderer );
	    t = t + " " + renderer.result() + "\n";
	    t = t + "</functional>\n";
	    tell( t );
	} else {
	    throw new OWLException( event.toString() );
	}
    }

    public void visit( SetTransitive event ) throws OWLException {
	if ( event.isTransitive() ) {
	    String t = "<transitive>\n";
	    renderer.reset();
	    event.getProperty().accept( renderer );
	    t = t + " " + renderer.result() + "\n";
	    t = t + "</transitive>\n";
	    tell( t );
	} else {
	    throw new OWLException( event.toString() );
	}
    }

    public void visit( SetSymmetric event ) throws OWLException{
	throw new OWLException( "Can't deal with symmetric" );
    }
    public void visit( SetInverseFunctional event ) throws OWLException {
	throw new OWLException( "Can't deal with inversefunctional" );
    }
    public void visit( SetOneToOne event ) throws OWLException {
	throw new OWLException( "Can't deal with one-to-one" );
    }

    public void visit( SetDeprecated event ) throws OWLException {
	/* Doesn't do anything */
    }

    public void visit( AddObjectPropertyInstance event ) throws OWLException {
	throw new OWLException( "Can't deal with object property instance" );
    }

    public void visit( AddDataPropertyInstance event ) throws OWLException {
	throw new OWLException( "Can't deal with data property instance" );
    }
    

    private class EntityAdder implements OWLEntityVisitor {

        public void visit(OWLClass entity) throws OWLException {
	    tell("<defconcept name=\"" + entity.getURI() + "\"/>\n");
        }

        public void visit(OWLObjectProperty entity) throws OWLException {
	    tell("<defrole name=\"" + entity.getURI() + "\"/>\n");
        }

        public void visit(OWLAnnotationProperty entity) throws OWLException {
	    /* Has no effect on the semantics */
        }

        public void visit(OWLDataProperty entity) throws OWLException {
	    tell("<defattribute name=\"" + entity.getURI() + "\"/>\n");
        }

        public void visit(OWLIndividual entity) throws OWLException {
	    tell("<defindividual name=\"" + entity.getURI() + "\"/>\n");
        }
    }
    
} // OntologyChangeConverter



/*
 * ChangeLog
 * $Log: OntologyChangeConverter.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/06/03 17:01:53  seanb
 * Additional inference
 *
 * Revision 1.2  2003/05/19 12:48:35  seanb
 * Individual -> Object
 *
 * Revision 1.1  2003/05/19 11:51:40  seanb
 * Implementation of reasoners
 *
 */
