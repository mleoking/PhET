// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.Piccolo3DCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Renders an arbitrary Piccolo node into a quadrilateral
 */
public class PlanarPiccoloNode extends PlanarComponentNode {
    private final PNode node;

    public PlanarPiccoloNode( final PNode node ) {
        // use a wrapper panel that takes up no extra room
        super( new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ) {{
            add( new Piccolo3DCanvas( node ) );
        }} );
        this.node = node;
    }

    public Piccolo3DCanvas getCanvas() {
        return (Piccolo3DCanvas) ( getComponent().getComponent( 0 ) );
    }

    public PNode getNode() {
        return node;
    }
}
