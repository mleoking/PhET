/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.controls.ClearButton;
import edu.colorado.phet.qm.controls.ResetButton;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 1:29:46 PM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelControlPanel extends SchrodingerControlPanel {
    public MandelControlPanel( MandelModule mandelModule ) {
        super( mandelModule );
        addControl( new ResetButton( mandelModule ) );
        addControl( new ClearButton( mandelModule.getSchrodingerPanel() ) );
//        addControl( new ExpandableDoubleSlitPanel( mandelModule ) );
//        AdvancedPanel advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );
//        advancedPanel.addControlFullWidth( super.createPotentialPanel( mandelModule ) );
//        advancedPanel.addControlFullWidth( new InverseSlitsCheckbox( getSchrodingerPanel() ) );
//        addControlFullWidth( advancedPanel );

//        final ModelSlider modelSlider = new ModelSlider( "Wave Dist from wall.", "", 0, 50, DoublePhotonWave.insetX );
//        modelSlider.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                int value = (int)modelSlider.getValue();
////                DoublePhotonWave.insetX = value;
//            }
//        } );
//
//
//        final ModelSlider dPhase = new ModelSlider( "Delta Phase", "", 0, Math.PI * 2, DoublePhotonWave.dPhase );
//        dPhase.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
////                DoublePhotonWave.dPhase = dPhase.getValue();
//            }
//        } );
//
//        final ModelSlider dSigma = new ModelSlider( "Wave Width", "", 1, 12, MandelDampedWave.dxLattice );
//        dSigma.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                MandelDampedWave.dxLattice = dSigma.getValue();
//            }
//        } );

//        addControlFullWidth( modelSlider );
//        addControlFullWidth( dPhase );
//        addControlFullWidth( dSigma );


    }
}
