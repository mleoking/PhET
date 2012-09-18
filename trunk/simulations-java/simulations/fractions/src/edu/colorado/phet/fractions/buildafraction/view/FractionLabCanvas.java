// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode.Element;
import edu.colorado.phet.fractions.buildafraction.FractionLabCanvasContext;
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
import edu.colorado.phet.fractions.common.view.RefreshButtonNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType.BAR;
import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType.PIE;
import static edu.colorado.phet.fractions.buildafraction.view.LevelSelectionNode.colors;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.createPieSlice;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.createRect;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.PieceNode.stroke;
import static java.awt.Color.black;

/**
 * Canvas for the "Free Play" tab.
 *
 * @author Sam Reid
 */
public class FractionLabCanvas extends AbstractFractionsCanvas {
    public FractionLabCanvas( final FractionLabCanvasContext context2 ) {
        setBackground( BuildAFractionCanvas.LIGHT_BLUE );
        final BuildAFractionModel circleModel = createModel( PIE, colors[0] );

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

        NumberSceneNode numberSceneNode = new NumberSceneNode( 0, rootNode, circleModel, STAGE_SIZE, context, soundEnabled, true );
        addChild( numberSceneNode );

        final ShapeSceneNode circleShapeSceneNode = new ShapeSceneNode( 0, circleModel, STAGE_SIZE, context, soundEnabled, true ) {{
            translate( 0, -STAGE_SIZE.height + toolboxHeight + INSET * 3 );
        }};
        addChild( circleShapeSceneNode );

        final ShapeSceneNode barShapeSceneNode = new ShapeSceneNode( 0, createModel( BAR, colors[1] ), STAGE_SIZE, context, soundEnabled, true ) {{
            translate( 0, -STAGE_SIZE.height + toolboxHeight + INSET * 3 );
        }};
        addChild( barShapeSceneNode );

        final java.util.List<Element<ShapeType>> elements = Arrays.asList( new Element<ShapeType>( new PhetPPath( createPieSlice( 1 ), colors[0], stroke, black ) {{scale( 0.15 );}}, PIE, Components.pieShapeRadioButton ),
                                                                           new Element<ShapeType>( new PhetPPath( createRect( 1 ), colors[1], stroke, black ) {{scale( 0.15 );}}, BAR, Components.barShapeRadioButton ) );
        final Property<ShapeType> selectedShapeType = new Property<ShapeType>( PIE );
        selectedShapeType.addObserver( new VoidFunction1<ShapeType>() {
            public void apply( final ShapeType shapeType ) {
                if ( shapeType == BAR ) {
                    setEnabled( circleShapeSceneNode, false );
                    setEnabled( barShapeSceneNode, true );
                }
                else {
                    setEnabled( circleShapeSceneNode, true );
                    setEnabled( barShapeSceneNode, false );
                }
            }
        } );

        RadioButtonStripControlPanelNode<ShapeType> representations = new RadioButtonStripControlPanelNode<ShapeType>( selectedShapeType, elements, 3, Color.white, new BasicStroke( 1 ), Color.black, 8, 2, 3 );
        addChild( new VBox( representations, new ResetAllButtonNode( new Resettable() {
            public void reset() {
                context2.resetCanvas();
            }
        }, this, 18, black, RefreshButtonNode.BUTTON_COLOR ) {{
            setConfirmationEnabled( false );
        }} ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, 150 );
        }} );
    }

    private void setEnabled( final PNode node, final boolean enabled ) {
        node.setVisible( enabled );
        node.setPickable( enabled );
        node.setChildPaintInvalid( enabled );
    }

    private BuildAFractionModel createModel( final ShapeType x, final Color color ) {
        return new BuildAFractionModel( new BooleanProperty( false ), new ShapeLevelFactory() {
            public ShapeLevel createLevel( final int level ) {
                final MixedFraction t = MixedFraction.mixedFraction( 3, Fraction.fraction( 1, 2 ) );
                return new ShapeLevel( List.range( 1, 9 ).append( List.range( 1, 9 ) ), List.list( t, t ), color, x, false );
            }
        }, new NumberLevelFactory() {
            public NumberLevel createLevel( final int level ) {
                final NumberTarget t = NumberTarget.target( 1, 2, Color.blue, NumberLevelList.pie.sequential() );
                return new NumberLevel( List.range( 1, 9 ).append( List.range( 1, 9 ) ), List.list( t, t ) );
            }
        }
        );
    }
}