// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.bands;

import edu.colorado.phet.semiconductor.macro.MacroSystem;

import java.util.Random;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            DefaultBandSet, Band, EnergyLevel

public class PhotoconductorBandSet extends DefaultBandSet {

    boolean lightOn;
    int excitedCount;

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
//        excitedCount++;
//        if( excitedCount == 0 ) {
//            setConductionCount( 0, 0 );
//        }
//        else if( excitedCount == 1 ) {
//            setConductionCount( 1, 0 );
//        }
//        else if( excitedCount == 2 ) {
//            setConductionCount( 1, 1 );
//        }
//        else if( excitedCount == 3 ) {
//            setConductionCount( 2, 1 );
//        }
//        else if( excitedCount == 4 ) {
//            setConductionCount( 2, 2 );
//        }

        moveUp();

        super.photonGotAbsorbed();
    }

    private EnergyLevel getUpperBand( int i ) {
        return upper.energyLevelAt( i );
    }

    private EnergyLevel getLowBandFromTop( int i ) {
        return lowband.energyLevelAt( lowband.numEnergyLevels() - 1 - i );
    }

    private void moveDown() {
        moveParticle( getLowBandFromTop( 0 ), getUpperBand( 1 ), true );
        moveParticle( getLowBandFromTop( 1 ), getUpperBand( 0 ), true );

        moveParticle( getLowBandFromTop( 2 ), getLowBandFromTop( 0 ), true );
        moveParticle( getLowBandFromTop( 3 ), getLowBandFromTop( 1 ), true );
        setConducting( false );
    }

    private void moveUp() {
        moveParticle( getLowBandFromTop( 0 ), getUpperBand( 1 ) );
        moveParticle( getLowBandFromTop( 1 ), getUpperBand( 0 ) );

        moveParticle( getLowBandFromTop( 2 ), getLowBandFromTop( 0 ) );
        moveParticle( getLowBandFromTop( 3 ), getLowBandFromTop( 1 ) );
        setConducting( true );
    }

    private void setConducting( boolean flag ) {
        getLowBandFromTop( 0 ).setCanConduct( flag );
        getLowBandFromTop( 1 ).setCanConduct( flag );
        getUpperBand( 0 ).setCanConduct( flag );
        getUpperBand( 1 ).setCanConduct( flag );
    }

//    private void setConductionCount( int i, int j ) {
//        System.out.println( "PhotoconductorBandSet.setConductionCount" );
////        int k = getFermiLevel();
////        Band band = getLowerBand();
//
//        EnergyLevel a = lowband.energyLevelAt( lowband.numEnergyLevels() - 1 );
//        EnergyLevel b = lowband.energyLevelAt( lowband.numEnergyLevels() - 1 - 1 );
//        EnergyLevel c = lowband.energyLevelAt( lowband.numEnergyLevels() - 1 - 2 );
//        EnergyLevel d = lowband.energyLevelAt( lowband.numEnergyLevels() - 1 - 3 );
//        if( i == 0 && j == 0 ) {
//            moveParticle( a, c );
//            moveParticle( a, d );
//            moveParticle( b, c );
//            moveParticle( b, d );
//        }
//
//        EnergyLevel level0 = upper.energyLevelAt( 0 );
//        level0.setCanConduct( true );
//        EnergyLevel level1 = upper.energyLevelAt( 1 );
//        level1.setCanConduct( true );
//        moveParticles( level0, i );
//        moveParticles( level1, j );
//
//        try {
//            int ijSum = i + j;
//            if( ijSum >= 1 ) {
//                moveParticle( c, a );
//            }
//            if( ijSum >= 2 ) {
//                moveParticle( d, a );
//            }
//            if( ijSum >= 3 ) {
//                moveParticle( c, b );
//            }
//            if( ijSum >= 4 ) {
//                moveParticle( d, b );
//            }
//
//        }
//        catch( RuntimeException ru ) {
//            ru.printStackTrace();
//        }
//
//        if( i == 0 && j == 0 ) {
//            a.setCanConduct( false );
//            b.setCanConduct( false );
//            c.setCanConduct( false );
//            d.setCanConduct( false );
//        }
//        else {
//            a.setCanConduct( true );
//            b.setCanConduct( true );
//        }
//    }

    public int topFreeLevel() {
        return lowband.numEnergyLevels() - 1 - 3;
    }

    EnergyLevel getDonorLevel() {
        int fermiLevel = getFermiLevel();
        for( int i = fermiLevel; i >= topFreeLevel(); i-- ) {
            if( lowband.energyLevelAt( i ).numParticles() == 2 ) {
                return lowband.energyLevelAt( i );
            }
        }

        for( int i = fermiLevel; i >= topFreeLevel(); i-- ) {
            if( lowband.energyLevelAt( i ).numParticles() == 1 ) {
                return lowband.energyLevelAt( i );
            }
        }

        throw new RuntimeException( "No Donor level." );
    }

    EnergyLevel getAcceptor() {
        int fermiLevel = getFermiLevel();
        for( int i = fermiLevel; i >= fermiLevel; i-- ) {
            if( lowband.energyLevelAt( i ).numParticles() < 2 ) {
                return lowband.energyLevelAt( i );
            }
        }

        throw new RuntimeException( "No Donor level." );
    }

    protected void moveParticles( EnergyLevel level, int desiredNumberElectrons ) {
        int j = 0;
        while( level.numParticles() > desiredNumberElectrons ) {
            EnergyLevel dst = getAcceptor();
            moveParticle( level, dst );
            if( ++j > 100 ) {
                throw new RuntimeException( "Loop broke." );
            }
        }
        while( level.numParticles() < desiredNumberElectrons ) {
            EnergyLevel src = getDonorLevel();
            moveParticle( src, level );
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
//            setConductionCount( 0, 0 );
            moveDown();
        }
        super.photonGotAbsorbed();
    }


}
