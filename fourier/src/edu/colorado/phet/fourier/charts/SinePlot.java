/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.charts;

import java.awt.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.LinePlot;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;


/**
 * SinePlot is used by Chart to draw a sine or cosine wave.
 * The number of data points used to draw the wave is optimized
 * and adjusted based on the range and size of the Chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SinePlot extends LinePlot {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    private static final Color DEFAULT_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _period;
    private double _amplitude;
    private double _startX;
    private boolean _cosineEnabled;
    private boolean _showZeroAmplitude;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param chart
     */
    public SinePlot( Component component, final Chart chart ) {
        super( component, chart );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _period = 1;
        _amplitude = 0;
        _startX = 0;
        _cosineEnabled = false;
        _showZeroAmplitude = true;

        setDataSet( new DataSet() );
        
        updateDataSet();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the length of one period of the wave, in model coordinates.
     * 
     * @param period
     */
    public void setPeriod( double period ) {
        assert( period >= 0 );
        if ( period < 0 ) {
            throw new IllegalArgumentException( "period must be >= 0" );
        }
        if ( period != _period ) {
            _period = period;
            updateDataSet();
        }
    }

    /**
     * Gets the length of one period of the wave, in model coordinates.
     * 
     * @return the period
     */
    public double getPeriod() {
        return _period;
    }
    
    /**
     * Sets the amplitude of the wave, in model coordinates.
     * 
     * @param amplitude
     */
    public void setAmplitude( double amplitude ) {
        if ( amplitude != _amplitude ) {
            _amplitude = amplitude;
            updateDataSet();
        }
    }
    
    /**
     * Gets the amplitude of the wave, in model coordinates.
     * 
     * @return the amplitude
     */
    public double getAmplitude() {
        return _amplitude;
    }
    
    /**
     * Sets the starting point of some cycle of the wave.
     *
     * @param startX
     */
    public void setStartX( double startX ) {
        if ( startX != _startX ) {
            _startX = startX;
            updateDataSet();
        }
    }
    
    /**
     * Gets the starting point of some cycle of the wave.
     * 
     * @return the starting point
     */
    public double getStartX() {
        return _startX;
    }

    /**
     * Enables or disabled cosine mode.
     * When cosine mode is enabled, the wave is drawn as a cosine.
     * 
     * @param enabled true or false
     */
    public void setCosineEnabled( boolean enabled ) {
        if ( enabled != _cosineEnabled  ) {
            _cosineEnabled = enabled;
            updateDataSet();
        }
    }
    
    /**
     * Determines whether cosine mode is enabled.
     * 
     * @return true or false
     */
    public boolean isCosineEnabled() {
        return _cosineEnabled;
    }
    
    /**
     * Determines whether to draw waves that have zero amplitude.
     * 
     * @param enabled true or false
     */
    public void setShowZeroAmplitudeEnabled( boolean enabled ) {
        if ( enabled != _showZeroAmplitude ) {
            _showZeroAmplitude = enabled;
            updateDataSet();
        }
    }
    
    /**
     * Determines whether waves with zero amplitude will be drawn.
     * 
     * @return true or false
     */
    public boolean isShowZeroAmplitudeEnabled() {
        return _showZeroAmplitude;
    }
    
    //----------------------------------------------------------------------------
    // LinePlot overrides
    //----------------------------------------------------------------------------
    
    /**
     * Updates the data set and redraws whenever the chart's range changes.
     */
    public void transformChanged() {
        updateDataSet(); // update the data set
        super.transformChanged(); // let LinePlot handle the rendering
    }
    
    //----------------------------------------------------------------------------
    // Update
    //----------------------------------------------------------------------------
    
    /**
     * Updates the data set to match the current property setting
     * and the chart range.  One point is plotted for each X-axis
     * pixel the is displayed.
     */
    protected void updateDataSet() {
        
        if ( isVisible() ) {
     
            Chart chart = getChart();
            Range2D range = chart.getRange();
            DataSet dataSet = getDataSet();
            
            // Clear the data set.
            dataSet.clear();
            
            if ( _amplitude == 0 ) {
                if ( _showZeroAmplitude ) {
                    // Optimized data set for zero-amplitude wave.
                    Point2D[] points = new Point2D[ 2 ];
                    points[0] = new Point2D.Double( range.getMinX(), 0 );
                    points[1] = new Point2D.Double( range.getMaxX(), 0 );
                    dataSet.addAllPoints( points );
                }
                else {
                    // Don't draw zero-amplitude wave.
                    return;
                }
            }
            else {
                // Number of pixels between the min and max X range.
                double minPixel = modelToViewX( range.getMinX() );
                double maxPixel = modelToViewX( range.getMaxX() );
                int numberOfPixels = (int) ( maxPixel - minPixel + 1 );

                // Change in X per pixel.
                double extent = Math.abs( range.getMaxX() - range.getMinX() );
                double deltaX = extent / numberOfPixels;

                // Change in angle per pixel.
                double cycles = extent / _period;
                double deltaAngle = cycles * ( 2 * Math.PI ) / numberOfPixels;

                // Pixels between the beginning of the cycle and the range min.
                double startPixel = modelToViewX( _startX );

                // Starting angle at the range min.
                double startAngle = Math.abs( startPixel - minPixel ) * deltaAngle;
                if ( startPixel > minPixel ) {
                    startAngle = -startAngle;
                }

                // Create a point for each pixel.
                Point2D[] points = new Point2D[numberOfPixels];
                for ( int i = 0; i < numberOfPixels; i++ ) {
                    double x = range.getMinX() + ( i * deltaX );
                    double angle = startAngle + ( i * deltaAngle );
                    double y = 0;
                    if ( _cosineEnabled ) {
                        y = _amplitude * Math.cos( angle );
                    }
                    else {
                        y = _amplitude * Math.sin( angle );
                    }
                    points[i] = new Point2D.Double( x, y );
                }

                // Update the data set.
                dataSet.addAllPoints( points );
            }
        }
    }
    
    /*
     * Encapsulates the algorithm used to convert from
     * model coordinates to view coordinates.
     * 
     * @param x the x model coordinate to be converted
     * @return the corresponding view x coordinate
     */
    private double modelToViewX( double x ) {
        return getChart().transform( x, 0 ).getX();
    }
}
