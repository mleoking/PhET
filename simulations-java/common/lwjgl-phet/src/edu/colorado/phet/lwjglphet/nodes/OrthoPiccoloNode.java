// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.Piccolo3DCanvas;
import edu.colorado.phet.lwjglphet.CanvasTransform;
import edu.colorado.phet.lwjglphet.LWJGLTab;
import edu.umd.cs.piccolo.PNode;

/**
 * Allows overlaying a Piccolo-based GUI onto LWJGL. This should only be rendered in an orthographic mode.
 */
public class OrthoPiccoloNode extends OrthoComponentNode {
    private final PNode node;

    public OrthoPiccoloNode( final PNode node, final LWJGLTab tab, CanvasTransform canvasTransform, Property<Vector2D> position, final VoidNotifier mouseEventNotifier ) {
        // use a wrapper panel that takes up no extra room
        super( new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ) {{
            add( new Piccolo3DCanvas( node ) );
        }}, tab, canvasTransform, position, mouseEventNotifier );
        this.node = node;
    }

    public Piccolo3DCanvas getCanvas() {
        return (Piccolo3DCanvas) ( getComponent().getComponent( 0 ) );
    }

    public PNode getNode() {
        return node;
    }
}
