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
        _strata.add( new Stratum( -13, 4 ) );
        _strata.add( new Stratum( -17, 4 ) );

        _datableObjects.add(new DatableObject("House", "house.png", new Point2D.Double(3, 5), 2.2, 1E11));
        _datableObjects.add(new DatableObject("Trilobyte", "trilobyte_fossil.png", new Point2D.Double(0, -11), 0.8, 1E11));
    	_datableObjects.add(new DatableObject("Animal Skull", "skull_animal.png", new Point2D.Double(-5, 2), 0.7, 1E11));
    	_datableObjects.add(new DatableObject("Living Tree", "tree_1.png", new Point2D.Double(-7, 8), 2.5, 1E11));
    	_datableObjects.add(new DatableObject("Distant Living Tree", "tree_1.png", new Point2D.Double(0, 6), 1, 1E11));
    	_datableObjects.add(new DatableObject("Fish Fossil", "fish_fossil.png", new Point2D.Double(-3, -8), 1, 1E11));
    	_datableObjects.add(new DatableObject("Dead Tree", "dead_tree.png", new Point2D.Double(8, 4), 1, 1E11));
    	_datableObjects.add(new DatableObject("Fish Bones", "fish_bones.png", new Point2D.Double(-4, -1.5), 1, 1E11));
    	_datableObjects.add(new DatableObject("Pottery", "pottery.png", new Point2D.Double(-7, -4.5), 0.5, 1E11));
    	_datableObjects.add(new DatableObject("Rock 1", "rock.png", new Point2D.Double(-3.0, 4.5), 1.2, 1E11));
    	_datableObjects.add(new DatableObject("Rock 2", "rock.png", new Point2D.Double(-2, -1.5), 0.5, 1E11));
    	_datableObjects.add(new DatableObject("Rock 3", "rock.png", new Point2D.Double(-8, -15), 0.3, 1E11));
    	_datableObjects.add(new DatableObject("Rock 4", "rock.png", new Point2D.Double(-2, -15), 0.6, 1E11));
    	_datableObjects.add(new DatableObject("Rock 5", "rock.png", new Point2D.Double(3, -4.5), 0.7, 1E11));
    	_datableObjects.add(new DatableObject("Rock 6", "rock.png", new Point2D.Double(2, -7.5), 0.25, 1E11));
    	_datableObjects.add(new DatableObject("Rock 7", "rock.png", new Point2D.Double(3, -15), 0.4, 1E11));
    	_datableObjects.add(new DatableObject("Rock 8", "rock.png", new Point2D.Double(5, -11), 0.3, 1E11));
    	_datableObjects.add(new DatableObject("Animal Skull 2", "skull_animal_2.png", new Point2D.Double(-4, -15.2), 1, 1E11));
    	_datableObjects.add(new DatableObject("Human Skull", "skull_human.png", new Point2D.Double(4, -1.5), 0.6, 1E11));
    	_datableObjects.add(new DatableObject("Gold Star", "gold_star.png", new Point2D.Double(-1, -4.5), 0.6, 1E11));
    	_datableObjects.add(new DatableObject("Cup", "cup.png", new Point2D.Double(2, -2), 0.4, 1E11));
    	_datableObjects.add(new DatableObject("Bone", "bone.png", new Point2D.Double(1, -15), 1, 1E11));

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
