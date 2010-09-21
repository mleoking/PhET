package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.util.ArrayList;

import edu.colorado.phet.semiconductor.common.EnergySpaceRegion;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 11:58:34 PM
 */
public class EnergyLevel {
    private Band band;
    ArrayList cells = new ArrayList();
    private EnergySpaceRegion region;
    private static int ID = 0;
    private int id;

    public EnergyLevel( Band band, EnergySpaceRegion region, int columnBase ) {
        id = ID;
        ID++;
        this.band = band;
        this.region = region;

        EnergyCell cell0 = new EnergyCell( this, region.getMinX() + region.getSpatialWidth() * .25, region.getMinEnergy() + region.getEnergyRange() / 2, columnBase );
        EnergyCell cell1 = new EnergyCell( this, region.getMinX() + region.getSpatialWidth() * .75, region.getMinEnergy() + region.getEnergyRange() / 2, columnBase + 1 );
        cells.add( cell0 );
        cells.add( cell1 );
    }

    public int numCells() {
        return cells.size();
    }

    public String toString() {
        return "id=" + id + ", index=" + band.indexOf( this );
    }

    public EnergySpaceRegion getRegion() {
        return region;
    }

    public EnergyCell cellAt( int i ) {
        return (EnergyCell) cells.get( i );
    }

    public int indexOf( EnergyCell cell ) {
        return cells.indexOf( cell );
    }

    public Band getBand() {
        return band;
    }

    public BandSet getBandSet() {
        return getBand().getBandSet();
    }

    public int getAbsoluteHeight() {
        return getBandSet().absoluteIndexOf( this );
    }

    public int getDistanceFromBottomLevelInBand() {
        return getBand().indexOf( this );
    }

    public int getID() {
        return id;
    }
}
