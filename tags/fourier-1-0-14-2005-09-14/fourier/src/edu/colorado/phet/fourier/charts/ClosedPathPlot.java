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

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.DataSetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;


/**
 * ClosedPathPlot is used by Chart to draw a closed path on a chart.
 * The closed path is defined by a set of data points.
 * The shape defined by the closed path can be filled and stroked.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ClosedPathPlot extends DataSetGraphic {
   
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetShapeGraphic _pathGraphic;
    private GeneralPath _pathShape;
    private ArrayList _points;  // array of Point2D
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a ClosedPathPlot with default properties.
     * The shape is filled with a solid black, and has no border.
     * 
     * @param component     parent Component
     * @param chart         chart that this plot is associated with
     */
    public ClosedPathPlot( Component component, Chart chart ) {
        this( component, chart, Color.BLACK /* fillPaint */, null /* borderPaint */, null /* stroke */ );
    }
    
    /**
     * Constructs a ClosedPathPlot with specified properties.
     * The shape is filled with a specified paint, but has no border.
     * 
     * @param component     parent Component
     * @param chart         chart that this plot is associated with
     * @param fillPaint     paint used to fill the path
     */
    public ClosedPathPlot( Component component, Chart chart, Paint fillPaint ) {
        this( component, chart, fillPaint, null /* borderPaint */, null /* stroke */ );
    }
    
    /**
     * Constructs a ClosedPathPlot with specific properties.
     * 
     * @param component     parent Component
     * @param chart         chart that this plot is associated with
     * @param fillPaint     paint used to fill the path
     * @param borderPaint   paint used to stroke the path
     * @param stroke        stroke used to stroke the path
     */
    public ClosedPathPlot( Component component, final Chart chart, Paint fillPaint, Paint borderPaint, Stroke stroke ) {
        super( component, chart, new DataSet() );
        
        _points = new ArrayList();
        
        _pathShape = new GeneralPath();
        _pathGraphic = new PhetShapeGraphic( component, _pathShape, fillPaint, stroke, borderPaint );
        addGraphic( _pathGraphic );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the fill paint.
     * 
     * @param color (null if you don't want the shape filled)
     */
    public void setFillPaint( Paint fillPaint ) {
        _pathGraphic.setPaint( fillPaint );
    }
    
    /**
     * Gets the fill paint.
     * 
     * @return the fill paint, possibly null
     */
    public Paint getFillPaint() {
        return _pathGraphic.getPaint();
    }
    
    /**
     * Sets the border color.
     * 
     * @param color (null if you don't want the border drawn)
     */
    public void setBorderPaint( Paint borderPaint ) {
        _pathGraphic.setBorderPaint( borderPaint );
    }
    
    /**
     * Gets the border color.
     * 
     * @return the border color, possibly null
     */
    public Paint getBorderColor() {
        return _pathGraphic.getBorderPaint();
    }
    
    /**
     * Sets the border stroke.
     * 
     * @param stroke (null if you don't want the border drawn)
     */
    public void setStroke( Stroke stroke ) {
        _pathGraphic.setStroke( stroke );
    }
    
    /**
     * Gets the border stroke.
     * 
     * @return the border stroke, possibly null
     */
    public Stroke getStroke() {
        return _pathGraphic.getStroke();
    }

    //----------------------------------------------------------------------------
    // DataSetGraphic implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when the associated Chart's size or range changes.
     * The plot is reset by re-adding all points. 
     */
    public void transformChanged() {
        pointsAdded( getDataSet().getPoints() );
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
        updatePath();
    }

    /**
     * Called when one point is added to the data set.
     * Clients should not call this method directly.
     * <p>
     * This method results in the path being rebuilt.
     * If adding many points, it is more efficient to add
     * them to the data set using DataSet.addPoints.
     * 
     * @param point
     */
    public void pointAdded( Point2D point ) {
        _points.add( point );
        updatePath();
    }

    /**
     * Called when a set of points is added to the data set.
     * Clients should not call this method directly.
     * 
     * @param points
     */
    public void pointsAdded( Point2D[] points ) {
        for ( int i = 0; i < points.length; i++ ) {
            _points.add( points[i] );
        }
        updatePath();
    }
    
    //----------------------------------------------------------------------------
    // Path construction
    //----------------------------------------------------------------------------
    
    /*
     * Updates the path to math the current set of points.
     */
    private void updatePath() {
        
        // Reset the path
        _pathShape.reset();
        
        // Connect the points with line segments
        for( int i = 0; i < _points.size(); i++ ) {
            Point2D modelPoint = (Point2D)_points.get( i );
            Point2D viewPoint = getChart().transformDouble( modelPoint );
            if ( i == 0 ) {
                _pathShape.moveTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
            }
            else {
                _pathShape.lineTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
            }
        }
        
        // Close the path
        if ( _points.size() > 1 ) {
            _pathShape.closePath();      
        }
        
        // Force the graphic to update
        _pathGraphic.setShapeDirty();
    }
}
