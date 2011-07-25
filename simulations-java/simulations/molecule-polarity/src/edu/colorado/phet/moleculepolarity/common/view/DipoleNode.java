// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.moleculepolarity.common.model.Bond;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for visual representation of dipoles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DipoleNode extends PComposite {

    private static final double PERPENDICULAR_OFFSET = 30; // offset perpendicular to the axis of the endpoints
    private static final double ARROW_HEAD_WIDTH = 20;
    private static final double ARROW_HEAD_HEIGHT = 20;
    private static final double ARROW_TAIL_WIDTH = 5;

    private final ArrowNode arrowNode;

    protected DipoleNode( Color color ) {
        arrowNode = new ArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( 100, 0 ), ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        arrowNode.setPaint( color );
        addChild( arrowNode );
    }

    protected void setEndpoints( Point2D tip, Point2D tail ) {
        arrowNode.setTipAndTailLocations( tip, tail );
    }

    // Visual representation of a bond dipole.
    public static class BondDipoleNode extends DipoleNode {
        public BondDipoleNode( final Bond bond ) {
            super( Color.RED );

            // align the dipole with the bond
            SimpleObserver endPointObserver = new SimpleObserver() {
                public void update() {
                    setEndpoints( bond.endpoint1.get().toPoint2D(), bond.endpoint2.get().toPoint2D() );
                }
            };
            bond.endpoint1.addObserver( endPointObserver );
            bond.endpoint2.addObserver( endPointObserver );
        }
    }
}
