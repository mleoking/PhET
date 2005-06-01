/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.theramp.view.RampLookAndFeel;

import java.awt.*;

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
    private Color color;

    public static final String joules = "Joules";
    public static final String joulesAbbreviation = "J";

    protected ValueAccessor( String name, String units, String unitAbbreviation, Color color ) {
        this.name = name;
        this.units = units;
        this.unitAbbreviation = unitAbbreviation;
        this.color = color;
    }

    public abstract double getValue( RampModel rampModel );

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public static class TotalEnergy extends EnergyAccessor {
        public TotalEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( "Total", rampLookAndFeel.getTotalEnergyColor() );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getTotalEnergy();
        }
    }

    public static class KineticEnergy extends EnergyAccessor {

        public KineticEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( "Kinetic", rampLookAndFeel.getKineticEnergyColor() );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getBlock().getKineticEnergy();
        }
    }

    public static class PotentialEnergy extends EnergyAccessor {
        public PotentialEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( "Potential", rampLookAndFeel.getPotentialEnergyColor() );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getPotentialEnergy();
        }
    }

    public static class ThermalEnergy extends EnergyAccessor {
        public ThermalEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( "Thermal", rampLookAndFeel.getThermalEnergyColor() );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getThermalEnergy();
        }
    }

    public static class AppliedWork extends WorkAccessor {
        public AppliedWork( RampLookAndFeel rampLookAndFeel ) {
            super( "Applied", rampLookAndFeel.getAppliedWorkColor() );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getAppliedWork();
        }
    }

    public static class FrictiveWork extends WorkAccessor {
        public FrictiveWork( RampLookAndFeel rampLookAndFeel ) {
            super( "Friction", rampLookAndFeel.getFrictionWorkColor() );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getFrictiveWork();
        }
    }

    public static class GravityWork extends WorkAccessor {
        public GravityWork( RampLookAndFeel rampLookAndFeel ) {
            super( "Gravity", rampLookAndFeel.getGravityWorkColor() );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getGravityWork();
        }
    }

    public static class TotalWork extends WorkAccessor {
        public TotalWork( RampLookAndFeel rampLookAndFeel ) {
            super( "Total", rampLookAndFeel.getTotalWorkColor() );
        }

        public double getValue( RampModel rampModel ) {
            return rampModel.getTotalWork();
        }
    }

    public static abstract class EnergyAccessor extends ValueAccessor {
        public EnergyAccessor( String name, Color color ) {
            super( name, joules, joulesAbbreviation, color );
        }
    }

    public static abstract class WorkAccessor extends ValueAccessor {
        public WorkAccessor( String name, Color color ) {
            super( name, joules, joulesAbbreviation, color );
        }
    }

}
