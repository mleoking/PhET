/*PhET, 2004.*/
package edu.colorado.phet.chart.controllers;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 9:02:00 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class HorizontalCursor extends PhetGraphic {

    private ArrayList listeners = new ArrayList();
    private double minX = Double.NEGATIVE_INFINITY;
    private double maxX = Double.POSITIVE_INFINITY;

    private Chart chart;
    private Color outlineColor;
    private Color fillColor;
    double modelX = 0;
    int width = 8;
//    boolean visible = true;
    private Stroke stroke = new BasicStroke( 1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 2, new float[]{6, 4}, 0 );
    private Rectangle shape = null;

    public HorizontalCursor( Component component, final Chart chart, Color fillColor, Color outlineColor, int width ) {
        super( component );

        this.chart = chart;
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
        this.width = width;
        shape = new Rectangle();
        setCursorHand();
        addMouseInputListener( new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                double newX = chart.getModelViewTransform().viewToModelX( e.getX() );
                newX = Math.max( newX, chart.getRange().getMinX() );
                newX = Math.min( newX, chart.getRange().getMaxX() );
                newX = Math.max( newX, minX );
                newX = Math.min( newX, maxX );
                if( newX != modelX ) {
                    setModelX( newX );
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
        update();
    }

    public void setX( double x ) {
        setModelX( x );
    }

    protected Rectangle determineBounds() {
        return null;
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


//    public void setVisible( boolean visible ) {
//        this.visible = visible;
//    }

    public void paint( Graphics2D g ) {
        if( isVisible() && shape != null ) {
            GraphicsState state = new GraphicsState( g );
            g.setColor( outlineColor );
            g.setStroke( stroke );
            g.draw( shape );
            g.setColor( fillColor );
            g.fill( shape );
            state.restoreGraphics();
        }
    }

    public void update() {
        Rectangle origShape = stroke.createStrokedShape( shape ).getBounds();
        int xCenter = chart.getModelViewTransform().modelToViewX( modelX );
        int x = xCenter - width / 2;
        int y = chart.getChartBounds().y;
        int height = chart.getChartBounds().height;
        shape.setBounds( x, y, width, height );

        Rectangle newShape = stroke.createStrokedShape( shape ).getBounds();
        Rectangle union = origShape.union( newShape );
        chart.getComponent().repaint( union.x, union.y, union.width, union.height );
    }

    public boolean contains( int x, int y ) {
        return isVisible() && shape != null && shape.contains( x, y );
    }

    public void setModelX( double modelX ) {
        this.modelX = modelX;
        update();
    }

}
