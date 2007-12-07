package edu.colorado.phet.movingman.motion.force1d;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.movingman.motion.AbstractMotionModule;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModule;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:26:27 PM
 */
public class Force1DMotionModule extends AbstractMotionModule {
    public Force1DMotionModule( ConstantDtClock clock ) {
        super( "Force 1D", clock );
        Force1DMotionModel model = new Force1DMotionModel( clock );
        setSimulationPanel( new Force1DMotionSimPanel( model ) );
        setControlPanel( new Force1DMotionControlPanel( model ) );
        setClockControlPanel( new Force1DSouthControlPanel( this, this, model.getTimeSeriesModel(), MovingManMotionModule.MIN_DT, MovingManMotionModule.MAX_DT ) );
        setLogoPanelVisible( false );
    }
}
