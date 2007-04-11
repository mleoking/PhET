/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.common.Legend;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 22, 2005
 * Time: 8:14:42 PM
 * Copyright (c) Dec 22, 2005 by Sam Reid
 */

public class EnergySkateParkLegend extends Legend {
    private EnergySkateParkModule ec3Module;

    public EnergySkateParkLegend( EnergySkateParkModule ec3Module ) {
        this.ec3Module = ec3Module;
        addEntry( EnergySkateParkStrings.getString( "kinetic.energy" ), ec3Module.getEnergyLookAndFeel().getKEColor() );
        addEntry( EnergySkateParkStrings.getString( "potential.energy" ), ec3Module.getEnergyLookAndFeel().getPEColor() );
        addEntry( EnergySkateParkStrings.getString( "thermal.energy" ), ec3Module.getEnergyLookAndFeel().getThermalEnergyColor() );
    }

    public void addNegPEEntry() {
        addEntry( EnergySkateParkStrings.getString( "negative.potential.energy" ), Color.black );
    }

    public void addTotalEnergyEntry() {
        addEntry( EnergySkateParkStrings.getString( "total.energy" ), ec3Module.getEnergyLookAndFeel().getTotalEnergyColor() );
    }
}
