/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.tests.RotationWaveGraphic;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:18:38 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaveRotateControl extends VerticalLayoutPanel {
    public WaveRotateControl( final RotationWaveGraphic rotationWaveGraphic ) {
        final ModelSlider rotate = new ModelSlider( "View Angle", "radians", 0, Math.PI / 2, rotationWaveGraphic.getRotation() );
        rotate.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rotationWaveGraphic.setRotation( rotate.getValue() );
            }
        } );
        add( rotate );
    }
}
