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
import edu.colorado.phet.reactantsproductsandleftovers.controls.LeftoversDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all "After Reaction" displays.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractAfterNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    private static final double TITLE_Y_SPACING = 10;
    private static final double LEFT_MARGIN = 0;
    private static final double RIGHT_MARGIN = LEFT_MARGIN;
    private static final double CONTROLS_Y_SPACING = 15;

    private static final double BRACKET_Y_SPACING = 3;
    private static final PhetFont BRACKET_FONT = new PhetFont( 16 );
    private static final Color BRACKET_TEXT_COLOR = Color.BLACK;
    private static final Color BRACKET_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.75f );
    private static final double BRACKET_MIN_WIDTH = 95;
    
    private final ChemicalReaction reaction;
    private final ChangeListener reactionChangeListener;

    private final BoxNode boxNode;
    private final ArrayList<ArrayList<SubstanceImageNode>> productImageNodeLists, reactantImageNodeLists; // one list of images per product and reactant
    private final ArrayList<QuantityDisplayNode> productQuantityDisplayNodes; // quantity displays for products
    private final ArrayList<LeftoversDisplayNode> reactantLeftoverDisplayNodes; // leftovers displays for reactants
    private final ImageLayoutStrategy imageLayoutStrategy;
    private final PNode productsLabelNode ;
    
    public AbstractAfterNode( String title, final ChemicalReaction reaction, IntegerRange quantityRange, boolean showSubstanceNames,  ImageLayoutStrategy imageLayoutStrategy ) {
        super();
        
        this.reaction = reaction;
        reactionChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        reaction.addChangeListener( reactionChangeListener );
        
        this.imageLayoutStrategy = imageLayoutStrategy;
        
        productImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        reactantImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        productQuantityDisplayNodes = new ArrayList<QuantityDisplayNode>();
        reactantLeftoverDisplayNodes = new ArrayList<LeftoversDisplayNode>();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        imageLayoutStrategy.setBoxNode( boxNode );
        
        // title for the box
        PText titleNode = new PText( title );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // product images and quantity displays
        Product[] products = reaction.getProducts();
        for ( Product product : products ) {
            
            // one list of image nodes for each product 
            productImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity display for each product
            QuantityDisplayNode displayNode = new QuantityDisplayNode( product, quantityRange, RPALConstants.HISTOGRAM_IMAGE_SCALE, showSubstanceNames );
            addChild( displayNode );
            productQuantityDisplayNodes.add( displayNode );
        }
        
        // reactant images and leftovers displays
        Reactant[] reactants = reaction.getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one list of image nodes for each reactant 
            reactantImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity display for each reactant
            LeftoversDisplayNode displayNode = new LeftoversDisplayNode( reactant, quantityRange, RPALConstants.HISTOGRAM_IMAGE_SCALE, showSubstanceNames );
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
        // product quantity displays, horizontally centered in "cells"
        final double deltaX = ( boxNode.getFullBoundsReference().getWidth() - LEFT_MARGIN - RIGHT_MARGIN ) / ( products.length + reactants.length );
        x = boxNode.getFullBoundsReference().getMinX() + LEFT_MARGIN + ( deltaX / 2 );
        y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
        for ( int i = 0; i < products.length; i++ ) {
            productQuantityDisplayNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        // reactant quantity displays, horizontally centered in "cells"
        for ( int i = 0; i < reactants.length; i++ ) {
            reactantLeftoverDisplayNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        
        // products bracket, after doing layout of product quantity displays
        double startX = productQuantityDisplayNodes.get( 0 ).getFullBoundsReference().getMinX();
        double endX = productQuantityDisplayNodes.get( productQuantityDisplayNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        double width = Math.max( BRACKET_MIN_WIDTH, endX - startX );
        productsLabelNode = new BracketedLabelNode( RPALStrings.LABEL_PRODUCTS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( productsLabelNode );
        x = startX + ( ( endX - startX - width ) / 2 ); 
        y = 0;
        for ( QuantityDisplayNode node : productQuantityDisplayNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        productsLabelNode.setOffset( x, y );
        
        // leftovers bracket, after doing layout of leftover quantity displays
        startX = reactantLeftoverDisplayNodes.get( 0 ).getFullBoundsReference().getMinX();
        endX = reactantLeftoverDisplayNodes.get( reactantLeftoverDisplayNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        width = endX - startX;
        PNode leftoversLabelNode = new BracketedLabelNode( RPALStrings.LABEL_LEFTOVERS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( leftoversLabelNode );
        x = startX;
        y = 0;
        for ( LeftoversDisplayNode node : reactantLeftoverDisplayNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        leftoversLabelNode.setOffset( x, y );
        
        // start with products and leftovers brackets vertically aligned
        double maxYOffset = Math.max( productsLabelNode.getYOffset(), leftoversLabelNode.getYOffset() );
        productsLabelNode.setOffset( productsLabelNode.getXOffset(), maxYOffset );
        leftoversLabelNode.setOffset( leftoversLabelNode.getXOffset(), maxYOffset );
        
        update();
        
        // do this after productLabelNode has been initialized
        for ( QuantityDisplayNode displayNode : productQuantityDisplayNodes ) {
            displayNode.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                        updateProductsLabelOffset();
                    }
                }
            } );
        }
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
        for ( ArrayList<SubstanceImageNode> list : productImageNodeLists ) {
            for ( SubstanceImageNode node : list ) {
                node.cleanup();
            }
        }
        // image nodes that are listening to reactants
        for ( ArrayList<SubstanceImageNode> list : reactantImageNodeLists ) {
            for ( SubstanceImageNode node : list ) {
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

        // products are invisible if we don't have a legitimate reaction
        productsLabelNode.setVisible( reaction.isReaction() );
        for ( QuantityDisplayNode node : productQuantityDisplayNodes ) {
            node.setVisible( reaction.isReaction() );
        }

        /*
         * Do all removal first, so that we free up space in the box.
         */
        
        // remove products
        Product[] products = reaction.getProducts();
        for ( int i = 0; i < products.length; i++ ) {
            Product product = products[i];
            ArrayList<SubstanceImageNode> imageNodes = productImageNodeLists.get( i );
            if ( product.getQuantity() < imageNodes.size() ) {
                while ( product.getQuantity() < imageNodes.size() ) {
                    SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                    imageNode.cleanup();
                    imageLayoutStrategy.removeNode( imageNode );
                    imageNodes.remove( imageNode );
                }
            }
        }
        
        // remove leftovers
        Reactant[] reactants = reaction.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = reactantImageNodeLists.get( i );
            if ( reactant.getLeftovers() < imageNodes.size() ) {
                while ( reactant.getLeftovers() < imageNodes.size() ) {
                    SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                    imageNode.cleanup();
                    imageLayoutStrategy.removeNode( imageNode );
                    imageNodes.remove( imageNode );
                }
            }
        }

        /*
         * Do all additions after removals, so that we free up space in the box.
         */
        
        // add products
        for ( int i = 0; i < products.length; i++ ) {
            ArrayList<SubstanceImageNode> imageNodes = productImageNodeLists.get( i );
            Product product = products[i];
            while ( product.getQuantity() > imageNodes.size() ) {

                PNode previousNode = null;
                if ( imageNodes.size() > 0 ) {
                    previousNode = imageNodes.get( imageNodes.size() - 1 );
                }

                while ( product.getQuantity() > imageNodes.size() ) {
                    SubstanceImageNode imageNode = new SubstanceImageNode( product );
                    imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                    imageNodes.add( imageNode );
                    imageLayoutStrategy.addNode( imageNode, previousNode, productQuantityDisplayNodes.get( i ) );
                    previousNode = imageNode;
                }
            }
        }

        // add leftovers
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = reactantImageNodeLists.get( i );
            while ( reactant.getLeftovers() > imageNodes.size() ) {

                PNode previousNode = null;
                if ( imageNodes.size() > 0 ) {
                    previousNode = imageNodes.get( imageNodes.size() - 1 );
                }

                while ( reactant.getLeftovers() > imageNodes.size() ) {
                    SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                    imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                    imageNodes.add( imageNode );
                    imageLayoutStrategy.addNode( imageNode, previousNode, reactantLeftoverDisplayNodes.get( i ) );
                    previousNode = imageNode;
                }
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
        double y = 0;
        for ( QuantityDisplayNode node : productQuantityDisplayNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        productsLabelNode.setOffset( x, y );
    }
}
