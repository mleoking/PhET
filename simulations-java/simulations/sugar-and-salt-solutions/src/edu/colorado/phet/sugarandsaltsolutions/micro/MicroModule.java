// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.solublesalts.SolubleSaltsApplication.SolubleSaltsClock;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.ISugarMolecule;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.OneToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.*;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.ExpandableConcentrationBarChartNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;
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
public class MicroModule extends SolubleSaltsModule {

    //Model for the concentration in SI (moles/m^3)
    private final Property<Double> sugarConcentration = new Property<Double>( 0.0 );
    private final Property<Double> saltConcentration = new Property<Double>( 0.0 );

    // Use NaCl by default
    private final Property<DispenserType> dispenserType = new Property<DispenserType>( DispenserType.SALT );

    static {
        IonGraphicManager.putImage( SugarMoleculePlus.class, getSucroseImage() );
        IonGraphicManager.putImage( SugarMoleculeMinus.class, getSucroseImage() );
    }

    public MicroModule( SugarAndSaltSolutionsColorScheme configuration ) {
        super( "Micro",
               new SolubleSaltsClock(),
               new SolubleSaltsConfig.Calibration( 1.7342E-25,
                                                   5E-23,
                                                   1E-23,
                                                   0.5E-23 ) );

        //When the user selects a different solute, update the dispenser type
        dispenserType.addObserver( new SimpleObserver() {
            public void update() {
                if ( dispenserType.get() == DispenserType.SALT ) {
                    ( (SolubleSaltsModel) getModel() ).setCurrentSalt( new SodiumChloride() );
                }
                else {
                    ( (SolubleSaltsModel) getModel() ).setCurrentSalt( new SugarCrystal() );
                }
                updateShakerAllowed();
            }
        } );

        getSolubleSaltsModel().addIonListener( new IonListener() {
            public void ionAdded( IonEvent event ) {
                ionCountChanged();
            }

            public void ionRemoved( IonEvent event ) {
                ionCountChanged();
            }
        } );

        //Show the expandable/collapsable concentration bar chart in the top right
        getFullScaleCanvasNode().addChild( new ExpandableConcentrationBarChartNode( new Property<Boolean>( true ), saltConcentration, sugarConcentration, new Property<Boolean>( true ), 1 ) {{
            scale( 1.5 );
            setOffset( 1400 - getFullBounds().getWidth(), MacroCanvas.INSET );
        }} );

        //Show a control that lets the user choose different solutes (salt/sugar) just below the bar chart
        getFullScaleCanvasNode().addChild( new SoluteControlPanelNode( dispenserType ) {{
            scale( 1.5 );
            setOffset( 1400 - getFullBounds().getWidth(), 768 / 2 );
        }} );

        //Set the background to match the other tabs
        getPhetPCanvas().setBackground( configuration.backgroundColor.get() );
    }

    //Update concentrations and whether the shaker can emit more solutes
    private void ionCountChanged() {
        updateShakerAllowed();

        //according to VesselGraphic, the way to get the volume in liters is by multiplying the water height by the volumeCalibraitonFactor:
        double volumeInLiters = getSolubleSaltsModel().getVessel().getWaterLevel() * getCalibration().volumeCalibrationFactor;

        final double molesSugarPerLiter = getNumSugarMolecules() / 6.022E23 / volumeInLiters;

        //Set sugar concentration in SI (moles per m^3)
        sugarConcentration.set( molesSugarPerLiter * 1000 );//TODO: this looks wrong
//        System.out.println( "s = " + s + ", volume = " + volumeInLiters + ", molesSugarPerLiter = " + molesSugarPerLiter );

        final double molesSaltPerLiter = getNumSaltMolecules() / 6.022E23 / volumeInLiters;
        saltConcentration.set( molesSaltPerLiter * 1000 );//TODO: this also looks the same wrong
    }

    //Change whether the shaker can emit more solutes.  limit the amount of solute you can add - lets try 60 particles of salt (so 60 Na+ and 60 Cl- ions) and 10 particles of sugar
    private void updateShakerAllowed() {
        getSolubleSaltsModel().getShaker().setEnabledBasedOnMax( dispenserType.get() == DispenserType.SALT ?
                                                                 getNumSaltMolecules() < 60 :
                                                                 getNumSugarMolecules() < 10 );
    }

    private int getNumSugarMolecules() {
        return getSolubleSaltsModel().getIonsOfType( SugarMoleculePlus.class ).size() + getSolubleSaltsModel().getIonsOfType( SugarMoleculeMinus.class ).size();
    }

    private int getNumSaltMolecules() {
        return getSolubleSaltsModel().getIonsOfType( Sodium.class ).size();
    }

    //Model sugar as a kind of salt with positive and negative sucrose molecules (that look identical)
    public static class SugarCrystal extends Salt implements ISugarMolecule {
        static private Lattice lattice = new OneToOneLattice( SugarMoleculePlus.RADIUS + SugarMoleculeMinus.RADIUS );
        static private ArrayList<Component> components = new ArrayList<Component>();

        static {
            components.add( new Component( SugarMoleculePlus.class, 1 ) );
            components.add( new Component( SugarMoleculeMinus.class, 1 ) );
        }

        public SugarCrystal() {
            super( components, lattice, SugarMoleculePlus.class, SugarMoleculeMinus.class, 36 );
        }
    }

    //has to be public since loaded with reflection
    public static class SugarMoleculePlus extends Ion {
        public static final double RADIUS = 14;
        private static IonProperties ionProperties = new IonProperties( 80, 1, RADIUS );

        public SugarMoleculePlus() {
            super( ionProperties );
        }
    }

    //has to be public since loaded with reflection
    public static class SugarMoleculeMinus extends Ion {
        public static final double RADIUS = SugarMoleculePlus.RADIUS;
        private static IonProperties ionProperties = new IonProperties( 80, -1, RADIUS );

        public SugarMoleculeMinus() {
            super( ionProperties );
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
            scale( SugarMoleculeMinus.RADIUS * 3 / width );

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
}