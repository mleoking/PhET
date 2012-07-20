// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.jmephet.hud;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.Piccolo3DCanvas;
import edu.colorado.phet.jmephet.CanvasTransform;
import edu.colorado.phet.jmephet.JMETab;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.umd.cs.piccolo.PNode;

/**
 * Embed a Piccolo node within the JME3 space as a Spatial. Handles proper resizing.
 */
public class PiccoloJMENode extends SwingJMENode {
    private final PNode node;

    public PiccoloJMENode( final PNode node, final JMEInputHandler inputHandler, final JMETab tab ) {
        this( node, inputHandler, tab, getDefaultTransform() );
    }

    public PiccoloJMENode( final PNode node, final JMEInputHandler inputHandler, final JMETab tab, CanvasTransform canvasTransform ) {
        this( node, inputHandler, tab, canvasTransform, getDefaultPosition() );
    }

    public PiccoloJMENode( final PNode node, final JMEInputHandler inputHandler, final JMETab tab, CanvasTransform canvasTransform, Property<Vector2D> position ) {
        // use a wrapper panel that takes up no extra room
        super( new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ) {{
            add( new Piccolo3DCanvas( node ) );
        }}, inputHandler, tab, canvasTransform, position );
        this.node = node;
    }

    public Piccolo3DCanvas getCanvas() {
        return (Piccolo3DCanvas) ( getComponent().getComponent( 0 ) );
    }

    public PNode getNode() {
        return node;
    }
}
