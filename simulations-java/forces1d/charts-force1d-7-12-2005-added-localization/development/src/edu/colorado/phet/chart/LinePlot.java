/**
 * Class: LinePlot
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class LinePlot extends DataSetGraphic {
    private GeneralPath generalPath;
    private Stroke stroke;
    private Paint paint;
    private ArrayList observers = new ArrayList();

    public LinePlot( DataSet dataSet ) {
        this( dataSet, new BasicStroke( 1 ), Color.black );
    }

    public LinePlot( DataSet dataSet, Stroke stroke, Paint paint ) {
        super( dataSet );
        this.stroke = stroke;
        this.paint = paint;
    }

    /**
     * So clients can find out exactly what part changed for repainting.
     */
    public static interface Observer {
        public void plotChanged( Rectangle rect );
    }

    public void addObserver( Observer observer ) {
        observers.add( observer );
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
            //Determine the exact region for repaint.
            Line2D line = new Line2D.Double( generalPath.getCurrentPoint(), viewLocation );
            generalPath.lineTo( (float)viewLocation.getX(), (float)viewLocation.getY() );
            notifyObservers( line );

            Rectangle shape = stroke.createStrokedShape( line ).getBounds();
            getChart().getComponent().repaint( shape.x, shape.y, shape.width, shape.height );
        }
    }

    private void notifyObservers( Line2D line ) {
        Rectangle shape = stroke.createStrokedShape( line ).getBounds();
        for( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer)observers.get( i );
            observer.plotChanged( shape );
        }
    }

    public void pointRemoved( Point2D point ) {
        generalPath = null;
        addAllPoints();
        getChart().getComponent().repaint();
    }

    public void cleared() {
        if( generalPath != null ) {
            Rectangle shape = stroke.createStrokedShape( generalPath ).getBounds();
            getChart().getComponent().repaint( shape.x, shape.y, shape.width, shape.height );
        }
        generalPath = null;
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