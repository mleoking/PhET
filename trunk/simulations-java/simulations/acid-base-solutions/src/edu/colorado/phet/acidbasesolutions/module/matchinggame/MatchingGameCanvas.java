/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Random;

import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.control.PSwingButton;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.view.MatchingGameAnswerNode;
import edu.colorado.phet.acidbasesolutions.view.MatchingGameQuestionNode;
import edu.colorado.phet.acidbasesolutions.view.MatchingGameScoreNode;
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * MatchingGameCanvas is the canvas for MatchingGameModule.
 * <p>
 * Here's the general layout:
 * <code>
 * |----------------------------------------------------------------------------------
 * |                                                                                 |
 * |  ScoreNode               Question & Answer area      Right solution controls    |
 * |                                                                                 |
 * |  New Solution button                                                            |
 * |                                                                                 |
 * |                                                                                 |
 * |  Left solution                                       Right solution             |
 * |  beaker & graph             View controls            beaker & graph             |
 * |                                                                                 |
 * |                            Reset All button                                     |
 * |                                                                                 |
 * |----------------------------------------------------------------------------------
 * </code>
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameCanvas extends ABSAbstractCanvas {

    private static final int TIMER_DELAY = 2000; // ms
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private final MatchingGameModel model;
    private final StateMachine stateMachine;

    // View
    private final MatchingGameScoreNode scoreNode;
    private final BeakerNode beakerNodeLeft, beakerNodeRight;
    private final ConcentrationGraphNode graphNodeLeft, graphNodeRight;
    private final DecimalFormat pointsFormat;

    // Controls
    private final PSwingButton newSolutionButton;
    private final PSwingButton acidButton, baseButton, matchButton;
    private final SolutionControlsNode solutionControlsNodeRight;
    private final MatchingGameViewControlsNode viewControlsNode;

    // Parent nodes, for changing visibility based on game state
    private final PhetPNode acidBaseQuestionParent;
    private final PhetPNode matchQuestionParent;
    private final PhetPNode acidBaseCorrectParent;
    private final PhetPNode acidBaseWrongParent;
    private final PhetPNode matchCorrectParent;
    private final PhetPNode matchWrongParent;
    private final PhetPNode questionsAndAnswersParent;
    private final MatchingGameAnswerNode acidBaseCorrectNode, acidBaseWrongNode;
    private final MatchingGameAnswerNode matchCorrectNode, matchWrongNode;
    private final HTMLNode continueInstructionsNode;
    private final PhetPNode rightSolutionParent;

    // Dev
    private final PNode cheatNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MatchingGameCanvas( final MatchingGameModel model, Resettable resettable ) {
        super( resettable );

        this.model = model;
        AqueousSolution solutionLeft = model.getSolutionLeft();
        AqueousSolution solutionRight = model.getSolutionRight();

        this.stateMachine = new StateMachine();

        pointsFormat = new DecimalFormat( "0" );

        scoreNode = new MatchingGameScoreNode( model );

        newSolutionButton = new PSwingButton( ABSStrings.BUTTON_NEW_SOLUTION );
        newSolutionButton.scale( ABSConstants.PSWING_SCALE );
        newSolutionButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                model.newSolution();
                stateMachine.setStateAcidBaseQuestion();
            }
        } );

        // "Acid or Base" question and related controls
        {
            PNode question = new MatchingGameQuestionNode( ABSStrings.GAME_ACIDBASE_QUESTION );

            acidButton = new PSwingButton( ABSStrings.BUTTON_ACID );
            acidButton.scale( ABSConstants.PSWING_SCALE );
            acidButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkAcid();
                    if ( success ) {
                        stateMachine.setStateAcidBaseCorrect();
                    }
                    else {
                        stateMachine.setStateAcidBaseWrong();
                    }
                }
            } );

            baseButton = new PSwingButton( ABSStrings.BUTTON_BASE );
            baseButton.scale( ABSConstants.PSWING_SCALE );
            baseButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkBase();
                    if ( success ) {
                        stateMachine.setStateAcidBaseCorrect();
                    }
                    else {
                        stateMachine.setStateAcidBaseWrong();
                    }
                }
            } );

            acidBaseQuestionParent = new PhetPNode();
            acidBaseQuestionParent.addChild( question );
            acidBaseQuestionParent.addChild( acidButton );
            acidBaseQuestionParent.addChild( baseButton );

            question.setOffset( 0, 0 );
            acidButton.setOffset( 0, question.getFullBoundsReference().getMaxY() + 10 );
            baseButton.setOffset( acidButton.getFullBoundsReference().getMaxX() + 10, acidButton.getYOffset() );
        }

        // Correct answer to "Acid or Base" question
        {
            acidBaseCorrectNode = new MatchingGameAnswerNode( ABSStrings.GAME_ACIDBASE_CORRECT );

            acidBaseCorrectParent = new PhetPNode();
            acidBaseCorrectParent.addChild( acidBaseCorrectNode );

            acidBaseCorrectNode.setOffset( 0, 0 );
        }

        // Wrong answer to "Acid or Base" question 
        {
            acidBaseWrongNode = new MatchingGameAnswerNode( ABSStrings.GAME_ACIDBASE_WRONG );

            acidBaseWrongParent = new PhetPNode();
            acidBaseWrongParent.addChild( acidBaseWrongNode );

            acidBaseWrongNode.setOffset( 0, 0 );
        }

        // "Match" question and related controls
        {
            PNode question = new MatchingGameQuestionNode( ABSStrings.GAME_MATCH_QUESTION );

            matchButton = new PSwingButton( ABSStrings.BUTTON_CHECK_MATCH );
            matchButton.scale( ABSConstants.PSWING_SCALE );
            matchButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkMatch();
                    if ( success ) {
                        stateMachine.setStateMatchCorrect();
                    }
                    else {
                        stateMachine.setStateMatchWrong();
                    }
                }
            } );

            matchQuestionParent = new PhetPNode();
            matchQuestionParent.addChild( question );
            matchQuestionParent.addChild( matchButton );

            question.setOffset( 0, 0 );
            matchButton.setOffset( 0, question.getFullBoundsReference().getMaxY() + 10 );
        }

        // Correct answer to "Match" question
        {
            matchCorrectNode = new MatchingGameAnswerNode( ABSStrings.GAME_MATCH_CORRECT );

            continueInstructionsNode = new HTMLNode( ABSStrings.GAME_CONTINUE );
            continueInstructionsNode.setFont( new PhetFont( 16 ) );
            continueInstructionsNode.setHTMLColor( Color.RED );

            matchCorrectParent = new PhetPNode();
            matchCorrectParent.addChild( matchCorrectNode );
            matchCorrectParent.addChild( continueInstructionsNode );

            matchCorrectNode.setOffset( 0, 0 );
            continueInstructionsNode.setOffset( matchCorrectNode.getXOffset(), matchCorrectNode.getFullBoundsReference().getMaxY() + 5 );
        }

        // Wrong answer to "Match" question 
        {
            matchWrongNode = new MatchingGameAnswerNode( ABSStrings.GAME_MATCH_WRONG );

            matchWrongParent = new PhetPNode();
            matchWrongParent.addChild( matchWrongNode );

            matchWrongNode.setOffset( 0, 0 );
        }

        // parent for all questions and answers, to simplify layout
        questionsAndAnswersParent = new PhetPNode();
        questionsAndAnswersParent.addChild( acidBaseQuestionParent );
        questionsAndAnswersParent.addChild( acidBaseCorrectParent );
        questionsAndAnswersParent.addChild( acidBaseWrongParent );
        questionsAndAnswersParent.addChild( matchQuestionParent );
        questionsAndAnswersParent.addChild( matchCorrectParent );
        questionsAndAnswersParent.addChild( matchWrongParent );

        beakerNodeLeft = new BeakerNode( MatchingGameDefaults.BEAKER_SIZE, solutionLeft );
        beakerNodeLeft.setBeakerLabelVisible( false );
        beakerNodeRight = new BeakerNode( MatchingGameDefaults.BEAKER_SIZE, solutionRight );
        beakerNodeRight.setBeakerLabelVisible( false );

        graphNodeLeft = new ConcentrationGraphNode( MatchingGameDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solutionLeft );
        graphNodeRight = new ConcentrationGraphNode( MatchingGameDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solutionRight );

        solutionControlsNodeRight = new SolutionControlsNode( this, solutionRight );
        solutionControlsNodeRight.setSoluteComboBoxEnabled( false );
        solutionControlsNodeRight.scale( ABSConstants.PSWING_SCALE );

        viewControlsNode = new MatchingGameViewControlsNode( getBackground() );
        viewControlsNode.scale( ABSConstants.PSWING_SCALE );
        viewControlsNode.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent event ) {
                updateView();
            }
        } );

        cheatNode = new MatchingGameCheatNode( solutionLeft );
        
        // put everything related to the right solution under one parent, to simplify visibility control
        rightSolutionParent = new PhetPNode();
        rightSolutionParent.addChild( beakerNodeRight );
        rightSolutionParent.addChild( graphNodeRight );
        rightSolutionParent.addChild( solutionControlsNodeRight );

        addNode( scoreNode );
        addNode( newSolutionButton );
        addNode( questionsAndAnswersParent );
        addNode( beakerNodeLeft );
        addNode( graphNodeLeft );
        addNode( rightSolutionParent );
        addNode( viewControlsNode );
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addNode( cheatNode );
        }

        updateView();
        stateMachine.setStateAcidBaseQuestion();
    }

    public void reset() {
        stateMachine.setStateAcidBaseQuestion();
    }

    /*
     * Encapsulates the state changes in the game.
     * A description of the states follows...
     * 
     * We start with the "acid or base?" questions.
     * If answered incorrectly, a "wrong" message is displayed for N seconds,
     * and then we automatically return to the question for another attempt.
     * If answered correctly, a "correct" message is displayed for N seconds,
     * and then we automatically advance to the "match" question.
     * 
     * If the "match" question is answered incorrectly, a "wrong" message is displayed for N seconds,
     * and then we automatically return to the question for another attempt.
     * If answered correctly, a "correct" message is displayed, along with
     * instructions about how to continue the game.  The user must press the
     * "New Solution" button, which takes them back to the "acid or base?" question.
     * 
     * At either question, the user can press the "New Solution" button to create 
     * a new solution and return to the "acid or base?" question.
     * When the "correct" or "wrong" message is displayed, all controls are 
     * temporarily disabled.
     *
     */
    private class StateMachine {

        /*
         * Displays the "acid or base?" question.
         * Only the left solution is visible, and a random view is chosen.
         * Pressing the "Acid" or "Base" button advance to the "correct" or "wrong" state, as appropriate.
         */
        public void setStateAcidBaseQuestion() {
            
            // "acid or base?" question
            hideAllQuestionsAndAnswers();
            acidBaseQuestionParent.setVisible( true );
            
            // buttons 
            newSolutionButton.setEnabled( true );
            acidButton.setEnabled( true );
            baseButton.setEnabled( true );
            
            // left solution
            beakerNodeLeft.setProductMoleculeCountsVisible( false );
            beakerNodeLeft.setReactantMoleculeCountsVisible( false );
            graphNodeLeft.setReactantVisible( false );
            graphNodeLeft.setProductVisible( false );
            
            // right solution
            rightSolutionParent.setVisible( false );
            
            // view controls
            setRandomView();
        }

        /*
         * Sets a random view for the "acid or base" question.
         * Note that the pH probe is treated as a view,
         * and the solute ion ratio is not an option.
         */
        private void setRandomView() {

            // start with default state of all views off
            beakerNodeLeft.setProbeVisible( false );
            viewControlsNode.setSoluteComponentsRatioSelected( false );
            viewControlsNode.setHydroniumHydroxideRatioSelected( false );
            viewControlsNode.setMoleculeCountsSelected( false );
            viewControlsNode.setBeakersSelected( true );

            // randomly select a view
            Random r = new Random();
            int i = r.nextInt( 4 );
            switch ( i ) {
            case 0:
                beakerNodeLeft.setProbeVisible( true );
                break;
            case 1:
                viewControlsNode.setHydroniumHydroxideRatioSelected( true );
                break;
            case 2:
                viewControlsNode.setMoleculeCountsSelected( true );
                break;
            case 3:
                viewControlsNode.setGraphsSelected( true );
                break;
            }

            // random view can't be changed by the user
            viewControlsNode.setEnabled( false );
        }

        /*
         * Displays the "correct" message for the "acid or base?" question.
         * All controls are temporarily disabled.
         * After N seconds, we automatically advance to the "match" question.
         */
        public void setStateAcidBaseCorrect() {
            
            // "correct" message
            hideAllQuestionsAndAnswers();
            acidBaseCorrectNode.setText( formatCorrectWrongMessage( ABSStrings.GAME_ACIDBASE_CORRECT, model.getDeltaPoints() ) );
            acidBaseCorrectParent.setVisible( true );
            
            // buttons
            newSolutionButton.setEnabled( false );
            acidButton.setEnabled( false );
            baseButton.setEnabled( false );
            
            // pause, then advance automatically to next state
            waitCursor();
            Timer timer = new StateTimer( TIMER_DELAY );
            timer.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    setStateMatchQuestion();
                    defaultCursor();
                }
            } );
            timer.start();
        }

        /*
         * Displays the "wrong" message for the "acid or base?" question.
         * All controls are temporarily disabled.
         * After N seconds, we automatically return to the "acid or base?" question, for another attempt.
         */
        public void setStateAcidBaseWrong() {
            
            // "wrong" message
            hideAllQuestionsAndAnswers();
            acidBaseWrongNode.setText( formatCorrectWrongMessage( ABSStrings.GAME_ACIDBASE_WRONG, model.getDeltaPoints() ) );
            acidBaseWrongParent.setVisible( true );
            
            // buttons
            newSolutionButton.setEnabled( false );
            acidButton.setEnabled( false );
            baseButton.setEnabled( false );
            
            // pause, then advance automatically to next state
            waitCursor();
            Timer timer = new StateTimer( TIMER_DELAY );
            timer.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    setStateAcidBaseQuestion();
                    defaultCursor();
                }
            } );
            timer.start();
        }

        /*
         * Displays the "match" question.
         * Both the left and right solutions are visible, along with controls for the right solution.
         * The random view chosen for the "acid or base?" question is visible.
         * View controls are enabled so that the user can change the view.
         * Pressing the "Check match" button checks the match, and advances to the "correct"
         * or "wrong" state, as appropriate.
         */
        public void setStateMatchQuestion() {
            
            // "match" question
            hideAllQuestionsAndAnswers();
            matchQuestionParent.setVisible( true );
            
            // buttons
            newSolutionButton.setEnabled( true );
            matchButton.setEnabled( true );
            
            // left solution
            beakerNodeLeft.setProbeVisible( true );
            beakerNodeLeft.setProductMoleculeCountsVisible( true );
            beakerNodeLeft.setReactantMoleculeCountsVisible( true );
            graphNodeLeft.setReactantVisible( true );
            graphNodeLeft.setProductVisible( true );
            
            // right solution
            rightSolutionParent.setVisible( true );
            
            // controls
            viewControlsNode.setEnabled( true );
            solutionControlsNodeRight.setConcentrationControlEnabled( true );
            solutionControlsNodeRight.setStrengthControlEnabled( true );
        }

        /*
         * Displays the "correct" message for the "match" question.
         * Solution controls are disabled, so that the match can't be changed.
         * View controls are enabled so the user can continue to compare.
         * The user must explicitly press the "New Solution" button to change state.
         */
        public void setStateMatchCorrect() {
            
            // "correct" message
            hideAllQuestionsAndAnswers();
            matchCorrectNode.setText( formatCorrectWrongMessage( ABSStrings.GAME_MATCH_CORRECT, model.getDeltaPoints() ) );
            matchCorrectParent.setVisible( true );
            
            // buttons
            newSolutionButton.setEnabled( true );
            matchButton.setEnabled( false );
            
            // disable solution controls
            solutionControlsNodeRight.setConcentrationControlEnabled( false );
            solutionControlsNodeRight.setStrengthControlEnabled( false );
        }

        /*
         * Displays the "wrong" message for the "match" question.
         * All controls are temporarily disabled.
         * After N seconds, we automatically return to the "match" question, for another attempt.
         */
        public void setStateMatchWrong() {
            
            // "wrong" message
            hideAllQuestionsAndAnswers();
            matchWrongNode.setText( formatCorrectWrongMessage( ABSStrings.GAME_MATCH_WRONG, model.getDeltaPoints() ) );
            matchWrongParent.setVisible( true );
            
            // buttons
            newSolutionButton.setEnabled( false );
            matchButton.setEnabled( false );
            
            // disable solution controls
            solutionControlsNodeRight.setConcentrationControlEnabled( false );
            solutionControlsNodeRight.setStrengthControlEnabled( false );
            
            // pause, then advance automatically to next state
            waitCursor();
            Timer timer = new StateTimer( TIMER_DELAY );
            timer.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    setStateMatchQuestion();
                    solutionControlsNodeRight.setConcentrationControlEnabled( true );
                    solutionControlsNodeRight.setStrengthControlEnabled( true );
                    defaultCursor();
                }
            } );
            timer.start();
        }
    }
    
    /*
     * Swing timer that fires an action once.
     * Used to automatically advance to the next state.
     */
    private static class StateTimer extends Timer {

        public StateTimer( int delayMillis ) {
            super( delayMillis, (ActionListener) null );
            setRepeats( false );
        }
    }

    private void hideAllQuestionsAndAnswers() {
        acidBaseQuestionParent.setVisible( false );
        acidBaseCorrectParent.setVisible( false );
        acidBaseWrongParent.setVisible( false );
        matchQuestionParent.setVisible( false );
        matchCorrectParent.setVisible( false );
        matchWrongParent.setVisible( false );
    }

    private String formatCorrectWrongMessage( String pattern, int deltaPoints ) {
        // set the positive prefix so we don't get +0
        if ( deltaPoints > 0 ) {
            pointsFormat.setPositivePrefix( "+" );
        }
        else {
            pointsFormat.setPositivePrefix( "" );
        }
        String s = pointsFormat.format( deltaPoints );
        return MessageFormat.format( pattern, s );
    }

    private void waitCursor() {
        this.setCursor( WAIT_CURSOR );
    }

    private void defaultCursor() {
        this.setCursor( DEFAULT_CURSOR );
    }
    
    private void updateView() {
        beakerNodeLeft.setVisible( viewControlsNode.isBeakersSelected() );
        beakerNodeRight.setVisible( viewControlsNode.isBeakersSelected() );
        beakerNodeLeft.setSoluteComponentsRatioVisible( viewControlsNode.isSoluteComponentsRatioSelected() );
        beakerNodeRight.setSoluteComponentsRatioVisible( viewControlsNode.isSoluteComponentsRatioSelected() );
        beakerNodeLeft.setHydroniumHydroxideRatioVisible( viewControlsNode.isHydroniumHydroxideRatioSelected() );
        beakerNodeRight.setHydroniumHydroxideRatioVisible( viewControlsNode.isHydroniumHydroxideRatioSelected() );
        beakerNodeLeft.setMoleculeCountsVisible( viewControlsNode.isMoleculeCountsSelected() );
        beakerNodeRight.setMoleculeCountsVisible( viewControlsNode.isMoleculeCountsSelected() );
        graphNodeLeft.setVisible( viewControlsNode.isGraphsSelected() );
        graphNodeRight.setVisible( viewControlsNode.isGraphsSelected() );
    }

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( ABSConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( getClass().getName() + ".updateLayout worldSize=" + worldSize );
        }

        // start at (0,0), we'll adjust this globally with centerRootNode
        double xOffset = 0; 
        double yOffset = 0;

        // score in upper left
        scoreNode.setOffset( xOffset, yOffset );

        // "Next Solution" button below score
        xOffset = scoreNode.getFullBoundsReference().getMinX();
        yOffset = scoreNode.getFullBoundsReference().getMaxY() + 10;
        newSolutionButton.setOffset( xOffset, yOffset );

        // "Cheat" panel below "Next Solution" button
        xOffset = newSolutionButton.getXOffset();
        yOffset = newSolutionButton.getFullBoundsReference().getMaxY() + 10;
        cheatNode.setOffset( xOffset, yOffset );

        // questions and answers
        xOffset = Math.max( scoreNode.getFullBoundsReference().getMaxX(), newSolutionButton.getFullBoundsReference().getMaxX() ) + 75;
        yOffset = scoreNode.getYOffset();
        questionsAndAnswersParent.setOffset( xOffset, yOffset );

        // left beaker
        xOffset = -PNodeUtils.getOriginXOffset( beakerNodeLeft );
        yOffset = solutionControlsNodeRight.getFullBoundsReference().getHeight() - PNodeUtils.getOriginYOffset( beakerNodeLeft ) + 20;
        beakerNodeLeft.setOffset( xOffset, yOffset );

        // view controls, between beakers
        PNode resetAllButton = getResetAllButton();
        xOffset = beakerNodeLeft.getFullBoundsReference().getMaxX() + 10;
        yOffset = beakerNodeLeft.getFullBoundsReference().getMaxY() - viewControlsNode.getFullBoundsReference().getHeight() - resetAllButton.getFullBoundsReference().getHeight() - 15;
        viewControlsNode.setOffset( xOffset, yOffset );

        // right beaker
        xOffset = viewControlsNode.getFullBoundsReference().getMaxX() + 10 - PNodeUtils.getOriginXOffset( beakerNodeRight );
        yOffset = beakerNodeLeft.getYOffset();
        beakerNodeRight.setOffset( xOffset, yOffset );

        // solution controls
        xOffset = beakerNodeRight.getFullBoundsReference().getCenterX() - ( solutionControlsNodeRight.getFullBoundsReference().getWidth() / 2 ) - PNodeUtils.getOriginXOffset( solutionControlsNodeRight );
        yOffset = -PNodeUtils.getOriginYOffset( solutionControlsNodeRight );
        solutionControlsNodeRight.setOffset( xOffset, yOffset );

        // left graph
        xOffset = beakerNodeLeft.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( graphNodeLeft );
        yOffset = beakerNodeLeft.getFullBoundsReference().getMinY() - PNodeUtils.getOriginYOffset( graphNodeLeft );
        graphNodeLeft.setOffset( xOffset, yOffset );

        // right graph, left justified below right solution controls
        xOffset = beakerNodeRight.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( graphNodeRight );
        yOffset = beakerNodeRight.getFullBoundsReference().getMinY() - PNodeUtils.getOriginYOffset( graphNodeRight );
        graphNodeRight.setOffset( xOffset, yOffset );

        // Reset All button below view controls
        xOffset = viewControlsNode.getFullBoundsReference().getCenterX() - ( resetAllButton.getFullBoundsReference().getWidth() / 2 );
        yOffset = viewControlsNode.getFullBoundsReference().getMaxY() + 15;
        resetAllButton.setOffset( xOffset, yOffset );

        centerRootNode();
    }
}
