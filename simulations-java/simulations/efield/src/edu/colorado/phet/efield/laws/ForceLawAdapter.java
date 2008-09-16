// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.laws;

import edu.colorado.phet.efield.core.ParticleList;
import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Law;
import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.System2D;

// Referenced classes of package edu.colorado.phet.efield.laws:
//            ForceLaw

public class ForceLawAdapter extends ParticleList
        implements Law {

    public ForceLawAdapter( Particle aparticle[], MyForceLaw forcelaw ) {
        addAll( aparticle );
        law = forcelaw;
        if ( forcelaw == null ) {
            throw new RuntimeException( "Law is null." );
        }
        else {
            return;
        }
    }

    public String toString() {
        return getClass().getName() + ", law=" + law + ", numCharges=" + numParticles();
    }

    public void iterate( double d, System2D system2d ) {
        for ( int i = 0; i < numParticles(); i++ ) {
            DoublePoint doublepoint = new DoublePoint();
            for ( int j = 0; j < numParticles(); j++ ) {
                if ( i != j ) {
                    doublepoint = doublepoint.add( law.getForce( particleAt( j ), particleAt( i ) ) );
                }
            }

            double d1 = particleAt( i ).getMass();
            DoublePoint doublepoint1 = doublepoint.multiply( 1.0D / d1 );
            DoublePoint doublepoint2 = particleAt( i ).getAcceleration();
            DoublePoint doublepoint3 = doublepoint2.add( doublepoint1 );
            particleAt( i ).setAcceleration( doublepoint3 );
        }

    }

    MyForceLaw law;
}
