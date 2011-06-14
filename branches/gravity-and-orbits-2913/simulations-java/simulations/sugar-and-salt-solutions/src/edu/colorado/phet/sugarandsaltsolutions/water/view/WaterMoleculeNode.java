// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.chemistry.molecules.AtomNode;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemistry.model.Element.H;
import static edu.colorado.phet.chemistry.model.Element.O;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;
import static edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule.hydrogenRadius;
import static edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule.oxygenRadius;

/**
 * Draws a single water molecule, including a circle for each of the O, H, H
 *
 * @author Sam Reid
 */
public class WaterMoleculeNode extends PNode {

    //Preload the images statically to save processor time during startup.  Use high resolution images here, and scale them down so they'll have good quality
    private static final BufferedImage OXYGEN_IMAGE = toBufferedImage( new AtomNode( 1000, O.getColor() ).toImage() );
    private static final BufferedImage HYDROGEN_IMAGE = toBufferedImage( new AtomNode( 1000, H.getColor() ).toImage() );

    public WaterMoleculeNode( final ModelViewTransform transform, final WaterMolecule waterMolecule, final VoidFunction1<VoidFunction0> addListener ) {

        //Get the diameters in view coordinates
        double oxygenDiameter = transform.modelToViewDeltaX( oxygenRadius * 2 );
        double hydrogenDiameter = transform.modelToViewDeltaX( hydrogenRadius * 2 );

        //Create images for O, H, H
        final AtomImage oxygen = new AtomImage( OXYGEN_IMAGE, oxygenDiameter, waterMolecule.getOxygen(), addListener, transform );
        final AtomImage h1 = new AtomImage( HYDROGEN_IMAGE, hydrogenDiameter, waterMolecule.getHydrogen1(), addListener, transform );
        final AtomImage h2 = new AtomImage( HYDROGEN_IMAGE, hydrogenDiameter, waterMolecule.getHydrogen2(), addListener, transform );

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
}