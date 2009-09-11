/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;

/**
 * This class defines a model (in the model-view-controller paradigm) that
 * defines a set a geological strata (or layers) containing objects that can
 * be dated using radiometric means.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameModel implements ModelContainingDatableItems {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	private static final double TOTAL_DEPTH_OF_STRATA = 19;
	private static final double NUMBER_OF_STRATA = 6;
	private static final double NOMINAL_STRATUM_DEPTH = TOTAL_DEPTH_OF_STRATA / NUMBER_OF_STRATA; 
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	private ArrayList<DatableItem> _datableObjects = new ArrayList<DatableItem>();
	private ArrayList<Stratum> _strata = new ArrayList<Stratum>();
	private RadiometricDatingMeter _meter;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public RadioactiveDatingGameModel()
    {
    	// Add the strata to the model.  Add the top layer first, then loop
    	// through adding the rest.
    	
    	Stratum stratum;
        stratum = new Stratum(new Stratum.LayerLine(0), new Stratum.LayerLine(NOMINAL_STRATUM_DEPTH));
        _strata.add(stratum);
        
        for (int i = 1; i < NUMBER_OF_STRATA; i++){
        	// Add the next stratum.
            stratum = new Stratum( stratum.getBottomLine(), new Stratum.LayerLine((i+1) * NOMINAL_STRATUM_DEPTH ));
            _strata.add( stratum );
        }
        
        // Add the datable objects.
        // Params:                             name, image file, location(x, y), size, rotation angle (radians), age (ms), is organic
        _datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_HOUSE, "house.png", new Point2D.Double(8, 4), 6, 0, HalfLifeInfo.convertYearsToMs(70), true));
        _datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_TRILOBYTE, "trilobyte_fossil.png", new Point2D.Double(-8, -14.5), 3.5, 0, HalfLifeInfo.convertYearsToMs(310.3E6), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ANIMAL_SKULL, "skull_animal.png", new Point2D.Double(-26, 2), 3, Math.PI/4, HalfLifeInfo.convertYearsToMs(135), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_LIVING_TREE, "tree_1.png", new Point2D.Double(-21, 5), 5.5, 0, 0, true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_DISTANT_LIVING_TREE, "tree_1.png", new Point2D.Double(0, 3.5), 2, 0, 0, true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_FISH_FOSSIL_1, "fish_fossil.png", new Point2D.Double(-20, -8), 7, 0, HalfLifeInfo.convertYearsToMs(28E6), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_DEAD_TREE, "tree_dead_no_space.png", new Point2D.Double(23, 2.5), 3, Math.PI/2, HalfLifeInfo.convertYearsToMs(235), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_FISH_FOSSIL_2, "fish_bones.png", new Point2D.Double(10, -14.5), 5, 0, HalfLifeInfo.convertYearsToMs(251E6), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_POTTERY, "pottery.png", new Point2D.Double(-18, -1.5), 2.8, Math.PI/2, HalfLifeInfo.convertYearsToMs(4975), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ROCK1, "rock_1.png", new Point2D.Double(-6.0, -1.5), 3, 0, HalfLifeInfo.convertYearsToMs(493.3E6), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ROCK2, "rock_2.png", new Point2D.Double(6, -4.5), 1.5, 0, HalfLifeInfo.convertYearsToMs(910.1E6), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ROCK3, "rock_3.png", new Point2D.Double(-22, -14.5), 2.5, 0, HalfLifeInfo.convertYearsToMs(120E6), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ROCK4, "rock_4.png", new Point2D.Double(18, -11), 1.5, 0, HalfLifeInfo.convertYearsToMs(1.61E9), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ROCK5, "rock_4.png", new Point2D.Double(18, -4.5), 2, 0, HalfLifeInfo.convertYearsToMs(2.05E6), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ROCK6, "rock_6.png", new Point2D.Double(20, -7.5), 1.5, 0, HalfLifeInfo.convertYearsToMs(52E6), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ROCK7, "rock_7.png", new Point2D.Double(25, -15), 2.5, 0, HalfLifeInfo.convertYearsToMs(3E9), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_ROCK8, "rock_8.png", new Point2D.Double(-15, -11), 2, 0, HalfLifeInfo.convertYearsToMs(3.45E9), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_DINOSAUR_SKULL, "skull_animal_2.png", new Point2D.Double(4, -11.0), 4.5, 0, HalfLifeInfo.convertYearsToMs(149E6), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_HUMAN_SKULL, "skull_human.png", new Point2D.Double(20, -1.5), 2.4, 0, HalfLifeInfo.convertYearsToMs(1135), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_WOODEN_CUP, "cup.png", new Point2D.Double(8, -2), 1.7,  -Math.PI / 3, HalfLifeInfo.convertYearsToMs(1019), true));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_BONE, "bone.png", new Point2D.Double(-3, -8), 4.5, 0, HalfLifeInfo.convertYearsToMs(34.35E6), false));
    	_datableObjects.add(new DatableItem(NuclearPhysicsStrings.DATABLE_ITEM_HUMAN_SKULL, "skull_human.png", new Point2D.Double(-10, -4.5), 2, 1.0, HalfLifeInfo.convertYearsToMs(66.2E3), true));

    	// Add the meter and register for user-initiated movements.
    	_meter = new RadiometricDatingMeter( this );
    	
    	_meter.getProbeModel().addObserver(new SimpleObserver(){
			public void update() {
				getDatableItemAtLocation( _meter.getProbeModel().getTipLocation() );
			}
    	});
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public Iterable<DatableItem> getItemIterable(){
    	return _datableObjects;
    }
    
    public Iterable<Stratum> getStratumIterable(){
        return _strata;
    }

    public int getLayerCount() {
        return _strata.size();
    }

    public Stratum getLayer( int i ) {
        return _strata.get(i);
    }
    
    public RadiometricDatingMeter getMeter(){
    	return _meter;
    }
    
    public ConstantDtClock getClock(){
    	// This model is unclocked, so return null.
    	return null;
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
    
    /**
     * Get the highest point of the model for which a stratum (a.k.a. a layer
     * of sediment) is found.
     * 
     * @return
     */
    public double getTopOfStrata(){
    	double top = Double.NEGATIVE_INFINITY;
    	for ( Stratum stratum : _strata ){
    		if ( stratum.getTopOfStratumY() > top ){
    			top = stratum.getTopOfStratumY();
    		}
    	}
    	return top;
    }
    
    //------------------------------------------------------------------------
    // Other Methods
    //------------------------------------------------------------------------

    /* (non-Javadoc)
	 * @see edu.colorado.phet.nuclearphysics.module.radioactivedatinggame.ModelContainingDatableItems#getDatableItemAtLocation(java.awt.geom.Point2D)
	 */
    public DatableItem getDatableItemAtLocation( Point2D probeLocation ){

    	DatableItem datableItem = null;
    	
    	for ( DatableItem datableObject : _datableObjects ){
    		if (datableObject.contains(probeLocation)){
    			datableItem = datableObject;
    		}
    	}
    	
    	return datableItem;
    }
    
    public DatableItem getDatableAir() {
    	// There is nothing special about the air for this model, so just
    	// return the static datable air object.
    	return DatableItem.DATABLE_AIR;
    }
}
