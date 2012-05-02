// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.awt.Canvas;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

public class LWJGLCanvas extends Canvas {

    private Thread renderThread;
    private boolean running;

    private boolean tabDirty = true;
    private LWJGLTab activeTab = null;

    private static LWJGLCanvas instance = null;
    private static ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();

    public static synchronized LWJGLCanvas getCanvasInstance() {
        if ( instance == null ) {
            instance = new LWJGLCanvas();
        }
        return instance;
    }

    public static void addTask( Runnable runnable ) {
        taskQueue.add( runnable );
    }

    private LWJGLCanvas() {
        setFocusable( true );
        requestFocus();
        setIgnoreRepaint( true );
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
        catch ( InterruptedException e ) {
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
        renderThread = new Thread( "PhET LWJGL Main Loop Thread" ) {
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
                }
                catch ( LWJGLException e ) {
                    e.printStackTrace();
                }

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
        };
        renderThread.start();
    }
}
