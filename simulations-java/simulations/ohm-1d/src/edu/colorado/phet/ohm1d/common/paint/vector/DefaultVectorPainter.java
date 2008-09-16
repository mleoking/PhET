package edu.colorado.phet.ohm1d.common.paint.vector;

import java.awt.*;

public class DefaultVectorPainter implements VectorPainter {
    Color c;
    Stroke stroke;
    double theta;//arrowtip angle.
    double tipLength;

    public DefaultVectorPainter( Color c, Stroke stroke ) {
        this( c, stroke, Math.PI / 8, 10 );
    }

    public DefaultVectorPainter( Color c, Stroke stroke, double theta, double tipLength )//the
    {
        this.c = c;
        this.stroke = stroke;
        this.theta = theta;
        this.tipLength = tipLength;
    }

    public void paint( Graphics2D g, int x, int y, int dx, int dy ) {
        g.setStroke( stroke );
        g.setColor( c );
        g.drawLine( x, y, x + dx, y + dy );
        /**Draw the arrowhead.*/
        if ( dx == 0 && dy == 0 ) {
            return;
        }
        double phi = Math.atan2( dx, dy );
        double phi1 = phi + theta;
        double phi2 = phi - theta;
        double x1 = x + dx - tipLength * Math.sin( phi1 );
        double y1 = y + dy - tipLength * Math.cos( phi1 );

        double x2 = x + dx - tipLength * Math.sin( phi2 );
        double y2 = y + dy - tipLength * Math.cos( phi2 );

        g.drawLine( x + dx, y + dy, (int) x1, (int) y1 );
        g.drawLine( x + dx, y + dy, (int) x2, (int) y2 );
    }
}
