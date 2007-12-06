package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.movingman.motion.movingman.MovingManGraph;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModel;

/**
 * Created by: Sam
 * Dec 6, 2007 at 12:22:51 AM
 */
public class AbstractMotionSimPanel extends BufferedPhetPCanvas {
    public AbstractMotionSimPanel() {
        setBackground( MotionProjectLookAndFeel.BACKGROUND_COLOR );
    }

    protected MovingManGraph getXGraph( MovingManMotionModel forceModel ) {
        return new MovingManGraph( this, forceModel.getXSeries(), SimStrings.get( "variables.position.abbreviation" ), "x", -11, 11,
                                   forceModel, true, forceModel.getTimeSeriesModel(), forceModel.getPositionDriven(), MovingManMotionModel.MAX_T, forceModel );
    }

    protected MovingManGraph getVGraph( MovingManMotionModel forceModel ) {
        return new MovingManGraph( this, forceModel.getVSeries(), SimStrings.get( "variables.velocity.abbreviation" ), "v", -11, 11,
                                   forceModel, true, forceModel.getTimeSeriesModel(), forceModel.getVelocityDriven(), MovingManMotionModel.MAX_T, forceModel );
    }

    protected MovingManGraph getAGraph( MovingManMotionModel forceModel ) {
        return new MovingManGraph( this, forceModel.getASeries(), SimStrings.get( "variables.position.abbreviation" ), "a", -11, 11,
                                   forceModel, true, forceModel.getTimeSeriesModel(), forceModel.getAccelDriven(), MovingManMotionModel.MAX_T, forceModel );
    }

}
