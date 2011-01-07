// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
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
import edu.colorado.phet.reactantsproductsandleftovers.controls.ValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all "Before Reaction" displays.
 * Includes a box for displaying a reactant's reactant molecule images, 
 * and a set of controls for the reaction's reactant quantities.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractBeforeNode extends PhetPNode implements IDynamicNode {
    
    private static final double CONTROLS_Y_SPACING = 15;
    
    private static final double BRACKET_Y_SPACING = 3;
    private static final PhetFont BRACKET_FONT = new PhetFont( 16 );
    private static final Color BRACKET_TEXT_COLOR = Color.BLACK;
    private static final Color BRACKET_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.75f );
    
    private final ChemicalReaction reaction;
    private final ChangeListener reactionChangeListener;

    private final TitledBoxNode titledBoxNode;
    private final ArrayList<ArrayList<SubstanceImageNode>> imageNodeLists; // one list of images per reactant
    private final ArrayList<QuantityValueNode> reactantValueNodes; // quantity controls for reactants
    private final ImageLayoutNode imageLayoutNode;
    
    public AbstractBeforeNode( String title, PDimension boxSize, final ChemicalReaction reaction, IntegerRange quantityRange, boolean showSubstanceNames, ImageLayoutNode imageLayoutNode ) {
        super();
        
        this.reaction = reaction;
        reactionChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateImages();
            }
        };
        reaction.addChangeListener( reactionChangeListener );
        
        this.imageLayoutNode = imageLayoutNode;
        
        imageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        reactantValueNodes = new ArrayList<QuantityValueNode>();
        
        // titled box
        titledBoxNode = new TitledBoxNode( title, boxSize );
        addChild( titledBoxNode );
        addChild( imageLayoutNode );
        
        // images and controls
        Reactant[] reactants = reaction.getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one list of image nodes for each reactant 
            imageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity control for each reactant
            QuantityValueNode quantityNode = new QuantityValueNode( reactant, quantityRange, RPALConstants.HISTOGRAM_IMAGE_SCALE, showSubstanceNames );
            quantityNode.setEditable( true );
            addChild( quantityNode );
            reactantValueNodes.add( quantityNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        titledBoxNode.setOffset( x, y );
        // reactant quantity controls, horizontally centered in "cells"
        double margin = ( reactants.length > 2 ) ? 0 : ( 0.15 * boxSize.getWidth() ); // make 2 reactants case look nice
        final double deltaX = ( boxSize.getWidth() - ( 2 * margin ) ) / ( reactants.length );
        x = titledBoxNode.getBoxNode().getFullBoundsReference().getMinX() + margin + ( deltaX / 2 );
        y = titledBoxNode.getBoxNode().getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
        for ( int i = 0; i < reactants.length; i++ ) {
            reactantValueNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        
        // reactants bracket, after doing layout of leftover quantity displays
        double startX = reactantValueNodes.get( 0 ).getFullBoundsReference().getMinX();
        double endX = reactantValueNodes.get( reactantValueNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        double width = endX - startX;
        PNode reactantsLabelNode = new BracketedLabelNode( RPALStrings.LABEL_REACTANTS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( reactantsLabelNode );
        x = startX;
        y = 0;
        for ( QuantityValueNode node : reactantValueNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        reactantsLabelNode.setOffset( x, y );
        
        updateImages();
    }
    
    /**
     * Cleans up all listeners that could cause memory leaks.
     */
    public void cleanup() {
        reaction.removeChangeListener( reactionChangeListener );
        // controls that are listening to reactants
        for ( QuantityValueNode node : reactantValueNodes ) {
            node.cleanup();
        }
        // images that are listening to reactants
        for ( ArrayList<SubstanceImageNode> list : imageNodeLists ) {
            for ( SubstanceImageNode node : list ) {
                node.cleanup();
            }
        }
    }
    
    /*
     * Sets the visibility of reaction molecule images.
     * Intended for use by subclasses that may want to hide images.
     */
    protected void setReactionImagesVisible( boolean visible ) {
        imageLayoutNode.setVisible( visible );
        
    }
    
    protected void setValueNodeImagesVisible( boolean visible ) {
        for ( ValueNode valueNode : reactantValueNodes ) {
            valueNode.setImageVisible( visible );
        }
    }
    
    /*
     * Sets the visibility of the numeric values below the box.
     * Intended for use by subclasses that may want to hide numbers.
     */
    protected void setNumbersVisible( boolean visible ) {
        for ( ValueNode valueNode : reactantValueNodes ) {
            valueNode.setValueVisible( visible );
            valueNode.setHistogramBarVisible( visible );
        }
    }
    
    /*
     * Gets a list of the reactant value nodes.
     * Intended for use by subclasses that need to fiddle with these.
     */
    protected ArrayList<QuantityValueNode> getReactantValueNodes() {
        return reactantValueNodes;
    }
    
    /*
     * For each reactant, update number of images to match the quantity.
     */
    private void updateImages() {
     
        // remove all images first, so that there's room in the layout...
        Reactant[] reactants = reaction.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = imageNodeLists.get( i );
            while ( reactant.getQuantity() < imageNodes.size() ) {
                SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                imageNode.cleanup();
                imageLayoutNode.removeNode( imageNode );
                imageNodes.remove( imageNode );
            }
        }
        
        // ...then add all images
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = imageNodeLists.get( i );
            while ( reactant.getQuantity() > imageNodes.size() ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                imageLayoutNode.addNode( imageNode, reactantValueNodes.get( i ) );
            }
        }
    }
}
