// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.moleculeshapes.jme.CanvasTransform.IdentityCanvasTransform;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Embed a Piccolo node within the JME3 space as a Spatial. Handles proper resizing.
 */
public class PiccoloJMENode extends SwingJMENode {
    private final PNode node;

    public PiccoloJMENode( final PNode node, PhetJMEApplication app ) {
        this( node, app, new IdentityCanvasTransform() );
    }

    public PiccoloJMENode( final PNode node, PhetJMEApplication app, CanvasTransform canvasTransform ) {
        // use a wrapper panel that takes up no extra room
        super( new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ) {{
                   add( new PiccoloJMECanvas( node ) );
               }}, app, canvasTransform );
        this.node = node;
    }

    public PCanvas getCanvas() {
        return (PSwingCanvas) getComponent();
    }

    public PNode getNode() {
        return node;
    }
}
