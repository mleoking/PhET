/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

/**
 * User: Sam Reid
 * Date: May 16, 2005
 * Time: 9:42:43 PM
 * Copyright (c) May 16, 2005 by Sam Reid
 */

public abstract class ValueAccessor {
    private String name;
    private String units;
    private String unitAbbreviation;

    public static final String joules = "Joules";
    public static final String joulesAbbreviation = "J";

    protected ValueAccessor( String name, String units, String unitAbbreviation ) {
        this.name = name;
        this.units = units;
        this.unitAbbreviation = unitAbbreviation;
    }

    public abstract double getValue( RampModel rampModel );

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public static class TotalEnergy extends EnergyAccessor {
        public TotalEnergy() {
            super( "Total" );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getTotalEnergy();
        }
    }

    public static class KineticEnergy extends EnergyAccessor {

        public KineticEnergy() {
            super( "Kinetic" );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getBlock().getKineticEnergy();
        }
    }

    public static class PotentialEnergy extends EnergyAccessor {
        public PotentialEnergy() {
            super( "Potential" );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getPotentialEnergy();
        }
    }

    public static class ThermalEnergy extends EnergyAccessor {
        public ThermalEnergy() {
            super( "Thermal" );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getThermalEnergy();
        }
    }

    public static class AppliedWork extends WorkAccessor {
        public AppliedWork() {
            super( "Applied" );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getAppliedWork();
        }
    }

    public static class FrictiveWork extends WorkAccessor {
        public FrictiveWork() {
            super( "Frictive" );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getFrictiveWork();
        }
    }

    public static class GravityWork extends WorkAccessor {
        public GravityWork() {
            super( "Gravity" );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getGravityWork();
        }
    }

    public static class TotalWork extends WorkAccessor {
        public TotalWork() {
            super( "Total" );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getTotalWork();
        }
    }

    public static abstract class EnergyAccessor extends ValueAccessor {
        public EnergyAccessor( String name ) {
            super( name, joules, joulesAbbreviation );
        }
    }

    public static abstract class WorkAccessor extends ValueAccessor {
        public WorkAccessor( String name ) {
            super( name, joules, joulesAbbreviation );
        }
    }

}
