package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.paint.Painter;
import edu.colorado.phet.balloons.common.paint.ParticlePainter;
import edu.colorado.phet.balloons.common.paint.ParticlePainterAdapter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;

import java.awt.*;

public class Dipole implements Painter {
    Charge p;
    Charge m;
    ParticlePainterAdapter ppa;
    ParticlePainterAdapter ppa2;

    Charge p() {
        return p;
    }

    Charge m() {
        return m;
    }

    public Dipole( int x, int y, int x2, int y2, ParticlePainter plus, ParticlePainter minus ) {
        p = new Charge();
        p.setCharge( 1 );
        m = new Charge();
        m.setCharge( -1 );
        //System.err.println("x="+x+", y="+y);
        p.setPosition( new DoublePoint( x, y ) );
        p.setInitialPosition( new DoublePoint( x, y ) );
        m.setPosition( new DoublePoint( x2, y2 ) );
        m.setInitialPosition( new DoublePoint( x2, y2 ) );

        ppa = new ParticlePainterAdapter( plus, p );
        ppa2 = new ParticlePainterAdapter( minus, m );
        p.setNeutral( true );
        m.setNeutral( true );
    }

    public void paint( Graphics2D g ) {
        //System.err.println(g);
        ppa.paint( g );
        //System.err.println(ppa);
        ppa2.paint( g );
    }
}
