package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Author: Sam Reid
 * Jul 18, 2007, 5:45:18 PM
 */
public class MovingManGraph extends MotionControlGraph {

    public MovingManGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String label, String title, double min, double max, PNode thumb, final MotionModel motionModel, boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, double maxDomainValue, final IUpdateStrategy iPositionDriven ) {
        super( pSwingCanvas, series, label, title, min, max, thumb, motionModel, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
    }

}
