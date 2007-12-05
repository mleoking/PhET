package edu.colorado.phet.movingman.motion.movingman;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:42:37 PM
 */
public class MovingManMotionModule extends Module {
    private MovingManMotionModel movingManMotionModel;

    public MovingManMotionModule( ConstantDtClock clock ) {
        super( "Moving Man", clock );
        movingManMotionModel = new MovingManMotionModel( clock );

        final MovingManMotionSimPanel simPanel = new MovingManMotionSimPanel( movingManMotionModel );
        setSimulationPanel( simPanel );
        setLogoPanelVisible( false );
    }

    public void activate() {
        super.activate();
        movingManMotionModel.startRecording();
    }
}
