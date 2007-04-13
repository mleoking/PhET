package edu.colorado.phet.balloon;


import phet.paint.LayeredPainter;
import phet.phys2d.DoublePoint;
import phet.phys2d.Propagator;
import phet.phys2d.NullPropagator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Moves charges back to their homes, and returns the balloons too.
 */
public class Reset implements ActionListener {
    Vector ch = new Vector();
    Vector b = new Vector();
    Component paint;
    LayeredPainter lp;
    int chargeLevel;
    ChargeMover chm;

    public void setChargeMover( ChargeMover chm ) {
        this.chm = chm;
    }

    public Reset( Component paint, LayeredPainter lp, int chargeLevel ) {
        this.chargeLevel = chargeLevel;
        this.lp = lp;
        this.paint = paint;
    }

    public void addBalloonPainter( BalloonPainter bp ) {
        b.add( bp );
    }

    public void addCharge( Charge c ) {
        ch.add( c );
    }

    public static final Propagator NULL = new NullPropagator();

    public void reset() {
        for( int i = 0; i < b.size(); i++ ) {
            BalloonPainter bp = ( (BalloonPainter)b.get( i ) );
            Charge[] cx = bp.getCharges();

            for( int k = 0; k < cx.length; k++ ) {
                Charge chg = cx[k];
                chg.setPosition( chg.getInitialPosition() );
                chg.setVelocity( new DoublePoint() );
                chg.setAcceleration( new DoublePoint() );
                chg.setNeutral( true );
                lp.addPainter( chg.getPainter(), chg.getLevel() );
                chg.setPropagator( NULL );
                chm.addParticle( chg );
            }
            Point init = bp.getInitialPosition();
            bp.setPosition( init );
            bp.removePainters();
            bp.setVelocity( new DoublePoint() );
            //bp.setAcceleration(new DoublePoint());
        }
        paint.repaint();
    }

    public void actionPerformed( ActionEvent ae ) {
        reset();
    }
}
