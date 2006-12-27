package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.ParticleFactory;
import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;

import java.awt.*;
import java.util.Random;

/**
 * A Default factory for creating electrons.
 */
public class ShockElectronFactory implements ParticleFactory {
    static Random rand = new Random();
    double charge;
    Rectangle bounds;
    Propagator init;

    /**
     * Creates charge -1 particles with no velocity within the specified bounds.
     */
    public ShockElectronFactory( int x, int y, int width, int height, Propagator init ) {
        this( new Rectangle( x, y, width, height ), init );
    }

    public ShockElectronFactory( Rectangle bounds, Propagator init ) {
        this.bounds = bounds;
        this.charge = -1;
        this.init = init;
        if( init == null ) {
            throw new RuntimeException( "Null Propagator" );
        }
    }

    public Particle newParticle() {
        ShockElectron p = new ShockElectron( init );
        int x = rand.nextInt( bounds.width ) + bounds.x;
        int y = rand.nextInt( bounds.height ) + bounds.y;
        p.setPosition( new DoublePoint( x, y ) );
        p.setCharge( charge );
        //p.setVelocity(new DoublePoint(0,-1));
        //edu.colorado.phet.common.util.Debug.traceln("Adding: "+p);
//  	if (init instanceof ParticleContainer)
//  	    ((ParticleContainer)init).add(p);
        return p;
    }
}
