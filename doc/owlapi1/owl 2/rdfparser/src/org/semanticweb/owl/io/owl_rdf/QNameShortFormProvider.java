package org.semanticweb.owl.io.owl_rdf;

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
 * Filename           $RCSfile: QNameShortFormProvider.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/12/15 13:00:04 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFSVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;

public class QNameShortFormProvider implements org.semanticweb.owl.io.ShortFormProvider {
	
    private static String OWL  = OWLVocabularyAdapter.OWL;
    private static String RDFS = RDFSVocabularyAdapter.RDFS;
    private static String RDF  = RDFVocabularyAdapter.RDF;	
    private static String XSD  = XMLSchemaSimpleDatatypeVocabulary.XS;
    private static String DC   = "http://purl.org/dc/elements/1.1/";
    private static String FOAF = "http://xmlns.com/foaf/0.1/";

    // stores a map of uri -> prefix
    Map uriToPrefix;
    Map prefixToUri;
    Map touchedPrefixes;

    /**
     *  
     */
    public QNameShortFormProvider() {
	uriToPrefix = new Hashtable();
	prefixToUri = new Hashtable();
	touchedPrefixes = new Hashtable();
	
	// initialize it with standard stuff
	setMapping("owl", OWL);
	setMapping("rdf", RDF);
	setMapping("rdfs", RDFS);
	setMapping("xsd", XSD);
	setMapping("dc", DC);
	//setMapping("foaf", FOAF);
    }
	
	
    public static boolean isNameStartChar(char ch) {
	return (Character.isLetter(ch) || ch == '_');
    }

    public static boolean isNameChar(char ch) {
	return (isNameStartChar(ch) ||
		Character.isDigit(ch) ||
		ch == '.' ||
		ch == '-');
    }
    
    public static int findNameStartIndex(String str) {
	char[] strChars = str.toCharArray();
	int nameStartIndex = -1;
	boolean foundNameChar = false;

	for (int strIndex = strChars.length-1; strIndex >= 0; strIndex--) {
	    char letter = strChars[strIndex];
	    
	    if (isNameStartChar(letter)) {
		nameStartIndex = strIndex;
		foundNameChar = true;
	    } else if (foundNameChar && !isNameChar(letter)) {
		break;
	    }
	}
	return nameStartIndex;
    }

    public static int findLastNameIndex(String str) {
	char [] strChars = str.toCharArray();
	int nameIndex = -1;
	
	for (int strIndex = strChars.length-1; strIndex >= 0; strIndex--) {
	    char letter = strChars[strIndex];
	    if (isNameChar(letter)) {
		nameIndex = strIndex;
	    } else {
		break;
	    }
	}
	return nameIndex;
    }

    public static int findNextNonNameIndex(String str, int startIndex) {
	char [] strChars = str.toCharArray();
	int nameIndex = startIndex;
	for (nameIndex = startIndex; nameIndex < strChars.length; nameIndex++) {
	    char letter = strChars[nameIndex];
	    if (!isNameChar(letter)) {
		break;
	    }
	}
	return nameIndex;
    }

    protected static String[] splitURI(URI uri) {
	int nameStart, prefixStart, prefixEnd;
	String uriString = uri.toString();
	String base, prefix, name;
	String[] bpn = new String[3];

	nameStart = findLastNameIndex(uriString);
	if (nameStart < 0) {
	    //System.out.println("Couldn't find name for "+uriString);
	    return null;
	}
	name = uriString.substring(nameStart);
	if (nameStart == 0) {
	    //System.out.println("Name starts at beginning");
	    base = "";
	    prefix = "a"; // Pick a unique prefix later
	} else {
	    base = uriString.substring(0, nameStart);
	    //System.out.println("Uri: "+ uri + " Base: " +base);
	    prefixStart = findNameStartIndex(base);
	    if (prefixStart < 0) {
		//System.out.println("Prefix < 0");
		prefix = "b"; // Pick a uniqe prefix later
	    } else {
		prefixEnd = findNextNonNameIndex(base, prefixStart+1);
		prefix = uriString.substring(prefixStart, prefixEnd);
	    }
	}
	bpn[0] = base;
	bpn[1] = prefix;
	bpn[2] = name;
	
	return bpn;
    }

    public String getPrefix(String uri) {
	return (String) uriToPrefix.get(uri);
    }

    public String getURI(String prefix) {
	return (String) prefixToUri.get(prefix);
    }

    public boolean setMapping(String prefix, String uri) {
    	prefix = removeExtension(prefix);
    	String currentUri = getURI(prefix);
    	if (currentUri == null) {
    		//the (prefix, uri) pair is not stored in the provider, we add it.
    		prefixToUri.put(prefix, uri);
    		uriToPrefix.put(uri, prefix);
    		return true;
    	} else if (currentUri == uri) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public Set getPrefixSet() {
	return prefixToUri.keySet();
    }
    
    public String shortForm(URI uri) {
	//System.out.println("Shortform for " + uri);
	return shortForm(uri, true);
    }
    public String shortForm(URI uri, boolean default_to_uri) {
	String[] bpn = splitURI(uri);
	String base, possible_prefix, prefix, name;
	String qname;

	if (bpn == null) {
	    if (default_to_uri) {
		return uri.toString();
	    } else {
		return null;
	    }
	}

	base = bpn[0];
	possible_prefix = bpn[1];
	name = bpn[2];

	prefix = getPrefix(base);
	if (prefix == null) {
	    // Check prefix for uniqueness
	    prefix = removeExtension(possible_prefix);
	    int mod = 0;
	    while (!setMapping(prefix, base)) {
		prefix = possible_prefix + mod;
		mod++;
	    }
	}
	
	touchedPrefixes.put(prefix, base);
	qname = prefix + ":" + name;
	return qname;
    }
    
    private String removeExtension(String prefix){
    	//	remove extension on prefix
    	if (prefix.indexOf(".")>=0) {
    		prefix = prefix.substring(0, prefix.lastIndexOf("."));
    	} // BUG: sometimes, for, I guess, new prefixes, you can get a .owl or something. Shouldn't be an else.
    	return prefix;
    }
}

/* ChangeLog
 * $Log: QNameShortFormProvider.java,v $
 * Revision 1.3  2004/12/15 13:00:04  sean_bechhofer
 * Adjustment of ShortFormProviders
 *
 * Revision 1.2  2004/10/25 18:01:31  aditkal
 * Bringing code up to date
 *  
 * Revision 1.1  2004/10/25 13:22:59  adityak
 * Adding QNameShortFormProvider
*/
