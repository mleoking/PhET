/*PhET, 2004.*/
package edu.colorado.phet.chart.controllers;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.view.graphics.BoundedGraphic;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
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
public class HorizontalCursor extends DefaultInteractiveGraphic {
    private CursorGraphic cursorGraphic;
    private ArrayList listeners = new ArrayList();

    public interface Listener {
        public void modelValueChanged( double modelX );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public HorizontalCursor( final Chart chart, Color fillColor, Color outlineColor, int width ) {
        super( new CursorGraphic( chart, fillColor, outlineColor, width ) );
        cursorGraphic = (CursorGraphic)super.getGraphic();
        addCursorHandBehavior();
        addMouseInputListener( new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                double newX = chart.getTransform().viewToModelX( e.getX() );
                newX = Math.max( newX, chart.getRange().getMinX() );
                newX = Math.min( newX, chart.getRange().getMaxX() );
                if( newX != cursorGraphic.modelX ) {
                    cursorGraphic.setModelX( newX );
                    for( int i = 0; i < listeners.size(); i++ ) {
                        Listener listener = (Listener)listeners.get( i );
                        listener.modelValueChanged( newX );
                    }
                }
            }
        } );
    }

    private static class CursorGraphic implements BoundedGraphic {
        private Chart chart;
        private Color outlineColor;
        private Color fillColor;
        double modelX = 0;
        int width = 8;
        boolean visible = true;
        private Stroke stroke = new BasicStroke( 1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 2, new float[]{6, 4}, 0 );
        private Rectangle shape = null;

        public CursorGraphic( Chart chart, Color fillColor, Color outlineColor, int width ) {
            this.chart = chart;
            this.fillColor = fillColor;
            this.outlineColor = outlineColor;
            this.width = width;
            shape = new Rectangle();
            update();
        }

        public void setVisible( boolean visible ) {
            this.visible = visible;
        }

        public void paint( Graphics2D g ) {
            if( visible && shape != null ) {
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
            int xCenter = chart.getTransform().modelToViewX( modelX );
            int x = xCenter - width / 2;
            int y = chart.getViewBounds().y;
            int height = chart.getViewBounds().height;
            shape.setBounds( x, y, width, height );

            Rectangle newShape = stroke.createStrokedShape( shape ).getBounds();
            Rectangle union = origShape.union( newShape );
            chart.getComponent().repaint( union.x, union.y, union.width, union.height );
        }

        public boolean contains( int x, int y ) {
            return visible && shape != null && shape.contains( x, y );
        }

        public void setModelX( double modelX ) {
            this.modelX = modelX;
            update();
        }
    }
}
