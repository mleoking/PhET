package edu.colorado.phet.bernoulli.meter;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Sep 25, 2003
 * Time: 10:25:20 AM
 * To change this template use Options | File Templates.
 */
public class BarometerView implements InteractiveGraphic, SimpleObserver {
    Barometer barometer;
    Point pt;
    int width;
    int height;
    private ModelViewTransform2d transform;
    boolean init;
    String messageString;

    Color textColor = Color.black;
    private MeasuringGraphicNew measuringGraphic = new MeasuringGraphicNew();
    private ArrayList data = new ArrayList();
    private DecimalFormat decimalFormat = new DecimalFormat( "#0.0#" );
//    private MeasuringGraphic measuringGraphic = new MeasuringGraphic();

    public BarometerView( Barometer barometer, int width, int height, ModelViewTransform2d transform ) {
        this.barometer = barometer;
        this.width = width;
        this.height = height;
        this.transform = transform;
        update();
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                update();
            }
        } );
        barometer.addObserver( this );

        data.clear();
        data.add( new MeasuringGraphicNew.Datum( "pressure", "???" ) );
        data.add( new MeasuringGraphicNew.Datum( "height", new Double( 0.0 ) ) );
        barometer.updateObservers();
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return measuringGraphic.contains( event.getPoint() );
    }

    DifferentialDragHandler dragHandler = null;

    public void mousePressed( MouseEvent event ) {
        dragHandler = new DifferentialDragHandler( event.getPoint() );
    }

    public void mouseDragged( MouseEvent event ) {
        Point dr = dragHandler.getDifferentialLocationAndReset( event.getPoint() );
        Point2D.Double modelDR = transform.viewToModelDifferential( dr );
        barometer.translate( modelDR.x, modelDR.y );
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void update() {
        this.pt = transform.modelToView( barometer.getLocation() );
        this.messageString = "Pressure = " + barometer.getPressure();
        init = true;
    }

    public void paint( Graphics2D g ) {
        if( init ) {
            data.clear();
//            data.add(new MeasuringGraphicNew.Datum("pressure", decimalFormat.format(barometer.getX())));
            data.add( new MeasuringGraphicNew.Datum( "water pressure", decimalFormat.format( barometer.getPressure() ) + " Pa" ) );
            data.add( new MeasuringGraphicNew.Datum( "height", decimalFormat.format( barometer.getY() ) + " m" ) );
            measuringGraphic.paint( g, pt, data );
        }
    }

    //
    // Inner Classes
    //
    private class MeasuringGraphic {
        Shape bounds;
        int crosshairRadius = 15;
        private int readoutWidth = 60;
        private int readoutHeight = 50;
        private int leading = 20;
        Point location = new Point();
        public int width = crosshairRadius * 2 + readoutWidth + 30;;
        public int height = readoutHeight + 20;
        private Font font = new Font( "dialog", 0, 12 );
        private DecimalFormat decimalFormat = new DecimalFormat( "#0.0#" );
        private Stroke crossHairStroke = new BasicStroke( 1f );
        private Stroke holeStroke = new BasicStroke( 3f );
        private Stroke boundsStroke = new BasicStroke( 2f );

        public void paint( Graphics2D g2, Point location, double p, double h ) {

            // Compute the locations of things
            location.setLocation( (int)location.getX(), (int)location.getY() );
            Point upperLeft = new Point( location.x - crosshairRadius, location.y - crosshairRadius );
            Point readoutLocation = new Point( upperLeft.x + crosshairRadius * 2 + 10,
                                               upperLeft.y + crosshairRadius - readoutHeight / 2 );
            bounds = new RoundRectangle2D.Double( upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                                                  width, height,
                                                  5, 5 );

            // Draw the outline of the whole thing and fill the background
            Ellipse2D.Double hole = new Ellipse2D.Double( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );
            Area a = new Area( bounds );
            a.subtract( new Area( hole ) );
            g2.setColor( new Color( 255, 255, 255, 128 ) );
            g2.fill( a );
            g2.setColor( Color.BLACK );
            g2.setStroke( boundsStroke );
            g2.drawRoundRect( upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                              crosshairRadius * 2 + readoutWidth + 30,
                              readoutHeight + 20,
                              5, 5 );

            // Draw the hole and the crosshairs
            g2.setStroke( holeStroke );
            g2.drawOval( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );
            g2.setStroke( crossHairStroke );
            g2.drawLine( upperLeft.x + crosshairRadius, upperLeft.y, upperLeft.x + crosshairRadius, upperLeft.y + crosshairRadius * 2 );
            g2.drawLine( upperLeft.x, upperLeft.y + crosshairRadius, upperLeft.x + crosshairRadius * 2, upperLeft.y + crosshairRadius );
            g2.drawRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );

            // Draw the readout values
            g2.setFont( font );
            g2.setColor( Color.WHITE );
            g2.fillRect( readoutLocation.x + 1, readoutLocation.y + 1, readoutWidth - 1, readoutHeight - 1 );
            String pressureStr = "p = " + decimalFormat.format( p );
            g2.setColor( Color.black );
            g2.drawString( pressureStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 );
            String heightStr = "h = " + decimalFormat.format( h );
            g2.setColor( Color.black );
            g2.drawString( heightStr, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 + leading );
        }

        boolean contains( Point point ) {
            return bounds == null ? false : bounds.contains( point );
        }
    }

}
