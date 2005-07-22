/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.SlitControlPanel;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.SmoothIntensityDisplay;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.colormaps.SplitColorMap;
import edu.colorado.phet.qm.view.gun.HighIntensityGun;
import edu.colorado.phet.qm.view.gun.Photon;

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
    private PhetGraphic slitControlGraphic;
    private SlitControlPanel slitControlPanel;
    private SplitColorMap splitColorMap;

    public IntensityPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        HighIntensityGun gun = createGun();
        setGunGraphic( gun );
        getIntensityDisplay().setHighIntensityMode();

//        setSplitGraphics();
        setNormalGraphics();
        smoothIntensityDisplay = new SmoothIntensityDisplay( getIntensityDisplay() );
        setSmoothScreen( false );

        PhetGraphic ds = getDoubleSlitCheckBoxGraphic();
        slitControlPanel = new SlitControlPanel( intensityModule );
        slitControlGraphic = PhetJComponent.newInstance( this, slitControlPanel );
        addGraphic( slitControlGraphic );
        slitControlGraphic.setLocation( ds.getX(), ds.getY() + ds.getHeight() + 5 );

        getIntensityDisplay().getDetectorSheet().setFadeEnabled( true );
        getIntensityDisplay().getDetectorSheet().addFadeCheckBox();

        splitColorMap = new SplitColorMap( this.intensityModule.getSplitModel() );
    }

    protected HighIntensityGun createGun() {
        HighIntensityGun gun = new HighIntensityGun( this );
        return gun;
    }

    public SlitControlPanel getSlitControlPanel() {
        return slitControlPanel;
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
        setColorMap( getWavefunctionGraphic().getMagnitudeMap() );
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
    }
}
