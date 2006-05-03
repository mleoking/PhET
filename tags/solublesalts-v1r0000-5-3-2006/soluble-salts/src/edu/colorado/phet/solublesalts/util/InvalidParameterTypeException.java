/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.util;

/**
 * InvalidParameterTypeException
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InvalidParameterTypeException extends RuntimeException {

    public InvalidParameterTypeException() {
        super( "Invalid parameter type" );
    }
}
