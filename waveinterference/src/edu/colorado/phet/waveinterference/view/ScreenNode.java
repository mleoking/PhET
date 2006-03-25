/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Mar 25, 2006
 * Time: 12:32:59 AM
 * Copyright (c) Mar 25, 2006 by Sam Reid
 */

public class ScreenNode extends PNode {
    private boolean enabled = false;
    private boolean intensityMode = true;
    private CurveScreenGraphic curveScreenGraphic;
    private BrightnessScreenGraphic brightnessScreenGraphic;

    public ScreenNode( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, ColorMap colorMap ) {
        curveScreenGraphic = new CurveScreenGraphic( waveModel, latticeScreenCoordinates );
        brightnessScreenGraphic = new BrightnessScreenGraphic( waveModel, latticeScreenCoordinates, colorMap );
        addChild( curveScreenGraphic );
        addChild( brightnessScreenGraphic );
        update();
    }

    public boolean isScreenEnabled() {
        return enabled;
    }

    public boolean isCurveMode() {
        return !isIntensityMode();
    }

    public boolean isIntensityMode() {
        return intensityMode;
    }

    public void setScreenEnabled( boolean selected ) {
        this.enabled = selected;
        update();
    }

    public void update() {
        curveScreenGraphic.setVisible( isCurveMode() && isScreenEnabled() );
        brightnessScreenGraphic.setVisible( isIntensityMode() && isScreenEnabled() );
    }

    public void setIntensityMode() {
        intensityMode = true;
        update();
    }

    public void setCurveMode() {
        intensityMode = false;
        update();
    }

    public void setColorMap( ColorMap colorMap ) {
        curveScreenGraphic.setColorMap( colorMap );
        brightnessScreenGraphic.setColorMap( colorMap );
    }

    public void updateScreen() {
        curveScreenGraphic.update();
        brightnessScreenGraphic.update();
    }
}
