// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.util.ArrayList;

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
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractAfterNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.GridLayoutNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceImageNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * "After Reaction" box and controls for the Game, adds the ability to switch 
 * between viewing the actual reaction and the user's guess.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameAfterNode extends AbstractAfterNode {
    
    private final GameModel model;
    private final GameListener gameListener;
    private final ImageLayoutNode guessImagesNode; // parent node for "guess" images, handles layout of the images
    private final ArrayList<ArrayList<SubstanceImageNode>> productImageNodeLists, leftoverImageNodeLists; // one list of "guess" images per product and leftover
    private final GameMessageNode moleculesHiddenNode;  // a message indicating that the molecule images are hidden
    private final GameMessageNode numbersHiddenNode;  // a message indicating that numbers are hidden
    
    public GameAfterNode( GameModel model, PDimension boxSize ) {
        super( RPALStrings.LABEL_AFTER_REACTION, boxSize, model.getChallenge().getReaction(), GameModel.getQuantityRange(), true /* showSubstanceNames */, new GridLayoutNode( boxSize ) );
        
        // listen for changes to the user's guess
        this.model = model;
        gameListener = new GameAdapter() {
            @Override 
            public void guessChanged() {
                updateGuessImages();
            }
        };
        model.addGameListener( gameListener );
        
        ChemicalReaction reaction = model.getChallenge().getReaction();
        
        // one list of image nodes for each product 
        productImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        for ( int i = 0; i < reaction.getNumberOfProducts(); i++ ) {
            productImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
        }

        // one list of image nodes for each leftover 
        leftoverImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        for ( int i = 0; i < reaction.getNumberOfReactants(); i++ ) {
            leftoverImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
        }
        
        // images
        guessImagesNode = new GridLayoutNode( boxSize );
        updateGuessImages();
        addChild( guessImagesNode );
        
        // "molecules hidden" message node
        moleculesHiddenNode = new GameMessageNode( RPALStrings.MESSAGE_MOLECULES_HIDDEN, Color.BLACK, 28 );
        addChild( moleculesHiddenNode );
        double x = ( boxSize.getWidth() - moleculesHiddenNode.getFullBoundsReference().getWidth() ) / 2;
        double y = ( boxSize.getHeight() - moleculesHiddenNode.getFullBoundsReference().getHeight() ) / 2;
        moleculesHiddenNode.setOffset( x, y );
        
        // "numbers hidden" message node
        numbersHiddenNode = new GameMessageNode( RPALStrings.MESSAGE_NUMBERS_HIDDEN, Color.BLACK, 28 );
        addChild( numbersHiddenNode );
        x = ( boxSize.getWidth() - numbersHiddenNode.getFullBoundsReference().getWidth() ) / 2;
        y = boxSize.getHeight() + 35;
        numbersHiddenNode.setOffset( x, y );

        // default state
        GameChallenge challenge = model.getChallenge();
        if ( challenge.getChallengeType() == ChallengeType.AFTER ) {
            showGuess( true /* editable */ );
        }
        else {
            showAnswer( challenge.isMoleculesVisible(), challenge.isNumbersVisible() );
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        model.removeGameListener( gameListener );
    }
    
    /**
     * Shows all components of the answer (the actual reaction).
     */
    public void showAnswer() {
        showAnswer( true /* showImages */, true /* showNumbers */ );
    }
    
    /**
     * Shows specific components of the answer (the actual reaction).
     * @param showImages
     * @param showNumbers
     */
    public void showAnswer( boolean showImages, boolean showNumbers ) {
        assert( showImages || showNumbers ); // at least one of these must be true
        
        ChemicalReaction reaction = model.getChallenge().getReaction();
        
        // attach read-only value nodes to products of the answer
        ArrayList<QuantityValueNode> productValueNodes = getProductValueNodes();
        for ( int i = 0; i < productValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = productValueNodes.get( i );
            valueNode.setEditable( false );
            valueNode.setSubstance( reaction.getProduct( i ) );
        }
        
        // attach read-only value nodes to leftovers of the answer
        ArrayList<LeftoversValueNode> leftoverValueNodes = getLeftoverValueNodes();
        for ( int i = 0; i < leftoverValueNodes.size(); i++ ) {
            LeftoversValueNode valueNode = leftoverValueNodes.get( i );
            valueNode.setEditable( false );
            valueNode.setReactant( reaction.getReactant( i ) );
        }
        
        // hide guess images
        guessImagesNode.setVisible( false );
        
        // possibly hide answer images
        setReactionImagesVisible( showImages );
        setValueNodeImagesVisible( showImages );
        moleculesHiddenNode.setVisible( !showImages );
        
        // possibily hide numbers
        setNumbersVisible( showNumbers );
        numbersHiddenNode.setVisible( !showNumbers );
    }
    
    /**
     * Shows the images and quantities corresponding to the user's guess.
     * The quantities are optionally editable.
     * @param editable
     */
    public void showGuess( boolean editable ) {
        
        GameGuess guess = model.getChallenge().getGuess();
        
        // attach value nodes to products of guess, optionally editable
        ArrayList<QuantityValueNode> productValueNodes = getProductValueNodes();
        for ( int i = 0; i < productValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = productValueNodes.get( i );
            valueNode.setEditable( editable );
            valueNode.setSubstance( guess.getProduct( i ) );
        }
        
        // attach value nodes to leftovers of guess, optionally editable
        ArrayList<LeftoversValueNode> leftoverValueNodes = getLeftoverValueNodes();
        for ( int i = 0; i < leftoverValueNodes.size(); i++ ) {
            LeftoversValueNode valueNode = leftoverValueNodes.get( i );
            valueNode.setEditable( editable );
            valueNode.setReactant( guess.getReactant( i ) );
        }
        
        // show guess images
        guessImagesNode.setVisible( true );
        moleculesHiddenNode.setVisible( false );
        setValueNodeImagesVisible( true );
        
        // hide answer images
        setReactionImagesVisible( false );
        
        // show numbers
        setNumbersVisible( true );
        numbersHiddenNode.setVisible( false );
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
        ArrayList<QuantityValueNode> productValueNodes = getProductValueNodes();
        for ( int i = 0; i < products.length; i++ ) {
            ArrayList<SubstanceImageNode> imageNodes = productImageNodeLists.get( i );
            Product product = products[i];
            while ( product.getQuantity() > imageNodes.size() ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( product );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                guessImagesNode.addNode( imageNode, productValueNodes.get( i ) );
            }
        }

        // add leftovers
        ArrayList<LeftoversValueNode> leftoverValueNodes = getLeftoverValueNodes();
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = leftoverImageNodeLists.get( i );
            while ( reactant.getLeftovers() > imageNodes.size() ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                guessImagesNode.addNode( imageNode, leftoverValueNodes.get( i ) );
            }
        }
    }
}
