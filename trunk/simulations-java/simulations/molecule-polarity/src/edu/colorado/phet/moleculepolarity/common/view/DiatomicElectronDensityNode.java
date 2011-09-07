// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
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

    private static final double DIAMETER_SCALE = 2.5; // multiply atom diameters by this scale when computing surface size
    private static final int ALPHA = 200; // the alpha channel, for transparency

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
        updateShape();
        updatePaint();
    }

    // Updates the shape of the isosurface.
    private void updateShape() {
        //TODO circles provide a rough approximation, need to smooth out the places where the circles overlap.
        pathNode.setPathTo( ShapeUtils.add( getCircle( molecule.atomA ), getCircle( molecule.atomB ) ) );
    }

    // Updates the gradient used to paint the isosurface. Width of the gradient expands as the difference in EN approaches zero.
    private void updatePaint() {

        // scale varies from 1 to 0, approaches zero as EN difference approaches zero.
        final double deltaEN = molecule.atomB.electronegativity.get() - molecule.atomA.electronegativity.get();
        final double scale = Math.abs( deltaEN / electronegativityRange.getLength() );

        // width of the surface
        final double distance = molecule.atomB.location.get().getDistance( molecule.atomB.location.get() );
        final double minX = -( distance / 2 ) - ( DIAMETER_SCALE * molecule.atomA.getDiameter() ) / 2;
        final double maxX = ( distance / 2 ) + ( DIAMETER_SCALE * molecule.atomB.getDiameter() ) / 2;
        final double surfaceWidth = maxX - minX;

        // compute the gradient width
        final double minGradientWidth = surfaceWidth;
        final double maxGradientWidth = 5 * surfaceWidth;
        LinearFunction f = new LinearFunction( 1, 0, minGradientWidth, maxGradientWidth );
        final double gradientWidth = f.evaluate( scale );
        final double deltaX = ( gradientWidth - surfaceWidth ) / 2;

        // gradient endpoints prior to accounting for molecule transform
        Point2D pointA = new Point2D.Double( minX - deltaX, 0 );
        Point2D pointB = new Point2D.Double( maxX + deltaX, 0 );

        // transform gradient endpoints to account for molecule transform
        AffineTransform transform = new AffineTransform();
        transform.translate( molecule.getLocation().getX(), molecule.getLocation().getY() );
        transform.rotate( molecule.getAngle() );
        transform.transform( pointA, pointA );
        transform.transform( pointB, pointB );

        // choose colors based on polarity
        Color colorA = ( deltaEN > 0 ) ? colors[1] : colors[0];
        Color colorB = ( deltaEN > 0 ) ? colors[0] : colors[1];

        // create the gradient
        GradientPaint paint = new GradientPaint( (float) pointA.getX(), (float) pointA.getY(), ColorUtils.createColor( colorA, ALPHA ),
                                                 (float) pointB.getX(), (float) pointB.getY(), ColorUtils.createColor( colorB, ALPHA ) );
        pathNode.setPaint( paint );
    }

    private Ellipse2D getCircle( Atom atom ) {
        final double diameter = DIAMETER_SCALE * atom.getDiameter();
        double x = atom.location.get().getX() - ( diameter / 2 );
        double y = atom.location.get().getY() - ( diameter / 2 );
        return new Ellipse2D.Double( x, y, diameter, diameter );
    }
}
