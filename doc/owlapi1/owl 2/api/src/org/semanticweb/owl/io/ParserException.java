/*
 * Copyright (C) 2005, University of Manchester
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

package org.semanticweb.owl.io; 
import org.xml.sax.SAXException;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * ParserException.java
 *
 *
 * Created: Thu Dec 19 15:57:01 2002
 *
 * @author Sean Bechhofer
 * @version $Id: ParserException.java,v 1.3 2005/06/10 12:20:28 sean_bechhofer Exp $
 */

public class ParserException extends OWLException 
{
    private int code;
    
    public ParserException( String message ) {
	super( message );
    }
    
    /**
     * Exception that has another Exception as a cause
     * @param string - the message
     * @param e - the causing exception
     */
    public ParserException(String string, Exception e) {
	super(string, e);
    }

    /**
     * Exception that has another Exception as a cause
     * @param string - the message
     * @param e - the causing exception
     */
    public ParserException(String string, Exception e, int code) {
	super(string, e);
	this.code = code;
    }
    
    public int getCode() {
	return code;
    }

    
} // ParserException



/*
 * ChangeLog
 * $Log: ParserException.java,v $
 * Revision 1.3  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/03/30 17:46:36  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/06/06 14:26:33  seanb
 * Minor changes to exceptions.
 *
 * Revision 1.2  2003/02/05 14:29:37  rvolz
 * Parser Stuff, Connection
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 */
