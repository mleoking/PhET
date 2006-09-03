/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 18, 2004
 * Time: 10:31:28 AM
 * Copyright (c) Sep 18, 2004 by Sam Reid
 */
public class ScatterPlot extends DataSetGraphic {
    private GraphicLayerSet points;
    private ScatterPaintFactory scatterPaintFactory;

    public ScatterPlot( Component component, Chart chart, DataSet dataSet ) {
        this( component, chart, dataSet, new ScatterPlot.CircleFactory( component, Color.blue, 3 ) );
    }

    public ScatterPlot( Component component, Chart chart, DataSet dataSet, ScatterPaintFactory scatterPaintFactory ) {
        super( component, chart, dataSet );
        this.scatterPaintFactory = scatterPaintFactory;
        points = new GraphicLayerSet();
        addGraphic( points );
    }

    public static interface ScatterPaintFactory {
        PhetGraphic createScatterPoint( double x, double y );
    }

    public void transformChanged() {
        points.clear();
        addAllPoints();
    }

    public void pointAdded( Point2D point ) {
        Point viewLoc = getChart().transform( point );
        points.addGraphic( scatterPaintFactory.createScatterPoint( viewLoc.getX(), viewLoc.getY() ) );
    }

    public void cleared() {
        points.clear();
    }

    public static class CircleFactory implements ScatterPaintFactory {
        private Component component;
        private Color color;
        private int radius;

        public CircleFactory( Component component, Color color, int radius ) {
            this.component = component;
            this.color = color;
            this.radius = radius;
        }

        public PhetGraphic createScatterPoint( double x, double y ) {
            Ellipse2D.Double ellipse = new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 );
            PhetGraphic graphic = new PhetShapeGraphic( component, ellipse, color );
            return graphic;
        }
    }
}
