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
 * ShapePlot is used by Chart to draw a shape on a chart.
 * The shape is defined by a set of points that plot a 
 * GeneralPath.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GeneralPathPlot extends DataSetGraphic {
   
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetShapeGraphic _pathGraphic;
    private GeneralPath _pathShape;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a GeneralPathPlot with default properties.
     * The shape is filled with a solid black, and has no border.
     * 
     * @param component     parent Component
     * @param chart         chart that this plot is associated with
     */
    public GeneralPathPlot( Component component, Chart chart ) {
        this( component, chart, Color.BLACK /* fillPaint */, null /* borderPaint */, null /* stroke */ );
    }
    
    /**
     * Constructs a GeneralPathPlot with specific properties.
     * 
     * @param component     parent Component
     * @param chart         chart that this plot is associated with
     * @param fillPaint     paint used to fill the path
     * @param borderPaint   paint used to stroke the path
     * @param stroke        stroke used to stroke the path
     */
    public GeneralPathPlot( Component component, final Chart chart, Paint fillPaint, Paint borderPaint, Stroke stroke ) {
        super( component, chart, new DataSet() );
        
        _pathShape = new GeneralPath();
        _pathGraphic = new PhetShapeGraphic( component, _pathShape, fillPaint, stroke, borderPaint );
        addGraphic( _pathGraphic );
        
        // Clip to the chart boundary, and change the clip when the chart changes.
        setClip( chart.getChartBounds() );
        chart.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                setClip( chart.getChartBounds() );
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
            }
        } );
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
        _pathShape.reset();
        _pathGraphic.setShapeDirty();
    }

    /**
     * Not implemented.
     * Do not add points one at a time for this graphic's data set!
     * 
     * @throws UnsupportedOperationException if you call this method
     */
    public void pointAdded( Point2D point ) {
        throw new  UnsupportedOperationException( "pointAdded is not supported" ); 
    }

    /**
     * Called when a set of points is added to the associateds data set.
     * Clients should not call this method directly.
     * The plot is redrawn after all the points have been added.
     * 
     * @param points
     */
    public void pointsAdded( Point2D[] points ) {
        _pathShape.reset();
        for( int i = 0; i < points.length; i++ ) {
            Point2D modelPoint = points[i];
            Point2D viewPoint = getChart().transformDouble( modelPoint );
            if ( i == 0 ) {
                _pathShape.moveTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
            }
            else {
                _pathShape.lineTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
            }
        }
        _pathShape.closePath();
        _pathGraphic.setShapeDirty();
    }
}
