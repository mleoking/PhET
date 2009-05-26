/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D.Double;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class defines a model (in the model-view-controller paradigm) that
 * defines a set a geological strata (or layers) containing objects that can
 * be dated using radiometric means.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameModel extends SimpleObservable {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	private static final double TOTAL_DEPTH_OF_STRATA = 16;
	private static final double NUMBER_OF_STRATA = 5;
	private static final double NOMINAL_STRATUM_DEPTH = TOTAL_DEPTH_OF_STRATA / NUMBER_OF_STRATA; 
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	private ArrayList<DatableObject> _datableObjects = new ArrayList<DatableObject>();
	private ArrayList<Stratum2> _strata = new ArrayList<Stratum2>();
	private RadiometricDatingMeter _meter;
	private Rectangle2D _edgeOfWorldRect = new Rectangle2D.Double();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public RadioactiveDatingGameModel()
    {
    	// Add the strata to the model.
    	
    	Stratum2 stratum;
        stratum = new Stratum2(new Stratum2.LayerLine(TOTAL_DEPTH_OF_STRATA - NOMINAL_STRATUM_DEPTH),
        		new Stratum2.LayerLine(TOTAL_DEPTH_OF_STRATA));
        _strata.add(stratum);
        
        for (int i = 1; i < NUMBER_OF_STRATA; i++){
        	// Add the next stratum.
            stratum = new Stratum2(new Stratum2.LayerLine(
            		TOTAL_DEPTH_OF_STRATA - (i + 1) * NOMINAL_STRATUM_DEPTH),
            		stratum.getTopLine() );
            _strata.add( stratum );
        }

        // Add the datable objects.
        // Params:                             name, image file, location(x, y), size, age (ms), rotation angle
        _datableObjects.add(new DatableObject("House", "house.png", new Point2D.Double(8, 4), 6.5, 0, MultiNucleusDecayModel.convertYearsToMs(75)));
        _datableObjects.add(new DatableObject("Trilobyte", "trilobyte_fossil.png", new Point2D.Double(0, -11), 3.5, 0, MultiNucleusDecayModel.convertYearsToMs(500E6)));
    	_datableObjects.add(new DatableObject("Animal Skull", "skull_animal.png", new Point2D.Double(-21, 2), 3, Math.PI/4, MultiNucleusDecayModel.convertYearsToMs(25)));
    	_datableObjects.add(new DatableObject("Living Tree", "tree_1.png", new Point2D.Double(-16, 7), 5.5, 0, 0));
    	_datableObjects.add(new DatableObject("Distant Living Tree", "tree_1.png", new Point2D.Double(0, 4.5), 2, 0, 0));
    	_datableObjects.add(new DatableObject("Fish Fossil", "fish_fossil.png", new Point2D.Double(-15, -8), 7, 0, MultiNucleusDecayModel.convertYearsToMs(10E6)));
    	_datableObjects.add(new DatableObject("Dead Tree", "dead_tree.png", new Point2D.Double(23, 3), 6, Math.PI/2, MultiNucleusDecayModel.convertYearsToMs(100)));
    	_datableObjects.add(new DatableObject("Fish Bones", "fish_bones.png", new Point2D.Double(-20, -1.5), 5, 0, MultiNucleusDecayModel.convertYearsToMs(1000)));
    	_datableObjects.add(new DatableObject("Pottery", "pottery.png", new Point2D.Double(-10, -4.5), 3.8, Math.PI/2, MultiNucleusDecayModel.convertYearsToMs(5000)));
    	_datableObjects.add(new DatableObject("Rock 1", "rock.png", new Point2D.Double(-7.0, 4.5), 3, 0, MultiNucleusDecayModel.convertYearsToMs(1E9)));
    	_datableObjects.add(new DatableObject("Rock 2", "rock.png", new Point2D.Double(-4, -1.5), 2, 0, MultiNucleusDecayModel.convertYearsToMs(1E9)));
    	_datableObjects.add(new DatableObject("Rock 3", "rock.png", new Point2D.Double(-22, -15), 2.5, 0, MultiNucleusDecayModel.convertYearsToMs(2E9)));
    	_datableObjects.add(new DatableObject("Rock 4", "rock.png", new Point2D.Double(-4, -15), 1.5, 0, MultiNucleusDecayModel.convertYearsToMs(2E9)));
    	_datableObjects.add(new DatableObject("Rock 5", "rock.png", new Point2D.Double(6, -4.5), 2, 0, MultiNucleusDecayModel.convertYearsToMs(2E9)));
    	_datableObjects.add(new DatableObject("Rock 6", "rock.png", new Point2D.Double(15, -7.5), 1.5, 0, MultiNucleusDecayModel.convertYearsToMs(3E9)));
    	_datableObjects.add(new DatableObject("Rock 7", "rock.png", new Point2D.Double(12, -15), 2.5, 0, MultiNucleusDecayModel.convertYearsToMs(3E9)));
    	_datableObjects.add(new DatableObject("Rock 8", "rock.png", new Point2D.Double(20, -11), 2, 0, MultiNucleusDecayModel.convertYearsToMs(3.5E9)));
    	_datableObjects.add(new DatableObject("Animal Skull 2", "skull_animal_2.png", new Point2D.Double(-8, -15.2), 4.5, 0, MultiNucleusDecayModel.convertYearsToMs(220E6)));
    	_datableObjects.add(new DatableObject("Human Skull", "skull_human.png", new Point2D.Double(13, -1.5), 2.4, 0, MultiNucleusDecayModel.convertYearsToMs(1000)));
    	_datableObjects.add(new DatableObject("Gold Star", "gold_star.png", new Point2D.Double(-2, -4.5), 2.5, 0, MultiNucleusDecayModel.convertYearsToMs(3000)));
    	_datableObjects.add(new DatableObject("Cup", "cup.png", new Point2D.Double(4, -2), 3.2,  -Math.PI / 3, MultiNucleusDecayModel.convertYearsToMs(1000)));
    	_datableObjects.add(new DatableObject("Bone", "bone.png", new Point2D.Double(4, -15), 4.5, 0, MultiNucleusDecayModel.convertYearsToMs(220E6)));

    	// Add the meter and register for user-initiated movements.
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
    public Iterable<Stratum2> getLayerIterable(){
        return _strata;
    }

    public int getLayerCount() {
        return _strata.size();
    }

    public Stratum2 getLayer( int i ) {
        return _strata.get(i);
    }
    
    public RadiometricDatingMeter getMeter(){
    	return _meter;
    }
    
    /**
     * Set the rectangle that represents the position of the edge of the world.
     * 
     * @param edgeOfWorldRect
     */
    public void setEdgeOfWorldRect(Rectangle2D edgeOfWorldRect){
    	_edgeOfWorldRect.setRect(edgeOfWorldRect);
    	notifyObservers();
    }
    
    /**
     * Get the rectangle that represents the position of the edge of the world.
     * 
     * @param edgeOfWorldRect
     */
    public Rectangle2D getEdgeOfWorldRect(){
    	Rectangle2D rect = new Rectangle2D.Double();
    	rect.setRect(_edgeOfWorldRect);
    	return rect;
    }
    
    /**
     * Get the lowest point of the model for which a stratum (a.k.a. a layer
     * of sediment) is found.
     * 
     * @return
     */
    public double getBottomOfStrata(){
    	double bottom = 0;
    	for ( Stratum2 stratum : _strata ){
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
