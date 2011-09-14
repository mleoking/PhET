// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Indicator that dragging an atom will change the bond angle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondAngleDragIndicatorNode extends PComposite {

    private final Atom atom;
    private PNode arrowNode;

    public BondAngleDragIndicatorNode( final Atom atom ) {

        // Indicator itself is not interactive.
        setPickable( false );
        setChildrenPickable( false );

        this.atom = atom;

        atom.location.addObserver( new SimpleObserver() {
            public void update() {
                updateNode();
            }
        } );
    }

    private void updateNode() {
        if ( arrowNode != null ) {
            removeChild( arrowNode );
        }
        double length = atom.getDiameter() + 60;
        double x = atom.location.get().getX() - ( length / 2 );
        double y = atom.location.get().getY();
        Point2D pTail = new Point2D.Double( x, y );
        Point2D pTip = new Point2D.Double( x + length, y );
        arrowNode = new DoubleArrowNode( pTail, pTip, 20, 20, 10 ) {{
            setPaint( atom.getColor() );
        }};
        addChild( arrowNode );
    }
}
