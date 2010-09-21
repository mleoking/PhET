package edu.colorado.phet.balloons;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

import edu.colorado.phet.balloons.common.paint.LayeredPainter;
import edu.colorado.phet.balloons.common.phys2d.Particle;

/**
 * Decides whether charges should start moving to the edu.colorado.phet.balloon.
 */
public class ChargeMover implements BalloonDragListener {
    Vector v = new Vector();
    MoveToBalloon mtb;
    LayeredPainter lp;

    public ChargeMover( MoveToBalloon mtb, LayeredPainter lp ) {
        this.mtb = mtb;
        this.lp = lp;
    }

    public void addParticle( Particle p ) {
        v.add( p );
    }

    public void balloonDragged( BalloonPainter pt ) {
        Point pos = pt.getPosition();
        BufferedImage im = pt.getImage();
        int yinset = 10;
        Rectangle region = new Rectangle( pos.x, pos.y - yinset * 2, 15, im.getHeight() - yinset );
        for ( int i = 0; i < v.size(); i++ ) {
            //System.err.println("i="+i);
            Charge p = (Charge) v.get( i );
            double positionX = p.getPosition().getX();
            double positionY = p.getPosition().getY();
            if ( region.contains( new Point( (int) positionX, (int) positionY ) ) ) {
                /**Set the painter to be on top.*/
                lp.removePainter( p.getPainter(), p.getLevel() );
                lp.addPainter( p.getPainter(), 100 );
                p.setPainter( p.getPainter(), 100 );
                //System.err.println("Moving: "+p);
                p.setPropagator( mtb );
                p.setNeutral( false );
                mtb.setTarget( pt );
                //System.err.println("setPropagator: "+mtb);
                v.remove( p );
            }
        }
    }
}



