/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.ToolboxNode;
import edu.colorado.phet.glaciers.control.ToolIconNode.ToolIconListener;
import edu.colorado.phet.glaciers.defaults.BasicDefaults;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.view.BirdsEyeViewNode;
import edu.colorado.phet.glaciers.view.MagnifiedViewNode;
import edu.colorado.phet.glaciers.view.PenguinNode;
import edu.colorado.phet.glaciers.view.ToolNodeFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * BasicCanvas
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private BasicModel _model;
    
    // View 
    private PNode _rootNode;
    private BirdsEyeViewNode _birdsEyeViewNode;
    private PenguinNode _penguinNode;
    private MagnifiedViewNode _magnifiedViewNode;
    private ToolboxNode _toolboxNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BasicCanvas( BasicModel model ) {
        super( BasicDefaults.WORLD_SIZE );
        
        _model = model;
        
        setBackground( GlaciersConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addScreenChild( _rootNode );
        
        // Birds Eye View
        {
            _birdsEyeViewNode = new BirdsEyeViewNode();
            _rootNode.addChild( _birdsEyeViewNode );
            _birdsEyeViewNode.setOffset( 0, 0 ); // upper left
        }
        
        // Penguin
        {
            PPath penguinDragBoundsNode = new PPath();
            penguinDragBoundsNode.setStroke( null );
            _rootNode.addChild( penguinDragBoundsNode );

            try {
                _penguinNode = new PenguinNode( this, penguinDragBoundsNode );
            }
            catch ( IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            _rootNode.addChild( _penguinNode );
            PBounds bb = _birdsEyeViewNode.getFullBoundsReference();
            double x = bb.getX() + ( bb.getWidth() / 2 );
            double y = bb.getY() + bb.getHeight() - _penguinNode.getFullBoundsReference().getHeight();
            _penguinNode.setOffset( x, y );
            PBounds pb = _penguinNode.getFullBoundsReference();

            Rectangle2D r = new Rectangle2D.Double( bb.getX(), pb.getY(), bb.getWidth(), pb.getHeight() );
            penguinDragBoundsNode.setPathTo( r );
        }
        
        // Magnified View
        {
            _magnifiedViewNode = new MagnifiedViewNode();
            _rootNode.addChild( _magnifiedViewNode );
            
            double x = 0;
            double y = _birdsEyeViewNode.getFullBoundsReference().getMaxY();
            _magnifiedViewNode.setOffset( x, y );
        }
        
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
    
    public BirdsEyeViewNode getBirdsEyeViewNode() {
        return _birdsEyeViewNode;
    }
    
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
