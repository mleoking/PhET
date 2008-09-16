package edu.colorado.phet.efield.electron.electricField;

import java.util.Vector;

import edu.colorado.phet.efield.electron.core.ParticleContainer;
import edu.colorado.phet.efield.electron.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.electron.phys2d_efield.Particle;

public class ChargeFieldSource implements ElectricFieldSource {
    ParticleContainer pc;
    double k;
    double max;
    Vector ignore;

    public ChargeFieldSource( ParticleContainer pc, double k, double max ) {
        this.pc = pc;
        this.k = k;
        this.max = max;
        ignore = new Vector();
    }

    public boolean isIgnoring( Particle p ) {
        return ignore.contains( p );
    }

    public void removeFromIgnore( Particle p ) {
        while ( ignore.contains( p ) ) {
            ignore.remove( p );
        }
    }

    public void ignore( Particle p ) {
        ignore.add( p );
    }

    public DoublePoint getField( double x, double y ) {
        DoublePoint field = new DoublePoint();
        DoublePoint pos = new DoublePoint( x, y );
        for ( int i = 0; i < pc.numParticles(); i++ ) {
            Particle p = pc.particleAt( i );
            if ( !ignore.contains( p ) ) {
                DoublePoint f = ( getField( p, pos ) );
                field = field.add( f );
            }
            //util.Debug.traceln("Adding field: "+f);
        }
        //util.Debug.traceln("Got field: "+field);
        return field;
    }

    public DoublePoint getField( Particle p, DoublePoint test ) {
        double q = p.getCharge();
        DoublePoint pos = p.getPosition();
        DoublePoint r = test.subtract( pos );
        double dist = r.getLength();
        if ( dist == 0 ) {
            return new DoublePoint();
        }

        double scale = Math.pow( dist, -3 ) * k * q;
        r = r.multiply( scale );
        double mag = r.getLength();
        if ( mag > max ) {
            double rescale = max / mag;
            r = r.multiply( rescale );
        }
        return r;
    }
}
