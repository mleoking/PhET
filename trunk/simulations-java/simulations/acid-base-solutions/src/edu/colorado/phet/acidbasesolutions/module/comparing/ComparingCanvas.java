/* Copyright 2007, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.defaults.SolutionsDefaults;
import edu.colorado.phet.acidbasesolutions.view.ExampleNode;
import edu.umd.cs.piccolo.PNode;

/**
 * ComparingCanvas is the canvas for ComparingModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private ComparingModel _model;
    
    // View 
    private PNode _rootNode;
    private ExampleNode _exampleNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ComparingCanvas( ComparingModel model ) {
        super( SolutionsDefaults.VIEW_SIZE );
        
        _model = model;
        
        setBackground( ABSConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        _exampleNode = new ExampleNode();
        _rootNode.addChild( _exampleNode );
    }
    

    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ExampleNode getExampleNode() {
        return _exampleNode;
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( ABSConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
