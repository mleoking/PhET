// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Indicator that dragging an atom will change the bond angle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondAngleDragIndicatorNode extends PComposite {

    private final Atom atom;
    private final PPath barNode;

    public BondAngleDragIndicatorNode( final Atom atom ) {

        // Indicator itself is not interactive.
        setPickable( false );
        setChildrenPickable( false );

        this.atom = atom;

        this.barNode = new PPath() {{
            setStroke( new BasicStroke( 12f ) );
            setStrokePaint( atom.getColor() );
        }};
        addChild( barNode );

        atom.location.addObserver( new SimpleObserver() {
            public void update() {
                updateNode();
            }
        } );
    }

    private void updateNode() {
        double length = atom.getDiameter() + 40;
        double x = atom.location.get().getX() - ( length / 2 );
        double y = atom.location.get().getY();
        barNode.setPathTo( new Line2D.Double( x, y, x + length, y ) );
    }
}
