/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.chart.controllers.ChartSlider;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.Force1DWiggleMe;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 11, 2005
 * Time: 10:18:48 PM
 * Copyright (c) Apr 11, 2005 by Sam Reid
 */

public class SliderWiggleMe extends Force1DWiggleMe implements MovingManModule.Listener {
    private MovingManApparatusPanel movingManApparatusPanel;
    private MovingManModule module;
    private AbstractClock clock;
    private boolean disposed = false;

    public SliderWiggleMe( MovingManApparatusPanel movingManApparatusPanel,
                           MovingManModule module, AbstractClock clock ) {
        super( movingManApparatusPanel, clock, "Drag the Slider", new SliderWiggleMe.FirstSliderTarget( movingManApparatusPanel ) );
        this.movingManApparatusPanel = movingManApparatusPanel;
        this.module = module;
        this.clock = clock;
        setArrow( -30, 5 );
        ChartSlider.Listener listener = new ChartSlider.Listener() {
            public void valueChanged( double value ) {
                dispose();
            }
        };
        movingManApparatusPanel.getPlotSet().getPositionPlot().getChartSlider().addListener( listener );
        movingManApparatusPanel.getPlotSet().getVelocityPlot().getChartSlider().addListener( listener );
        movingManApparatusPanel.getPlotSet().getAccelerationPlot().getChartSlider().addListener( listener );
        module.addListener( new MovingManModule.Listener() {
            public void reset() {
                if( !disposed ) {
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
        if( !disposed ) {
            setVisible( true );
        }
    }

    public void soundOptionChanged( boolean soundEnabled ) {
    }

    protected Point getOscillationCenter( Point targetLoc ) {
        Point oscillationCenter = new Point( targetLoc.x + 5, targetLoc.y );
        return oscillationCenter;
    }

    public static class FirstSliderTarget implements Target {
        private MovingManApparatusPanel movingManApparatusPanel;

        public FirstSliderTarget( MovingManApparatusPanel movingManApparatusPanel ) {
            this.movingManApparatusPanel = movingManApparatusPanel;
        }

        public Point getLocation() {
            Point loc = movingManApparatusPanel.getPlotSet().getPositionPlot().getChartSlider().getBounds().getLocation();
            return loc;
        }

        public int getHeight() {
            return movingManApparatusPanel.getPlotSet().getPositionPlot().getChartSlider().getHeight();
        }
    }
}
