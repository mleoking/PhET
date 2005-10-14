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
    private String html;
    private String units;
    private String unitAbbreviation;
    private Color color;
    private String fullName;

    public static final String joules = "Joules";
    public static final String joulesAbbreviation = "J";

    protected ValueAccessor( String name, String html, String units, String unitAbbreviation, Color color, String fullName ) {
        this.name = name;
        this.html = html;
        this.units = units;
        this.unitAbbreviation = unitAbbreviation;
        this.color = color;
        this.fullName = fullName;
    }

    public abstract double getValue( RampPhysicalModel rampPhysicalModel );

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUnitsAbbreviation() {
        return unitAbbreviation;
    }

    public String getHTML() {
        return html;
    }

    public static class TotalEnergy extends EnergyAccessor {
        public TotalEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( "Total", rampLookAndFeel.getTotalEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getTotalEnergy();
        }
    }

    public static class KineticEnergy extends EnergyAccessor {

        public KineticEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( "Kinetic", rampLookAndFeel.getKineticEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getBlock().getKineticEnergy();
        }
    }

    public static class PotentialEnergy extends EnergyAccessor {
        public PotentialEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( "Potential", rampLookAndFeel.getPotentialEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getPotentialEnergy();
        }
    }

    public static class ThermalEnergy extends EnergyAccessor {
        public ThermalEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( "Thermal", rampLookAndFeel.getThermalEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getThermalEnergy();
        }
    }

    public static class AppliedWork extends WorkAccessor {
        public AppliedWork( RampLookAndFeel rampLookAndFeel ) {
            super( "Applied", rampLookAndFeel.getAppliedWorkColor(), "applied" );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getAppliedWork();
        }
    }

    public static class FrictiveWork extends WorkAccessor {
        public FrictiveWork( RampLookAndFeel rampLookAndFeel ) {
            super( "Friction", rampLookAndFeel.getFrictionWorkColor(), "friction" );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getFrictiveWork();
        }
    }

    public static class GravityWork extends WorkAccessor {
        public GravityWork( RampLookAndFeel rampLookAndFeel ) {
            super( "Gravity", rampLookAndFeel.getGravityWorkColor(), "gravity" );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getGravityWork();
        }
    }

    public static class TotalWork extends WorkAccessor {
        public TotalWork( RampLookAndFeel rampLookAndFeel ) {
            super( "Net", rampLookAndFeel.getTotalWorkColor(), "net" );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getTotalWork();
        }
    }

    public static abstract class EnergyAccessor extends ValueAccessor {
        public EnergyAccessor( String name, Color color ) {
            super( name, name, joules, joulesAbbreviation, color, name + " Energy" );
        }
    }

    public static abstract class WorkAccessor extends ValueAccessor {
        public WorkAccessor( String name, Color color, String subText ) {
            super( name, "W<sub>" + subText + "</sub>", joules, joulesAbbreviation, color, name + " Work" );
        }
    }

    public static abstract class ParallelForceAccessor extends ValueAccessor {

        protected ParallelForceAccessor( String name, Color color, String subText ) {
            super( name, "F<sub>" + subText + "</sub>", "Newtons", "N", color, name );
        }

    }

    public static class ParallelFrictionAccessor extends ParallelForceAccessor {
        public ParallelFrictionAccessor( RampLookAndFeel rampLookAndFeel ) {
            super( "Parallel Friction", rampLookAndFeel.getFrictionForceColor(), "friction" );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelFrictionForce();
        }
    }

    public static class ParallelAppliedAccessor extends ParallelForceAccessor {
        public ParallelAppliedAccessor( RampLookAndFeel lookAndFeel ) {
            super( "Parallel Applied Force", lookAndFeel.getAppliedForceColor(), "applied" );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelAppliedForce();
        }
    }

    public static class ParallelGravityAccessor extends ParallelForceAccessor {
        public ParallelGravityAccessor( RampLookAndFeel lookAndFeel ) {
            super( "Parallel Gravity Force", lookAndFeel.getWeightColor(), "gravity" );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelWeightForce();
        }
    }

    public static class ParallelWallForceAccessor extends ParallelForceAccessor {
        public ParallelWallForceAccessor( RampLookAndFeel rampLookAndFeel ) {
            super( "Parallel Wall Force", rampLookAndFeel.getWallForceColor(), "wall" );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelWallForce();
        }
    }
}
