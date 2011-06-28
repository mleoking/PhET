// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.view.IonGraphic;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.NegativeSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.PositiveSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.view.SucroseNode;

/**
 * Canvas for the "micro" tab of the sugar and salt solutions sim.  This shares lots of functionality with the first tab, so much of that code is reused.
 *
 * @author Sam Reid
 */
public class MicroCanvas extends SugarAndSaltSolutionsCanvas {
    private final boolean debug = false;

    //Enable the IonGraphicManager to create graphics for sucrose molecules
    static {
        IonGraphicManager.putImage( NegativeSugarIon.class, getSucroseImage() );
        IonGraphicManager.putImage( PositiveSugarIon.class, getSucroseImage() );
    }

    public MicroCanvas( final MicroModel model, GlobalState globalState ) {
        super( model, globalState );

        //Add graphics for each ion.  Overriden here to map from soluble salts model coordinates -> sugar and salt model coordinates -> sugar and salt canvas coordinates
        model.addIonListener( new IonGraphicManager( getRootNode() ) {
            @Override protected IonGraphic createImage( Ion ion, BufferedImage image ) {
                return new IonGraphic( ion, image ) {
                    @Override protected void updateOffset( Point2D ionPosition ) {
                        //Map the soluble salts model coordinates through sugar and salt solution model coordinates to canvas coordinates
                        Point2D sugarModelPosition = model.solubleSaltsTransform.modelToView( ionPosition );
                        Point2D viewLocation = new Point2D.Double( transform.modelToView( sugarModelPosition ).getX() - pImage.getWidth() / 2,
                                                                   transform.modelToView( sugarModelPosition ).getY() - pImage.getHeight() / 2 );
                        setOffset( viewLocation );
                    }
                };
            }
        } );

        //Show a debug path for the fluid volume, to help make sure the coordinate frames are correct
        if ( debug ) {
            addChild( new PhetPPath( new Color( 255, 0, 0, 128 ) ) {{
                final SimpleObserver updatePath = new SimpleObserver() {
                    public void update() {
                        setPathTo( transform.modelToView( model.solubleSaltsTransform.modelToView( model.getSolubleSaltsModel().getVessel().getWater().getBounds() ) ) );
                    }
                };
                //Update the path continuously in case the mode changed it for any reason that we couldn't observe
                new javax.swing.Timer( 100, new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        updatePath.update();
                    }
                } ).start();
                model.waterVolume.addObserver( updatePath );
            }} );
        }
    }

    //Create an image for sucrose using the same code as in the water tab to keep representations consistent
    private static BufferedImage getSucroseImage() {
        //Create a transform that will make the constituent particles big enough since they are rasterized.  I obtained the values by running the Water tab and printing out the box2d transform used in SucroseNode
        final ModelViewTransform transform = ModelViewTransform.createSinglePointScaleMapping( new Point2D.Double(), new Point2D.Double(), 3150 / 6.3E-7 * 400 );

        //Create the graphic
        final SucroseNode sucroseNode = new SucroseNode( transform, new WaterModel().newSugar( 0, 0 ), new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 voidFunction0 ) {
                voidFunction0.apply();
            }
        }, Element.O.getColor(), Element.H.getColor(), Color.gray ) {{
//        }, Color.yellow, Color.yellow, Color.yellow ) {{

            //Scale the graphic so it will be a good size for putting into a crystal lattice, with sizes
            //Just using RADIUS * 2 leaves too much space between particles in the lattice
            double width = getFullBounds().getWidth();
            scale( SugarIon.RADIUS * 3 / width );

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