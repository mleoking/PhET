/**
 * Class: TargetReadoutTool
 * Package: edu.colorado.phet.bernoulli.meter
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.view.graphics.BoundedGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class TargetReadoutTool implements BoundedGraphic {
    private RoundRectangle2D.Double bounds;
    private int crosshairRadius = 15;
    private int readoutWidth = 140;
    private int readoutHeight = 50;
    private int leading = 20;
    private Point location = new Point();
    private int width = crosshairRadius * 2 + readoutWidth + 30;
    private int height = readoutHeight + 20;
    private Font font = new Font( "dialog", Font.BOLD, 14 );
    private Stroke crossHairStroke = new BasicStroke( 1f );
    private Stroke holeStroke = new BasicStroke( 2f );
    private int boundsStrokeWidth = 2;
    private Stroke boundsStroke = new BasicStroke( boundsStrokeWidth );
    private ArrayList data = new ArrayList();

    public void paint( Graphics2D g2 ) {
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
        for( int i = 0; i < data.size(); i++ ) {
            String text = (String)data.get( i );
            g2.drawString( text, readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 + leading * i );
        }
    }

    public Rectangle getBounds() {
        if( bounds == null ) {
            return null;
        }
        Rectangle full = bounds.getBounds();
        int expand = boundsStrokeWidth + 1;
        Rectangle tot = new Rectangle( full.x - expand, full.y - expand, full.width + expand * 2, full.height + expand * 2 );
        return tot;
    }

    public void addText( String value ) {
        data.add( value );
    }

    public boolean contains( Point point ) {
        return bounds == null ? false : bounds.contains( point );
    }

    public boolean contains( int x, int y ) {
        return contains( new Point( x, y ) );
    }

    public void setLocation( int x, int y ) {
        this.location.setLocation( x, y );
    }

    public void translate( int dx, int dy ) {
        setLocation( location.x + dx, location.y + dy );
    }

    public Point getPoint() {
        return new Point( location );
    }

    public void clear() {
        this.data.clear();
    }

}
