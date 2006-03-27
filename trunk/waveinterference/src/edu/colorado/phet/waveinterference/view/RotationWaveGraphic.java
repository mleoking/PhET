/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

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
    private ArrayList listeners = new ArrayList();

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
        setViewAngle( 0.0 );
        this.waveSideView.setSpaceBetweenCells( this.waveModelGraphic.getCellDimensions().width );
        updateLocations();
        updateGraphics();
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

    public void setViewAngle( double value ) {
        if( this.rotation != value ) {
            this.rotation = value;
            updateGraphics();
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.rotationChanged();
            }
        }
    }

    public boolean isTopView() {
        return rotation == 0;
    }

    public boolean isSideView() {
        return rotation >= Math.PI / 2 - 0.02;
    }

    private void updateGraphics() {
        updateRotationGlyph( rotation );
        if( isTopView() ) {
            rotationGlyph.setVisible( false );
            waveSideView.setVisible( false );
            waveModelGraphic.setVisible( true );
        }
        else if( isSideView() ) {
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

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return waveModelGraphic.getLatticeScreenCoordinates();
    }

    public static interface Listener {
        void rotationChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

}
