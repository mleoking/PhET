// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Introductory (macro) module for sugar and salt solutions
 *
 * @author Sam Reid
 */
public class MacroModule extends SugarAndSaltSolutionsModule {
    private final MacroModel model;

    public MacroModule( SugarAndSaltSolutionsColorScheme backgroundColor, Function1<MacroModel, PNode> removeSolutesControl ) {
        this( new MacroModel(), backgroundColor, removeSolutesControl );
    }

    private MacroModule( MacroModel model, SugarAndSaltSolutionsColorScheme config, Function1<MacroModel, PNode> removeSolutesControl ) {
        super( "Macro", model.clock );
        this.model = model;
        setSimulationPanel( new MacroCanvas( this.model, config, removeSolutesControl ) );
    }
}
