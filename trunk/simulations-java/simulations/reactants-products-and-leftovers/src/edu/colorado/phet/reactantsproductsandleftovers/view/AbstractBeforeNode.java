package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ReactantQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all "Before Reaction" displays.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractBeforeNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    private static final double TITLE_Y_SPACING = 10;
    private static final double LEFT_MARGIN = 80;
    private static final double RIGHT_MARGIN = 60;
    private static final double CONTROLS_Y_SPACING = 15;
    private static final double IMAGES_Y_MARGIN = 18;
    private static final double IMAGES_Y_SPACING = 27;
    private static final double IMAGE_SCALE = 0.25; //XXX
    
    private final ChemicalReaction reaction;
    private final ChangeListener reactionChangeListener;

    private final BoxNode boxNode;
    private final ArrayList<PComposite> substanceNodeParents; // parents for reactant images
    private final ArrayList<ArrayList<SubstanceNode>> substanceNodeLists; // one list of images per reactant
    private final ArrayList<ReactantQuantityControlNode> quantityControlNodes; // quantity displays for reactants
    
    public AbstractBeforeNode( String title, final ChemicalReaction reaction, IntegerRange quantityRange ) {
        super();
        
        this.reaction = reaction;
        reactionChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        reaction.addChangeListener( reactionChangeListener );
        
        substanceNodeParents = new ArrayList<PComposite>();
        substanceNodeLists = new ArrayList<ArrayList<SubstanceNode>>();
        quantityControlNodes = new ArrayList<ReactantQuantityControlNode>();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        PText titleNode = new PText( title );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // images and controls
        ArrayList<Reactant> reactants = reaction.getReactantsReference();
        for ( Reactant reactant : reactants ) {
            
            // one parent node for each reactant image
            PComposite parent = new PComposite();
            addChild( parent );
            substanceNodeParents.add( parent );
            
            // one list of image nodes for each reactant 
            substanceNodeLists.add( new ArrayList<SubstanceNode>() );
            
            // one quantity control for each reactant
            ReactantQuantityControlNode controlNode = new ReactantQuantityControlNode( reactant, quantityRange, IMAGE_SCALE );
            addChild( controlNode );
            quantityControlNodes.add( controlNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        boxNode.setOffset( x, y );
        // title centered above box
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - TITLE_Y_SPACING;
        titleNode.setOffset( x, y );
        // reactant-specific nodes
        x = boxNode.getFullBoundsReference().getMinX() + LEFT_MARGIN;
        double deltaX = boxNode.getFullBoundsReference().getWidth() - LEFT_MARGIN - RIGHT_MARGIN;
        if ( reactants.size() > 2 ) {
            deltaX = ( boxNode.getFullBoundsReference().getWidth() - LEFT_MARGIN - RIGHT_MARGIN ) / ( reactants.size() - 1 );
        }
        for ( int i = 0; i < reactants.size(); i++ ) {
            
            // quantity controls
            y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
            quantityControlNodes.get( i ).setOffset( x, y );
            
            // images, centered above controls
            y = boxNode.getFullBoundsReference().getMaxY() - IMAGES_Y_MARGIN;
            substanceNodeParents.get( i ).setOffset( x, y );
            
            x += deltaX;
        }
        
        update();
    }
    
    /**
     * Cleans up all listeners that could cause memory leaks.
     */
    public void cleanup() {
        reaction.removeChangeListener( reactionChangeListener );
        // controls that are listening to reactants
        for ( ReactantQuantityControlNode node : quantityControlNodes ) {
            node.cleanup();
        }
        // images that are listening to reactants
        for ( ArrayList<SubstanceNode> list : substanceNodeLists ) {
            for ( SubstanceNode node : list ) {
                node.cleanup();
            }
        }
    }
    
    /*
     * For each reactant, update quantity control and number of images to match the quantity.
     */
    private void update() {
        
        ArrayList<Reactant> reactants = reaction.getReactantsReference();
        for ( int i = 0; i < reactants.size(); i++ ) {
            
            Reactant reactant = reactants.get( i );
            PNode parent = substanceNodeParents.get( i );
            ArrayList<SubstanceNode> images = substanceNodeLists.get( i );
            
            if ( reactant.getQuantity() < images.size() ) {
                // remove images
                while ( reactant.getQuantity() < images.size() ) {
                    SubstanceNode node = images.get( images.size() - 1 );
                    node.cleanup();
                    parent.removeChild( node );
                    images.remove( node );
                }
            }
            else {
                // add images
                while( reactant.getQuantity() > images.size() ) {
                    SubstanceNode node = new SubstanceNode( reactant );
                    node.scale( IMAGE_SCALE );
                    parent.addChild( node );
                    images.add( node );
                    // images are vertically stacked
                    double x = -node.getFullBoundsReference().getWidth() / 2;
                    if ( parent.getChildrenCount() > 1 ) {
                        double y = parent.getChild( parent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - PNodeLayoutUtils.getOriginYOffset( node ) - IMAGES_Y_SPACING;
                        node.setOffset( x, y );
                    }
                    else {
                        double y = -PNodeLayoutUtils.getOriginYOffset( node ) - IMAGES_Y_SPACING;
                        node.setOffset( x, y );
                    }
                }
            }
        }
    }
    
}
