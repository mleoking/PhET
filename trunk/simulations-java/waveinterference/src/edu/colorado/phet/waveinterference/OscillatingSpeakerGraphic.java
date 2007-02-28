/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.piccolo.util.PImageFactory;
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
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class OscillatingSpeakerGraphic extends ImageOscillatorPNode {
    private PImage cone;
    private double x0 = -2;
    private double amp = 5 / 1.5;
    private PImage foreground;

    public OscillatingSpeakerGraphic( PSwingCanvas pSwingCanvas, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( oscillator, latticeScreenCoordinates, "images/speaker-back.png" );
        cone = PImageFactory.create( "images/speaker-cone.png" );
//        cone.setOffset( 30, 0 );
        addChild( cone );
        OscillatorOnOffControlPanel oscillatorOnOffControlPanel = new OscillatorOnOffControlPanel( oscillator );

        foreground = PImageFactory.create( "images/speaker-front.png" );
        addChild( foreground );

        PSwing pswing = new PSwing( pSwingCanvas, new ShinyPanel( oscillatorOnOffControlPanel ) );
        addChild( pswing );
        update();

        ResizeHandler.getInstance().setResizable( pSwingCanvas, this );
    }

    public void update() {
        double value = getOscillator().getValue() * ( getOscillator().isEnabled() ? 1.0 : 0.0 );
        double offsetX = x0 + amp * value;
        cone.setOffset( offsetX, 0 );
    }
}
