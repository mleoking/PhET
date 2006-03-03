/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.view.colormaps.ColorData;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:00:58 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelModule extends SchrodingerModule {

    private MandelModel splitModel;
    private MandelSchrodingerPanel mandelSchrodingerPanel;
    private MandelControlPanel intensityControlPanel;
    private ArrayList listeners = new ArrayList();

    public MandelModule( SchrodingerApplication app, IClock clock ) {
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
                mandelSchrodingerPanel.wavelengthChanged();
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

    public MandelModel getSplitModel() {
        return splitModel;
    }

    public MandelSchrodingerPanel getMandelSchrodingerPanel() {
        return mandelSchrodingerPanel;
    }

//    public boolean isRightDetectorEnabled() {
//        return splitModel.containsDetector( splitModel.getRightDetector() );
//    }
//
//    public boolean isLeftDetectorEnabled() {
//        return splitModel.containsDetector( splitModel.getLeftDetector() );
//    }

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

    private MandelGun getRightGun() {
        return mandelSchrodingerPanel.getRightGun();
    }

    private void synchronizeModel() {
        if( getWavefunctionDifference() < 10 ) {
            setSplitMode( false );
        }
        else {
            setSplitMode( true );
        }
    }

    private void setSplitMode( boolean splitMode ) {
        splitModel.setSplitMode( splitMode );
        mandelSchrodingerPanel.setSplitMode( splitMode );
    }

}
