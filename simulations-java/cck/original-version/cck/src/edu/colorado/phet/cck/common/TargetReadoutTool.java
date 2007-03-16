/**
 * Class: TargetReadoutTool
 * Package: edu.colorado.phet.bernoulli.meter
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class TargetReadoutTool {
    RoundRectangle2D.Double bounds;
    int crosshairRadius = 15;
    private int readoutWidth = 140;
    private int readoutHeight = 50;
    private int leading = 20;
    Point location = new Point();
    public int width = crosshairRadius * 2 + readoutWidth + 30;
    public int height = readoutHeight + 20;
    private Font font = new Font( "dialog", Font.BOLD, 14 );
    private Stroke crossHairStroke = new BasicStroke( 1f );
    private Stroke holeStroke = new BasicStroke( 3f );
    private Stroke boundsStroke = new BasicStroke( 2f );

    public void paint( Graphics2D g2, Point location, Datum[] data ) {

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

        // Draw the readout values
        g2.setFont( font );
        g2.setColor( Color.WHITE );
        g2.fillRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );
        g2.setColor( Color.black );
        g2.drawRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );
        g2.setColor( Color.black );
        for( int i = 0; i < data.length; i++ ) {
            Datum datum = data[i];

            String displayString = datum.getName() + ": " + datum.getValue();
            g2.drawString( displayString, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 + leading * i );
        }
    }

    public boolean contains( Point point ) {
        return bounds == null ? false : bounds.contains( point );
    }

    void doPaint( Graphics2D g, Point loc, Datum[] data ) {
        paint( g, loc, data );
    }

    public RoundRectangle2D.Double getBounds() {
        return bounds;
    }

    //
    // Inner classes
    //
    public static class Datum {
        private String name;
        private Object value;

        public Datum( String name, Object value ) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }

    public static class TargetGraphic implements Graphic {
        ArrayList data = new ArrayList();
        Point location;
        TargetReadoutTool mgn = new TargetReadoutTool();

        public TargetGraphic( Point location ) {
            this.location = location;
        }

        public void addDatum( String name, Object value ) {
            data.add( new Datum( name, value ) );
        }

        public void paint( Graphics2D g ) {
            mgn.doPaint( g, location, (Datum[])data.toArray( new Datum[0] ) );
        }
    }
}
