/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;

/**
 * This class encapsulates a meter that supplies information about the amount
 * of a radiometric substance that has decayed in a given sample.
 * 
 * @author John Blanco
 */
public class RadiometricDatingMeter {

	private final ProbeModel _probe;
	
	public RadiometricDatingMeter() {
		_probe = new ProbeModel(new Point2D.Double(0, 0), 0.7);
	}

	public static double getPercentageCarbon14Remaining( DatableObject item ){
		return calculatePercentageRemaining(item.getAge(), Carbon14Nucleus.HALF_LIFE);
	}
	
	public static double getPercentageUranium238Remaining( DatableObject item ){
		return calculatePercentageRemaining(item.getAge(), Uranium238Nucleus.HALF_LIFE);
	}
	
	public ProbeModel getProbeModel(){
		return _probe;
	}
	
	/**
	 * Get the amount of a substance that would be left based on the age of an
	 * item and the half life of the nucleus of the radiometric material being
	 * tested.
	 * 
	 * @param item
	 * @param customNucleusHalfLife
	 * @return
	 */
	public static double getPercentageCustomNucleusRemaining( DatableObject item, double customNucleusHalfLife ){
		return calculatePercentageRemaining(item.getAge(), customNucleusHalfLife);
	}
	
	private static double calculatePercentageRemaining( double age, double halfLife ){
		if ( age <= 0 ){
			return 100;
		}
		else{
			return 100 * Math.exp( -0.693 * age / halfLife );
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
