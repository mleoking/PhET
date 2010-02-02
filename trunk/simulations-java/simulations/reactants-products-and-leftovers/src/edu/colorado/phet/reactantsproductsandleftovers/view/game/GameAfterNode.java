/* Copyright 2010, University of Colorado */

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
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameGuess;
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
    private final MoleculesHiddenNode moleculesHiddenNode;
    
    public GameAfterNode( GameModel model ) {
        super( TITLE );
        
        GameChallenge challenge = model.getChallenge();
        ChemicalReaction reaction = challenge.getReaction();
        
        // image node lists
        productImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        leftoverImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        
        // product images and value displays
        quantityValueNodes = new ArrayList<QuantityValueNode>();
        Product[] products = reaction.getProducts();
        for ( Product product : products ) {
            
            // one list of image nodes for each product 
            productImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one value display for each product
            QuantityValueNode quantityNode = new QuantityValueNode( product, GameModel.getQuantityRange(), RPALConstants.HISTOGRAM_IMAGE_SCALE, true /* showNames */ );
            addChild( quantityNode );
            quantityValueNodes.add( quantityNode );
        }
        
        // leftovers images and value displays
        leftoverValueNodes = new ArrayList<LeftoversValueNode>();
        Reactant[] reactants = reaction.getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one list of image nodes for each leftover 
            leftoverImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity display for each leftover
            LeftoversValueNode leftoverNode = new LeftoversValueNode( reactant, GameModel.getQuantityRange(), RPALConstants.HISTOGRAM_IMAGE_SCALE, true /* showNames */ );
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
        
        // "images hidden" message node
        moleculesHiddenNode = new MoleculesHiddenNode();
        addChild( moleculesHiddenNode );
        x = ( getBoxWidth() - moleculesHiddenNode.getFullBoundsReference().getWidth() ) / 2;
        y = ( getBoxHeight() - moleculesHiddenNode.getFullBoundsReference().getHeight() ) / 2;
        moleculesHiddenNode.setOffset( x, y );
        
        // default state
        if ( challenge.getChallengeType() == ChallengeType.AFTER ) {
            showGuess( true /* editable */, challenge.isImagesVisible() );
        }
        else {
            showAnswer( challenge.isImagesVisible() );
        }
    }
    
    public void cleanup() {
        model.removeGameListener( gameListener );
    }
    
    public void showAnswer( boolean showImages ) {
        
        ChemicalReaction reaction = model.getChallenge().getReaction();
        
        // products
        for ( int i = 0; i < quantityValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = quantityValueNodes.get( i );
            // attach to product of reaction
            valueNode.setSubstance( reaction.getProduct( i ) );
            // set to read-only
            valueNode.setEditable( false );
        }
        
        // leftovers
        for ( int i = 0; i < leftoverValueNodes.size(); i++ ) {
            LeftoversValueNode valueNode = leftoverValueNodes.get( i );
            // attach to reactant of reaction
            valueNode.setReactant( reaction.getReactant( i ) );
            // set to read-only
            valueNode.setEditable( false );
        }
        
        // show images for reaction
        showGuessImages( false );
        showAnswerImages( showImages );
        showImagesHiddenMessage( !showImages );
    }
    
    public void showGuess( boolean editable, boolean showImages ) {
        
        GameGuess guess = model.getChallenge().getGuess();
        
        // products
        for ( int i = 0; i < quantityValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = quantityValueNodes.get( i );
            // attach to product of guess
            valueNode.setSubstance( guess.getProduct( i ) );
            // set editability
            valueNode.setEditable( editable );
        }
        
        // leftovers
        for ( int i = 0; i < leftoverValueNodes.size(); i++ ) {
            LeftoversValueNode valueNode = leftoverValueNodes.get( i );
            // attach to reactant of guess
            valueNode.setReactant( guess.getReactant( i ) );
            // set to read-only
            valueNode.setEditable( editable );
        }
        
        // show images for user's answer
        showAnswerImages( false );
        showGuessImages( showImages );
        showImagesHiddenMessage( false );
    }
    
    public void showAnswerImages( boolean b ) {
        answerImagesNode.setVisible( b );
    }
    
    public void showGuessImages( boolean b ) {
        guessImagesNode.setVisible( b );
    }
    
    public void showImagesHiddenMessage( boolean b ) {
        moleculesHiddenNode.setVisible( b );
    }
    
    /*
     * Sets images for the products and leftovers of the correct answer.
     */
    private void createAnswerImages() {
        
        ChemicalReaction reaction = model.getChallenge().getReaction();
        
        // products
        Product[] products = reaction.getProducts();
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
        Reactant[] reactants = reaction.getReactants();
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
        
        GameGuess guess = model.getChallenge().getGuess();
        
        /*
         * Do all removal first, so that we free up space in the box.
         */
        
        // remove products
        Product[] products = guess.getProducts();
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
        Reactant[] reactants = guess.getReactants();
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
