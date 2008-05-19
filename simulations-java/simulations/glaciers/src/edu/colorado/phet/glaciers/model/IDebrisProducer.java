/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

/**
 * IDebrisProducer is the interface implemented by objects that create Debris model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IDebrisProducer {

    /**
     * Adds debris as a specified position.
     * @param position position (meters)
     * @return
     */
    public Debris addDebris( Point2D position );
    
    /**
     * Removes the specified debris.
     * @param debris
     */
    public void removeDebris( Debris debris );
    
    /**
     * Removes all debris.
     */
    public void removeAllDebris();
    
    /**
     * DebrisProducerListener is the interface implemented by all parties
     * that are interested in debris creation and deletion.
     */
    public interface IDebrisProducerListener {
        public void debrisAdded( Debris debris );
        public void debrisRemoved( Debris debris );
    }
    
    /**
     * Adds a DebrisProducerListener.
     * @param listener
     */
    public void addDebrisProducerListener( IDebrisProducerListener listener );
    
    /**
     * Removes a DebrisProducerListener.
     * @param listener
     */
    public void removeDebrisProducerListener( IDebrisProducerListener listener );
}
