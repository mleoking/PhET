/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * This class encapsulates a meter that supplies information about the amount
 * of a radiometric substance that has decayed in a given sample.
 * 
 * @author John Blanco
 */
public class RadiometricDatingMeter {

	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private final ProbeModel _probe;
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	private DatableObject _itemBeingTouched = null;
	private RadioactiveDatingGameModel _model;
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public RadiometricDatingMeter( RadioactiveDatingGameModel model ) {
		_model = model;
		_probe = new ProbeModel(new Point2D.Double(-20, -8), -0.3);
		_probe.addListener(new ProbeModel.Listener(){

			public void probeModelChanged() {
				updateReading();
			}
		});
	}

	//----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	public ProbeModel getProbeModel(){
		return _probe;
	}
	
	/**
	 * Get the item that is currently being touched by the meter's probe, if
	 * there is one.
	 * 
	 * @return item being touched if there is one, null if not
	 */
	public DatableObject getItemBeingTouched(){
		return _itemBeingTouched;
	}
	
    /**
     * Update the current reading based on the input probe location.
     */
    private void updateReading(){

    	DatableObject newTouchedItem = _model.getDatableItemAtLocation(_probe.getTipLocation());
    	
    	if (_itemBeingTouched != newTouchedItem){
    		_itemBeingTouched = newTouchedItem;
    		notifyListeners();
    	}
    }
	
    static interface Listener {
        void touchedItemChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.touchedItemChanged();
        }
    }
	
	/**
	 * This class represents the probe that moves around and comes in contact
	 * with various datable elements in the model.
	 */
    public static class ProbeModel {
        private Point2D.Double tipLocation;
        private ArrayList listeners = new ArrayList();
        private double angle;
        private double tipWidth = 0.1 * 0.35;
        private double tipHeight = 0.3 * 1.25 * 0.75;

        public ProbeModel( double angle ) {
            this( new Point2D.Double(), angle );
        }

        public ProbeModel( Point2D.Double tipLocation, double angle ) {
            this.tipLocation = new Point2D.Double( tipLocation.getX(), tipLocation.getY() );
            this.angle = angle;
        }

        public void translate( double dx, double dy ) {
            tipLocation.x += dx;
            tipLocation.y += dy;
            notifyListeners();
        }

        public Point2D getTipLocation() {
            return new Point2D.Double( tipLocation.x, tipLocation.y );
        }

        public Shape getTipShape() {
            Rectangle2D.Double tip = new Rectangle2D.Double( tipLocation.x - tipWidth / 2, tipLocation.y, tipWidth, tipHeight );
            return AffineTransform.getRotateInstance( angle, tipLocation.x, tipLocation.y ).createTransformedShape( tip );
        }

        public double getAngle() {
            return angle;
        }

        static interface Listener {
            void probeModelChanged();
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void notifyListeners() {
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.probeModelChanged();
            }
        }
    }
}
