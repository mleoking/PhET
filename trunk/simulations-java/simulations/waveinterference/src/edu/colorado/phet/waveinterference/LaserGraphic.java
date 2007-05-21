/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.view.ImageOscillatorPNode;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 7:44:31 PM
 *
 */

public class LaserGraphic extends ImageOscillatorPNode {

    public LaserGraphic( Oscillator primaryOscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( primaryOscillator, latticeScreenCoordinates, "waveinterference/images/laser.gif" );
    }
}
