package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
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

    private static final double MIN_RADIUS_FOR_LABEL = 10; // In screen units, which is roughly pixels.

    private final SphericalNode sphericalNode;

    /**
     * Constructor.
     */
    public AtomNode( final ModelViewTransform mvt, final AtomModel atom ) {

        final Color baseColor = atom.getColor();
        // Create a gradient that gives the particles a 3D look.  The numbers
        // used were empirically determined.
        double radius = atom.getRadius();
        double transformedRadius = mvt.modelToViewDeltaX( radius );
        Paint particlePaint = new RoundGradientPaint(
                transformedRadius / 2,
                -transformedRadius / 2,
                ColorUtils.brighterColor( baseColor, 0.8 ),
                new Point2D.Double( transformedRadius / 2, transformedRadius / 2 ),
                ColorUtils.darkerColor( baseColor, 0.2 ) );
        sphericalNode = new SphericalNode( mvt.modelToViewDeltaX( radius * 2 ), particlePaint, false );
        addChild( sphericalNode );

        // Create, scale, and add the label, assuming the atom is large enough.
        if ( transformedRadius > MIN_RADIUS_FOR_LABEL ) {
            PText labelNode = new PText() {{
                setText( atom.getAtom().getSymbol() );
                setFont( new PhetFont( 12, true ) );
                setScale( sphericalNode.getFullBoundsReference().width * 0.65 / getFullBoundsReference().width );
                if ( 0.30 * baseColor.getRed() + 0.59 * baseColor.getGreen() + 0.11 * baseColor.getBlue() < 125 ) {
                    setTextPaint( Color.WHITE );
                }
                setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height / 2 );
            }};
            sphericalNode.addChild( labelNode );
        }

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
