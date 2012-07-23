// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Base class for all equation nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationNode extends PhetPNode {

    public EquationNode() {
        setPickable( false );
    }

    // Changes the color of the equation by doing a deep traversal of this node's descendants.
    public void setEquationColor( Color color ) {
        setColorDeep( this, color );
    }

    private static void setColorDeep( PNode node, Color color ) {
        for ( int i = 0; i < node.getChildrenCount(); i++ ) {
            PNode child = node.getChild( i );
            if ( child instanceof PText ) {
                ( (PText) child ).setTextPaint( color );
            }
            else if ( child instanceof PPath ) {
                ( (PPath) child ).setStrokePaint( color );
            }
            setColorDeep( child, color );
        }
    }

}
