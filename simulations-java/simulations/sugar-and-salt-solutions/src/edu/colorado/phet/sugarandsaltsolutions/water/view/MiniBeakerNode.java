// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerNodeWithoutTicks;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SolutionNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;

/**
 * Shows a small representation of a beaker with solution that is "zoomed in" on by the ParticleWindowNode
 *
 * @author Sam Reid
 */
public class MiniBeakerNode extends PNode {
    public MiniBeakerNode() {
        //Create a whole model, but just for the purpose of making a beaker graphic.  Shouldn't be a memory leak since no listeners are wired up and this is done only once.
        final MacroModel model = new MacroModel();
        final ModelViewTransform transform = SugarAndSaltSolutionsCanvas.createTransform( model );

        //Add the beaker and water graphics, and increase the wall thickness to make it more visible
        model.beaker.setWallWidth( model.beaker.wallWidth );
        addChild( new BeakerNodeWithoutTicks( transform, model.beaker ) );
        addChild( new SolutionNode( transform, model.solution, new Color( WATER_COLOR.getRed(), WATER_COLOR.getGreen(), WATER_COLOR.getBlue(), 255 ) ) );

        //Make it smaller so it will fit on the screen
        scale( 0.34 * 0.5 );
    }
}