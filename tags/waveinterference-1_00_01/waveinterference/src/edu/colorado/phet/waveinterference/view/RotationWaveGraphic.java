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
    private PNode topView;
    private AbstractWaveSideView waveSideView;
    private ArrayList listeners = new ArrayList();

    public RotationWaveGraphic( WaveModelGraphic waveModelGraphic, AbstractWaveSideView waveSideView, RotationGlyph rotationGlyph ) {
        this( waveModelGraphic, waveModelGraphic, waveSideView, rotationGlyph );
    }

    public RotationWaveGraphic( WaveModelGraphic waveModelGraphic, PNode topView, AbstractWaveSideView waveSideView, RotationGlyph rotationGlyph ) {
        this( waveModelGraphic, topView, waveSideView, rotationGlyph, 0.0 );
    }

    public RotationWaveGraphic( WaveModelGraphic waveModelGraphic, PNode topView, AbstractWaveSideView waveSideView, RotationGlyph rotationGlyph, double rotation ) {
        this.waveModelGraphic = waveModelGraphic;
        this.topView = topView;
        this.waveSideView = waveSideView;
        this.rotationGlyph = rotationGlyph;
        this.rotation = rotation;
        addChild( rotationGlyph );
        addChild( this.topView );
        addChild( this.waveSideView );
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
                RotationWaveGraphic.Listener listener = (RotationWaveGraphic.Listener)listeners.get( i );
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

    protected void updateGraphics() {
        updateRotationGlyph( rotation );
        if( isTopView() ) {
            showTopView();
        }
        else if( isSideView() ) {
            showSideView();
        }
        else {
            showRotationGlyph();
        }
    }

    protected void showRotationGlyph() {
        setVisibility( true, false, false );
    }

    protected void showSideView() {
        setVisibility( false, true, false );
    }

    protected void showTopView() {
        setVisibility( false, false, true );
    }

    protected void setVisibility( boolean rotationGlyph, boolean sideView, boolean topView ) {
        this.rotationGlyph.setVisible( rotationGlyph );
        waveSideView.setVisible( sideView );
        this.topView.setVisible( topView );
    }

    public void update() {
        waveSideView.update();
        waveModelGraphic.update();
        updateLocations();
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return waveModelGraphic.getLatticeScreenCoordinates();
    }

    public void setCellSize( int pixelsPerCell ) {
        waveModelGraphic.setCellDimensions( pixelsPerCell, pixelsPerCell );
    }

    public void reset() {
        setViewAngle( 0.0 );
    }

    public static interface Listener {
        void rotationChanged();
    }

    public void addListener( RotationWaveGraphic.Listener listener ) {
        listeners.add( listener );
    }

    public RotationGlyph getRotationGlyph() {
        return rotationGlyph;
    }
}
