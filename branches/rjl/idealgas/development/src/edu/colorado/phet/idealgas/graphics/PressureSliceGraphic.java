/**
 * Class: PressureSliceGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.graphics.PhetGraphic;
import edu.colorado.phet.physics.PressureSlice;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.collision.Box2D;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PressureSliceGraphic extends PhetGraphic implements MouseInputListener {

    private float y;
    private Rectangle2D.Double boundingRect = new Rectangle2D.Double();
    private RoundRectangle2D.Double readoutRectangle = new RoundRectangle2D.Double();
    private PressureSlice pressureSlice;
    private Stroke pressureSliceStroke = new BasicStroke();
    private int pressureSliceHeight = 10;
    private float boxLeftEdge;
    private float boxLowerEdge;
    private float boxTopEdge;
    private float boxRightEdge;
    private boolean selected;
    private Box2D box;
    private NumberFormat pressureFormatter = new DecimalFormat( "#.##" );
    private NumberFormat heightFormatter = new DecimalFormat( "#.##" );
    private float temperature;

    public PressureSliceGraphic( PressureSlice pressureSlice, Box2D box ) {
        init( pressureSlice );
        this.pressureSlice = pressureSlice;
        this.box = box;
        y = box.getMinY() + ( box.getMaxY() - box.getMinY() ) / 2;
        setGraphicsLocations() ;
        pressureSlice.setY( y );
    }

    private void setGraphicsLocations() {
        boxLeftEdge = box.getMinX();
        boxRightEdge = box.getMaxX();
        boxLowerEdge = box.getMaxY();
        boxTopEdge = box.getMinY();

        boundingRect.setRect( boxLeftEdge, this.y - pressureSliceHeight / 2,
                              boxRightEdge - boxLeftEdge,
                              pressureSliceHeight );
        readoutRectangle.setRoundRect( (int)boundingRect.getMinX() - 100,
                          (int)boundingRect.getMinY() - 25,
                          100, 50 + (int)boundingRect.getHeight(), 10, 10 );
    }

    protected void setPosition( Particle body ) {
    }

    private long lastTimeRecorderUpdated = 0;
    private long timeBetweenRecorderUpdates = 500;
    private double pressure = 0;

    public void paint( Graphics2D g2 ) {

        setGraphicsLocations();

        g2.setStroke( pressureSliceStroke );

        g2.setColor( Color.YELLOW );
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.3f ) );
        g2.fill( boundingRect );
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
        g2.draw( boundingRect );

        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.3f ) );
        g2.fill( readoutRectangle );
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
        g2.draw( readoutRectangle );

        Point readoutLocation = new Point( (int)boundingRect.getMinX() - 90,
                                           (int)boundingRect.getMinY() - 15 );
        int readoutWidth = 80;
        int readoutHeight = 40;
        int leading = 20;
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
        g2.drawRoundRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight, 5, 5 );
        g2.setColor( Color.WHITE );
        g2.fillRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );
        g2.setColor( Color.yellow );
        g2.drawRoundRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight, 5, 5 );

        pressure = pressureSlice.getPressure();
        String pressureStr = "p = " + pressureFormatter.format( pressure );
        g2.setColor( Color.black );
        g2.drawString( pressureStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 );

        temperature = pressureSlice.getTemperature();
        String temperatureStr = "t = " + pressureFormatter.format( temperature );
        g2.setColor( Color.black );
        g2.drawString( temperatureStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 + leading );

        // y location must be converted to units compatible with the graphic ruler
        String heightStr = "h = " + heightFormatter.format((( (boxLowerEdge - y ) - 3.3 ) / 70.857 ));
        g2.setColor( Color.black );
        g2.drawString( heightStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 + leading * 2 );

        // Reset the alpha vaule, just in case the next client of the Graphics2D
        // assumes it is 1.
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
    }


    //
    // Mouse-related methods
    //
    Point2D.Float dragStartPt = new Point2D.Float();
    Point2D.Float imageStartPt = new Point2D.Float();
    private double dy;

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        if( isInHotSpot( e.getPoint() )) {
            selected = true;
            getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
            dy = e.getPoint().getY() - y;
            dragStartPt.setLocation( e.getPoint() );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if( selected ) {
            selected = false;
            getApparatusPanel().setCursor( Cursor.getDefaultCursor() );
        }
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
        getApparatusPanel().setCursor( Cursor.getDefaultCursor() );
    }

    public void mouseDragged( MouseEvent e ) {
        if( selected ) {
            y = (float)( e.getY() - dy );
            y = Math.max( y, boxTopEdge + pressureSliceHeight );
            y = Math.min( y, boxLowerEdge - pressureSliceHeight );
            this.setGraphicsLocations();
            pressureSlice.setY( y );

            // We do this so the panel will update even if the clock is stopped
            this.getApparatusPanel().invalidate();
            this.getApparatusPanel().repaint();
        }
    }

    public void mouseMoved( MouseEvent e ) {
        if( this.isInHotSpot( e.getPoint() )) {
            getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
    }

    public boolean isInHotSpot( Point p ) {
        return boundingRect.contains( p ) || readoutRectangle.contains( p );
    }

    protected Point2D.Float getDragStartPt() {
        return dragStartPt;
    }

    protected Point2D.Float getImageStartPt() {
        return imageStartPt;
    }

    //
    // Inner classes
    //
    private class MeasuringGraphic /*implements Graphic*/ {
        int crosshairRadius = 15;
        private int xOffset;
        private int yOffset;
        Point2D.Double startPoint = new Point2D.Double();
        Point2D.Double endPoint = new Point2D.Double();
        private int readoutWidth = 60;
        private int readoutHeight = 50;
        private int leading = 20;
        Point location = new Point();
        public int width = crosshairRadius * 2 + readoutWidth + 30;;
        public int height = readoutHeight + 20;

        private Point getLocation() {
            return location;
        }

        public void paint( Graphics2D g2 ) {
            location.setLocation( (int)endPoint.getX() - xOffset, (int)endPoint.getY() - yOffset );
            Point upperLeft = new Point( location.x, location.y );
            Point readoutLocation = new Point( upperLeft.x + crosshairRadius * 2 + 10,
                                               upperLeft.y + crosshairRadius - readoutHeight / 2 );

            g2.setColor( Color.white );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ));
            g2.fillRoundRect(upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                              width, height,
                              5, 5);

            g2.setColor( Color.white );
//            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.DST_OUT, 1f ));
            g2.fillOval( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );

            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC, 1f ));
            g2.setColor( Color.BLACK );
            g2.setStroke( new BasicStroke( 2f ));
            g2.drawRoundRect( upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                              crosshairRadius * 2 + readoutWidth + 30,
                              readoutHeight + 20,
                              5, 5);

            g2.setStroke( new BasicStroke( 3f ) );
            g2.drawOval( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );
            g2.setStroke( new BasicStroke( 1f ) );
            g2.drawLine( upperLeft.x + crosshairRadius, upperLeft.y, upperLeft.x + crosshairRadius, upperLeft.y + crosshairRadius * 2 );
            g2.drawLine( upperLeft.x, upperLeft.y + crosshairRadius, upperLeft.x + crosshairRadius * 2, upperLeft.y + crosshairRadius );
            g2.drawRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );
            g2.setColor( Color.WHITE );
            g2.fillRect( readoutLocation.x + 1, readoutLocation.y + 1, readoutWidth - 1, readoutHeight - 1 );
            String pressureStr = "p = " + endPoint.x;
            g2.setColor( Color.black );
            g2.drawString( pressureStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 );
            String heightStr = "h = " + y;
            g2.setColor( Color.black );
            g2.drawString( heightStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 + leading );
        }
    }

}

