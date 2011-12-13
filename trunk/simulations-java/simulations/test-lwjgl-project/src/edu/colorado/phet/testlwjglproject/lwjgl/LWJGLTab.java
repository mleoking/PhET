// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule.Tab;

public abstract class LWJGLTab implements Tab {
    private final String title;
    public final Property<Boolean> active = new Property<Boolean>( false );

    // get/set from render thread
    public final Property<Dimension> canvasSize;

    public LWJGLTab( final LWJGLCanvas canvas, String title ) {
        this.title = title;

        // switch to this tab when active
        active.addObserver(
                new SimpleObserver() {
                    public void update() {
                        if ( active.get() ) {
                            canvas.switchToTab( LWJGLTab.this );
                        }
                    }
                }, false );

        canvasSize = new Property<Dimension>( canvas.getSize() );

        canvas.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                final Dimension newSize = canvas.getSize();
                LWJGLCanvas.addTask( new Runnable() {
                    public void run() {
                        canvasSize.set( newSize );
                    }
                } );
            }
        } );

        canvasSize.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println( "Canvas size: " + canvasSize.get() );
            }
        } );
    }

    /**
     * Called before looping, in the LWJGL thread, when this tab is activated. This can
     * happen multiple times, but it is guaranteed to happen before loop()
     */
    public abstract void start();

    /**
     * Called when the tab is switched away from, in the LWJGL thread. Should not render.
     */
    public abstract void stop();

    /**
     * A single iteration of the run-time loop responsible for rendering the scene and
     * handling various events. This will be called by the LWJGL thread.
     */
    public abstract void loop();

    public String getTitle() {
        return title;
    }

    public void setActive( boolean active ) {
        this.active.set( active );
    }

    public int getCanvasWidth() {
        return canvasSize.get().width;
    }

    public int getCanvasHeight() {
        return canvasSize.get().height;
    }
}
