// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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

    private final static int DIFFERENCE_FOR_MAX_TILT = 4;

    private final Atom atom;
    private int leftNumberOfAtoms, rightNumberOfAtoms;

    public BalanceScaleNode( Atom atom, int leftNumberOfAtoms, int rightNumberOfAtoms ) {
        this.atom = atom;
        this.leftNumberOfAtoms = leftNumberOfAtoms;
        this.rightNumberOfAtoms = rightNumberOfAtoms;
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
        removeAllChildren();

        FulcrumNode fulcrumNode = new FulcrumNode( atom );
        addChild( fulcrumNode );

        // update to match leftNumberOfAtoms and rightNumberOfAtoms
    }

    /*
     * Fulcrum on which the scale balances.
     * Labeled with the atom symbol.
     * Origin is at the tip of the fulcrum.
     */
    private static class FulcrumNode extends PComposite {

        private static final PDimension SIZE = new PDimension( 40, 25 );

        public FulcrumNode( Atom atom ) {

            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f );
            path.lineTo( (float)(SIZE.getWidth()/2), (float)SIZE.getHeight() );
            path.lineTo( (float)(-SIZE.getWidth()/2), (float)SIZE.getHeight() );
            path.closePath();
            PPath pathNode = new PPath( path );
            pathNode.setPaint( Color.WHITE );
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
