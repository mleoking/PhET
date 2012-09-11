// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractions.buildafraction.view.shapes.ShapeSceneNode;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;

/**
 * Canvas for the "Free Play" tab.
 *
 * @author Sam Reid
 */
public class FreePlayCanvas extends AbstractFractionsCanvas {
    public FreePlayCanvas() {
        setBackground( BuildAFractionCanvas.LIGHT_BLUE );
        final BuildAFractionModel model = new BuildAFractionModel( new BooleanProperty( false ) );
        final SceneContext context = new SceneContext() {
            public void goToShapeLevel( final int newLevelIndex ) {
            }

            public void goToNumberLevel( final int newLevelIndex ) {
            }

            public void goToLevelSelectionScreen( final int fromLevelIndex ) {
            }

            public void resampleShapeLevel( final int levelIndex ) {
            }

            public void resampleNumberLevel( final int levelIndex ) {
            }
        };
        final BooleanProperty soundEnabled = new BooleanProperty( false );

        NumberSceneNode numberSceneNode = new NumberSceneNode( 0, rootNode, model, STAGE_SIZE, context, soundEnabled, true );
        addChild( numberSceneNode );

        ShapeSceneNode shapeSceneNode = new ShapeSceneNode( 0, model, STAGE_SIZE, context, soundEnabled, true ) {{
            translate( 0, -STAGE_SIZE.height + toolboxHeight + INSET * 3 );
        }};
        addChild( shapeSceneNode );
    }
}