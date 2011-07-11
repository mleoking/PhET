package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.view.SucroseNode;

/**
 * Utility class for generating images to show for sucrose molecules in the Micro tab
 *
 * @author Sam Reid
 */
public class SucroseImage {
    //Create an image for sucrose using the same code as in the water tab to keep representations consistent
    public static BufferedImage getSucroseImage() {
        //Create a transform that will make the constituent particles big enough since they are rasterized.  I obtained the values by running the Water tab and printing out the box2d transform used in SucroseNode
        final ModelViewTransform transform = ModelViewTransform.createSinglePointScaleMapping( new Point2D.Double(), new Point2D.Double(), 3150 / 6.3E-7 * 400 );

        //Create the graphic
        final SucroseNode sucroseNode = new SucroseNode( transform,

                                                         // TODO: Creating a new WaterModel to make this happen is too time consuming, make it so you can create sucrose and/or sucrosenode without it
                                                         new WaterModel() {
                                                             @Override protected void initModel() {
                                                                 //Skip creating the water to speed up creation
                                                             }
                                                         }.newSugar( 0, 0 ), new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 voidFunction0 ) {
                voidFunction0.apply();
            }
        }, Element.O.getColor(), Element.H.getColor(), Color.gray ) {{
//        }, Color.yellow, Color.yellow, Color.yellow ) {{

            //Put it a random angle
            rotate( Math.random() * Math.PI * 2 );
        }};
        return (BufferedImage) sucroseNode.toImage();
    }

    //Sample main writes a sucrose image to file for inspection
    public static void main( String[] args ) throws IOException {
        ImageIO.write( getSucroseImage(), "PNG", new File( args[0], System.currentTimeMillis() + ".PNG" ) );
    }
}