// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;

/**
 * Creates a JME node that displays a Swing component
 */
public class SwingJMENode extends Node {

    private Dimension size = new Dimension();
    private HUDNode hudNode;
    private final JComponent component;
    private final AssetManager assetManager;
    private final InputManager inputManager;

    public SwingJMENode( final JComponent component, AssetManager assetManager, InputManager inputManager ) {
        this.component = component;
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        component.setSize( component.getPreferredSize() );
        component.setDoubleBuffered( false ); // avoids having the RepaintManager attempt to get the containing window (and throw a NPE)
        component.setOpaque( false );

        component.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                onResize();
            }
        } );
        onResize();
    }

    public void onResize() {
        if ( !component.getPreferredSize().equals( size ) ) {
            if ( hudNode != null ) {
                detachChild( hudNode );
                hudNode.dispose();
            }
            size = component.getPreferredSize();
            hudNode = new HUDNode( component, size.width, size.height, assetManager, inputManager );
            attachChild( hudNode );
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
