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
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.common.view.LevelSelectionScreenButton;
import edu.colorado.phet.fractions.common.view.RefreshButtonNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

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
 * Base class for a Scene (such as the picture or number game scene).
 * Factors out duplicate behavior and code that appears in both scene types.
 *
 * @author Sam Reid
 */
public abstract class SceneNode<T extends ICollectionBoxPair> extends PNode {

    //List of ICollectionBoxPairs, that is, the collection box itself as well as the target representation.
    private List<T> collectionBoxPairs;

    //Button to go back to the level selection screen.
    private final LevelSelectionScreenButton levelSelectionScreenButton;

    //Shows the smiling face and optionally "next" button.
    protected VBox faceNodeDialog;

    //Shows the title for the level
    protected PhetPText title;

    //Flag for whether this is used in the "Fraction Lab" tab.
    public final boolean fractionLab;

    //Plays a ding sound when collection box filled and "ta-da" when all collection boxes filled.
    private final GameAudioPlayer gameAudioPlayer;

    //Index of the current level
    public final int levelIndex;

    //When dragging an object, move to front for fraction lab
    private ArrayList<SimpleObserver> interactionHandlers = new ArrayList<SimpleObserver>();

    //For Sim Sharing
    public static int idCounter = 0;
    public final int id = idCounter++;

    protected SceneNode( final int levelIndex, BooleanProperty audioEnabled, final SceneContext context, boolean fractionLab ) {
        this.fractionLab = fractionLab;
        this.levelIndex = levelIndex;
        gameAudioPlayer = new GameAudioPlayer( audioEnabled.get() );
        audioEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean enabled ) {
                gameAudioPlayer.setEnabled( enabled );
            }
        } );

        levelSelectionScreenButton = new LevelSelectionScreenButton( new VoidFunction0() {
            public void apply() {
                context.goToLevelSelectionScreen( levelIndex );
            }
        }, Images.FRACTIONS_BUTTON_BUILD ) {{
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
        if ( !fractionLab ) {
            addChild( levelSelectionScreenButton );
        }
    }

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

    public void addInteractionHandler( final SimpleObserver simpleObserver ) {
        this.interactionHandlers.add( simpleObserver );
    }

    //Lay out and add the collection box nodes and target representations.
    protected void initCollectionBoxes( final double insetY, final ArrayList<T> pairs, boolean fractionLab ) {
        this.collectionBoxPairs = iterableList( pairs );

        List<PNode> patterns = collectionBoxPairs.map( new F<T, PNode>() {
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
        final PBounds targetCellBounds = collectionBoxPairs.head().getCollectionBoxNode().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
        double offsetY = INSET;
        for ( ICollectionBoxPair pair : collectionBoxPairs ) {

            pair.getCollectionBoxNode().setOffset( offsetX, offsetY );
            pair.getTargetNode().setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );

            if ( !fractionLab ) {
                addChild( pair.getCollectionBoxNode() );
                addChild( pair.getTargetNode() );
            }

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }
    }

    //Finish creating the UI, including adding title, refresh button, reset button, etc.
    protected void init( final int levelIndex, final BuildAFractionModel model, final ActionListener goToNextLevel, final VoidFunction0 _resampleLevel, boolean fractionLab ) {

        double minScoreCellX = collectionBoxPairs.map( new F<T, Double>() {
            @Override public Double f( final T target ) {
                return target.getCollectionBoxNode().getFullBounds().getMinX();
            }
        } ).minimum( doubleOrd );
        title = new PhetPText( MessageFormat.format( Strings.LEVEL__PATTERN, levelIndex + 1 ), new PhetFont( 32, true ) );
        title.setOffset( minScoreCellX / 2 - title.getFullWidth() / 2, levelSelectionScreenButton.getFullBounds().getCenterY() - title.getFullHeight() / 2 );
        if ( !fractionLab ) { addChild( title ); }

        final TextButtonNode resetButton = new TextButtonNode( Strings.RESET, AbstractFractionsCanvas.CONTROL_FONT, BUTTON_COLOR ) {{
            setUserComponent( Components.resetButton );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    reset();
                }
            } );
        }};
        final RefreshButtonNode refreshButton = new RefreshButtonNode( _resampleLevel );

        if ( !fractionLab ) {
            addChild( new HBox( resetButton, refreshButton ) {{
                setOffset( title.getCenterX() - getFullBounds().getWidth() / 2, title.getMaxY() + INSET );
            }} );
        }

        final HTMLImageButtonNode nextButton = new HTMLImageButtonNode( Strings.NEXT, new PhetFont( 20, true ), BUTTON_COLOR ) {{
            setUserComponent( Components.nextButton );
            addActionListener( goToNextLevel );
        }};
        faceNodeDialog = new VBox( new FaceNode( 200 ), model.isLastLevel( levelIndex ) ? new PNode() : nextButton ) {{
            setOffset( title.getCenterX() - getFullBounds().getWidth() / 2, AbstractFractionsCanvas.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 - 50 );
        }};

        faceNodeDialog.setTransparency( 0 );
        faceNodeDialog.setVisible( false );
        faceNodeDialog.setPickable( false );
        faceNodeDialog.setChildrenPickable( false );

        addChild( faceNodeDialog );
    }

    protected abstract void reset();

    public List<T> getCollectionBoxPairs() { return collectionBoxPairs; }

    public void fireInteractionEvent() {
        for ( SimpleObserver interactionHandler : interactionHandlers ) {
            interactionHandler.update();
        }
    }

    public void moveToFrontDuringInteraction() {
        addInteractionHandler( new SimpleObserver() {
            public void update() {
                moveToFront();
            }
        } );
    }

    //Tab 3: When changing representations, keep the old representation on the screen, so you can see both red circles and blue boxes (but only one toolbox at a time)
    public abstract void setToolboxEnabled( final boolean enabled );
}