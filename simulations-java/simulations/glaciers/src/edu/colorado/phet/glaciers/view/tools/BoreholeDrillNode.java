/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.piccolophet.nodes.ImageButtonNode;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.BoreholeDrill;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BoreholeDrillNode is the visual representation of a borehole drill.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoreholeDrillNode extends AbstractToolNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // can the drill be dragged while its button is pressed?
    private static final boolean ALLOW_DRAGGING_WHILE_BUTTON_IS_PRESSED = true;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BoreholeDrillNode( final BoreholeDrill boreholeDrill, ModelViewTransform mvt, TrashCanDelegate trashCan ) {
        super( boreholeDrill, mvt, trashCan );
        
        PNode drillNode = new DrillNode();
        drillNode.scale( 0.75 ); //XXX
        addChild( drillNode );
        
        ButtonNode buttonNode = new ButtonNode();
        buttonNode.scale( 0.7 ); //XXX compute this based on image width
        addChild( buttonNode );
        
        /*
         * NOTE! These offsets were tweaked to line up with specific images.
         * If you change the images, you will need to re-tweak these values.
         */
        drillNode.setOffset( -8, -drillNode.getFullBoundsReference().getHeight() ); // tip of drill bit
        buttonNode.setOffset( -11, -drillNode.getFullBoundsReference().getHeight() + 8 );
        
        buttonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                boreholeDrill.drill();
            }
        });
        
        if ( !ALLOW_DRAGGING_WHILE_BUTTON_IS_PRESSED ) {
            // disable dragging while the button is pressed
            buttonNode.addInputEventListener( new PBasicInputEventHandler() {

                public void mousePressed( PInputEvent event ) {
                    setDraggingEnabled( false );
                }

                public void mouseReleased( PInputEvent event ) {
                    setDraggingEnabled( true );
                }
            } );
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private static class DrillNode extends PComposite {
        public DrillNode() {
            super();
            PImage imageNode = new PImage( GlaciersImages.BOREHOLE_DRILL );
            addChild( imageNode );
        }
    }
    
    private static class ButtonNode extends ImageButtonNode {
        public ButtonNode() {
            super( GlaciersImages.BOREHOLE_DRILL_OFF_BUTTON, GlaciersImages.BOREHOLE_DRILL_ON_BUTTON );
        }
    }
    
    //----------------------------------------------------------------------------
    // Image icon
    //----------------------------------------------------------------------------
    
    public static Image createImage() {
        PImage imageNode = new PImage( GlaciersImages.BOREHOLE_DRILL );
        imageNode.scale( 0.5 );
        //TODO add button
        return imageNode.toImage();
    }
}
