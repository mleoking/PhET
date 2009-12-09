package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all "Before Reaction" displays.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractBeforeNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final double TITLE_Y_SPACING = 10;
    
    private static final double CONTROLS_Y_SPACING = 15;
    
    private static final double BRACKET_Y_SPACING = 3;
    private static final PhetFont BRACKET_FONT = new PhetFont( 16 );
    private static final Color BRACKET_TEXT_COLOR = Color.BLACK;
    private static final Color BRACKET_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.75f );
    
    private final ChemicalReaction reaction;
    private final ChangeListener reactionChangeListener;

    private final BoxNode boxNode;
    private final ArrayList<ArrayList<SubstanceImageNode>> imageNodeLists; // one list of images per reactant
    private final ArrayList<QuantityValueNode> quantityValueNodes; // quantity controls for reactants
    private final ImageLayoutStrategy imageLayoutStrategy;
    
    public AbstractBeforeNode( String title, final ChemicalReaction reaction, IntegerRange quantityRange, boolean showSubstanceNames, ImageLayoutStrategy imageLayoutStrategy ) {
        super();
        
        this.reaction = reaction;
        reactionChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        reaction.addChangeListener( reactionChangeListener );
        
        this.imageLayoutStrategy = imageLayoutStrategy;
        
        imageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        quantityValueNodes = new ArrayList<QuantityValueNode>();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        imageLayoutStrategy.setBoxNode( boxNode );
        
        // title for the box
        PText titleNode = new PText( title );
        titleNode.setFont( TITLE_FONT );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // images and controls
        Reactant[] reactants = reaction.getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one list of image nodes for each reactant 
            imageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity control for each reactant
            QuantityValueNode quantityNode = new QuantityValueNode( reactant, quantityRange, RPALConstants.HISTOGRAM_IMAGE_SCALE, showSubstanceNames );
            quantityNode.setEditable( true );
            addChild( quantityNode );
            quantityValueNodes.add( quantityNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        boxNode.setOffset( x, y );
        // title centered above box
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - TITLE_Y_SPACING;
        titleNode.setOffset( x, y );
        // reactant quantity controls, horizontally centered in "cells"
        double margin = ( reactants.length > 2 ) ? 0 : ( 0.15 * BOX_SIZE.getWidth() ); // make 2 reactants case look nice
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
        PNode leftoversLabelNode = new BracketedLabelNode( RPALStrings.LABEL_REACTANTS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( leftoversLabelNode );
        x = startX;
        y = 0;
        for ( QuantityValueNode node : quantityValueNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        leftoversLabelNode.setOffset( x, y );
        
        update();
    }
    
    /**
     * Cleans up all listeners that could cause memory leaks.
     */
    public void cleanup() {
        reaction.removeChangeListener( reactionChangeListener );
        // controls that are listening to reactants
        for ( QuantityValueNode node : quantityValueNodes ) {
            node.cleanup();
        }
        // images that are listening to reactants
        for ( ArrayList<SubstanceImageNode> list : imageNodeLists ) {
            for ( SubstanceImageNode node : list ) {
                node.cleanup();
            }
        }
    }
    
    /**
     * Box height, used by layout code.
     * @return
     */
    public double getBoxHeight() {
        return boxNode.getFullBoundsReference().getHeight();
    }
    
    /*
     * For each reactant, update quantity control and number of images to match the quantity.
     */
    private void update() {
        
        Reactant[] reactants = reaction.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = imageNodeLists.get( i );
            
            if ( reactant.getQuantity() < imageNodes.size() ) {
                // remove images
                while ( reactant.getQuantity() < imageNodes.size() ) {
                    SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                    imageNode.cleanup();
                    imageLayoutStrategy.removeNode( imageNode );
                    imageNodes.remove( imageNode );
                }
            }
            else {
                PNode previousNode = null;
                if ( imageNodes.size() > 0 ) {
                    previousNode = imageNodes.get( imageNodes.size() - 1 );
                }
                
                while( reactant.getQuantity() > imageNodes.size() ) {
                    SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                    imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                    imageNodes.add( imageNode );
                    imageLayoutStrategy.addNode( imageNode, previousNode, quantityValueNodes.get( i ) );
                    previousNode = imageNode;
                }
            }
        }
    }
}
