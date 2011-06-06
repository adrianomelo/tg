/*
 * Copyright (C) 2005, University of Karlsruhe
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

package org.semanticweb.owl.io.vocabulary;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides access to those XMLS Simple Datatypes,
 * that is a subset of (Primitive and Derived Datatypes)
 * which make sense for OWL and RDF.
 * 
 * @since 04.02.2003
 * @author <a href="mailto:volz@fzi.de">Raphael Volz</a>
 * 
 */
public class XMLSchemaSimpleDatatypeVocabulary {
    public static XMLSchemaSimpleDatatypeVocabulary INSTANCE =
        new XMLSchemaSimpleDatatypeVocabulary();

    /* Is this right?? */
    //String ns = "http://www.w3.org/2000/02/XMLSchema#";
    //    String ns = "http://www.w3.org/2001/XMLSchema#";

    public static final String XS = "http://www.w3.org/2001/XMLSchema#";

    protected XMLSchemaSimpleDatatypeVocabulary() {
    }

    public String getString() {
        return XS + "string";
    }
    public String getBoolean() {
        return XS + "boolean";
    }
    public String getDecimal() {
        return XS + "decimal";
    }
    public String getFloat() {
        return XS + "float";
    }
    public String getDouble() {
        return XS + "double";
    }
    public String getDuration() {
        return XS + "duration";
    }
    public String getDateTime() {
        return XS + "dateTime";
    }
    public String getTime() {
        return XS + "time";
    }
    public String getDate() {
        return XS + "date";
    }
    public String getGYearMonth() {
        return XS + "gYearMonth";
    }
    public String getGYear() {
        return XS + "gYear";
    }
    public String getGMonthDay() {
        return XS + "gMonthDay";
    }
    public String getGDay() {
        return XS + "gDay";
    }
    public String getGMonth() {
        return XS + "gMonth";
    }
    public String getHexBinary() {
        return XS + "hexBinary";
    }
    public String getBase64Binary() {
        return XS + "base64Binary";
    }
    public String getAnyURI() {
        return XS + "anyURI";
    }

    public String getNormalizedString() {
        return XS + "normalizedString";
    }
    public String getToken() {
        return XS + "token";
    }
    public String getLanguage() {
        return XS + "language";
    }
    public String getNMTOKEN() {
        return XS + "NMTOKEN";
    }
    public String getName() {
        return XS + "Name";
    }
    public String getNCName() {
        return XS + "NCName";
    }
    public String getInteger() {
        return XS + "integer";
    }
    public String getNonPositiveInteger() {
        return XS + "nonPositiveInteger";
    }
    public String getNegativeInteger() {
        return XS + "negativeInteger";
    }
    public String getLong() {
        return XS + "long";
    }
    public String getInt() {
        return XS + "int";
    }
    public String getShort() {
        return XS + "short";
    }
    public String getByte() {
        return XS + "byte";
    }
    public String getNonNegativeInteger() {
        return XS + "nonNegativeInteger";
    }
    public String getUnsignedLong() {
        return XS + "unsignedLong";
    }
    public String getUnsignedInt() {
        return XS + "unsignedInt";
    }
    public String getUnsignedShort() {
        return XS + "unsignedShort";
    }
    public String getUnsignedByte() {
        return XS + "unsignedByte";
    }
    public String getPositiveInteger() {
        return XS + "positiveInteger";
    }

    Set dt = null;
    public Set getDatatypes() {
        if (dt == null) {
        	dt = new HashSet();
            dt.add(getString());
            dt.add(getBoolean());
            dt.add(getDecimal());
            dt.add(getFloat());
            dt.add(getDouble());
            dt.add(getDuration());
            dt.add(getDateTime());
            dt.add(getTime());
            dt.add(getDate());
            dt.add(getGYearMonth());
            dt.add(getGYear());
            dt.add(getGMonthDay());
            dt.add(getGDay());
            dt.add(getGMonth());
            dt.add(getHexBinary());
            dt.add(getBase64Binary());
            dt.add(getAnyURI());
            dt.add(getNormalizedString());
            dt.add(getToken());
            dt.add(getLanguage());
            dt.add(getNMTOKEN());
            dt.add(getName());
            dt.add(getNCName());
            dt.add(getInteger());
            dt.add(getNonPositiveInteger());
            dt.add(getNegativeInteger());
            dt.add(getLong());
            dt.add(getInt());
            dt.add(getShort());
            dt.add(getByte());
            dt.add(getNonNegativeInteger());
            dt.add(getUnsignedLong());
            dt.add(getUnsignedInt());
            dt.add(getUnsignedShort());
            dt.add(getUnsignedByte());
            dt.add(getPositiveInteger());
        }
        return dt;
    }

}
