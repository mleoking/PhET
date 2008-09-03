package edu.colorado.phet.signalcircuit.paint;

import java.awt.*;

public class FilledRectanglePainter implements Painter {
    int x;
    int y;
    int width;
    int height;
    Color c;

    public FilledRectanglePainter( int x, int y, int width, int height, Color c ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.c = c;
    }

    public void paint( Graphics2D g ) {
        g.setColor( c );
        g.fillRect( x, y, width, height );
    }
}
