/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.colorado.phet.energyskatepark.common.Legend;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 22, 2005
 * Time: 8:14:42 PM
 */

public class EnergySkateParkLegend extends Legend {
    private EnergySkateParkModule ec3Module;

    public EnergySkateParkLegend( EnergySkateParkModule ec3Module ) {
        this.ec3Module = ec3Module;
        addEntry( EnergySkateParkStrings.getEnergyString( "energy.kinetic" ), ec3Module.getEnergyLookAndFeel().getKEColor() );
        addEntry( EnergySkateParkStrings.getEnergyString( "energy.potential" ), ec3Module.getEnergyLookAndFeel().getPEColor() );
        addEntry( EnergySkateParkStrings.getEnergyString( "energy.thermal" ), ec3Module.getEnergyLookAndFeel().getThermalEnergyColor() );
        setBackgroundPaint( EnergyLookAndFeel.getLegendBackground() );
    }

    public void addTotalEnergyEntry() {
        addEntry( EnergySkateParkStrings.getEnergyString( "energy.total" ), ec3Module.getEnergyLookAndFeel().getTotalEnergyColor() );
    }
}
