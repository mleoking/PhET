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
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BoreholeDrillNode is the visual representation of a borehole drill.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoreholeDrillNode extends AbstractToolNode {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BoreholeDrillNode( final BoreholeDrill boreholeDrill, ModelViewTransform mvt, TrashCanIconNode trashCanIconNode ) {
        super( boreholeDrill, mvt, trashCanIconNode );
        
        PNode drillNode = new DrillNode();
        addChild( drillNode );
        
        ButtonNode buttonNode = new ButtonNode();
        buttonNode.scale( 0.4 ); //XXX compute this based on image width
        addChild( buttonNode );
        
        /*
         * NOTE! These offsets were tweaked to line up with specific images.
         * If you change the images, you will need to re-tweak these values.
         */
        drillNode.setOffset( -6, -drillNode.getFullBoundsReference().getHeight() ); // tip of drill bit
        buttonNode.setOffset( -6, -drillNode.getFullBoundsReference().getHeight() + 10 );
        
        buttonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                boreholeDrill.drill();
            }
        });
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
        //TODO add button
        return GlaciersImages.BOREHOLE_DRILL;
    }
}
