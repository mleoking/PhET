/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;

/**
 * This class encapsulates a meter that supplies information about the amount
 * of a radiometric substance that has decayed in a given sample.
 * 
 * @author John Blanco
 */
public class RadiometricDatingMeter {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	enum MeasurementMode { OBJECTS, AIR };
	
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private final ObjectProbe _probe;
	private DatableItem _itemBeingTouched = null;
	private ModelContainingDatableItems _model;
	private NucleusType _nucleusTypeForDating;
	private double _halfLifeOfCustomNucleus = NuclearPhysicsConstants.DEFAULT_CUSTOM_NUCLEUS_HALF_LIFE;
	private double _prevPercentageRemaining = Double.NaN;
	protected ArrayList<Listener> _listeners = new ArrayList<Listener>();
	private ClockAdapter _clockListener;
	private MeasurementMode _measurementMode = MeasurementMode.OBJECTS;
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	/**
	 * Constructor for the model representation of a meter that can be used to
	 * radiometrically date various items.
	 * 
	 * @param model - Model containing datable items.
	 * @param initialTipLocation - Initial location of the probe tip in model
	 * space.
	 * @param updatePeriodically - Controls whether periodic updating should
	 * be performed.  Periodic updates are only needed when used in
	 * situations where the datable items can move and/or age. 
	 */
	public RadiometricDatingMeter( ModelContainingDatableItems model, Point2D initialTipLocation,
			boolean updatePeriodically ) {
		
		_model = model;
		_probe = new ObjectProbe(initialTipLocation, 2);
		_probe.addObserver(new SimpleObserver(){
			public void update() {
				updateTouchedItem();
			}
		});
		
		if (updatePeriodically){
			_clockListener = new ClockAdapter(){
				@Override
			    public void clockTicked( ClockEvent clockEvent ) {
			    	updateState();
			    }

				@Override
				public void simulationTimeReset(ClockEvent clockEvent) {
					updateState();
				}
			};
			_model.getClock().addClockListener(_clockListener);
		}
		
		// Set the default nucleus type.
		_nucleusTypeForDating = NucleusType.CARBON_14;
		
		updateTouchedItem();
	}

	public RadiometricDatingMeter( ModelContainingDatableItems model ) {
		// Construct with the probe in a default location.
		this(model, new Point2D.Double(-2, -5.0), false);
	}

	//----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	public ObjectProbe getProbeModel(){
		return _probe;
	}
	
	public MeasurementMode getMeasurementMode(){
		return _measurementMode;
	}
	
	public void setMeasurementMode( MeasurementMode measurementMode ){
		if (_measurementMode != measurementMode){
			
			_measurementMode = measurementMode;
			notifyMeasurementModeChanged();
			updateTouchedItem();
		}
	}
	
	/**
	 * Update the reading state and the touched state.
	 */
	public void updateState(){
		
		// Update the touched state.  This will send out any needed notifications.
    	updateTouchedItem();
    	
    	// Update the age.
    	if ((getPercentageOfDatingElementRemaining() != _prevPercentageRemaining) &&
    		!(Double.isNaN(getPercentageOfDatingElementRemaining()) && Double.isNaN(_prevPercentageRemaining))){
    		notifyReadingChanged();
    		_prevPercentageRemaining = getPercentageOfDatingElementRemaining();
    	}
	}
	
	/**
	 * Clean up any memory references that could cause memory leaks.  This
	 * should be called just before removing an instance of this class from
	 * the model.
	 */
	public void cleanup(){
		_model.getClock().removeClockListener(_clockListener);
	}
	
	/**
	 * Get the percentage of the element that is being used for radiometric
	 * dating that remains in the currently touched item.  If no item is being
	 * touched, this returns NaN (not a number).
	 */
	public double getPercentageOfDatingElementRemaining(){
		
		if (_itemBeingTouched == null){
			return Double.NaN;
		}
		
		double halflife;
		
		if (_nucleusTypeForDating == NucleusType.HEAVY_CUSTOM){
			halflife = _halfLifeOfCustomNucleus;
		}
		else {
			halflife = HalfLifeInfo.getHalfLifeForNucleusType( _nucleusTypeForDating );
		}
		
		if (_itemBeingTouched.isOrganic() && _nucleusTypeForDating == NucleusType.URANIUM_238){
			// For the purposes of this sim, organic materials do not contain
			// any U238, nor matter how old they are.
			return 0;
		}
		
		if (!_itemBeingTouched.isOrganic() && _nucleusTypeForDating == NucleusType.CARBON_14){
			// For the purposes of this sim, inorganic materials do not
			// contain any Carbon 14.
			return 0;
		}
		
		if ( _itemBeingTouched.getRadiometricAge() <= 0 ){
			return 100;
		}
		else{
			// Calculate the percentage based on the standard exponential
			// decay curve.
			return 100 * Math.exp( -0.693 * _itemBeingTouched.getRadiometricAge() / halflife );
		}
	}
	
	public void addListener(Listener listener) {
	    if ( !_listeners.contains( listener )){
	        _listeners.add( listener );
	    }
	}

	/**
	 * Get the item that is currently being touched by the meter's probe, if
	 * there is one.
	 * 
	 * @return item being touched if there is one, null if not
	 */
	public DatableItem getItemBeingTouched(){
		if (_measurementMode == MeasurementMode.AIR){
			return _model.getDatableAir();
		}
		else{
			return _itemBeingTouched;
		}
	}
	
	public void setNucleusTypeUsedForDating(NucleusType nucleusType){
		_nucleusTypeForDating = nucleusType;
		notifyDatingElementChanged();
	}
	
	public NucleusType getNucleusTypeUsedForDating(){
		return _nucleusTypeForDating;
	}
	
	/**
	 * Set the half life to use when dating.  This is only applicable when a
	 * custom nuclues is being used, otherwise the half life is determined by
	 * the selected nucleus type.
	 * 
	 * @param halfLife - Half life in milliseconds.
	 */
	public void setHalfLifeForCustomNucleus(double halfLife){
		
		_halfLifeOfCustomNucleus = halfLife;
		
		notifyDatingElementChanged();
	}
	
	/**
	 * Get the half life of the currently selected dating element.
	 * 
	 * @return half life in milliseconds.
	 */
	public double getHalfLifeForDating(){
		if (_nucleusTypeForDating == NucleusType.HEAVY_CUSTOM){
			return _halfLifeOfCustomNucleus;
		}
		else{
			return HalfLifeInfo.getHalfLifeForNucleusType(_nucleusTypeForDating);
		}
	}
	
    /**
     * Update the current touched item based on the input probe location.
     */
    private void updateTouchedItem(){

    	if (_measurementMode == MeasurementMode.OBJECTS){
	    	DatableItem newTouchedItem = _model.getDatableItemAtLocation(_probe.getTipLocation());
	    	
	    	if (_itemBeingTouched != newTouchedItem){
	    		_itemBeingTouched = newTouchedItem;
	    		notifyTouchedStateChanged();
	    	}
    	}
    	else{
    		// Always touching air when set to the AIR mode.
    		if (_itemBeingTouched != _model.getDatableAir()){
    			_itemBeingTouched = _model.getDatableAir();
    			notifyTouchedStateChanged();
    		}
    	}
    }
    
	/**
	 * Notify listeners about a change in the touch state.
	 */
	private void notifyTouchedStateChanged() {
	    for (int i = 0; i < _listeners.size(); i++){
	        _listeners.get(i).touchedStateChanged();
	    }
	}
	
	/**
	 * Notify listeners about a change in the touch state.
	 */
	private void notifyReadingChanged() {
	    for (int i = 0; i < _listeners.size(); i++){
	        _listeners.get(i).readingChanged();
	    }
	}
	
	/**
	 * Notify listeners about a change in the element that is being used to
	 * perform the dating.
	 */
	private void notifyDatingElementChanged() {
	    for (int i = 0; i < _listeners.size(); i++){
	        _listeners.get(i).datingElementChanged();
	    }
	}
	
	private void notifyMeasurementModeChanged() {
	    for (int i = 0; i < _listeners.size(); i++){
	        _listeners.get(i).measurementModeChanged();
	    }
	}
	
	//----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    static interface Listener{
    	public void touchedStateChanged();
    	public void datingElementChanged();
    	public void readingChanged();
    	public void measurementModeChanged();
    }
    
    static class Adapter implements Listener {
    	public void touchedStateChanged(){};
    	public void datingElementChanged(){};
    	public void readingChanged(){};
		public void measurementModeChanged() {};
    }

	/**
	 * This class represents the probe that moves around and comes in contact
	 * with various datable elements in the model.
	 */
    public static class ObjectProbe extends SimpleObservable {
        private Point2D.Double tipLocation;
        private double angle;
        private double tipWidth = 0.1 * 0.35;
        private double tipHeight = 0.3 * 1.25 * 0.75;

        public ObjectProbe( Point2D tipLocation, double angle ) {
            this.tipLocation = new Point2D.Double( tipLocation.getX(), tipLocation.getY() );
            this.angle = angle;
        }

        public void translate( double dx, double dy ) {
            tipLocation.x += dx;
            tipLocation.y += dy;
            notifyObservers();
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
    }
}
