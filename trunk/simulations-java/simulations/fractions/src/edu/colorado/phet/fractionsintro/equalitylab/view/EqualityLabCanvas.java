// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.equalitylab.model.EqualityLabModel;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.FractionControlNode;
import edu.colorado.phet.fractionsintro.intro.view.NumberLineNode;
import edu.colorado.phet.fractionsintro.intro.view.NumberLineNode.Vertical;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.colorado.phet.fractionsintro.intro.view.RepresentationNode;
import edu.colorado.phet.fractionsintro.intro.view.WaterGlassSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.MovableSliceLayer;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.HorizontalBarIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.NumberLineIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.PieIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationControlPanel;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.WaterGlassIcon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScale;
import static edu.colorado.phet.fractions.FractionsResources.Images.LOCKED;
import static edu.colorado.phet.fractions.FractionsResources.Images.UNLOCKED;
import static edu.colorado.phet.fractionsintro.equalitylab.model.EqualityLabModel.scaledFactorySet;
import static edu.colorado.phet.fractionsintro.intro.view.Representation.*;
import static edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode.CreateEmptyCellsNode;
import static edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode.NodeToShape;
import static java.awt.Color.black;
import static java.awt.Color.orange;

/**
 * Canvas for "Equality Lab" tab.
 *
 * @author Sam Reid
 */
public class EqualityLabCanvas extends AbstractFractionsCanvas {

    public EqualityLabCanvas( final EqualityLabModel model ) {

        final SettableProperty<Representation> leftRepresentation = model.leftRepresentation;
        final SettableProperty<Representation> rightRepresentation = model.rightRepresentation;

        //Control panel for choosing different representations, can be split into separate controls for each display
        //Make the control panels a little smaller in this one so that we have more vertical space for representations
        final double representationControlPanelScale = 0.80;
        final RichPNode leftRepresentationControlPanel = new ZeroOffsetNode( new RepresentationControlPanel( leftRepresentation, getRepresentations( leftRepresentation, LIGHT_GREEN ) ) {{ scale( representationControlPanelScale ); }} ) {{
            setOffset( 114, INSET );
        }};

        final RichPNode rightRepresentationControlPanel = new ZeroOffsetNode( new RepresentationControlPanel( model.rightRepresentation, getRepresentations( model.rightRepresentation, LIGHT_BLUE ) ) {{scale( representationControlPanelScale );}} ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullWidth() - 30 - 84, INSET );
        }};

        //Bridge for the lock to sit on
        final int g = 250;
        addChild( new ZeroOffsetNode( new ControlPanelNode( new PhetPPath( new Rectangle2D.Double( 0, 0, 200, 30 ), null, null, null ), new Color( g, g, g ), new BasicStroke( 1 ), Color.lightGray ) ) {{
            setOffset( ( leftRepresentationControlPanel.getCenterX() + rightRepresentationControlPanel.getCenterX() ) / 2 - getFullBounds().getWidth() / 2, leftRepresentationControlPanel.getCenterY() - getFullBounds().getHeight() / 2 );
            model.locked.addObserver( new VoidFunction1<Boolean>() {
                @Override public void apply( final Boolean locked ) {
                    setVisible( locked );
                }
            } );
        }} );

        addChildren( leftRepresentationControlPanel, rightRepresentationControlPanel );

        //Toggle to lock/unlock representations
        addChild( new PImage( multiScale( LOCKED, 0.4 ) ) {{
            model.locked.addObserver( new VoidFunction1<Boolean>() {
                @Override public void apply( final Boolean locked ) {
                    setImage( multiScale( locked ? LOCKED : UNLOCKED, 0.4 ) );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    model.locked.set( !model.locked.get() );
                }
            } );
            setOffset( ( leftRepresentationControlPanel.getCenterX() + rightRepresentationControlPanel.getCenterX() ) / 2 - getFullBounds().getWidth() / 2, leftRepresentationControlPanel.getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, black, orange ) {{
            setConfirmationEnabled( false );
        }};
        addChild( resetAllButtonNode );

        addPrimaryRepresentationNodes( model, leftRepresentation, model.pieSet );

        //Show the pie set node when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, PIE, new PNode() {{
            model.rightPieSet.addObserver( new SimpleObserver() {
                @Override public void update() {
                    removeAllChildren();
                    addChild( CreateEmptyCellsNode.f( model.rightPieSet.get() ) );
                    addChild( new MovableSliceLayer( model.rightPieSet.get(), NodeToShape, model.rightPieSet, rootNode, STAGE_SIZE.getHeight() ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Show the horizontal bar set node when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, HORIZONTAL_BAR, new PNode() {{
            model.rightHorizontalBars.addObserver( new SimpleObserver() {
                @Override public void update() {
                    removeAllChildren();
                    addChild( CreateEmptyCellsNode.f( model.rightHorizontalBars.get() ) );
                    addChild( new MovableSliceLayer( model.rightHorizontalBars.get(), NodeToShape, model.rightHorizontalBars, rootNode, STAGE_SIZE.getHeight() ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Show the water glasses when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, WATER_GLASSES, new PNode() {{
            model.rightWaterGlasses.addObserver( new SimpleObserver() {
                @Override public void update() {
                    removeAllChildren();
                    final Shape shape = scaledFactorySet.waterGlassSetFactory.createSlicesForBucket( model.denominator.get(), 1 ).head().getShape();
                    addChild( WaterGlassSetNode.createEmptyCellsNode( LIGHT_BLUE, shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight() ).f( model.rightWaterGlasses.get() ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Number line

        //Experiment with making semi-transparent
//        addChild( new NumberLineNode( model.scaledNumerator, null, model.scaledDenominator, model.rightRepresentation.valueEquals( NUMBER_LINE ), model.maximum, new Vertical(), 15, new Color( LIGHT_BLUE.getRed(), LIGHT_BLUE.getGreen(), LIGHT_BLUE.getBlue(), 200 ) ) {{
        addChild( new NumberLineNode( model.scaledNumerator, null, model.scaledDenominator, model.rightRepresentation.valueEquals( NUMBER_LINE ), model.maximum, new Vertical(), 15, new Color( LIGHT_BLUE.getRed(), LIGHT_BLUE.getGreen(), LIGHT_BLUE.getBlue(), 200 ) ) {{
            setOffset( 385 + 200, 445 );

            //Can't interact with right-side representations
            setPickable( false );
            setChildrenPickable( false );
        }} );

        resetAllButtonNode.setOffset( STAGE_SIZE.getWidth() - resetAllButtonNode.getFullBounds().getWidth(), STAGE_SIZE.getHeight() - resetAllButtonNode.getFullBounds().getHeight() );

        //The fraction control node.  In front so the user doesn't accidentally press a flying pie slice when they are trying to toggle the spinner
        final ZeroOffsetNode fractionControl = new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator, model.maximum, 6 ) {{
            setScale( 0.75 );
        }} ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() - 50, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( fractionControl );

        final PhetPText equalsSign = new PhetPText( "=", new PhetFont( 120 ) ) {{
            setOffset( fractionControl.getMaxX() + 10, fractionControl.getCenterY() - getFullHeight() / 2 );
        }};
        addChild( equalsSign );

        addChild( new ZeroOffsetNode( new ScaledUpFractionNode( model.numerator, model.denominator, model.scale ) {{setScale( 0.75 );}} ) {{
            setOffset( equalsSign.getMaxX() + 10, equalsSign.getCenterY() - getFullHeight() / 2 );
        }} );
    }

    //Add representations for the left side
    private void addPrimaryRepresentationNodes( final EqualityLabModel model,
                                                final SettableProperty<Representation> representation,
                                                SettableProperty<PieSet> pieSet ) {
        //Show the pie set node when pies are selected
        addChild( new RepresentationNode( representation, PIE, new PieSetNode( pieSet, rootNode ) ) );

        //For horizontal bars
        addChild( new RepresentationNode( representation, HORIZONTAL_BAR, new PieSetNode( model.horizontalBarSet, rootNode ) ) );

        //For water glasses
        final Rectangle2D b = model.primaryFactorySet.waterGlassSetFactory.createEmptyPies( 1, 1 ).head().cells.head().getShape().getBounds2D();
        addChild( new RepresentationNode( representation, WATER_GLASSES, new WaterGlassSetNode( model.waterGlassSet, rootNode, LIGHT_GREEN, b.getWidth(), b.getHeight() ) ) );

        //Number line
        addChild( new NumberLineNode( model.numerator, model.numerator, model.denominator, representation.valueEquals( NUMBER_LINE ), model.maximum, new Vertical(), 15, LIGHT_GREEN ) {{
            setOffset( 385, 445 );
        }} );
    }

    private RepresentationIcon[] getRepresentations( SettableProperty<Representation> representation, Color color ) {
        return new RepresentationIcon[] {
                new PieIcon( representation, color ),
                new HorizontalBarIcon( representation, color ),
                new WaterGlassIcon( representation, color ),
                new NumberLineIcon( representation ),
        };
    }
}