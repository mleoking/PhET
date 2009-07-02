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
    private final MatchingGameAnswerNode acidBaseCorrectNode, acidBaseWrongNode;
    private final MatchingGameAnswerNode matchCorrectNode, matchWrongNode;
    private final HTMLNode continueInstructionsNode;
    
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
        
        pointsFormat = new DecimalFormat( "0" );
        
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
            PNode question = new MatchingGameQuestionNode( ABSStrings.GAME_ACIDBASE_QUESTION );

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
        // pH probe
        beakerNodeLeft.setProbeVisible( false );
        // counts
        beakerNodeLeft.setProductMoleculeCountsVisible( false );
        beakerNodeLeft.setReactantMoleculeCountsVisible( false );
        // graph
        graphNodeLeft.setReactantVisible( false );
        graphNodeLeft.setProductVisible( false );
        // view controls
        setRandomView();
        // solution views
        solutionControlsNodeRight.setVisible( false );
        beakerNodeRight.setVisible( false );
        graphNodeRight.setVisible( false );
    }
    
    /*
     * Sets a random view for the "acid or base" question.
     * Note that the pH probe is treated as a view,
     * and the disassociated components ratio is not an option.
     */
    private void setRandomView() {
        
        // start with default state of all views off
        beakerNodeLeft.setProbeVisible( false );
        viewControlsNode.setDissociatedComponentsRatioSelected( false );
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
    
    public void setStateAcidBaseCorrect() {
        // show "Correct"
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
        });
        timer.start();
    }
    
    public void setStateAcidBaseWrong() {
        // show "Wrong"
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
        // pH probe
        beakerNodeLeft.setProbeVisible( true );
        // counts
        beakerNodeLeft.setProductMoleculeCountsVisible( true );
        beakerNodeLeft.setReactantMoleculeCountsVisible( true );
        // graph
        graphNodeLeft.setReactantVisible( true );
        graphNodeLeft.setProductVisible( true );
        // view controls
        viewControlsNode.setEnabled( true );
        // solution views
        solutionControlsNodeRight.setVisible( true );
        beakerNodeRight.setVisible( viewControlsNode.isBeakersSelected() );
        graphNodeRight.setVisible( viewControlsNode.isGraphsSelected() );
        updateView(); // do this last!
    }
    
    public void setStateMatchCorrect() {
        // show "Correct"
        hideAllQuestionsAndAnswers();
        matchCorrectNode.setText( formatCorrectWrongMessage( ABSStrings.GAME_MATCH_CORRECT, model.getDeltaPoints() ) );
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
        matchWrongNode.setText( formatCorrectWrongMessage( ABSStrings.GAME_MATCH_WRONG, model.getDeltaPoints() ) );
        matchWrongParent.setVisible( true );
        // buttons
        newSolutionButton.setEnabled( false );
        matchButton.setEnabled( false );
        // freeze solution controls
        solutionControlsNodeRight.setAllControlsEnabled( false );
        // pause, then advance automatically to next state
        waitCursor();
        Timer timer = new StateTimer( TIMER_DELAY );
        timer.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setStateMatchQuestion();
                solutionControlsNodeRight.setAllControlsEnabled( true );
                defaultCursor();
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
    
    private String formatCorrectWrongMessage( String pattern, int deltaPoints) {
        if ( deltaPoints > 0 ) {
            pointsFormat.setPositivePrefix( "+" );
        }
        else {
            pointsFormat.setPositivePrefix( "" );
        }
        String s = pointsFormat.format( deltaPoints );
        return MessageFormat.format( pattern, s );
    }
    
    private static class StateTimer extends Timer {
        public StateTimer( int delayMillis ) {
            super( delayMillis, (ActionListener)null );
            setRepeats( false );
        }
    }
    
    private void waitCursor() {
        this.setCursor( WAIT_CURSOR );
    }
    
    private void defaultCursor() {
        this.setCursor( DEFAULT_CURSOR );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void updateView() {
        beakerNodeLeft.setVisible( viewControlsNode.isBeakersSelected() );
        beakerNodeRight.setVisible( viewControlsNode.isBeakersSelected() && solutionControlsNodeRight.isVisible() );
        beakerNodeLeft.setDisassociatedRatioComponentsVisible( viewControlsNode.isDissociatedComponentsRatioSelected() );
        beakerNodeRight.setDisassociatedRatioComponentsVisible( viewControlsNode.isDissociatedComponentsRatioSelected() && solutionControlsNodeRight.isVisible() );
        beakerNodeLeft.setHydroniumHydroxideRatioVisible( viewControlsNode.isHydroniumHydroxideRatioSelected() );
        beakerNodeRight.setHydroniumHydroxideRatioVisible( viewControlsNode.isHydroniumHydroxideRatioSelected() && solutionControlsNodeRight.isVisible() );
        beakerNodeLeft.setMoleculeCountsVisible( viewControlsNode.isMoleculeCountsSelected() );
        beakerNodeRight.setMoleculeCountsVisible( viewControlsNode.isMoleculeCountsSelected() && solutionControlsNodeRight.isVisible() );
        graphNodeLeft.setVisible( viewControlsNode.isGraphsSelected() );
        graphNodeRight.setVisible( viewControlsNode.isGraphsSelected() && solutionControlsNodeRight.isVisible() );
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
        xOffset = Math.max( scoreNode.getFullBoundsReference().getMaxX(), scoreNode.getFullBoundsReference().getMaxX() ) + 75;
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
