package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.util.Iterator;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;

/**
 * User: Sam Reid
 * Date: Mar 26, 2004
 * Time: 8:56:40 AM
 */
public class SemiconductorBandSet extends BandSet {
    private DopantType dopantType;
    private EnergySection dopantManager;

    public SemiconductorBandSet( BandSetDescriptor bsd, EnergySection dopantManager, int index ) {

        for ( int i = 0; i < bsd.numBands(); i++ ) {
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
            if ( curBand >= numBands() ) {
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
            if ( curIndex >= band.numEnergyLevels() ) {
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

    public void trickDopantType( DopantType dopantType ) {
        this.dopantType = dopantType;
    }

    public void setDopantType( DopantType dopantType ) {
        this.dopantType = dopantType;
        EnergyLevelIterator it = energyLevelIterator();
        while ( it.hasNext() ) {
            EnergyLevel energyLevel = (EnergyLevel) it.next();
            dopantManager.clear( energyLevel );
        }
        if ( dopantType == null ) {
            dopeLevels( getBottomBand(), 0, getBottomBand().numEnergyLevels(), getEnergySection() );
            dopeLevels( getValenceBand(), 0, getBottomBand().numEnergyLevels(), getEnergySection() );
        }
        else {
            dope();
        }
    }


    public void dope() {
        SemiconductorBandSet semiconductorBandSet = this;
        if ( getDopantType() == DopantType.N ) {
            dopeLevels( semiconductorBandSet.getBottomBand(), 0, semiconductorBandSet.getBottomBand().numEnergyLevels(), semiconductorBandSet.getEnergySection() );
            dopeLevels( semiconductorBandSet.getValenceBand(), 0, semiconductorBandSet.getValenceBand().numEnergyLevels(), semiconductorBandSet.getEnergySection() );
            dopeLevels( semiconductorBandSet.getConductionBand(), 0, getDopantType().getNumFilledLevels(), semiconductorBandSet.getEnergySection() );
        }
        else if ( getDopantType() == DopantType.P ) {
            dopeLevels( semiconductorBandSet.getBottomBand(), 0, semiconductorBandSet.getBottomBand().numEnergyLevels(), semiconductorBandSet.getEnergySection() );
            dopeLevels( semiconductorBandSet.getValenceBand(), 0, getDopantType().getNumFilledLevels(), semiconductorBandSet.getEnergySection() );
        }

    }

    public void dopeLevels( Band band, int min, int max, EnergySection energySection ) {
        for ( int level = min; level < max; level++ ) {
            energySection.fillLevel( band.energyLevelAt( level ) );
        }
    }

    public DopantType getDopantType() {
        return dopantType;
    }

    public EnergyCell energyCellAt( int level, int zeroOrOne ) {
        EnergyLevel lvl = levelAt( level );
        return lvl.cellAt( zeroOrOne );
    }

    public EnergySection getEnergySection() {
        return dopantManager;
    }

}
