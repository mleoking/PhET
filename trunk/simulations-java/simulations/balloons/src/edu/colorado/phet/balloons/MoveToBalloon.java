package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.paint.LayeredPainter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;
import edu.colorado.phet.balloons.common.phys2d.NullPropagator;
import edu.colorado.phet.balloons.common.phys2d.Particle;
import edu.colorado.phet.balloons.common.phys2d.Propagator;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MoveToBalloon implements Propagator {
    BalloonPainter ip;
    double v;
    LayeredPainter lp;
    int level;
    MinusPainter mp;

    public MoveToBalloon( double v, LayeredPainter lp, int level, MinusPainter mp ) {
        this.mp = mp;
        this.v = v;
        this.lp = lp;
        this.level = level;
    }

    public void setTarget( BalloonPainter fip ) {
        this.ip = fip;
    }

    public void propagate( double dt, Particle p ) {
        /*When the particle gets there, set a BalloonLandingPropagator,
         May be easiest to paint particles onto the bufferedImage of the edu.colorado.phet.balloon and move that...
         Otherwise, have to change the painting level (so it's above the edu.colorado.phet.balloon.)*/
        Point topleft = ip.getPosition();
        BufferedImage im = ip.getImage();
        DoublePoint imgCtr = new DoublePoint( topleft.x + im.getWidth() / 2, topleft.y + im.getHeight() / 2 );
        DoublePoint vhat = imgCtr.subtract( p.getPosition() ).normalize().multiply( v * dt );

        DoublePoint newPos = vhat.add( p.getPosition() );
        p.setPosition( newPos );
        double throsh = 60;
        if( newPos.distance( imgCtr ) < throsh ) {

            Point pt = new Point( (int)newPos.getX(), (int)newPos.getY() );
            Point imPos = ip.getPosition();
            Point rel = new Point( pt.x - imPos.x, pt.y - imPos.y );
            Charge ch = (Charge)p;
            ch.setPropagator( new NullPropagator() );
            Stick s = new Stick( ip, rel, ch, mp );
            lp.removePainter( ch.getPainter(), ch.getLevel() );
            ch.setPainter( ch.getPainter(), BalloonsSimulationPanel.CHARGE_LEVEL );
            ip.addPainter( s, ch );
            //lp.addPainter(s,10);
        }
    }
}
