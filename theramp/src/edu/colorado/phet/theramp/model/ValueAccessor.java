/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.view.RampLookAndFeel;

import java.awt.*;
import java.text.MessageFormat;

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

    public static final String joules = TheRampStrings.getString( "joules" );
    public static final String joulesAbbreviation = TheRampStrings.getString( "j" );

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
            super( TheRampStrings.getString( "total" ), rampLookAndFeel.getTotalEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getTotalEnergy();
        }
    }

    public static class KineticEnergy extends EnergyAccessor {

        public KineticEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "kinetic" ), rampLookAndFeel.getKineticEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getBlock().getKineticEnergy();
        }
    }

    public static class PotentialEnergy extends EnergyAccessor {
        public PotentialEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "potential" ), rampLookAndFeel.getPotentialEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getPotentialEnergy();
        }
    }

    public static class ThermalEnergy extends EnergyAccessor {
        public ThermalEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "thermal" ), rampLookAndFeel.getThermalEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getThermalEnergy();
        }
    }

    public static class AppliedWork extends WorkAccessor {
        public AppliedWork( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "applied1" ), rampLookAndFeel.getAppliedWorkColor(), TheRampStrings.getString( "applied" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getAppliedWork();
        }
    }

    public static class FrictiveWork extends WorkAccessor {
        public FrictiveWork( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "friction1" ), rampLookAndFeel.getFrictionWorkColor(), TheRampStrings.getString( "friction" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getFrictiveWork();
        }
    }

    public static class GravityWork extends WorkAccessor {
        public GravityWork( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "gravity" ), rampLookAndFeel.getGravityWorkColor(), TheRampStrings.getString( "gravity1" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getGravityWork();
        }
    }

    public static class TotalWork extends WorkAccessor {
        public TotalWork( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "net1" ), rampLookAndFeel.getTotalWorkColor(), TheRampStrings.getString( "net" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getTotalWork();
        }
    }

    public static abstract class EnergyAccessor extends ValueAccessor {
        public EnergyAccessor( String name, Color color ) {
            super( name, name, joules, joulesAbbreviation, color, MessageFormat.format( TheRampStrings.getResourceBundle().getString( "0.energy" ), new Object[]{name} ) );
        }
    }

    public static abstract class WorkAccessor extends ValueAccessor {
        public WorkAccessor( String name, Color color, String subText ) {
            super( name, MessageFormat.format( TheRampStrings.getResourceBundle().getString( "w.sub.0.sub" ), new Object[]{subText} ), joules, joulesAbbreviation, color, MessageFormat.format( TheRampStrings.getResourceBundle().getString( "0.work" ), new Object[]{name} ) );
        }
    }

    public static abstract class ParallelForceAccessor extends ValueAccessor {

        protected ParallelForceAccessor( String name, Color color, String subText ) {
            super( name, MessageFormat.format( TheRampStrings.getResourceBundle().getString( "f.sub.0.sub" ), new Object[]{subText} ), TheRampStrings.getString( "newtons" ), TheRampStrings.getString( "n" ), color, name );
        }

    }

    public static class ParallelFrictionAccessor extends ParallelForceAccessor {
        public ParallelFrictionAccessor( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "parallel.friction" ), rampLookAndFeel.getFrictionForceColor(), TheRampStrings.getString( "friction" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelFrictionForce();
        }
    }

    public static class ParallelAppliedAccessor extends ParallelForceAccessor {
        public ParallelAppliedAccessor( RampLookAndFeel lookAndFeel ) {
            super( TheRampStrings.getString( "parallel.applied.force" ), lookAndFeel.getAppliedForceColor(), TheRampStrings.getString( "applied" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelAppliedForce();
        }
    }

    public static class ParallelGravityAccessor extends ParallelForceAccessor {
        public ParallelGravityAccessor( RampLookAndFeel lookAndFeel ) {
            super( TheRampStrings.getString( "parallel.gravity.force" ), lookAndFeel.getWeightColor(), TheRampStrings.getString( "gravity1" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelWeightForce();
        }
    }

    public static class ParallelWallForceAccessor extends ParallelForceAccessor {
        public ParallelWallForceAccessor( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "parallel.wall.force" ), rampLookAndFeel.getWallForceColor(), TheRampStrings.getString( "wall1" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelWallForce();
        }
    }
}
