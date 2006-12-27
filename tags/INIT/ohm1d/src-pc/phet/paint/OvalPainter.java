package phet.paint;

import java.awt.*;

public class OvalPainter implements Painter {
    Color c;
    int x;
    int y;
    int width;
    int height;
    Stroke s;

    public OvalPainter( Color c, Stroke s, int x, int y, int width, int height ) {
        this.c = c;
        this.s = s;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String toString() {
        return getClass().getName() + ", Color=" + c + ", Stroke=" + s + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height;
    }

    public void paint( Graphics2D g ) {
        g.setColor( c );
        g.setStroke( s );
        g.drawOval( x, y, width, height );
    }
}
