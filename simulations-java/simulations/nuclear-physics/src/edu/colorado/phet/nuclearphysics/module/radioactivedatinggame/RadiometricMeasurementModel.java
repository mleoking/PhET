/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This class defines a model (in the model-view-controller paradigm) that
 * includes a single datable item that contains a single datable item and
 * a meter which can be used to radiometrically date the item.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementModel implements ModelContainingDatableItems {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	private DatableItem _datableItem;
	private RadiometricDatingMeter _meter;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public RadiometricMeasurementModel()
    {

    	// Add the meter and register for user-initiated movements.
    	_meter = new RadiometricDatingMeter( this, new Point2D.Double(-20, 3) );
    	
    	_meter.getProbeModel().addObserver(new SimpleObserver(){
			public void update() {
				getDatableItemAtLocation( _meter.getProbeModel().getTipLocation() );
				
			}
    	});
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public RadiometricDatingMeter getMeter(){
    	return _meter;
    }
    
    /**
     * Get the Y location of the ground.  Note that the ground is assumed to
     * extend infinitely in the positive and negative X directions.
     */
    public double getGroundLevelY(){
    	// At least for now, the ground is always assumed to be at Y location
    	// zero.
    	return 0;
    }
    
    //------------------------------------------------------------------------
    // Other Methods
    //------------------------------------------------------------------------

    /**
     * Get the datable item at the specified model location, or null if there
     * isn't anything there.
     */
    public DatableItem getDatableItemAtLocation( Point2D probeLocation ){

    	DatableItem datableItem = null;
    	
    	if ( (_datableItem != null ) && (_datableItem.contains( probeLocation ) ) ){
    		datableItem = _datableItem;
    	}
    	
    	return datableItem;
    }
}
