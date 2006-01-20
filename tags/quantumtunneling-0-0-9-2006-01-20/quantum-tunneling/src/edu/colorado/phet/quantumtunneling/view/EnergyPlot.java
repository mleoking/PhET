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
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.WaveType;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
import edu.colorado.phet.quantumtunneling.model.WavePacket;


/**
 * EnergyPlot is the plot that displays total and potential energy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EnergyPlot extends XYPlot implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractPotential _potentialEnergy;
    private TotalEnergy  _totalEnergy;
    private WavePacket _wavePacket;
    
    private XYSeries _totalEnergySeries;
    private XYSeries _potentialEnergySeries;
    
    private TotalEnergyRenderer _tePacketRenderer; // renderer for total energy for wave packet
    private int _tePlaneIndex; // total energy data set index for plane wave
    private int _tePacketIndex; // total energy data set index for wave packet
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EnergyPlot() {
        super();
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" ) + " (" + SimStrings.get( "units.energy" ) + ")";
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        String totalEnergyLabel = SimStrings.get( "legend.totalEnergy" );
        
        int index = 0;
        
        // Potential Energy series
        _potentialEnergySeries = new XYSeries( potentialEnergyLabel, false /* autoSort */ );
        {
            int peIndex = index++;
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _potentialEnergySeries );
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( QTConstants.POTENTIAL_ENERGY_COLOR );
            renderer.setStroke( QTConstants.POTENTIAL_ENERGY_STROKE );
            setDataset( peIndex, dataset );
            setRenderer( peIndex, renderer );
        }
        
        // Total Energy series
        _totalEnergySeries = new XYSeries( totalEnergyLabel, false /* autoSort */);
        {
            // plane wave...
            {
                _tePlaneIndex = index++;
                XYSeriesCollection dataset = new XYSeriesCollection();
                dataset.addSeries( _totalEnergySeries );
                XYItemRenderer renderer = new StandardXYItemRenderer();
                renderer.setPaint( QTConstants.TOTAL_ENERGY_COLOR );
                renderer.setStroke( QTConstants.TOTAL_ENERGY_STROKE );
                setDataset( _tePlaneIndex, dataset );
                setRenderer( _tePlaneIndex, renderer );
            }

            // wave packet...
            {
                _tePacketIndex = index++;
                XYSeriesCollection dataset = new XYSeriesCollection();
                dataset.addSeries( _totalEnergySeries );  // shares the same series!
                _tePacketRenderer = new TotalEnergyRenderer();
                _tePacketRenderer.setPaint( QTConstants.TOTAL_ENERGY_COLOR );
                _tePacketRenderer.setStroke( QTConstants.TOTAL_ENERGY_STROKE );
                setDataset( _tePacketIndex, dataset );
                setRenderer( _tePacketIndex, _tePacketRenderer );
            }
        }
        setWaveType( WaveType.PLANE );
        
        // X axis 
        PositionAxis xAxis = new PositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( energyLabel );
        yAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
        yAxis.setRange( QTConstants.ENERGY_RANGE );
        yAxis.setTickLabelPaint( QTConstants.TICK_LABEL_COLOR );
        yAxis.setTickMarkPaint( QTConstants.TICK_MARK_COLOR );

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        setDomainGridlinesVisible( QTConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( QTConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis ); 
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the wave type, which determines how total energy is displayed.
     * 
     * @param true or false
     */
    public void setWaveType( WaveType waveType ) {
        boolean isPlane = ( waveType == WaveType.PLANE );
        getRenderer( _tePlaneIndex ).setSeriesVisible( new Boolean( isPlane ) );
        getRenderer( _tePacketIndex ).setSeriesVisible( new Boolean( !isPlane ) );
    }
    
    /**
     * Sets the total energy model that is displayed.
     * 
     * @param totalEnergy
     */
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
        }
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        updateTotalEnergy();
    }
    
    /**
     * Sets the potential energy model that is displayed.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _potentialEnergy = potentialEnergy;
        _tePacketRenderer.setPotentialEnergy( potentialEnergy );
        _potentialEnergy.addObserver( this );
        updatePotentialEnergy();
        updateTotalEnergy();
    }
    
    /**
     * Sets the font used for labeling the axes.
     * 
     * @param font
     */
    public void setAxesFont( Font font ) {
        getDomainAxis().setLabelFont( font );
        getRangeAxis().setLabelFont( font );
    }
    
    /**
     * Sets the wave packet used by the total energy renderer.
     * 
     * @param wavePacket
     */
    public void setWavePacket( WavePacket wavePacket ) {
        if ( _wavePacket != null ) {
            _wavePacket.deleteObserver( this );
        }
        _wavePacket = wavePacket;
        _tePacketRenderer.setWavePacket( wavePacket );
        _wavePacket.addObserver( this );
        updateTotalEnergy();
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param observable
     * @param arg
     */
    public void update( Observable observable, Object arg ) {
        if ( observable == _potentialEnergy ) {
            updatePotentialEnergy();
            updateTotalEnergy();
        }
        else if ( observable == _totalEnergy ) {
            updateTotalEnergy();
        }
        else if ( observable == _wavePacket ) {
            updateTotalEnergy();
        }
    }
    
    //----------------------------------------------------------------------------
    // Update handlers
    //----------------------------------------------------------------------------
    
    /*
     * Updates the total energy series to match the model.
     */
    private void updateTotalEnergy() {
        if ( _totalEnergy != null ) {
            Range range = getDomainAxis().getRange();
            _totalEnergySeries.setNotify( false );
            _totalEnergySeries.clear();
            _totalEnergySeries.add( range.getLowerBound(), _totalEnergy.getEnergy() );
            _totalEnergySeries.add( range.getUpperBound(), _totalEnergy.getEnergy() );
            _totalEnergySeries.setNotify( true );
        }
    }
    
    /*
     * Updates the potential energy series to match the model.
     */
    private void updatePotentialEnergy() {
        if ( _potentialEnergy != null ) {
            _potentialEnergySeries.setNotify( false );
            _potentialEnergySeries.clear();
            int numberOfRegions = _potentialEnergy.getNumberOfRegions();
            for ( int i = 0; i < numberOfRegions; i++ ) {
                double start = _potentialEnergy.getStart( i );
                double end = _potentialEnergy.getEnd( i );
                double energy = _potentialEnergy.getEnergy( i );
                _potentialEnergySeries.add( start, energy );
                _potentialEnergySeries.add( end, energy );
            }
            _potentialEnergySeries.setNotify( true );
        }
    }
}
