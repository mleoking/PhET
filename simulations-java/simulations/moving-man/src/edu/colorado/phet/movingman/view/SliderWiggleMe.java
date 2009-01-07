/*  */
package edu.colorado.phet.movingman.view;

import java.awt.*;
import java.awt.event.KeyEvent;

import edu.colorado.phet.chart_movingman.controllers.ChartSlider;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_movingman.model.clock.AbstractClock;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.Force1DWiggleMe;

/**
 * User: Sam Reid
 * Date: Apr 11, 2005
 * Time: 10:18:48 PM
 */

public class SliderWiggleMe extends Force1DWiggleMe implements MovingManModule.Listener {
    private boolean disposed = false;

    public SliderWiggleMe( MovingManApparatusPanel movingManApparatusPanel,
                           MovingManModule module, AbstractClock clock ) {
        super( movingManApparatusPanel, clock, SimStrings.get( "help.drag-the-slider" ), new SliderWiggleMe.FirstSliderTarget( movingManApparatusPanel ) );
        setArrow( -30, 5 );
        ChartSlider.Listener listener = new ChartSlider.Listener() {
            public void valueChanged( double value ) {
                dispose();
            }
        };
        java.awt.event.KeyAdapter keyListener = new java.awt.event.KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                dispose();
            }
        };
        movingManApparatusPanel.getPlotSet().getPositionPlotSuite().getTextBox().addKeyListener( keyListener );
        movingManApparatusPanel.getPlotSet().getVelocityPlotSuite().getTextBox().addKeyListener( keyListener );
        movingManApparatusPanel.getPlotSet().getAccelerationPlotSuite().getTextBox().addKeyListener( keyListener );
        movingManApparatusPanel.getPlotSet().getPositionPlot().getChartSlider().addListener( listener );
        movingManApparatusPanel.getPlotSet().getVelocityPlot().getChartSlider().addListener( listener );
        movingManApparatusPanel.getPlotSet().getAccelerationPlot().getChartSlider().addListener( listener );
        module.addListener( new MovingManModule.Listener() {
            public void reset() {
                if ( !disposed ) {
                    setVisible( true );
                }
            }

            public void soundOptionChanged( boolean soundEnabled ) {
            }
        } );
        setVisible( false );
    }

    private void dispose() {
        setVisible( false );
        disposed = true;
    }

    public void reset() {
        if ( !disposed ) {
            setVisible( true );
        }
    }

    public void soundOptionChanged( boolean soundEnabled ) {
    }

    protected Point getOscillationCenter( Point targetLoc ) {
        return new Point( targetLoc.x + 5, targetLoc.y );
    }

    public static class FirstSliderTarget implements Target {
        private MovingManApparatusPanel movingManApparatusPanel;

        public FirstSliderTarget( MovingManApparatusPanel movingManApparatusPanel ) {
            this.movingManApparatusPanel = movingManApparatusPanel;
        }

        public Point getLocation() {
            return movingManApparatusPanel.getPlotSet().getPositionPlot().getChartSlider().getBounds().getLocation();
        }

        public int getHeight() {
            return movingManApparatusPanel.getPlotSet().getPositionPlot().getChartSlider().getHeight();
        }
    }
}
