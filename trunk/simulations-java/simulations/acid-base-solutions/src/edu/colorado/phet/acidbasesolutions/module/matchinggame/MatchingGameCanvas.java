/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;

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
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.PNode;

/**
 * MatchingGameCanvas is the canvas for MatchingGameModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameCanvas extends ABSAbstractCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View
    private final MatchingGameScoreNode scoreNode;
    private final MatchingGameQuestionNode acidBaseQuestion;
    private final MatchingGameQuestionNode matchSolutionQuestion;
    private final MatchingGameAnswerNode correctAnswer;
    private final MatchingGameAnswerNode wrongAnswer;
    private final BeakerNode beakerNodeLeft, beakerNodeRight;
    private final ConcentrationGraphNode graphNodeLeft, graphNodeRight;
    
    // Controls
    private final PSwingButton newSolutionButton;
    private final PSwingButton acidButton, baseButton;
    private final PSwingButton checkMatchButton;
    private final SolutionControlsNode solutionControlsNodeRight;
    private final MatchingGameViewControlsNode viewControlsNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MatchingGameCanvas( final MatchingGameModel model, Resettable resettable ) {
        super( resettable );
        
        AqueousSolution solutionLeft = model.getSolutionLeft();
        AqueousSolution solutionRight = model.getSolutionRight();
        
        scoreNode = new MatchingGameScoreNode( model );
        
        acidBaseQuestion = new MatchingGameQuestionNode( ABSStrings.QUESTION_ACID_OR_BASE );
        matchSolutionQuestion = new MatchingGameQuestionNode( ABSStrings.QUESTION_MATCH_SOLUTION );
        
        correctAnswer = new MatchingGameAnswerNode( ABSStrings.ANSWER_CORRECT );
        wrongAnswer = new MatchingGameAnswerNode( ABSStrings.ANSWER_WRONG );
        
        newSolutionButton = new PSwingButton( ABSStrings.BUTTON_NEW_SOLUTION );
        newSolutionButton.scale( ABSConstants.PSWING_SCALE );
        newSolutionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.newSolution();
            }
        });
        
        acidButton = new PSwingButton( ABSStrings.BUTTON_ACID );
        acidButton.scale( ABSConstants.PSWING_SCALE );
        acidButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean success = model.checkAcid();
                //TODO change state based on success
            }
        } );
        
        baseButton = new PSwingButton( ABSStrings.BUTTON_BASE );
        baseButton.scale( ABSConstants.PSWING_SCALE );
        baseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean success = model.checkBase();
                //TODO change state based on success
            }
        } );

        checkMatchButton = new PSwingButton( ABSStrings.BUTTON_CHECK_MATCH );
        checkMatchButton.scale( ABSConstants.PSWING_SCALE );
        checkMatchButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean success = model.checkMatch();
                //TODO change state based on success
            }
        } );
        
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
        
        addNode( scoreNode );
        addNode( newSolutionButton );
        
        addNode( acidBaseQuestion );
        addNode( acidButton );
        addNode( baseButton );
        
        addNode( matchSolutionQuestion );
        addNode( checkMatchButton );
        
        addNode( correctAnswer );
        addNode( wrongAnswer );
        
        addNode( beakerNodeLeft );
        addNode( beakerNodeRight );
        addNode( graphNodeLeft );
        addNode( graphNodeRight );
        addNode( solutionControlsNodeRight );
        addNode( viewControlsNode );
        
        updateView();
    }
    
    public void setModeAcidBaseQuestion() {
        viewControlsNode.setModeAcidBaseQuestion();
        viewControlsNode.setBeakersSelected( true );
        viewControlsNode.setDissociatedComponentsRatioSelected( false );
        viewControlsNode.setHydroniumHydroxideRatioSelected( true );
        viewControlsNode.setMoleculeCountsSelected( false );
        acidBaseQuestion.setVisible( true );
        acidButton.setVisible( true );
        baseButton.setVisible( true );
        matchSolutionQuestion.setVisible( false );
        checkMatchButton.setVisible( false );
        correctAnswer.setVisible( false );
        wrongAnswer.setVisible( false );
        beakerNodeRight.setVisible( false );
        graphNodeRight.setVisible( false );
        solutionControlsNodeRight.setVisible( false );
    }
    
    public void setModeMatchSolutionQuestion() {
        viewControlsNode.setModeMatchSolutionQuestion();
        acidBaseQuestion.setVisible( false );
        acidButton.setVisible( false );
        baseButton.setVisible( false );
        matchSolutionQuestion.setVisible( true );
        checkMatchButton.setVisible( true );
        correctAnswer.setVisible( false );
        wrongAnswer.setVisible( false );
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
        
        // questions and answers
        xOffset = Math.max( scoreNode.getFullBoundsReference().getMaxX(), newSolutionButton.getFullBoundsReference().getMaxX() ) + 30;
        yOffset = scoreNode.getYOffset();
        acidBaseQuestion.setOffset( xOffset, yOffset );
        matchSolutionQuestion.setOffset( xOffset, yOffset );
        correctAnswer.setOffset( xOffset, yOffset );
        wrongAnswer.setOffset( xOffset, yOffset );
        
        // "Acid" and "Base" buttons
        xOffset = acidBaseQuestion.getXOffset();
        yOffset = acidBaseQuestion.getFullBoundsReference().getMaxY() + 15;
        acidButton.setOffset( xOffset, yOffset );
        xOffset = acidButton.getFullBoundsReference().getMaxX() + 10;
        baseButton.setOffset( xOffset, yOffset );
        
        // "Check Match" button
        xOffset = matchSolutionQuestion.getXOffset();
        yOffset = matchSolutionQuestion.getFullBoundsReference().getMaxY() + 15;
        checkMatchButton.setOffset( xOffset, yOffset );
        
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
