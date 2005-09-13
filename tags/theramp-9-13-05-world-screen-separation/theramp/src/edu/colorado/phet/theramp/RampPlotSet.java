/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.theramp.common.Range2D;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.colorado.phet.theramp.view.plot.TimePlotSuitePNode;
import edu.colorado.phet.theramp.view.plot.TimeSeriesPNode;
import edu.colorado.phet.timeseries.TimeSeries;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 16, 2005
 * Time: 10:49:44 AM
 * Copyright (c) May 16, 2005 by Sam Reid
 */

public class RampPlotSet extends PNode {
    private RampModule module;
    private RampPanel rampPanel;
    private ArrayList dataUnits = new ArrayList();
    private TimePlotSuitePNode energyPlot;
    private TimePlotSuitePNode workPlot;
    private TimePlotSuitePNode parallelForcePlot;
    private static final double LAYOUT_X = 30;
//    private int chartWidth = TimePlotSuitePNode.DEFAULT_CHART_WIDTH;

    public RampPlotSet( RampModule module, RampPanel rampPanel ) {
        this.module = module;
        this.rampPanel = rampPanel;
        int plotY = 400;
        int plotHeight = 80;
        int plotInset = 2;
        int range = 30000;
        energyPlot = createTimePlotSuitePNode( new Range2D( 0, -range, RampModule.MAX_TIME, range ), "Energy", plotY, plotHeight, false );

        ValueAccessor.TotalEnergy totalEnergy = new ValueAccessor.TotalEnergy( getLookAndFeel() );
        addTimeSeries( energyPlot, totalEnergy, "10000.00" );

        ValueAccessor.ThermalEnergy thermalEnergy = new ValueAccessor.ThermalEnergy( getLookAndFeel() );
        addTimeSeries( energyPlot, thermalEnergy, "10000.00" );

        ValueAccessor.PotentialEnergy potentialEnergy = new ValueAccessor.PotentialEnergy( getLookAndFeel() );
        addTimeSeries( energyPlot, potentialEnergy, "10000.00" );

        ValueAccessor.KineticEnergy kineticEnergy = new ValueAccessor.KineticEnergy( getLookAndFeel() );
        addTimeSeries( energyPlot, kineticEnergy, "10000.00" );

        workPlot = createTimePlotSuitePNode( new Range2D( 0, -range, RampModule.MAX_TIME, range ), "Work", plotY + plotHeight + plotInset, plotHeight, false );

        ValueAccessor.AppliedWork appliedWork = new ValueAccessor.AppliedWork( getLookAndFeel() );
        addTimeSeries( workPlot, appliedWork, "10000.00" );

        ValueAccessor.FrictiveWork frictiveWork = new ValueAccessor.FrictiveWork( getLookAndFeel() );
        addTimeSeries( workPlot, frictiveWork, "10000.00" );

        ValueAccessor.TotalWork totalWork = new ValueAccessor.TotalWork( getLookAndFeel() );
        addTimeSeries( workPlot, totalWork, "10000.00" );

        ValueAccessor.GravityWork gravityWork = new ValueAccessor.GravityWork( getLookAndFeel() );
        addTimeSeries( workPlot, gravityWork, "10000.00" );

        parallelForcePlot = createTimePlotSuitePNode( new Range2D( 0, -1000, RampModule.MAX_TIME, 1000 ), "Parallel Forces", plotY + plotHeight * 2 + plotInset, plotHeight, true );
        ValueAccessor.ParallelForceAccessor parallelFriction = new ValueAccessor.ParallelFrictionAccessor( getLookAndFeel() );
        addTimeSeries( parallelForcePlot, parallelFriction, "10000.00" );
        ValueAccessor.ParallelForceAccessor parallelApplied = new ValueAccessor.ParallelAppliedAccessor( getLookAndFeel() );
        addTimeSeries( parallelForcePlot, parallelApplied, "10000.00" );
        ValueAccessor.ParallelForceAccessor parallelGravity = new ValueAccessor.ParallelGravityAccessor( getLookAndFeel() );
        addTimeSeries( parallelForcePlot, parallelGravity, "10000.00" );
        ValueAccessor.ParallelForceAccessor parallelWall = new ValueAccessor.ParallelWallForceAccessor( getLookAndFeel() );
        addTimeSeries( parallelForcePlot, parallelWall, "10000.00" );

        addChild( energyPlot );
        addChild( workPlot );
        addChild( parallelForcePlot );
        parallelForcePlot.setMinimized( true );

//        getRampPanel().addChild( energyPlot );
//        getRampPanel().addChild( workPlot );
        TimePlotSuitePNode.Listener listener = new TimePlotSuitePNode.Listener() {
            public void minimizeStateChanged() {
                invalidateLayout();
            }
        };
        energyPlot.addListener( listener );
        workPlot.addListener( listener );
        invalidateLayout();
        rampPanel.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {

            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                invalidateLayout();
            }

            public void componentShown( ComponentEvent e ) {
                invalidateLayout();
            }
        } );
    }

    public void minimizeAllPlots() {
        energyPlot.setMinimized( true );
        workPlot.setMinimized( true );
        parallelForcePlot.setMinimized( true );
    }

    public void maximizeForcePlot() {
        parallelForcePlot.setMinimized( false );
    }

    public double getTopY() {
        return parallelForcePlot.getTopY();
    }

    static class VariablePlotItem implements LayoutSet.VariableLayoutItem {
        private TimePlotSuitePNode plot;
        private int width;

        public VariablePlotItem( TimePlotSuitePNode plot, int width ) {
            this.plot = plot;
            this.width = width;
        }

        public void setOffset( double offset ) {
            plot.setOffset( LAYOUT_X, offset );
        }

        public void setSize( double size ) {
            plot.setChartSize( width, (int)size );
        }

        public void setVisible( boolean b ) {
            if( !b ) {
                System.out.println( "Chart too small to be shown, needs error handling." );
            }
//            plot.setVisible( b );
        }
    }

    static class FixedPlotItem extends LayoutSet.FixedLayoutItem {
        private TimePlotSuitePNode plot;


        public FixedPlotItem( TimePlotSuitePNode plot ) {
            super( plot.getButtonHeight() );
            this.plot = plot;
        }

        public void setOffset( double offset ) {
            plot.setOffset( LAYOUT_X, offset );
        }
    }

    public void layoutChildren() {
        super.layoutChildren();
        LayoutSet layoutSet = new LayoutSet();

        int availableWidth = (int)getAvailableWidth();
        if( availableWidth > 0 ) {

            layoutSet.addItem( toPlotLayoutItem( availableWidth, parallelForcePlot ) );
            layoutSet.addItem( toPlotLayoutItem( availableWidth, energyPlot ) );
            layoutSet.addItem( toPlotLayoutItem( availableWidth, workPlot ) );
            layoutSet.layout( getLayoutStartY(), getAvailableHeight() );
        }
        notifyLayedOutChildren();
    }

    ArrayList listeners = new ArrayList();

    private void notifyLayedOutChildren() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.layoutChanged();
        }
    }

    public static interface Listener {

        void layoutChanged();
    }

    static class Adapter implements Listener {

        public void layoutChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private double getAvailableHeight() {
        return rampPanel.getSize().height - getLayoutStartY();
    }

    private double getAvailableWidth() {
        return rampPanel.getChartLayoutMaxX() - LAYOUT_X;
//        return rampPanel.getSize().width / 2;
//        return TimePlotSuitePNode.DEFAULT_CHART_WIDTH;
    }

    //Todo; This was super tricky to figure out.   I'll email the chat list.
    //TODO: Just thought I'd mention it twice.
    private double getLayoutStartY() {
        Point2D point = rampPanel.getRampWorld().getEarthGraphic().getGlobalFullBounds().getOrigin();
        globalToLocal( point );
        localToParent( point );
//        point = new Point2D.Double( point.getX(), point.getY() + 80 );
//        rampPanel.getCamera().getViewTransform().transform( point, point );
        return point.getY() + 80;
    }

    private double getLayoutStartYWithCameras() {
        Point2D point = rampPanel.getRampWorld().getEarthGraphic().getGlobalFullBounds().getOrigin();
        rampPanel.getCamera().getViewTransform().transform( point, point );
        return point.getY() + 80;
    }


    private LayoutSet.LayoutItem toPlotLayoutItem( int width, TimePlotSuitePNode plot ) {
        if( plot.isMinimized() ) {
            return new FixedPlotItem( plot );
        }
        else {
            return new VariablePlotItem( plot, width );
        }
    }

    public void repaintBackground() {
        energyPlot.repaintAll();
        workPlot.repaintAll();
    }

    public int numDataUnits() {
        return dataUnits.size();
    }

    public static class DataUnit {
        private ValueAccessor valueAccessor;
        private TimeSeries timeSeries;
        private TimePlotSuitePNode plotDeviceSeries;
        private TimeSeriesPNode seriesGraphic;

        public DataUnit( ValueAccessor valueAccessor, TimeSeries timeSeries, TimePlotSuitePNode plotDeviceSeries, TimeSeriesPNode timePlotSuite ) {
            this.valueAccessor = valueAccessor;
            this.timeSeries = timeSeries;
            this.plotDeviceSeries = plotDeviceSeries;
            this.seriesGraphic = timePlotSuite;
        }

        public void reset() {
            timeSeries.reset();
            plotDeviceSeries.reset();
            seriesGraphic.reset();
        }

        public void updatePlot( RampPhysicalModel state, double recordTime ) {
            double value = valueAccessor.getValue( state );
            timeSeries.addPoint( value, recordTime );
        }

        public void repaintBackground() {
//            timePlotSuite.getPlotDevice().reset();
        }

        public String getName() {
            return valueAccessor.getName();
        }

        public void setVisible( boolean selected ) {
            seriesGraphic.setVisible( selected );
            plotDeviceSeries.repaintAll();
        }

        public Color getColor() {
            return valueAccessor.getColor();
        }
    }

    private void addTimeSeries( TimePlotSuitePNode energyPlot, ValueAccessor valueAccessor, String justifyString ) {
        TimeSeries series = new TimeSeries();
        TimeSeriesPNode timeSeriesPNode = new TimeSeriesPNode( energyPlot, series, valueAccessor, valueAccessor.getColor(), justifyString );
        energyPlot.addTimeSeries( timeSeriesPNode );

        dataUnits.add( new DataUnit( valueAccessor, series, energyPlot, timeSeriesPNode ) );
    }

    private RampLookAndFeel getLookAndFeel() {
        return getRampPanel().getLookAndFeel();
    }

    private RampPanel getRampPanel() {
        return rampPanel;
    }

    private TimePlotSuitePNode createTimePlotSuitePNode( Range2D range, String name, int y, int height, boolean appliedForceSlider ) {
        TimeSeriesModel timeSeriesModel = module.getTimeSeriesModel();
        TimePlotSuitePNode timePlotSuitePNode = new TimePlotSuitePNode( module, getRampPanel(), range, name, timeSeriesModel, height, appliedForceSlider );
        timePlotSuitePNode.setOffset( 0, y );
        return timePlotSuitePNode;
    }

    public void updatePlots( RampPhysicalModel state, double recordTime ) {
        for( int i = 0; i < dataUnits.size(); i++ ) {
            DataUnit dataUnit = (DataUnit)dataUnits.get( i );
            dataUnit.updatePlot( state, recordTime );
        }
    }

    public void reset() {
        for( int i = 0; i < dataUnits.size(); i++ ) {
            dataUnitAt( i ).reset();
        }
    }

    public DataUnit dataUnitAt( int i ) {
        return (DataUnit)dataUnits.get( i );
    }
}
