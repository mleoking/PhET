// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

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

    //Create a radio-button-based selector for solutes
    @Override protected SoluteControlPanelNode createSoluteControlPanelNode( SugarAndSaltSolutionModel model, PSwingCanvas canvas, PDimension stageSize ) {
        return new RadioButtonSoluteControlPanelNode( model.dispenserType, canvas );
    }
}