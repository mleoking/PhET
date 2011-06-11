// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.solublesalts.SolubleSaltsApplication.SolubleSaltsClock;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.salt.MercuryBromide;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;

/**
 * Micro tab that shows the NaCl ions and Sucrose molecules
 *
 * @author Sam Reid
 */
public class MicroModule extends SolubleSaltsModule {
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
                    ( (SolubleSaltsModel) getModel() ).setCurrentSalt( new MercuryBromide() );
                }
            }
        } );
        getFullScaleCanvas().addChild( new SoluteControlPanelNode( dispenserType ) {{
            scale( 1.5 );
            setOffset( 1300 - getFullBounds().getWidth(), 768 / 2 - getFullBounds().getHeight() / 2 );
        }} );
    }
}
