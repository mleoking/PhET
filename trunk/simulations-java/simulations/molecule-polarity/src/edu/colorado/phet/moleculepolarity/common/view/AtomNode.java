// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Visual representation of an atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomNode extends PhetPNode {

    public AtomNode( Atom atom ) {

        // shaded sphere
        final ShadedSphereNode sphereNode = new ShadedSphereNode( atom.getDiameter(), atom.getColor() );
        addChild( sphereNode );

        // atom name
        addChild( new PText( atom.getName() ) {{
            setFont( new PhetFont( Font.BOLD, 32 ) );
            setOffset( sphereNode.getFullBoundsReference().getCenterX() - getFullBoundsReference().getWidth() / 2,
                       sphereNode.getFullBoundsReference().getCenterY() - getFullBoundsReference().getHeight() / 2 );
        }} );

        // change offset when atom moves
        atom.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                setOffset( location.toPoint2D() );
            }
        } );
    }
}
