/*, 2003.*/
package edu.colorado.phet.semiconductor_semi.macro.energy.states;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.semiconductor_semi.macro.energy.EnergySection;

/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 11:48:44 AM
 *
 */
public class ExitLeftState extends StateChain {

    public ExitLeftState( PhetVector dest, Speed speed, EnergySection section ) {
        super();

        addState( new MoveToPosition( dest, speed ) );
        addState( new Remove( section ) );
    }

}
