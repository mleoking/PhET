package edu.colorado.phet.ec3.view.bargraphs;

import edu.colorado.phet.ec3.EnergyLookAndFeel;
import edu.colorado.phet.ec3.EnergySkateParkStrings;
import edu.colorado.phet.ec3.model.EnergyConservationModel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 1:48:27 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */
public abstract class ValueAccessor {
    String name;
    Color color;

    protected ValueAccessor( String name, Color color ) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public abstract double getValue( EnergyConservationModel rampPhysicalModel );

    public Color getColor() {
        return color;
    }


    public static class KineticEnergy extends ValueAccessor {
        public KineticEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkStrings.getString( "kinetic" ), lookAndFeel.getKEColor() );
        }

        public double getValue( EnergyConservationModel rampPhysicalModel ) {
            if( rampPhysicalModel.numBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.bodyAt( 0 ).getKineticEnergy();
        }
    }

    public static class PotentialEnergy extends ValueAccessor {
        public PotentialEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkStrings.getString( "potential" ), lookAndFeel.getPEColor() );
        }

        public double getValue( EnergyConservationModel rampPhysicalModel ) {
            if( rampPhysicalModel.numBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getPotentialEnergy( rampPhysicalModel.bodyAt( 0 ) );
        }
    }

    public static class TotalEnergy extends ValueAccessor {
        public TotalEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkStrings.getString( "total" ), lookAndFeel.getTotalEnergyColor() );
        }

        public double getValue( EnergyConservationModel rampPhysicalModel ) {
            if( rampPhysicalModel.numBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getPotentialEnergy( rampPhysicalModel.bodyAt( 0 ) ) + rampPhysicalModel.bodyAt( 0 ).getKineticEnergy() + rampPhysicalModel.getThermalEnergy();
        }
    }

    public static class ThermalEnergy extends ValueAccessor {
        public ThermalEnergy( EnergyLookAndFeel lookAndFeel ) {
            super( EnergySkateParkStrings.getString( "thermal" ), lookAndFeel.getThermalEnergyColor() );
        }

        public double getValue( EnergyConservationModel rampPhysicalModel ) {
            if( rampPhysicalModel.numBodies() == 0 ) {
                return 0;
            }
            return rampPhysicalModel.getThermalEnergy();
        }
    }
}
