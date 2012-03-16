// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;


/**
 * IDebrisProducer is the interface implemented by objects that create Debris model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IDebrisProducer {

    /**
     * Adds debris as a specified position.
     * @param position 3D position (meters)
     * @return
     */
    public Debris addDebris( Point3D position );
    
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
     * IDebrisProducerListener is the interface implemented by all parties
     * that are interested in debris creation and deletion.
     */
    public interface IDebrisProducerListener {
        public void debrisAdded( Debris debris );
        public void debrisRemoved( Debris debris );
    }
    
    /**
     * Adds an IDebrisProducerListener.
     * @param listener
     */
    public void addDebrisProducerListener( IDebrisProducerListener listener );
    
    /**
     * Removes an IDebrisProducerListener.
     * @param listener
     */
    public void removeDebrisProducerListener( IDebrisProducerListener listener );
}
