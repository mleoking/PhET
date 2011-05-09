// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import javax.swing.*;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.phetcommon.ShinyPanel;
import edu.colorado.phet.waveinterference.view.ImageOscillatorPNode;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.OscillatorOnOffControlPanel;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 7:44:31 PM
 */

public class LightSourceGraphic extends ImageOscillatorPNode {

    public LightSourceGraphic( Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( oscillator, latticeScreenCoordinates, "wave-interference/images/spotlight_3d.png" );
        JComponent control = ( new OscillatorOnOffControlPanel( oscillator ) );
        final PSwing swing = new PSwing( new ShinyPanel( control ) );
//        swing.translate( 0, -50 );
        addChild( swing );

    }
}
