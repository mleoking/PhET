package edu.colorado.phet.batteryvoltage.common.phys2d.propagators;

import java.awt.*;

import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;
import edu.colorado.phet.batteryvoltage.common.phys2d.Propagator;

public class FourBounds implements Propagator {
    NorthBounce n;
    SouthBounce s;
    EastBounce e;
    WestBounce w;
    double x;
    double y;
    double width;
    double height;

    public FourBounds( double x, double y, double width, double height, double distFromWall ) {
        this.n = new NorthBounce( y, distFromWall );
        this.s = new SouthBounce( y + height, distFromWall );
        this.e = new EastBounce( x + width, distFromWall );
        this.w = new WestBounce( x, distFromWall );
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle toRectangle() {
        Rectangle r = new Rectangle( (int) x, (int) y, (int) width, (int) height );
        return r;
    }

    public void propagate( double time, Particle p ) {
        //util.Debug.traceln();
        n.propagate( time, p );
        e.propagate( time, p );
        w.propagate( time, p );
        s.propagate( time, p );
    }
}
