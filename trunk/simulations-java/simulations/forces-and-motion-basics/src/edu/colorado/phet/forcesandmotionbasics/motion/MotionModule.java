package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.Context;

/**
 * Module for tab 2 "Motion" and tab 3 "Friction".  The model is created and maintained within MotionCanvas to make it simple to recerate on reset.
 * Reset is implemented as throwing away the old model and view, and creating new ones.
 *
 * @author Sam Reid
 */
public class MotionModule extends SimSharingPiccoloModule implements Context {
    private final boolean friction;

    public MotionModule( String title, boolean friction ) {
        super( UserComponents.motionTab, title, new ConstantDtClock() );
        setSimulationPanel( new MotionCanvas( this, getClock(), friction ) );
        setClockControlPanel( null );
        this.friction = friction;
    }

    @Override public void reset() {
        super.reset();
        setSimulationPanel( new MotionCanvas( this, getClock(), friction ) );
    }
}