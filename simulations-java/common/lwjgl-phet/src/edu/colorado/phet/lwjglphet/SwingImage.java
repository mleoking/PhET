// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.lwjglphet.contrib.LWJGLSwingEventHandler;

/**
 * An image backed by a Swing component that can be rendered as a texture in OpenGL
 * <p/>
 * NOTE: Much of the ugly ugly code is copied and slightly tweaked with necessary licensing. Not my own.
 */
public class SwingImage extends TextureImage {
    private final JComponent component;

    // allows us to forward mouse events to the Swing component somewhat correctly
    private final LWJGLSwingEventHandler swingEventHandler;

    // owned by the EDT
    private volatile boolean dirty = true;

    public static final String ON_REPAINT_CALLBACK = "!@#%^&*"; // tag used in the repaint manager to notify this instance for repainting

    public SwingImage( int width, int height, boolean hasAlpha, int magFilter, int minFilter, JComponent component ) {
        super( width, height, hasAlpha, magFilter, minFilter );
        this.component = component;
        swingEventHandler = new LWJGLSwingEventHandler( component );
        init();
    }

    public SwingImage( int width, int height, boolean hasAlpha, int magFilter, int minFilter, AffineTransform imageTransform, JComponent component ) {
        super( width, height, hasAlpha, magFilter, minFilter, imageTransform );
        this.component = component;
        swingEventHandler = new LWJGLSwingEventHandler( component );
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

    public void handleMouseEvent( int rawX, int rawY ) {
        Vector2F swingPoint = localToComponentCoordinates( new Vector2F( rawX, rawY ) );
        final double x = swingPoint.x;
        final double y = swingPoint.y;

        final int eventButton = Mouse.getEventButton();
        final boolean eventButtonState = Mouse.getEventButtonState();

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                if ( eventButton == -1 ) {
                    swingEventHandler.sendAWTMouseEvent( (int) x, (int) y, false, MouseEvent.NOBUTTON );
                }
                else {
                    swingEventHandler.sendAWTMouseEvent( (int) x, (int) y, eventButtonState, swingEventHandler.getSwingButtonIndex( eventButton ) );
                }
            }
        } );
    }

    public void update() {
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

    @Override public void repaint() {
        dirty = true;
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

        /*---------------------------------------------------------------------------*
        * debugging visuals
        *----------------------------------------------------------------------------*/
//        g.setStroke( new BasicStroke( 0 ) );
//        g.setPaint( new Color( 0f, 0f, 1f, 0.25f ) );
//        debuggingRectangle( g, component.getPreferredSize().width, component.getPreferredSize().height );
//
//        g.setTransform( new AffineTransform() );
//        g.setPaint( new Color( 1f, 0f, 0f, 0.5f ) );
//        debuggingRectangle( g, getWidth(), getHeight() );
    }

    private void debuggingRectangle( Graphics2D g, int w, int h ) {
        g.drawRect( 0, 0, w, 1 );
        g.drawRect( 0, 0, 1, h );
        g.drawRect( w - 1, 0, 1, h );
        g.drawRect( 0, h - 1, w, 1 );
    }

    private static void layoutComponent( Component component ) {
        synchronized( component.getTreeLock() ) {
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

    public Component componentAt( final int x, final int y ) {
        return swingEventHandler.componentAt( x, y );
    }
}
