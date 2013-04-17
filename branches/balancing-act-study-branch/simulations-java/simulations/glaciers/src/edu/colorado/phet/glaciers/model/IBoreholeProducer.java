// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

/**
 * IBoreholeProducer is the interface implemented by objects that create Borehole model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IBoreholeProducer {

    /**
     * Adds a borehole as a specified position.
     * @param position position (meters)
     * @return
     */
    public Borehole addBorehole( Point2D position );
    
    /**
     * Removes a specified borehole.
     * @param borehole
     */
    public void removeBorehole( Borehole borehole );
    
    /**
     * Removes all boreholes.
     */
    public void removeAllBoreholes();
    
    /**
     * IBoreholeProducerListener is the interface implemented by all parties
     * that are interested in borehole creation and deletion.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     */
    public interface IBoreholeProducerListener {
        public void boreholeAdded( Borehole borehole );
        public void boreholeRemoved( Borehole borehole );
    }
    
    /**
     * Adds an IBoreholeProducerListener.
     * @param listener
     */
    public void addBoreholeProducerListener( IBoreholeProducerListener listener );
    
    /**
     * Removes an IBoreholeProducerListener.
     * @param listener
     */
    public void removeBoreholeProducerListener( IBoreholeProducerListener listener );
}
