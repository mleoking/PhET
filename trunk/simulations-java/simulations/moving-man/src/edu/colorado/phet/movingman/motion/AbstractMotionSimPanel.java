package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
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
        return getGraph( forceModel, forceModel.getXSeries(), "variables.position.abbreviation", "x", forceModel.getPositionDriven() );
    }

    protected MovingManGraph getVGraph( MovingManMotionModel forceModel ) {
        return getGraph( forceModel, forceModel.getVSeries(), "variables.velocity.abbreviation", "v", forceModel.getVelocityDriven() );
    }

    protected MovingManGraph getAGraph( MovingManMotionModel forceModel ) {
        return getGraph( forceModel, forceModel.getASeries(), "variables.position.abbreviation", "a", forceModel.getAccelDriven() );
    }

    private MovingManGraph getGraph( MovingManMotionModel forceModel, ControlGraphSeries series, String name, String shortCutName, UpdateStrategy strategy ) {
        return new MovingManGraph( this, series, SimStrings.get( name ), shortCutName, -11, 11,
                                   forceModel, true, forceModel.getTimeSeriesModel(), strategy, MovingManMotionModel.MAX_T, forceModel );
    }

}
