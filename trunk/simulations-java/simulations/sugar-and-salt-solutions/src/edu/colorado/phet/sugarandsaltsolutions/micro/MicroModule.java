// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.solublesalts.SolubleSaltsApplication.SolubleSaltsClock;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.view.EvaporationSlider;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.ExpandableConcentrationBarChartNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.RemoveSoluteControlNode;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.NegativeSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.PositiveSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.view.SucroseNode;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.BUTTON_COLOR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.INSET;

/**
 * Micro tab that shows the NaCl ions and Sucrose molecules.
 * <p/>
 * In order to efficiently re-use pre existing code from the Soluble Salts (AKA Salts and Solubility) project, we make the following inaccurate encodings:
 * 1. Sugar is a subclass of Salt
 * 2. Sugar has two constituents, a "positive" sugar molecule and a "negative" sugar molecule
 *
 * @author Sam Reid
 */
public class MicroModule extends SolubleSaltsModule {

    private MicroModel model;
    private final double CONTROL_SCALE_FACTOR = 1.2;

    static {
        IonGraphicManager.putImage( PositiveSugarIon.class, getSucroseImage() );
        IonGraphicManager.putImage( NegativeSugarIon.class, getSucroseImage() );
    }

    @Override public void reset() {
        super.reset();
        model.reset();
    }

    public MicroModule( SugarAndSaltSolutionsColorScheme configuration ) {
        super( "Micro",
               new SolubleSaltsClock(),
               new SolubleSaltsConfig.Calibration( 1.7342E-25,
                                                   5E-23,
                                                   1E-23,
                                                   0.5E-23 ) );

        //Don't use the entire south panel for the clock controls
        setClockControlPanel( null );

        //Shrink the play area so the controls fit, became necessary when we removed the south/clock control panel
        getFullScaleCanvasNode().scale( 0.95 );

        model = new MicroModel( getSolubleSaltsModel(), getCalibration() );
        final Dimension2DDouble stageSize = new Dimension2DDouble( 1300, 800 );

        //Show the expandable/collapsable concentration bar chart in the top right
        final ExpandableConcentrationBarChartNode barChartNode = new ExpandableConcentrationBarChartNode( model.showConcentrationBarChart, model.saltConcentration, model.sugarConcentration, model.showConcentrationValues, 1 ) {{
            scale( CONTROL_SCALE_FACTOR );
            setOffset( stageSize.width - getFullBounds().getWidth(), MacroCanvas.INSET );
        }};
        getFullScaleCanvasNode().addChild( barChartNode );

        //Show a control that lets the user choose different solutes (salt/sugar) just below the bar chart
        getFullScaleCanvasNode().addChild( new SoluteControlPanelNode( model.dispenserType ) {{
            scale( CONTROL_SCALE_FACTOR );
            setOffset( barChartNode.getFullBounds().getX() - getFullBounds().getWidth() - MacroCanvas.INSET, barChartNode.getFullBounds().getY() );
        }} );

        //Add the reset all button
        getFullScaleCanvasNode().addChild( new HTMLImageButtonNode( "Reset All", BUTTON_COLOR ) {{
            setFont( SugarAndSaltSolutionsCanvas.CONTROL_FONT );
            scale( CONTROL_SCALE_FACTOR );
            //Have to set the offset after changing the font since it changes the size of the node
            setOffset( stageSize.width - getFullBounds().getWidth() - INSET, stageSize.height - getFullBounds().getHeight() - INSET );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reset();
                }
            } );
        }} );

        getFullScaleCanvasNode().addChild( new RemoveSoluteControlNode( model ) {{
            scale( CONTROL_SCALE_FACTOR );
            PBounds vesselBounds = getFullScaleCanvasNode().getVesselGraphic().getFullBounds();
            setOffset( vesselBounds.getX() + INSET * 4, vesselBounds.getMaxY() + INSET );
        }} );

        //Add an evaporation rate slider below the beaker
        getFullScaleCanvasNode().addChild( new EvaporationSlider( model.evaporationRate ) {{
            scale( CONTROL_SCALE_FACTOR );
            PBounds vesselBounds = getFullScaleCanvasNode().getVesselGraphic().getFullBounds();
            setOffset( vesselBounds.getCenterX() - 70, vesselBounds.getMaxY() + INSET );
        }} );

        //Set the background to match the other tabs
        getPhetPCanvas().setBackground( configuration.backgroundColor.get() );
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

    //Make the HTML tick mark labels white so they will be visible on a dark background
    @Override public void updateHTMLNode( HTMLNode text ) {
        super.updateHTMLNode( text );
        text.setHTMLColor( Color.white );
        text.setFont( new PhetFont( 15, true ) );
    }

    @Override public double getMinimumFluidVolume() {
        return MicroModel.MIN_FLUID_VOLUME;
    }
}