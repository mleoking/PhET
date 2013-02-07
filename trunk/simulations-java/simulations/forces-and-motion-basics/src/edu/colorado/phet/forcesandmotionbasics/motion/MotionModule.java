package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents;

/**
 * Module for tab 2 "Motion" and tab 3 "Friction".  The model is created and maintained within MotionCanvas to make it simple to recreate on reset.
 * Reset is implemented as throwing away the old model and view, and creating new ones.
 *
 * @author Sam Reid
 */
public class MotionModule extends SimSharingPiccoloModule implements Resettable {
    private final boolean friction;
    private final boolean accelerometer;

    public MotionModule( UserComponents component, String title, boolean friction, boolean accelerometer ) {
        super( component, title, new ConstantDtClock( ConstantDtClock.DEFAULT_FRAMES_PER_SECOND ) );
        setSimulationPanel( new MotionCanvas( this, getClock(), friction, accelerometer ) );
        setClockControlPanel( null );
        this.friction = friction;
        this.accelerometer = accelerometer;
    }

    @Override public void reset() {
        super.reset();
        setSimulationPanel( new MotionCanvas( this, getClock(), friction, accelerometer ) );
    }
}