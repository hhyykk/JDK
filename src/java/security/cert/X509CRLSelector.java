/*
 * @(#)X509CRLSelector.java	1.8 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import sun.security.util.BigInt;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.x509.CRLNumberExtension;
import sun.security.x509.X500Name;

/**
 * A <code>CRLSelector</code> that selects <code>X509CRLs</code> that
 * match all specified criteria. This class is particularly useful when
 * selecting CRLs from a <code>CertStore</code> to check revocation status
 * of a particular certificate.
 * <p>
 * When first constructed, an <code>X509CRLSelector</code> has no criteria
 * enabled and each of the <code>get</code> methods return a default
 * value (<code>null</code>). Therefore, the {@link #match match} method 
 * would return <code>true</code> for any <code>X509CRL</code>. Typically, 
 * several criteria are enabled (by calling {@link #setIssuerNames setIssuerNames} 
 * or {@link #setDateAndTime setDateAndTime}, for instance) and then the
 * <code>X509CRLSelector</code> is passed to
 * {@link CertStore#getCRLs CertStore.getCRLs} or some similar
 * method.
 * <p>
 * Please refer to RFC 2459 for definitions of the X.509 CRL fields and
 * extensions mentioned below.
 * <p>
 * <b>Concurrent Access</b>
 * <p>
 * Unless otherwise specified, the methods defined in this class are not
 * thread-safe. Multiple threads that need to access a single
 * object concurrently should synchronize amongst themselves and
 * provide the necessary locking. Multiple threads each manipulating
 * separate objects need not synchronize.
 *
 * @see CRLSelector
 * @see X509CRL
 *
 * @version 	1.8 12/03/01
 * @since	1.4
 * @author	Steve Hanna
 */
public class X509CRLSelector implements CRLSelector {

    private static final Debug debug = Debug.getInstance("certpath");
    private HashSet issuerNames;
    private HashSet issuerX500Names;
    private BigInteger minCRL;
    private BigInteger maxCRL;
    private Date dateAndTime;
    private X509Certificate certChecking;

    /**
     * Creates an <code>X509CRLSelector</code>. Initially, no criteria are set
     * so any <code>X509CRL</code> will match.
     */
    public X509CRLSelector() {}

    /**
     * Sets the issuerNames criterion. The issuer distinguished name in the
     * <code>X509CRL</code> must match at least one of the specified
     * distinguished names. If <code>null</code>, any issuer distinguished name
     * will do.
     * <p>
     * This method allows the caller to specify, with a single method call,
     * the complete set of issuer names which <code>X509CRLs</code> may contain.
     * The specified value replaces the previous value for the issuerNames
     * criterion.
     * <p>
     * The <code>names</code> parameter (if not <code>null</code>) is a
     * <code>Collection</code> of names. Each name is a <code>String</code>
     * or a byte array representing a distinguished name (in RFC 2253 or
     * ASN.1 DER encoded form, respectively). If <code>null</code> is supplied
     * as the value for this argument, no issuerNames check will be performed.
     * <p>
     * Note that the <code>names</code> parameter can contain duplicate
     * distinguished names, but they may be removed from the 
     * <code>Collection</code> of names returned by the
     * {@link #getIssuerNames getIssuerNames} method.
     * <p>
     * If a name is specified as a byte array, it should contain a single DER
     * encoded distinguished name, as defined in X.501. The ASN.1 notation for
     * this structure is as follows.
     * <pre><code>
     * Name ::= CHOICE {
     *   RDNSequence }
     *
     * RDNSequence ::= SEQUENCE OF RelativeDistinguishedName
     *
     * RelativeDistinguishedName ::=
     *   SET SIZE (1 .. MAX) OF AttributeTypeAndValue
     *
     * AttributeTypeAndValue ::= SEQUENCE {
     *   type     AttributeType,
     *   value    AttributeValue }
     *
     * AttributeType ::= OBJECT IDENTIFIER
     *
     * AttributeValue ::= ANY DEFINED BY AttributeType
     * ....
     * DirectoryString ::= CHOICE {
     *       teletexString           TeletexString (SIZE (1..MAX)),
     *       printableString         PrintableString (SIZE (1..MAX)),
     *       universalString         UniversalString (SIZE (1..MAX)),
     *       utf8String              UTF8String (SIZE (1.. MAX)),
     *       bmpString               BMPString (SIZE (1..MAX)) }
     * </code></pre>
     * <p>
     * Note that a deep copy is performed on the <code>Collection</code> to
     * protect against subsequent modifications.
     *
     * @param names a <code>Collection</code> of names (or <code>null</code>)
     * @throws IOException if a parsing error occurs
     * @see #getIssuerNames
     */
    public void setIssuerNames(Collection names) throws IOException {
        if (names == null || names.size() == 0) {
            issuerNames = null;
            issuerX500Names = null;
        } else {
            HashSet tempNames = cloneAndCheckIssuerNames(names);
            // Ensure that we either set both of these or neither
            issuerX500Names = parseIssuerNames(tempNames);
            issuerNames = tempNames;
        }
    }

    /**
     * Adds a name to the issuerNames criterion. The issuer distinguished
     * name in the <code>X509CRL</code> must match at least one of the specified
     * distinguished names.
     * <p>
     * This method allows the caller to add a name to the set of issuer names
     * which <code>X509CRLs</code> may contain. The specified name is added to
     * any previous value for the issuerNames criterion.
     * If the specified name is a duplicate, it may be ignored.
     *
     * @param name the name in RFC 2253 form
     * @throws IOException if a parsing error occurs
     */
    public void addIssuerName(String name) throws IOException {
        addIssuerNameInternal(name, new X500Name(name, "RFC2253"));
    }

    /**
     * Adds a name to the issuerNames criterion. The issuer distinguished
     * name in the <code>X509CRL</code> must match at least one of the specified
     * distinguished names.
     * <p>
     * This method allows the caller to add a name to the set of issuer names
     * which <code>X509CRLs</code> may contain. The specified name is added to
     * any previous value for the issuerNames criterion. If the specified name
     * is a duplicate, it may be ignored.
     * If a name is specified as a byte array, it should contain a single DER
     * encoded distinguished name, as defined in X.501. The ASN.1 notation for
     * this structure is as follows.
     * <p>
     * The name is provided as a byte array. This byte array should contain
     * a single DER encoded distinguished name, as defined in X.501. The ASN.1
     * notation for this structure appears in the documentation for
     * {@link #setIssuerNames setIssuerNames(Collection names)}.
     * <p>
     * Note that the byte array supplied here is cloned to protect against
     * subsequent modifications.
     *
     * @param name a byte array containing the name in ASN.1 DER encoded form
     * @throws IOException if a parsing error occurs
     */
    public void addIssuerName(byte [] name) throws IOException {
        // clone because byte arrays are modifiable
        addIssuerNameInternal(name.clone(), new X500Name(name));
    }

    /**
     * A private method that adds a name (String or byte array) to the
     * issuerNames criterion. The issuer distinguished
     * name in the <code>X509CRL</code> must match at least one of the specified
     * distinguished names.
     *
     * @param name the name in string or byte array form
     * @param principal the name in X500Name form
     * @throws IOException if a parsing error occurs
     */
    private void addIssuerNameInternal(Object name, X500Name principal) 
    throws IOException {
        if (issuerNames == null) {
            issuerNames = new HashSet();
	}
        if (issuerX500Names == null) {
            issuerX500Names = new HashSet();
	}
        issuerNames.add(name);
        issuerX500Names.add(principal);
    }

    /**
     * Clone and check an argument of the form passed to
     * setIssuerNames. Throw an IOException if the argument is malformed.
     *
     * @param names a <code>Collection</code> of names. Each entry is a
     *              String or a byte array (the name, in string or ASN.1
     *              DER encoded form, respectively). <code>null</code> is
     *              not an acceptable value.
     * @return a deep copy of the specified <code>Collection</code>
     * @throws IOException if a parsing error occurs
     */
    private static HashSet cloneAndCheckIssuerNames(Collection names)
        throws IOException 
    {
        HashSet namesCopy = new HashSet();
        Iterator i = names.iterator();
        while (i.hasNext()) {
            Object nameObject = i.next();
            if (!(nameObject instanceof byte []) &&
	        !(nameObject instanceof String))
	        throw new IOException("name not byte array or String");
            if (nameObject instanceof byte [])
	        namesCopy.add(((byte []) nameObject).clone());
            else
	        namesCopy.add(nameObject);
        }
        return(namesCopy);
    }

    /**
     * Clone an argument of the form passed to setIssuerNames.
     * Throw a RuntimeException if the argument is malformed.
     * <p>
     * This method wraps cloneAndCheckIssuerNames, changing any IOException 
     * into a RuntimeException. This method should be used when the object being
     * cloned has already been checked, so there should never be any exceptions.
     *
     * @param names a <code>Collection</code> of names. Each entry is a
     *              String or a byte array (the name, in string or ASN.1
     *              DER encoded form, respectively). <code>null</code> is
     *              not an acceptable value.
     * @return a deep copy of the specified <code>Collection</code>
     * @throws RuntimeException if a parsing error occurs
     */
    private static HashSet cloneIssuerNames(Collection names) {
        try {
            return cloneAndCheckIssuerNames(names);
        } catch (IOException ioe) {
	    throw new RuntimeException(ioe);
        }
    }

    /**
     * Parse an argument of the form passed to setIssuerNames,
     * returning a Collection of X500Names.
     * Throw an IOException if the argument is malformed.
     *
     * @param names a <code>Collection</code> of names. Each entry is a
     *              String or a byte array (the name, in string or ASN.1
     *              DER encoded form, respectively). <Code>Null</Code> is
     *              not an acceptable value.
     * @return a HashSet of X500Names
     * @throws IOException if a parsing error occurs
     */
    private static HashSet parseIssuerNames(Collection names) 
    throws IOException {
        HashSet x500Names = new HashSet();
        Iterator i = names.iterator();
        while (i.hasNext()) {
            Object nameObject = i.next();
            if (nameObject instanceof String) {
	        x500Names.add(new X500Name((String)nameObject, "RFC2253"));
	    } else {
		x500Names.add(new X500Name((byte[])nameObject));
	    }
        }
        return(x500Names);
    }

    /**
     * Sets the minCRLNumber criterion. The <code>X509CRL</code> must have a
     * CRL number extension whose value is greater than or equal to the
     * specified value. If <code>null</code>, no minCRLNumber check will be 
     * done.
     *
     * @param minCRL the minimum CRL number accepted (or <code>null</code>)
     */
    public void setMinCRLNumber(BigInteger minCRL) {
        this.minCRL = minCRL;
    }

    /**
     * Sets the maxCRLNumber criterion. The <code>X509CRL</code> must have a
     * CRL number extension whose value is less than or equal to the
     * specified value. If <code>null</code>, no maxCRLNumber check will be 
     * done.
     *
     * @param maxCRL the maximum CRL number accepted (or <code>null</code>)
     */
    public void setMaxCRLNumber(BigInteger maxCRL) {
        this.maxCRL = maxCRL;
    }

    /**
     * Sets the dateAndTime criterion. The specified date must be
     * equal to or later than the value of the thisUpdate component
     * of the <code>X509CRL</code> and earlier than the value of the
     * nextUpdate component. There is no match if the <code>X509CRL</code>
     * does not contain a nextUpdate component.
     * If <code>null</code>, no dateAndTime check will be done.
     * <p>
     * Note that the <code>Date</code> supplied here is cloned to protect 
     * against subsequent modifications.
     *
     * @param dateAndTime the <code>Date</code> to match against
     *                    (or <code>null</code>)
     * @see #getDateAndTime
     */
    public void setDateAndTime(Date dateAndTime) {
        if (dateAndTime == null)
            this.dateAndTime = null;
        else
            this.dateAndTime = (Date) dateAndTime.clone();
    }

    /**
     * Sets the certificate being checked. This is not a criterion. Rather,
     * it is optional information that may help a <code>CertStore</code>
     * find CRLs that would be relevant when checking revocation for the
     * specified certificate. If <code>null</code> is specified, then no
     * such optional information is provided.
     *
     * @param cert the <code>X509Certificate</code> being checked
     *             (or <code>null</code>)
     * @see #getCertificateChecking
     */
    public void setCertificateChecking(X509Certificate cert) {
        certChecking = cert;
    }

    /**
     * Returns a copy of the issuerNames criterion. The issuer distinguished
     * name in the <code>X509CRL</code> must match at least one of the specified
     * distinguished names. If the value returned is <code>null</code>, any
     * issuer distinguished name will do.
     * <p>
     * If the value returned is not <code>null</code>, it is a
     * <code>Collection</code> of names. Each name is a <code>String</code>
     * or a byte array representing a distinguished name (in RFC 2253 or
     * ASN.1 DER encoded form, respectively).  Note that the 
     * <code>Collection</code> returned may contain duplicate names.
     * <p>
     * If a name is specified as a byte array, it should contain a single DER
     * encoded distinguished name, as defined in X.501. The ASN.1 notation for
     * this structure is given in the documentation for
     * {@link #setIssuerNames setIssuerNames(Collection names)}.
     * <p>
     * Note that a deep copy is performed on the <code>Collection</code> to
     * protect against subsequent modifications.
     *
     * @return a <code>Collection</code> of names (or <code>null</code>)
     * @see #setIssuerNames
     */
    public Collection getIssuerNames() {
        if (issuerNames == null)
            return null;
        return(cloneIssuerNames(issuerNames));
    }

    /**
     * Returns the minCRLNumber criterion. The <code>X509CRL</code> must have a
     * CRL number extension whose value is greater than or equal to the
     * specified value. If <code>null</code>, no minCRLNumber check will be done.
     *
     * @return the minimum CRL number accepted (or <code>null</code>)
     */
    public BigInteger getMinCRL() {
        return minCRL;
    }

    /**
     * Returns the maxCRLNumber criterion. The <code>X509CRL</code> must have a
     * CRL number extension whose value is less than or equal to the
     * specified value. If <code>null</code>, no maxCRLNumber check will be 
     * done.
     *
     * @return the maximum CRL number accepted (or <code>null</code>)
     */
    public BigInteger getMaxCRL() {
        return maxCRL;
    }

    /**
     * Returns the dateAndTime criterion. The specified date must be
     * equal to or later than the value of the thisUpdate component
     * of the <code>X509CRL</code> and earlier than the value of the
     * nextUpdate component. There is no match if the
     * <code>X509CRL</code> does not contain a nextUpdate component.
     * If <code>null</code>, no dateAndTime check will be done.
     * <p>
     * Note that the <code>Date</code> returned is cloned to protect against
     * subsequent modifications.
     *
     * @return the <code>Date</code> to match against (or <code>null</code>)
     * @see #setDateAndTime
     */
    public Date getDateAndTime() {
        if (dateAndTime == null)
            return null;
        return (Date) dateAndTime.clone();
    }

    /**
     * Returns the certificate being checked. This is not a criterion. Rather,
     * it is optional information that may help a <code>CertStore</code>
     * find CRLs that would be relevant when checking revocation for the
     * specified certificate. If the value returned is <code>null</code>, then 
     * no such optional information is provided.
     *
     * @return the certificate being checked (or <code>null</code>)
     * @see #setCertificateChecking
     */
    public X509Certificate getCertificateChecking() {
        return certChecking;
    }

    /**
     * Returns a printable representation of the <code>X509CRLSelector</code>.
     *
     * @return a <code>String</code> describing the contents of the
     *         <code>X509CRLSelector</code>.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("X509CRLSelector: [\n");
        if (issuerNames != null) {
            sb.append("  IssuerNames:\n");
            Iterator i = issuerNames.iterator();
            while (i.hasNext())
	        sb.append("    " + i.next() + "\n");
        }
        if (minCRL != null)
            sb.append("  minCRLNumber: " + minCRL + "\n");
        if (maxCRL != null)
            sb.append("  maxCRLNumber: " + maxCRL + "\n");
        if (dateAndTime != null)
            sb.append("  dateAndTime: " + dateAndTime + "\n");
        if (certChecking != null)
            sb.append("  Certificate being checked: " + certChecking + "\n");
        sb.append("]");
        return sb.toString();
    }

    /**
     * Decides whether a <code>CRL</code> should be selected.
     *
     * @param crl the <code>CRL</code> to be checked
     * @return <code>true</code> if the <code>CRL</code> should be selected,
     *         <code>false</code> otherwise
     */
    public boolean match(CRL crl) {
        if (!(crl instanceof X509CRL))
            return(false);
        X509CRL xcrl = (X509CRL) crl;

        /* match on issuer name */
        if (issuerNames != null) {
            X500Principal issuer = xcrl.getIssuerX500Principal();
            Iterator i = issuerX500Names.iterator();
            boolean found = false;
            while (!found && i.hasNext()) {
	        if (((X500Name) i.next()).equals(issuer))
	            found = true;
	    }
            if (!found) {
	        if (debug != null)
	    	    debug.println("X509CRLSelector.match: issuer DNs "
			+ "don't match");
	        return false;
            }
        }

        /* Get CRL number extension from CRL */
        BigInteger crlNum = null;
        byte [] crlNumExtVal = xcrl.getExtensionValue("2.5.29.20");
        if (crlNumExtVal != null) {
            try {
	        DerInputStream in = new DerInputStream(crlNumExtVal);
	        byte [] encoded = in.getOctetString();
	        CRLNumberExtension crlNumExt = 
	            new CRLNumberExtension(Boolean.FALSE, encoded);
	        crlNum = (BigInteger) crlNumExt.get(CRLNumberExtension.NUMBER);
            } catch (IOException ex) {
	        if (debug != null)
	  	    debug.println("X509CRLSelector.match: exception in "
			+ "decoding CRL number");
	        return false;
            }
        }

        /* match on minCRLNumber */
        if (minCRL != null) {
            if (crlNum == null) {
	        if (debug != null)
	  	    debug.println("X509CRLSelector.match: no CRLNumber");
	        return false;
            }
            if (crlNum.compareTo(minCRL) == -1) {
	        if (debug != null)
	  	    debug.println("X509CRLSelector.match: CRLNumber too small");
	        return false;
            }
        }

        /* match on maxCRLNumber */
        if (maxCRL != null) {
            if (crlNum == null) {
	        if (debug != null)
		    debug.println("X509CRLSelector.match: no CRLNumber");
	        return false;
            }
            if (crlNum.compareTo(maxCRL) == 1) {
	        if (debug != null)
		    debug.println("X509CRLSelector.match: CRLNumber too large");
	        return false;
            }
        }

        /* match on dateAndTime */
        Date crlThisUpdate = xcrl.getThisUpdate();
        if (dateAndTime != null) {
            Date nextUpdate = xcrl.getNextUpdate();
            if (nextUpdate == null) {
	        if (debug != null)
		    debug.println("X509CRLSelector.match: nextUpdate null");
	        return false;
            }
            if (crlThisUpdate.after(dateAndTime) 
	          || nextUpdate.before(dateAndTime)) {
	        if (debug != null)
		    debug.println("X509CRLSelector.match: update out of range");
	        return false;
            }
        }

        return true ;
    }

    /**
     * Returns a copy of this object.
     *
     * @return the copy
     */
    public Object clone() {
        try {
            Object copy = super.clone();
            if (issuerNames != null) {
                issuerNames = new HashSet(issuerNames);
                issuerX500Names = new HashSet(issuerX500Names);
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            /* Cannot happen */
            throw new InternalError(e.toString());
        }
    }
}
