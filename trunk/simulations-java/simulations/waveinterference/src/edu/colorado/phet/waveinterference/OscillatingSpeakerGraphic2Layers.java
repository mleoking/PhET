/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.phetcommon.ShinyPanel;
import edu.colorado.phet.waveinterference.view.ImageOscillatorPNode;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.OscillatorOnOffControlPanel;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 7:47:29 PM
 */

public class OscillatingSpeakerGraphic2Layers extends ImageOscillatorPNode {
    private PImage cone;
    private double x0 = 35;
    private double amp = 5 / 1.5;

    public OscillatingSpeakerGraphic2Layers( PSwingCanvas pSwingCanvas, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( oscillator, latticeScreenCoordinates, "waveinterference/images/speaker-frame.gif" );
        cone = PImageFactory.create( "waveinterference/images/speaker-cone.gif" );
        cone.setOffset( 30, 0 );
        addChild( cone );
        OscillatorOnOffControlPanel oscillatorOnOffControlPanel = new OscillatorOnOffControlPanel( oscillator );
        PSwing pswing = new PSwing( new ShinyPanel( oscillatorOnOffControlPanel ) );
        addChild( pswing );
    }

    public void update() {
        double value = getOscillator().getValue() * ( getOscillator().isEnabled() ? 1.0 : 0.0 );
        double offsetX = x0 + amp * value;
        cone.setOffset( offsetX, 0 );
    }
}
