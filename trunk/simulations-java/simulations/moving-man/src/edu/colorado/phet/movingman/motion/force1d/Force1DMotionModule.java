package edu.colorado.phet.movingman.motion.force1d;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:26:27 PM
 */
public class Force1DMotionModule extends Module {
    public Force1DMotionModule( ConstantDtClock clock ) {
        super( "Force 1D", clock );
        ForceModel model = new ForceModel( clock );
        setSimulationPanel( new Force1DMotionSimPanel( model ) );
        setLogoPanelVisible( false );
    }
}
