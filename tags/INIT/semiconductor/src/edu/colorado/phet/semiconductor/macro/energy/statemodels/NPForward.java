/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 8:50:15 AM
 * Copyright (c) Mar 15, 2004 by Sam Reid
 */
public class NPForward extends ForwardBiasDiode {

    public NPForward( EnergySection energySection, EnergyCell entryPoint, EnergyCell exitCell ) {
        super( energySection, entryPoint, exitCell, 1 );
    }

}
