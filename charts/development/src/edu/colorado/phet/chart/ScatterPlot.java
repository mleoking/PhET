/** Sam Reid*/
package edu.colorado.phet.chart;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 18, 2004
 * Time: 10:31:28 AM
 * Copyright (c) Sep 18, 2004 by Sam Reid
 */
public class ScatterPlot extends DataSetGraphic {
    ScatterPaint scatterPaint = null;
    ArrayList viewPoints = new ArrayList();

    public ScatterPlot( DataSet dataSet ) {
        this( dataSet, new CirclePaint( Color.blue, 1, true ) );
    }

    public ScatterPlot( DataSet dataSet, ScatterPaint scatterPaint ) {
        super( dataSet );
        this.scatterPaint = scatterPaint;
    }

    public void paint( Graphics2D graphics2D ) {
        for( int i = 0; i < viewPoints.size(); i++ ) {
            Point point = (Point)viewPoints.get( i );
            scatterPaint.paint( point, graphics2D );
        }
    }

    public interface ScatterPaint {
        void paint( Point point, Graphics2D graphics2D );
    }

    public static class CirclePaint implements ScatterPaint {
        Color color;
        int radius;
        boolean filled;

        public CirclePaint( Color color, int radius, boolean filled ) {
            this.color = color;
            this.radius = radius;
            this.filled = filled;
        }

        public void paint( Point point, Graphics2D graphics2D ) {
            graphics2D.setColor( color );
            int x = point.x - radius;
            int y = point.y - radius;
            if( filled ) {
                graphics2D.fillOval( x, y, radius * 2, radius * 2 );
            }
            else {
                graphics2D.drawOval( x, y, radius * 2, radius * 2 );
            }
        }
    }

    public void transformChanged() {
        viewPoints.clear();
        addAllPoints();
    }

    public void pointAdded( Point2D point ) {
        viewPoints.add( getChart().transform( point ) );
    }

    public void pointRemoved( Point2D point ) {
        viewPoints.remove( getChart().transform( point ) );
    }
}
