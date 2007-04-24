/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.phetcommon.ShinyPanel;
import edu.colorado.phet.waveinterference.view.ImageOscillatorPNode;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.OscillatorOnOffControlPanel;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 7:44:31 PM
 *
 */

public class LightSourceGraphic extends ImageOscillatorPNode {

    public LightSourceGraphic( PhetPCanvas phetPCanvas, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( oscillator, latticeScreenCoordinates, "images/spotlight_3d.png" );
        JComponent control = ( new OscillatorOnOffControlPanel( oscillator ) );
        addChild( new PSwing(new ShinyPanel( control ) ) );
    }
}
