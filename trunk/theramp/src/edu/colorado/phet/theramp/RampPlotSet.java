/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.colorado.phet.timeseries.TimeSeries;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
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
    private int plotY;

    public RampPlotSet( RampModule module, RampPanel rampPanel ) {
        this.module = module;
        this.rampPanel = rampPanel;

        plotY = 440;
        int plotHeight = 210;
        int plotInset = 2;
        int range = 30000;
        energyPlot = createTimePlotSuitePNode( new Range2D( 0, -range, 20, range ), "Energy", plotY, plotHeight );

        ValueAccessor.TotalEnergy totalEnergy = new ValueAccessor.TotalEnergy( getLookAndFeel() );
        addTimeSeries( energyPlot, totalEnergy, totalEnergy.getColor(), "10000.00" );

        ValueAccessor.ThermalEnergy thermalEnergy = new ValueAccessor.ThermalEnergy( getLookAndFeel() );
        addTimeSeries( energyPlot, thermalEnergy, thermalEnergy.getColor(), "10000.00" );

        ValueAccessor.PotentialEnergy potentialEnergy = new ValueAccessor.PotentialEnergy( getLookAndFeel() );
        addTimeSeries( energyPlot, potentialEnergy, potentialEnergy.getColor(), "10000.00" );

        ValueAccessor.KineticEnergy kineticEnergy = new ValueAccessor.KineticEnergy( getLookAndFeel() );
        addTimeSeries( energyPlot, kineticEnergy, kineticEnergy.getColor(), "10000.00" );

        workPlot = createTimePlotSuitePNode( new Range2D( 0, -range, 20, range ), "Work", plotY + plotHeight + plotInset, plotHeight );

        ValueAccessor.AppliedWork appliedWork = new ValueAccessor.AppliedWork( getLookAndFeel() );
        addTimeSeries( workPlot, appliedWork, appliedWork.getColor(), "10000.00" );

        ValueAccessor.FrictiveWork frictiveWork = new ValueAccessor.FrictiveWork( getLookAndFeel() );
        addTimeSeries( workPlot, frictiveWork, frictiveWork.getColor(), "10000.00" );

        ValueAccessor.TotalWork totalWork = new ValueAccessor.TotalWork( getLookAndFeel() );
        addTimeSeries( workPlot, totalWork, totalWork.getColor(), "10000.00" );

        ValueAccessor.GravityWork gravityWork = new ValueAccessor.GravityWork( getLookAndFeel() );
        addTimeSeries( workPlot, gravityWork, gravityWork.getColor(), "10000.00" );

        addChild( energyPlot );
        addChild( workPlot );
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
    }

    static class VariablePlotItem implements LayoutSet.VariableLayoutItem {
        private TimePlotSuitePNode plot;

        public VariablePlotItem( TimePlotSuitePNode plot ) {
            this.plot = plot;
        }

        public void setOffset( double offset ) {
            plot.setOffset( 0, offset );
        }

        public void setSize( double size ) {
            plot.setChartSize( TimePlotSuitePNode.DEFAULT_CHART_WIDTH, (int)size );
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
            plot.setOffset( 0, offset );
        }
    }

    protected void layoutChildren() {
        int availableHeight = 420;

        super.layoutChildren();
        LayoutSet layoutSet = new LayoutSet();
        layoutSet.addItem( toPlotLayoutItem( energyPlot ) );
        layoutSet.addItem( toPlotLayoutItem( workPlot ) );
        layoutSet.layout( plotY, availableHeight );
//        LayoutUtil layoutUtil = new LayoutUtil();
//        layoutUtil.layout( new LayoutUtil.LayoutItem[]{} );
//        energyPlot.setOffset( 0, plotY );
//        workPlot.setOffset( 0, energyPlot.getOffset().getY() + energyPlot.getVisibleHeight() );

//        if( energyPlot.isMinimized() && workPlot.isMinimized() ) {
//            energyPlot.setOffset( 0, plotY );
//            workPlot.setOffset( 0, energyPlot.getFullBounds().getY() + energyPlot.getButtonHeight() );
//        }
    }

    private LayoutSet.LayoutItem toPlotLayoutItem( TimePlotSuitePNode plot ) {
        if( plot.isMinimized() ) {
            return new FixedPlotItem( plot );
        }
        else {
            return new VariablePlotItem( plot );
        }
    }

    public void repaintBackground() {
        energyPlot.repaintAll();
        workPlot.repaintAll();
//        for( int i = 0; i < dataUnits.size(); i++ ) {
//            DataUnit dataUnit = (DataUnit)dataUnits.get( i );
//            dataUnit.repaintBackground();
//        }
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

    private void addTimeSeries( TimePlotSuitePNode energyPlot, ValueAccessor valueAccessor, Color color, String justifyString ) {
        TimeSeries series = new TimeSeries();
        TimeSeriesPNode timeSeriesPNode = new TimeSeriesPNode( energyPlot, series, valueAccessor, color, justifyString );
        energyPlot.addTimeSeries( timeSeriesPNode );

        dataUnits.add( new DataUnit( valueAccessor, series, energyPlot, timeSeriesPNode ) );
    }

    private RampLookAndFeel getLookAndFeel() {
        return getRampPanel().getLookAndFeel();
    }

    private RampPanel getRampPanel() {
        return rampPanel;
    }

    private TimePlotSuitePNode createTimePlotSuitePNode( Range2D range, String name, int y, int height ) {
        TimeSeriesModel timeSeriesModel = module.getTimeSeriesModel();
        TimePlotSuitePNode timePlotSuitePNode = new TimePlotSuitePNode( module, getRampPanel(), range, name, timeSeriesModel, height );
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
