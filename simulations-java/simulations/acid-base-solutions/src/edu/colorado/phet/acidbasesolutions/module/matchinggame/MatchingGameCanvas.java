/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;

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
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * MatchingGameCanvas is the canvas for MatchingGameModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameCanvas extends ABSAbstractCanvas {
    
    private static final int CORRECT_TIMER_DELAY = 2000; // ms
    private static final int WRONG_TIMER_DELAY = 2000; // ms

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View
    private final MatchingGameScoreNode scoreNode;
    private final BeakerNode beakerNodeLeft, beakerNodeRight;
    private final ConcentrationGraphNode graphNodeLeft, graphNodeRight;
    
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
    
    // Dev
    private final PNode cheatNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MatchingGameCanvas( final MatchingGameModel model, Resettable resettable ) {
        super( resettable );
        
        AqueousSolution solutionLeft = model.getSolutionLeft();
        AqueousSolution solutionRight = model.getSolutionRight();
        
        scoreNode = new MatchingGameScoreNode( model );
        
        newSolutionButton = new PSwingButton( ABSStrings.BUTTON_NEW_SOLUTION );
        newSolutionButton.scale( ABSConstants.PSWING_SCALE );
        newSolutionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.newSolution();
                setStateAcidBaseQuestion();
            }
        });
        
        // "Acid or Base" question and related controls
        {
            PNode question = new MatchingGameQuestionNode( ABSStrings.QUESTION_ACIDBASE );

            acidButton = new PSwingButton( ABSStrings.BUTTON_ACID );
            acidButton.scale( ABSConstants.PSWING_SCALE );
            acidButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkAcid();
                    if ( success ) {
                        setStateAcidBaseCorrect();
                    }
                    else {
                        setStateAcidBaseWrong();
                    }
                }
            } );

            baseButton = new PSwingButton( ABSStrings.BUTTON_BASE );
            baseButton.scale( ABSConstants.PSWING_SCALE );
            baseButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkBase();
                    if ( success ) {
                        setStateAcidBaseCorrect();
                    }
                    else {
                        setStateAcidBaseWrong();
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
            PNode answer = new MatchingGameAnswerNode( ABSStrings.CORRECT_ACIDBASE );
            
            acidBaseCorrectParent = new PhetPNode();
            acidBaseCorrectParent.addChild( answer );
            
            answer.setOffset( 0, 0 );
        }
        
        // Wrong answer to "Acid or Base" question 
        {
            PNode answer = new MatchingGameAnswerNode( ABSStrings.WRONG_ACIDBASE );
            
            acidBaseWrongParent = new PhetPNode();
            acidBaseWrongParent.addChild( answer );
            
            answer.setOffset( 0, 0 );
        }
        
        // "Match" question and related controls
        {
            PNode question = new MatchingGameQuestionNode( ABSStrings.QUESTION_MATCH );
            
            matchButton = new PSwingButton( ABSStrings.BUTTON_CHECK_MATCH );
            matchButton.scale( ABSConstants.PSWING_SCALE );
            matchButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkMatch();
                    if ( success ) {
                        model.newSolution();
                        setStateMatchCorrect();
                    }
                    else {
                        setStateMatchWrong();
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
            PNode answer = new MatchingGameAnswerNode( ABSStrings.CORRECT_MATCH );
            
            matchCorrectParent = new PhetPNode();
            matchCorrectParent.addChild( answer );
            
            answer.setOffset( 0, 0 );
        }
        
        // Wrong answer to "Match" question 
        {
            PNode answer = new MatchingGameAnswerNode( ABSStrings.WRONG_MATCH );
            
            matchWrongParent = new PhetPNode();
            matchWrongParent.addChild( answer );
            
            answer.setOffset( 0, 0 );
        }
        
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
        });
        
        cheatNode = new MatchingGameCheatNode( solutionLeft );
        
        addNode( scoreNode );
        addNode( newSolutionButton );
        addNode( acidBaseQuestionParent );
        addNode( acidBaseCorrectParent );
        addNode( acidBaseWrongParent );
        addNode( matchQuestionParent );
        addNode( matchCorrectParent );
        addNode( matchWrongParent );
        
        addNode( beakerNodeLeft );
        addNode( beakerNodeRight );
        addNode( graphNodeLeft );
        addNode( graphNodeRight );
        addNode( solutionControlsNodeRight );
        addNode( viewControlsNode );
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addNode( cheatNode );
        }
        
        updateView();
        setStateAcidBaseQuestion();
    }
    
    public void reset() {
        setStateAcidBaseQuestion();
    }
    
    public void setStateAcidBaseQuestion() {
        // show the "Acid or Base" question
        hideAllQuestionsAndAnswers();
        acidBaseQuestionParent.setVisible( true );
        // buttons 
        newSolutionButton.setEnabled( true );
        acidButton.setEnabled( true );
        baseButton.setEnabled( true );
        // view controls
        viewControlsNode.setModeAcidBaseQuestion();
        // solution views
        solutionControlsNodeRight.setVisible( false );
        beakerNodeRight.setVisible( false );
        graphNodeRight.setVisible( false );
    }
    
    public void setStateAcidBaseCorrect() {
        // show "Correct"
        hideAllQuestionsAndAnswers();
        acidBaseCorrectParent.setVisible( true );
        // buttons
        newSolutionButton.setEnabled( false );
        acidButton.setEnabled( false );
        baseButton.setEnabled( false );
        // pause, then advance automatically to next state
        Timer timer = new StateTimer( CORRECT_TIMER_DELAY );
        timer.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setStateMatchQuestion();
            }
        });
        timer.start();
    }
    
    public void setStateAcidBaseWrong() {
        // show "Wrong"
        hideAllQuestionsAndAnswers();
        acidBaseWrongParent.setVisible( true );
        // buttons
        newSolutionButton.setEnabled( false );
        acidButton.setEnabled( false );
        baseButton.setEnabled( false );
        // pause, then advance automatically to next state
        Timer timer = new StateTimer( WRONG_TIMER_DELAY );
        timer.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setStateAcidBaseQuestion();
            }
        });
        timer.start();
    }
    
    public void setStateMatchQuestion() {
        // show "Match" question
        hideAllQuestionsAndAnswers();
        matchQuestionParent.setVisible( true );
        // buttons
        newSolutionButton.setEnabled( true );
        matchButton.setEnabled( true );
        // view controls
        viewControlsNode.setModeMatchSolutionQuestion();
        // solution views
        solutionControlsNodeRight.setVisible( true );
        beakerNodeRight.setVisible( viewControlsNode.isBeakersSelected() );
        graphNodeRight.setVisible( viewControlsNode.isGraphsSelected() );
    }
    
    public void setStateMatchCorrect() {
        // show "Correct"
        hideAllQuestionsAndAnswers();
        matchCorrectParent.setVisible( true );
        // buttons
        newSolutionButton.setEnabled( true );
        matchButton.setEnabled( false );
        // freeze solution controls
        solutionControlsNodeRight.setAllControlsEnabled( false );
    }
    
    public void setStateMatchWrong() {
        // show "Wrong"
        hideAllQuestionsAndAnswers();
        matchWrongParent.setVisible( true );
        // buttons
        newSolutionButton.setEnabled( false );
        matchButton.setEnabled( false );
        // freeze solution controls
        solutionControlsNodeRight.setAllControlsEnabled( false );
        // pause, then advance automatically to next state
        Timer timer = new StateTimer( WRONG_TIMER_DELAY );
        timer.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setStateMatchQuestion();
                solutionControlsNodeRight.setAllControlsEnabled( true );
            }
        });
        timer.start();
    }
    
    private void hideAllQuestionsAndAnswers() {
        acidBaseQuestionParent.setVisible( false );
        acidBaseCorrectParent.setVisible( false );
        acidBaseWrongParent.setVisible( false );
        matchQuestionParent.setVisible( false );
        matchCorrectParent.setVisible( false );
        matchWrongParent.setVisible( false );
    }
    
    private static class StateTimer extends Timer {
        public StateTimer( int delayMillis ) {
            super( delayMillis, (ActionListener)null );
            setRepeats( false );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void updateView() {
        beakerNodeLeft.setVisible( viewControlsNode.isBeakersSelected() );
        beakerNodeRight.setVisible( viewControlsNode.isBeakersSelected() );
        beakerNodeLeft.setDisassociatedRatioComponentsVisible( viewControlsNode.isDissociatedComponentsRatioSelected() );
        beakerNodeRight.setDisassociatedRatioComponentsVisible( viewControlsNode.isDissociatedComponentsRatioSelected() );
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
        
        double xOffset = 0;
        double yOffset = 15;
        
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
        xOffset = Math.max( scoreNode.getFullBoundsReference().getMaxX(), newSolutionButton.getFullBoundsReference().getMaxX() ) + 30;
        yOffset = scoreNode.getYOffset();
        acidBaseQuestionParent.setOffset( xOffset, yOffset );
        acidBaseCorrectParent.setOffset( xOffset, yOffset );
        acidBaseWrongParent.setOffset( xOffset, yOffset );
        matchQuestionParent.setOffset( xOffset, yOffset );
        matchCorrectParent.setOffset( xOffset, yOffset );
        matchWrongParent.setOffset( xOffset, yOffset );
        
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
        resetAllButton.setOffset( xOffset , yOffset );
        
        centerRootNode();
    }
}
