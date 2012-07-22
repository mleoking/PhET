// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.Bond;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for the visual representations of partial charge, a delta symbol followed by either + or -.
 * Controls its own offset in world coordinates, so clients should not call setOffset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PartialChargeNode extends PComposite {

    private static final double REFERENCE_MAGNITUDE = MPConstants.ELECTRONEGATIVITY_RANGE.getLength();
    private static final double REFERENCE_SCALE = 1;

    protected final RichSimpleObserver observer;

    /**
     * Constructor.
     *
     * @param atom               the atom whose partial charge is displayed
     * @param unitVectorFunction a function that determines where the charge is placed along the outside edge of the atom
     */
    public PartialChargeNode( final Atom atom, final Function0<Vector2D> unitVectorFunction ) {

        final PText textNode = new PText() {{
            setFont( new PhetFont( 32 ) );
            setTextPaint( Color.BLACK );
        }};
        addChild( textNode );

        observer = new RichSimpleObserver() {
            public void update() {

                final double partialCharge = atom.partialCharge.get();

                textNode.setVisible( partialCharge != 0 ); // invisible if dipole is zero

                if ( partialCharge != 0 ) {

                    // d+ or d-
                    if ( partialCharge > 0 ) {
                        textNode.setText( MPStrings.DELTA + "+" );
                    }
                    else {
                        textNode.setText( MPStrings.DELTA + "-" );
                    }

                    // size proportional to bond dipole magnitude
                    final double scale = Math.abs( REFERENCE_SCALE * partialCharge / REFERENCE_MAGNITUDE );
                    if ( scale != 0 ) {
                        textNode.setScale( scale );
                        textNode.setOffset( -textNode.getFullBoundsReference().getWidth() / 2, -textNode.getFullBoundsReference().getHeight() / 2 ); // origin at center
                    }

                    // A vector that points in the direction we will need to move the charge node.
                    Vector2D unitVector = unitVectorFunction.apply();

                    // Compute the amount to move the partial charge node
                    double multiplier = ( atom.getDiameter() / 2 ) + ( Math.max( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() ) / 2 ) + 3;
                    Vector2D relativeOffset = unitVector.times( multiplier );
                    setOffset( atom.location.get().plus( relativeOffset ).toPoint2D() );
                }
            }
        };
        observer.observe( atom.partialCharge, atom.location );
    }

    /*
     * Partial charge for an atom that participates in a single bond.
     * It's partial charge is the opposite of the charge of the other atom in the bond.
     * The charge is placed along the axis of the bond, away from the atom.
     */
    public static class OppositePartialChargeNode extends PartialChargeNode {
        public OppositePartialChargeNode( final Atom atom, final Bond bond ) {
            super( atom, new Function0<Vector2D>() {
                public Vector2D apply() {
                    // along the bond axis, in the direction of the atom
                    Vector2D v = new Vector2D( bond.getCenter(), atom.location.get() );
                    /*
                     * Avoid the case where pressing Reset All causes the atoms to swap locations, temporarily
                     * resulting in a zero-magnitude vector when the first atom has moved but the second atom
                     * hasn't moved yet. This sorts itself out when both atoms have moved.
                     */
                    if ( v.magnitude() > 0 ) {
                        v = v.normalized();
                    }
                    return v;
                }
            } );
        }
    }

    /*
     * Partial charge for an atom that participates in more than one bond.
     * It's partial charge is the composite of charges contributed by other atoms in the bonds.
     * The charge is placed along the axis of the molecular dipole, on the opposite side of the atom from the dipole.
     */
    public static class CompositePartialChargeNode extends PartialChargeNode {
        public CompositePartialChargeNode( final Atom atom, final Molecule2D molecule ) {
            super( atom, new Function0<Vector2D>() {
                public Vector2D apply() {
                    Vector2D normalVector;
                    if ( molecule.dipole.get().magnitude() > 0 ) {
                        normalVector = molecule.dipole.get().getRotatedInstance( Math.PI ).normalized();
                    }
                    else {
                        // can't normalize a zero-magnitude vector, so create our own with the proper angle
                        normalVector = new Vector2D( 1, molecule.dipole.get().getAngle() );
                    }
                    return normalVector;
                }
            } );
            molecule.dipole.addObserver( observer );
        }
    }
}
