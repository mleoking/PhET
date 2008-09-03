/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 5:57:59 PM
 */
public class EnergyCell {
    double x;
    double energy;
    EnergyLevel level;
    int column;

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

    public int getIndex() {
        return getEnergyLevel().indexOf( this );
    }

    public SemiconductorBandSet getBandSet() {
        return getEnergyLevel().getBand().getBandSet();
    }

    public int getEnergyLevelAbsoluteIndex() {
        return getEnergyLevel().getAbsoluteHeight();
    }

    public int getColumn() {
        return column;
    }

//    public void ownerChanged() {
//        this.lastVisitTime = System.currentTimeMillis();
//    }

}
