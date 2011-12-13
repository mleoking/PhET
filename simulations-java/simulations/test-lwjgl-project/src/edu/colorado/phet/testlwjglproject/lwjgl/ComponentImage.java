// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

// TODO: event handling (listen to events). use similar to HUDNode model
public class ComponentImage extends TextureImage {
    private final JComponent component;

    // owned by the EDT
    private boolean dirty = true;

    public static final String ON_REPAINT_CALLBACK = "!@#%^&*"; // tag used in the repaint manager to notify this instance for repainting

    public ComponentImage( int width, int height, boolean hasAlpha, JComponent component ) {
        super( width, height, hasAlpha );
        this.component = component;
        init();
    }

    public ComponentImage( int width, int height, boolean hasAlpha, AffineTransform imageTransform, JComponent component ) {
        super( width, height, hasAlpha, imageTransform );
        this.component = component;
        init();
    }

    private void init() {
        initRepaintManager();

        component.setDoubleBuffered( false ); // not necessary. we are already essentially double-buffering it
        refreshImage(); // update it on construction

        // attach the property that our JMERepaintManager will look for. when we receive a repaint request, the dirty flag will be set
        component.putClientProperty( ON_REPAINT_CALLBACK, new VoidFunction0() {
            public void apply() {
                // mark as dirty from the render thread
                dirty = true;
            }
        } );
    }

    // TODO: run this every frame
    public void checkRepaint() {
        // make sure we acquire the swing thread before doing the repainting that needs to be done
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                if ( dirty ) {
                    refreshImage();
                    dirty = false;
                }
            }
        } );
    }

    @Override public void paint( Graphics2D g ) {
        // background is transparent, to support transparent graphics
        g.setBackground( new Color( 0f, 0f, 0f, 0f ) );
        g.clearRect( 0, 0, getWidth(), getHeight() );

        // validating, so that Swing will accept non-frame-connected component trees
        component.validate();

        // set the specific size to the preferred size. otherwise size stays at 0,0
        component.setSize( component.getPreferredSize() );

        // run the full layout now that we have the size
        layoutComponent( component );

        //Fix for rendering problems on 1.5, see #3122
        component.printAll( g );
    }

    private static void layoutComponent( Component component ) {
        synchronized ( component.getTreeLock() ) {
            component.doLayout();

            if ( component instanceof Container ) {
                for ( Component child : ( (Container) component ).getComponents() ) {
                    layoutComponent( child );
                }
            }
        }
    }

    private void initRepaintManager() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                final RepaintManager repaintManager = RepaintManager.currentManager( component );
                if ( !( repaintManager instanceof LWJGLRepaintManager ) ) {
                    RepaintManager.setCurrentManager( new LWJGLRepaintManager() );
                }
            }
        } );
    }
}
