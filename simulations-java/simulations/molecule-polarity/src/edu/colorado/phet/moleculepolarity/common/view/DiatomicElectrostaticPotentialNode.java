// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeRotationHandler;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * 2D isosurface that represents electrostatic potential for a diatomic molecule.
 * Electron density uses a 3-color gradient, so we use 2 PPath nodes that meet in the middle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicElectrostaticPotentialNode extends PComposite {

    private static final double DIAMETER_SCALE = 2.5; // multiply atom diameters by this scale when computing surface size
    private static final int ALPHA = 100; // the alpha channel, for transparency

    private final DiatomicMolecule molecule;
    private final DoubleRange electronegativityRange;
    private final Color[] colors;
    private final PPath pathNodeA, pathNodeB;

    /**
     * Constructor
     *
     * @param molecule
     * @param colors   color scheme for the surface, ordered from negative to positive
     */
    public DiatomicElectrostaticPotentialNode( final DiatomicMolecule molecule, DoubleRange electronegativityRange, Color[] colors ) {
        assert ( colors.length == 3 ); // this implementation only works for 3 colors

        this.molecule = molecule;
        this.electronegativityRange = electronegativityRange;
        this.colors = colors;

        this.pathNodeA = new PPath() {{
            setStroke( null );
        }};
        addChild( pathNodeA );

        this.pathNodeB = new PPath() {{
            setStroke( null );

        }};
        addChild( pathNodeB );

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
        updateShape();
        updatePaint();
    }

    // Updates the shape of the isosurface.
    private void updateShape() {
        //TODO using circles doesn't really cut it, need to smooth out the places where the circles overlap.
        pathNodeA.setPathTo( getCircle( molecule.atomA ) );
        pathNodeB.setPathTo( getCircle( molecule.atomB ) );
    }

    // Updates the Paints uses to color the isosurface. Width of the gradients expands as the difference in EN approaches zero.
    private void updatePaint() {
        final double deltaEN = molecule.atomB.electronegativity.get() - molecule.atomA.electronegativity.get();
        if ( deltaEN == 0 ) {
            Color neutralColor = ColorUtils.createColor( colors[1], ALPHA );
            pathNodeA.setPaint( neutralColor );
            pathNodeB.setPaint( neutralColor );
        }
        else {
            //TODO create GradientPaints based on EN
            // choose colors based on polarity
            Color colorA = ( deltaEN > 0 ) ? colors[2] : colors[0];
            Color colorB = ( deltaEN > 0 ) ? colors[0] : colors[2];
            pathNodeA.setPaint( ColorUtils.createColor( colorA, ALPHA ) );
            pathNodeB.setPaint( ColorUtils.createColor( colorB, ALPHA ) );
        }
    }

    private Ellipse2D getCircle( Atom atom ) {
        final double diameter = DIAMETER_SCALE * atom.getDiameter();
        double x = atom.location.get().getX() - ( diameter / 2 );
        double y = atom.location.get().getY() - ( diameter / 2 );
        return new Ellipse2D.Double( x, y, diameter, diameter );
    }
}
