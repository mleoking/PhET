/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.RotationGlyph;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.colorado.phet.waveinterference.view.WaveSideView;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:20:39 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class RotationWaveGraphic extends PNode {
    private double rotation;
    private RotationGlyph rotationGlyph;
    private WaveModelGraphic waveModelGraphic;
    private WaveSideView waveSideView;

    public RotationWaveGraphic( WaveModelGraphic waveModelGraphic, WaveSideView waveSideView, RotationGlyph rotationGlyph ) {
        this( waveModelGraphic, waveSideView, rotationGlyph, 0.0 );
    }

    public RotationWaveGraphic( WaveModelGraphic waveModelGraphic, WaveSideView waveSideView, RotationGlyph rotationGlyph, double rotation ) {
        this.waveModelGraphic = waveModelGraphic;
        this.waveSideView = waveSideView;
        this.rotationGlyph = rotationGlyph;
        this.rotation = rotation;
        addChild( rotationGlyph );
        addChild( waveModelGraphic );
        addChild( waveSideView );
        setRotation( 0.0 );
        this.waveSideView.setSpaceBetweenCells( this.waveModelGraphic.getCellDimensions().width );
        updateLocations();
    }

    public double getRotation() {
        return rotation;
    }

    private void updateRotationGlyph( double value ) {
        rotationGlyph.setPrimaryHeight( waveModelGraphic.getFullBounds().getHeight() );
        rotationGlyph.setPrimaryWidth( waveModelGraphic.getFullBounds().getWidth() );
        rotationGlyph.setAngle( value );
        rotationGlyph.setOffset( 0, waveModelGraphic.getFullBounds().getCenterY() - rotationGlyph.getSurfaceHeight() );
    }

    public void updateLocations() {
        waveSideView.setOffset( waveModelGraphic.getFullBounds().getX(), waveModelGraphic.getFullBounds().getCenterY() );
    }

    public void setRotation( double value ) {
        this.rotation = value;
        updateRotationGlyph( value );
        if( value == 0 ) {
            rotationGlyph.setVisible( false );
            waveSideView.setVisible( false );
            waveModelGraphic.setVisible( true );
        }
        else if( value >= Math.PI / 2 - 0.02 ) {
            rotationGlyph.setVisible( false );
            waveSideView.setVisible( true );
            waveModelGraphic.setVisible( false );
        }
        else {
            rotationGlyph.setVisible( true );
            waveSideView.setVisible( false );
            waveModelGraphic.setVisible( false );
        }
    }

    public void update() {
        waveSideView.update();
        waveModelGraphic.update();
        updateLocations();
    }
}
