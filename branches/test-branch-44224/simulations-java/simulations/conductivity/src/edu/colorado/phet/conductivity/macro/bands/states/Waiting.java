// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.bands.states;

import java.util.Random;

import edu.colorado.phet.conductivity.macro.bands.*;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands.states:
//            MoveTo, Speed

public class Waiting
        implements BandParticleState {

    public Waiting() {
        random = new Random( 0L );
    }

    public BandParticleState stepInTime( final BandParticle particle, double d ) {
        int i = particle.getEnergyLevel().indexOf( particle );
        if ( i == -1 ) {
            return this;
        }
        double d1 = particle.getEnergyLevel().getBand().getVoltage();
        if ( d1 == 0.0D && ( particle.getEnergyLevel().canConduct() && !isPhotoConductor( particle ) || canPhotoFall( particle ) ) ) {
            EnergyLevel energylevel = particle.getEnergyLevel().getBand().getBandSet().getLowerLevel( particle.getEnergyLevel() );
            if ( energylevel != null ) {
                EnergyCell energycell = energylevel.cellAt( i );
                boolean flag = energycell.isOccupied();
                if ( particle.getEnergyLevel().getBand() == energylevel.getBand() ) {
                    ;
                }
                if ( !flag ) {
                    return particle.moveTo( energycell, new Speed() {

                        public double getSpeed() {
                            return 0.0078000000000000005D;
                        }

                    } );
                }
            }
        }
        if ( d1 == 0.0D ) {
            return this;
        }
        int j = i - 1;
        if ( j == -1 ) {
            j = 1;
        }
        EnergyCell energycell1 = particle.getEnergyLevel().cellAt( j );
        if ( !energycell1.hasOwner() && particle.getEnergyLevel().canConduct() && random.nextInt( 100 ) <= 100 ) {
            MoveTo moveto = particle.moveTo( energycell1, new Speed() {

                public double getSpeed() {
                    return Math.abs( particle.getEnergyLevel().getBand().getSpeed() );
                }

            } );
            moveto.stepInTime( particle, d );
            return moveto;
        }
        else {
            return this;
        }
    }

    private boolean canPhotoFall( BandParticle bandparticle ) {
        if ( !isPhotoConductor( bandparticle ) ) {
            return false;
        }
        else {
            PhotoconductorBandSet photoconductorbandset = (PhotoconductorBandSet) bandparticle.getEnergyLevel().getBand().getBandSet();
            return !photoconductorbandset.isLightOn();
        }
    }

    private boolean isPhotoConductor( BandParticle bandparticle ) {
        return bandparticle.getEnergyLevel().getBand().getBandSet() instanceof PhotoconductorBandSet;
    }

    private Random random;
}
