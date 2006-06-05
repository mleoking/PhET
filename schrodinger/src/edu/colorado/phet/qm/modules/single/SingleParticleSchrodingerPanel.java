/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.gun.SingleParticleGunNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:13 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleSchrodingerPanel extends QWIPanel {
    private SingleParticleGunNode abstractGun;

    public SingleParticleSchrodingerPanel( final QWIModule module ) {
        super( module );
        abstractGun = new SingleParticleGunNode( this );
        setGunGraphic( abstractGun );
        getSchrodingerScreenNode().setGunControlPanel( abstractGun.getGunControlPanel() );
        ImagePComboBox comboBox = abstractGun.getComboBox();

        PSwing pSwing = new PSwing( this, comboBox );
        comboBox.setEnvironment( pSwing, this );
        getSchrodingerScreenNode().setGunTypeChooserGraphic( pSwing );

        getIntensityDisplay().setMultiplier( 1 );
        getIntensityDisplay().setProbabilityScaleFudgeFactor( 5 );
        getDetectorSheetPNode().setOpacity( 255 );
        getIntensityDisplay().setNormDecrement( 1.0 );

        getIntensityDisplay().addListener( new IntensityManager.Listener() {
            public void detectionOccurred() {
                module.getQWIModel().enableAllDetectors();
            }
        } );
    }

    public void reset() {
        super.reset();
        abstractGun.reset();
    }

    public void clearWavefunction() {
        super.clearWavefunction();
        abstractGun.reset();
    }

    public SingleParticleGunNode getAbstractGun() {
        return abstractGun;
    }
}
