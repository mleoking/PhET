/**
 * Class: LinePlot
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class LinePlot extends DataSetGraphic {
    private GeneralPath generalPath;
    private Stroke stroke;
    private Paint paint;

    public LinePlot( DataSet dataSet, Stroke stroke, Paint paint ) {
        super( dataSet );
        this.stroke = stroke;
        this.paint = paint;
    }

    public void pointAdded( Point2D point ) {
        if( point == null ) {
            throw new RuntimeException( "Null point" );
        }
        Point viewLocation = getChart().transform( point );
        if( generalPath == null ) {
            generalPath = new GeneralPath();
            generalPath.moveTo( viewLocation.x, viewLocation.y );
        }
        else {
            generalPath.lineTo( (float)viewLocation.getX(), (float)viewLocation.getY() );
        }
        //TODO this should get the specific rectangle for repainting.
        getChart().getComponent().repaint();
    }

    public void pointRemoved( Point2D point ) {
        generalPath = null;
        addAllPoints();
        getChart().getComponent().repaint();
    }

    public void paint( Graphics2D graphics2D ) {
        if( generalPath != null ) {
            Stroke oldStroke = graphics2D.getStroke();
            Paint oldPaint = graphics2D.getPaint();
            graphics2D.setStroke( stroke );
            graphics2D.setPaint( paint );
            graphics2D.draw( generalPath );
            graphics2D.setStroke( oldStroke );
            graphics2D.setPaint( oldPaint );
        }
    }

    public void transformChanged() {
        generalPath = null;
        addAllPoints();
    }

}
