/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.charts;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.LinePlot;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.shaper.util.TrigCache;


/**
 * InputPulsePlot draws an input pulse waveform.
 * <p>
 * The formula that describes the input pulse is:
 * <br>
 * F(x) = exp[ -( ( 10 * pi * x )^2 ) / 2 ]
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class InputPulsePlot extends LinePlot {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    private static final Color DEFAULT_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

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
    public InputPulsePlot( Component component, Chart chart ) {
        super( component, chart );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

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
     * Sets the number of pixels per data point.
     *
     * @param pixelsPerPoint
     */
    public void setPixelsPerPoint( double pixelsPerPoint ) {
        assert( pixelsPerPoint > 0 );
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
     * Gets the absolute value of the maximum amplitude of the wave packet.
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
     * Updates the data set to match the current property settings
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
                 * y =  F(x) = Exp[ -( (10 pi x)^2) / 2 ]
                 */
                double y = Math.exp( -( Math.pow( 10 * Math.PI * x, 2 ) ) / 2 );
                
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
