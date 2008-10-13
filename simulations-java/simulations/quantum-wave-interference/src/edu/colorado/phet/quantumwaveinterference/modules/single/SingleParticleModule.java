/*  */
package edu.colorado.phet.quantumwaveinterference.modules.single;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.QuantumWaveInterferenceApplication;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.WaveModel;
import edu.colorado.phet.quantumwaveinterference.view.gun.AbstractGunNode;
import edu.colorado.phet.quantumwaveinterference.view.gun.SingleParticleGunNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.IntensityManager;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:05:52 AM
 */

public class SingleParticleModule extends QWIModule {
    public SingleParticleSchrodingerPanel schrodingerSchrodingerPanel;
    int count;

    public SingleParticleModule( QuantumWaveInterferenceApplication application, IClock clock ) {
        super( QWIResources.getString( "module.single-particles" ), application, clock );
        setQWIModel( new QWIModel() );
        schrodingerSchrodingerPanel = new SingleParticleSchrodingerPanel( this );
        setSchrodingerPanel( schrodingerSchrodingerPanel );
        setSchrodingerControlPanel( new SingleParticleControlPanel( this ) );
//        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setBrightnessSliderVisible( false );

        //•	If I run QWI on auto-repeat for single particles and a double slit for a long time, I would like an interference pattern to build up, but it doesn’t because the dots on the screen fade too fast.  The rate of fading should be much slower in single particle mode than in the other two cases, since the dots don’t build up as fast.
//        getSchrodingerPanel().getDetectorSheetPNode().setFadeDelay( DetectorSheetPNode.DEFAULT_FADE_DELAY * 10 );

        //•	It would be nice to be have the fade checkbox in single particle mode so that I can turn it off.
//        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setFadeCheckBoxVisible( false );
        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setTypeControlVisible( false );
        getSchrodingerPanel().getDetectorSheetPNode().updatePSwing();
//        setMinimumProbabilityForDetection( 0.05 );
        getSingleParticleGunNode().addSingleParticleGunNodeListener( new SingleParticleGunNode.SingleParticleGunNodeListener() {
            public void gunParticleTypeChanged() {
//                System.out.println( "type=" + getSingleParticleGunNode().getGunParticle() );
                updateDetectionThresholds();
            }
        } );
        getSchrodingerPanel().setFadeEnabled( true );
        updateDetectionThresholds();
        setClockControlPanel( new SingleParticleClockControlPanel( this, clock ) );

        getSingleParticleGunNode().addListener( new AbstractGunNode.Listener() {
            public void gunFired() {
                count = 0;
            }
        } );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                count++;

//                System.out.println( "count = " + count );
                if( count > 100 ) {
                    reduceWavefunction();
                }
            }

        } );
        finishInit();
    }

    private void reduceWavefunction() {
        WaveModel waveModel = getQWIModel().getWaveModel();
        double origMagnitude = waveModel.getWavefunction().getMagnitude();
        double newMagnitude = ( Math.max( origMagnitude - 0.01, 0 ) ) * 0.9;
//        System.out.println( "origMagnitude = " + origMagnitude+", new="+newMagnitude );
        waveModel.setMagnitude( newMagnitude );
    }

    private void updateDetectionThresholds() {
        setMinimumProbabilityForDetection( getSingleParticleGunNode().getGunParticle().getMinimumProbabilityForDetection() );
        setTimeThreshold( getSingleParticleGunNode().getGunParticle().getTimeThresholdAllowed() );
        setTimeThresholdCount( getSingleParticleGunNode().getGunParticle().getTimeThresholdCount() );
    }


    private SingleParticleGunNode getSingleParticleGunNode() {
        return schrodingerSchrodingerPanel.getSingleParticleGunNode();
    }

    public void rapidTest() {
//        AbstractGunNode gun = getSchrodingerPanel().getGunGraphic();
//        if( gun instanceof SingleParticleGunNode ) {
//            SingleParticleGunNode singleParticleGunNode = (SingleParticleGunNode)gun;
//            singleParticleGunNode.fireParticle();
//        }
        SwingClock clock = (SwingClock)getClock();
        clock.setDelay( 0 );
    }

    public void setRapid( boolean rapid ) {
        SwingClock clock = (SwingClock)getClock();
        clock.setDelay( rapid ? 0 : 30 );
    }

    public IntensityManager getIntensityManager() {
        return getSchrodingerPanel().getSchrodingerScreenNode().getIntensityDisplay();
    }
}
