/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.common.Legend;

/**
 * User: Sam Reid
 * Date: Dec 22, 2005
 * Time: 8:14:42 PM
 * Copyright (c) Dec 22, 2005 by Sam Reid
 */

public class EC3Legend extends Legend {
    private EC3Module ec3Module;

    public EC3Legend( EC3Module ec3Module ) {
        this.ec3Module = ec3Module;
        addEntry( "Kinetic Energy", ec3Module.getEnergyLookAndFeel().getKEColor() );
        addEntry( "Potential Energy", ec3Module.getEnergyLookAndFeel().getPEColor() );
        addEntry( "Thermal Energy", ec3Module.getEnergyLookAndFeel().getThermalEnergyColor() );
    }

    public void addTotalEnergyEntry() {
        addEntry( "Total Energy", ec3Module.getEnergyLookAndFeel().getTotalEnergyColor() );
    }
}
