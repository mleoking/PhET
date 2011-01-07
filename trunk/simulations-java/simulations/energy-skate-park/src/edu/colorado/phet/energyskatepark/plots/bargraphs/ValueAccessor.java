// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots.bargraphs;

import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 1:48:27 PM
 */
public abstract class ValueAccessor {
    private String name;
    private Color color;

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
            super( EnergySkateParkStrings.getString( "energy.kinetic" ), lookAndFeel.getKEColor() );
        }

        public double getValue( EnergySkateParkModel rampPhysicalModel ) {
            if( rampPhysicalModel.getNumBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getBody( 0 ).getKineticEnergy();
        }
    }

    public static class PotentialEnergy extends ValueAccessor {
        public PotentialEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkStrings.getString( "energy.potential" ), lookAndFeel.getPEColor() );
        }

        public double getValue( EnergySkateParkModel rampPhysicalModel ) {
            if( rampPhysicalModel.getNumBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getBody( 0 ).getPotentialEnergy();
//            return rampPhysicalModel.getPotentialEnergy( rampPhysicalModel.getBody( 0 ) );
        }
    }

    public static class TotalEnergy extends ValueAccessor {
        public TotalEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkStrings.getString( "energy.total" ), lookAndFeel.getTotalEnergyColor() );
        }

        public double getValue( EnergySkateParkModel rampPhysicalModel ) {
            if( rampPhysicalModel.getNumBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getBody( 0 ).getTotalEnergy();
//            return rampPhysicalModel.getPotentialEnergy( rampPhysicalModel.getBody( 0 ) ) + rampPhysicalModel.getBody( 0 ).getKineticEnergy() + rampPhysicalModel.getThermalEnergy();
        }
    }

    public static class ThermalEnergy extends ValueAccessor {
        public ThermalEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkStrings.getString( "energy.thermal" ), lookAndFeel.getThermalEnergyColor() );
        }

        public double getValue( EnergySkateParkModel rampPhysicalModel ) {
            if( rampPhysicalModel.getNumBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getBody( 0 ).getThermalEnergy();
//            return rampPhysicalModel.getThermalEnergy();
        }
    }
}
