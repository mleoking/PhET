// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.IMolecule;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Indicator that dragging an atom will change the bond angle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondAngleDragIndicatorNode extends PComposite {

    private final IMolecule molecule;
    private final Atom atom;
    private final PPath pathNode;

    public BondAngleDragIndicatorNode( final IMolecule molecule, final Atom atom ) {

        // Indicator itself is not interactive.
        setPickable( false );
        setChildrenPickable( false );

        this.molecule = molecule;
        this.atom = atom;

        pathNode = new PPath() {{
            setPaint( atom.getColor() );
            setStrokePaint( Color.GRAY );
        }};
        addChild( pathNode );

        atom.location.addObserver( new SimpleObserver() {
            public void update() {
                updateNode();
            }
        } );
    }

    private void updateNode() {

        // create the "normalized" shape at (0,0)
        double length = atom.getDiameter() + 60;
        double thickness = 20;
        Shape normalShape = new Rectangle2D.Double( -length / 2, -thickness / 2, length, thickness );

        // transform the shape to account for atom location and relationship to molecule location
        ImmutableVector2D v = new ImmutableVector2D( molecule.getLocation(), atom.location.get() );
        double angle = v.getAngle() - ( Math.PI / 2 );
        AffineTransform transform = new AffineTransform();
        transform.translate( atom.location.get().getX(), atom.location.get().getY() );
        transform.rotate( angle );
        Shape shape = transform.createTransformedShape( normalShape );

        pathNode.setPathTo( shape );
    }
}
