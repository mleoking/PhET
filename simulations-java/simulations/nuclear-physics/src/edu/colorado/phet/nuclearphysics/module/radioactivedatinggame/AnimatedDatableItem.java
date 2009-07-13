package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.common.Cleanupable;

/**
 * This class extends the datable item class to add animation and other time-
 * driven behaviors.
 */
public abstract class AnimatedDatableItem extends DatableItem implements Cleanupable{

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
    private final ConstantDtClock _clock;
    private final double _timeConversionFactor;
    private final ClockAdapter _clockAdapter;
    private final double _birthTime;
    private final ArrayList<ClosureListener> _closureListeners = new ArrayList<ClosureListener>();
    private double _age = 0; // Age in milliseconds of this datable item.
    private double _ageOffset = 0;
    private RadiometricClosureState _closureState = RadiometricClosureState.CLOSURE_NOT_POSSIBLE;
    private double _closureAge = 0;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public AnimatedDatableItem( String name, List<String> resourceImageNames, Point2D center, double width, 
    		double rotationAngle, double age, ConstantDtClock clock, double ageAdjustmentFactor ) {
        super( name, resourceImageNames, center, width, rotationAngle, age );
        _clock = clock;
        _birthTime = _clock.getSimulationTime() * ageAdjustmentFactor;
        this._timeConversionFactor = ageAdjustmentFactor;
        // Create the adapter that will listen to the clock.
		_clockAdapter = new ClockAdapter(){
		    public void clockTicked( ClockEvent clockEvent ) {
		    	handleClockTicked();
		    }
		    public void simulationTimeReset( ClockEvent clockEvent ) {
		    	handleSimulationTimeReset();
		    }
		};
		_clock.addClockListener(_clockAdapter);
    }

	//------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Handle an animation event that results from the interpretation of the
     * animation sequence.  These events are generally used to synchronize the
     * animation sequence with other aspects of the model behavior.
     * 
     * @param event
     */
    protected void handleAnimationEvent(EventObject event){
    	if (event instanceof RadiometricClosureEvent){
    		setClosureState(((RadiometricClosureEvent) event).getClosureState()); 
    	}
    }

    /**
     * Force radiometric closure to occur.
     */
    public void forceClosure(){
    	if (_closureAge > _age){
    		_ageOffset = _closureAge - _age;
    	}
    	else{
    		System.err.println(getClass().getName() + 
    				" - Warning: Attempt to force closure after it should have already occurred.");
    	}
    }
    
    public RadiometricClosureState getClosureState(){
    	return _closureState;
    }
    
    protected void setClosureAge(double closureAge){
    	this._closureAge = closureAge;
    }
    
    protected void setClosureState(RadiometricClosureState newClosureState){
    	if (_closureState != newClosureState){
    		// NOTE: There is currently no validity checking done here.  It
    		// may be necessary to do so at some point.
    		_closureState = newClosureState;
    		notifyClosureStateChanged();
    	}
    }
    
	public void addClosureListener(ClosureListener listener) {
		if (!_closureListeners.contains(listener)){
			_closureListeners.add(listener);
		}
	}
	
	public void removeClosureListener(ClosureListener listener) {
		_closureListeners.remove(listener);
	}

	public void removeAllClosureListeners() {
		_closureListeners.clear();
	}
	
	/**
	 * Get the value that is being used to convert simulation time into the
	 * time values needed by the aging time.  This is often a very large
	 * number.
	 *
	 * @return
	 */
	public double getTimeConversionFactor(){
		return _timeConversionFactor;
	}

	private void notifyClosureStateChanged(){
		for (ClosureListener listener : _closureListeners){
			listener.closureStateChanged(this);
		}
	}

	
    protected void handleClockTicked(){
    	// Update our age value.
        _age = _clock.getSimulationTime() * _timeConversionFactor + _ageOffset - _birthTime;
    }
    
    protected ConstantDtClock getClock(){
    	return _clock;
    }
    
    protected double getBirthTime(){
    	return _birthTime;
    }

    public void cleanup() {
		_clock.removeClockListener(_clockAdapter);
	}

    protected void handleSimulationTimeReset(){
        _age = 0;
    }

    @Override
	public double getAge() {
    	if (_closureState != RadiometricClosureState.CLOSED){
    		// Radiometric aging does not begin until closure occurs.
    		return 0;
    	}
    	else {
    		// Calculate the time since closure occurred, since
    		// radiometrically speaking, that is our age.
    		assert _age > _closureAge;
    		return _age - _closureAge;
    	}
    }

    public static class TimeUpdater {
        private double time;
        private double dt;

        public TimeUpdater( double startTimeMS, double dtMS ) {
            this.time = startTimeMS;
            this.dt = dtMS;
        }

        double updateTime() {
            time = time + dt;
            return time;
        }
    }
    
    /**
     * Event used to convey information about changes to the closure state.
     */
    public static class RadiometricClosureEvent extends EventObject{

    	private final RadiometricClosureState closureState;
    	
		public RadiometricClosureEvent(Object source, RadiometricClosureState closureState) {
			super(source);
			this.closureState = closureState;
		}

		public RadiometricClosureState getClosureState() {
			return closureState;
		}
    }

    /**
     * Listener through which information about the radiometric closure state
     * can be monitored.
     */
    public interface ClosureListener{
    	public void closureStateChanged(AnimatedDatableItem datableItem);
    }
}
