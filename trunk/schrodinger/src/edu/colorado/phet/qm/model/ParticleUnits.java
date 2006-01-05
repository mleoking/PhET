/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

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

    protected ParticleUnits() {
    }

    public ParticleUnits( Value hbar, Value mass, Value dx, Value dt, Value minVelocity, Value maxVelocity ) {
        this.hbar = hbar;
        this.mass = mass;
        this.dx = dx;
        this.dt = dt;
        this.minVelocity = minVelocity;
        this.maxVelocity = maxVelocity;
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

    public void setMinVelocity( Value minVelocity ) {
        this.minVelocity = minVelocity;
    }

    public Value getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity( Value maxVelocity ) {
        this.maxVelocity = maxVelocity;
    }

    public static class ElectronUnits extends ParticleUnits {

        public ElectronUnits() {
            setHbar( new Value( 0.658, 1, "eV fs" ) );
            setMass( new Value( 0.057, 100, "eV fs^2/nm^2" ) );
            setDx( new Value( 1.0, 0.1, "nm" ) );
            setDt( new Value( 1, 1, "fs" ) );
            setMinVelocity( new Value( 9.2, 100, "km/s" ) );
            setMaxVelocity( new Value( 36.8, 100, "km/s" ) );
        }
    }

    public static class NeutronUnits extends ParticleUnits {

        public NeutronUnits() {
            setHbar( new Value( 0.000658, 1, "eV ps" ) );
            setDx( new Value( 1.0, 0.1, "nm" ) );
            setDt( new Value( 1, 1, "ps" ) );

            setMass( new Value( 104800000, 1.0 / 10000.0, "eV fs^2/nm^2" ) );
            setMinVelocity( new Value( 5, 0.1, "km/s" ) );
            setMaxVelocity( new Value( 20, 0.1, "km/s" ) );
        }
    }

    public static class HeliumUnits extends ParticleUnits {

        public HeliumUnits() {
            setHbar( new Value( 0.000658, 1, "eV ps" ) );
            setDx( new Value( 1.0, 0.1, "nm" ) );
            setDt( new Value( 1, 1, "ps" ) );

            setMass( new Value( 415776000, 1.0 / 10000.0, "eV fs^2/nm^2" ) );
            setMinVelocity( new Value( 1.25, 0.1, "km/s" ) );
            setMaxVelocity( new Value( 5, 0.1, "km/s" ) );
        }
    }

    public static class Value {
        double value;
        double displayScaleFactor;
        String units;

        public Value( double modelValue, double displayScaleFactor, String units ) {
            this.value = modelValue;
            this.displayScaleFactor = displayScaleFactor;
            this.units = units;
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
    }
}

