// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Canvas for the introductory (macro) tab of sugar and salt solutions
 *
 * @author Sam Reid
 */
public class MacroCanvas extends SugarAndSaltSolutionsCanvas {

    //Separate layer for the conductivity toolbox to make sure the conductivity node shows as submerged in the water, but still goes behind the shaker
    protected final PNode conductivityToolboxLayer = new PNode();

    public MacroCanvas( final MacroModel model, GlobalState globalState ) {
        super( model, globalState );

        //This tab uses the conductivity tester
        submergedInWaterNode.addChild( conductivityToolboxLayer );

        //Toolbox from which the conductivity tester can be dragged
        conductivityToolboxLayer.addChild( new ConductivityTesterToolboxNode( model, this ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - INSET, soluteControlPanelNode.getFullBounds().getMaxY() + INSET );
        }} );

        //When the shape of the flowing-out water changes, update the model so we can account for conductivity of the water while it is draining
        drainFaucetNode.addListener( new VoidFunction1<Rectangle2D>() {
            public void apply( Rectangle2D outFlowShape ) {
                ImmutableRectangle2D r = new ImmutableRectangle2D( outFlowShape );
                Rectangle2D transformed = drainFaucetNode.localToGlobal( r.toRectangle2D() );
                model.setOutflowShape( transform.viewToModel( transformed ).getBounds2D() );
            }
        } );
    }

    //Create a radio-button-based selector for solutes
    @Override protected SoluteControlPanelNode createSoluteControlPanelNode( SugarAndSaltSolutionModel model, PSwingCanvas canvas, PDimension stageSize ) {
        return new RadioButtonSoluteControlPanelNode( model.dispenserType, canvas );
    }
}