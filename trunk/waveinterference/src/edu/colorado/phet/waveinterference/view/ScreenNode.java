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
    private WaveModel waveModel;
    private IntensityColorMap intensityColorMap;

    public ScreenNode( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, final WaveModelGraphic waveModelGraphic ) {
        this.waveModel = waveModel;
        curveScreenGraphic = new CurveScreenGraphic( waveModel, latticeScreenCoordinates );
        intensityColorMap = new IntensityColorMap( waveModel, waveModelGraphic.getColorMap() );
        brightnessScreenGraphic = new BrightnessScreenGraphic( waveModel, latticeScreenCoordinates, intensityColorMap );
        addChild( curveScreenGraphic );
        addChild( brightnessScreenGraphic );
        waveModelGraphic.addListener( new WaveModelGraphic.Listener() {
            public void colorMapChanged() {
                setColorMap( waveModelGraphic.getColorMap() );
            }
        } );

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
        updateScreen();
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
        intensityColorMap.setColorMap( new IndexColorMap( waveModel.getLattice(), colorMap.getRootColor(), new WaveValueReader.AverageAbs( 2 ) ) );
//        intensityColorMap.setColorMap( new IndexColorMap( waveModel.getLattice(), colorMap.getRootColor(), new WaveValueReader.AverageAbs( 1 ) ) );
    }

    public void updateScreen() {
        if( curveScreenGraphic.getVisible() ) {
            curveScreenGraphic.update();
        }
        else if( brightnessScreenGraphic.getVisible() ) {
            brightnessScreenGraphic.update();
        }
    }

    public void setIntensityScale( double intensityScale ) {
        intensityColorMap.setIntensityScale( intensityScale );
    }
}
