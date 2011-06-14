// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.sandwich.SandwichEquationNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.sandwich.SandwichShopAfterNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.sandwich.SandwichShopBeforeNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopCanvas extends RPALCanvas {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    
    private final SandwichShopModel model;
    
    // these nodes are final, allocated once
    private final SandwichChoiceNode sandwichChoiceNode;
    private final RightArrowNode arrowNode;
    private final PSwing resetAllButtonWrapper;

    // these nodes are mutable, allocated when reaction changes
    private SandwichEquationNode equationNode;
    private SandwichShopBeforeNode beforeNode;
    private SandwichShopAfterNode afterNode;
    
    public SandwichShopCanvas( SandwichShopModel model, Resettable resettable ) {
        super();
        
        this.model = model;
        
        sandwichChoiceNode = new SandwichChoiceNode( model );
        sandwichChoiceNode.scale( 1.25 );
        addChild( sandwichChoiceNode );
        
        arrowNode = new RightArrowNode();
        addChild( arrowNode );
        
        ResetAllButton resetAllButton = new ResetAllButton( resettable, this );
        resetAllButtonWrapper = new PSwing( resetAllButton );
        resetAllButtonWrapper.scale( 1.25 );
        addChild( resetAllButtonWrapper );
        
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateDynamicNodes();
            }
        } );
        
        updateDynamicNodes();
    }
    
    /*
     * Updates nodes that are "dynamic".
     * Dynamic nodes are replaced when the reaction changes.
     */
    private void updateDynamicNodes() {
        
        if ( equationNode != null ) {
            removeChild( equationNode );
            equationNode.cleanup();
        }
        equationNode = new SandwichEquationNode( model );
        addChild( equationNode );

        if ( beforeNode != null ) {
            removeChild( beforeNode );
            beforeNode.cleanup();
        }
        beforeNode = new SandwichShopBeforeNode( model, BOX_SIZE );
        addChild( beforeNode );

        if ( afterNode != null ) {
            removeChild( afterNode );
            afterNode.cleanup();
        }
        afterNode = new SandwichShopAfterNode( model, BOX_SIZE );
        addChild( afterNode );

        updateNodesLayout();
    }
    
    /*
     * Updates the layout of all nodes.
     */
    private void updateNodesLayout() {
        
        double x = 0;
        double y = 0;
        sandwichChoiceNode.setOffset( x, y );
        
        // equation below choices, left justified
        x = sandwichChoiceNode.getFullBoundsReference().getMinX();
        y = sandwichChoiceNode.getFullBoundsReference().getMaxY() + 20;
        equationNode.setOffset( x, y );
        
        // Before box below equation, left justified
        x = equationNode.getFullBoundsReference().getMinX();
        y = 180; // use a constant, the height of the equation changes based on sandwich complexity, and we don't want stuff below it to vertically shift
        beforeNode.setOffset( x, y );
        
        // arrow to the right of Before box, vertically centered with box
        final double arrowXSpacing = 20;
        x = beforeNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset() + ( BOX_SIZE.getHeight() / 2 );
        arrowNode.setOffset( x, y );
        
        // After box to the right of arrow, top aligned with Before box
        x = arrowNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset();
        afterNode.setOffset( x, y );
        
        // Reset All button at bottom center, cheated toward Before box
        x = arrowNode.getFullBoundsReference().getMaxX() - resetAllButtonWrapper.getFullBoundsReference().getWidth();
        y = afterNode.getFullBoundsReference().getMaxY();
        resetAllButtonWrapper.setOffset( x, y );
    }
}
