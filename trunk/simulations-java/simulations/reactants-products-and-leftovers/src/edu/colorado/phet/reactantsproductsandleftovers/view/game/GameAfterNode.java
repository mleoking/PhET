package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.LeftoversValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameListener;
import edu.colorado.phet.reactantsproductsandleftovers.view.BoxNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.BracketedLabelNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceImageNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.GridLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;


public class GameAfterNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    
    private static final String TITLE = RPALStrings.LABEL_AFTER_REACTION;
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final double TITLE_Y_SPACING = 10;
    
    private static final double CONTROLS_Y_SPACING = 15;

    private static final double BRACKET_Y_SPACING = 3;
    private static final PhetFont BRACKET_FONT = new PhetFont( 16 );
    private static final Color BRACKET_TEXT_COLOR = Color.BLACK;
    private static final Color BRACKET_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.75f );
    private static final double BRACKET_MIN_WIDTH = 95;
    
    private final GameModel model;
    private final BoxNode boxNode;
    private final GameListener gameListener;
    private final ImageLayoutNode imageLayoutNode;
    private final ArrayList<QuantityValueNode> quantityValueNodes;
    private final ArrayList<LeftoversValueNode> leftoverValueNodes;
    
    public GameAfterNode( GameModel model ) {
        super();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        PText titleNode = new PText( TITLE );
        titleNode.setFont( TITLE_FONT );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // one quantity display for each product
        quantityValueNodes = new ArrayList<QuantityValueNode>();
        Product[] products = model.getReaction().getProducts();
        for ( Product product : products ) {
            QuantityValueNode quantityNode = new QuantityValueNode( product, model.getQuantityRange(), RPALConstants.HISTOGRAM_IMAGE_SCALE, true /* showNames */ );
            addChild( quantityNode );
            quantityValueNodes.add( quantityNode );
        }
        
        // one quantity display for each leftover
        leftoverValueNodes = new ArrayList<LeftoversValueNode>();
        Reactant[] reactants = model.getReaction().getReactants();
        for ( Reactant reactant : reactants ) {
            LeftoversValueNode leftoverNode = new LeftoversValueNode( reactant, model.getQuantityRange(), RPALConstants.HISTOGRAM_IMAGE_SCALE, true /* showNames */ );
            addChild( leftoverNode );
            leftoverValueNodes.add( leftoverNode );
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
        final double deltaX = boxNode.getFullBoundsReference().getWidth() / ( products.length + reactants.length );
        x = boxNode.getFullBoundsReference().getMinX() + ( deltaX / 2 );
        y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
        for ( int i = 0; i < products.length; i++ ) {
            quantityValueNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        // leftover quantity displays, horizontally centered in "cells"
        for ( int i = 0; i < reactants.length; i++ ) {
            leftoverValueNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        
        // products bracket, after doing layout of product quantity displays
        double startX = quantityValueNodes.get( 0 ).getFullBoundsReference().getMinX();
        double endX = quantityValueNodes.get( quantityValueNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        double width = Math.max( BRACKET_MIN_WIDTH, endX - startX );
        PNode productsLabelNode = new BracketedLabelNode( RPALStrings.LABEL_PRODUCTS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( productsLabelNode );
        x = startX + ( ( endX - startX - width ) / 2 ); 
        y = 0;
        for ( QuantityValueNode node : quantityValueNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        productsLabelNode.setOffset( x, y );
        
        // leftovers bracket, after doing layout of leftover quantity displays
        startX = leftoverValueNodes.get( 0 ).getFullBoundsReference().getMinX();
        endX = leftoverValueNodes.get( leftoverValueNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        width = endX - startX;
        PNode leftoversLabelNode = new BracketedLabelNode( RPALStrings.LABEL_LEFTOVERS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( leftoversLabelNode );
        x = startX;
        y = 0;
        for ( LeftoversValueNode node : leftoverValueNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        leftoversLabelNode.setOffset( x, y );
        
        // start with products and leftovers brackets vertically aligned
        double maxYOffset = Math.max( productsLabelNode.getYOffset(), leftoversLabelNode.getYOffset() );
        productsLabelNode.setOffset( productsLabelNode.getXOffset(), maxYOffset );
        leftoversLabelNode.setOffset( leftoversLabelNode.getXOffset(), maxYOffset );

        // sync with model
        this.model = model;
        gameListener = new GameAdapter() {
            //XXX show the user's guess, if applicable
            //XXX show the answer, when requested
        };
        model.addGameListener( gameListener );
        
        // images
        imageLayoutNode = new GridLayoutNode( BOX_SIZE );
        addChild( imageLayoutNode );
        addProductsAndLeftoversImages();
        
        // default state
        if ( model.getChallengeType() == ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS ) {
            showUserAnswer( true /* editable */ );
        }
        else {
            showCorrectAnswer();
        }
    }
    
    public void cleanup() {
        model.removeGameListener( gameListener );
    }
    
    /**
     * Box width, used by layout code.
     * @return
     */
    public double getBoxWidth() {
        return boxNode.getFullBoundsReference().getWidth();
    }

    /**
     * Box height, used by layout code.
     * @return
     */
    public double getBoxHeight() {
        return boxNode.getFullBoundsReference().getHeight();
    }
    
    public void showCorrectAnswer() {
        
        // products
        for ( int i = 0; i < quantityValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = quantityValueNodes.get( i );
            // attach to product of reaction
            valueNode.setSubstance( model.getReaction().getProduct( i ) );
            // set to read-only
            valueNode.setEditable( false );
        }
        
        // leftovers
        for ( int i = 0; i < leftoverValueNodes.size(); i++ ) {
            LeftoversValueNode valueNode = leftoverValueNodes.get( i );
            // attach to reactant of reaction
            valueNode.setReactant( model.getReaction().getReactant( i ) );
            // set to read-only
            valueNode.setEditable( false );
        }
        
        //XXX show images for reaction
    }
    
    public void showUserAnswer( boolean editable ) {
        
        // products
        for ( int i = 0; i < quantityValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = quantityValueNodes.get( i );
            // attach to product of user's answer
            valueNode.setSubstance( model.getAnswer().getProduct( i ) );
            // set editability
            valueNode.setEditable( editable );
        }
        
        // leftovers
        for ( int i = 0; i < leftoverValueNodes.size(); i++ ) {
            LeftoversValueNode valueNode = leftoverValueNodes.get( i );
            // attach to reactant of user's answer
            valueNode.setReactant( model.getAnswer().getReactant( i ) );
            // set to read-only
            valueNode.setEditable( editable );
        }
        
        //XXX show images for user's answer
    }
    
    /*
     * Add images for the products and leftovers.
     */
    private void addProductsAndLeftoversImages() {
        
        // products
        Product[] products = model.getReaction().getProducts();
        PNode previousNode = null;
        for ( int i = 0; i < products.length; i++ ) {
            Product reactant = products[i];
            for ( int j = 0; j < reactant.getQuantity(); j++ ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageLayoutNode.addNode( imageNode, previousNode, null );
                previousNode = imageNode;
            }
        }
        
        // leftovers
        Reactant[] reactants = model.getReaction().getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            for ( int j = 0; j < reactant.getLeftovers(); j++ ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageLayoutNode.addNode( imageNode, previousNode, null );
                previousNode = imageNode;
            }
        }
    }
}
