package electron.paint;

import java.awt.*;

public class StrokedRectanglePainter implements Painter {
    int x;
    int y;
    int width;
    int height;
    Color c;
    Stroke stroke;

    public StrokedRectanglePainter( int x, int y, int width, int height, Stroke stroke, Color c ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.stroke = stroke;
        this.c = c;
    }

    public void paint( Graphics2D g ) {
        g.setColor( c );
        g.setStroke( stroke );
        g.draw( new Rectangle( x, y, width, height ) );
    }
}
