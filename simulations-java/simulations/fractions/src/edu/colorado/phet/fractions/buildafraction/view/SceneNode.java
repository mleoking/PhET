// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.common.view.LevelSelectionScreenButton;
import edu.colorado.phet.fractions.common.view.RefreshButtonNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendSystemMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SystemComponentTypes.application;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.common.view.RefreshButtonNode.BUTTON_COLOR;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.SystemActions.allChallengesComplete;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.SystemActions.oneChallengeComplete;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.SystemComponents.buildAFraction;
import static fj.Ord.doubleOrd;
import static fj.data.List.iterableList;

/**
 * Base class for a Scene (such as the picture or number game scene).  Factors out duplicate behavior and code that appears in both scene types.
 *
 * @author Sam Reid
 */
public abstract class SceneNode<T extends ICollectionBoxPair> extends PNode {
    public List<T> pairs;
    protected final LevelSelectionScreenButton levelSelectionScreenButton;
    protected VBox faceNodeDialog;
    protected PhetPText levelReadoutTitle;

    protected SceneNode( BooleanProperty audioEnabled, final SceneContext context ) {
        gameAudioPlayer = new GameAudioPlayer( audioEnabled.get() );
        audioEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean enabled ) {
                gameAudioPlayer.setEnabled( enabled );
            }
        } );

        levelSelectionScreenButton = new LevelSelectionScreenButton( new VoidFunction0() {
            public void apply() {
                context.goToLevelSelectionScreen();
            }
        }, 0, 0 ) {{
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
        addChild( levelSelectionScreenButton );
    }

    private final GameAudioPlayer gameAudioPlayer;

    //Play audio and send simsharing message
    protected void notifyOneCompleted() {
        gameAudioPlayer.correctAnswer();
        sendSystemMessage( buildAFraction, application, oneChallengeComplete );
    }

    //Play audio and send simsharing message
    protected void notifyAllCompleted() {
        gameAudioPlayer.gameOverPerfectScore();
        sendSystemMessage( buildAFraction, application, allChallengesComplete );
    }

    protected void initCollectionBoxes( final double insetY, final ArrayList<T> _pairs ) {
        this.pairs = iterableList( _pairs );

        List<PNode> patterns = pairs.map( new F<T, PNode>() {
            @Override public PNode f( final T pair ) {
                return pair.getTargetNode();
            }
        } );
        double maxWidth = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getWidth();
            }
        } ).maximum( doubleOrd );
        double maxHeight = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getHeight();
            }
        } ).maximum( doubleOrd );

        //Layout for the scoring cells and target patterns
        double separation = 5;
        double rightInset = 10;
        final PBounds targetCellBounds = pairs.head().getCollectionBoxNode().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
        double offsetY = INSET;
        for ( ICollectionBoxPair pair : pairs ) {

            pair.getCollectionBoxNode().setOffset( offsetX, offsetY );
            pair.getTargetNode().setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.getCollectionBoxNode() );
            addChild( pair.getTargetNode() );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }
    }

    protected void finishCreatingUI( final int levelIndex, final BuildAFractionModel model, final PDimension stageSize, final ActionListener goToNextLevel, final VoidFunction0 _resampleLevel ) {
        final HTMLImageButtonNode nextButton = new HTMLImageButtonNode( Strings.NEXT, new PhetFont( 20, true ), BUTTON_COLOR ) {{
            setUserComponent( Components.nextButton );
            addActionListener( goToNextLevel );
        }};
        faceNodeDialog = new VBox( new FaceNode( 200 ), model.isLastLevel( levelIndex ) ? new PNode() : nextButton ) {{
            setOffset( stageSize.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, stageSize.getHeight() / 2 - getFullBounds().getHeight() / 2 - 50 );
        }};

        faceNodeDialog.setTransparency( 0 );
        faceNodeDialog.setVisible( false );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );

        addChild( faceNodeDialog );

        double minScoreCellX = pairs.map( new F<T, Double>() {
            @Override public Double f( final T target ) {
                return target.getCollectionBoxNode().getFullBounds().getMinX();
            }
        } ).minimum( doubleOrd );
        levelReadoutTitle = new PhetPText( MessageFormat.format( Strings.LEVEL__PATTERN, levelIndex + 1 ), new PhetFont( 32, true ) );
        levelReadoutTitle.setOffset( minScoreCellX / 2 - levelReadoutTitle.getFullWidth() / 2, levelSelectionScreenButton.getFullBounds().getCenterY() - levelReadoutTitle.getFullHeight() / 2 );
        addChild( levelReadoutTitle );

        final TextButtonNode resetButton = new TextButtonNode( Strings.RESET, AbstractFractionsCanvas.CONTROL_FONT, BUTTON_COLOR ) {{
            setUserComponent( Components.resetButton );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    reset();
                }
            } );
        }};
        final RefreshButtonNode refreshButton = new RefreshButtonNode( _resampleLevel );

        addChild( new HBox( resetButton, refreshButton ) {{
            setOffset( levelReadoutTitle.getCenterX() - getFullBounds().getWidth() / 2, levelReadoutTitle.getMaxY() + INSET );
        }} );
    }

    protected abstract void reset();
}