// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.AbstractWaveSideView;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

/**
 * User: Sam Reid
 * Date: May 17, 2006
 * Time: 11:16:34 AM
 */

public class WaveModelGraphicRotationAdapter extends AbstractWaveSideView {
    private WaveModelGraphic waveModelGraphic;

    public WaveModelGraphicRotationAdapter( WaveModelGraphic waveModelGraphic ) {
        super();
        addChild( waveModelGraphic );
        this.waveModelGraphic = waveModelGraphic;
    }

    public void setSpaceBetweenCells( double dim ) {
    }

    public void update() {
        waveModelGraphic.update();
    }
}
