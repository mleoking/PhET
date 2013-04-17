// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.RotationWaveGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 5, 2007
 * Time: 9:48:16 PM
 */
public class WaveSizeButtonPSwing extends PNode {
    private PSwing waveSizeButtonPSwing;
    private RotationWaveGraphic rotationWaveGraphic;

    public WaveSizeButtonPSwing( RotationWaveGraphic rotationWaveGraphic, Maximizable maximizable ) {
        this.rotationWaveGraphic = rotationWaveGraphic;
        WaveSizeButton waveSizeButton = new WaveSizeButton( rotationWaveGraphic, maximizable );
        waveSizeButtonPSwing = new PSwing( waveSizeButton );
        waveSizeButtonPSwing.addInputEventListener( new CursorHandler() );
        addChild( waveSizeButtonPSwing );

        rotationWaveGraphic.getLatticeScreenCoordinates().addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateWaveSizeButtonLocation();
            }
        } );
        updateWaveSizeButtonLocation();
    }

    private void updateWaveSizeButtonLocation() {
//        waveSizeButtonPSwing.setOffset( rotationWaveGraphic.getFullBounds().getMaxX() - waveSizeButtonPSwing.getWidth(), rotationWaveGraphic.getFullBounds().getY() );
        waveSizeButtonPSwing.setOffset( rotationWaveGraphic.getLatticeScreenCoordinates().getScreenRect().getMaxX() - waveSizeButtonPSwing.getWidth(), rotationWaveGraphic.getLatticeScreenCoordinates().getScreenRect().getY() );
    }
}
