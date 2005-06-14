/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.util;

import java.util.EventObject;


/**
 * FourierLog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierLog {

    private static boolean _traceEnabled = true;
    
    private FourierLog() {}
    
    public static void setTraceEnabled( boolean traceEnabled ) {
        _traceEnabled = traceEnabled;
    }
    
    public static void trace( String message ) {
        if ( _traceEnabled ) {
            System.out.println( "trace: " + message );
        } 
    }
}
