
package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.game.*;
import edu.colorado.phet.reactantsproductsandleftovers.view.game.DevValuesNode.DevAfterValuesNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.game.DevValuesNode.DevBeforeValuesNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionEquationNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends RPALCanvas {
    
    private final GameModel model;
    
    // these nodes are final, allocated once
    private final PhetPNode gameSettingsNode;
    private final ScoreboardNode scoreboardNode;
    private final FaceNode beforeFaceNode, afterFaceNode;
    private final RightArrowNode arrowNode;
    private final ReactionNumberLabelNode reactionNumberLabelNode;
    private final PhetPNode parentNode;
    private final GradientButtonNode checkButton, nextButton, tryAgainButton, showAnswerButton;
    private final PhetPNode buttonsParentNode;
    private final GameInstructionsNode beforeInstructions, afterInstructions;
    private final DevBeforeValuesNode devBeforeValuesNode;
    private final DevAfterValuesNode devAfterValuesNode;
    private final GameSummaryNode gameSummaryNode;
    
    // these nodes are mutable, allocated when reaction changes
    private RealReactionEquationNode equationNode;
    private GameBeforeNode beforeNode;
    private GameAfterNode afterNode;
    
    public GameCanvas( final GameModel model, Resettable resettable ) {
        super();
        
        // game settings
        gameSettingsNode = new GameSettingsNode( model );
        gameSettingsNode.scale( 1.5 ); //XXX scale
        addChild( gameSettingsNode );
        
        // game summary
        gameSummaryNode = new GameSummaryNode( model );
        gameSummaryNode.scale( 1.5 ); //XXX scale
        addChild( gameSummaryNode );
        
        // all other nodes are children of this node
        parentNode = new PhetPNode();
        addChild( parentNode );
        parentNode.moveToBack();
        
        // right-pointing arrow
        arrowNode = new RightArrowNode();
        parentNode.addChild( arrowNode );
        
        // reaction number label
        reactionNumberLabelNode = new ReactionNumberLabelNode( model );
        parentNode.addChild( reactionNumberLabelNode );
        
        // scoreboard
        scoreboardNode = new ScoreboardNode( model );
        scoreboardNode.scale( 1.5 ); //XXX scale
        parentNode.addChild( scoreboardNode );
        
        // faces, for indicating correct/incorrect answers
        beforeFaceNode = new FaceNode();
        parentNode.addChild( beforeFaceNode );
        afterFaceNode = new FaceNode();
        parentNode.addChild( afterFaceNode );
        
        // buttons, all under the same parent, to facilitate moving between Before & After boxes
        buttonsParentNode = new PhetPNode();
        parentNode.addChild( buttonsParentNode );
        checkButton = new GradientButtonNode( RPALStrings.BUTTON_CHECK, 20, Color.YELLOW );
        buttonsParentNode.addChild( checkButton );
        nextButton = new GradientButtonNode( RPALStrings.BUTTON_NEXT, 20, Color.YELLOW );
        buttonsParentNode.addChild( nextButton );
        tryAgainButton = new GradientButtonNode( RPALStrings.BUTTON_TRY_AGAIN, 20, Color.YELLOW );
        buttonsParentNode.addChild( tryAgainButton );
        showAnswerButton = new GradientButtonNode( RPALStrings.BUTTON_SHOW_ANSWER, 20, Color.YELLOW );
        buttonsParentNode.addChild( showAnswerButton );
        
        // instructions
        beforeInstructions = new GameInstructionsNode( RPALStrings.QUESTION_HOW_MANY_REACTANTS );
        parentNode.addChild( beforeInstructions );
        afterInstructions = new GameInstructionsNode( RPALStrings.QUESTION_HOW_MANY_PRODUCTS_AND_LEFTOVERS );
        parentNode.addChild( afterInstructions );
        
        // dev nodes
        devBeforeValuesNode = new DevBeforeValuesNode( model );
        devAfterValuesNode = new DevAfterValuesNode( model );
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            parentNode.addChild( devBeforeValuesNode );
            parentNode.addChild( devAfterValuesNode );
        }
        
        this.model = model;
        model.addGameListener( new GameAdapter() {
            
            // User requested to start a new game.
            @Override
            public void newGame() {
                gameSettingsNode.setVisible( true );
                gameSummaryNode.setVisible( false );
                parentNode.setVisible( false );
            }
            
            // When a game starts, hide the game settings panel.
            @Override
            public void gameStarted() {
                gameSettingsNode.setVisible( false );
                gameSummaryNode.setVisible( false );
                parentNode.setVisible( true );
            }
            
            @Override
            public void gameCompleted() {
                gameSummaryNode.setVisible( true );
            }
            
            // When a game is aborted, show the game settings panel.
            @Override 
            public void gameAborted() {
                gameSettingsNode.setVisible( true );
                gameSummaryNode.setVisible( false );
                parentNode.setVisible( false );
            }
            
            // When the reaction changes, rebuild dynamic nodes.
            @Override
            public void challengeChanged() {
                updateNodes();
            }
            
        } );
        
        // Check button checks the user's solution
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "GameCanvas.checkButton.actionPerformed" ); //XXX
            }
        });
        
        // Next button advanced to the next challenge
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.nextChallenge();
            }
        });
        
        // Try Again button lets the user make another attempt
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "GameCanvas.tryAgainButton.actionPerformed" ); //XXX
            }
        });
        
        // Show Answer button shows the answer
        showAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "GameCanvas.showAnswerButton.actionPerformed" ); //XXX
            }
        });
        
        // initial state
        updateNodes();
        gameSettingsNode.setVisible( true );
        gameSummaryNode.setVisible( false );
        parentNode.setVisible( false );
   }
    
    private void updateNodes() {
        
        //XXX call cleanup on these dynamic nodes so we don't have memory leaks

        parentNode.removeChild( equationNode );
        equationNode = new RealReactionEquationNode( model.getReaction() );
        parentNode.addChild( equationNode );

        if ( beforeNode != null ) {
            beforeNode.cleanup();
        }
        parentNode.removeChild( beforeNode );
        beforeNode = new GameBeforeNode( model );
        parentNode.addChild( beforeNode );

        if ( afterNode != null ) {
            afterNode.cleanup();
        }
        parentNode.removeChild( afterNode );
        afterNode = new GameAfterNode( model );
        parentNode.addChild( afterNode );
        
        // move a bunch of static nodes to the front
        beforeFaceNode.moveToFront();
        afterFaceNode.moveToFront();
        buttonsParentNode.moveToFront();
        beforeInstructions.moveToFront();
        afterInstructions.moveToFront();
        devBeforeValuesNode.moveToFront();
        devAfterValuesNode.moveToFront();
        
        updateNodesLayout();
    }
    
    private void updateNodesLayout() {
        
        double x = 0;
        double y = 0;
        
        // reaction number label in upper right
        reactionNumberLabelNode.setOffset( x, y );
        
        // equation to right of label, vertically centered
        x = reactionNumberLabelNode.getFullBoundsReference().getWidth() + 35;
        y = reactionNumberLabelNode.getYOffset();
        equationNode.setOffset( x, y );
        
        // Before box below reaction number label, left justified
        x = reactionNumberLabelNode.getFullBoundsReference().getMinX();
        y = reactionNumberLabelNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
        beforeNode.setOffset( x, y );
        
        // arrow to the right of Before box, vertically centered with box
        final double arrowXSpacing = 20;
        x = beforeNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset() + ( beforeNode.getBoxHeight() / 2 );
        arrowNode.setOffset( x, y );

        // After box to the right of arrow, top aligned with Before box
        x = arrowNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset();
        afterNode.setOffset( x, y );
        
        // scoreboard, at bottom center of play area
        x = beforeNode.getFullBoundsReference().getMinX();
        y = beforeNode.getFullBoundsReference().getMaxY() + 10;
        scoreboardNode.setOffset( x, y ) ;
        
        // faces in upper center of Before box
        x = beforeNode.getFullBoundsReference().getCenterX() - ( beforeFaceNode.getFullBoundsReference().getWidth() / 2 );
        y = beforeNode.getYOffset() + 20;
        beforeFaceNode.setOffset( x, y );
        
        // face in upper center of After box
        x = afterNode.getFullBoundsReference().getCenterX() - ( afterFaceNode.getFullBoundsReference().getWidth() / 2 );
        y = afterNode.getYOffset() + 20;
        afterFaceNode.setOffset( x, y );
        
        // buttons
        {
            //XXX arrange all buttons in a row for now
            x = 0;
            y = 0;
            checkButton.setOffset( x, y );
            x = checkButton.getFullBoundsReference().getMaxX() + 10;
            y = checkButton.getYOffset();
            nextButton.setOffset( x, y );
            x = nextButton.getFullBoundsReference().getMaxX() + 10;
            y = nextButton.getYOffset();
            tryAgainButton.setOffset( x, y );
            x = tryAgainButton.getFullBoundsReference().getMaxX() + 10;
            y = tryAgainButton.getYOffset();
            showAnswerButton.setOffset( x, y );
            
            // put buttons at bottom center of the proper box
            PNode challengeBoxNode = beforeNode;
            if ( model.getChallengeType() == ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS ) {
                challengeBoxNode = afterNode;
            }
            double boxWidth = beforeNode.getBoxWidth(); //XXX boxes should be the same size, but should call challengeBoxNode.getBoxWidth
            double boxHeight = beforeNode.getBoxHeight(); //XXX boxes should be the same size, but should call challengeBoxNode.getBoxHeight 
            x = challengeBoxNode.getXOffset() + ( ( boxWidth - buttonsParentNode.getFullBoundsReference().getWidth() ) / 2 );
            y = challengeBoxNode.getYOffset() + boxHeight - buttonsParentNode.getFullBoundsReference().getHeight() - 10;
            buttonsParentNode.setOffset( x, y );
        }
        
        // instructions centered in Before box
        x = beforeNode.getXOffset() + ( ( beforeNode.getBoxWidth() - beforeInstructions.getFullBoundsReference().getWidth() ) / 2 );
        y = beforeNode.getYOffset() + ( ( beforeNode.getBoxHeight() - beforeInstructions.getFullBoundsReference().getHeight() ) / 2 );
        beforeInstructions.setOffset( x, y );
        
        // instructions centered in After box
        x = afterNode.getXOffset() + ( ( afterNode.getBoxWidth() - afterInstructions.getFullBoundsReference().getWidth() ) / 2 );
        y = afterNode.getYOffset() + ( ( afterNode.getBoxHeight() - afterInstructions.getFullBoundsReference().getHeight() ) / 2 );;
        afterInstructions.setOffset( x, y );
        
        // dev values in upper-left of Before box
        x = beforeNode.getXOffset() + 10;
        y = beforeNode.getYOffset() + 10;
        devBeforeValuesNode.setOffset( x, y );
        
        // dev values in upper-left of After box
        x = afterNode.getXOffset() + 10;
        y = afterNode.getYOffset() + 10;
        devAfterValuesNode.setOffset( x, y );
        
        // game summmary, horizontally and vertically centered on everything else
        x = parentNode.getFullBoundsReference().getCenterX() - ( gameSummaryNode.getFullBoundsReference().getWidth() / 2 );
        y =  parentNode.getFullBoundsReference().getCenterY() - ( gameSummaryNode.getFullBoundsReference().getHeight() / 2 );
        gameSummaryNode.setOffset( x, y );
        
        // game settings, horizontally and vertically centered on everything else
        x = parentNode.getFullBoundsReference().getCenterX() - ( gameSettingsNode.getFullBoundsReference().getWidth() / 2 );
        y =  parentNode.getFullBoundsReference().getCenterY() - ( gameSettingsNode.getFullBoundsReference().getHeight() / 2 );
        gameSettingsNode.setOffset( x, y );
    }

    /*
     * Centers the root node on the canvas when the canvas size changes.
     */
    @Override
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
            centerRootNode();
        }
    }
}
