package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityValueNode;
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


public class GameBeforeNode extends GameBoxNode {
    
    private static final String TITLE = RPALStrings.LABEL_BEFORE_REACTION;
    private static final double TITLE_Y_SPACING = 10;
    
    private static final double CONTROLS_Y_SPACING = 15;
    
    private static final double BRACKET_Y_SPACING = 3;
    private static final PhetFont BRACKET_FONT = new PhetFont( 16 );
    private static final Color BRACKET_TEXT_COLOR = Color.BLACK;
    private static final Color BRACKET_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.75f );
    
    private final GameModel model;
    private final GameListener gameListener;
    private final ImageLayoutNode imageLayoutNode;
    private final ArrayList<QuantityValueNode> quantityValueNodes;
    
    public GameBeforeNode( GameModel model ) {
        super( TITLE );
        
        // one quantity control for each reactant
        quantityValueNodes = new ArrayList<QuantityValueNode>();
        Reactant[] reactants = model.getReaction().getReactants();
        for ( Reactant reactant : reactants ) {
            QuantityValueNode quantityNode = new QuantityValueNode( reactant, model.getQuantityRange(), RPALConstants.HISTOGRAM_IMAGE_SCALE, true /* showName */ );
            quantityNode.setEditable( false );
            addChild( quantityNode );
            quantityValueNodes.add( quantityNode );
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
        // reactant quantity controls, horizontally centered in "cells"
        double margin = ( reactants.length > 2 ) ? 0 : ( 0.15 * getBoxWidth() ); // make 2 reactants case look nice
        final double deltaX = ( boxNode.getFullBoundsReference().getWidth() - ( 2 * margin ) ) / ( reactants.length );
        x = boxNode.getFullBoundsReference().getMinX() + margin + ( deltaX / 2 );
        y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
        for ( int i = 0; i < reactants.length; i++ ) {
            quantityValueNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        
        // reactants bracket, after doing layout of leftover quantity displays
        double startX = quantityValueNodes.get( 0 ).getFullBoundsReference().getMinX();
        double endX = quantityValueNodes.get( quantityValueNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        double width = endX - startX;
        PNode reactantsLabelNode = new BracketedLabelNode( RPALStrings.LABEL_REACTANTS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( reactantsLabelNode );
        x = startX;
        y = 0;
        for ( QuantityValueNode node : quantityValueNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        reactantsLabelNode.setOffset( x, y );
        
        // sync with model
        this.model = model;
        gameListener = new GameAdapter() {
            //XXX show the user's guess, if applicable
            //XXX show the answer, when requested
        };
        model.addGameListener( gameListener );
        
        // images
        imageLayoutNode = new GridLayoutNode( getBoxSize() );
        addChild( imageLayoutNode );
        addReactantImages();
        
        // default state
        if ( model.getChallengeType() == ChallengeType.HOW_MANY_REACTANTS ) {
            showUserAnswer( true /* editable */ );
        }
        else {
            showCorrectAnswer();
        }
    }
    
    public void cleanup() {
        model.removeGameListener( gameListener );
    }
    
    public void showCorrectAnswer() {
        
        // reactants
        for ( int i = 0; i < quantityValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = quantityValueNodes.get( i );
            // attach to reactant of reaction
            valueNode.setSubstance( model.getReaction().getReactant( i ) );
            // set to read-only
            valueNode.setEditable( false );
        }
        
        //XXX show images for reaction
    }
    
    public void showUserAnswer( boolean editable ) {
        
        // reactants
        for ( int i = 0; i < quantityValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = quantityValueNodes.get( i );
            // attach to reactant of user's answer
            valueNode.setSubstance( model.getAnswer().getReactant( i ) );
            // set editability
            valueNode.setEditable( editable );
        }
        
        //XXX show images for user's answer
    }
    
    /*
     * Add images for the reactants.
     */
    private void addReactantImages() {
        
        Reactant[] reactants = model.getReaction().getReactants();
        PNode previousNode = null;
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            for ( int j = 0; j < reactant.getQuantity(); j++ ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageLayoutNode.addNode( imageNode, previousNode, null );
                previousNode = imageNode;
            }
        }
    }
}
