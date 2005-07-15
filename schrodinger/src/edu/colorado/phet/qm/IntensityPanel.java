/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.SmoothIntensityDisplay;
import edu.colorado.phet.qm.view.colormaps.MagnitudeInGrayscale;
import edu.colorado.phet.qm.view.gun.HighIntensityGun;

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

    public IntensityPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        setGunGraphic( new HighIntensityGun( this ) );
        getIntensityDisplay().setMultiplier( 100 );
        getIntensityDisplay().setProbabilityScaleFudgeFactor( 10 );
        getIntensityDisplay().setOpacity( 6 );
        getIntensityDisplay().setNormDecrement( 0.0 );

        setSplitGraphics();
        smoothIntensityDisplay = new SmoothIntensityDisplay( getIntensityDisplay() );
        setSmoothScreen( true );

        PhetGraphic ds = getDoubleSlitCheckBoxGraphic();
        slitControlPanel = new SlitControlPanel( intensityModule );
        slitControlGraphic = PhetJComponent.newInstance( this, slitControlPanel );
        addGraphic( slitControlGraphic );
        slitControlGraphic.setLocation( ds.getX(), ds.getY() + ds.getHeight() + 5 );

        getIntensityDisplay().getDetectorSheet().setFadeEnabled( true );
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
        setColorMap( new SplitColorMap( this.intensityModule.getSplitModel() ) );
    }

    private void setColorMap( ColorMap colorMap ) {
        getWavefunctionGraphic().setWavefunctionColorMap( colorMap );
    }

    public void setNormalGraphics() {
        setColorMap( new MagnitudeInGrayscale( this ) );
    }

    public boolean isSmoothScreen() {
        return smoothScreen;
    }

    public SmoothIntensityDisplay getSmoothIntensityDisplay() {
        return smoothIntensityDisplay;
    }
}
