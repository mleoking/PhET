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
import edu.colorado.phet.reactantsproductsandleftovers.controls.LeftoversDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all "After Reaction" displays.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractAfterNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    private static final double TITLE_Y_SPACING = 10;
    private static final double LEFT_MARGIN = 50;
    private static final double RIGHT_MARGIN = LEFT_MARGIN;
    private static final double CONTROLS_Y_SPACING = 15;
    private static final double IMAGES_Y_MARGIN = 18;
    private static final double IMAGES_Y_SPACING = 27;
    private static final double LEFTOVERS_BRACKET_Y_SPACING = 3;
    private static final double IMAGE_SCALE = 0.25; //XXX
    
    private final ChemicalReaction reaction;
    private final ChangeListener reactionChangeListener;

    private final BoxNode boxNode;
    private final ArrayList<PComposite> productNodeParents, reactantNodeParents; // parents for product and reactant images
    private final ArrayList<ArrayList<SubstanceNode>> productNodeLists, reactantNodeLists; // one list of images per product and reactant
    private final ArrayList<QuantityDisplayNode> productQuantityDisplayNodes; // quantity displays for products
    private final ArrayList<LeftoversDisplayNode> reactantLeftoverDisplayNodes; // leftovers displays for reactants
    
    public AbstractAfterNode( String title, final ChemicalReaction reaction, IntegerRange quantityRange ) {
        super();
        
        this.reaction = reaction;
        reactionChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        reaction.addChangeListener( reactionChangeListener );
        
        productNodeParents = new ArrayList<PComposite>();
        reactantNodeParents = new ArrayList<PComposite>();
        productNodeLists = new ArrayList<ArrayList<SubstanceNode>>();
        reactantNodeLists = new ArrayList<ArrayList<SubstanceNode>>();
        productQuantityDisplayNodes = new ArrayList<QuantityDisplayNode>();
        reactantLeftoverDisplayNodes = new ArrayList<LeftoversDisplayNode>();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        PText titleNode = new PText( title );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // product images and quantity displays
        Product[] products = reaction.getProducts();
        for ( Product product : products ) {
            
            // one parent node for each product image
            PComposite parent = new PComposite();
            addChild( parent );
            productNodeParents.add( parent );
            
            // one list of image nodes for each product 
            productNodeLists.add( new ArrayList<SubstanceNode>() );
            
            // one quantity display for each product
            QuantityDisplayNode displayNode = new QuantityDisplayNode( product, quantityRange, IMAGE_SCALE );
            addChild( displayNode );
            productQuantityDisplayNodes.add( displayNode );
        }
        
        // reactant images and leftovers displays
        Reactant[] reactants = reaction.getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one parent node for each reactant image
            PComposite parent = new PComposite();
            addChild( parent );
            reactantNodeParents.add( parent );
            
            // one list of image nodes for each reactant 
            reactantNodeLists.add( new ArrayList<SubstanceNode>() );
            
            // one quantity display for each reactant
            LeftoversDisplayNode displayNode = new LeftoversDisplayNode( reactant, quantityRange, IMAGE_SCALE );
            addChild( displayNode );
            reactantLeftoverDisplayNodes.add( displayNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        boxNode.setOffset( x, y );
        // title centered above box
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - TITLE_Y_SPACING;
        titleNode.setOffset( x, y );
        // product-specific nodes
        x = boxNode.getFullBoundsReference().getMinX() + LEFT_MARGIN;
        double deltaX = ( boxNode.getFullBoundsReference().getWidth() - LEFT_MARGIN - RIGHT_MARGIN ) / ( products.length + reactants.length - 1 );
        for ( int i = 0; i < products.length; i++ ) {
            
            // quantity displays
            y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
            productQuantityDisplayNodes.get( i ).setOffset( x, y );
            
            // images, centered above quantity displays
            y = boxNode.getFullBoundsReference().getMaxY() - IMAGES_Y_MARGIN;
            productNodeParents.get( i ).setOffset( x, y );
            
            x += deltaX;
        }
        // reactant-specific nodes
        for ( int i = 0; i < reactants.length; i++ ) {
            
            // quantity displays
            y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
            reactantLeftoverDisplayNodes.get( i ).setOffset( x, y );
            
            // images, centered above quantity displays
            y = boxNode.getFullBoundsReference().getMaxY() - IMAGES_Y_MARGIN;
            reactantNodeParents.get( i ).setOffset( x, y );
            
            x += deltaX;
        }
        
        // leftovers label, after doing layout of leftover quantity displays
        double startX = reactantLeftoverDisplayNodes.get( 0 ).getFullBoundsReference().getMinX();
        double endX = reactantLeftoverDisplayNodes.get( reactantLeftoverDisplayNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        double width = endX - startX;
        PNode leftoversLabelNode = new LeftoversLabelNode( width );
        addChild( leftoversLabelNode );
        x = startX;
        y = reactantLeftoverDisplayNodes.get( 0 ).getFullBoundsReference().getMaxY() + LEFTOVERS_BRACKET_Y_SPACING;
        leftoversLabelNode.setOffset( x, y );
        
        update();
    }
    
    /**
     * Cleans up all listeners that could cause memory leaks.
     */
    public void cleanup() {
        reaction.removeChangeListener( reactionChangeListener );
        // displays that are listening to products
        for ( QuantityDisplayNode node : productQuantityDisplayNodes ) {
            node.cleanup();
        }
        // displays that are listening to reactants
        for ( LeftoversDisplayNode node : reactantLeftoverDisplayNodes ) {
            node.cleanup();
        }
        // image nodes that are listening to products
        for ( ArrayList<SubstanceNode> list : productNodeLists ) {
            for ( SubstanceNode node : list ) {
                node.cleanup();
            }
        }
        // image nodes that are listening to reactants
        for ( ArrayList<SubstanceNode> list : reactantNodeLists ) {
            for ( SubstanceNode node : list ) {
                node.cleanup();
            }
        }
    }
    
    /*
     * For each product, update quantity display and number of images to match the quantity.
     * For each reactant, update quantity display and number of images to match the leftovers.
     * If we don't have a legitimate reaction, hide the product quantity displays.
     */
    private void update() {
        
        // product quantities
        Product[] products = reaction.getProducts();
        for ( int i = 0; i < products.length; i++ ) {
            
            // products are invisible if we don't have a legitimate reaction
            productQuantityDisplayNodes.get(i).setVisible( reaction.isReaction() );
            
            Product product = products[i];
            PNode parent = productNodeParents.get( i );
            ArrayList<SubstanceNode> images = productNodeLists.get( i );
            
            if ( product.getQuantity() < images.size() ) {
                // remove images
                while ( product.getQuantity() < images.size() ) {
                    SubstanceNode node = images.get( images.size() - 1 );
                    node.cleanup();
                    parent.removeChild( node );
                    images.remove( node );
                }
            }
            else {
                // add images
                while( product.getQuantity() > images.size() ) {
                    SubstanceNode node = new SubstanceNode( product );
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

        // reactant leftovers
        Reactant[] reactants = reaction.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            
            Reactant reactant = reactants[i];
            PNode parent = reactantNodeParents.get( i );
            ArrayList<SubstanceNode> images = reactantNodeLists.get( i );
            
            if ( reactant.getLeftovers() < images.size() ) {
                // remove images
                while ( reactant.getLeftovers() < images.size() ) {
                    SubstanceNode node = images.get( images.size() - 1 );
                    node.cleanup();
                    parent.removeChild( node );
                    images.remove( node );
                }
            }
            else {
                // add images
                while( reactant.getLeftovers() > images.size() ) {
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
