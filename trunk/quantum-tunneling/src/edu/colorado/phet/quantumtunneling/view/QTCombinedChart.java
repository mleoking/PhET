/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.BasicStroke;
import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;



/**
 * QTCombinedChart
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTCombinedChart extends JFreeChart {

    private static final boolean CREATE_LEGEND = true;
    private static final Font AXIS_LABEL_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 16 );
    private static final double CHART_SPACING = 15.0;

    private XYSeriesCollection _energyData, _waveFunctionData, _probabilityDensityData;
    private XYSeries _totalEnergySeries, _potentialEnergySeries;
    private XYSeries _probabilityDensitySeries;
    
    public QTCombinedChart() {
        super( null, null, new CombinedDomainXYPlot(), CREATE_LEGEND );
        
        setBackgroundPaint( QTConstants.CHART_BACKGROUND );
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" );
        String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        String probabilityDensityLabel = SimStrings.get( "axis.probabilityDensity" );
        String positionLabel = SimStrings.get( "axis.position" );
        String totalEnergyLabel = SimStrings.get( "legend.totalEnergy" );
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        
        // Data series
        _totalEnergySeries = new XYSeries( totalEnergyLabel );
        _potentialEnergySeries = new XYSeries( potentialEnergyLabel );
        _probabilityDensitySeries = new XYSeries( probabilityDensityLabel );
        
        // Energy plot...
        _energyData = new XYSeriesCollection();
        XYItemRenderer energyRenderer = new StandardXYItemRenderer();
        _energyData.addSeries( _totalEnergySeries );
        _energyData.addSeries( _potentialEnergySeries );
        energyRenderer.setSeriesPaint( 0, QTConstants.TOTAL_ENERGY_PAINT );
        energyRenderer.setSeriesStroke( 0, QTConstants.TOTAL_ENERGY_STROKE );
        energyRenderer.setSeriesPaint( 1, QTConstants.POTENTIAL_ENERGY_PAINT );
        energyRenderer.setSeriesStroke( 1, QTConstants.POTENTIAL_ENERGY_STROKE );
        NumberAxis energyAxis = new NumberAxis( energyLabel );
        energyAxis.setLabelFont( AXIS_LABEL_FONT );
        energyAxis.setRange( QTConstants.ENERGY_RANGE );
        XYPlot energyPlot = new XYPlot( _energyData, null, energyAxis, energyRenderer );
        energyPlot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        energyPlot.setBackgroundPaint( QTConstants.PLOT_BACKGROUND );

        // Wave Function plot...
        _waveFunctionData = new XYSeriesCollection();
        XYItemRenderer waveFunctionRenderer = new StandardXYItemRenderer();
        NumberAxis waveFunctionAxis = new NumberAxis( waveFunctionLabel );
        waveFunctionAxis.setLabelFont( AXIS_LABEL_FONT );
        waveFunctionAxis.setRange( QTConstants.WAVE_FUNCTION_RANGE );
        XYPlot waveFunctionPlot = new XYPlot( _waveFunctionData, null, waveFunctionAxis, waveFunctionRenderer );
        waveFunctionPlot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        waveFunctionPlot.setBackgroundPaint( QTConstants.PLOT_BACKGROUND );

        // Probability Density plot...
        _probabilityDensityData = new XYSeriesCollection();
        XYItemRenderer probabilityDensityRenderer = new StandardXYItemRenderer();
        probabilityDensityRenderer.setSeriesPaint( 0, QTConstants.PROBABILITY_DENSITY_PAINT );
        probabilityDensityRenderer.setSeriesStroke( 0, QTConstants.PROBABILITY_DENSITY_STROKE );
        NumberAxis probabilityDensityAxis = new NumberAxis( probabilityDensityLabel );
        probabilityDensityAxis.setLabelFont( AXIS_LABEL_FONT );
        probabilityDensityAxis.setRange( QTConstants.PROBABILITY_DENSITY_RANGE );
        XYPlot probabilityDensityPlot = new XYPlot( _probabilityDensityData, null, probabilityDensityAxis, probabilityDensityRenderer );
        probabilityDensityPlot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        probabilityDensityPlot.setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        
        // Parent plot...
        CombinedDomainXYPlot plot = (CombinedDomainXYPlot) getPlot();
        NumberAxis positionAxis = new NumberAxis( positionLabel );
        positionAxis.setLabelFont( AXIS_LABEL_FONT );
        positionAxis.setRange( QTConstants.POSITION_RANGE );
        plot.setDomainAxis( positionAxis );
        plot.setGap( CHART_SPACING );
        plot.setOrientation( PlotOrientation.VERTICAL );
        
        // Add the subplots...
        plot.add(energyPlot, 1);
        plot.add(waveFunctionPlot, 1);
        plot.add( probabilityDensityPlot, 1 );   
    }
    
    public XYSeriesCollection getEnergyData() {
        return _energyData;
    }
    
    public XYSeries getTotalEnergySeries() {
        return _totalEnergySeries;
    }
    
    public XYSeries getPotentialEnergySeries() {
        return _potentialEnergySeries;
    }
    
    public XYSeriesCollection getWaveFunctionData() {
        return _waveFunctionData;
    }
    
    public XYSeriesCollection getProbabilityDensityData() {
        return _probabilityDensityData;
    }
}
