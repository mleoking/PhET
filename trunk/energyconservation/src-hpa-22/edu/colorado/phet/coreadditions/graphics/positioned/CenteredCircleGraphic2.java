package edu.colorado.phet.coreadditions.graphics.positioned;

import edu.colorado.phet.common.view.graphics.positioned.PositionedGraphic;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 6:14:53 PM
 * To change this template use Options | File Templates.
 */
public class CenteredCircleGraphic2 implements PositionedGraphic {
    int radius;
    Color c;

    public CenteredCircleGraphic2( int radius, Color c ) {
        this.radius = radius;
        this.c = c;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius( int radius ) {
        this.radius = radius;
    }

    public void paint( Graphics2D g, int x, int y ) {
        int left = x - radius;
        int top = y - radius;
        g.setColor( c );
        g.fillOval( left, top, radius * 2, radius * 2 );
    }

    public boolean containsPoint( int x, int y, int circleX0, int circleY0 ) {
        double dist = new Point( x, y ).distance( circleX0, circleY0 );
        return dist <= radius;
    }

    public void setColor( Color color ) {
        this.c = color;
    }

}


