/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;

/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 11:48:44 AM
 * Copyright (c) Mar 17, 2004 by Sam Reid
 */
public class ExitRightState extends StateChain {

    public ExitRightState( PhetVector dest, Speed speed, EnergySection section ) {
        super();

        addState( new MoveToPosition( dest, speed ) );
        addState( new Remove( section ) );
    }

}
