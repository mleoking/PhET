/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package com.pixelzoom.util;


/**
 * PrintSystemProperties prints the System properties to System.out.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PrintSystemProperties {

    public static void main( String[] args ) {
        System.getProperties().list(System.out);
    }
}
