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
import java.awt.geom.Point2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.LinePlot;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.util.TrigCache;


/**
 * GaussianWavePacketPlot is used by Chart to draw a Gaussian wave packet.
 * The number of data points used to draw the wave is optimized
 * and adjusted based on the range and size of the Chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GaussianWavePacketPlot extends LinePlot {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    private static final Color DEFAULT_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _dx;
    private double _k0;
    private int _waveType;
    private double _pixelsPerPoint;
    private Point2D[] _points;
    private double _maxAmplitude;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component
     * @param chart
     */
    public GaussianWavePacketPlot( Component component, D2CSumChart chart ) {
        super( component, chart );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _dx = 1;
        _k0 = 0;
        _waveType = FourierConstants.WAVE_TYPE_SINE;
        _pixelsPerPoint = 1.0;
        _points = null;
        _maxAmplitude = 0;

        setDataSet( new DataSet() );
        setBorderColor( DEFAULT_COLOR );
        setStroke( DEFAULT_STROKE );

        updateDataSet();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
 
    /**
     * Sets delta X, the wave packet width in x-space.
     * 
     * @param dx
     */
    public void setDeltaX( double dx ) {
        assert ( dx > 0 );
        if ( dx != _dx ) {
            _dx = dx;
            updateDataSet();
        }
    }

    /**
     * Gets delta X.
     * 
     * @return
     */
    public double getDeltaX() {
        return _dx;
    }
    
    /**
     * Sets the center point of the wave packet in k space.
     * 
     * @param k0
     */
    public void setK0( double k0 ) {
        if ( k0 != _k0 ) {
            _k0 = k0;
            updateDataSet();
        }
    }
    
    /**
     * Gets the center point of the wave packet in k space.
     * @return
     */
    public double getK0() {
        return _k0;
    }
    
    /**
     * Sets the wave type.
     * 
     * @param waveType WAVE_TYPE_SINE or WAVE_TYPE_COSINE
     */
    public void setWaveType( int waveType ) 
    {
        assert( FourierConstants.isValidWaveType( waveType ) );
        if ( waveType != _waveType ) {
            _waveType = waveType;
            updateDataSet();
        }
    }
    
    /**
     * Gets the wave type.
     * 
     * @return WAVE_TYPE_SINE or WAVE_TYPE_COSINE
     */
    public int getWaveType() {
        return _waveType;
    }

    /**
     * Sets the number of pixels per data point.
     *
     * @param pixelsPerPoint
     */
    public void setPixelsPerPoint( double pixelsPerPoint ) {
        assert( pixelsPerPoint >= 1 );
        if( pixelsPerPoint != _pixelsPerPoint ) {
            _pixelsPerPoint = pixelsPerPoint;
            updateDataSet();
        }
    }

    /**
     * Gets the number of pixels per data point.
     *
     * @return the number of pixels per data point
     */
    public double getPixelsPerPoint() {
        return _pixelsPerPoint;
    }
    
    /**
     * Gets absolute value of the maximum amplitude of the wave packet.
     * 
     * @return
     */
    public double getMaxAmplitude() {
        return _maxAmplitude;
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
     * and the chart range.
     */
    protected void updateDataSet() {

        if ( isVisible() ) {

            Chart chart = getChart();
            Range2D range = chart.getRange();
            DataSet dataSet = getDataSet();

            // Clear the data set.
            dataSet.clear();
            
            _maxAmplitude = 0;

            // Number of pixels between the min and max X range.
            final double minPixel = modelToViewX( range.getMinX() );
            final double maxPixel = modelToViewX( range.getMaxX() );
            final double numberOfPixels = maxPixel - minPixel + 1;
            final int numberOfPoints = (int) ( numberOfPixels / _pixelsPerPoint );

            // Change in X per pixel.
            final double extent = Math.abs( range.getMaxX() - range.getMinX() );
            final double deltaX = extent / numberOfPoints;

            // Reuse the points if the count hasn't changed.
            if ( _points == null || numberOfPoints + 1 != _points.length ) {
                _points = new Point2D.Double[numberOfPoints + 1];
            }

            // Create points.  
            for ( int i = 0; i < _points.length; i++ ) {
                
                // x coordinate
                final double x = range.getMinX() + ( i * deltaX );
                
                /* 
                 * y coordinate:
                 * y = F(x) = exp( -(x^2) / (2 * (deltax^2)) ) * sin(k0*x)
                 */
                double y = 0;
                if ( _waveType == FourierConstants.WAVE_TYPE_SINE ) {
                    y = Math.exp( -( x * x ) / ( 2 * ( _dx * _dx ) ) ) * TrigCache.sin( _k0 * x );
                }
                else {
                    y = Math.exp( -( x * x ) / ( 2 * ( _dx * _dx ) ) ) * TrigCache.cos( _k0 * x );
                }
                
                // point
                if ( _points[i] == null ) {
                    _points[i] = new Point2D.Double( x, y );
                }
                else {
                    _points[i].setLocation( x, y );
                }
                
                // maximum amplitude
                if ( Math.abs( y ) > _maxAmplitude ) {
                    _maxAmplitude = Math.abs( y );
                }
            }

            // Update the data set.
            dataSet.addAllPoints( _points );
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
        return getChart().transformXDouble( x );
    }
}
