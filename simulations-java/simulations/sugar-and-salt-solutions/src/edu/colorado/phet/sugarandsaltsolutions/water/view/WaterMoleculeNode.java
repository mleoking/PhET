// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.chemistry.molecules.AtomNode;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.water.model.Atom;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

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

    //Text symbol to show for the partial charge delta
    public static final char DELTA = '\u03B4';

    //The default "-" sign on Windows is too short, the team requested to use a longer symbol, so I switched to the unicode figure dash
    //As described on this page: http://www.fileformat.info/info/unicode/char/2012/index.htm
    //The unicode figure dash also has the benefit that it looks further away from the delta symbol
    public static final String MINUS = "\u2012";
    public static final String PLUS = "+";

    //Preload the images statically to save processor time during startup.  Use high resolution images here, and scale them down so they'll have good quality
    private static final BufferedImage OXYGEN_IMAGE = toBufferedImage( new AtomNode( 1000, O.getColor() ).toImage() );
    private static final BufferedImage HYDROGEN_IMAGE = toBufferedImage( new AtomNode( 1000, H.getColor() ).toImage() );

    public WaterMoleculeNode( final ModelViewTransform transform, final WaterMolecule waterMolecule, final VoidFunction1<VoidFunction0> addListener, final ObservableProperty<Boolean> showWaterCharge ) {

        //Get the diameters in view coordinates
        double oxygenDiameter = transform.modelToViewDeltaX( oxygenRadius * 2 );
        double hydrogenDiameter = transform.modelToViewDeltaX( hydrogenRadius * 2 );

        //Show the atom image as well as the partial charge, if the user has chosen to show it
        class AtomImageWithText extends AtomImage {
            AtomImageWithText( BufferedImage image, double diameter, Atom atom, String text ) {
                super( image, diameter, atom, addListener, transform );
                final PNode parent = this;
                addChild( new PText( text ) {{
                    showWaterCharge.addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean showPartialCharge ) {
                            setVisible( showPartialCharge );
                        }
                    } );
                    setFont( new PhetFont( 16 ) );
                    setOffset( parent.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2,
                               parent.getFullBounds().getHeight() / 2 - getFullBounds().getHeight() / 2 );
                }} );
            }
        }

        //Create images for O, H, H.
        final AtomImage oxygen = new AtomImageWithText( OXYGEN_IMAGE, oxygenDiameter, waterMolecule.getOxygen(), DELTA + MINUS );
        final AtomImage h1 = new AtomImageWithText( HYDROGEN_IMAGE, hydrogenDiameter, waterMolecule.getHydrogen1(), DELTA + PLUS );
        final AtomImage h2 = new AtomImageWithText( HYDROGEN_IMAGE, hydrogenDiameter, waterMolecule.getHydrogen2(), DELTA + PLUS );

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