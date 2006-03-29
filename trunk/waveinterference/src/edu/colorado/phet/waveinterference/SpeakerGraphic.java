/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.view.ImageOscillatorPNode;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 7:47:29 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class SpeakerGraphic extends ImageOscillatorPNode {
    public SpeakerGraphic( Oscillator primaryOscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( primaryOscillator, latticeScreenCoordinates, "images/speaker.gif" );
    }
}
