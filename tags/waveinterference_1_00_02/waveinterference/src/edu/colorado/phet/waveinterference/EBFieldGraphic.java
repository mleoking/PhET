/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.WaveSideView;

/**
 * User: Sam Reid
 * Date: Mar 31, 2006
 * Time: 8:20:34 PM
 * Copyright (c) Mar 31, 2006 by Sam Reid
 */

public class EBFieldGraphic extends WaveSideView {
    private EFieldGraphic eFieldGraphic;
    private BFieldGraphic bFieldGraphic;

    public EBFieldGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, int distBetweenSamples ) {
        super( waveModel, latticeScreenCoordinates );
        eFieldGraphic = new EFieldGraphic( waveModel, latticeScreenCoordinates, distBetweenSamples );
        bFieldGraphic = new BFieldGraphic( waveModel, latticeScreenCoordinates, distBetweenSamples );
        addChild( eFieldGraphic );
        addChild( bFieldGraphic );
    }

    public void update() {
        if( eFieldGraphic != null && bFieldGraphic != null ) {
            eFieldGraphic.update();
            bFieldGraphic.update();
        }
    }

}
