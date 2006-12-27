/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;

import java.util.Iterator;

/**
 * User: Sam Reid
 * Date: Mar 26, 2004
 * Time: 8:56:40 AM
 * Copyright (c) Mar 26, 2004 by Sam Reid
 */
public class SemiconductorBandSet extends BandSet {
    private DopantType dopantType;
    private EnergySection dopantManager;

    public SemiconductorBandSet( BandSetDescriptor bsd, EnergySection dopantManager, int index ) {

        for( int i = 0; i < bsd.numBands(); i++ ) {
            BandDescriptor bd = bsd.bandDescriptorAt( i );
            Band band = new Band( bd.getRegion(), bd.getNumLevels(), this, index );
            bands.add( band );
        }
        this.dopantManager = dopantManager;
    }

    public Band getConductionBand() {
        return bandAt( 2 );
    }

    public Band getValenceBand() {
        return bandAt( 1 );
    }

    public Band getBottomBand() {
        return bandAt( 0 );
    }

    public Band getTopBand() {
        return bandAt( 3 );
    }

    public class EnergyLevelIterator implements Iterator {
        int curBand;
        int curIndex;

        public boolean hasNext() {
            if( curBand >= numBands() ) {
                return false;
            }
            else {
                return true;
            }
        }

        public Object next() {
            return nextLevel();
        }

        public EnergyLevel nextLevel() {
            Band band = bandAt( curBand );
            EnergyLevel level = band.energyLevelAt( curIndex );

            curIndex++;
            if( curIndex >= band.numEnergyLevels() ) {
                curIndex = 0;
                curBand++;
            }
            return level;
//            return null;
        }

        public void remove() {
            throw new RuntimeException( "Not supported." );
        }

    }

    public EnergyLevelIterator energyLevelIterator() {
        return new EnergyLevelIterator();
    }

    public static final int NUM_DOPING_LEVELS = 6;

    public void setDopantType( DopantType dopantType ) {
        this.dopantType = dopantType;
        EnergyLevelIterator it = energyLevelIterator();
        while( it.hasNext() ) {
            EnergyLevel energyLevel = (EnergyLevel)it.next();
            dopantManager.clear( energyLevel );
        }
        if( dopantType == DopantType.N ) {
            dopeLevels( getBottomBand(), 0, getBottomBand().numEnergyLevels() );
            dopeLevels( getValenceBand(), 0, getValenceBand().numEnergyLevels() );
            dopeLevels( getConductionBand(), 0, NUM_DOPING_LEVELS );
        }
        else if( dopantType == DopantType.P ) {
            dopeLevels( getBottomBand(), 0, getBottomBand().numEnergyLevels() );
            dopeLevels( getValenceBand(), 0, getValenceBand().numEnergyLevels() - NUM_DOPING_LEVELS );
        }
        else if( dopantType == null ) {
            dopeLevels( getBottomBand(), 0, getBottomBand().numEnergyLevels() );
            dopeLevels( getValenceBand(), 0, getBottomBand().numEnergyLevels() );
        }
    }

    public void dopeLevels( Band band, int min, int max ) {
        for( int level = min; level < max; level++ ) {
            dopantManager.fillLevel( band.energyLevelAt( level ) );
        }
    }

    public DopantType getDopantType() {
        return dopantType;
    }

    public EnergyCell energyCellAt( int level, int zeroOrOne ) {
        EnergyLevel lvl = levelAt( level );
        return lvl.cellAt( zeroOrOne );
    }

}
