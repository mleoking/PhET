// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;

/**
 * Main rendering area for an LWJGL-based simulation. Native code will take over painting of this
 * canvas with the OpenGL accelerated view.
 * <p/>
 * This class is a singleton because to use all of LWJGL's features, we can only effectively have
 * one LWJGL display, and thus one LWJGL canvas. Exceptions would be thrown on creation of a
 * second one, and LWJGL will only render to one canvas.
 * <p/>
 * For LWJGL simulations, LWJGLTab instances should be used for each tab instead of modules. This
 * canvas only renders (and forwards events to) the active tab. This is easy to do by setting
 * LWJGLTabs up in a TabbedModule.
 * <p/>
 * Every frame, the active tab's loop() method is called, and this method takes care of all of the
 * rendering to the canvas, in addition to state changes and event handling if necessary.
 */
public class LWJGLCanvas extends Canvas {

    public static final String LWJGL_THREAD_NAME = "PhET LWJGL Main Loop Thread";

    private Thread renderThread;
    private boolean running;

    private boolean tabDirty = true;
    private LWJGLTab activeTab = null;

    private static LWJGLCanvas instance = null;
    private static ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
    private final Frame parentFrame;

    public static synchronized LWJGLCanvas getCanvasInstance( Frame parentFrame ) {
        if ( instance == null ) {
            instance = new LWJGLCanvas( parentFrame );
        }
        return instance;
    }

    public static void addTask( Runnable runnable ) {
        taskQueue.add( runnable );
    }

    private LWJGLCanvas( Frame parentFrame ) {
        this.parentFrame = parentFrame;
        setFocusable( true );
        requestFocus();
        setIgnoreRepaint( true );

        System.setProperty( "org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true" );
//        System.setProperty( "org.lwjgl.util.Debug", "true" );
    }

    @Override public void addNotify() {
        super.addNotify();
        initialize();
    }

    @Override public final void removeNotify() {
        running = false;
        try {
            renderThread.join();
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        super.removeNotify();
    }

    public void switchToTab( final LWJGLTab tab ) {
        addTask( new Runnable() {
            public void run() {
                if ( activeTab != null ) {
                    activeTab.stop();
                }
                activeTab = tab;
                tabDirty = true;
            }
        } );
    }

    public void initialize() {
        renderThread = new Thread( LWJGLCanvas.LWJGL_THREAD_NAME ) {
            public void run() {
                running = true;
                try {
                    int maxAntiAliasingSamples = StartupUtils.getMaximumAntialiasingSamples();
                    Display.setParent( LWJGLCanvas.this );
                    Display.create( new PixelFormat( 24, // bpp, excluding alpha
                                                     8, // alpha
                                                     24, // depth
                                                     0, // stencil
                                                     Math.min( 4, maxAntiAliasingSamples ) ) // antialiasing samples
                    );

                    // main loop
                    while ( running ) {
                        while ( !taskQueue.isEmpty() ) {
                            taskQueue.poll().run();
                        }
                        if ( activeTab != null ) {
                            if ( tabDirty ) {
                                activeTab.start();
                                tabDirty = false;
                            }
                            activeTab.loop();
                        }
                    }

                    Display.destroy();

                }
                catch( LWJGLException e ) {
                    LWJGLUtils.showErrorDialog( parentFrame, e );
                    e.printStackTrace();
                }

            }
        };
        renderThread.start();
    }
}
