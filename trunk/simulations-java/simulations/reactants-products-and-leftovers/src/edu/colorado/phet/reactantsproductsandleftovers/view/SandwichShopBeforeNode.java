package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ReactantQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichShopBeforeNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    private static final double TITLE_Y_SPACING = 10;
    private static final double LEFT_MARGIN = 80;
    private static final double RIGHT_MARGIN = 60;
    private static final double CONTROLS_Y_SPACING = 15;
    private static final double IMAGES_Y_MARGIN = 18;
    private static final double IMAGES_Y_SPACING = 27;
    private static final double IMAGE_SCALE = 0.25; //XXX
    
    private final SandwichShopModel model;

    private final BoxNode boxNode;
    private final ArrayList<PComposite> substanceNodeParents;
    private final ArrayList<ArrayList<SubstanceNode>> substanceNodeLists; // one list per reactant
    private final ArrayList<ReactantQuantityControlNode> quantityControls;
    
    public SandwichShopBeforeNode( final SandwichShopModel model ) {
        super();
        
        this.model = model;
        model.getReaction().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        substanceNodeParents = new ArrayList<PComposite>();
        substanceNodeLists = new ArrayList<ArrayList<SubstanceNode>>();
        quantityControls = new ArrayList<ReactantQuantityControlNode>();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        PText titleNode = new PText( RPALStrings.LABEL_BEFORE_SANDWICH );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // images and controls
        ArrayList<Reactant> reactants = model.getReaction().getReactantsReference();
        for ( Reactant reactant : reactants ) {
            
            // one parent node for each reactant image
            PComposite parent = new PComposite();
            addChild( parent );
            substanceNodeParents.add( parent );
            
            // one list of image nodes for each reactant 
            substanceNodeLists.add( new ArrayList<SubstanceNode>() );
            
            // one quantity control for each reactant
            ReactantQuantityControlNode controlNode = new ReactantQuantityControlNode( reactant, SandwichShopDefaults.QUANTITY_RANGE, IMAGE_SCALE );
            addChild( controlNode );
            quantityControls.add( controlNode );
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
            
            // quantity controls, centered below images
            y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
            quantityControls.get( i ).setOffset( x, y );
            
            // images
            y = boxNode.getFullBoundsReference().getMaxY() - IMAGES_Y_MARGIN;
            substanceNodeParents.get( i ).setOffset( x, y );
            
            x += deltaX;
        }
        
        update();
    }
    
    /*
     * For each reactant, updates the number of images to match the quantity.
     */
    private void update() {
        
        ArrayList<Reactant> reactants = model.getReaction().getReactantsReference();
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
                    node.scale( IMAGE_SCALE ); //XXX
                    parent.addChild( node );
                    images.add( node );
                    // images are vertically stacked
                    double x = -node.getFullBoundsReference().getWidth() / 2;
                    if ( parent.getChildrenCount() > 1 ) {
                        double y = parent.getChild( parent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - IMAGES_Y_SPACING;
                        node.setOffset( x, y );
                    }
                    else {
                        double y = -IMAGES_Y_SPACING;
                        node.setOffset( x, y );
                    }
                }
            }
        }
    }
    
}
