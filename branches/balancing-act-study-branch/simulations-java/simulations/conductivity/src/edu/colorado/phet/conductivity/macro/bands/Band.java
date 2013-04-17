// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.macro.bands;

import java.util.ArrayList;

import edu.colorado.phet.conductivity.ConductivityResources;
import edu.colorado.phet.conductivity.macro.bands.states.Speed;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            EnergyLevel, DefaultBandSet

public class Band {

    public Band( DefaultBandSet defaultbandset ) {
        levels = new ArrayList();
        bandSet = defaultbandset;
    }

    public double getSpeed() {
        return speed;
    }

    public String toString() {
        if ( bandSet.getUpperBand() == this ) {
            return ConductivityResources.getString( "Band.UpperLabel" );
        }
        if ( bandSet.getLowerBand() == this ) {
            return ConductivityResources.getString( "Band.LowerLabel" );
        }
        else {
            return super.toString();
        }
    }

    public void addLevels( double d, double d1, int i, double d2,
                           double d3, int j, double d4, double d5 ) {
        de = d5;
        double d6 = d;
        for ( int k = 0; k < i; k++ ) {
            EnergyLevel energylevel = new EnergyLevel( this, d2, d6, d3, 2, d4 );
            addLevel( energylevel );
            d6 += d5;
        }

    }

    private void addLevel( EnergyLevel energylevel ) {
        levels.add( energylevel );
    }

    public int numEnergyLevels() {
        return levels.size();
    }

    public EnergyLevel energyLevelAt( int i ) {
        return (EnergyLevel) levels.get( i );
    }

    public int indexOf( EnergyLevel energylevel ) {
        return levels.indexOf( energylevel );
    }

    public double getVoltage() {
        return voltage;
    }

    public void voltageChanged( double d, double d1 ) {
        voltage = d;
        speed = d1;
    }

    public DefaultBandSet getBandSet() {
        return bandSet;
    }

    public int numParticles() {
        int i = 0;
        for ( int j = 0; j < levels.size(); j++ ) {
            EnergyLevel energylevel = (EnergyLevel) levels.get( j );
            i += energylevel.numParticles();
        }

        return i;
    }

    public java.awt.geom.Rectangle2D.Double getBounds() {
        EnergyLevel energylevel = energyLevelAt( 0 );
        EnergyLevel energylevel1 = energyLevelAt( numEnergyLevels() - 1 );
        double d = ( energylevel1.getY() - energylevel.getY() ) + de;
        java.awt.geom.Rectangle2D.Double double1 = new java.awt.geom.Rectangle2D.Double( energylevel.x, energylevel.y, energylevel.getWidth(), d );
        return double1;
    }

    public java.awt.geom.Line2D.Double getTopLine() {
        java.awt.geom.Line2D.Double double1 = energyLevelAt( numEnergyLevels() - 1 ).getLine();
        return new java.awt.geom.Line2D.Double( double1.getX1(), double1.getY1() + de, double1.getX2(), double1.getY2() + de );
    }

    public void propagate() {
        for ( int i = 0; i < numEnergyLevels(); i++ ) {
            EnergyLevel energylevel = energyLevelAt( i );
            if ( energylevel.canConduct() && energylevel.isFull() ) {
//            if( energylevel.canConduct()  ) {
                energylevel.propagateBoth( new Speed() {

                    public double getSpeed() {
                        return speed;
                    }

                } );
            }
        }

    }

    ArrayList levels;
    private double voltage;
    private double speed;
    DefaultBandSet bandSet;
    private double de;

}
