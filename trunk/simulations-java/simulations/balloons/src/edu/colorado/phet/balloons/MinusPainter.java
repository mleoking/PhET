package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.paint.ParticlePainter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;
import edu.colorado.phet.balloons.common.phys2d.Particle;

import java.awt.*;

public class MinusPainter implements ParticlePainter {
    int halfWidth;
    int width;
    Color color;
    int strokeWidth;
    int halfStroke;
    int paint;
    public static final int ALL = 0;
    public static final int NONE = 1;
    public static final int DIFF = 2;

    public MinusPainter( int width, int strokeWidth, Color color ) {
        this.strokeWidth = strokeWidth;
        this.width = width;
        this.color = color;
        this.halfWidth = width / 2;
        this.halfStroke = strokeWidth / 2;
    }

    public void setPaint( int x ) {
        this.paint = x;
    }

    public boolean ok( Charge p ) {
        return !p.addsToNeutral();
    }

    public void paint( Charge p, int x, int y, Graphics2D g ) {
        if( paint == NONE ) {
            return;
        }
        if( paint == ALL || ( paint == DIFF && ok( p ) ) ) {
            paintAt( x, y, g );
        }
    }

    public void paintAt( int x, int y, Graphics2D g ) {
        //System.err.println("Paint="+paint);
//        g.setColor( oval );
//        g.fillOval( x - halfWidth, y - halfWidth, width, width );
        g.setColor( color );
        //Point topLeft=new Point(x-halfStroke,y-halfWidth);
        Point leftUp = new Point( x - halfWidth, y - halfStroke );
        //g.fillRect(topLeft.x,topLeft.y,strokeWidth,width);
        g.fillRect( leftUp.x, leftUp.y, width, strokeWidth );
    }

    public void paint( Particle p, Graphics2D g ) {
        DoublePoint pos = p.getPosition();
        int x = (int)pos.getX();
        int y = (int)pos.getY();
        paint( (Charge)p, x, y, g );
    }

}
