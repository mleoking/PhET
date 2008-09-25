// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.bands;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.conductivity.model.ModelElement;
import edu.colorado.phet.conductivity.macro.MacroSystem;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            Band, BandParticle, BandParticleObserver, EnergyLevel, 
//            EnergyCell

public class DefaultBandSet implements ModelElement {
    protected Band upper;
    protected Band lowband;
    ArrayList bandParticles;
    ArrayList bandParticleObservers;
    protected double inset;
    protected Random random;
    MacroSystem system;
    private double y;
    private double boundsHeight;
    double lowBandY1;
    double lowBandY2;
    int lowNumLevels;
    int highBandNumLevels;

    public DefaultBandSet( MacroSystem macrosystem, double d ) {
        bandParticles = new ArrayList();
        bandParticleObservers = new ArrayList();
        random = new Random( 0L );
        y = 0.10000000000000001D;
        boundsHeight = 0.80000000000000004D;
        lowBandY1 = 0.20000000000000001D;
        lowBandY2 = 0.40000000000000002D;
        lowNumLevels = 6;
        highBandNumLevels = 6;
        system = macrosystem;
        if ( macrosystem == null ) {
            throw new RuntimeException( "Null system." );
        }
        else {
            inset = d * 2D;
            lowband = new Band( this );
            upper = new Band( this );
            return;
        }
    }

    public void removeParticles() {
        for ( ; bandParticles.size() > 0; removeParticle( (BandParticle) bandParticles.get( 0 ) ) ) {
            ;
        }
    }

    public void initParticles() {
        removeParticles();
    }

    public void fillLevel( EnergyLevel energylevel ) {
        BandParticle abandparticle[] = energylevel.fillLevel();
        for ( int i = 0; i < abandparticle.length; i++ ) {
            BandParticle bandparticle = abandparticle[i];
            addParticle( bandparticle );
        }

    }

    private void addParticle( BandParticle bandparticle ) {
        bandParticles.add( bandparticle );
        fireParticleAdded( bandparticle );
    }

    private void fireParticleAdded( BandParticle bandparticle ) {
        for ( int i = 0; i < bandParticleObservers.size(); i++ ) {
            BandParticleObserver bandparticleobserver = (BandParticleObserver) bandParticleObservers.get( i );
            bandparticleobserver.particleAdded( bandparticle );
        }

    }

    public Band getUpperBand() {
        return upper;
    }

    public Band getLowerBand() {
        return lowband;
    }

    public double getX() {
        return 0.10000000000000001D;
    }

    public double getWidth() {
        return 0.14000000000000001D;
    }

    public java.awt.geom.Rectangle2D.Double getBounds() {
        double d = getX();
        double d1 = getWidth();
        java.awt.geom.Rectangle2D.Double double1 = new java.awt.geom.Rectangle2D.Double( d, y, d1, boundsHeight );
        double d2 = 0.02D;
        double1 = new java.awt.geom.Rectangle2D.Double( double1.x - d2, double1.y - d2, double1.width + d2 * 2D, double1.height + d2 * 2D );
        return double1;
    }

    public double voltageChanged( double voltage, double d1 ) {
        upper.voltageChanged( voltage, d1 );
        lowband.voltageChanged( voltage, d1 );
        return desiredSpeedToActualSpeed( d1 );
    }

    public double desiredSpeedToActualSpeed( double d ) {
        return d;
    }

    public void stepInTime( double d ) {
        for ( int i = 0; i < bandParticles.size(); i++ ) {
            BandParticle bandparticle = (BandParticle) bandParticles.get( i );
            bandparticle.stepInTime( d );
        }

        upper.propagate();
        lowband.propagate();
    }

    public void removeParticle( BandParticle bandparticle ) {
        bandparticle.detach();
        bandParticles.remove( bandparticle );
        fireParticleRemoved( bandparticle );
    }

    public void addBandParticleObserver( BandParticleObserver bandparticleobserver ) {
        bandParticleObservers.add( bandparticleobserver );
    }

    private void fireParticleRemoved( BandParticle bandparticle ) {
        for ( int i = 0; i < bandParticleObservers.size(); i++ ) {
            BandParticleObserver bandparticleobserver = (BandParticleObserver) bandParticleObservers.get( i );
            bandparticleobserver.particleRemoved( bandparticle );
        }

    }

    protected boolean moveParticle( EnergyLevel src, EnergyLevel dst, boolean reverse ) {
        if ( reverse ) {
            return moveParticle( dst, src );
        }
        else {
            return moveParticle( src, dst );
        }
    }

    protected boolean moveParticle( EnergyLevel src, EnergyLevel dst ) {
        if ( !dst.hasAnEmptyCell() ) {
//            System.out.println( "No empty cell in destination level." );
            return false;
        }
        boolean flag = false;
        int i = random.nextInt( src.numCells() );
        for ( int j = i; j < src.numCells(); j++ ) {
            flag = tryToMove( src.cellAt( j ), dst.cellAt( j ) );
            if ( flag ) {
                return true;
            }
        }

        for ( int k = i; k >= 0; k-- ) {
            flag = tryToMove( src.cellAt( k ), dst.cellAt( k ) );
            if ( flag ) {
                return true;
            }
        }

        flag = tryToMoveAny( src, dst );
        if ( flag ) {
            return true;
        }
        else {
//            System.out.println( "Couldn't move the desired particle." );
            return false;
        }
    }

    private boolean tryToMoveAny( EnergyLevel energylevel, EnergyLevel energylevel1 ) {
        for ( int i = 0; i < energylevel.numCells(); i++ ) {
            for ( int j = 0; j < energylevel1.numCells(); j++ ) {
                EnergyCell energycell = energylevel.cellAt( i );
                EnergyCell energycell1 = energylevel1.cellAt( j );
                boolean flag = tryToMove( energycell, energycell1 );
                if ( flag ) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryToMove( EnergyCell energycell, EnergyCell energycell1 ) {
        BandParticle bandparticle = energycell.getOwner();
        if ( bandparticle != null && !energycell1.isOccupied() ) {
            bandparticle.moveTo( energycell1, 0.006 );
            return true;
        }
        else {
            return false;
        }
    }

    public EnergyLevel getLowerLevel( EnergyLevel energylevel ) {
        if ( upper.indexOf( energylevel ) >= 1 ) {
            return upper.energyLevelAt( upper.indexOf( energylevel ) - 1 );
        }
        if ( upper.indexOf( energylevel ) == 0 ) {
            return lowband.energyLevelAt( lowband.numEnergyLevels() - 1 );
        }
        if ( lowband.indexOf( energylevel ) >= 1 ) {
            return lowband.energyLevelAt( lowband.indexOf( energylevel ) - 1 );
        }
        else {
            return null;
        }
    }

    public void photonGotAbsorbed() {
        system.photonGotAbsorbed();
    }


}
