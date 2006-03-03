/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.SplitModel;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.view.colormaps.ColorData;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:00:58 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelModule extends SchrodingerModule {

    private SplitModel splitModel;
    private MandelSchrodingerPanel mandelSchrodingerPanel;
    private MandelControlPanel intensityControlPanel;
    private ArrayList listeners = new ArrayList();

    public MandelModule( SchrodingerApplication app, IClock clock ) {
        super( "2 Lasers", app, clock );
        splitModel = new SplitModel();
        setDiscreteModel( splitModel );
        mandelSchrodingerPanel = new MandelSchrodingerPanel( this );
        setSchrodingerPanel( mandelSchrodingerPanel );
        intensityControlPanel = new MandelControlPanel( this );
        setSchrodingerControlPanel( intensityControlPanel );

        finishInit();
        MandelGun.Listener listener = new MandelGun.Listener() {
            public void wavelengthChanged() {
                synchronizeModel();
            }

            public void intensityChanged() {
                synchronizeModel();
            }
        };
        getLeftGun().addListener( listener );
        getRightGun().addListener( listener );
        synchronizeModel();
    }

    public SplitModel getSplitModel() {
        return splitModel;
    }

    public HighIntensitySchrodingerPanel getIntensityPanel() {
        return mandelSchrodingerPanel;
    }

    public boolean isRightDetectorEnabled() {
        return splitModel.containsDetector( splitModel.getRightDetector() );
    }

    public boolean isLeftDetectorEnabled() {
        return splitModel.containsDetector( splitModel.getLeftDetector() );
    }

    public ColorData getRootColor() {
        return mandelSchrodingerPanel.getRootColor();
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

    private boolean isRightGunOn() {
        return getRightGun().isOn();
    }

    private MandelGun getRightGun() {
        return mandelSchrodingerPanel.getRightGun();
    }

    private boolean isLeftGunOn() {
        return getLeftGun().isOn();
    }

    private void synchronizeModel() {
        //four cases:
        if( isLeftGunOn() && isRightGunOn() && getWavefunctionDifference() < 10 ) {
            setSplitMode( false );
        }
        else {
            setSplitMode( true );
        }
        if( isLeftGunOn() && isRightGunOn() && getWavefunctionDifference() >= 10 ) {

        }
        else if( isLeftGunOn() ) {
            if( isLeftGunOn() && !isRightGunOn() ) {

            }
        }
    }

    private void setSplitMode( boolean splitMode ) {
        splitModel.setSplitMode( splitMode );
        mandelSchrodingerPanel.setSplitMode( splitMode );
    }

}
