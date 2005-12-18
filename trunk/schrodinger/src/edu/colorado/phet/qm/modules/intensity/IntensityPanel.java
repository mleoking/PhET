/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.qm.controls.SlitDetectorPanel;
import edu.colorado.phet.qm.view.colormaps.ColorMap;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.colormaps.SplitColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeInGrayscale3;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.SmoothIntensityDisplay;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:11 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityPanel extends SchrodingerPanel {
    private IntensityModule intensityModule;
    private SmoothIntensityDisplay smoothIntensityDisplay;
    private boolean smoothScreen = false;
    private SplitColorMap splitColorMap;
    private HighIntensityGunGraphic highIntensityGun;
    public static final boolean SMOOTH_SCREEN_DEFAULT = true;

    public IntensityPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        highIntensityGun = createGun();
        setGunGraphic( highIntensityGun );
        getIntensityDisplay().setHighIntensityMode();

        setNormalGraphics();
        smoothIntensityDisplay = new SmoothIntensityDisplay( this, getIntensityDisplay() );
        setSmoothScreen( SMOOTH_SCREEN_DEFAULT );
        splitColorMap = new SplitColorMap( intensityModule.getSplitModel(), this );//this.intensityModule.getSplitModel() );
        setDisplayPhotonColor( super.getDisplayPhotonColor() );
        getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setBrightness();
    }

    protected HighIntensityGunGraphic createGun() {
        return new HighIntensityGunGraphic( this );
    }

    public SlitDetectorPanel getSlitDetectorPanel() {
        return getDoubleSlitPanel().getSlitDetectorPanel();
    }

    public void setSplitMode( boolean splitMode ) {
        if( splitMode ) {
            setSplitGraphics();
        }
        else {
            setNormalGraphics();
        }
    }

    public void setSmoothScreen( boolean smoothScreen ) {
        if( smoothScreen != this.smoothScreen ) {
            this.smoothScreen = smoothScreen;
            getIntensityDisplay().clearScreen();
            if( smoothScreen ) {
                getIntensityDisplay().getDetectorSheet().setSaveButtonVisible( true );
            }
        }
    }

    protected void updateScreen() {
        if( smoothScreen ) {
            smoothIntensityDisplay.updateValues();
        }
        else {
            getIntensityDisplay().tryDetecting();
        }
    }

    public void setSplitGraphics() {
        setColorMap( splitColorMap );
    }

    private void setColorMap( ColorMap colorMap ) {
        getWavefunctionGraphic().setColorMap( colorMap );
    }

    public void setNormalGraphics() {
        getWavefunctionGraphic().setComplexColorMap( new MagnitudeInGrayscale3() );
    }

    public boolean isSmoothScreen() {
        return smoothScreen;
    }

    public SmoothIntensityDisplay getSmoothIntensityDisplay() {
        return smoothIntensityDisplay;
    }

    public SplitColorMap getSplitColorMap() {
        return splitColorMap;
    }

    public void setDisplayPhotonColor( Photon photon ) {
        super.setDisplayPhotonColor( photon );
        if( splitColorMap != null ) {
            splitColorMap.setRootColor( photon == null ? null : new PhotonColorMap.ColorData( photon.getWavelengthNM() ) );
        }
        if( smoothIntensityDisplay != null ) {
            smoothIntensityDisplay.setPhotonColor( photon );
        }
    }

    public PhotonColorMap.ColorData getRootColor() {
        return highIntensityGun.getRootColor();
    }

    public void setWaveSize( int width, int height ) {
        super.setWaveSize( width, height );
        highIntensityGun.setOn( highIntensityGun.isOn() );
    }

    public IntensityModule getIntensityModule() {
        return intensityModule;
    }
}
