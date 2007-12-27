/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.ToolboxNode;
import edu.colorado.phet.glaciers.control.ToolIconNode.ToolIconListener;
import edu.colorado.phet.glaciers.defaults.AdvancedDefaults;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.view.ToolNodeFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;

/**
 * AdvancedCanvas
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private AdvancedModel _model;
    
    // View 
    private PNode _rootNode;
    private ToolboxNode _toolboxNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AdvancedCanvas( AdvancedModel model ) {
        super( AdvancedDefaults.WORLD_SIZE );
        
        _model = model;
        
        setBackground( GlaciersConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addScreenChild( _rootNode );
        
        // Toolbox
        _toolboxNode = new ToolboxNode();
        _rootNode.addChild( _toolboxNode );
        _toolboxNode.addListener( new ToolIconListener() {
            public void addTool( AbstractTool tool ) {
                PNode node = ToolNodeFactory.createNode( tool );
                node.addInputEventListener( new CursorHandler() );
                node.addInputEventListener( new PDragEventHandler() );
                _rootNode.addChild( node );
                _model.addModelElement( tool );
            }
        });
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
    protected void updateLayout() {

        Dimension2D screenSize = getScreenSize();
        if ( screenSize.getWidth() <= 0 || screenSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GlaciersConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout screenSize=" + screenSize );//XXX
        }
        
        // Toolbox at the bottom of the play area
        _toolboxNode.setOffset( 20, screenSize.getHeight() - _toolboxNode.getFullBoundsReference().getHeight() - 5 );
    }
}
