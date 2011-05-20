// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;

/**
 * Module for "microscopic" tab of sugar and salt solutions module
 *
 * @author Sam Reid
 */
public class MicroscopicModule extends SugarAndSaltSolutionsModule {
    private final MicroscopicModel model;

    public MicroscopicModule( SugarAndSaltSolutionsColorScheme config ) {
        this( new MicroscopicModel(), config );
    }

    public MicroscopicModule( final MicroscopicModel model, SugarAndSaltSolutionsColorScheme config ) {
        super( "Microscopic", model.clock );
        this.model = model;
        setSimulationPanel( new MicroscopicCanvas( model, config ) );
    }
}