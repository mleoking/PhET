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
    private static final int ALPHA = 185; // the alpha channel, for transparency
    private static final int GRADIENT_WIDTH_MULTIPLIER = 5; // smaller values result in a more noticeable change as the EN sliders are dragged

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
        assert ( molecule.atomA.getDiameter() == molecule.atomB.getDiameter() ); // creation of gradient assumes that both atoms have the same diameter

        this.molecule = molecule;
        this.electronegativityRange = electronegativityRange;
        this.colors = colors;

        this.pathNode = new PPath() {{
            setStroke( null );
        }};
        addChild( pathNode );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                if ( getVisible() ) {
                    updateNode();
                }
            }
        };
        for ( Atom atom : molecule.getAtoms() ) {
            atom.location.addObserver( observer );
            atom.electronegativity.addObserver( observer );
        }

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
    }

    @Override public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            updateNode();
        }
    }

    private void updateNode() {
        updateShape();
        updatePaint();
    }

    // Updates the shape of the isosurface, 2 circles combined using constructive area geometry.
    private void updateShape() {
        pathNode.setPathTo( ShapeUtils.add( getCircle( molecule.atomA ), getCircle( molecule.atomB ) ) );
    }

    // Updates the Paint used to color the isosurface. Width of the gradient expands as the difference in EN approaches zero.
    private void updatePaint() {

        // scale varies from 1 to 0, approaches zero as EN difference approaches zero.
        final double deltaEN = molecule.atomB.electronegativity.get() - molecule.atomA.electronegativity.get();
        if ( deltaEN == 0 ) {
            // no difference, use neutral color that's halfway between "more" and "less" colors
            pathNode.setPaint( ColorUtils.interpolateRBGA( ColorUtils.createColor( colors[0], ALPHA ), ColorUtils.createColor( colors[1], ALPHA ), 0.5 ) );
        }
        else {
            final double scale = Math.abs( deltaEN / electronegativityRange.getLength() );

            // width of the isosurface
            final double surfaceWidth = molecule.bond.getLength() + ( DIAMETER_SCALE * molecule.atomA.getDiameter() / 2 ) + ( DIAMETER_SCALE * molecule.atomB.getDiameter() / 2 );

            // compute the gradient width
            final double minGradientWidth = surfaceWidth;
            final double maxGradientWidth = surfaceWidth * GRADIENT_WIDTH_MULTIPLIER;
            LinearFunction f = new LinearFunction( 1, 0, minGradientWidth, maxGradientWidth );
            final double gradientWidth = f.evaluate( scale );

            // gradient endpoints prior to accounting for molecule transform
            final double xOffset = gradientWidth / 2;
            Point2D pointA = new Point2D.Double( -xOffset, 0 );
            Point2D pointB = new Point2D.Double( xOffset, 0 );

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
    }

    private Ellipse2D getCircle( Atom atom ) {
        final double diameter = DIAMETER_SCALE * atom.getDiameter();
        double x = atom.location.get().getX() - ( diameter / 2 );
        double y = atom.location.get().getY() - ( diameter / 2 );
        return new Ellipse2D.Double( x, y, diameter, diameter );
    }
}
