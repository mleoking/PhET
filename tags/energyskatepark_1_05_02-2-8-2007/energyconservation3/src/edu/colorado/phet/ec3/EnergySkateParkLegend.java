/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.common.Legend;

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
