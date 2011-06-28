// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;

/**
 * Canvas for the introductory (macro) tab of sugar and salt solutions
 *
 * @author Sam Reid
 */
public class MacroCanvas extends SugarAndSaltSolutionsCanvas {
    public MacroCanvas( final MacroModel model, GlobalState globalState ) {
        super( model, globalState );

        //This tab uses the conductivity tester
        submergedInWaterNode.addChild( conductivityToolboxLayer );
    }
}