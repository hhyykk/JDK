/*
 * @(#)LoginException.java	1.13 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth.login;

/**
 * This is the basic login exception.
 *
 * @version 1.13, 12/03/01
 * @see javax.security.auth.login.LoginContext
 */

public class LoginException extends java.security.GeneralSecurityException {

    /**
     * Constructs a LoginException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public LoginException() {
	super();
    }

    /**
     * Constructs a LoginException with the specified detail message.
     * A detail message is a String that describes this particular
     * exception.
     *
     * <p>
     *
     * @param msg the detail message.  
     */
    public LoginException(String msg) {
	super(msg);
    }
}
