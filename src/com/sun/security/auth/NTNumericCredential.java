/*
 * @(#)NTNumericCredential.java	1.11 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.security.auth;

/**
 * <p> This class abstracts an NT security token
 * and provides a mechanism to do same-process security impersonation.
 *
 * @version 1.11, 12/03/01
 */

public class NTNumericCredential {

    private int impersonationToken;
    
    /**
     * Create an <code>NTNumericCredential</code> with an integer value.
     *
     * <p>
     *
     * @param token the Windows NT security token for this user. <p>
     *
     */
    public NTNumericCredential(int token) {
        this.impersonationToken = token;
    }
    
    /**
     * Return an integer representation of this
     * <code>NTNumericCredential</code>.
     *
     * <p>
     *
     * @return an integer representation of this
     *		<code>NTNumericCredential</code>.
     */
    public int getToken() {
        return impersonationToken;
    }
    
    /**
     * Return a string representation of this <code>NTNumericCredential</code>.
     *
     * <p>
     *
     * @return a string representation of this <code>NTNumericCredential</code>.
     */
    public String toString() {
	java.text.MessageFormat form = new java.text.MessageFormat
		(sun.security.util.ResourcesMgr.getString
			("NTNumericCredential: name",
			"sun.security.util.AuthResources"));
	Object[] source = {Integer.toString(impersonationToken)};
	return form.format(source);
    }
    
    /**
     * Compares the specified Object with this <code>NTNumericCredential</code>
     * for equality.  Returns true if the given object is also a
     * <code>NTNumericCredential</code> and the two NTNumericCredentials
     * represent the same NT security token.
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *		<code>NTNumericCredential</code>.
     *
     * @return true if the specified Object is equal equal to this
     *		<code>NTNumericCredential</code>.
     */
    public boolean equals(Object o) {
	if (o == null)
	    return false;

        if (this == o)
            return true;
 
        if (!(o instanceof NTNumericCredential))
            return false;
        NTNumericCredential that = (NTNumericCredential)o;

	if (impersonationToken == that.getToken())
	    return true;
	return false;
    }
 
    /**
     * Return a hash code for this <code>NTNumericCredential</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>NTNumericCredential</code>.
     */
    public int hashCode() {
	return this.impersonationToken;
    }
}
