// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.common.Legend;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;

/**
 * User: Sam Reid
 * Date: Dec 22, 2005
 * Time: 8:14:42 PM
 */

public class EnergySkateParkLegend extends Legend {
    private EnergySkateParkModule module;

    public EnergySkateParkLegend( EnergySkateParkModule module ) {
        this.module = module;
        addEntry( EnergySkateParkStrings.getEnergyString( "energy.kinetic" ), module.getEnergyLookAndFeel().getKEColor() );
        addEntry( EnergySkateParkStrings.getEnergyString( "energy.potential" ), module.getEnergyLookAndFeel().getPEColor() );
        addEntry( EnergySkateParkStrings.getEnergyString( "energy.thermal" ), module.getEnergyLookAndFeel().getThermalEnergyColor() );
        setBackgroundPaint( EnergyLookAndFeel.getLegendBackground() );
    }

    public void addTotalEnergyEntry() {
        addEntry( EnergySkateParkStrings.getEnergyString( "energy.total" ), module.getEnergyLookAndFeel().getTotalEnergyColor() );
    }
}
