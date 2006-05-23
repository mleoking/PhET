/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.view.ImageOscillatorPNode;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.OscillatorOnOffControlPanel;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 7:47:29 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class SpeakerGraphic extends ImageOscillatorPNode {
    public SpeakerGraphic( PSwingCanvas pSwingCanvas, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( oscillator, latticeScreenCoordinates, "images/speaker.gif" );
        OscillatorOnOffControlPanel oscillatorOnOffControlPanel = new OscillatorOnOffControlPanel( oscillator );
        PSwing pswing = new PSwing( pSwingCanvas, oscillatorOnOffControlPanel );
        addChild( pswing );
    }
}
