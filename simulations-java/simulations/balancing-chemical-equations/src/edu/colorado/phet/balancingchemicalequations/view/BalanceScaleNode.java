// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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
    private final ArrayList<AtomNode> atomNodes;
    private BeamNode beamNode;
    private int leftNumberOfAtoms, rightNumberOfAtoms;

    public BalanceScaleNode( Atom atom, int leftNumberOfAtoms, int rightNumberOfAtoms ) {

        this.atom = atom;
        this.leftNumberOfAtoms = leftNumberOfAtoms;
        this.rightNumberOfAtoms = rightNumberOfAtoms;

        this.atomNodes = new ArrayList<AtomNode>();

        FulcrumNode fulcrumNode = new FulcrumNode( atom );
        addChild( fulcrumNode );

        update();
    }

    public void setNumberOfAtoms( int leftNumberOfAtoms, int rightNumberOfAtoms ) {
        if ( leftNumberOfAtoms != this.leftNumberOfAtoms || rightNumberOfAtoms != this.rightNumberOfAtoms ) {
            this.leftNumberOfAtoms = leftNumberOfAtoms;
            this.rightNumberOfAtoms = rightNumberOfAtoms;
            update();
        }
    }

    private void update() {

        // beam, with appropriate tilt
        if ( beamNode != null ) {
            removeChild( beamNode );
        }
        beamNode = new BeamNode( leftNumberOfAtoms - rightNumberOfAtoms, FulcrumNode.SIZE.getHeight() );
        addChild( beamNode );

        // update to match leftNumberOfAtoms and rightNumberOfAtoms
    }

    /*
     * Atoms that appear on the scale.
     * Origin at upper left of bounding rectangle.
     */
    private static class AtomNode extends PImage {
        public AtomNode( Atom atom ) {
            setImage( atom.getImage() );
        }
    }

    /*
     * The beam is a horizontal lever, centered on the fulcrum, and pivoted
     * to represent the relationship between quantities on either side of the fulcrum.
     */
    private static class BeamNode extends PPath {

        private static final int LENGTH = 200;
        private static final int DIFFERENCE_FOR_MAX_TILT = 4;

        public BeamNode( int difference, double fulcrumHeight ) {
            Line2D line = new Line2D.Double( -LENGTH / 2, 0, LENGTH / 2, 0 );
            setPathTo( line );
            setStroke( new BasicStroke( 1f ) );
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
