// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.jmephet.PhetJMEApplication;

import com.jme3.math.ColorRGBA;

public class PlateTectonicsJMEApplication extends PhetJMEApplication {

    private final Frame parentFrame;

    public PlateTectonicsJMEApplication( Frame parentFrame ) {
        this.parentFrame = parentFrame;
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
        if ( resizeDirty ) {

        }
    }

    @Override public void onResize( Dimension canvasSize ) {
        super.onResize( canvasSize );
        resizeDirty = true;
    }

    @Override public void handleError( String errMsg, final Throwable t ) {
        super.handleError( errMsg, t );
        if ( errMsg.equals( "Failed to initialize OpenGL context" ) ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    PhetOptionPane.showMessageDialog( parentFrame, "The simulation was unable to start.\nUpgrading your video card's drivers may fix the problem.\nError information:\n" + t.getMessage() );
                }
            } );
        }
    }
}