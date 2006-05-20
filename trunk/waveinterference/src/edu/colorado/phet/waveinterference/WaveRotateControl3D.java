/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.util.WISwingUtil;
import edu.colorado.phet.waveinterference.view.RotationWaveGraphic;
import edu.colorado.phet.waveinterference.view.WaveRotateControl;

/**
 * User: Sam Reid
 * Date: May 17, 2006
 * Time: 11:28:46 AM
 * Copyright (c) May 17, 2006 by Sam Reid
 */

public class WaveRotateControl3D extends WaveRotateControl {
    private WaveInterferenceModel model;

    public WaveRotateControl3D( WaveInterferenceModel model, RotationWaveGraphic rotationWaveGraphic ) {
        super( rotationWaveGraphic );
        this.model = model;

        model.addListener( new WaveInterferenceModel.Listener() {
            public void symmetryChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        setEnabled( model.isSymmetric() );
    }

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        WISwingUtil.setChildrenEnabled( this, enabled );
    }

}
