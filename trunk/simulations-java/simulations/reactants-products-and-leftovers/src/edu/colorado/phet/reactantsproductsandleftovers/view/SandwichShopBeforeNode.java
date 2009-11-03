package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ReactantQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichShopBeforeNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = new PDimension( 400, 300 );
    private static final double CONTROLS_Y_SPACING = 20;
    private static final double IMAGES_Y_MARGIN = 25;
    private static final double IMAGES_Y_SPACING = 25;
    private static final double REACTANTS_SCALE = 0.5; //XXX
    
    private final SandwichShopModel model;

    private final BoxNode boxNode;
    private final ArrayList<PComposite> imageParents;
    private final ArrayList<ArrayList<PNode>> imageLists; // one list per reactant
    private final ArrayList<ReactantQuantityControlNode> quantityControls;
    
    public SandwichShopBeforeNode( final SandwichShopModel model ) {
        super();
        
        this.model = model;
        model.getReaction().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        imageParents = new ArrayList<PComposite>();
        imageLists = new ArrayList<ArrayList<PNode>>();
        quantityControls = new ArrayList<ReactantQuantityControlNode>();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        PText titleNode = new PText( RPALStrings.LABEL_BEFORE_SANDWICH );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // images and controls
        ArrayList<Reactant> reactants = model.getReaction().getReactantsReference();
        for ( Reactant reactant : reactants ) {
            
            // one parent node for each reactant image
            PComposite parent = new PComposite();
            addChild( parent );
            imageParents.add( parent );
            
            // one list of image nodes for each reactant 
            imageLists.add( new ArrayList<PNode>() );
            
            // one quantity control for each reactant
            ReactantQuantityControlNode controlNode = createReactantQuantityControlNode( reactant );
            addChild( controlNode );
            quantityControls.add( controlNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        boxNode.setOffset( x, y );
        // title centered above box
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 10;
        titleNode.setOffset( x, y );
        // reactant-specific nodes
        //TODO generalize and fix this part of layout
        x = boxNode.getFullBoundsReference().getMinX() + 60;
        for ( int i = 0; i < reactants.size(); i++ ) {
            
            // quantity controls, centered below images
            y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
            quantityControls.get( i ).setOffset( x, y );
            
            // images
            y = boxNode.getFullBoundsReference().getMaxY() - IMAGES_Y_MARGIN;
            imageParents.get( i ).setOffset( x, y );
            
            x += quantityControls.get( i ).getFullBoundsReference().getWidth() + 20;
        }
        
        update();
    }
    
    //XXX push most of this stuff into ReactantQuantityControlNode
    private ReactantQuantityControlNode createReactantQuantityControlNode( final Reactant reactant ) {
        PImage image = new PImage( reactant.getNode().toImage() );
        final ReactantQuantityControlNode node = new ReactantQuantityControlNode( SandwichShopDefaults.QUANTITY_RANGE, image, 0.5 /*XXX */ );
        node.setValue( reactant.getQuantity() );
        node.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                reactant.setQuantity( node.getValue() );
            }
        });
        reactant.addReactantChangeListener( new ReactantChangeAdapter() {
            @Override
            public void quantityChanged() {
                node.setValue( reactant.getQuantity() );
            }
        });
        return node;
    }
    
    /*
     * For each reactant, updates the number of images to match the quantity.
     */
    private void update() {
        
        ArrayList<Reactant> reactants = model.getReaction().getReactantsReference();
        for ( int i = 0; i < reactants.size(); i++ ) {
            
            Reactant reactant = reactants.get( i );
            PNode parent = imageParents.get( i );
            ArrayList<PNode> images = imageLists.get( i );
            
            if ( reactant.getQuantity() < images.size() ) {
                // remove images
                while ( reactant.getQuantity() < images.size() ) {
                    PNode image = images.get( images.size() - 1 );
                    parent.removeChild( image );
                    images.remove( image );
                }
            }
            else {
                // add images
                while( reactant.getQuantity() > images.size() ) {
                    PNode image = new PImage( reactant.getNode().toImage() );
                    image.scale( REACTANTS_SCALE ); //XXX
                    parent.addChild( image );
                    images.add( image );
                    if ( parent.getChildrenCount() > 1 ) {
                        // images are vertically stacked
                        double x = 0;
                        double y = parent.getChild( parent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - IMAGES_Y_SPACING;
                        image.setOffset( x, y );
                    }
                }
            }
        }
    }
    
}
