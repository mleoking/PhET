// Copyright 2002-2012, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;


/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 11:48:44 AM
 */
public class ExitLeftState extends StateChain {

    public ExitLeftState( MutableVector2D dest, Speed speed, EnergySection section ) {
        super();

        addState( new MoveToPosition( dest, speed ) );
        addState( new Remove( section ) );
    }

}
