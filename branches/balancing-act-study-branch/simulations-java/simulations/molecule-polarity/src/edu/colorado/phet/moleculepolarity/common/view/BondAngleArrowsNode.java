// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A pair of arrows that are placed around an atom, indicating that dragging the atom will change the bond angle.
 * Shapes are created in world coordinates, so this node's offset should be (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondAngleArrowsNode extends PComposite {

    private static final int ARROW_LENGTH = 25; // relatively short, so we don't need curved arrows

    private final Molecule2D molecule;
    private final Atom atom;
    private final PPath leftArrowNode, rightArrowNode;

    public BondAngleArrowsNode( final Molecule2D molecule, final Atom atom ) {

        // Indicator itself is not interactive.
        setPickable( false );
        setChildrenPickable( false );

        this.molecule = molecule;
        this.atom = atom;

        leftArrowNode = new PPath() {{
            setPaint( atom.getColor() );
            setStrokePaint( Color.GRAY );
        }};
        addChild( leftArrowNode );

        rightArrowNode = new PPath() {{
            setPaint( atom.getColor() );
            setStrokePaint( Color.GRAY );
        }};
        addChild( rightArrowNode );

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
        Vector2D v = new Vector2D( molecule.location, atom.location.get() );
        double angle = v.getAngle() - ( Math.PI / 2 );
        AffineTransform transform = new AffineTransform();
        transform.translate( atom.location.get().getX(), atom.location.get().getY() );
        transform.rotate( angle );
        Shape leftShape = transform.createTransformedShape( leftArrow.getShape() );
        Shape rightShape = transform.createTransformedShape( rightArrow.getShape() );

        leftArrowNode.setPathTo( leftShape );
        rightArrowNode.setPathTo( rightShape );
    }

    // Encapsulates the "look" of the arrows.
    private static class IndicatorArrow extends Arrow {
        public IndicatorArrow( Point2D pTail, Point2D pTip ) {
            super( pTail, pTip, 20, 20, 10 );
        }
    }
}
