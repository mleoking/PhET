/**
 * Class: Arrow
 * Package: edu.colorado.phet.common.view.graphics.lines
 * Author: Another Guy
 * Date: Jun 17, 2003
 */
package edu.colorado.phet.common.view.graphics.lines;

import java.awt.*;

public class Arrow implements LinePainter {

    private Polygon arrowHead = new Polygon();
    Stroke stroke = new BasicStroke( 1 );
    private Color color = Color.black;

    public Arrow( Color color, Stroke stroke ) {
        this.stroke = stroke;
        this.color = color;
    }

    public void drawLine( Graphics2D g2, int x1, int y1, int x2, int y2 ) {
        if( x1 != x2 || y1 != y2 ) {

            // draw the line
            g2.setStroke( stroke );
            g2.setColor( this.color );
            g2.drawLine( x1, y1, x2, y2 );

            // draw the arrowhead
            int w = 4; // width of arrowhead
            double l = Math.sqrt( ( x2 - x1 ) * ( x2 - x1 ) + ( y2 - y1 ) * ( y2 - y1 ) );
            double px = ( x2 - x1 ) / l;
            double py = ( y2 - y1 ) / l;
            arrowHead.reset();
            arrowHead.addPoint( (int)( x2 + w * px ), (int)( y2 + w * py ) );
            arrowHead.addPoint( (int)( x2 - ( w / 2 ) * py ), (int)( y2 - ( w / 2 ) * px ) );
            arrowHead.addPoint( (int)( x2 + ( w / 2 ) * py ), (int)( y2 + ( w / 2 ) * px ) );
            g2.drawPolygon( arrowHead );
            g2.fillPolygon( arrowHead );
        }
    }
}
