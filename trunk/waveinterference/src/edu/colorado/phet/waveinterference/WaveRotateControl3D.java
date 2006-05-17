/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.RotationWaveGraphic;
import edu.colorado.phet.waveinterference.view.WaveRotateControl;

import java.awt.*;

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
        setEnabled( this, model.isSymmetric() );
        System.out.println( "model.isSymmetric() = " + model.isSymmetric() );
    }

    public void setEnabled( Component c, boolean enabled ) {
        c.setEnabled( enabled );
        if( c instanceof Container ) {
            Container c2 = (Container)c;
            for( int i = 0; i < c2.getComponentCount(); i++ ) {
                Component a = c2.getComponent( i );
                setEnabled( a, enabled );
            }
        }
    }
}
