// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.solublesalts.SolubleSaltsApplication.SolubleSaltsClock;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig.Calibration;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.module.ISolubleSaltsModelContainer;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule.ResetListener;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.NegativeSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.PositiveSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.view.SucroseNode;

/**
 * Micro tab that shows the NaCl ions and Sucrose molecules.
 * <p/>
 * In order to efficiently re-use pre existing code from the Soluble Salts (AKA Salts and Solubility) project, we make the following inaccurate encodings:
 * 1. Sugar is a subclass of Salt
 * 2. Sugar has two constituents, a "positive" sugar molecule and a "negative" sugar molecule
 *
 * @author Sam Reid
 */
public class MicroModule extends Module implements ISolubleSaltsModelContainer {

    private MicroModel model;

    static {
        IonGraphicManager.putImage( PositiveSugarIon.class, getSucroseImage() );
        IonGraphicManager.putImage( NegativeSugarIon.class, getSucroseImage() );
    }

    @Override public void reset() {
        super.reset();
        model.reset();
    }

    private final Calibration calibration = new SolubleSaltsConfig.Calibration( 1.7342E-25,
                                                                                5E-23,
                                                                                1E-23,
                                                                                0.5E-23 );

    public MicroModule( SugarAndSaltSolutionsColorScheme configuration ) {
        super( "Micro", new SolubleSaltsClock() );

        model = new MicroModel( new SolubleSaltsModel( getClock(), this ), getCalibration() );

        //Don't use the entire south panel for the clock controls
        setClockControlPanel( null );

        setSimulationPanel( new MicroCanvas( model, configuration ) );
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

    public Calibration getCalibration() {
        return calibration;
    }

    public void addResetListener( ResetListener resetListener ) {
    }

    public double getMinimumFluidVolume() {
        return MicroModel.MIN_FLUID_VOLUME;
    }

    //Sample main writes a sucrose image to file for inspection
    public static void main( String[] args ) throws IOException {
        ImageIO.write( getSucroseImage(), "PNG", new File( args[0], System.currentTimeMillis() + ".PNG" ) );
    }
}