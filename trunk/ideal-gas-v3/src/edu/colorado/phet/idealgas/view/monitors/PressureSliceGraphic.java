/**
 * Class: PressureSliceGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.model.Box2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;

public class PressureSliceGraphic extends DefaultInteractiveGraphic {

    private double y;
    private Rectangle2D.Double boundingRect = new Rectangle2D.Double();
    private RoundRectangle2D.Double readoutRectangle = new RoundRectangle2D.Double();
    private PressureSlice pressureSlice;
    private Stroke pressureSliceStroke = new BasicStroke();
    private int pressureSliceHeight = 10;
    private double boxLeftEdge;
    private double boxLowerEdge;
    private double boxTopEdge;
    private double boxRightEdge;
    private boolean selected;
    private Box2D box;
    private NumberFormat pressureFormatter = new DecimalFormat( "#.##" );
    private NumberFormat heightFormatter = new DecimalFormat( "#.##" );
    private double temperature;
    private double pressure;

    public PressureSliceGraphic( Component component, PressureSlice pressureSlice, Box2D box ) {
        super( null );

        final PhetGraphic internalGraphic = new InternalGraphic( component, pressureSlice, box );
        this.setBoundedGraphic( internalGraphic );
        this.addCursorHandBehavior();
        this.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                y += dy;
            }
        } );
//        init( pressureSlice );
        this.pressureSlice = pressureSlice;
        this.box = box;
        y = box.getMinY() + ( box.getMaxY() - box.getMinY() ) / 2;
        pressureSlice.setY( y );
//        pressureSlice.addObserver( this );
    }

    protected void setPosition( Particle body ) {
        // NOP
    }

    private class InternalGraphic extends PhetShapeGraphic implements SimpleObserver {

        InternalGraphic( Component component, PressureSlice pressureSlice, Box2D box ) {
            super( component, boundingRect, null );
            pressureSlice.addObserver( this );
        }

        public void update() {
            repaint();
        }

        public void paint( Graphics2D g2 ) {

            FontMetrics fontMetrics = g2.getFontMetrics();
            int readoutWidth = 80;
            int borderThickness = 8;
            int readoutHeight = fontMetrics.getHeight() * 3 + fontMetrics.getMaxDescent();// + 2 * borderThickness;

            g2.setStroke( pressureSliceStroke );

            boxLeftEdge = box.getMinX();
            boxRightEdge = box.getMaxX();
            boxLowerEdge = box.getMaxY();
            boxTopEdge = box.getMinY();

            boundingRect.setRect( boxLeftEdge, y - pressureSliceHeight / 2,
                                  boxRightEdge - boxLeftEdge,
                                  pressureSliceHeight );
            readoutRectangle.setRoundRect( (int)boundingRect.getMinX() - 100,
                                           (int)boundingRect.getMinY() - ( readoutHeight / 2 ) - borderThickness,
                                           100, readoutHeight + 2 * borderThickness, 10, 10 );

            // Draw the slice itself, over the box
            g2.setColor( Color.YELLOW );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.3f ) );
            g2.fill( boundingRect );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.draw( boundingRect );

            // Draw the framing rectangle for the readout
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.3f ) );
            g2.fill( readoutRectangle );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.draw( readoutRectangle );

            // Draw the readouts
            Point readoutLocation = new Point( (int)( readoutRectangle.getMinX() + borderThickness ),
                                               (int)( readoutRectangle.getMinY() + borderThickness ) );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.drawRoundRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight, 5, 5 );
            g2.setColor( Color.WHITE );
            g2.fillRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );
            g2.setColor( Color.yellow );
            g2.drawRoundRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight, 5, 5 );
            int strLocY = readoutLocation.y + fontMetrics.getAscent() / 2;
            String pressureStr = "p = " + pressureFormatter.format( pressure );
            g2.setColor( Color.black );
            strLocY += borderThickness;
            g2.drawString( pressureStr, readoutLocation.x + 5, strLocY );

            String temperatureStr = "t = " + pressureFormatter.format( temperature );
            g2.setColor( Color.black );
            strLocY += fontMetrics.getHeight();
            g2.drawString( temperatureStr, readoutLocation.x + 5, strLocY );

            // y location must be converted to units compatible with the graphic ruler
            String heightStr = "h = " + heightFormatter.format( ( ( ( boxLowerEdge - y ) - 3.3 ) / 70.857 ) );
            g2.setColor( Color.black );
            strLocY += fontMetrics.getHeight();
            g2.drawString( heightStr, readoutLocation.x + 5, strLocY );

            // Reset the alpha vaule, just in case the next client of the Graphics2D
            // assumes it is 1.
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
        }

        public void update( Observable o, Object arg ) {
            temperature = pressureSlice.getTemperature();
            pressure = pressureSlice.getPressure();
        }

    }

    //
    // Mouse-related methods
    //

//    Point2D.Float dragStartPt = new Point2D.Float();
//    Point2D.Float imageStartPt = new Point2D.Float();
//    private double dy;
//
//    public void mouseClicked( MouseEvent e ) {
//    }
//
//    public void mousePressed( MouseEvent e ) {
//        if( isInHotSpot( e.getPoint() ) ) {
//            selected = true;
//            getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
//            dy = e.getPoint().getY() - y;
//            dragStartPt.setLocation( e.getPoint() );
//        }
//    }
//
//    public void mouseReleased( MouseEvent e ) {
//        if( selected ) {
//            selected = false;
//            getApparatusPanel().setCursor( Cursor.getDefaultCursor() );
//        }
//    }
//
//    public void mouseEntered( MouseEvent e ) {
//    }
//
//    public void mouseExited( MouseEvent e ) {
//        getApparatusPanel().setCursor( Cursor.getDefaultCursor() );
//    }
//
//    public void mouseDragged( MouseEvent e ) {
//        if( selected ) {
//            y = (float)( e.getY() - dy );
//            y = Math.max( y, boxTopEdge + pressureSliceHeight );
//            y = Math.min( y, boxLowerEdge - pressureSliceHeight );
//            pressureSlice.setY( y );
//
//            // We do this so the panel will update even if the clock is stopped
//            this.getApparatusPanel().invalidate();
//            this.getApparatusPanel().repaint();
//        }
//    }
//
//    public void mouseMoved( MouseEvent e ) {
//        if( this.isInHotSpot( e.getPoint() ) ) {
//            getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
//        }
//    }
//
//    public boolean isInHotSpot( Point p ) {
//        return boundingRect.contains( p ) || readoutRectangle.contains( p );
//    }
//
//    protected Point2D.Float getDragStartPt() {
//        return dragStartPt;
//    }
//
//    protected Point2D.Float getImageStartPt() {
//        return imageStartPt;
//    }
}

