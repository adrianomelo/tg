/*
 * Copyright (C) 2003, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.semanticweb.owl.model;

/**
 *
* A Visitor for OWL data structure objects. See <i>Design
 Patterns</i>, Gamma et. al. p.331 for a detailed description of the
 Visitor pattern. If you wish to define operations over the data
 structure (e.g. providing some kind of 3rd party representation of an
 OWL ontology), then you should implement this interface
 accordingly. For each concrete class <CODE>Concrete</CODE> in the
 OWLDescription hierarchy, a function <CODE>visit(Concrete)</CODE>
 must be provided.

 <BR><BR> The expression can then be visited using the {@link OWLDescription#accept(OWLDescriptionVisitor) accept} method.

 <BR><BR> Use of the Visitor architecture allows us to add
 application-specific functionality without "tainting" the data
 structure. Be aware, though, that if the concrete subclasses of
 {@link OWLDescription OWLDescription} change, then the implementors of this
 interface may need to change accordingly.

 *
 * @author Sean Bechhofer 
 * @version $Id: OWLDescriptionVisitor.java,v 1.2 2005/06/10 12:20:28 sean_bechhofer Exp $ 
 */

public interface OWLDescriptionVisitor 
{
    public void visit( OWLAnd node ) throws OWLException;
    //    public void visit( OWLClassAxiom node ) throws OWLException;
    public void visit( OWLDataAllRestriction node ) throws OWLException;
    public void visit( OWLDataCardinalityRestriction node ) throws OWLException;
    public void visit( OWLDataSomeRestriction node ) throws OWLException;
    public void visit( OWLDataValueRestriction node ) throws OWLException;
    public void visit( OWLFrame node ) throws OWLException;
    public void visit( OWLObjectAllRestriction node ) throws OWLException;
    public void visit( OWLObjectCardinalityRestriction node ) throws OWLException;
    public void visit( OWLObjectSomeRestriction node ) throws OWLException;
    public void visit( OWLObjectValueRestriction node ) throws OWLException;
    public void visit( OWLNot node ) throws OWLException;

    public void visit( OWLOr node ) throws OWLException;
    public void visit( OWLClass node ) throws OWLException;
    public void visit( OWLEnumeration node ) throws OWLException;

}// OWLDescriptionVisitor

/*
 * ChangeLog
 * $Log: OWLDescriptionVisitor.java,v $
 * Revision 1.2  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 *
 */
