package phet.paint;

import java.awt.*;

public class FilledRectanglePainter implements Painter {
    int x;
    int y;
    int width;
    int height;
    Color c;

    public FilledRectanglePainter( int width, int height, Color c ) {
        this( 0, 0, width, height, c );
    }

    public FilledRectanglePainter( int x, int y, int width, int height, Color c ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.c = c;
    }

    public void setX( int x ) {
        this.x = x;
    }

    public void setY( int y ) {
        this.y = y;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    public void paint( Graphics2D g ) {
        g.setColor( c );
        g.fillRect( x, y, width, height );
    }
}
