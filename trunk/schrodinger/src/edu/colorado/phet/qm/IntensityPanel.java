/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;
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

    public IntensityPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        setGunGraphic( new HighIntensityGun( this ) );
        getIntensityDisplay().setMultiplier( 100 );
        getIntensityDisplay().setProbabilityScaleFudgeFactor( 10 );
        getIntensityDisplay().setOpacity( 6 );
        getIntensityDisplay().setNormDecrement( 0.0 );

        setSplitGraphics();
    }

    public void setSplitMode( boolean splitMode ) {
        if( splitMode ) {
            setSplitGraphics();
        }
        else {
            setNormalGraphics();
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


}
