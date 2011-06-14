// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.LeftoversValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all "After Reaction" displays.
 * Includes a box for displaying a reactant's product and leftover molecule images, 
 * and a set of controls for the reaction's product and leftover quantities.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractAfterNode extends PhetPNode implements IDynamicNode {
    
    private static final double CONTROLS_Y_SPACING = 15;

    private static final double BRACKET_Y_SPACING = 3;
    private static final PhetFont BRACKET_FONT = new PhetFont( 16 );
    private static final Color BRACKET_TEXT_COLOR = Color.BLACK;
    private static final Color BRACKET_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.75f );
    private static final double BRACKET_MIN_WIDTH = 95;
    
    private final ChemicalReaction reaction;
    private final ChangeListener reactionChangeListener;

    private final TitledBoxNode titledBoxNode;
    private final ArrayList<ArrayList<SubstanceImageNode>> productImageNodeLists, leftoverImageNodeLists; // one list of images per product and leftover
    private final ArrayList<QuantityValueNode> productValueNodes; // quantity displays for products
    private final ArrayList<LeftoversValueNode> leftoverValueNodes; // leftover displays for reactants
    private final ImageLayoutNode imageLayoutNode;
    private final PNode productsLabelNode, leftoversLabelNode;
    
    public AbstractAfterNode( String title, PDimension boxSize, final ChemicalReaction reaction, IntegerRange quantityRange, boolean showSubstanceNames,  ImageLayoutNode imageLayoutNode ) {
        super();
        
        this.reaction = reaction;
        reactionChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        reaction.addChangeListener( reactionChangeListener );
        
        this.imageLayoutNode = imageLayoutNode;
        
        productImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        leftoverImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        productValueNodes = new ArrayList<QuantityValueNode>();
        leftoverValueNodes = new ArrayList<LeftoversValueNode>();
        
        // titled box
        titledBoxNode = new TitledBoxNode( title, boxSize );
        addChild( titledBoxNode );
        addChild( imageLayoutNode );
        
        // product images and quantity displays
        Product[] products = reaction.getProducts();
        for ( Product product : products ) {
            
            // one list of image nodes for each product 
            productImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity display for each product
            QuantityValueNode quantityNode = new QuantityValueNode( product, quantityRange, RPALConstants.HISTOGRAM_IMAGE_SCALE, showSubstanceNames );
            addChild( quantityNode );
            productValueNodes.add( quantityNode );
        }
        
        // leftovers images and quantity displays
        Reactant[] reactants = reaction.getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one list of image nodes for each leftover 
            leftoverImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity display for each leftover
            LeftoversValueNode leftoverNode = new LeftoversValueNode( reactant, quantityRange, RPALConstants.HISTOGRAM_IMAGE_SCALE, showSubstanceNames );
            addChild( leftoverNode );
            leftoverValueNodes.add( leftoverNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        titledBoxNode.setOffset( x, y );
        // product quantity displays, horizontally centered in "cells"
        final double deltaX = titledBoxNode.getBoxNode().getFullBoundsReference().getWidth() / ( products.length + reactants.length );
        x = titledBoxNode.getBoxNode().getFullBoundsReference().getMinX() + ( deltaX / 2 );
        y = titledBoxNode.getBoxNode().getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
        for ( int i = 0; i < products.length; i++ ) {
            productValueNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        // leftover quantity displays, horizontally centered in "cells"
        for ( int i = 0; i < reactants.length; i++ ) {
            leftoverValueNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        
        // products bracket, after doing layout of product quantity displays
        double startX = productValueNodes.get( 0 ).getFullBoundsReference().getMinX();
        double endX = productValueNodes.get( productValueNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        double width = Math.max( BRACKET_MIN_WIDTH, endX - startX );
        productsLabelNode = new BracketedLabelNode( RPALStrings.LABEL_PRODUCTS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( productsLabelNode );
        x = startX + ( ( endX - startX - width ) / 2 ); 
        y = 0; // vertical offset will be adjusted later, via updateProductsLabelOffset
        productsLabelNode.setOffset( x, y );
        
        // leftovers bracket, after doing layout of leftover quantity displays
        startX = leftoverValueNodes.get( 0 ).getFullBoundsReference().getMinX();
        endX = leftoverValueNodes.get( leftoverValueNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        width = endX - startX;
        leftoversLabelNode = new BracketedLabelNode( RPALStrings.LABEL_LEFTOVERS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( leftoversLabelNode );
        x = startX;
        y = 0;
        for ( LeftoversValueNode node : leftoverValueNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        leftoversLabelNode.setOffset( x, y );
        
        // do this after productLabelNode has been initialized
        PropertyChangeListener fullBoundsListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    updateProductsLabelOffset();
                }
            }
        };
        for ( QuantityValueNode displayNode : productValueNodes ) {
            displayNode.addPropertyChangeListener( fullBoundsListener );
        }
        
        // adjust products bracket so that it's never higher than leftovers bracket
        updateProductsLabelOffset();
        
        update();
    }
    
    /**
     * Cleans up all listeners that could cause memory leaks.
     */
    public void cleanup() {
        reaction.removeChangeListener( reactionChangeListener );
        // displays that are listening to products
        for ( QuantityValueNode node : productValueNodes ) {
            node.cleanup();
        }
        // displays that are listening to reactants
        for ( LeftoversValueNode node : leftoverValueNodes ) {
            node.cleanup();
        }
        // image nodes that are listening to products
        for ( ArrayList<SubstanceImageNode> list : productImageNodeLists ) {
            for ( SubstanceImageNode node : list ) {
                node.cleanup();
            }
        }
        // image nodes that are listening to reactants
        for ( ArrayList<SubstanceImageNode> list : leftoverImageNodeLists ) {
            for ( SubstanceImageNode node : list ) {
                node.cleanup();
            }
        }
    }
    
    /*
     * Sets the visibility of reaction molecule images.
     * Intended for use by subclasses that may want to to hide images.
     */
    protected void setReactionImagesVisible( boolean visible ) {
        imageLayoutNode.setVisible( visible );
    }
    
    protected void setValueNodeImagesVisible( boolean visible ) {
        for ( ValueNode valueNode : productValueNodes ) {
            valueNode.setImageVisible( visible );
        }
        for ( ValueNode valueNode : leftoverValueNodes ) {
            valueNode.setImageVisible( visible );
        }
    }

    /*
     * Sets the visibility of the numeric values below the box.
     * Intended for use by subclasses that may want to hide these controls.
     */
    protected void setNumbersVisible( boolean visible ) {
        for ( ValueNode valueNode : productValueNodes ) {
            valueNode.setValueVisible( visible );
            valueNode.setHistogramBarVisible( visible );
        }
        for ( ValueNode valueNode : leftoverValueNodes ) {
            valueNode.setValueVisible( visible );
            valueNode.setHistogramBarVisible( visible );
        }
    }
    
    /*
     * Gets a list of the product value nodes.
     * Intended for use by subclasses that need to fiddle with these.
     */
    protected ArrayList<QuantityValueNode> getProductValueNodes() {
        return productValueNodes;
    }
    
    /*
     * Gets a list of the leftover value nodes.
     * Intended for use by subclasses that need to fiddle with these.
     */
    protected ArrayList<LeftoversValueNode> getLeftoverValueNodes() {
        return leftoverValueNodes;
    }
    
    /*
     * For each product, update quantity display and number of images to match the quantity.
     * For each reactant, update quantity display and number of images to match the leftovers.
     * If we don't have a legitimate reaction, hide the product quantity displays.
     */
    private void update() {

        // products are invisible if we don't have a legitimate reaction
        for ( QuantityValueNode node : productValueNodes ) {
            node.setVisible( reaction.isReaction() );
        }

        /*
         * Do all removal first, so that we free up space in the layout.
         */
        
        // remove products
        Product[] products = reaction.getProducts();
        for ( int i = 0; i < products.length; i++ ) {
            Product product = products[i];
            ArrayList<SubstanceImageNode> imageNodes = productImageNodeLists.get( i );
            while ( product.getQuantity() < imageNodes.size() ) {
                SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                imageNode.cleanup();
                imageLayoutNode.removeNode( imageNode );
                imageNodes.remove( imageNode );
            }
        }
        
        // remove leftovers
        Reactant[] reactants = reaction.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = leftoverImageNodeLists.get( i );
            while ( reactant.getLeftovers() < imageNodes.size() ) {
                SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                imageNode.cleanup();
                imageLayoutNode.removeNode( imageNode );
                imageNodes.remove( imageNode );
            }
        }

        /*
         * Do all additions after removals, so that we free up space in the layout.
         */
        
        // add products
        for ( int i = 0; i < products.length; i++ ) {
            ArrayList<SubstanceImageNode> imageNodes = productImageNodeLists.get( i );
            Product product = products[i];
            while ( product.getQuantity() > imageNodes.size() ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( product );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                imageLayoutNode.addNode( imageNode, productValueNodes.get( i ) );
            }
        }

        // add leftovers
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = leftoverImageNodeLists.get( i );
            while ( reactant.getLeftovers() > imageNodes.size() ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                imageLayoutNode.addNode( imageNode, leftoverValueNodes.get( i ) );
            }
        }
    }
    
    /*
     * Adjusts the y offset of the products label bracket.
     * Some products (eg, sandwich) may have a dynamic image, and we need to keep 
     * this label below the product quantity display nodes.
     */
    private void updateProductsLabelOffset() {
        double x = productsLabelNode.getXOffset();
        double y = leftoversLabelNode.getYOffset(); // never higher than the leftovers bracket;
        for ( QuantityValueNode node : productValueNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        productsLabelNode.setOffset( x, y );
    }
}
