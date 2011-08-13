// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomsMolecule;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * 2D isosurface for a diatomic molecule (composed of 2 atoms).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsIsosurfaceNode extends PComposite {

    private static final double DIAMETER_SCALE = 2; // multiply atom diameters by this scale when computing surface size

    private final TwoAtomsMolecule molecule;
    private final PPath pathNode;

    public TwoAtomsIsosurfaceNode( final TwoAtomsMolecule molecule ) {

        this.molecule = molecule;

        this.pathNode = new PPath() {{
            setStroke( null );
            setPaint( new Color( 100, 100, 100, 100 ) );
        }};
        addChild( pathNode );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        molecule.atomA.location.addObserver( observer );
        molecule.atomB.location.addObserver( observer );

        addInputEventListener( new CursorHandler() ); //TODO change cursor to indicate rotation
        addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
    }

    private void updateNode() {
        //TODO using 2 circles doesn't really cut it, need to smooth out the places where the circles overlap.
        Ellipse2D circleA = getCircle( molecule.atomA );
        Ellipse2D circleB = getCircle( molecule.atomB );
        Area area = new Area();
        area.add( new Area( circleA ) );
        area.add( new Area( circleB ) );
        pathNode.setPathTo( area );
    }

    private Ellipse2D getCircle( Atom atom ) {
        final double diameter = DIAMETER_SCALE * atom.getDiameter();
        double x = atom.location.get().getX() - ( diameter / 2 );
        double y = atom.location.get().getY() - ( diameter / 2 );
        return new Ellipse2D.Double( x, y, diameter, diameter );
    }
}
