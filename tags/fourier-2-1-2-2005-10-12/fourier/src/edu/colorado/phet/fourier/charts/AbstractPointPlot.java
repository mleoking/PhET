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

import java.awt.Component;
import java.awt.geom.Point2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.DataSetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;


/**
 * AbstractPointPlot is a DataSetGraphic that places itself at a 
 * specified location (in model coordinates) on a chart.
 * It can be used to write a DataSetGraphic that will place 
 * anything (like a PhetGraphic) at a specified point.
 * The location of the plot is changed to reflect changes
 * in the Chart's range or size.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractPointPlot extends DataSetGraphic {

    private Point2D _point;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AbstractPointPlot( Component component, Chart chart ) {
        super( component, chart, new DataSet() );
        _point = new Point2D.Double( 0, 0 );  // default location is at the chart's origin
        updateLocation();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public Point2D getPoint() {
        return new Point2D.Double( _point.getX(), _point.getY() );
    }
    
    //----------------------------------------------------------------------------
    // Update
    //----------------------------------------------------------------------------
    
    protected abstract void updateGraphic();
    
    private void updateLocation() {
        Point2D viewLocation = getChart().transformDouble( _point );
        setLocation( (int) viewLocation.getX(), (int) viewLocation.getY() );
    }
    
    //----------------------------------------------------------------------------
    // DataSetGraphic implementation
    //----------------------------------------------------------------------------

    /**
     * Called when the associated Chart's size or range changes.
     * The graphic is repositioned relative to the chart.
     */
    public void transformChanged() {
        updateGraphic();
        updateLocation();
    }

    //----------------------------------------------------------------------------
    // DataSet.Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Called when the associated data set is cleared.
     * Calls to this method are ignored.
     */
    public void cleared() {
        // do nothing
    }

    /**
     * Called when a point is added to the associated data set.
     * The most-recently added point determines the graphic's location on the chart.
     *
     * @param point
     */
    public void pointAdded( Point2D point ) {
        _point.setLocation( point.getX(), point.getY() );
        updateLocation();
    }

    /**
     * Called when a set of points is added to the associated data set.
     * The last point in the set determines the graphic's location on the chart.
     *
     * @param points
     */
    public void pointsAdded( Point2D[] points ) {
        pointAdded( points[ points.length - 1 ] );
    }
}
