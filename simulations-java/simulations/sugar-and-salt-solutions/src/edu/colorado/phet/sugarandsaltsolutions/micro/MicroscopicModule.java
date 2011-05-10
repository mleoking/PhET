// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;

/**
 * Module for "microscopic" tab of sugar and salt solutions module
 *
 * @author Sam Reid
 */
public class MicroscopicModule extends SugarAndSaltSolutionsModule {
    private final MicroscopicModel model;

    public MicroscopicModule() {
        this( new MicroscopicModel() );
    }

    public MicroscopicModule( final MicroscopicModel model ) {
        super( "Microscopic", model.clock );
        this.model = model;
        setSimulationPanel( new SugarAndSaltSolutionsCanvas( this.model, new Property<Boolean>( true ) ) );
    }
}