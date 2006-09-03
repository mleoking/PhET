/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.HelpItem2;
import edu.colorado.phet.movingman.model.TimeListenerAdapter;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;

/**
 * User: Sam Reid
 * Date: Apr 18, 2005
 * Time: 9:41:17 PM
 * Copyright (c) Apr 18, 2005 by Sam Reid
 */

public class SliderHelpItem extends HelpItem2 {
    private boolean shown = false;
    private MovingManApparatusPanel movingManApparatusPanel;

    public SliderHelpItem( MovingManApparatusPanel movingManApparatusPanel, PhetGraphic goButtonGraphic, final MMPlotSuite mmPlotSuite ) {
        super( movingManApparatusPanel, "Press Go!" );
        this.movingManApparatusPanel = movingManApparatusPanel;
        setVisible( false );
        pointLeftAt( goButtonGraphic, 30 );

        movingManApparatusPanel.getModule().getMovingManModel().getTimeModel().addListener( new TimeListenerAdapter() {
            public void recordingStarted() {
                hideGoHelp();
            }

            public void playbackStarted() {
                hideGoHelp();
            }

            public void rewind() {
                hideGoHelp();
            }

            public void reset() {
                hideGoHelp();
            }
        } );

        mmPlotSuite.getPlotDevice().addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                if( mmPlotSuite.isPaused() && !isAtEndOfTime() ) {
                    showSliderHelp();
                }
            }
        } );
    }

    private boolean isAtEndOfTime() {
        return getModule().isAtEndOfTime();
    }

    private MovingManModule getModule() {
        return movingManApparatusPanel.getModule();
    }

    public void hideGoHelp() {//todo call this function.
        setVisible( false );
    }

    private void showSliderHelp() {
        if( !shown ) {
            shown = true;
            setVisible( true );
        }
    }
}
