package edu.colorado.phet.movingman.motion.movingman;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;
import edu.colorado.phet.movingman.ArrowPanel;
import edu.colorado.phet.movingman.motion.SoundCheckBox;

/**
 * Created by: Sam
 * Dec 6, 2007 at 7:02:46 AM
 */
class MovingManSouthControlPanel extends HorizontalLayoutPanel {
    private MovingManMotionModule movingManMotionModule;

    public MovingManSouthControlPanel( MovingManMotionModule movingManMotionModule, MovingManMotionModule seriesModel, TimeSeriesModel timeSeriesModel, double min, double max ) {
        this.movingManMotionModule = movingManMotionModule;
        TimeSeriesControlPanel panel = new TimeSeriesControlPanel( timeSeriesModel, min, max );
        panel.setSpeedControlVisible(false);
        add( panel );
        add( new ArrowPanel( movingManMotionModule ) );
        add( new SoundCheckBox( seriesModel ) );
    }

}
