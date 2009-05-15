/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;

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
	RadiometricDatingMeter _meter;

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

        // Add the datable object.
        // Params:                             name, image file, location(x, y), size, age (ms), rotation angle
        _datableObjects.add(new DatableObject("House", "house.png", new Point2D.Double(8, 4), 6.5, 0, 3.2E12));
        _datableObjects.add(new DatableObject("Trilobyte", "trilobyte_fossil.png", new Point2D.Double(0, -11), 3.5, 0, 1.6E19));
    	_datableObjects.add(new DatableObject("Animal Skull", "skull_animal.png", new Point2D.Double(-21, 2), 3, Math.PI/4, 1E11));
    	_datableObjects.add(new DatableObject("Living Tree", "tree_1.png", new Point2D.Double(-16, 7), 5.5, 0, 1E11));
    	_datableObjects.add(new DatableObject("Distant Living Tree", "tree_1.png", new Point2D.Double(0, 4.5), 2, 0, 1E11));
    	_datableObjects.add(new DatableObject("Fish Fossil", "fish_fossil.png", new Point2D.Double(-15, -8), 7, 0, 1E11));
    	_datableObjects.add(new DatableObject("Dead Tree", "dead_tree.png", new Point2D.Double(23, 3), 6, Math.PI/2, 1E11));
    	_datableObjects.add(new DatableObject("Fish Bones", "fish_bones.png", new Point2D.Double(-20, -1.5), 5, 0, 1E11));
    	_datableObjects.add(new DatableObject("Pottery", "pottery.png", new Point2D.Double(-10, -4.5), 3.8, Math.PI/2, 6.3E13));
    	_datableObjects.add(new DatableObject("Rock 1", "rock.png", new Point2D.Double(-7.0, 4.5), 3, 0, 1E11));
    	_datableObjects.add(new DatableObject("Rock 2", "rock.png", new Point2D.Double(-4, -1.5), 2, 0, 1E11));
    	_datableObjects.add(new DatableObject("Rock 3", "rock.png", new Point2D.Double(-22, -15), 2.5, 0, 1E11));
    	_datableObjects.add(new DatableObject("Rock 4", "rock.png", new Point2D.Double(-4, -15), 1.5, 0, 1E11));
    	_datableObjects.add(new DatableObject("Rock 5", "rock.png", new Point2D.Double(6, -4.5), 2, 0, 1E11));
    	_datableObjects.add(new DatableObject("Rock 6", "rock.png", new Point2D.Double(15, -7.5), 1.5, 0, 1E11));
    	_datableObjects.add(new DatableObject("Rock 7", "rock.png", new Point2D.Double(12, -15), 2.5, 0, 1E11));
    	_datableObjects.add(new DatableObject("Rock 8", "rock.png", new Point2D.Double(20, -11), 2, 0, 1E11));
    	_datableObjects.add(new DatableObject("Animal Skull 2", "skull_animal_2.png", new Point2D.Double(-8, -15.2), 4.5, 0, 1E11));
    	_datableObjects.add(new DatableObject("Human Skull", "skull_human.png", new Point2D.Double(13, -1.5), 2.4, 0, 1E11));
    	_datableObjects.add(new DatableObject("Gold Star", "gold_star.png", new Point2D.Double(-2, -4.5), 2.5, 0, 1E11));
    	_datableObjects.add(new DatableObject("Cup", "cup.png", new Point2D.Double(4, -2), 3.2,  -Math.PI / 3, 1E11));
    	_datableObjects.add(new DatableObject("Bone", "bone.png", new Point2D.Double(4, -15), 4.5, 0, 1E11));

    	_meter = new RadiometricDatingMeter( this );
    	
    	_meter.getProbeModel().addListener(new RadiometricDatingMeter.ProbeModel.Listener(){
    		public void probeModelChanged(){
    			getDatableItemAtLocation( _meter.getProbeModel().getTipLocation() );
    		}
    	});
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
    
    public RadiometricDatingMeter getMeter(){
    	return _meter;
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
    
    //------------------------------------------------------------------------
    // Other Methods
    //------------------------------------------------------------------------

    /**
     * Get the datable item at the specified model location, or null if there
     * isn't anything there.
     */
    public DatableObject getDatableItemAtLocation( Point2D probeLocation ){

    	DatableObject datableItem = null;
    	
    	for ( DatableObject datableObject : _datableObjects ){
    		if (datableObject.contains(probeLocation)){
    			datableItem = datableObject;
    		}
    	}
    	
    	return datableItem;
    }
}
