/*PhET, 2004.*/
package edu.colorado.phet.chart.controllers;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 9:02:00 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class ChartCursor extends CompositePhetGraphic {

    private ArrayList listeners = new ArrayList();
    private double minX = Double.NEGATIVE_INFINITY;
    private double maxX = Double.POSITIVE_INFINITY;

    private Chart chart;
    private double modelX = 0;
    private int width;
    private Stroke stroke = new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 2, new float[]{6, 4}, 0 );
    private Rectangle shape = null;
    private PhetShapeGraphic shapeGraphic;

    public ChartCursor( Component component, final Chart chart, int width ) {
        this( component, chart, new Color( 0, 0, 220, 40 ), Color.black, width );
    }

    public ChartCursor( Component component, final Chart chart, Color fill, Color outline, int width ) {
        super( component );

        this.chart = chart;
        this.width = width;
        shape = new Rectangle();
        setCursorHand();
        addMouseInputListener( new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                double newX = toModelCoordinate( e, chart );
                newX = Math.max( newX, chart.getRange().getMinX() );
                newX = Math.min( newX, chart.getRange().getMaxX() );
                newX = Math.max( newX, minX );
                newX = Math.min( newX, maxX );
                if( newX != modelX ) {
//                    setModelX( newX );//TODO this will break old interfaces.
                    for( int i = 0; i < listeners.size(); i++ ) {
                        Listener listener = (Listener)listeners.get( i );
                        listener.modelValueChanged( newX );
                    }
                }
            }
        } );
        chart.addListener( new Chart.Listener() {
            public void transformChanged( Chart chart ) {
                update();
            }
        } );
        shapeGraphic = new PhetShapeGraphic( component, null, fill, stroke, outline );
        addGraphic( shapeGraphic );
        update();
    }

    protected double toModelCoordinate( MouseEvent e, final Chart chart ) {
        try {
            Point2D txPt = chart.getNetTransform().inverseTransform( e.getPoint(), null );
            int x = (int)txPt.getX();
            double newX = chart.getModelViewTransform().viewToModelX( x );
            return newX;
        }
        catch( NoninvertibleTransformException e1 ) {
            e1.printStackTrace();
            throw new RuntimeException( e1 );
        }
    }

    public void setX( double x ) {
        setModelX( x );
    }

    public double getMaxX() {
        return maxX;
    }

    public interface Listener {
        public void modelValueChanged( double modelX );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setMinX( double minX ) {
        this.minX = minX;
    }

    public void setMaxX( double maxX ) {
        this.maxX = maxX;
    }

    public void update() {
        int xCenter = chart.getModelViewTransform().modelToViewX( modelX ) + chart.getX();
        int x = xCenter - width / 2;
        int y = chart.getChartBounds().y;
        int height = chart.getChartBounds().height;
        shape.setBounds( x, y, width, height );
        shapeGraphic.setShape( shape );
        shapeGraphic.setShapeDirty();
        setBoundsDirty();
        autorepaint();
    }

    public void setModelX( double modelX ) {
        this.modelX = modelX;
        update();
    }


}
