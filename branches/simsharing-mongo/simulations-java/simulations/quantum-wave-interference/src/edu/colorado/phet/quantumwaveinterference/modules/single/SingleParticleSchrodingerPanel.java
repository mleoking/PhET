// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.modules.single;

import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.phetcommon.ImagePComboBox;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.gun.SingleParticleGunNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.IntensityManager;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:13 AM
 */

public class SingleParticleSchrodingerPanel extends QWIPanel {
    private SingleParticleGunNode abstractGun;

    public SingleParticleSchrodingerPanel( final QWIModule module ) {
        super( module );
        abstractGun = new SingleParticleGunNode( this );
        setGunGraphic( abstractGun );
        getSchrodingerScreenNode().setGunControlPanel( abstractGun.getGunControlPanel() );
        ImagePComboBox comboBox = abstractGun.getComboBox();

        PSwing pSwing = new PSwing( comboBox );
        comboBox.setEnvironment( pSwing, this );
        getSchrodingerScreenNode().setGunTypeChooserGraphic( pSwing );

        getIntensityDisplay().setMultiplier( 1 );
        getIntensityDisplay().setProbabilityScaleFudgeFactor( 5 );

        getDetectorSheetPNode().setBrightness( 0.75 );
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

    public SingleParticleGunNode getSingleParticleGunNode() {
        return abstractGun;
    }
}
