/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * User: Sam Reid
 * Date: Jan 5, 2006
 * Time: 2:33:36 PM
 * Copyright (c) Jan 5, 2006 by Sam Reid
 */

public class ParticleUnits {
    private Value hbar;
    private Value mass;
    private Value dx;
    private Value dt;
    private Value minVelocity;
    private Value maxVelocity;
    private int numRulerReadings;
    private double rulerScale;
    private DecimalFormat rulerFormat;
    private double rulerWidth;
    private double latticeWidth;

    protected ParticleUnits() {
    }

    public ParticleUnits( Value hbar, Value mass, Value dx, Value dt, Value minVelocity, Value maxVelocity,
                          int numRulerReadings, double rulerScale, DecimalFormat rulerFormat ) {
        this.hbar = hbar;
        this.mass = mass;
        this.dx = dx;
        this.dt = dt;
        this.minVelocity = minVelocity;
        this.maxVelocity = maxVelocity;
    }

    public String toString() {
        return "hbar=" + hbar + ", mass=" + mass + ", dx=" + dx + ", dt=" + dt + ", minVelocity=" + minVelocity + ", maxVelocity=" + maxVelocity;
    }

    public Value getHbar() {
        return hbar;
    }

    public void setHbar( Value hbar ) {
        this.hbar = hbar;
    }

    public Value getMass() {
        return mass;
    }

    public void setMass( Value mass ) {
        this.mass = mass;
    }

    public Value getDx() {
        return dx;
    }

    public void setDx( Value dx ) {
        this.dx = dx;
    }

    public Value getDt() {
        return dt;
    }

    public void setDt( Value dt ) {
        this.dt = dt;
    }

    public Value getMinVelocity() {
        return minVelocity;
    }

    public double getAverageVelocity() {
        return ( minVelocity.getValue() + maxVelocity.getValue() ) / 2.0;
    }

    public void setMinVelocity( Value minVelocity ) {
        this.minVelocity = minVelocity;
    }

    public Value getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity( Value maxVelocity ) {
        this.maxVelocity = maxVelocity;
    }

    private static double scaleDownMaxVel = 0.5;

    public int getNumRulerReadings() {
        return numRulerReadings;
    }

    public double getRulerScale() {
        return rulerScale;
    }

    public DecimalFormat getRulerFormat() {
        return rulerFormat;
    }

    public void setNumRulerReadings( int numRulerReadings ) {
        this.numRulerReadings = numRulerReadings;
    }

    public void setRulerFormat( DecimalFormat rulerFormat ) {
        this.rulerFormat = rulerFormat;
    }

    public double getRulerWidth() {
        return rulerWidth;
    }

    public double getLatticeWidth() {
        return latticeWidth;
    }

    protected void setRulerWidth( double v ) {
        this.rulerWidth = v;
    }

    protected void setLatticeWidth( double v ) {
        this.latticeWidth = v;
    }

    public static class ElectronUnits extends ParticleUnits {
        public ElectronUnits() {
            //o	Electrons: 920 km/s to 1840 km/s

            setHbar( new Value( 0.658, 1, "eV fs" ) );
            setMass( new Value( 0.057, 100, "eV fs^2/nm^2" ) );
            setDx( new Value( 1.0, 0.1, "nm" ) );
//            setDt( new Value( 1, 1, "fs" ) );
            setDt( new Value( 0.05, 0.10, "fs" ) );
            setMinVelocity( new Value( 9.2, 100, "km/s" ) );
            setMaxVelocity( new Value( 36.8 * scaleDownMaxVel, 100, "km/s" ) );

            DecimalFormat defaultFormat = new DecimalFormat( "0" );
            setVelocityFormat( defaultFormat );

            setLatticeWidth( 0.45 );
            setRulerWidth( 0.50 );
            setRulerFormat( new DecimalFormat( "0.0" ) );
            setNumRulerReadings( 6 );
//            int numRulerReadings, double rulerScale, DecimalFormat rulerFormat ) {
        }
    }

    protected void setVelocityFormat( DecimalFormat defaultFormat ) {
        getMinVelocity().setDefaultFormat( defaultFormat );
        getMaxVelocity().setDefaultFormat( defaultFormat );
    }

    public static class NeutronUnits extends ParticleUnits {
        public NeutronUnits() {
            setHbar( new Value( 0.000658, 1, "eV ps" ) );
            setDx( new Value( 1.0, 0.1, "nm" ) );
            setDt( new Value( 0.1, 0.1, "ps" ) );
            setMass( new Value( 0.000104539, 1.0 / 10000.0, "eV fs^2/nm^2" ) );
            setMinVelocity( new Value( 5, 0.1, "km/s" ) );
            setMaxVelocity( new Value( 20 * scaleDownMaxVel, 0.1, "km/s" ) );
            DecimalFormat defaultFormat = new DecimalFormat( "0.0" );
            setVelocityFormat( defaultFormat );
            setLatticeWidth( 0.45 );
            setRulerWidth( 0.50 );
            setRulerFormat( new DecimalFormat( "0.0" ) );
            setNumRulerReadings( 6 );
        }
    }

    public static class PhotonUnits extends ParticleUnits {
        public PhotonUnits() {
//            setHbar( new Value( 1, 1, "eV ps" ) );
//            setDx( new Value( 1.0, 0.1*5/3000, "nm" ) );
            setDx( new Value( 1.0, 0.06 * 20000, "nm" ) );
            setDt( new Value( 1, 0.1, "fs" ) );
//            setMass( new Value( 0.000414741, 1.0 / 10000.0, "eV fs^2/nm^2" ) );
//            setMinVelocity( new Value( 1.25, 0.1, "km/s" ) );
//            setMaxVelocity( new Value( 5 * scaleDownMaxVel, 0.1, "km/s" ) );
//            setLatticeWidth( 3000 );
//            setRulerWidth( 3000 );

            setLatticeWidth( 2700 );
            setRulerWidth( 3000 );

            setRulerFormat( new DecimalFormat( "0" ) );
            setNumRulerReadings( 4 );
        }
    }

    public static class HeliumUnits extends ParticleUnits {
        public HeliumUnits() {
            setHbar( new Value( 0.000658, 1, "eV ps" ) );
            setDx( new Value( 1.0, 0.1, "nm" ) );
            setDt( new Value( 0.5, 0.1, "ps" ) );
            setMass( new Value( 0.000414741, 1.0 / 10000.0, "eV fs^2/nm^2" ) );
            setMinVelocity( new Value( 1.25, 0.1, "km/s" ) );
            setMaxVelocity( new Value( 5 * scaleDownMaxVel, 0.1, "km/s" ) );
            DecimalFormat defaultFormat = new DecimalFormat( "0.000" );
            setVelocityFormat( defaultFormat );
            setLatticeWidth( 0.45 );
            setRulerWidth( 0.50 );
            setRulerFormat( new DecimalFormat( "0.0" ) );
            setNumRulerReadings( 6 );
        }
    }

    public static class Value {
        double value;
        double displayScaleFactor;
        String units;
        private NumberFormat defaultFormat;

        public Value( double modelValue, double displayScaleFactor, String units ) {
            this.value = modelValue;
            this.displayScaleFactor = displayScaleFactor;
            this.units = units;
        }

        public void setDefaultFormat( NumberFormat defaultFormat ) {
            this.defaultFormat = defaultFormat;
        }

        public double getValue() {
            return value;
        }

        public double getDisplayValue() {
            return value * displayScaleFactor;
        }

        public double getDisplayScaleFactor() {
            return displayScaleFactor;
        }

        public String getUnits() {
            return units;
        }

        public String toString() {
            return "" + getDisplayValue() + " " + units + " [@" + value + "]";
        }

        public String toPrettyString( NumberFormat numberFormat ) {
            return numberFormat.format( getDisplayValue() ) + " " + units;
        }

        public String toPrettyString() {
            return toPrettyString( defaultFormat );
        }
    }
}

