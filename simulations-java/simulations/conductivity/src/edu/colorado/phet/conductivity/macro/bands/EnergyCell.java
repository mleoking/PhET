// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

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

    public Vector2D.Double getPosition() {
        return new Vector2D.Double( x, y );
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
