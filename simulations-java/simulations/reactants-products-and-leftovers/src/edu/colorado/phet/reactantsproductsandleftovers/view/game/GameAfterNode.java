package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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
import edu.colorado.phet.reactantsproductsandleftovers.view.BracketedLabelNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceImageNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.GridLayoutNode;
import edu.umd.cs.piccolo.PNode;


public class GameAfterNode extends GameBoxNode {
    
    
    private static final String TITLE = RPALStrings.LABEL_AFTER_REACTION;
    private static final double TITLE_Y_SPACING = 10;
    
    private static final double CONTROLS_Y_SPACING = 15;

    private static final double BRACKET_Y_SPACING = 3;
    private static final PhetFont BRACKET_FONT = new PhetFont( 16 );
    private static final Color BRACKET_TEXT_COLOR = Color.BLACK;
    private static final Color BRACKET_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.75f );
    private static final double BRACKET_MIN_WIDTH = 95;
    
    private final GameModel model;
    private final GameListener gameListener;
    private final ImageLayoutNode answerImagesNode, guessImagesNode;
    private final ArrayList<QuantityValueNode> quantityValueNodes;
    private final ArrayList<LeftoversValueNode> leftoverValueNodes;
    private final ArrayList<ArrayList<SubstanceImageNode>> productImageNodeLists, leftoverImageNodeLists; // one list of images per product and leftover
    
    public GameAfterNode( GameModel model ) {
        super( TITLE );
        
        // image node lists
        productImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        leftoverImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        
        // product images and value displays
        quantityValueNodes = new ArrayList<QuantityValueNode>();
        Product[] products = model.getReaction().getProducts();
        for ( Product product : products ) {
            
            // one list of image nodes for each product 
            productImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one value display for each product
            QuantityValueNode quantityNode = new QuantityValueNode( product, model.getQuantityRange(), RPALConstants.HISTOGRAM_IMAGE_SCALE, true /* showNames */ );
            addChild( quantityNode );
            quantityValueNodes.add( quantityNode );
        }
        
        // leftovers images and value displays
        leftoverValueNodes = new ArrayList<LeftoversValueNode>();
        Reactant[] reactants = model.getReaction().getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one list of image nodes for each leftover 
            leftoverImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity display for each leftover
            LeftoversValueNode leftoverNode = new LeftoversValueNode( reactant, model.getQuantityRange(), RPALConstants.HISTOGRAM_IMAGE_SCALE, true /* showNames */ );
            addChild( leftoverNode );
            leftoverValueNodes.add( leftoverNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        PNode boxNode = getBoxNode();
        boxNode.setOffset( x, y );
        // title centered above box
        PNode titleNode = getTitleNode();
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
            @Override 
            public void guessChanged() {
                updateGuessImages();
            }
        };
        model.addGameListener( gameListener );
        
        // images
        answerImagesNode = new GridLayoutNode( getBoxSize() );
        createAnswerImages();
        addChild( answerImagesNode );
        guessImagesNode = new GridLayoutNode( getBoxSize() );
        updateGuessImages();
        addChild( guessImagesNode );
        
        // default state
        if ( model.getChallengeType() == ChallengeType.AFTER ) {
            showGuess( true /* editable */ );
        }
        else {
            showAnswer();
        }
    }
    
    public void cleanup() {
        model.removeGameListener( gameListener );
    }
    
    public void showAnswer() {
        
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
        
        // show images for reaction
        answerImagesNode.setVisible( true );
        guessImagesNode.setVisible( false );
    }
    
    public void showGuess( boolean editable ) {
        
        // products
        for ( int i = 0; i < quantityValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = quantityValueNodes.get( i );
            // attach to product of guess
            valueNode.setSubstance( model.getGuess().getProduct( i ) );
            // set editability
            valueNode.setEditable( editable );
        }
        
        // leftovers
        for ( int i = 0; i < leftoverValueNodes.size(); i++ ) {
            LeftoversValueNode valueNode = leftoverValueNodes.get( i );
            // attach to reactant of guess
            valueNode.setReactant( model.getGuess().getReactant( i ) );
            // set to read-only
            valueNode.setEditable( editable );
        }
        
        // show images for user's answer
        answerImagesNode.setVisible( false );
        guessImagesNode.setVisible( true );
    }
    
    /*
     * Sets images for the products and leftovers of the correct answer.
     */
    private void createAnswerImages() {
        
        // products
        Product[] products = model.getReaction().getProducts();
        PNode previousNode = null;
        for ( int i = 0; i < products.length; i++ ) {
            Product reactant = products[i];
            for ( int j = 0; j < reactant.getQuantity(); j++ ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                answerImagesNode.addNode( imageNode, previousNode, null );
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
                answerImagesNode.addNode( imageNode, previousNode, null );
                previousNode = imageNode;
            }
        }
    }
    
    /*
     * Updates images for products and leftovers to match the user's guess.
     * The last image added is the first to be removed. 
     */
    private void updateGuessImages() {
        
        /*
         * Do all removal first, so that we free up space in the box.
         */
        
        // remove products
        Product[] products = model.getGuess().getProducts();
        for ( int i = 0; i < products.length; i++ ) {
            Product product = products[i];
            ArrayList<SubstanceImageNode> imageNodes = productImageNodeLists.get( i );
            if ( product.getQuantity() < imageNodes.size() ) {
                while ( product.getQuantity() < imageNodes.size() ) {
                    SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                    imageNode.cleanup();
                    guessImagesNode.removeNode( imageNode );
                    imageNodes.remove( imageNode );
                }
            }
        }
        
        // remove leftovers
        Reactant[] reactants = model.getGuess().getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = leftoverImageNodeLists.get( i );
            if ( reactant.getLeftovers() < imageNodes.size() ) {
                while ( reactant.getLeftovers() < imageNodes.size() ) {
                    SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                    imageNode.cleanup();
                    guessImagesNode.removeNode( imageNode );
                    imageNodes.remove( imageNode );
                }
            }
        }

        /*
         * Do all additions after removals, so that we have free space in the box.
         */
        
        // add products
        for ( int i = 0; i < products.length; i++ ) {
            ArrayList<SubstanceImageNode> imageNodes = productImageNodeLists.get( i );
            Product product = products[i];
            while ( product.getQuantity() > imageNodes.size() ) {
                PNode lastNodeAdded = null;
                if ( imageNodes.size() > 0 ) {
                    lastNodeAdded = imageNodes.get( imageNodes.size() - 1 );
                }
                SubstanceImageNode imageNode = new SubstanceImageNode( product );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                guessImagesNode.addNode( imageNode, lastNodeAdded, quantityValueNodes.get( i ) );
            }
        }

        // add leftovers
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = leftoverImageNodeLists.get( i );
            while ( reactant.getLeftovers() > imageNodes.size() ) {
                PNode lastNodeAdded = null;
                if ( imageNodes.size() > 0 ) {
                    lastNodeAdded = imageNodes.get( imageNodes.size() - 1 );
                }
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                guessImagesNode.addNode( imageNode, lastNodeAdded, leftoverValueNodes.get( i ) );
            }
        }
    }
}
