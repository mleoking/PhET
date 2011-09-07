// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeRotationHandler;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * 2D isosurface that represents electron density for a diatomic molecule.
 * Electron density uses a 2-color gradient, so we can use a single PPath.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicElectronDensityNode extends PComposite {

    private static final double DIAMETER_SCALE = 2; // multiply atom diameters by this scale when computing surface size
    private static final int ALPHA = 100; // the alpha channel, for transparency

    private final DiatomicMolecule molecule;
    private final DoubleRange electronegativityRange;
    private final Color[] colors;
    private final PPath pathNode;

    /**
     * Constructor
     *
     * @param molecule
     * @param colors   color scheme for the surface, ordered from more to less density
     */
    public DiatomicElectronDensityNode( final DiatomicMolecule molecule, DoubleRange electronegativityRange, Color[] colors ) {
        assert ( colors.length == 2 ); // this implementation only works for 2 colors

        this.molecule = molecule;
        this.electronegativityRange = electronegativityRange;
        this.colors = colors;

        this.pathNode = new PPath() {{
            setStroke( null );
        }};
        addChild( pathNode );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        for ( Atom atom : molecule.getAtoms() ) {
            atom.location.addObserver( observer );
            atom.electronegativity.addObserver( observer );
        }

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
    }

    private void updateNode() {

        //TODO using circles doesn't really cut it, need to smooth out the places where the circles overlap.
        pathNode.setPathTo( ShapeUtils.add( getCircle( molecule.atomA ), getCircle( molecule.atomB ) ) );

        //TODO create a GradientPaint based on EN
        pathNode.setPaint( new Color( 100, 100, 100, ALPHA ) );
    }

    private Ellipse2D getCircle( Atom atom ) {
        final double diameter = DIAMETER_SCALE * atom.getDiameter();
        double x = atom.location.get().getX() - ( diameter / 2 );
        double y = atom.location.get().getY() - ( diameter / 2 );
        return new Ellipse2D.Double( x, y, diameter, diameter );
    }
}
