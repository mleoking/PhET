// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.jmephet.CanvasTransform.CenteredStageCanvasTransform;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.jmephet.PiccoloJMENode;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.math.ColorRGBA;

public class PlateTectonicsJMEApplication extends PhetJMEApplication {

    private final Frame parentFrame;
    private CenteredStageCanvasTransform canvasTransform;

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

        JMEUtils.swingLock( new Runnable() {
            public void run() {
                canvasTransform = new CenteredStageCanvasTransform( PlateTectonicsJMEApplication.this );

                Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D() );
                getBackgroundGui().getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new PText( "Toolbox" ) {{
                    setFont( new PhetFont( 16, true ) );
                }} ), PlateTectonicsJMEApplication.this, canvasTransform, position ) );
            }
        } );
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
        // TODO: move this into jme-phet
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