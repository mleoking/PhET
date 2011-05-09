// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.macro.bands;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            EnergyLevel, BandParticle

public class EnergyCell {

    public EnergyCell( EnergyLevel energylevel, double d, double d1 ) {
        level = energylevel;
        x = d;
        y = d1;
    }

    public String toString() {
        return "Band=" + level.getBand() + ", level=" + level + ", index=" + level.indexOf( this );
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public BandParticle getOwner() {
        return owner;
    }

    public void setOwner( BandParticle bandparticle ) {
        if ( owner == null || owner == bandparticle ) {
            owner = bandparticle;
        }
        else {
            throw new RuntimeException( "Wrong owner." );
        }
    }

    public void detach( BandParticle bandparticle ) {
        if ( owner == bandparticle ) {
            owner = null;
        }
        else {
            throw new RuntimeException( "Only owner can detach." );
        }
    }

    public Vector2D getPosition() {
        return new Vector2D( x, y );
    }

    public EnergyLevel getEnergyLevel() {
        return level;
    }

    public boolean isOccupied() {
        return owner != null;
    }

    double x;
    double y;
    BandParticle owner;
    EnergyLevel level;
}
