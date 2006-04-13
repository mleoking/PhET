/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 12:00:58 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class RotationFaucetGraphic extends FaucetGraphic {
    private RotationWaveGraphic rotationWaveGraphic;

    public RotationFaucetGraphic( WaveModel waveModel, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates, RotationWaveGraphic rotationWaveGraphic ) {
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
