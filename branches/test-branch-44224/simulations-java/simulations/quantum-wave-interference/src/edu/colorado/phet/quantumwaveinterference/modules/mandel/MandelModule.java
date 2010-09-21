/*  */
package edu.colorado.phet.quantumwaveinterference.modules.mandel;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.QuantumWaveInterferenceApplication;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.model.WaveModel;
import edu.colorado.phet.quantumwaveinterference.view.colormaps.ColorData;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:00:58 AM
 */

public class MandelModule extends QWIModule {

    private MandelModel splitModel;
    private MandelSchrodingerPanel mandelSchrodingerPanel;
    private MandelControlPanel intensityControlPanel;
    private ArrayList listeners = new ArrayList();

    public MandelModule( QuantumWaveInterferenceApplication app, IClock clock ) {
        super( QWIResources.getString( "module.lasers" ), app, clock );
        splitModel = new MandelModel();
        setQWIModel( splitModel );
        mandelSchrodingerPanel = new MandelSchrodingerPanel( this );
        setSchrodingerPanel( mandelSchrodingerPanel );
        intensityControlPanel = new MandelControlPanel( this );
        setSchrodingerControlPanel( intensityControlPanel );

        finishInit();
        MandelGun.Listener listener = new MandelGun.Listener() {
            public void wavelengthChanged() {
                clearWaves();
                mandelSchrodingerPanel.wavelengthChanged();
                mandelSchrodingerPanel.updateDetectorColors();
                synchronizeModel();
            }

            public void intensityChanged() {
                synchronizeModel();
                mandelSchrodingerPanel.updateDetectorColors();
            }
        };
        mandelSchrodingerPanel.updateDetectorColors();
        getLeftGun().addListener( listener );
        getRightGun().addListener( listener );

        synchronizeModel();
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 1, VisibleColor.MIN_WAVELENGTH, VisibleColor.MAX_WAVELENGTH );
        getLeftGun().setWavelength( linearFunction.evaluate( 0.75 / 3.0 ) );
        getRightGun().setWavelength( linearFunction.evaluate( 2.0 / 3.0 ) );
    }

    private void clearWaves() {
        getSplitModel().clearAllWaves();
    }

    public MandelModel getSplitModel() {
        return splitModel;
    }

    public MandelSchrodingerPanel getMandelSchrodingerPanel() {
        return mandelSchrodingerPanel;
    }

    public ColorData getRootColor() {
        return mandelSchrodingerPanel.getRootColor();
    }

    public MandelModel getMandelModel() {
        return splitModel;
    }

    public static interface Listener {
        void detectorsChanged();
    }

    public static class Adapter implements Listener {

        public void detectorsChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private double getWavefunctionDifference() {
        return Math.abs( getLeftGun().getWavelength() - getRightGun().getWavelength() );
    }

    private MandelGun getLeftGun() {
        return mandelSchrodingerPanel.getLeftGun();
    }

    private MandelGun getRightGun() {
        return mandelSchrodingerPanel.getRightGun();
    }

    public static class BeamParam {
        double momentum;
        double intensity;
        WaveModel wavefunction;

        public BeamParam( double momentum, double leftIntensity, WaveModel leftWavefunction ) {
            this.momentum = momentum;
            this.intensity = leftIntensity;
            this.wavefunction = leftWavefunction;
        }

        public double getMomentum() {
            return momentum;
        }

        public double getIntensity() {
            return intensity;
        }

        public WaveModel getWaveModel() {
            return wavefunction;
        }

        public String toString() {
            return "wavelength=" + momentum + ", intensity=" + intensity + ", wavefunction=" + wavefunction;
        }

        public void setMomentum( double momentum ) {
            this.momentum = momentum;
        }
    }

    private void synchronizeModel() {
//        System.out.println( "getWavefunctionDifference() = " + getWavefunctionDifference() );
        if( getWavefunctionDifference() < 10 ) {
            setSplitModel( false );
            BeamParam leftParam = new BeamParam( getLeftGun().getWavelength(), getLeftGun().getIntensity(), splitModel.getWaveModel() );
//            BeamParam rightParam = new BeamParam( getRightGun().getWavelength(), getRightGun().getIntensity(), splitModel.getWaveModel() );
            BeamParam rightParam = new BeamParam( getLeftGun().getWavelength(), getRightGun().getIntensity(), splitModel.getWaveModel() );//todo here we're setting the right gun's wavelength to be that of the left gun.  Hopefully the controls don't reflect this, or it's not a problem.
            mandelSchrodingerPanel.getMandelGunSet().setBeamParameters( leftParam, rightParam );
        }
        else {
            setSplitModel( true );
            BeamParam leftParam = new BeamParam( getLeftGun().getWavelength(), getLeftGun().getIntensity(), splitModel.getLeftWaveModel() );
            BeamParam rightParam = new BeamParam( getRightGun().getWavelength(), getRightGun().getIntensity(), splitModel.getRightWaveModel() );
            mandelSchrodingerPanel.getMandelGunSet().setBeamParameters( leftParam, rightParam );
        }
    }

    private void setSplitModel( boolean splitMode ) {
        splitModel.setSplitMode( splitMode );
        mandelSchrodingerPanel.setSplitMode( splitMode );
    }

}
