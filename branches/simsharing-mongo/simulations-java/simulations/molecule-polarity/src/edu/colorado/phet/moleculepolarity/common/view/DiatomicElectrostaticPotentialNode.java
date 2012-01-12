// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * 2D surface that represents electrostatic potential for a diatomic molecule.
 * Electron density uses a 3-color gradient, so we use 2 PPath nodes that meet in the middle.
 * This node's look is similar to the corresponding Jmol isosurface.
 * Shapes are created in world coordinates, so this node's offset should be (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicElectrostaticPotentialNode extends SurfaceNode {

    private static final double DIAMETER_SCALE = 2.5; // multiply atom diameters by this scale when computing surface size
    private static final int ALPHA = 185; // the alpha channel, for transparency
    private static final int GRADIENT_WIDTH_MULTIPLIER = 5; // smaller values result in a more noticeable change as the EN sliders are dragged

    private final DiatomicMolecule molecule;
    private final DoubleRange electronegativityRange;
    private final Color[] colors;
    private final PPath pathNodeA, pathNodeB;

    /**
     * Constructor
     *
     * @param molecule
     * @param electronegativityRange
     * @param colors                 color scheme for the surface, ordered from negative to positive
     */
    public DiatomicElectrostaticPotentialNode( final DiatomicMolecule molecule, DoubleRange electronegativityRange, Color[] colors ) {
        super( molecule );

        assert ( colors.length == 3 ); // this implementation only works for 3 colors
        assert ( molecule.atomA.getDiameter() == molecule.atomB.getDiameter() ); // creation of gradient assumes that both atoms have the same diameter

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

        updateNode();
    }

    protected void updateNode() {
        updateShape();
        updatePaint();
    }

    // Updates the shape of the surface.
    private void updateShape() {

        // surround each atom with a cloud
        Shape cloudA = createCloudShape( molecule.atomA, DIAMETER_SCALE );
        Shape cloudB = createCloudShape( molecule.atomB, DIAMETER_SCALE );

        // rectangles for clipping where the clouds join at the center of the bond, with overlap so we don't see seam
        final double bondLength = molecule.bond.getLength();
        Shape clipA = new Rectangle2D.Double( 0.25, DIAMETER_SCALE * -molecule.atomA.getDiameter() / 2, DIAMETER_SCALE * bondLength / 2, DIAMETER_SCALE * molecule.atomA.getDiameter() );
        Shape clipB = new Rectangle2D.Double( DIAMETER_SCALE * -bondLength / 2, DIAMETER_SCALE * -molecule.atomA.getDiameter() / 2, DIAMETER_SCALE * bondLength / 2, DIAMETER_SCALE * molecule.atomA.getDiameter() );
        AffineTransform transform = createTransform( molecule );
        clipA = transform.createTransformedShape( clipA );
        clipB = transform.createTransformedShape( clipB );

        pathNodeA.setPathTo( ShapeUtils.subtract( cloudA, clipA ) );
        pathNodeB.setPathTo( ShapeUtils.subtract( cloudB, clipB ) );
    }

    // Updates the Paints uses to color the surface. Width of the gradients expands as the difference in EN approaches zero.
    private void updatePaint() {
        final double deltaEN = molecule.getDeltaEN();
        if ( deltaEN == 0 ) {
            Color neutralColor = ColorUtils.createColor( colors[1], ALPHA );
            pathNodeA.setPaint( neutralColor );
            pathNodeB.setPaint( neutralColor );
        }
        else {
            final double scale = Math.abs( deltaEN / electronegativityRange.getLength() );

            // width of the surface
            final double surfaceWidth = molecule.bond.getLength() + ( DIAMETER_SCALE * molecule.atomA.getDiameter() / 2 ) + ( DIAMETER_SCALE * molecule.atomB.getDiameter() / 2 );

            // compute the gradient width
            final double minGradientWidth = surfaceWidth / 2;
            final double maxGradientWidth = minGradientWidth * GRADIENT_WIDTH_MULTIPLIER;
            LinearFunction f = new LinearFunction( 1, 0, minGradientWidth, maxGradientWidth );
            final double gradientWidth = f.evaluate( scale );

            // gradient endpoints prior to accounting for molecule transform
            Point2D pointCenter = new Point2D.Double( 0, 0 );
            Point2D pointA = new Point2D.Double( -gradientWidth / 2, 0 );
            Point2D pointB = new Point2D.Double( gradientWidth / 2, 0 );

            // transform gradient endpoints to account for molecule transform
            AffineTransform transform = createTransform( molecule );
            transform.transform( pointCenter, pointCenter );
            transform.transform( pointA, pointA );
            transform.transform( pointB, pointB );

            // choose colors based on polarity
            Color colorCenter = colors[1];
            Color colorA = ( deltaEN > 0 ) ? colors[2] : colors[0];
            Color colorB = ( deltaEN > 0 ) ? colors[0] : colors[2];

            // create the gradient from center to atom A
            GradientPaint paintA = new GradientPaint( (float) pointA.getX(), (float) pointA.getY(), ColorUtils.createColor( colorA, ALPHA ),
                                                      (float) pointCenter.getX(), (float) pointCenter.getY(), ColorUtils.createColor( colorCenter, ALPHA ) );
            pathNodeA.setPaint( paintA );

            // create the gradient from center to atom B
            GradientPaint paintB = new GradientPaint( (float) pointCenter.getX(), (float) pointCenter.getY(), ColorUtils.createColor( colorCenter, ALPHA ),
                                                      (float) pointB.getX(), (float) pointB.getY(), ColorUtils.createColor( colorB, ALPHA ) );
            pathNodeB.setPaint( paintB );
        }
    }
}
