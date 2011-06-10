// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import java.awt.image.BufferedImage;

import org.jbox2d.common.Vec2;

import edu.colorado.phet.chemistry.molecules.AtomNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.chemistry.model.Element.H;
import static edu.colorado.phet.chemistry.model.Element.O;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;

/**
 * Draws a single water molecule, including a circle for each of the O, H, H
 *
 * @author Sam Reid
 */
public class WaterMoleculeNode extends PNode {

    //Preload the images statically to save processor time during startup.  Use high resolution images here, and scale them down so they'll have good quality
    private static final BufferedImage OXYGEN_IMAGE = toBufferedImage( new AtomNode( 1000, O.getColor() ).toImage() );
    private static final BufferedImage HYDROGEN_IMAGE = toBufferedImage( new AtomNode( 1000, H.getColor() ).toImage() );

    public WaterMoleculeNode( final ModelViewTransform transform, final WaterMolecule waterMolecule, VoidFunction1<VoidFunction0> addListener ) {

        //Get the diameters in view coordinates
        double oxygenDiameter = transform.modelToViewDeltaX( waterMolecule.oxygenRadius * 2 );
        double hydrogenDiameter = transform.modelToViewDeltaX( waterMolecule.hydrogenRadius * 2 );

        //Create shapes for O, H, H
        //Use images from chemistry since they look shaded and good colors
        final PImage oxygen = new PImage( multiScaleToWidth( OXYGEN_IMAGE, (int) oxygenDiameter ) );

        //First hydrogen
        final PImage h1 = new PImage( multiScaleToWidth( HYDROGEN_IMAGE, (int) hydrogenDiameter ) );

        //Second hydrogen
        final PImage h2 = new PImage( multiScaleToWidth( HYDROGEN_IMAGE, (int) hydrogenDiameter ) );

        //Update the graphics for the updated model objects
        VoidFunction0 update = new VoidFunction0() {
            public void apply() {
                //Compute angle and position of the molecule
                ImmutableVector2D oxygenPosition = waterMolecule.oxygen.position.get();

                //Set the location of the oxygen atom
                ImmutableVector2D viewPosition = transform.modelToView( oxygenPosition );
                oxygen.setOffset( viewPosition.getX() - oxygen.getFullBounds().getWidth() / 2, viewPosition.getY() - oxygen.getFullBounds().getHeight() / 2 );

                //Set the location of the hydrogens
                ImmutableVector2D h1Position = transform.modelToView( waterMolecule.hydrogen1.position.get() );
                h1.setOffset( h1Position.getX() - h1.getFullBounds().getWidth() / 2, h1Position.getY() - h1.getFullBounds().getHeight() / 2 );

                ImmutableVector2D h2Position = transform.modelToView( waterMolecule.hydrogen2Position.get() );
                h2.setOffset( h2Position.getX() - h2.getFullBounds().getWidth() / 2, h2Position.getY() - h2.getFullBounds().getHeight() / 2 );
            }
        };
        addListener.apply( update );

        //Also update initially
        update.apply();

        //Add the children in staggered layers so it looks 3d
        //Z-Flip about half of them so they don't all look like 2d rotated versions of each other
        if ( SugarAndSaltSolutionsApplication.random.nextBoolean() ) {
            addChild( h1 );
            addChild( oxygen );
            addChild( h2 );
        }
        else {
            addChild( h2 );
            addChild( oxygen );
            addChild( h1 );
        }
    }

    public static ImmutableVector2D toImmutableVector2D( Vec2 from ) {
        return new ImmutableVector2D( from.x, from.y );
    }
}
