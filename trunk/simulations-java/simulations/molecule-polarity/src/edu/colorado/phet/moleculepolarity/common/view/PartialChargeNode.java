// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.Bond;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of partial charge, a delta followed by either + or -.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PartialChargeNode extends PComposite {

    private static final double REF_MAGNITUDE = MPConstants.ELECTRONEGATIVITY_RANGE.getLength();
    private static final double REF_SCALE = 1;

    public PartialChargeNode( final Bond bond, final Atom atom, final boolean positivePolarity ) {

        final PText textNode = new PText() {{
            setFont( new PhetFont( 32 ) );
            setTextPaint( Color.BLACK );
        }};
        addChild( textNode );

        SimpleObserver updater = new SimpleObserver() {
            public void update() {

                final double deltaElectronegativity = bond.deltaElectronegativity.get();

                textNode.setVisible( deltaElectronegativity != 0 ); // invisible if dipole is zero

                if ( deltaElectronegativity != 0 ) {

                    // d+ or d-
                    if ( ( positivePolarity && deltaElectronegativity > 0 ) || ( !positivePolarity && deltaElectronegativity < 0 ) ) {
                        textNode.setText( MPStrings.DELTA + "-" );
                    }
                    else {
                        textNode.setText( MPStrings.DELTA + "+" );
                    }

                    // size proportional to bond dipole magnitude
                    final double scale = Math.abs( REF_SCALE * deltaElectronegativity / REF_MAGNITUDE );
                    if ( scale != 0 ) {
                        textNode.setScale( scale );
                        textNode.setOffset( -textNode.getFullBoundsReference().getWidth() / 2, -textNode.getFullBoundsReference().getHeight() / 2 ); // origin at center
                    }

                    //A vector that points in the direction we will need to move the charge node; away from the associated atom
                    ImmutableVector2D unitVectorFromBond = new ImmutableVector2D( bond.getCenter(), atom.location.get() ).getNormalizedInstance();

                    //Compute the amount to move the partial charge node
                    double multiplier = ( atom.getDiameter() / 2 ) + ( Math.max( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() ) / 2 ) + 3;
                    ImmutableVector2D relativeOffset = unitVectorFromBond.times( multiplier );
                    setOffset( atom.location.get().plus( relativeOffset ).toPoint2D() );
                }
            }
        };
        bond.deltaElectronegativity.addObserver( updater );
        atom.location.addObserver( updater );
    }
}
