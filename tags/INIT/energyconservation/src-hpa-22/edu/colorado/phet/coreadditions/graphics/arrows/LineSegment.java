/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics.arrows;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 3:22:27 PM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class LineSegment {
    Stroke stroke;
    Color c;
    Shape shape;
    int x, y, x2, y2;

    public LineSegment( Stroke stroke, Color c ) {
        this.stroke = stroke;
        this.c = c;
    }

    public void paint( Graphics2D g, int x, int y, int x2, int y2 ) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        g.setColor( c );
        g.setStroke( stroke );
        g.drawLine( x, y, x2, y2 );
    }

    public boolean contains( Point point ) {
        return stroke.createStrokedShape( new Line2D.Double( x, y, x2, y2 ) ).contains( point );
    }
}
