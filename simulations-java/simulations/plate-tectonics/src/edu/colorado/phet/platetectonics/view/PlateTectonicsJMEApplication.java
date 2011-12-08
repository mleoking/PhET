// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.jmephet.PhetJMEApplication;

import com.jme3.math.ColorRGBA;

public class PlateTectonicsJMEApplication extends PhetJMEApplication {

    public PlateTectonicsJMEApplication( Frame parentFrame ) {
        super( parentFrame );
        backgroundColor.set( new ColorRGBA( 0.85f, 0.95f, 1f, 1.0f ) );
    }

    private volatile boolean resizeDirty = false;


    /**
     * Pseudo-constructor (JME-based)
     */
    @Override public void initialize() {
        super.initialize();
    }


    @Override public void updateState( final float tpf ) {
        super.updateState( tpf );
        if ( resizeDirty ) {

        }
    }

    @Override public void onResize( Dimension canvasSize ) {
        super.onResize( canvasSize );
        resizeDirty = true;
    }
}