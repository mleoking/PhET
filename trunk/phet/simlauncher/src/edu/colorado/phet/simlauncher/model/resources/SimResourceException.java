/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model.resources;

/**
 * SimResourceException
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimResourceException extends Exception {
//public class SimResourceException extends RuntimeException {

    public SimResourceException() {
        super();
    }

    public SimResourceException( String message ) {
        super( message );
    }

    public SimResourceException( String message, Throwable cause ) {
        super( message, cause );
    }

    public SimResourceException( Throwable cause ) {
        super( cause );
    }
}
