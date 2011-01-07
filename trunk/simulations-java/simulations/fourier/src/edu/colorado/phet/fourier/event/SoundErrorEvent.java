// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.event;

import java.util.EventObject;


/**
 * SoundErrorEvent indicates that a sound error has occurred.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SoundErrorEvent extends EventObject {

    private Exception _exception;
    private String _message;
    
    /**
     * Sole constructor.
     * 
     * @param source
     * @param exception
     * @param message
     */
    public SoundErrorEvent( Object source, Exception exception, String message ) {
        super( source );
        _exception = exception;
        _message = message;
    }
    
    public Exception getException() {
        return _exception;
    }
    
    public String getMessage() {
        return _message;
    }
}
