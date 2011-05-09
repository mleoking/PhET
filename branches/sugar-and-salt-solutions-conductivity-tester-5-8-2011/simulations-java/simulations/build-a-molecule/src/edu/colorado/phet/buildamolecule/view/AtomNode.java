// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.model.AtomModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that displays an atom and that labels it with the chemical symbol
 *
 * @author John Blanco
 * @author Jonathan Olson
 */
public class AtomNode extends PNode {

    private final ShadedSphereNode sphericalNode;

    public AtomNode( final ModelViewTransform mvt, final AtomModel atom ) {

        double transformedRadius = mvt.modelToViewDeltaX( atom.getRadius() );
        sphericalNode = new ShadedSphereNode( 2 * transformedRadius, atom.getColor() );
        addChild( sphericalNode );

        // Create, scale, and add the label
        PText labelNode = new PText() {{
            setText( atom.getAtomInfo().getSymbol() );
            setFont( new PhetFont( 10, true ) );
            setScale( sphericalNode.getFullBoundsReference().width * 0.65 / getFullBoundsReference().width );
            if ( 0.30 * atom.getColor().getRed() + 0.59 * atom.getColor().getGreen() + 0.11 * atom.getColor().getBlue() < 125 ) {
                setTextPaint( Color.WHITE );
            }
            setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height / 2 );
        }};
        sphericalNode.addChild( labelNode );

        // Add the code for moving this node when the model element's position
        // changes.
        atom.addPositionListener( new SimpleObserver() {
            public void update() {
                sphericalNode.setOffset( mvt.modelToView( atom.getPosition() ).toPoint2D() );
            }
        } );

        // Add a cursor handler to signal to the user that this is movable.
        addInputEventListener( new CursorHandler() );

        // respond to the visibility of the atom
        atom.visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( atom.visible.getValue() );
            }
        } );

        // and if we remove the atom from play, we will get rid of this node
        atom.addListener( new AtomModel.Adapter() {
            @Override
            public void removedFromModel( AtomModel atom ) {
                getParent().removeChild( AtomNode.this );
            }
        } );
    }
}
