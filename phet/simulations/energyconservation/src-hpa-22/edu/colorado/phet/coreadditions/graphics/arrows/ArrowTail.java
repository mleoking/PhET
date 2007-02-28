/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics.arrows;

import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 3:22:27 PM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class ArrowTail {
    Color c;
    Shape shape;
    int x, y, x2, y2;
    private double width;
    private double halfWidth;
    private Polygon polygon;

    public ArrowTail( double width, Color c ) {
        this.width = width;
        this.c = c;
        this.halfWidth = width / 2;
    }

    public void paint( Graphics2D g, int x, int y, int x2, int y2 ) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        g.setColor( c );

        PhetVector vector = new PhetVector( x2 - x, y2 - y );
        PhetVector norm = vector.getNormalVector().getScaledInstance( halfWidth );
        PhetVector a = norm.getAddedInstance( x, y );
        polygon = new Polygon();
        polygon.addPoint( (int)a.getX(), (int)a.getY() );
        PhetVector b = a.getAddedInstance( vector );
        polygon.addPoint( (int)b.getX(), (int)b.getY() );
        PhetVector c = b.getAddedInstance( norm.getScaledInstance( -2 ) );
        polygon.addPoint( (int)c.getX(), (int)c.getY() );
        PhetVector d = a.getAddedInstance( norm.getScaledInstance( -2 ) );
        polygon.addPoint( (int)d.getX(), (int)d.getY() );

        g.fill( polygon );
    }

    public boolean contains( Point point ) {
        if( polygon == null ) {
            return false;
        }
        return polygon.contains( point );
    }
}
