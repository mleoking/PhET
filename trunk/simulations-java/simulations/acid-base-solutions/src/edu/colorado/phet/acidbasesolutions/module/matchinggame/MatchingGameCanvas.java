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

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View
    private final MatchingGameScoreNode scoreNode;
    private final BeakerNode beakerNodeLeft, beakerNodeRight;
    private final ConcentrationGraphNode graphNodeLeft, graphNodeRight;
    
    // Controls
    private final PSwingButton newSolutionButton;
    private final SolutionControlsNode solutionControlsNodeRight;
    private final MatchingGameViewControlsNode viewControlsNode;
    
    // Parent nodes, for changing visibility based on game state
    private final PhetPNode acidBaseQuestionParent;
    private final PhetPNode matchQuestionParent;
    private final PhetPNode correctParent;
    private final PhetPNode wrongParent;
    
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
                setModeAcidBaseQuestion();
            }
        });
        
        // "Acid or Base" question and related controls
        {
            PNode question = new MatchingGameQuestionNode( ABSStrings.QUESTION_ACID_OR_BASE );

            PSwingButton acidButton = new PSwingButton( ABSStrings.BUTTON_ACID );
            acidButton.scale( ABSConstants.PSWING_SCALE );
            acidButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkAcid();
                    if ( success ) {
                        setModeMatchQuestion();
                    }
                }
            } );

            PSwingButton baseButton = new PSwingButton( ABSStrings.BUTTON_BASE );
            baseButton.scale( ABSConstants.PSWING_SCALE );
            baseButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkBase();
                    if ( success ) {
                        setModeMatchQuestion();
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

        // "Match" question and related controls
        {
            PNode question = new MatchingGameQuestionNode( ABSStrings.QUESTION_MATCH_SOLUTION );
            
            PSwingButton checkMatchButton = new PSwingButton( ABSStrings.BUTTON_CHECK_MATCH );
            checkMatchButton.scale( ABSConstants.PSWING_SCALE );
            checkMatchButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    boolean success = model.checkMatch();
                    if ( success ) {
                        model.newSolution();
                        setModeAcidBaseQuestion();
                    }
                }
            } );
            
            matchQuestionParent = new PhetPNode();
            matchQuestionParent.addChild( question );
            matchQuestionParent.addChild( checkMatchButton );
            
            question.setOffset( 0, 0 );
            checkMatchButton.setOffset( 0, question.getFullBoundsReference().getMaxY() + 10 );
        }
        
        // "Correct" answer
        {
            PNode answer = new MatchingGameAnswerNode( ABSStrings.ANSWER_CORRECT );
            
            correctParent = new PhetPNode();
            correctParent.addChild( answer );
            
            answer.setOffset( 0, 0 );
        }
        
        // "Wrong" answer
        {
            PNode answer = new MatchingGameAnswerNode( ABSStrings.ANSWER_WRONG );
            
            wrongParent = new PhetPNode();
            wrongParent.addChild( answer );
            
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
        addNode( matchQuestionParent );
        addNode( correctParent );
        addNode( wrongParent );
        
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
        setModeAcidBaseQuestion();
    }
    
    public void setModeAcidBaseQuestion() {
        // question & answer area
        acidBaseQuestionParent.setVisible( true );
        matchQuestionParent.setVisible( false );
        correctParent.setVisible( false );
        wrongParent.setVisible( false );
        // view controls
        viewControlsNode.setModeAcidBaseQuestion();
        viewControlsNode.setBeakersSelected( true );
        viewControlsNode.setDissociatedComponentsRatioSelected( false );
        viewControlsNode.setHydroniumHydroxideRatioSelected( true );
        viewControlsNode.setMoleculeCountsSelected( false );
        // solution views
        solutionControlsNodeRight.setVisible( false );
        beakerNodeRight.setVisible( false );
        graphNodeRight.setVisible( false );
    }
    
    public void setModeMatchQuestion() {
        // question & answer area
        acidBaseQuestionParent.setVisible( false );
        matchQuestionParent.setVisible( true );
        correctParent.setVisible( false );
        wrongParent.setVisible( false );
        // view controls
        viewControlsNode.setModeMatchSolutionQuestion();
        // solution views
        solutionControlsNodeRight.setVisible( true );
        beakerNodeRight.setVisible( viewControlsNode.isBeakersSelected() );
        graphNodeRight.setVisible( viewControlsNode.isGraphsSelected() );
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
        matchQuestionParent.setOffset( xOffset, yOffset );
        correctParent.setOffset( xOffset, yOffset );
        wrongParent.setOffset( xOffset, yOffset );
        
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
