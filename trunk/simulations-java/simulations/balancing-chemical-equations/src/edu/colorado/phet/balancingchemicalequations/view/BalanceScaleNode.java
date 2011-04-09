// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A balance scale, depicts the relationship between the atom count
 * on the left and right side of an equation.
 * <p>
 * The 2 main parts of a balance scale are the fulcrum and the beam.
 * Origin is at the tip of the fulcrum.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class BalanceScaleNode extends PComposite {

    private static final PDimension FULCRUM_SIZE = new PDimension( 60, 45 );
    private static final double BEAM_LENGTH = 315;
    private static final double BEAM_THICKNESS = 6;

    private static final int NUMBER_OF_TILT_ANGLES = 6;
    private static final int ATOMS_IN_PILE_BASE = 5; // number of atoms along the base of each pile

    private final Atom atom;
    private int leftNumberOfAtoms, rightNumberOfAtoms;
    private final BeamNode beamNode;
    private final PNode atomPilesParentNode;

    /**
     * Constructor.
     * @param atom the atom that we're displaying on the scale
     * @param leftNumberOfAtoms
     * @param rightNumberOfAtoms
     * @param highlighted whether the beam is highlighted (used to indicate whether the scale is balanaced)
     */
    public BalanceScaleNode( Atom atom, int leftNumberOfAtoms, int rightNumberOfAtoms, boolean highlighted ) {

        this.atom = atom;
        this.leftNumberOfAtoms = leftNumberOfAtoms;
        this.rightNumberOfAtoms = rightNumberOfAtoms;

        final FulcrumNode fulcrumNode = new FulcrumNode( atom );
        addChild( fulcrumNode );

        beamNode = new BeamNode();
        addChild( beamNode );

        atomPilesParentNode = new PComposite();
        addChild( atomPilesParentNode );

        setHighlighted( highlighted );
        updateNode();
    }

    //REVIEW: remove if unused
    /**
     * Sets the number of atoms on the left and right sides of the beam.
     * @param leftNumberOfAtoms
     * @param rightNumberOfAtoms
     */
    public void setNumberOfAtoms( int leftNumberOfAtoms, int rightNumberOfAtoms ) {
        if ( leftNumberOfAtoms != this.leftNumberOfAtoms || rightNumberOfAtoms != this.rightNumberOfAtoms ) {
            this.leftNumberOfAtoms = leftNumberOfAtoms;
            this.rightNumberOfAtoms = rightNumberOfAtoms;
            updateNode();
        }
    }

    /**
     * Determines whether the beam is highlighted, use to indicate whether the scale is balanced.
     * @param highlighted
     */
    public void setHighlighted( boolean highlighted ) {
        beamNode.setHighlighted( highlighted );
    }

    public static double getBeamLength() {
        return BEAM_LENGTH;
    }

    /*
     * Places piles of atoms on the ends of the beam, with a count of the number of
     * atoms above each pile.  Then rotates the beam and stuff on it to indicate the
     * relative balance between the left and right piles.
     */
    private void updateNode() {

        // all dynamic stuff is above the beam, and is children of atomPilesParentNode
        atomPilesParentNode.removeAllChildren();

        // left pile of atoms, centered on left-half of beam
        PNode leftPileNode = createAtomPile( leftNumberOfAtoms, atom );
        leftPileNode.setOffset( -( 0.25 * BEAM_LENGTH ) - ( leftPileNode.getFullBoundsReference().getWidth() / 2 ), 0 );
        atomPilesParentNode.addChild( leftPileNode );

        // right pile of atoms, centered on right-half of beam
        PNode rightPileNode = createAtomPile( rightNumberOfAtoms, atom );
        rightPileNode.setOffset( ( 0.25 * BEAM_LENGTH ) - ( rightPileNode.getFullBoundsReference().getWidth() / 2 ), 0 );
        atomPilesParentNode.addChild( rightPileNode );

        // left count, centered above left pile
        CountNode leftCountNode = new CountNode( leftNumberOfAtoms );
        atomPilesParentNode.addChild( leftCountNode );
        double x = leftPileNode.getXOffset() + ( leftPileNode.getFullBoundsReference().getWidth() / 2 ) - ( leftCountNode.getFullBoundsReference().getWidth() / 2 );
        double y = leftPileNode.getFullBoundsReference().getMinY() - leftCountNode.getFullBoundsReference().getHeight() - 2;
        leftCountNode.setOffset( x, y );

        // right count, centered above right pile
        CountNode rightCountNode = new CountNode( rightNumberOfAtoms );
        atomPilesParentNode.addChild( rightCountNode );
        x = rightPileNode.getXOffset() + ( rightPileNode.getFullBoundsReference().getWidth() / 2 ) - ( rightCountNode.getFullBoundsReference().getWidth() / 2 );
        y = rightPileNode.getFullBoundsReference().getMinY() - rightCountNode.getFullBoundsReference().getHeight() - 2;
        rightCountNode.setOffset( x, y );

        // rotate beam and piles on fulcrum
        double maxAngle = ( Math.PI / 2 ) - Math.acos( FULCRUM_SIZE.getHeight() / ( BEAM_LENGTH / 2 ) );
        final double difference = rightNumberOfAtoms - leftNumberOfAtoms;
        double angle = 0;
        if ( Math.abs( difference ) >= NUMBER_OF_TILT_ANGLES ) {
            // max tilt
            int sign = (int)( Math.abs( difference ) / difference );
            angle = sign * maxAngle;
        }
        else {
            // partial tilt
            angle = difference * ( maxAngle / NUMBER_OF_TILT_ANGLES );
        }
        beamNode.setRotation( angle );
        atomPilesParentNode.setRotation( angle );
    }

    /*
     * Creates a triangular pile of atoms.
     * Atoms are populated one row at a time, starting from the base of the triangle and working up.
     * Origin is at the lower-left corner of the pile.
     */
    private static PNode createAtomPile( int numberOfAtoms, Atom atom ) {
        PComposite parent = new PComposite();
        int atomsInRow = ATOMS_IN_PILE_BASE;
        int row = 0;
        int pile = 0;
        double x = 0;
        double y = 0;
        for ( int i = 0; i < numberOfAtoms; i++ ) {

            PNode atomNode = new AtomNode( atom );
            parent.addChild( atomNode );

            atomNode.setOffset( x + ( atomNode.getFullBoundsReference().getWidth() / 2 ), y - ( atomNode.getFullBoundsReference().getHeight() / 2 ) );

            atomsInRow--;
            if ( atomsInRow > 0 ) {
                // continue with current row
                x = atomNode.getFullBoundsReference().getMaxX();
            }
            else if ( row < ATOMS_IN_PILE_BASE - 1 ) {
                // move to next row in current triangular pile
                row++;
                atomsInRow = ATOMS_IN_PILE_BASE - row;
                x = (double) ( pile + row ) * ( atomNode.getFullBoundsReference().getWidth() / 2 );
                y = -( row * 0.85 * atomNode.getFullBoundsReference().getHeight() );
            }
            else {
                // start a new pile, offset from the previous pile
                row = 0;
                pile++;
                atomsInRow = ATOMS_IN_PILE_BASE;
                x = (double) pile * ( atomNode.getFullBoundsReference().getWidth() / 2 );
                y = 0;
            }
        }
        return parent;
    }

    /*
     * Displays an atom count.
     */
    private static class CountNode extends PText {
        public CountNode( int count ) {
            setText( String.valueOf( count ) );
            setFont( new PhetFont( 18 ) );
            setTextPaint( Color.BLACK );
        }
    }

    /*
     * The beam is a horizontal lever, centered on the fulcrum.
     * It will be pivoted to represent the relationship between quantities on either side of the fulcrum.
     */
    private static class BeamNode extends PPath {
        public BeamNode() {
            Rectangle2D shape = new Rectangle2D.Double( -BEAM_LENGTH / 2, -BEAM_THICKNESS / 2, BEAM_LENGTH, BEAM_THICKNESS );
            setPathTo( shape );
            setStrokePaint( Color.BLACK );
        }

        public void setHighlighted( boolean highlighted ) {
            setPaint( highlighted ? BCEConstants.BALANCED_HIGHLIGHT_COLOR : Color.BLACK );
            setStroke( highlighted ? new BasicStroke( 1f ) : null );
        }
    }

    /*
     * Fulcrum on which the scale balances.
     * Labeled with the atom symbol.
     * Origin is at the tip of the fulcrum.
     */
    private static class FulcrumNode extends PComposite {

        private static final Paint FILL_PAINT = new GradientPaint( new Point2D.Double( 0, 0 ), Color.WHITE, new Point2D.Double( 0, FULCRUM_SIZE.getHeight() ), Color.LIGHT_GRAY );

        public FulcrumNode( Atom atom ) {

            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f );
            path.lineTo( (float) ( FULCRUM_SIZE.getWidth() / 2 ), (float) FULCRUM_SIZE.getHeight() );
            path.lineTo( (float) ( -FULCRUM_SIZE.getWidth() / 2 ), (float) FULCRUM_SIZE.getHeight() );
            path.closePath();
            PPath pathNode = new PPath( path );
            pathNode.setPaint( FILL_PAINT );
            pathNode.setStroke( new BasicStroke( 1f ) );
            pathNode.setStrokePaint( Color.BLACK );
            addChild( pathNode );

            PText symbolNode = new PText( atom.getSymbol() );
            symbolNode.setFont( new PhetFont( 22 ) );
            symbolNode.setTextPaint( Color.BLACK );
            addChild( symbolNode );

            // layout
            double x = 0;
            double y = 0;
            pathNode.setOffset( x, y );
            x = -symbolNode.getFullBoundsReference().getWidth() / 2;
            y = pathNode.getFullBoundsReference().getMaxY() - symbolNode.getFullBoundsReference().getHeight() - 2;
            symbolNode.setOffset( x, y );
        }
    }
}
