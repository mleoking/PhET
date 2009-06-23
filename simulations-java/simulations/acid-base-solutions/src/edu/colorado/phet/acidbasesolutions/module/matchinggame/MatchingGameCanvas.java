/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.control.PSwingButton;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
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
    
    public MatchingGameCanvas( MatchingGameModel model, Resettable resettable ) {
        super( resettable );
        
        AqueousSolution solutionLeft = model.getSolutionLeft();
        AqueousSolution solutionRight = model.getSolutionRight();
        
        scoreNode = new MatchingGameScoreNode();
        
        acidBaseQuestion = new MatchingGameQuestionNode( ABSStrings.QUESTION_ACID_OR_BASE );
        matchSolutionQuestion = new MatchingGameQuestionNode( ABSStrings.QUESTION_MATCH_SOLUTION );
        
        correctAnswer = new MatchingGameAnswerNode( ABSStrings.ANSWER_CORRECT );
        wrongAnswer = new MatchingGameAnswerNode( ABSStrings.ANSWER_WRONG );
        //XXX add listener
        
        newSolutionButton = new PSwingButton( ABSStrings.BUTTON_NEW_SOLUTION );
        newSolutionButton.scale( ABSConstants.PSWING_SCALE );
        //XXX add listener
        
        acidButton = new PSwingButton( ABSStrings.BUTTON_ACID );
        acidButton.scale( ABSConstants.PSWING_SCALE );
        //XXX add listener
        
        baseButton = new PSwingButton( ABSStrings.BUTTON_BASE );
        baseButton.scale( ABSConstants.PSWING_SCALE );
        //XXX add listener

        checkMatchButton = new PSwingButton( ABSStrings.BUTTON_CHECK_MATCH );
        checkMatchButton.scale( ABSConstants.PSWING_SCALE );
        //XXX add listener
        
        beakerNodeLeft = new BeakerNode( MatchingGameDefaults.BEAKER_SIZE, solutionLeft );
        beakerNodeRight = new BeakerNode( MatchingGameDefaults.BEAKER_SIZE, solutionRight );
        
        graphNodeLeft = new ConcentrationGraphNode( MatchingGameDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solutionLeft );
        graphNodeRight = new ConcentrationGraphNode( MatchingGameDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solutionRight );
        
        solutionControlsNodeRight = new SolutionControlsNode( this, solutionRight );
        solutionControlsNodeRight.setSoluteComboBoxEnabled( false );
        solutionControlsNodeRight.scale( ABSConstants.PSWING_SCALE );
        //XXX add listener
        
        viewControlsNode = new MatchingGameViewControlsNode( getBackground() );
        viewControlsNode.scale( ABSConstants.PSWING_SCALE );
        //XXX add listener
        
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
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
        
        // message 
        xOffset = Math.max( scoreNode.getFullBoundsReference().getMaxX(), newSolutionButton.getFullBoundsReference().getMaxX() ) + 30;
        yOffset = scoreNode.getYOffset();
        matchSolutionQuestion.setOffset( xOffset, yOffset );
        
        // "Check Match" button
        xOffset = matchSolutionQuestion.getXOffset();
        yOffset = Math.max( newSolutionButton.getYOffset(), matchSolutionQuestion.getFullBoundsReference().getMaxY() + 15 );
        checkMatchButton.setOffset( xOffset, yOffset );
        
        // Reset All button at bottom center
        PNode resetAllButton = getResetAllButton();
        xOffset = ( worldSize.getWidth() / 2 ) - ( resetAllButton.getFullBoundsReference().getWidth() / 2 );
        yOffset = worldSize.getHeight() - resetAllButton.getFullBounds().getHeight() - 20;
        resetAllButton.setOffset( xOffset , yOffset );
        
        centerRootNode();
    }
}
