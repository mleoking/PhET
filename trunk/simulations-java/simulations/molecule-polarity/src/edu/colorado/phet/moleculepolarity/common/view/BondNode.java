// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.common.model.Bond;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Visual representation of a bond between 2 atoms.
 * Intended to be rendered before the 2 atoms, so that the atoms cover the portion of the bond that overlaps the atoms.
 * Shapes are created in world coordinates, so this node's offset should be (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondNode extends PPath {

    public BondNode( final Bond bond ) {
        setStrokePaint( MPColors.BOND );
        setStroke( new BasicStroke( 12f ) );
        VoidFunction1<Vector2D> updater = new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setPathTo( new Line2D.Double( bond.endpoint1.get().toPoint2D(), bond.endpoint2.get().toPoint2D() ) );
            }
        };
        bond.endpoint1.addObserver( updater );
        bond.endpoint2.addObserver( updater );
    }
}
