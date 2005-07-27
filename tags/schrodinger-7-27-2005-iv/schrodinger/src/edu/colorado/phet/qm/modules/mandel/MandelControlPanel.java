/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 1:29:46 PM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelControlPanel extends ControlPanel {
    public MandelControlPanel( MandelModule mandelModule ) {
        super( mandelModule );
        final ModelSlider modelSlider = new ModelSlider( "Wave Dist from wall.", "", 0, 50, DoublePhotonWave.insetX );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int value = (int)modelSlider.getValue();
                DoublePhotonWave.insetX = value;
            }
        } );

        addControlFullWidth( modelSlider );

        final ModelSlider dPhase = new ModelSlider( "Delta Phase", "", 0, Math.PI * 2, DoublePhotonWave.dPhase );
        dPhase.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                DoublePhotonWave.dPhase = dPhase.getValue();
            }
        } );
        addControlFullWidth( dPhase );

        final ModelSlider dSigma = new ModelSlider( "Wave Width", "", 1, 12, MandelDampedWave.dxLattice );
        dSigma.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                MandelDampedWave.dxLattice = dSigma.getValue();
            }
        } );
        addControlFullWidth( dSigma );
    }
}
