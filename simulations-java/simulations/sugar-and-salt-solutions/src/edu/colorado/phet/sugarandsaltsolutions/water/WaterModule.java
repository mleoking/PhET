// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;

/**
 * Module for "water" tab of sugar and salt solutions module
 *
 * @author Sam Reid
 */
public class WaterModule extends SugarAndSaltSolutionsModule {
    private final WaterModel model;

    public WaterModule( SugarAndSaltSolutionsColorScheme config ) {
        this( new WaterModel(), config );
    }

    public WaterModule( final WaterModel model, SugarAndSaltSolutionsColorScheme config ) {
        super( "Water", model.clock );
        this.model = model;
        setSimulationPanel( new WaterCanvas( model, config ) );
    }
}