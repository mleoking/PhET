
package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.awt.geom.Dimension2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.controls.ReactionChoiceNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.RealReactionFormulaNode;


public class RealReactionCanvas extends RPALCanvas {

    private final RealReactionModel model;
    private final ReactionChoiceNode reactionChoiceNode;
    
    private RealReactionFormulaNode formulaNode;
    
    public RealReactionCanvas( final RealReactionModel model ) {
        super();
        
        this.model = model;
        model.addChangeListeners( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateNodes();
            }
        });
        
        reactionChoiceNode = new ReactionChoiceNode( model );
        reactionChoiceNode.scale( 1.25 );
        addChild( reactionChoiceNode );
        
        updateNodes();
    }
    

    private void updateNodes() {
        
        if ( formulaNode != null ) {
            removeChild( formulaNode );
        }
        formulaNode = new RealReactionFormulaNode( model.getReaction() );
        formulaNode.scale( 3 ); //XXX
        addChild( formulaNode );
        
        updateLayout();
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

        // formula at upper left
        double x = 0;
        double y = 0;
        formulaNode.setOffset( x, y );
        // radio buttons to right of formula
        x = formulaNode.getFullBoundsReference().getWidth() + 30;
        y = formulaNode.getFullBoundsReference().getCenterY() - ( reactionChoiceNode.getFullBoundsReference().getHeight() / 2 );
        reactionChoiceNode.setOffset( x, y );
        
        centerRootNode();
    }
}
