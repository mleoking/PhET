/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayModelListener;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Polonium211CompositeNucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;

/**
 * This class defines a model (in the model-view-controller paradigm) that
 * defines a set a geological strata (or layers) containing objects that can
 * be dated using radiometric means.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	ArrayList<DatableObject> _datableObjects = new ArrayList<DatableObject>();
	ArrayList<Stratum> _strata = new ArrayList<Stratum>();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public RadioactiveDatingGameModel()
    {
        _strata.add( new Stratum( -3, 3 ) );
        _strata.add( new Stratum( -6, 3 ) );
        _strata.add( new Stratum( -9, 3 ) );
        _strata.add( new Stratum( -12, 3 ) );
        _strata.add( new Stratum( -16, 4 ) );

        _datableObjects.add(new DatableObject("Trilobyte", "trilobyte_fossil.png", new Point2D.Double(0, 0), 0.5, 1E11));
    	_datableObjects.add(new DatableObject("Animal Skull", "skull_animal.png", new Point2D.Double(5, -6), 0.7, 1E11));
    	_datableObjects.add(new DatableObject("Living Tree", "tree_1.png", new Point2D.Double(3, -1), 3, 1E11));
    	_datableObjects.add(new DatableObject("House", "house.png", new Point2D.Double(5, -1), 2, 1E11));
    	_datableObjects.add(new DatableObject("Fish Fossil", "fish_fossil.png", new Point2D.Double(4, getBottomOfStrata()), 1, 1E11));
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public Iterable<DatableObject> getItemIterable(){
    	return _datableObjects;
    }
    public Iterable<Stratum> getLayerIterable(){
        return _strata;
    }

    public int getLayerCount() {
        return _strata.size();
    }

    public Stratum getLayer( int i ) {
        return _strata.get(i);
    }
    
    /**
     * Get the lowest point of the model for which a stratum (a.k.a. a layer
     * of sediment) is found.
     * 
     * @return
     */
    public double getBottomOfStrata(){
    	double bottom = 0;
    	for ( Stratum stratum : _strata ){
    		if ( stratum.getBottomOfStratumY() < bottom ){
    			bottom = stratum.getBottomOfStratumY();
    		}
    	}
    	return bottom;
    }
}
