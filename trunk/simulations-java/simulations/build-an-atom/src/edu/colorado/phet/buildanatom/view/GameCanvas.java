/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomResources;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.module.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Canvas for the game tab.
 */
public class GameCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private BuildAnAtomModel model;
    
    // View 
    private PNode rootNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GameCanvas( BuildAnAtomModel model ) {
        super( BuildAnAtomDefaults.VIEW_SIZE );
        
        this.model = model;
        
        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );
        
        // TODO: Add at TBD note.
        PText message = new PText("TBD");
        message.setFont( new PhetFont( 200 ) );
        message.setTextPaint( Color.WHITE );
        rootNode.addChild( message );
    }


    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
