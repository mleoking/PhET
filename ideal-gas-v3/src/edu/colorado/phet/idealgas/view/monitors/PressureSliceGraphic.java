/**
 * Class: PressureSliceGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.model.Box2D;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PressureSliceGraphic extends DefaultInteractiveGraphic {

    private float s_overlayTransparency = 0.3f;

    private double y;
    private Rectangle2D.Double boundingRect = new Rectangle2D.Double();
    private RoundRectangle2D.Double readoutRectangle = new RoundRectangle2D.Double();
    private PressureSlice pressureSlice;
    private Stroke pressureSliceStroke = new BasicStroke();
    private int pressureSliceHeight = 10;
    private double boxLeftEdge;
    private double boxLowerEdge;
    private double boxRightEdge;
    private Box2D box;
    private NumberFormat pressureFormatter = new DecimalFormat( "#.##" );
    private NumberFormat heightFormatter = new DecimalFormat( "#.##" );
    private double temperature;
    private double pressure;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 10 );

    public PressureSliceGraphic( Component component, final PressureSlice pressureSlice, final Box2D box ) {
        super( null );
        this.pressureSlice = pressureSlice;

        final PhetGraphic internalGraphic = new InternalGraphic( component );
        this.setBoundedGraphic( internalGraphic );
        this.addCursorHandBehavior();
        this.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                double newY = Math.min( box.getMaxY(),
                                        Math.max( y + dy, box.getMinY() ) );
                y = newY;
                pressureSlice.setY( y );
            }
        } );
        this.box = box;
        y = box.getMinY() + ( box.getMaxY() - box.getMinY() ) / 2;
        pressureSlice.setY( y );
    }

    private class InternalGraphic extends PhetShapeGraphic implements SimpleObserver {
        private Area drawingArea = new Area();

        InternalGraphic( Component component ) {
            super( component, null, null );
            setShape( drawingArea );
            pressureSlice.addObserver( this );
            update();
        }

        protected Rectangle determineBounds() {
            return drawingArea.getBounds();
        }

        public boolean contains( int x, int y ) {
            return boundingRect.contains( x, y ) || readoutRectangle.contains( x, y );
        }

        public void update() {
            pressure = pressureSlice.getPressure();

            // Clear the drawing area and rebuild it
            drawingArea.exclusiveOr( drawingArea );
            drawingArea.add( new Area( boundingRect ) );
            drawingArea.add( new Area( readoutRectangle ) );
            setBoundsDirty();
            repaint();
        }

        public void paint( Graphics2D g2 ) {
            saveGraphicsState( g2 );

            g2.setFont( font );
            FontMetrics fontMetrics = g2.getFontMetrics();
            int readoutWidth = 80;
            int borderThickness = 8;
            int readoutHeight = fontMetrics.getHeight() * 3 + fontMetrics.getMaxDescent();// + 2 * borderThickness;

            g2.setStroke( pressureSliceStroke );

            boxLeftEdge = box.getMinX();
            boxRightEdge = box.getMaxX();
            boxLowerEdge = box.getMaxY();
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
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, s_overlayTransparency ) );
            g2.fill( readoutRectangle );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.draw( readoutRectangle );

            // Draw the readouts
            Point readoutLocation = new Point( (int)( readoutRectangle.getMinX() + borderThickness ),
                                               (int)( readoutRectangle.getMinY() + borderThickness ) );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.drawRoundRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight, 5, 5 );

            g2.setColor( Color.WHITE );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.fillRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );

            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
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

            restoreGraphicsState();
        }
    }

}

