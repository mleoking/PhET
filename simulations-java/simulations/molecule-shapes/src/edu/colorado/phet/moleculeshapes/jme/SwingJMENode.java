// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import edu.colorado.phet.moleculeshapes.util.VoidNotifier;

import com.jme3.app.Application;
import com.jme3.scene.Node;

/**
 * Creates a JME node that displays a Swing component
 */
public class SwingJMENode extends Node {

    private Dimension size = new Dimension();
    private HUDNode hudNode;
    private final JComponent component;
    private final Application app;

    public final VoidNotifier onResize = new VoidNotifier();

    public SwingJMENode( final JComponent component, final Application app ) {
        this.component = component;
        this.app = app;
        component.setSize( component.getPreferredSize() );
        component.setDoubleBuffered( false ); // avoids having the RepaintManager attempt to get the containing window (and throw a NPE)
        component.setOpaque( false );

        component.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                synchronized ( app ) {
                    onResize();
                }
            }
        } );
        onResize();
    }

    public void refresh() {
        if ( hudNode != null ) {
            hudNode.refresh();
        }
    }

    public void onResize() {
        if ( !component.getPreferredSize().equals( size ) ) {
            if ( hudNode != null ) {
                detachChild( hudNode );
                hudNode.dispose();
            }
            size = component.getPreferredSize();
            hudNode = new HUDNode( component, size.width, size.height, app );
            attachChild( hudNode );

            // notify that we resized
            onResize.fire();
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
