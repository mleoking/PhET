package edu.colorado.phet.signalcircuit.paint.vector;

import edu.colorado.phet.signalcircuit.paint.Painter;

import java.awt.*;

public class VectorPainterAdapter implements Painter {
    VectorPainter vp;
    int x;
    int y;
    int dx;
    int dy;

    public VectorPainterAdapter( VectorPainter vp ) {
        this.vp = vp;
    }

    public void setArrow( int x, int y, int dx, int dy ) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void paint( Graphics2D g ) {
        vp.paint( g, x, y, dx, dy );
    }
}
