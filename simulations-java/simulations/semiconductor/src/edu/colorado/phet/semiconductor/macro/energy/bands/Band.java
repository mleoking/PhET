package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.util.ArrayList;

import edu.colorado.phet.semiconductor.common.EnergySpaceRegion;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 11:58:19 PM
 */
public class Band {
    SemiconductorBandSet bandSet;
    ArrayList levels = new ArrayList();
    private EnergySpaceRegion region;

    /**
     * Creates a band, splitting up the specified EnergySpaceRegion into numLevels adjacent EnergyLevels.
     */
    public Band( EnergySpaceRegion region, int numLevels, SemiconductorBandSet bandSet, int index ) {
        this.region = region;
        this.bandSet = bandSet;
        double energyPerLevel = region.getEnergyRange() / numLevels;
        for ( int i = 0; i < numLevels; i++ ) {
            double energy = i * energyPerLevel + region.getMinEnergy();
            EnergySpaceRegion subRegion = new EnergySpaceRegion( region.getMinX(), energy, region.getSpatialWidth(), energyPerLevel );
            EnergyLevel level = new EnergyLevel( this, subRegion, index );
            addLevel( level );
        }
        if ( bandSet == null ) {
            throw new RuntimeException( "Null bandset." );
        }
    }

    public String toString() {
//        if( bandSet.getConductionBand() == this ) {
//            return "Upper";
//        }
//        else if( bandSet.getValenceBand() == this ) {
//            return "Lower";
//        }
//        else {
//            return super.toString();
//        }
        return super.toString();
    }

    public EnergySpaceRegion getRegion() {
        return region;
    }

    private void addLevel( EnergyLevel level ) {
        this.levels.add( level );
    }

    public int numEnergyLevels() {
        return levels.size();
    }

    public EnergyLevel energyLevelAt( int i ) {
        return (EnergyLevel) levels.get( i );
    }

    public int indexOf( EnergyLevel level ) {
        return levels.indexOf( level );
    }

    public SemiconductorBandSet getBandSet() {
        return bandSet;
    }

//    public boolean isUpperBand() {
//        return getBandSet().indexOf( this ) == BandSet.UPPER;
//    }

}
