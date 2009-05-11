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
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Decay for a single atomic
 * nucleus.
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
	ArrayList<Stratum> _layers= new ArrayList<Stratum>();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public RadioactiveDatingGameModel()
    {
    	_datableObjects.add(new DatableObject("Trilobyte", "trilobyte_fossil.png", new Point2D.Double(0, 0), 0.5, 1E11));
    	_datableObjects.add(new DatableObject("Animal Skull", "skull_animal.png", new Point2D.Double(5, -6), 0.7, 1E11));
    	_datableObjects.add(new DatableObject("Living Tree", "tree_1.png", new Point2D.Double(3, -1), 3, 1E11));
    	_datableObjects.add(new DatableObject("House", "house.png", new Point2D.Double(5, -1), 2, 1E11));
    	_datableObjects.add(new DatableObject("Fish Fossil", "fish_fossil.png", new Point2D.Double(4, -4), 1, 1E11));

        _layers.add( new Stratum( -3, 3 ) );
        _layers.add( new Stratum( -6, 3 ) );
        _layers.add( new Stratum( -9, 3 ) );
        _layers.add( new Stratum( -12, 3 ) );
        _layers.add( new Stratum( -16, 4 ) );
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public Iterable<DatableObject> getItemIterable(){
    	return _datableObjects;
    }
    public Iterable<Stratum> getLayerIterable(){
        return _layers;
    }

    public int getLayerCount() {
        return _layers.size();
    }

    public Stratum getLayer( int i ) {
        return _layers.get(i);
    }
}
