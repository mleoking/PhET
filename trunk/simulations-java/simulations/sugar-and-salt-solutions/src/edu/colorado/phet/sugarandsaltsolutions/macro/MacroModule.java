// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro;

import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
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

    public MacroModule( GlobalState globalState ) {
        this( new MacroModel(), globalState );
    }

    private MacroModule( final MacroModel model, GlobalState globalState ) {
        super( SugarAndSaltSolutionsResources.Strings.MACRO, model.clock );
        this.model = model;
        setSimulationPanel( new MacroCanvas( this.model, globalState ) );

        //When the module becomes activated/deactivated, update the flag in the model for purposes of starting and stopping the clock
        listenForModuleActivated( model.moduleActive );
    }
}
