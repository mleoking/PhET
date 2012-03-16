// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots.bargraphs;

import java.awt.Color;

import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 1:48:27 PM
 */
public abstract class ValueAccessor {
    private final String name;
    private final Color color;

    protected ValueAccessor( String name, Color color ) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public abstract double getValue( EnergySkateParkModel rampPhysicalModel );

    public Color getColor() {
        return color;
    }


    public static class KineticEnergy extends ValueAccessor {
        public KineticEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkResources.getString( "energy.kinetic" ), lookAndFeel.getKEColor() );
        }

        public double getValue( EnergySkateParkModel rampPhysicalModel ) {
            if ( rampPhysicalModel.getNumBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getBody( 0 ).getKineticEnergy();
        }
    }

    public static class PotentialEnergy extends ValueAccessor {
        public PotentialEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkResources.getString( "energy.potential" ), lookAndFeel.getPEColor() );
        }

        public double getValue( EnergySkateParkModel rampPhysicalModel ) {
            if ( rampPhysicalModel.getNumBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getBody( 0 ).getPotentialEnergy();
        }
    }

    public static class TotalEnergy extends ValueAccessor {
        public TotalEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkResources.getString( "energy.total" ), lookAndFeel.getTotalEnergyColor() );
        }

        public double getValue( EnergySkateParkModel rampPhysicalModel ) {
            if ( rampPhysicalModel.getNumBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getBody( 0 ).getTotalEnergy();
        }
    }

    public static class ThermalEnergy extends ValueAccessor {
        public ThermalEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkResources.getString( "energy.thermal" ), lookAndFeel.getThermalEnergyColor() );
        }

        public double getValue( EnergySkateParkModel rampPhysicalModel ) {
            if ( rampPhysicalModel.getNumBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getBody( 0 ).getThermalEnergy();
        }
    }
}
