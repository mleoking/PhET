/**
 * Class: Chart
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Chart {
    private Component component;
    private ArrayList dataSetGraphics = new ArrayList();

    public void addDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        dataSetGraphic.setChart( this );
        dataSetGraphics.add( dataSetGraphic );
    }

    Component getComponent() {
        return component;
    }

    /**
     * Takes a point in model coordinates and returns the corresponding view location.
     *
     * @param point
     * @return the Point in view coordinates.
     */
    public Point transform( Point2D point ) {
        return null;
    }

    public void paint( Graphics2D graphics2D ) {
        //paint the background
        //paint the gridlines
        //paint the ornaments.
        for( int i = 0; i < dataSetGraphics.size(); i++ ) {
            DataSetGraphic dataSetGraphic = (DataSetGraphic)dataSetGraphics.get( i );
            dataSetGraphic.paint( graphics2D );
        }
    }
}
