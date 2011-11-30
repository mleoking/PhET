// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.common.Legend;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;

/**
 * User: Sam Reid
 * Date: Dec 22, 2005
 * Time: 8:14:42 PM
 */

public class EnergySkateParkLegend extends Legend {
    private final AbstractEnergySkateParkModule module;

    public EnergySkateParkLegend( AbstractEnergySkateParkModule module ) {
        this.module = module;
        addEntry( EnergySkateParkStrings.getString( "energy.kinetic.energy" ), module.getEnergyLookAndFeel().getKEColor() );
        addEntry( EnergySkateParkStrings.getString( "energy.potential.energy" ), module.getEnergyLookAndFeel().getPEColor() );
        addEntry( EnergySkateParkStrings.getString( "energy.thermal.energy" ), module.getEnergyLookAndFeel().getThermalEnergyColor() );
        setBackgroundPaint( EnergyLookAndFeel.getLegendBackground() );
    }

    public void addTotalEnergyEntry() {
        addEntry( EnergySkateParkStrings.getString( "energy.total.energy" ), module.getEnergyLookAndFeel().getTotalEnergyColor() );
    }
}
