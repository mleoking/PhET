/*PhET, 2004.*/
package edu.colorado.phet.movingman.common.arrows;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 9:58:05 AM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class Arrowhead {
    Color c;
    int width;
    int height;
    Polygon poly = new Polygon();

    public Arrowhead( Color c, int width, int height ) {
        this.c = c;
        this.width = width;
        this.height = height;
    }

    public void paint( Graphics2D g, int x0, int y0, int xdir, int ydir ) {
        paint( g, new Vector2D.Double( x0, y0 ), new Vector2D.Double( xdir, ydir ) );
    }

    public void paint( Graphics2D g, Vector2D root, Vector2D direction ) {
        poly.reset();
        direction.normalize();

        AbstractVector2D normal = direction.getNormalVector();
        AbstractVector2D lefty = root.getAddedInstance( normal.getScaledInstance( width / 2 ) );
        poly.addPoint( (int)lefty.getX(), (int)lefty.getY() );
        AbstractVector2D tippy = root.getAddedInstance( direction.getScaledInstance( height ) );
        poly.addPoint( (int)tippy.getX(), (int)tippy.getY() );
        AbstractVector2D righty = root.getAddedInstance( normal.getScaledInstance( -width / 2 ) );
        poly.addPoint( (int)righty.getX(), (int)righty.getY() );

        g.setColor( c );
        g.fill( poly );
    }

}
