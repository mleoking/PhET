// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.DataSet;
import edu.colorado.phet.common.charts.Range2D;

import java.awt.*;

/**
 * PhotoelectricGraph
 * <p/>
 * A Chart for graphs for the Photoelectric simulation. It has an abstract method for clearing the data,
 * and methods to zoom in and out on the vertical axis.
 */
public abstract class PhotoelectricGraph extends Chart {
    private double zoomFactor = 0.2;
    private DataSet lineDataSet = new DataSet();
    private DataSet dotDataSet = new DataSet();

    public PhotoelectricGraph( Component component, Range2D range, Dimension chartSize, double horizMinorSpacing, double horizMajorSpacing, double vertMinorSpacing, double vertMajorSpacing ) {
        super( component, range, chartSize, horizMinorSpacing, horizMajorSpacing, vertMinorSpacing, vertMajorSpacing );
    }

    public void zoomIn() {
        Range2D range = getRange();
        range.setMaxY( range.getMaxY() * ( 1 - zoomFactor ) );
        setRange( range );
    }

    public void zoomOut() {
        Range2D range = getRange();
        range.setMaxY( range.getMaxY() * ( 1 + zoomFactor ) );
        setRange( range );
    }

    protected DataSet getLineDataSet() {
        return lineDataSet;
    }

    protected DataSet getDotDataSet() {
        return dotDataSet;
    }

    protected void clearLinePlot() {
        lineDataSet.clear();
    }

    protected void clearData() {
        clearLinePlot();
    }

    protected void setDotDataPoint( double x, double y ) {
        dotDataSet.clear();
        dotDataSet.addPoint( x, y );
    }
}
