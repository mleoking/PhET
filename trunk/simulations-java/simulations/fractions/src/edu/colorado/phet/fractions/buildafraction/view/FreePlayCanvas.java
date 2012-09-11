// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.model.NumberLevelFactory;
import edu.colorado.phet.fractions.buildafraction.model.ShapeLevelFactory;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractions.buildafraction.view.shapes.ShapeSceneNode;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;

/**
 * Canvas for the "Free Play" tab.
 *
 * @author Sam Reid
 */
public class FreePlayCanvas extends AbstractFractionsCanvas {
    public FreePlayCanvas() {
        setBackground( BuildAFractionCanvas.LIGHT_BLUE );
        final BuildAFractionModel model = new BuildAFractionModel( new BooleanProperty( false ), new ShapeLevelFactory() {
            public ShapeLevel createLevel( final int level ) {
                final MixedFraction t = MixedFraction.mixedFraction( 0, Fraction.fraction( 1, 2 ) );
                return new ShapeLevel( List.range( 1, 9 ).append( List.range( 1, 9 ) ), List.list( t, t ), Color.blue, ShapeType.PIE );
            }
        }, new NumberLevelFactory() {
            public NumberLevel createLevel( final int level ) {
                final NumberTarget t = NumberTarget.target( 1, 2, Color.blue, NumberLevelList.pie.sequential() );
                return new NumberLevel( List.range( 1, 10 ).append( List.range( 1, 10 ) ), List.list( t, t ) );
            }
        }
        );
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