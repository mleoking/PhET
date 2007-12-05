package edu.colorado.phet.movingman.motion.movingman;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;
import edu.colorado.phet.movingman.ArrowPanel;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:42:37 PM
 */
public class MovingManMotionModule extends Module implements ArrowPanel.IArrowPanelModule {
    private MovingManMotionModel movingManMotionModel;
    private MovingManMotionSimPanel movingManMotionSimPanel;

    public MovingManMotionModule( ConstantDtClock clock ) {
        super( "Moving Man", clock );
        movingManMotionModel = new MovingManMotionModel( clock );

        movingManMotionSimPanel = new MovingManMotionSimPanel( movingManMotionModel );
        setSimulationPanel( movingManMotionSimPanel );
        setClockControlPanel( new MovingManSouthControlPanel( movingManMotionModel.getTimeSeriesModel(), clock.getDt() / 2, clock.getDt() * 2 ) );
        setLogoPanelVisible( false );
    }

    public void activate() {
        super.activate();
        movingManMotionModel.startRecording();
    }

    public void setShowVelocityVector( boolean selected ) {
        movingManMotionSimPanel.setShowVelocityVector( selected );
    }

    public void setShowAccelerationVector( boolean selected ) {
        movingManMotionSimPanel.setShowAccelerationVector( selected );
    }

    private class MovingManSouthControlPanel extends HorizontalLayoutPanel {
        public MovingManSouthControlPanel( TimeSeriesModel timeSeriesModel, double min, double max ) {
            add( new TimeSeriesControlPanel( timeSeriesModel, min, max ) );
            add( new ArrowPanel( MovingManMotionModule.this ) );
            add( new SoundCheckBox() );
        }

        private class SoundCheckBox extends JCheckBox {
            private SoundCheckBox() {
                super( "Sound" );
            }
        }
    }
}
