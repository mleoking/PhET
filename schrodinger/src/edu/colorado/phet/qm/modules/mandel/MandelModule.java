/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.QWIApplication;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.model.WaveModel;
import edu.colorado.phet.qm.view.colormaps.ColorData;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:00:58 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelModule extends QWIModule {

    private MandelModel splitModel;
    private MandelSchrodingerPanel mandelSchrodingerPanel;
    private MandelControlPanel intensityControlPanel;
    private ArrayList listeners = new ArrayList();

    public MandelModule( QWIApplication app, IClock clock ) {
        super( "2 Lasers", app, clock );
        splitModel = new MandelModel();
        setDiscreteModel( splitModel );
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
//        addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                mandelSchrodingerPanel.updateDetectorColors();
//            }
//        } );
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
            BeamParam rightParam = new BeamParam( getRightGun().getWavelength(), getRightGun().getIntensity(), splitModel.getWaveModel() );
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
