// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * 2D surface that represents electron density for a diatomic molecule.
 * Electron density uses a 2-color gradient, so we can use a single PPath.
 * This node's look is similar to the corresponding Jmol isosurface.
 * Shapes are created in world coordinates, so this node's offset should be (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicElectronDensityNode extends SurfaceNode {

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
     * @param electronegativityRange
     * @param colors                 color scheme for the surface, ordered from more to less density
     */
    public DiatomicElectronDensityNode( final DiatomicMolecule molecule, DoubleRange electronegativityRange, Color[] colors ) {
        super( molecule );

        assert ( colors.length == 2 ); // this implementation only works for 2 colors
        assert ( molecule.atomA.getDiameter() == molecule.atomB.getDiameter() ); // creation of gradient assumes that both atoms have the same diameter

        this.molecule = molecule;
        this.electronegativityRange = electronegativityRange;
        this.colors = colors;

        this.pathNode = new PPath() {{
            setStroke( null );
        }};
        addChild( pathNode );

        updateNode();
    }

    protected void updateNode() {
        updateShape();
        updatePaint();
    }

    // Updates the shape of the surface, 2 circles combined using constructive area geometry.
    private void updateShape() {
        pathNode.setPathTo( ShapeUtils.add( createCloudShape( molecule.atomA, DIAMETER_SCALE ), createCloudShape( molecule.atomB, DIAMETER_SCALE ) ) );
    }

    // Updates the Paint used to color the surface. Width of the gradient expands as the difference in EN approaches zero.
    private void updatePaint() {

        // scale varies from 1 to 0, approaches zero as EN difference approaches zero.
        final double deltaEN = molecule.getDeltaEN();
        if ( deltaEN == 0 ) {
            // no difference, use neutral color that's halfway between "more" and "less" colors
            pathNode.setPaint( MPColors.NEUTRAL_GRAY );
        }
        else {
            final double scale = Math.abs( deltaEN / electronegativityRange.getLength() );

            // width of the surface
            final double surfaceWidth = molecule.bond.getLength() + ( DIAMETER_SCALE * molecule.atomA.getDiameter() / 2 ) + ( DIAMETER_SCALE * molecule.atomB.getDiameter() / 2 );

            // compute the gradient width
            LinearFunction f = new LinearFunction( 1, 0, surfaceWidth, surfaceWidth * GRADIENT_WIDTH_MULTIPLIER );
            final double gradientWidth = f.evaluate( scale );

            // gradient endpoints prior to accounting for molecule transform
            Point2D pointA = new Point2D.Double( -gradientWidth / 2, 0 );
            Point2D pointB = new Point2D.Double( gradientWidth / 2, 0 );

            // transform gradient endpoints to account for molecule transform
            AffineTransform transform = createTransform( molecule );
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
}
