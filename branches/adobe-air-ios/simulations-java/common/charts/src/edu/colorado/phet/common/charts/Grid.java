// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.charts;

import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:25:31 AM
 */
public class Grid extends AbstractGrid {

    public Grid( Chart chart, Orientation orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
        super( chart, orientation, stroke, color, tickSpacing, crossesOtherAxisAt );
        chart.addListener( new Chart.Listener() {
            public void transformChanged( Chart chart ) {
                update();
            }
        } );
        update();
    }

    protected void update() {
        clear();
        Orientation orientation = super.getOrientation();
        Chart chart = super.getChart();
        double[] gridLines = getVisibleGridlines();
        if ( orientation.isVertical() ) {
            for ( int i = 0; i < gridLines.length; i++ ) {
                double gridLineX = gridLines[i];
                if ( chart.getRange().containsX( gridLineX ) ) {
                    Point src = chart.transform( gridLineX, chart.getRange().getMinY() );
                    Point dst = chart.transform( gridLineX, chart.getRange().getMaxY() );
                    Line2D.Double line = new Line2D.Double( src.x, src.y, dst.x, dst.y );
                    PhetShapeGraphic lineGraphic = new PhetShapeGraphic( chart.getComponent(), line, getStroke(), getColor() );
                    addGraphic( lineGraphic );
                }
            }
        }
        else if ( orientation.isHorizontal() ) {
            for ( int i = 0; i < gridLines.length; i++ ) {//TODO this is a big bug.
                double gridLineY = gridLines[i];
                if ( chart.getRange().containsY( gridLineY ) ) {
                    Point src = chart.transform( chart.getRange().getMinX(), gridLineY );
                    Point dst = chart.transform( chart.getRange().getMaxX(), gridLineY );
                    Line2D.Double line = new Line2D.Double( src.x, src.y, dst.x, dst.y );
                    PhetShapeGraphic lineGraphic = new PhetShapeGraphic( chart.getComponent(), line, getStroke(), getColor() );
                    addGraphic( lineGraphic );
                }
            }
        }
    }

}
