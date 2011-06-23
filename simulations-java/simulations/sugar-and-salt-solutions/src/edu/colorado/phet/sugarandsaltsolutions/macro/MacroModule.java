// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro;

import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;

/**
 * Introductory (macro) module for sugar and salt solutions
 *
 * @author Sam Reid
 */
public class MacroModule extends SugarAndSaltSolutionsModule {
    private final MacroModel model;

    public MacroModule( SugarAndSaltSolutionsColorScheme backgroundColor ) {
        this( new MacroModel(), backgroundColor );
    }

    private MacroModule( MacroModel model, SugarAndSaltSolutionsColorScheme config ) {
        super( SugarAndSaltSolutionsResources.MACRO, model.clock );
        this.model = model;
        setSimulationPanel( new MacroCanvas( this.model, config ) );
    }
}
