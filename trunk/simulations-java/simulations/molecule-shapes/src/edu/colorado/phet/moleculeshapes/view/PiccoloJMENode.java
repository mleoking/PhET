// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;

public class PiccoloJMENode extends SwingJMENode {

    public PiccoloJMENode( final PNode node, AssetManager assetManager, InputManager inputManager ) {
        super( new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ) {{
                   add( new PSwingCanvas() {
                       {
                           setOpaque( false );
                           removeInputEventListener( getZoomEventHandler() );
                           removeInputEventListener( getPanEventHandler() );

                           getLayer().addChild( node );

                           node.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                               public void propertyChange( PropertyChangeEvent evt ) {
                                   updateSize();
                               }
                           } );

                           updateSize();
                       }

                       public void updateSize() {
                           PBounds bounds = getRoot().getFullBounds();

                           setPreferredSize( new Dimension( (int) bounds.width, (int) bounds.height ) );
                           setSize( getPreferredSize() );
                       }
                   } );
               }}, assetManager, inputManager );
    }

    public PCanvas getCanvas() {
        return (PCanvas) getComponent();
    }
}
