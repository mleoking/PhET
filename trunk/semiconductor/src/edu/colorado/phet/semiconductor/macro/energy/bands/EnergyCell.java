/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.common.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 5:57:59 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
 */
public class EnergyCell {
    double x;
    double energy;
    EnergyLevel level;
    int column;
    private long lastVisitTime = 0;

    public EnergyCell( EnergyLevel level, double x, double energy, int column ) {
        this.level = level;
        this.x = x;
        this.energy = energy;
        this.column = column;
    }

    public String toString() {
//        return "Band=" + level.getBand() + ", level=" + level + ", index=" + level.indexOf(this);
        return "x=" + x + ", energy=" + energy + ", level=" + level;
    }

    public double getX() {
        return x;
    }

    public double getEnergy() {
        return energy;
    }

    public PhetVector getPosition() {
        return new PhetVector( x, energy );
    }

    public EnergyLevel getEnergyLevel() {
        return level;
    }

    public void setPosition( double x, double y ) {
        this.x = x;
        this.energy = y;
    }

    public int getIndex() {
        return getEnergyLevel().indexOf( this );
    }

    public SemiconductorBandSet getBandSet() {
        return getEnergyLevel().getBand().getBandSet();
    }

    public int getEnergyLevelBandIndex() {
        return getEnergyLevel().getDistanceFromBottomLevelInBand();
    }

    public int getEnergyLevelAbsoluteIndex() {
        return getEnergyLevel().getAbsoluteHeight();
    }

    public Band getBand() {
        return getEnergyLevel().getBand();
    }

    public int getBandIndex() {
        return getBand().getIndex();
    }

    public int getColumn() {
        return column;
    }

    public void ownerChanged() {
        this.lastVisitTime = System.currentTimeMillis();
    }

    public long getLastVisitTime() {
        return lastVisitTime;
    }
}
