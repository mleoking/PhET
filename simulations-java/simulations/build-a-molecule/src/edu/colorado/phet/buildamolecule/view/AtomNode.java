package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents an atom and that labels it with the chemical symbol
 * and atomic number so that isotopes of the same element can be distinguished
 * from one another.
 *
 * @author John Blanco
 */
public class AtomNode extends PNode {

    private final ShadedSphereNode sphericalNode;

    public AtomNode( final ModelViewTransform mvt, final AtomModel atom ) {

        double transformedRadius = mvt.modelToViewDeltaX( atom.getRadius() );
        sphericalNode = new ShadedSphereNode( transformedRadius, atom.getColor() );
        addChild( sphericalNode );

        // Create, scale, and add the label
        PText labelNode = new PText() {{
            setText( atom.getAtom().getSymbol() );
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
                sphericalNode.setOffset( mvt.modelToView( atom.getPosition() ) );
            }
        } );

        // Add a cursor handler to signal to the user that this is movable.
        addInputEventListener( new CursorHandler() );

        // Add a drag listener that will move the model element when the user
        // drags this node.
        addInputEventListener( new PDragEventHandler() {

            @Override
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                atom.setUserControlled( true );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( delta.width, delta.height ) );
                atom.setPositionAndDestination( atom.getPosition().getX() + modelDelta.getX(),
                                                atom.getPosition().getY() + modelDelta.getY() );
            }

            @Override
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                atom.setUserControlled( false );
            }
        } );
    }
}
