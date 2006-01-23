/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.colormaps.SplitColorMap;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.SmoothIntensityDisplay;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:11 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class HighIntensitySchrodingerPanel extends SchrodingerPanel {
    private IntensityModule intensityModule;
    private SmoothIntensityDisplay smoothIntensityDisplay;
    private boolean smoothScreen = false;
    private SplitColorMap splitColorMap;
    private HighIntensityGunGraphic highIntensityGun;
    public static final boolean SMOOTH_SCREEN_DEFAULT = true;

    public HighIntensitySchrodingerPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        highIntensityGun = createGun();
        setGunGraphic( highIntensityGun );

        addGunChooserGraphic();

        getIntensityDisplay().setHighIntensityMode();

        setNormalGraphics();
        smoothIntensityDisplay = new SmoothIntensityDisplay( this, getIntensityDisplay() );
        setSmoothScreen( SMOOTH_SCREEN_DEFAULT );
        splitColorMap = new SplitColorMap( intensityModule.getSplitModel(), this );
        setPhoton( super.getDisplayPhotonColor() );
        getDetectorSheetPNode().getDetectorSheetControlPanel().setBrightness();
    }

    protected void addGunChooserGraphic() {
        if( useGunChooserGraphic() ) {
            PSwing pSwing = new PSwing( this, highIntensityGun.getComboBox() );
            highIntensityGun.getComboBox().setEnvironment( pSwing, this );
            getSchrodingerScreenNode().setGunTypeChooserGraphic( pSwing );
        }
    }

    protected boolean useGunChooserGraphic() {
        return true;
    }

    protected HighIntensityGunGraphic createGun() {
        return new HighIntensityGunGraphic( this );
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
            getDetectorSheetPNode().clearScreen();
            if( smoothScreen ) {
                getDetectorSheetPNode().setSaveButtonVisible( true );
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
        getWavefunctionGraphic().setColorMap( splitColorMap );
    }

    public void setNormalGraphics() {
        setVisualizationStyle( getComplexColorMap(), getWaveValueAccessor() );
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

    public void setPhoton( Photon photon ) {
        super.setPhoton( photon );
        if( splitColorMap != null ) {
            splitColorMap.setRootColor( photon == null ? null : new ColorData( photon.getWavelengthNM() ) );
        }
        if( smoothIntensityDisplay != null ) {
            smoothIntensityDisplay.setPhotonColor( photon );
        }
    }

    public ColorData getRootColor() {
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
