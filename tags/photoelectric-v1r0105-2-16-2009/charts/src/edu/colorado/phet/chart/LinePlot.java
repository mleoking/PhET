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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * LinePlot takes a set of points (a DataSet) and connects the
 * points with line segments.
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class LinePlot extends DataSetGraphic {

    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    private static final Color DEFAULT_COLOR = Color.BLACK;

    private boolean generalPathIsEmpty; // true if generalPath contains zero points
    private GeneralPath generalPath;
    private PhetShapeGraphic phetShapeGraphic;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public LinePlot( Component component, Chart chart ) {
        this( component, chart, null );
    }

    public LinePlot( Component component, Chart chart, DataSet dataSet ) {
        this( component, chart, dataSet, DEFAULT_STROKE, DEFAULT_COLOR );
    }

    public LinePlot( Component component, final Chart chart, DataSet dataSet, Stroke stroke, Paint paint ) {
        super( component, chart, dataSet );

        // Create the PhetShape graphic that draws the plot.
        generalPath = new GeneralPath();
        generalPathIsEmpty = true;
        phetShapeGraphic = new PhetShapeGraphic( getComponent(), generalPath, stroke, paint );
        addGraphic( phetShapeGraphic );

        addAllPoints();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the Stroke used to stroke the plot.
     *
     * @param stroke
     */
    public void setStroke( Stroke stroke ) {
        phetShapeGraphic.setStroke( stroke );
    }

    /**
     * Sets the Color used to stroke the plot.
     *
     * @param color
     */
    public void setBorderColor( Color color ) {
        phetShapeGraphic.setBorderColor( color );
    }

    /**
     * Sets the Color used to stroke the plot.
     *
     * @param color
     */
    public void setStrokeColor( Color color ) {
        phetShapeGraphic.setBorderColor( color );
    }

    //----------------------------------------------------------------------------
    // DataSetGraphic implementation
    //----------------------------------------------------------------------------

    /**
     * Called when the associated Chart's size or range changes.
     * The plot is reset by re-adding all points.
     */
    public void transformChanged() {
        generalPath.reset();
        generalPathIsEmpty = true;
        addAllPoints();
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
        generalPath.reset();
        generalPathIsEmpty = true;
        repaintAll();
    }

    /**
     * Called when a single point is added to the associated data set.
     * Clients should not call this method directly.
     * The plot is redrawn after the point is added.
     * If you need to add a set of point, it is more efficient to call pointsAdded.
     *
     * @param point
     */
    public void pointAdded( Point2D point ) {
        pointAddedNoRepaint( point );
        repaintAll();
    }

    /**
     * Called when a set of points is added to the associateds data set.
     * Clients should not call this method directly.
     * The plot is redrawn after all the points have been added.
     * If you need points to appear individually as they are added,
     * then you need to call pointAdded.
     *
     * @param points
     */
    public void pointsAdded( Point2D[] points ) {
        for( int i = 0; i < points.length; i++ ) {
            pointAddedNoRepaint( points[i] );
        }
        repaintAll();
    }

    /**
     * Adds a point to the plot, but performs no redraw.
     * This method is shared by pointAdded and pointsAdded.
     *
     * @param point
     */
    protected void pointAddedNoRepaint( Point2D point ) {
        if( point == null ) {
            throw new RuntimeException( "Null point" );
        }
        Point2D viewLocation = getChart().transformDouble( point );
        if( generalPathIsEmpty ) {
            generalPath.moveTo( (float)viewLocation.getX(), (float)viewLocation.getY() );
            generalPathIsEmpty = false;
        }
        else {
            generalPath.lineTo( (float)viewLocation.getX(), (float)viewLocation.getY() );
        }
    }

    /**
     * Triggers a repaint of the plot.
     * This is currently done by setting the PhetShapeGraphic to dirty.
     */
    private void repaintAll() {
        phetShapeGraphic.setShapeDirty();
    }
}