// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.bands;

import java.util.ArrayList;

import edu.colorado.phet.conductivity.macro.MacroSystem;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            DefaultBandSet, Band, EnergyLevel

public class ConductorBandSet extends DefaultBandSet {

    public ConductorBandSet( MacroSystem macrosystem, double d ) {
        super( macrosystem, d );
        double d1 = d;
        double d2 = d1 * 1.2D;
        double d3 = 0.14000000000000001D + inset * 2D;
        int i = (int) ( d3 / d2 );
        double d4 = d1 * 0.51000000000000001D;
        lowband.addLevels( lowBandY1, lowBandY2, lowNumLevels, 0.10000000000000001D - inset, d3, i, d4, 0.033333333333333333D );
        upper.addLevels( 0.45000000000000001D, 0.65000000000000002D, highBandNumLevels, 0.10000000000000001D - inset, d3, i, d4, 0.033333333333333333D );
    }

    public void initParticles() {
        super.initParticles();
        for ( int i = 0; i <= getFermiLevel(); i++ ) {
            super.fillLevel( getLowerBand().energyLevelAt( i ) );
        }

    }

    public int getFermiLevel() {
        int i = super.getLowerBand().numEnergyLevels() / 2;
        return i;
    }

    public double voltageChanged( double d, double d1 ) {
        int i = getFermiLevel();
        Band band = getLowerBand();
        EnergyLevel energylevel = band.energyLevelAt( i + 1 );
        EnergyLevel energylevel1 = band.energyLevelAt( i + 2 );
        energylevel.setCanConduct( true );
        energylevel1.setCanConduct( true );
        if ( d == 0.0D ) {
            setConductionCount( 0, 0 );
        }
        else if ( d > 0.0D && d <= 0.5D ) {
            setConductionCount( 1, 0 );
        }
        else if ( ( d > 0.5D ) & ( d <= 1.0D ) ) {
            setConductionCount( 1, 1 );
        }
        else if ( d > 1.0D && d <= 1.5D ) {
            setConductionCount( 2, 1 );
        }
        else if ( d > 1.5D && d <= 2D ) {
            setConductionCount( 2, 2 );
        }
        return super.voltageChanged( d, d1 );
    }

    private void setConductionCount( int i, int j ) {
        int k = getFermiLevel();
        Band band = getLowerBand();
        ArrayList arraylist = new ArrayList();
        arraylist.add( band.energyLevelAt( k ) );
        EnergyLevel energylevel = band.energyLevelAt( k + 1 );
        EnergyLevel energylevel1 = band.energyLevelAt( k + 2 );
        moveParticles( energylevel, i );
        moveParticles( energylevel1, j );
    }

    EnergyLevel getDonorLevel() {
        int i = getFermiLevel();
        for ( int j = i; j >= 0; j-- ) {
            if ( lowband.energyLevelAt( j ).numParticles() > 0 ) {
                return lowband.energyLevelAt( j );
            }
        }

        throw new RuntimeException( "No Donor level." );
    }

    EnergyLevel getAcceptor() {
        int i = getFermiLevel();
        for ( int j = i; j >= 0; j-- ) {
            if ( lowband.energyLevelAt( j ).numParticles() < 2 ) {
                return lowband.energyLevelAt( j );
            }
        }

        throw new RuntimeException( "No Donor level." );
    }

    protected void moveParticles( EnergyLevel energylevel, int i ) {
        int j = 0;
        while ( energylevel.numParticles() > i ) {
            EnergyLevel energylevel1 = getAcceptor();
            moveParticle( energylevel, energylevel1 );
            if ( ++j > 100 ) {
                throw new RuntimeException( "Loop broke." );
            }
        }
        while ( energylevel.numParticles() < i ) {
            EnergyLevel energylevel2 = getDonorLevel();
            moveParticle( energylevel2, energylevel );
            if ( ++j > 100 ) {
                throw new RuntimeException( "Loop broke." );
            }
        }
    }
}
