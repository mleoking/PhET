// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.bands;

import java.util.Random;

import edu.colorado.phet.conductivity.macro.MacroSystem;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            DefaultBandSet, Band, EnergyLevel

public class PhotoconductorBandSet extends DefaultBandSet {
    //    ConductorBandSet conductorBandSet;
    boolean lightOn;
    int excitedCount;
    private MacroSystem macrosystem;
//    private boolean conducting=false;

    public PhotoconductorBandSet( MacroSystem macrosystem, double d ) {
        super( macrosystem, d );
        this.macrosystem = macrosystem;
//        this.conductorBandSet=new ConductorBandSet( macrosystem, d);
        lightOn = false;
        random = new Random( 0L );
        excitedCount = 0;
        double d1 = d;
        double d2 = d1 * 1.2D;
        double d3 = 0.14000000000000001D + inset * 2D;
        int i = (int) ( d3 / d2 );
        double d4 = d1 * 0.51000000000000001D;
        upper.addLevels( 0.5D, 0.69999999999999996D, highBandNumLevels, 0.10000000000000001D - inset, d3, i, d4, 0.033333333333333333D );
        lowband.addLevels( lowBandY1, lowBandY2, lowNumLevels, 0.10000000000000001D - inset, d3, i, d4, 0.033333333333333333D );
        setConducting( false );
    }

    public void stepInTime( double d ) {
        super.stepInTime( d );
        //make sure everything is working.
        macrosystem.doVoltageChanged();
        if ( !lightOn ) {
            excitedCount = 0;
            moveDown();//src,dst,false
            //make sure everything falls down
            moveParticle( getUpperBand( 2 ), getUpperBand( 1 ), false );
            moveParticle( getUpperBand( 1 ), getUpperBand( 0 ), false );
            moveParticle( getUpperBand( 0 ), getLowBandFromTop( 0 ), false );
            moveParticle( getLowBandFromTop( 0 ), getLowBandFromTop( 1 ), false );
            moveParticle( getLowBandFromTop( 1 ), getLowBandFromTop( 2 ), false );
            moveParticle( getLowBandFromTop( 2 ), getLowBandFromTop( 3 ), false );
        }
        else {
//            setConducting( conducting );
        }
//        super.stepInTime( d );
    }

    public void initParticles() {
        super.initParticles();
        for ( int i = 0; i < getLowerBand().numEnergyLevels(); i++ ) {
            super.fillLevel( getLowerBand().energyLevelAt( i ) );
        }
    }

    public double desiredSpeedToActualSpeed( double d ) {
        int i = getUpperBand().numParticles();
        if ( i == 0 ) {
            return 0.0D;
        }
        else {
            return super.desiredSpeedToActualSpeed( d );
        }
    }

    public void photonHit() {
        if ( !lightOn ) {
            return;
        }
        moveUp();
        setConducting( true );
        super.photonGotAbsorbed();
    }

    private EnergyLevel getUpperBand( int i ) {
        return upper.energyLevelAt( i );
    }

    private EnergyLevel getLowBandFromTop( int i ) {
        return lowband.energyLevelAt( lowband.numEnergyLevels() - 1 - i );
    }

    private void moveDown() {
        setConducting( false );
        moveParticle( getLowBandFromTop( 0 ), getUpperBand( 1 ), true );
        moveParticle( getLowBandFromTop( 1 ), getUpperBand( 0 ), true );
//        macrosystem.doVoltageChanged();

    }

    private void moveUp() {
        moveParticle( getLowBandFromTop( 0 ), getUpperBand( 1 ) );
        moveParticle( getLowBandFromTop( 1 ), getUpperBand( 0 ) );
//        macrosystem.doVoltageChanged();
//        setConducting( true );
    }

    private void setConducting( boolean flag ) {
//        this.conducting=flag;
        getLowBandFromTop( 0 ).setCanConduct( flag );
        getLowBandFromTop( 1 ).setCanConduct( flag );
//        getLowBandFromTop( 2 ).setCanConduct( flag );
        getUpperBand( 0 ).setCanConduct( flag );
        getUpperBand( 1 ).setCanConduct( flag );
    }

    public int topFreeLevel() {
        return lowband.numEnergyLevels() - 1 - 3;
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public void setFlashlightOn( boolean flag ) {
        lightOn = flag;
        if ( !lightOn ) {
            excitedCount = 0;
            moveDown();
        }
//        super.photonGotAbsorbed();
    }

    /**
     * ************
     */
    public int getFermiLevel() {
        return super.getLowerBand().numEnergyLevels() / 2;
    }
//

    public double voltageChanged( double voltage, double desiredSpeed ) {
//        super.voltageChanged( d, d1 );
        if ( lightOn && particlesElevated() ) {
//            int i = getFermiLevel();
//            Band band = getLowerBand();
//            EnergyLevel energylevel = band.energyLevelAt( i + 1 );
//            EnergyLevel energylevel1 = band.energyLevelAt( i + 2 );
//            energylevel.setCanConduct( true );
//            energylevel1.setCanConduct( true );
            if ( voltage == 0.0D ) {
                setConductionCount( 0, 0 );
            }
            else if ( voltage > 0.0D && voltage <= 0.5D ) {
                setConductionCount( 1, 0 );
            }
            else if ( ( voltage > 0.5D ) & ( voltage <= 1.0D ) ) {
                setConductionCount( 1, 1 );
            }
            else if ( voltage > 1.0D && voltage <= 1.5D ) {
                setConductionCount( 2, 1 );
            }
            else if ( voltage > 1.5D && voltage <= 2D ) {
                setConductionCount( 2, 2 );
            }
        }
        else {
            setConductionCount( 0, 0 );
        }
        return super.voltageChanged( voltage, desiredSpeed );
    }

    private boolean particlesElevated() {
        return getUpperBand( 0 ).isFull() && getUpperBand( 1 ).isFull();
    }

    private void setConductionCount( int i, int j ) {
        int k = getFermiLevel();
        Band band = getLowerBand();
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
        return null;
//        throw new RuntimeException( "No Donor level." );
    }

    EnergyLevel getAcceptor() {
        int i = getFermiLevel();
        for ( int j = i; j >= 0; j-- ) {
            if ( lowband.energyLevelAt( j ).numParticles() < 2 ) {
                return lowband.energyLevelAt( j );
            }
        }
        return null;
//        throw new RuntimeException( "No Acceptor level." );
    }
//

    protected void moveParticles( EnergyLevel energylevel, int i ) {
        int j = 0;
        while ( energylevel.numParticles() > i ) {
            EnergyLevel dst = getAcceptor();
            if ( dst == null ) {
                break;
            }
            moveParticle( energylevel, dst );
            if ( ++j > 100 ) {
                throw new RuntimeException( "Loop broke." );
            }
        }
        while ( energylevel.numParticles() < i ) {
            EnergyLevel src = getDonorLevel();
            if ( src == null ) {
                break;
            }
            moveParticle( src, energylevel );
            if ( ++j > 100 ) {
                throw new RuntimeException( "Loop broke." );
            }
        }
    }

}
