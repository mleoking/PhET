// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule;

import com.jme3.asset.AssetManager;

/**
 * Support for creating a JME application, context and canvas
 */
public abstract class JMEModule extends TabbedModule {

    private static PhetJMEApplication app = null;
    private static Canvas canvas;

    public JMEModule( Frame parentFrame, Function1<Frame, PhetJMEApplication> applicationFactory ) {
        super( JMECanvasFactory.createCanvas( parentFrame, applicationFactory ) );

        // gets what we created in the super-call
        canvas = (Canvas) getContent();

        // stores the created application statically, so we need to retrieve this
        app = JMEUtils.getApplication();

        addListener( new Listener() {
            public void activated() {
                app.startCanvas();
            }

            public void deactivated() {
            }
        } );

        // listen to resize events on our canvas, so that we can update our layout
        canvas.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                app.onResize( canvas.getSize() );
            }
        } );
    }

    public Dimension getStageSize() {
        return app.getStageSize();
    }

    public Dimension getCanvasSize() {
        return app.canvasSize.get();
    }

    public AssetManager getAssetManager() {
        return app.getAssetManager();
    }
}
