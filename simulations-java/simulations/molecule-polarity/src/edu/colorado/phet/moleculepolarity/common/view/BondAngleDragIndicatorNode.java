// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.IMolecule;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Indicator that dragging an atom will change the bond angle.
 * This consists of a pair of arrows on either side of the atom,
 * rotated to indicate the drag direction that will result in a change to bond angle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondAngleDragIndicatorNode extends PComposite {

    private static final int ARROW_LENGTH = 25; // relatively short, so we don't need curved arrows

    private final IMolecule molecule;
    private final Atom atom;
    private final PPath leftNode, rightNode;

    public BondAngleDragIndicatorNode( final IMolecule molecule, final Atom atom ) {

        // Indicator itself is not interactive.
        setPickable( false );
        setChildrenPickable( false );

        this.molecule = molecule;
        this.atom = atom;

        leftNode = new PPath() {{
            setPaint( atom.getColor() );
            setStrokePaint( Color.GRAY );
        }};
        addChild( leftNode );

        rightNode = new PPath() {{
            setPaint( atom.getColor() );
            setStrokePaint( Color.GRAY );
        }};
        addChild( rightNode );

        atom.location.addObserver( new SimpleObserver() {
            public void update() {
                updateNode();
            }
        } );
    }

    private void updateNode() {

        // create "normalized" shapes at (0,0) with no rotation
        final double radius = atom.getDiameter() / 2;
        final double spacing = 2;
        IndicatorArrow leftArrow = new IndicatorArrow( new Point2D.Double( -( radius + spacing ), 0 ), new Point2D.Double( -( radius + spacing + ARROW_LENGTH ), 0 ) );
        IndicatorArrow rightArrow = new IndicatorArrow( new Point2D.Double( ( radius + spacing ), 0 ), new Point2D.Double( ( radius + spacing + ARROW_LENGTH ), 0 ) );

        // transform the shapes to account for atom location and relationship to molecule location
        ImmutableVector2D v = new ImmutableVector2D( molecule.getLocation(), atom.location.get() );
        double angle = v.getAngle() - ( Math.PI / 2 );
        AffineTransform transform = new AffineTransform();
        transform.translate( atom.location.get().getX(), atom.location.get().getY() );
        transform.rotate( angle );
        Shape leftShape = transform.createTransformedShape( leftArrow.getShape() );
        Shape rightShape = transform.createTransformedShape( rightArrow.getShape() );

        leftNode.setPathTo( leftShape );
        rightNode.setPathTo( rightShape );
    }

    // Encapsulates the "look" of the arrows.
    private static class IndicatorArrow extends Arrow {
        public IndicatorArrow( Point2D pTail, Point2D pTip ) {
            super( pTail, pTip, 20, 20, 10 );
        }
    }
}
