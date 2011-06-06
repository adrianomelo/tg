/*
 * Copyright (C) 2003 The University of Manchester 
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
 * Filename           $RCSfile: AllBuildTest.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/05/05 16:05:46 $
 *               by   $Author: dturi $
 ****************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author dturi 
 * $Id: AllBuildTest.java,v 1.1 2004/05/05 16:05:46 dturi Exp $
 */
public class AllBuildTest {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllBuildTest.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for uk.ac.man.cs.img.owl.io.dig1_1");
		//$JUnit-BEGIN$
		suite.addTestSuite(RendererTest.class);
		//$JUnit-END$
		return suite;
	}
}
