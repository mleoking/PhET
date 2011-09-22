// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.jmephet.CanvasTransform.CenteredStageCanvasTransform;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.math.ColorRGBA;

public class PlateTectonicsJMEApplication extends PhetJMEApplication {

    private CenteredStageCanvasTransform canvasTransform;

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

        JMEUtils.swingLock( new Runnable() {
            public void run() {
                canvasTransform = new CenteredStageCanvasTransform( PlateTectonicsJMEApplication.this );

                Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D() );
                getBackgroundGui().getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new PText( "Toolbox" ) {{
                    setFont( new PhetFont( 16, true ) );
                }} ), getDirectInputHandler(), PlateTectonicsJMEApplication.this, canvasTransform, position ) ); // TODO: use module input handler
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
}