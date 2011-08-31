// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;

import javax.swing.*;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;

public class PiccoloJMENode extends SwingJMENode {

    public PiccoloJMENode( final PNode node, AssetManager assetManager, InputManager inputManager ) {
        super( new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ) {{
                   add( new PiccoloJMECanvas( node ) );
               }}, assetManager, inputManager );
    }

    public PCanvas getCanvas() {
        return (PSwingCanvas) getComponent();
    }
}
