/*
 * @(#)Unreferenced.java	1.10 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

/**
 * A remote object implementation should implement the
 * <code>Unreferenced</code> interface to receive notification when there are
 * no more clients that reference that remote object.
 *
 * @version 1.10, 12/03/01
 * @author  Ann Wollrath
 * @author  Roger Riggs
 * @since   JDK1.1
 */
public interface Unreferenced {
    /**
     * Called by the RMI runtime sometime after the runtime determines that
     * the reference list, the list of clients referencing the remote object,
     * becomes empty.
     * @since JDK1.1
     */
    public void unreferenced();
}
