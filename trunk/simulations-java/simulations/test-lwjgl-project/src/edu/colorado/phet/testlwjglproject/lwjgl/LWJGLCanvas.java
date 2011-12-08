// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import java.awt.Canvas;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

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
        taskQueue.add( new Runnable() {
            public void run() {
                activeTab = tab;
            }
        } );
    }

    public void initialize() {
        System.out.println( "LWJGLCanvas initialize" );
        renderThread = new Thread() {
            public void run() {
                running = true;
                try {
                    Display.setParent( LWJGLCanvas.this );
                    Display.create();
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
                            activeTab.initialize();
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
