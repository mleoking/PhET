// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.*;

import edu.colorado.phet.jmephet.PhetJMEApplication;

import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

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

    public static void addLighting( Node node ) {
        final DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, -0.5f, -2 ).normalizeLocal() );
        sun.setColor( new ColorRGBA( 1, 1, 1, 1.3f ) );
        node.addLight( sun );

        final DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( new ColorRGBA( 1, 1, 1, 0.5f ) );
        node.addLight( moon );
    }
}