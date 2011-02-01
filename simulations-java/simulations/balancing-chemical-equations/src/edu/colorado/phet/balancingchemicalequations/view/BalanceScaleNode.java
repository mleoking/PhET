// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A balance scale, depicts the relationship between the atom count
 * on the left and right side of an equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceScaleNode extends PComposite {

    private final Atom atom;
    private int leftNumberOfAtoms, rightNumberOfAtoms;
    private final PNode atomPilesParentNode;

    public BalanceScaleNode( Atom atom, int leftNumberOfAtoms, int rightNumberOfAtoms ) {

        this.atom = atom;
        this.leftNumberOfAtoms = leftNumberOfAtoms;
        this.rightNumberOfAtoms = rightNumberOfAtoms;

        final FulcrumNode fulcrumNode = new FulcrumNode( atom );
        addChild( fulcrumNode );

        BeamNode beamNode = new BeamNode();
        addChild( beamNode );

        atomPilesParentNode = new PComposite();
        addChild( atomPilesParentNode );

        // layout
        double x = 0;
        double y = 0;
        fulcrumNode.setOffset( x, y );
        beamNode.setOffset( x, y );
        atomPilesParentNode.setOffset( x, y );

        updateNode();
    }

    public void setNumberOfAtoms( int leftNumberOfAtoms, int rightNumberOfAtoms ) {
        if ( leftNumberOfAtoms != this.leftNumberOfAtoms || rightNumberOfAtoms != this.rightNumberOfAtoms ) {
            this.leftNumberOfAtoms = leftNumberOfAtoms;
            this.rightNumberOfAtoms = rightNumberOfAtoms;
            updateNode();
        }
    }

    private void updateNode() {

        atomPilesParentNode.removeAllChildren();

        // left pile of atoms, centered on left-half of beam
        PNode leftPileNode = createAtomPile( leftNumberOfAtoms, atom );
        leftPileNode.setOffset( -( 0.25 * BeamNode.LENGTH ) - ( leftPileNode.getFullBoundsReference().getWidth() / 2 ), 0 );
        atomPilesParentNode.addChild( leftPileNode );

        // right pile of atoms, centered on right-half of beam
        PNode rightPileNode = createAtomPile( rightNumberOfAtoms, atom );
        rightPileNode.setOffset( ( 0.25 * BeamNode.LENGTH ) - ( rightPileNode.getFullBoundsReference().getWidth() / 2 ), 0 );
        atomPilesParentNode.addChild( rightPileNode );

        // left count, centered above left pile
        CountNode leftCountNode = new CountNode( leftNumberOfAtoms );
        addChild( leftCountNode );
        double x = leftPileNode.getXOffset() + ( leftPileNode.getFullBoundsReference().getWidth() / 2 ) - ( leftCountNode.getFullBoundsReference().getWidth() / 2 );
        double y = leftPileNode.getFullBoundsReference().getMinY() - leftCountNode.getFullBoundsReference().getHeight() - 2;
        leftCountNode.setOffset( x, y );

        // right count, centered above right pile
        CountNode rightCountNode = new CountNode( rightNumberOfAtoms );
        addChild( rightCountNode );
        x = rightPileNode.getXOffset() + ( rightPileNode.getFullBoundsReference().getWidth() / 2 ) - ( rightCountNode.getFullBoundsReference().getWidth() / 2 );
        y = rightPileNode.getFullBoundsReference().getMinY() - rightCountNode.getFullBoundsReference().getHeight() - 2;
        rightCountNode.setOffset( x, y );

        //TODO rotate beam and atoms on fulcrum
    }

    /*
     * Creates a triangular pile of atoms.
     * Origin is at the lower-left corner of the pile.
     */
    private static PNode createAtomPile( int numberOfAtoms, Atom atom ) {
        PComposite parent = new PComposite();
        final int atomsInBase = 5; // number of atoms along the base of each pile
        int atomsInRow = atomsInBase;
        int row = 0;
        int pile = 0;
        double x = 0;
        double y = 0;
        for ( int i = 0; i < numberOfAtoms; i++ ) {

            AtomNode atomNode = new AtomNode( atom );
            parent.addChild( atomNode );

            atomNode.setOffset( x, y - atomNode.getFullBoundsReference().getHeight() );

            atomsInRow--;
            if ( atomsInRow > 0 ) {
                // continue with current row
                x = atomNode.getFullBoundsReference().getMaxX();
            }
            else if ( row < atomsInBase - 1 ) {
                // move to next row in current triangular pile
                row++;
                atomsInRow = atomsInBase - row;
                x = (double) ( pile + row ) * ( atomNode.getFullBoundsReference().getWidth() / 2 );
                y = -( row * atomNode.getFullBoundsReference().getHeight() );
            }
            else {
                // start a new pile, offset from the previous pile
                row = 0;
                pile++;
                atomsInRow = atomsInBase;
                x = (double) pile * ( atomNode.getFullBoundsReference().getWidth() / 2 );
                y = 0;
            }
        }
        return parent;
    }

    /*
     * Atoms that appear on the scale.
     * Origin at upper left of bounding rectangle.
     */
    private static class AtomNode extends PImage {
        public AtomNode( Atom atom ) {
            setImage( atom.getImage() );
            scale( 0.35 );
        }
    }

    /*
     * Displays an atom count.
     */
    private static class CountNode extends PText {

        public CountNode( int count ) {
            setText( String.valueOf( count ) );
            setFont( new PhetFont( 14 ) );
            setTextPaint( Color.BLACK );
        }

        public void setCount( int count ) {
            setText( String.valueOf( count ) );
        }
    }

    /*
     * The beam is a horizontal lever, centered on the fulcrum.
     * It will be pivoted to represent the relationship between quantities on either side of the fulcrum.
     */
    private static class BeamNode extends PPath {

        private static final double LENGTH = 200;

        public BeamNode() {
            Line2D line = new Line2D.Double( -LENGTH / 2, 0, LENGTH / 2, 0 );
            setPathTo( line );
            setStroke( new BasicStroke( 2f ) );
            setStrokePaint( Color.BLACK );
        }
    }

    /*
     * Fulcrum on which the scale balances.
     * Labeled with the atom symbol.
     * Origin is at the tip of the fulcrum.
     */
    private static class FulcrumNode extends PComposite {

        private static final PDimension SIZE = new PDimension( 40, 30 );
        private static final Paint FILL_PAINT = new GradientPaint( new Point2D.Double( 0, 0 ), Color.WHITE, new Point2D.Double( 0, SIZE.getHeight() ), Color.LIGHT_GRAY );

        public FulcrumNode( Atom atom ) {

            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f );
            path.lineTo( (float)(SIZE.getWidth()/2), (float)SIZE.getHeight() );
            path.lineTo( (float)(-SIZE.getWidth()/2), (float)SIZE.getHeight() );
            path.closePath();
            PPath pathNode = new PPath( path );
            pathNode.setPaint( FILL_PAINT );
            pathNode.setStroke( new BasicStroke( 1f ) );
            pathNode.setStrokePaint( Color.BLACK );
            addChild( pathNode );

            PText symbolNode = new PText( atom.getSymbol() );
            symbolNode.setFont( new PhetFont( 14 ) );
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
