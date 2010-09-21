/*  */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.view.RampLookAndFeel;

import java.awt.*;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: May 16, 2005
 * Time: 9:42:43 PM
 */

public abstract class ValueAccessor {
    private String name;
    private String html;
    private String units;
    private String unitAbbreviation;
    private Color color;
    private String fullName;

    public static final String joules = TheRampStrings.getString( "units.joules" );
    public static final String joulesAbbreviation = TheRampStrings.getString( "units.abbr.joules" );

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
            super( TheRampStrings.getString( "forces.total" ), rampLookAndFeel.getTotalEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getTotalEnergy();
        }
    }

    public static class KineticEnergy extends EnergyAccessor {

        public KineticEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "energy.kinetic" ), rampLookAndFeel.getKineticEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getBlock().getKineticEnergy();
        }
    }

    public static class PotentialEnergy extends EnergyAccessor {
        public PotentialEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "energy.potential" ), rampLookAndFeel.getPotentialEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getPotentialEnergy();
        }
    }

    public static class ThermalEnergy extends EnergyAccessor {
        public ThermalEnergy( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "energy.thermal" ), rampLookAndFeel.getThermalEnergyColor() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getThermalEnergy();
        }
    }

    public static class AppliedWork extends WorkAccessor {
        public AppliedWork( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "forces.applied" ), rampLookAndFeel.getAppliedWorkColor(), TheRampStrings.getString( "forces.applied" ).toLowerCase() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getAppliedWork();
        }
    }

    public static class FrictiveWork extends WorkAccessor {
        public FrictiveWork( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "controls.friction" ), rampLookAndFeel.getFrictionWorkColor(), TheRampStrings.getString( "forces.friction" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getFrictiveWork();
        }
    }

    public static class GravityWork extends WorkAccessor {
        public GravityWork( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "forces.Gravity" ), rampLookAndFeel.getGravityWorkColor(), TheRampStrings.getString( "forces.Gravity" ).toLowerCase() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getGravityWork();
        }
    }

    public static class TotalWork extends WorkAccessor {
        public TotalWork( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "forces.Net" ), rampLookAndFeel.getTotalWorkColor(), TheRampStrings.getString( "forces.Net" ).toLowerCase() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getTotalWork();
        }
    }

    public static abstract class EnergyAccessor extends ValueAccessor {
        public EnergyAccessor( String name, Color color ) {
            super( name, name, joules, joulesAbbreviation, color, MessageFormat.format( TheRampStrings.getString( "0.energy" ), new Object[]{name} ) );
        }
    }

    public static abstract class WorkAccessor extends ValueAccessor {
        public WorkAccessor( String name, Color color, String subText ) {
            super( name, MessageFormat.format( TheRampStrings.getString( "work.subscript" ), new Object[]{subText} ), joules, joulesAbbreviation, color, MessageFormat.format( TheRampStrings.getString( "0.work" ), new Object[]{name} ) );
        }
    }

    public static abstract class ParallelForceAccessor extends ValueAccessor {

        protected ParallelForceAccessor( String name, Color color, String subText ) {
            super( name, MessageFormat.format( TheRampStrings.getString( "force.subscript" ), new Object[]{subText} ), TheRampStrings.getString( "units.newtons" ), TheRampStrings.getString( "units.abbr.newtons" ), color, name );
        }

    }

    public static class ParallelFrictionAccessor extends ParallelForceAccessor {
        public ParallelFrictionAccessor( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "forces.parallel-friction" ), rampLookAndFeel.getFrictionForceColor(), TheRampStrings.getString( "forces.friction" ) );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelFrictionForce();
        }
    }

    public static class ParallelAppliedAccessor extends ParallelForceAccessor {
        public ParallelAppliedAccessor( RampLookAndFeel lookAndFeel ) {
            super( TheRampStrings.getString( "forces.parallel-applied" ), lookAndFeel.getAppliedForceColor(), TheRampStrings.getString( "forces.applied" ).toLowerCase() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelAppliedForce();
        }
    }

    public static class ParallelGravityAccessor extends ParallelForceAccessor {
        public ParallelGravityAccessor( RampLookAndFeel lookAndFeel ) {
            super( TheRampStrings.getString( "forces.parallel-gravity" ), lookAndFeel.getWeightColor(), TheRampStrings.getString( "forces.Gravity" ).toLowerCase() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelWeightForce();
        }
    }

    public static class ParallelWallForceAccessor extends ParallelForceAccessor {
        public ParallelWallForceAccessor( RampLookAndFeel rampLookAndFeel ) {
            super( TheRampStrings.getString( "forces.parallel-wall" ), rampLookAndFeel.getWallForceColor(), TheRampStrings.getString( "forces.Wall" ).toLowerCase() );
        }

        public double getValue( RampPhysicalModel rampPhysicalModel ) {
            return rampPhysicalModel.getParallelWallForce();
        }
    }
}
