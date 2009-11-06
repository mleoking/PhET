package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ReactantQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all "Before Reaction" displays.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractBeforeNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    private static final double TITLE_Y_SPACING = 10;
    private static final double CONTROLS_Y_SPACING = 15;
    private static final double IMAGE_SCALE = 0.25; //XXX
    
    private final ChemicalReaction reaction;
    private final ChangeListener reactionChangeListener;

    private final BoxNode boxNode;
    private final ArrayList<PComposite> imageNodeParents; // parents for reactant images
    private final ArrayList<ArrayList<SubstanceImageNode>> imageNodeLists; // one list of images per reactant
    private final ArrayList<ReactantQuantityControlNode> quantityControlNodes; // quantity displays for reactants
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
        
        imageNodeParents = new ArrayList<PComposite>();
        imageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        quantityControlNodes = new ArrayList<ReactantQuantityControlNode>();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        PText titleNode = new PText( title );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // images and controls
        Reactant[] reactants = reaction.getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one parent node for each reactant image
            PComposite parent = new PComposite();
            addChild( parent );
            imageNodeParents.add( parent );
            
            // one list of image nodes for each reactant 
            imageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one quantity control for each reactant
            ReactantQuantityControlNode controlNode = new ReactantQuantityControlNode( reactant, quantityRange, IMAGE_SCALE, showSubstanceNames );
            addChild( controlNode );
            quantityControlNodes.add( controlNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        boxNode.setOffset( x, y );
        // title centered above box
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - TITLE_Y_SPACING;
        titleNode.setOffset( x, y );
        // reactant-specific nodes, horizontally centered in "cells"
        double margin = ( reactants.length > 2 ) ? 0 : ( 0.15 * BOX_SIZE.getWidth() ); // make 2 reactants case look nice
        final double deltaX = ( boxNode.getFullBoundsReference().getWidth() - ( 2 * margin ) ) / ( reactants.length );
        x = boxNode.getFullBoundsReference().getMinX() + margin + ( deltaX / 2 );
        for ( int i = 0; i < reactants.length; i++ ) {
            
            // quantity controls
            y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
            quantityControlNodes.get( i ).setOffset( x, y );
            
            // image parents, same offset as box
            imageNodeParents.get( i ).setOffset( boxNode.getOffset() );
            
            x += deltaX;
        }
        
        update();
    }
    
    /**
     * Cleans up all listeners that could cause memory leaks.
     */
    public void cleanup() {
        reaction.removeChangeListener( reactionChangeListener );
        // controls that are listening to reactants
        for ( ReactantQuantityControlNode node : quantityControlNodes ) {
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
            PNode parent = imageNodeParents.get( i );
            ArrayList<SubstanceImageNode> imageNodes = imageNodeLists.get( i );
            
            if ( reactant.getQuantity() < imageNodes.size() ) {
                // remove images
                while ( reactant.getQuantity() < imageNodes.size() ) {
                    SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                    imageNode.cleanup();
                    parent.removeChild( imageNode );
                    imageNodes.remove( imageNode );
                }
            }
            else {
                // add images
                PNode previousNode = null;
                if ( parent.getChildrenCount() > 0 ) {
                    previousNode = parent.getChild( parent.getChildrenCount() - 1 );
                }
                
                while( reactant.getQuantity() > imageNodes.size() ) {
                    
                    SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                    imageNode.scale( IMAGE_SCALE );
                    parent.addChild( imageNode );
                    imageNodes.add( imageNode );
                    
                    Point2D offset = imageLayoutStrategy.getOffset( imageNode, previousNode, boxNode, quantityControlNodes.get( i ) );
                    imageNode.setOffset( offset );
                    previousNode = imageNode;
                }
            }
        }
    }
}
