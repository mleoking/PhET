/**
 * Class: Chart
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Chart {
    private Component component;
    private ArrayList dataSetGraphics = new ArrayList();
    private Axis xAxis;
    private Axis yAxis;
    private Range2D range;
    private Rectangle viewBounds;

    public Chart( Component component, Range2D range, Rectangle viewBounds ) {
        this.component = component;
        this.range = range;
        this.viewBounds = viewBounds;
        this.xAxis = new Axis( this );
        this.yAxis = new Axis( this );
    }

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

    public Axis getXAxis() {
        return xAxis;
    }

    public Axis getYAxis() {
        return yAxis;
    }

    public void setXAxis( Axis xAxis ) {
        this.xAxis = xAxis;
    }

    public void setYAxis( Axis yAxis ) {
        this.yAxis = yAxis;
    }

    public static class Axis implements Graphic {
        public final static int HORIZONTAL = 0;
        public final static int VERTICAL = 1;
        Chart chart;
        Stroke stroke;
        Color color;
        double majorTickSpacing;
        double minorTickSpacing;
        int orientation;
        double crossesOtherAxisAt;

        public Axis( Chart chart, int orientation ) {
            this( chart, orientation, new BasicStroke( 1 ), Color.black );
        }

        public Axis( Chart chart, int orientation, Stroke stroke, Color color ) {
            this.chart = chart;
            this.orientation = orientation;
            this.stroke = stroke;
            this.color = color;
            this.crossesOtherAxisAt = 0.0;
        }

        public void paint( Graphics2D g ) {
            Stroke origStroke = g.getStroke();
            Color origColor = g.getColor();
            g.setStroke( stroke );
            g.setColor( color );
            switch( orientation ) {
                case HORIZONTAL:
                    {
                        Point2D.Double leftEndOfAxis = new Point2D.Double( chart.getRange().getMinX(), crossesOtherAxisAt );
                        Point left = chart.transform( leftEndOfAxis );

                        Point2D.Double rightEndOfAxis = new Point2D.Double( chart.getRange().getMaxX(), crossesOtherAxisAt );
                        Point right = chart.transform( rightEndOfAxis );

                        g.drawLine( left.x, left.y, right.x, right.y );
                        break;
                    }
                case VERTICAL:
                    {
                        Point2D.Double bottomEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMinY() );
                        Point bottom = chart.transform( bottomEndOfAxis );

                        Point2D.Double topEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMaxX() );
                        Point top = chart.transform( topEndOfAxis );

                        g.drawLine( bottom.x, bottom.y, top.x, top.y );
                        break;
                    }
                default:
                    throw new RuntimeException( "invalid orientation" );
            }
            g.setStroke( origStroke );
            g.setColor( origColor );
        }
    }

    public static double[] getGridLines( double origin, double min, double max, double spacing ) {
        ArrayList results = new ArrayList();

        for( double currentPoint = origin; currentPoint <= max; currentPoint += spacing ) {
            results.add( new Double( currentPoint ) );
        }
        for( double currentPoint = origin - spacing; currentPoint >= min; currentPoint -= spacing ) {
            results.add( new Double( currentPoint ) );
        }
        Collections.sort( results );
        double[] output = new double[results.size()];
        for( int i = 0; i < output.length; i++ ) {
            output[i] = ((Double)results.get(i)).doubleValue();
        }
        return output;
    }

    public Range2D getRange() {
        return range;
    }

    public Rectangle getViewBounds() {
        return viewBounds;
    }

    public static void main( String[] args ) {
    }
}
