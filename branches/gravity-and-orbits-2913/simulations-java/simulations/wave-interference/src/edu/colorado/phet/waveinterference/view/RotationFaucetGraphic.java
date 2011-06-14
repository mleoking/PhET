// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 12:00:58 AM
 */

public class RotationFaucetGraphic extends FaucetGraphic {
    private RotationWaveGraphic rotationWaveGraphic;

    public RotationFaucetGraphic( PSwingCanvas pSwingCanvas, WaveModel waveModel, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates, RotationWaveGraphic rotationWaveGraphic ) {
        super( waveModel, oscillator, latticeScreenCoordinates );
        this.rotationWaveGraphic = rotationWaveGraphic;
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                update();
            }

        } );
        update();
    }

    private void update() {
        super.setClipDrops( !rotationWaveGraphic.isSideView() );
    }

}
