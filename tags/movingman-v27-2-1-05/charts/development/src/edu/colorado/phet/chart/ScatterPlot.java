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
    private ScatterPaint scatterPaint = null;
    private ArrayList viewPoints = new ArrayList();

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

    public void setScatterPaint( ScatterPaint scatterPaint ) {
        this.scatterPaint = scatterPaint;
    }

    public interface ScatterPaint {
        void paint( Point point, Graphics2D graphics2D );
    }

    public static class CirclePaint implements ScatterPaint {
        private Stroke stroke;
        private Color color;
        private int radius;
        private boolean filled;

        public CirclePaint( Color color ) {
            this( color, 1, true );
        }

        public CirclePaint( Color color, int radius, boolean filled ) {
            this( color, new BasicStroke( 1 ), radius, filled );
        }

        public CirclePaint( Color color, BasicStroke stroke, int radius, boolean filled ) {
            this.stroke = stroke;
            this.color = color;
            this.radius = radius;
            this.filled = filled;
        }

        public void paint( Point point, Graphics2D graphics2D ) {
            Color origColor = graphics2D.getColor();
            Stroke origStroke = graphics2D.getStroke();
            graphics2D.setColor( color );
            graphics2D.setStroke( stroke );
            int x = point.x - radius;
            int y = point.y - radius;
            if( filled ) {
                graphics2D.fillOval( x, y, radius * 2, radius * 2 );
            }
            else {
                graphics2D.drawOval( x, y, radius * 2, radius * 2 );
            }
            graphics2D.setColor( origColor );
            graphics2D.setStroke( origStroke );
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

    public void cleared() {
        viewPoints.clear();
    }
}
