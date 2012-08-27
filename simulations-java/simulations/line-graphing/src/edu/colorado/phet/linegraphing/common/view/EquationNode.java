// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Paint;

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
    public void setPaintDeep( Paint paint ) {
        setPaintDeep( this, paint );
    }

    public static void setPaintDeep( PNode node, Paint paint ) {
        for ( int i = 0; i < node.getChildrenCount(); i++ ) {
            PNode child = node.getChild( i );
            if ( child instanceof PText ) {
                ( (PText) child ).setTextPaint( paint );
            }
            else if ( child instanceof PPath ) {
                ( (PPath) child ).setStrokePaint( paint );
            }
            setPaintDeep( child, paint );
        }
    }

}
