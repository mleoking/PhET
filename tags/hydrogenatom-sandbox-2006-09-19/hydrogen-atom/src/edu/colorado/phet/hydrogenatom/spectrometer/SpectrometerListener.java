/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.spectrometer;

import java.util.EventListener;
import java.util.EventObject;


public interface SpectrometerListener extends EventListener {

    public void stop( SpectrometerEvent event );
    
    public void start( SpectrometerEvent event );
    
    public void reset( SpectrometerEvent event );
    
    public void close( SpectrometerEvent event );
    
    public void snapshot( SpectrometerEvent event );
    
    public static class SpectrometerEvent extends EventObject {
        public SpectrometerEvent( Object source ) {
            super( source );
        }
    }
    
    public static class SpectrometerAdapter implements SpectrometerListener {
        public SpectrometerAdapter() {}

        public void stop( SpectrometerEvent event ) {}

        public void start( SpectrometerEvent event ) {}

        public void reset( SpectrometerEvent event ) {}
        
        public void close( SpectrometerEvent event ) {}

        public void snapshot( SpectrometerEvent event ) {}
    }
}
