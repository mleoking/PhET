/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.WaveSideView;

/**
 * User: Sam Reid
 * Date: Mar 31, 2006
 * Time: 7:45:45 PM
 * Copyright (c) Mar 31, 2006 by Sam Reid
 */

public class WaveSideViewPhoton extends WaveSideView {
    private WaveSideView vectorViewGraphic;

    public WaveSideViewPhoton( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( waveModel, latticeScreenCoordinates );
        vectorViewGraphic = new EFieldGraphic( waveModel, latticeScreenCoordinates, 2 );
//        vectorViewGraphic = new WaveSideView( lattice, latticeScreenCoordinates);
        addChild( vectorViewGraphic );
        update();
    }

    public void update() {
        super.update();
        if( vectorViewGraphic != null ) {
            vectorViewGraphic.update();
        }
    }

}
