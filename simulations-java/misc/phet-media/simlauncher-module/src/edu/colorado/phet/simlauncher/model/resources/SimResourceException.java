/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/model/resources/SimResourceException.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.model.resources;

/**
 * SimResourceException
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
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
