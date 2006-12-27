// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.bands;

import edu.colorado.phet.semiconductor.macro.MacroSystem;

import java.util.Random;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            DefaultBandSet, Band, EnergyLevel

public class PhotoconductorBandSet extends DefaultBandSet {

    public PhotoconductorBandSet( MacroSystem macrosystem, double d ) {
        super( macrosystem, d );
        lightOn = false;
        random = new Random( 0L );
        excitedCount = 0;
        double d1 = d;
        double d2 = d1 * 1.2D;
        double d3 = 0.14000000000000001D + inset * 2D;
        int i = (int)( d3 / d2 );
        double d4 = d1 * 0.51000000000000001D;
        upper.addLevels( 0.5D, 0.69999999999999996D, highBandNumLevels, 0.10000000000000001D - inset, d3, i, d4, 0.033333333333333333D );
        lowband.addLevels( lowBandY1, lowBandY2, lowNumLevels, 0.10000000000000001D - inset, d3, i, d4, 0.033333333333333333D );
    }

    public void initParticles() {
        super.initParticles();
        for( int i = 0; i <= getFermiLevel(); i++ ) {
            super.fillLevel( getLowerBand().energyLevelAt( i ) );
        }

    }

    public int getFermiLevel() {
        return getLowerBand().numEnergyLevels() - 1;
    }

    public double desiredSpeedToActualSpeed( double d ) {
        int i = getUpperBand().numParticles();
        if( i == 0 ) {
            return 0.0D;
        }
        else {
            return super.desiredSpeedToActualSpeed( d );
        }
    }

    public double voltageChanged( double d, double d1 ) {
        return super.voltageChanged( d, d1 );
    }

    public void photonHit() {
        if( !lightOn ) {
            return;
        }
        excitedCount++;
        if( excitedCount == 0 ) {
            setConductionCount( 0, 0 );
        }
        else if( excitedCount == 1 ) {
            setConductionCount( 1, 0 );
        }
        else if( excitedCount == 2 ) {
            setConductionCount( 1, 1 );
        }
        else if( excitedCount == 3 ) {
            setConductionCount( 2, 1 );
        }
        else if( excitedCount == 4 ) {
            setConductionCount( 2, 2 );
        }
        super.photonGotAbsorbed();
    }

    private void setConductionCount( int i, int j ) {
        int k = getFermiLevel();
        Band band = getLowerBand();
        EnergyLevel energylevel = upper.energyLevelAt( 0 );
        energylevel.setCanConduct( true );
        EnergyLevel energylevel1 = upper.energyLevelAt( 1 );
        energylevel1.setCanConduct( true );
        moveParticles( energylevel, i );
        moveParticles( energylevel1, j );
    }

    EnergyLevel getDonorLevel() {
        int i = getFermiLevel();
        for( int j = i; j >= 0; j-- ) {
            if( lowband.energyLevelAt( j ).numParticles() == 2 ) {
                return lowband.energyLevelAt( j );
            }
        }

        for( int k = i; k >= 0; k-- ) {
            if( lowband.energyLevelAt( k ).numParticles() == 1 ) {
                return lowband.energyLevelAt( k );
            }
        }

        throw new RuntimeException( "No Donor level." );
    }

    EnergyLevel getAcceptor() {
        int i = getFermiLevel();
        for( int j = i; j >= 0; j-- ) {
            if( lowband.energyLevelAt( j ).numParticles() < 2 ) {
                return lowband.energyLevelAt( j );
            }
        }

        throw new RuntimeException( "No Donor level." );
    }

    protected void moveParticles( EnergyLevel energylevel, int i ) {
        int j = 0;
        while( energylevel.numParticles() > i ) {
            EnergyLevel energylevel1 = getAcceptor();
            moveParticle( energylevel, energylevel1 );
            if( ++j > 100 ) {
                throw new RuntimeException( "Loop broke." );
            }
        }
        while( energylevel.numParticles() < i ) {
            EnergyLevel energylevel2 = getDonorLevel();
            moveParticle( energylevel2, energylevel );
            if( ++j > 100 ) {
                throw new RuntimeException( "Loop broke." );
            }
        }
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public void setFlashlightOn( boolean flag ) {
        lightOn = flag;
        if( !lightOn ) {
            excitedCount = 0;
            setConductionCount( 0, 0 );
        }
        super.photonGotAbsorbed();
    }

    boolean lightOn;
    int excitedCount;
}
