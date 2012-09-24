package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.Context;

/**
 * @author Sam Reid
 */
public class MotionModule extends SimSharingPiccoloModule implements Context {
    public MotionModule() {
        super( UserComponents.motionTab, "Motion", new ConstantDtClock() );
        setSimulationPanel( new MotionCanvas( this, getClock() ) );
        setClockControlPanel( null );
    }

    @Override public void reset() {
        super.reset();
        setSimulationPanel( new MotionCanvas( this, getClock() ) );
    }
}