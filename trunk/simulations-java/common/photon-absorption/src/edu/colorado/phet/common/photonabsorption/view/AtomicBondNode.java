// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.photonabsorption.model.atoms.AtomicBond;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * Class that represents an atomic bond in the view.
 *
 * @author John Blanco
 */
public class AtomicBondNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // Constants that control the width of the bond representation with
    // with respect to the average atom radius.
    private static double BOND_WIDTH_PROPORTION_SINGLE = 0.45;
    private static double BOND_WIDTH_PROPORTION_DOUBLE = 0.28;
    private static double BOND_WIDTH_PROPORTION_TRIPLE = 0.24;

    private static Color BOND_COLOR = new Color( 0, 200, 0 );

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final AtomicBond atomicBond;
    private final ModelViewTransform2D mvt;
    private final double averageAtomRadius;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public AtomicBondNode( AtomicBond atomicBond, ModelViewTransform2D mvt ) {
        assert atomicBond.getBondCount() > 0 && atomicBond.getBondCount() <= 3;  // Only single through triple bonds currently supported.
        this.atomicBond = atomicBond;
        this.mvt = mvt;

        // Listen to the bond for changes that may require an update of the
        // representation.
        atomicBond.addObserver( new SimpleObserver() {
            public void update() {
                updateRepresentation();
            }
        } );

        // Calculate the width to use for the bond representation(s).
        averageAtomRadius = mvt.modelToViewDifferentialXDouble( ( atomicBond.getAtom1().getRadius() + atomicBond.getAtom2().getRadius() ) / 2 );

        // Create the initial representation.
        updateRepresentation();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    private void updateRepresentation() {
        removeAllChildren();  // Clear out any previous representations.
        float bondWidth;
        switch( atomicBond.getBondCount() ) {
            case 1: {
                // Single bond, so connect it from the center of one atom to the
                // center of the other.
                Point2D transformedPt1 = mvt.modelToViewDouble( atomicBond.getAtom1().getPositionRef() );
                Point2D transformedPt2 = mvt.modelToViewDouble( atomicBond.getAtom2().getPositionRef() );
                bondWidth = (float) ( BOND_WIDTH_PROPORTION_SINGLE * averageAtomRadius );
                PPath bond = new PhetPPath( new BasicStroke( bondWidth ), BOND_COLOR );
                bond.setPathTo( new Line2D.Double( transformedPt1, transformedPt2 ) );
                addChild( bond );
                break;
            }

            case 2: {
                // Double bond.
                final double transformedRadius = mvt.modelToViewDifferentialXDouble( Math.min( atomicBond.getAtom1().getRadius(),
                                                                                               atomicBond.getAtom2().getRadius() ) );
                // Get the center points of the two atoms.
                Point2D p1 = mvt.modelToViewDouble( atomicBond.getAtom1().getPositionRef() );
                Point2D p2 = mvt.modelToViewDouble( atomicBond.getAtom2().getPositionRef() );
                final double angle = Math.atan2( p1.getX() - p2.getX(), p1.getY() - p2.getY() );
                // Create a vector that will act as the offset from the center
                // point to the origin of the bond line.
                Vector2D offsetVector = new Vector2D() {{
                    setMagnitude( transformedRadius / 3 );
                    setAngle( angle );
                }};
                // Draw the bonds.
                Stroke bondLineStroke = new BasicStroke( (float) ( BOND_WIDTH_PROPORTION_DOUBLE * averageAtomRadius ) );
                PPath bond1 = new PhetPPath( bondLineStroke, BOND_COLOR );
                bond1.setPathTo( new Line2D.Double( p1.getX() + offsetVector.getX(), p1.getY() - offsetVector.getY(), p2.getX() + offsetVector.getX(), p2.getY() - offsetVector.getY() ) );
                offsetVector.rotate( Math.PI );
                PPath bond2 = new PhetPPath( bondLineStroke, BOND_COLOR );
                bond2.setPathTo( new Line2D.Double( p1.getX() + offsetVector.getX(), p1.getY() - offsetVector.getY(), p2.getX() + offsetVector.getX(), p2.getY() - offsetVector.getY() ) );
                addChild( bond1 );
                addChild( bond2 );
                break;
            }

            case 3: {
                // Triple bond.
                // Double bond.
                final double transformedRadius = mvt.modelToViewDifferentialXDouble( Math.min( atomicBond.getAtom1().getRadius(),
                                                                                               atomicBond.getAtom2().getRadius() ) );
                // Get the center points of the two atoms.
                Point2D p1 = mvt.modelToViewDouble( atomicBond.getAtom1().getPositionRef() );
                Point2D p2 = mvt.modelToViewDouble( atomicBond.getAtom2().getPositionRef() );
                final double angle = Math.atan2( p1.getX() - p2.getX(), p1.getY() - p2.getY() );
                // Create a vector that will act as the offset from the center
                // point to the origin of the bond line.
                Vector2D offsetVector = new Vector2D() {{
                    setMagnitude( transformedRadius * 0.6 );
                    setAngle( angle );
                }};
                // Draw the bonds.
                Stroke bondLineStroke = new BasicStroke( (float) ( BOND_WIDTH_PROPORTION_TRIPLE * averageAtomRadius ) );
                PPath bond1 = new PhetPPath( bondLineStroke, BOND_COLOR );
                bond1.setPathTo( new Line2D.Double( p1, p2 ) );
                PPath bond2 = new PhetPPath( bondLineStroke, BOND_COLOR );
                bond2.setPathTo( new Line2D.Double( p1.getX() + offsetVector.getX(), p1.getY() - offsetVector.getY(), p2.getX() + offsetVector.getX(), p2.getY() - offsetVector.getY() ) );
                offsetVector.rotate( Math.PI );
                PPath bond3 = new PhetPPath( bondLineStroke, BOND_COLOR );
                bond3.setPathTo( new Line2D.Double( p1.getX() + offsetVector.getX(), p1.getY() - offsetVector.getY(), p2.getX() + offsetVector.getX(), p2.getY() - offsetVector.getY() ) );
                addChild( bond1 );
                addChild( bond2 );
                addChild( bond3 );
                break;
            }

            default:
                System.err.println( getClass().getName() + " - Error: Can't represent bond number, value = " + atomicBond.getBondCount() );
                assert false;
                break;
        }
    }
}
