package edu.colorado.phet.ec2.elements.history;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.positioned.PositionedGraphic;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 6:14:53 PM
 * To change this template use Options | File Templates.
 */
public class CenteredCircleGraphic3 implements PositionedGraphic {
    int radius;
    Color c;

    public CenteredCircleGraphic3( int radius, Color c ) {
        this.radius = radius;
        this.c = c;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius( int radius ) {
        this.radius = radius;
    }

    public Graphic createGraphic( int x, int y ) {
        final int left = x - radius;
        final int top = y - radius;
        final int rad = radius * 2;
        return new Graphic() {
            public void paint( Graphics2D graphics2D ) {
                if( graphics2D == null ) {
                    return;///TODO this is a hack.
                }
                graphics2D.setColor( c );
                graphics2D.fillOval( left, top, rad, rad );
            }
        };
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



