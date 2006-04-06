/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

/**
 * Spin
 * <p>
 * An enumeration class.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Spin {

    public static final Spin UP = new Spin();
    public static final Spin DOWN = new Spin();

    /**
     * No public or protected constructors
     */
    private Spin() {
    }
}
