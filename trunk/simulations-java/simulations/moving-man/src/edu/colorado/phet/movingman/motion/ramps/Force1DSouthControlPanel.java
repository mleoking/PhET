package edu.colorado.phet.movingman.motion.ramps;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;
import edu.colorado.phet.movingman.motion.ISoundObject;
import edu.colorado.phet.movingman.motion.SoundCheckBox;

/**
 * Created by: Sam
 * Dec 6, 2007 at 7:02:46 AM
 */
public class Force1DSouthControlPanel extends HorizontalLayoutPanel {
    private Force1DMotionModule movingManMotionModule;

    public Force1DSouthControlPanel( Force1DMotionModule movingManMotionModule, ISoundObject seriesModel, TimeSeriesModel timeSeriesModel, double min, double max ) {
        this.movingManMotionModule = movingManMotionModule;
        add( new TimeSeriesControlPanel( timeSeriesModel, min, max ) );
        add( new SoundCheckBox( seriesModel ) );
    }

}
