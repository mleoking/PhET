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
import edu.colorado.phet.solublesalts.SolubleSaltsApplication.SolubleSaltsClock;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.OneToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonProperties;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.view.SucroseNode;

/**
 * Micro tab that shows the NaCl ions and Sucrose molecules
 *
 * @author Sam Reid
 */
public class MicroModule extends SolubleSaltsModule {
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

        // Use NaCl by default

        final Property<DispenserType> dispenserType = new Property<DispenserType>( DispenserType.SALT );
        dispenserType.addObserver( new SimpleObserver() {
            public void update() {
                if ( dispenserType.get() == DispenserType.SALT ) {
                    ( (SolubleSaltsModel) getModel() ).setCurrentSalt( new SodiumChloride() );
                }
                else {
                    ( (SolubleSaltsModel) getModel() ).setCurrentSalt( new SugarCrystal() );
                }
            }
        } );
        getFullScaleCanvas().addChild( new SoluteControlPanelNode( dispenserType ) {{
            scale( 1.5 );
            setOffset( 1300 - getFullBounds().getWidth(), 768 / 2 - getFullBounds().getHeight() / 2 );
        }} );
    }

    public static class SugarCrystal extends Salt {
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

    private static BufferedImage getSucroseImage() {
        final SucroseNode sucroseNode = new SucroseNode( ModelViewTransform.createSinglePointScaleMapping( new Point2D.Double(), new Point2D.Double(), 3150 / 6.3E-7 * 400 ), new WaterModel().newSugar( 0, 0 ), new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 voidFunction0 ) {
                voidFunction0.apply();
            }
        }, Element.O.getColor(), Element.H.getColor(), Color.gray ) {{
            double width = getFullBounds().getWidth();
            scale( SugarMoleculeMinus.RADIUS * 3 / width );
            rotate( Math.random() * Math.PI * 2 );
        }};
        return (BufferedImage) sucroseNode.toImage();
    }

    //Sample main writes a sucrose image to file for inspection
    public static void main( String[] args ) throws IOException {
        ImageIO.write( getSucroseImage(), "PNG", new File( args[0], System.currentTimeMillis() + ".PNG" ) );
    }
}