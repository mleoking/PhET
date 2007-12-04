package edu.colorado.phet.movingman.motion.force1d;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:26:27 PM
 */
public class Force1DMotionModule extends Module {
    public Force1DMotionModule() {
        super( "Force 1D", new ConstantDtClock( 30, 1.0 ) );
        setSimulationPanel( new Force1DMotionSimPanel( (ConstantDtClock) getClock() ) );
        setLogoPanelVisible( false );
    }
}
