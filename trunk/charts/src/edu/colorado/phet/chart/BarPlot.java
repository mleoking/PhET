/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


/**
 * BarPlot is used by Chart to draw a set of bars in a bar graph.
 * <p>
 * Each point in the BarPlot's data set corresponds a bar.
 * The x coordinate is the bar's position.
 * The y coordinate is the bar's height.
 * All bars are anchored at y=0.
 * For example, the point (50,100) defines a bar at x=50
 * that extends from y=0 to y=100.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarPlot extends DataSetGraphic {
   
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _barWidth;
    private Color _fillColor;
    private Color _borderColor;
    private Stroke _stroke;

    private ArrayList _points; // array of Point2D
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a BarPlot with default properties.
     * The bars are black with no border.
     * 
     * @param component     parent Component
     * @param chart         chart that this plot is associated with
     * @param barWidth      width of the bars, in model coordinates
     */
    public BarPlot( Component component, Chart chart, double barWidth ) {
        this( component, chart, barWidth, Color.BLACK, null /* borderColor */, null /* stroke */ );
    }
    
    /**
     * Constructs a BarPlot with specific properties.
     * 
     * @param component     parent Component
     * @param chart         chart that this plot is associated with
     * @param barWidth      width of the bars, in model coordinates
     * @param fillColor     color used to fill the bars
     * @param borderColor   color used for the bars' borders
     * @param stroke        stroke used to draw the bars' borders
     */
    public BarPlot( Component component, final Chart chart, double barWidth, Color fillColor, Color borderColor, Stroke stroke ) {
        super( component, chart, new DataSet() );
        _barWidth = barWidth;
        _fillColor = fillColor;
        _borderColor = borderColor;
        _stroke = stroke;
        _points = new ArrayList();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the bar width for all bars in this plot.
     * 
     * @param barWidth the bar width, in model coordinates
     * @throws IllegalArgumentException if barWidth is not > 0
     */
    public void setBarWidth( double barWidth ) {
        if( barWidth <= 0 ) {
            throw new IllegalArgumentException( "barWidth must be > 0 : " + barWidth );
        }
        _barWidth = barWidth;
        updateGraphics();
    }
    
    /**
     * Gets the bar width.
     * 
     * @return the bar width, in model coordinates
     */
    public double getBarWidth() {
        return _barWidth;
    }
    
    /**
     * Sets the fill color for all bars in this plot.
     * 
     * @param color (null if you don't want the bars filled)
     */
    public void setFillColor( Color fillColor ) {
        _fillColor = fillColor;
        updateGraphics();
    }
    
    /**
     * Gets the fill color.
     * 
     * @return the fill color, possibly null
     */
    public Color getFillColor() {
        return _fillColor;
    }
    
    /**
     * Sets the border color for all bars in this plot.
     * 
     * @param color (null if you don't want the border drawn)
     */
    public void setBorderColor( Color borderColor ) {
        _borderColor = borderColor;
        updateGraphics();
    }
    
    /**
     * Gets the border color.
     * 
     * @return the border color, possibly null
     */
    public Color getBorderColor() {
        return _borderColor;
    }
    
    /**
     * Sets the border stroke for all bars in this plot.
     * 
     * @param stroke (null if you don't want the border drawn)
     */
    public void setStroke( Stroke stroke ) {
        _stroke = stroke;
        updateGraphics();
    }
    
    /**
     * Gets the border stroke.
     * 
     * @return the border stroke, possibly null
     */
    public Stroke getStroke() {
        return _stroke;
    }
    
    //----------------------------------------------------------------------------
    // graphics
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphics when some property or the associated data set has changed.
     */
    private void updateGraphics() {
        clear(); // remove all child graphics from this GraphicLayerSet
        
        // Determine the view coordinates of y=0
        Point2D viewOrigin = getChart().transformDouble( 0, 0 );
        
        // Create a bar for each point.
        for( int i = 0; i < _points.size(); i++ ) {
            Point2D modelPoint = (Point2D) _points.get( i );
            Point2D viewPoint = getChart().transformDouble( modelPoint );
            double viewBarWidth = getChart().transformXDouble( _barWidth );
            
            double x = viewPoint.getX() - ( viewBarWidth / 2 );
            double y = viewPoint.getY();
            double width = viewBarWidth;
            double height = viewOrigin.getY() - viewPoint.getY();
            Rectangle2D barShape = new Rectangle2D.Double( x, y, width, height );
            
            PhetShapeGraphic barGraphic = new PhetShapeGraphic( getComponent(), barShape, _fillColor, _stroke, _borderColor );
            addGraphic( barGraphic );
        }
    }
    
    //----------------------------------------------------------------------------
    // DataSetGraphic implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when the associated Chart's size or range changes.
     * The plot is reset by re-adding all points. 
     */
    public void transformChanged() {
        updateGraphics();
    }

    //----------------------------------------------------------------------------
    // DataSet.Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when the associated data set is cleared.
     * Clients should not call this method directly.
     * Effectively causes the plot to erase itself.
     */
    public void cleared() {
        _points.clear();
        updateGraphics();
    }

    /**
     * Called when a single point is added to the associated data set.
     * Clients should not call this method directly.
     * The plot is redrawn after the point is added.
     * 
     * @param point
     */
    public void pointAdded( Point2D point ) {
        _points.add( point );
        updateGraphics(); 
    }

    /**
     * Called when a set of points is added to the associateds data set.
     * Clients should not call this method directly.
     * The plot is redrawn after all the points have been added.
     * 
     * @param points
     */
    public void pointsAdded( Point2D[] points ) {
        for( int i = 0; i < points.length; i++ ) {
            _points.add( points[i] );
        }
        updateGraphics();
    }
}
