/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.qm.controls.SlitDetectorPanel;
import edu.colorado.phet.qm.view.colormaps.ColorMap;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.colormaps.SplitColorMap;
import edu.colorado.phet.qm.view.gun.HighIntensityGun;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.SmoothIntensityDisplay;
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
    private HighIntensityGun highIntensityGun;
    public static final boolean SMOOTH_SCREEN_DEFAULT = true;

    public IntensityPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        highIntensityGun = createGun();
        setGunGraphic( highIntensityGun );
        getIntensityDisplay().setHighIntensityMode();

        setNormalGraphics();
        smoothIntensityDisplay = new SmoothIntensityDisplay( getIntensityDisplay() );
        setSmoothScreen( SMOOTH_SCREEN_DEFAULT );

//        getDoubleSlitPanel().addDoubleSlitCheckBoxListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                setControlsEnabled( getDoubleSlitPanel().isDoubleSlitEnabled() );
//            }
//        } );


        splitColorMap = new SplitColorMap( intensityModule.getSplitModel(), this );//this.intensityModule.getSplitModel() );
//        setControlsEnabled( getDoubleSlitPanel().isDoubleSlitEnabled() );
        setDisplayPhotonColor( super.getDisplayPhotonColor() );
        getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setBrightness();

    }

//    public IntensityScreenNode getIntensityScreenNode() {
//        return (IntensityScreenNode)super.getScreenNode();
//    }

//    protected SchrodingerScreenNode createScreenNode( SchrodingerModule module ) {
//        return new IntensityScreenNode( module, this );
//    }

//    public PSwing getSlitControlGraphic() {
//        return getIntensityScreenNode().getSlitControlGraphic();
//    }

    private void setControlsEnabled( boolean doubleSlitEnabled ) {
        getSlitDetectorPanel().setEnabled( doubleSlitEnabled );
    }

    protected HighIntensityGun createGun() {
        return new HighIntensityGun( this );
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
        getWavefunctionGraphic().setWavefunctionColorMap( colorMap );
    }

    public void setNormalGraphics() {
        setColorMap( getWavefunctionGraphic().getMagnitudeColorMap() );
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

    public void setFadeEnabled( boolean selected ) {
        super.setFadeEnabled( selected );
        smoothIntensityDisplay.setFadeEnabled( selected );
    }

    public void setWaveSize( int width, int height ) {
        super.setWaveSize( width, height );
        highIntensityGun.setOn( highIntensityGun.isOn() );
    }

    public IntensityModule getIntensityModule() {
        return intensityModule;
    }
}
