// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculeshapes.util.VoidNotifier;

import com.jme3.app.Application;
import com.jme3.scene.Node;

/**
 * Creates a JME node that displays a Swing component, keeps it up-to-date, and
 * can resize its underlying 3D representation when the component resizes
 */
public class SwingJMENode extends Node {
    private final JComponent component;
    private final Application app;

    private volatile Dimension size = new Dimension(); // our current size
    private volatile HUDNode hudNode; // the current node displaying our component

    public final VoidNotifier onResize = new VoidNotifier(); // notifier that fires when this node is resized

    private boolean ignoreInput = false;

    /**
     * Basically whether this node should be antialiased. If it is set up in a position where the texture (image)
     * pixels are not 1-to-1 with the screen pixels (say, translated by fractions of a pixel, or any rotation),
     * this should be set to true.
     */
    public final Property<Boolean> antialiased = new Property<Boolean>( false );

    public SwingJMENode( final JComponent component, final Application app ) {
        this.component = component;
        this.app = app;

        // ensure that we have it at its preferred size before sizing and painting
        component.setSize( component.getPreferredSize() );

        component.setDoubleBuffered( false ); // avoids having the RepaintManager attempt to get the containing window (and throw a NPE)

        // by default, the components should usually be transparent
        component.setOpaque( false );

        // when our component resizes, we need to handle it!
        component.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                onResize();
            }
        } );
        onResize();
    }

    // flags the HUD node as needing a full repaint
    public void repaint() {
        if ( hudNode != null ) {
            hudNode.repaint();
        }
    }

    // if necessary, creates a new HUD node of a different size to display our component
    public synchronized void onResize() {
        final Dimension preferredSize = component.getPreferredSize();

        // verify that it is actually a size change. don't do the extra work!
        if ( !preferredSize.equals( size ) ) {
            size = preferredSize;

            // create the new HUD node within the EDT
            // TODO: move the graphics scale to higher up
            final HUDNode newHudNode = new HUDNode( component, size.width, size.height, 1.0, app, antialiased );

            // do the rest of the work in the JME thread
            JMEUtils.invoke( new Runnable() {
                public void run() {
                    // ditch the old HUD node
                    if ( hudNode != null ) {
                        detachChild( hudNode );
                        hudNode.dispose();
                    }

                    // hook up new HUD node.
                    hudNode = newHudNode;
                    attachChild( newHudNode );

                    if ( ignoreInput ) {
                        hudNode.ignoreInput();
                    }

                    // notify that we resized
                    onResize.fire();
                }
            } );
        }
    }

    public void ignoreInput() {
        this.ignoreInput = true;
        if ( hudNode != null ) {
            hudNode.ignoreInput();
        }
    }

    public int getWidth() {
        return hudNode.getWidth();
    }

    public int getHeight() {
        return hudNode.getHeight();
    }

    public JComponent getComponent() {
        return component;
    }
}
