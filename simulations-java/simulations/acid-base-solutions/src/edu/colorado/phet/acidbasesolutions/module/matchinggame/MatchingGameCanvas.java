/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.control.PSwingButton;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.view.MatchingGameMessageNode;
import edu.colorado.phet.acidbasesolutions.view.MatchingGameScoreNode;
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
    private final MatchingGameMessageNode messageNode;
    
    // Controls
    private final PSwingButton newSolutionButton;
    private final PSwingButton checkMatchButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MatchingGameCanvas( MatchingGameModel model, Resettable resettable ) {
        super( resettable );
        
        scoreNode = new MatchingGameScoreNode();
        
        messageNode = new MatchingGameMessageNode();
        messageNode.setText( ABSStrings.MESSAGE_MATCH );
        
        newSolutionButton = new PSwingButton( ABSStrings.BUTTON_NEW_SOLUTION );
        newSolutionButton.scale( ABSConstants.PSWING_SCALE );
        
        checkMatchButton = new PSwingButton( ABSStrings.BUTTON_CHECK_MATCH );
        checkMatchButton.scale( ABSConstants.PSWING_SCALE );
        
        addNode( scoreNode );
        addNode( newSolutionButton );
        addNode( messageNode );
        addNode( checkMatchButton );
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
        messageNode.setOffset( xOffset, yOffset );
        
        // "Check Match" button
        xOffset = messageNode.getXOffset();
        yOffset = Math.max( newSolutionButton.getYOffset(), messageNode.getFullBoundsReference().getMaxY() + 15 );
        checkMatchButton.setOffset( xOffset, yOffset );
        
        // Reset All button at bottom center
        PNode resetAllButton = getResetAllButton();
        xOffset = ( worldSize.getWidth() / 2 ) - ( resetAllButton.getFullBoundsReference().getWidth() / 2 );
        yOffset = worldSize.getHeight() - resetAllButton.getFullBounds().getHeight() - 20;
        resetAllButton.setOffset( xOffset , yOffset );
        
        centerRootNode();
    }
}
