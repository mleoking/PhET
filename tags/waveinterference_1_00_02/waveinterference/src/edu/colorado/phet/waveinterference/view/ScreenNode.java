/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.model.WaveModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 25, 2006
 * Time: 12:32:59 AM
 * Copyright (c) Mar 25, 2006 by Sam Reid
 */

public class ScreenNode extends PhetPNode {
    private boolean enabled = false;
    private boolean intensityMode = true;
    private CurveScreenGraphic curveScreenGraphic;
    private BrightnessScreenGraphic brightnessScreenGraphic;
    private WaveModel waveModel;
    private IntensityColorMap intensityColorMap;
    private ArrayList listeners = new ArrayList();

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
        notifyEnabledStateChanged();
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
        intensityColorMap.update();
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

    public BrightnessScreenGraphic getBrightnessScreenGraphic() {
        return brightnessScreenGraphic;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void reset() {
        setScreenEnabled( false );
    }

    public static interface Listener {
        void enabledStateChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyEnabledStateChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.enabledStateChanged();
        }
    }
}
