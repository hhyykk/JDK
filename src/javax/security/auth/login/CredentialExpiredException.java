/*
 * @(#)CredentialExpiredException.java	1.13 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth.login;

/**
 * Signals that a <code>Credential</code> has expired.
 * 
 * <p> This exception is thrown by LoginModules when they determine
 * that a <code>Credential</code> has expired.
 * For example, a <code>LoginModule</code> authenticating a user
 * in its <code>login</code> method may determine that the user's
 * password, although entered correctly, has expired.  In this case
 * the <code>LoginModule</code> throws this exception to notify
 * the application.  The application can then take the appropriate
 * steps to assist the user in updating the password.
 *
 * @version 1.13, 12/03/01
 */
public class CredentialExpiredException extends LoginException {

    /**
     * Constructs a CredentialExpiredException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public CredentialExpiredException() {
	super();
    }

    /**
     * Constructs a CredentialExpiredException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * <p>
     *
     * @param msg the detail message.  
     */
    public CredentialExpiredException(String msg) {
	super(msg);
    }
}
