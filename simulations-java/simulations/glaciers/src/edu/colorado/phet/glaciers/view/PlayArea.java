/* Copyright 2007, University of Colorado */ 

package edu.colorado.phet.glaciers.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.JPanel;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.ToolboxNode;
import edu.colorado.phet.glaciers.control.ToolIconNode.ToolIconListener;
import edu.colorado.phet.glaciers.model.AbstractModel;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.Viewport;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;

/**
 * PlayArea
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlayArea extends JPanel {
    
    private static final double TOP_PANEL_HEIGHT = 75; // height of top panel will be constrained to this many pixels
    private static final double TOP_SCALE = 0.2;
    private static final double BOTTOM_SCALE = 1;
    private static final Color CANVAS_BACKGROUND = new Color( 180, 158, 134 ); // tan, should match the ground color in the valley image
    private static final float VIEWPORT_STROKE_WIDTH = 4;
    
    private AbstractModel _model;
    
    private Viewport _viewport;
    private PhetPCanvas _topCanvas, _bottomCanvas;
    
    private PLayer _valleyLayer, _glacierLayer, _toolboxLayer, _toolsLayer, _viewportLayer;
    private ToolboxNode _toolboxNode;

    public PlayArea( AbstractModel model ) {
        super();
        
        _model = model;
        
        // top canvas shows "birds-eye" view, has a fixed height
        _topCanvas = new PhetPCanvas();
        _topCanvas.setBackground( CANVAS_BACKGROUND );
        _topCanvas.getCamera().setViewScale( TOP_SCALE );
        JPanel topPanel = new JPanel( new BorderLayout() );
        topPanel.add( Box.createVerticalStrut( (int) TOP_PANEL_HEIGHT ), BorderLayout.WEST );
        topPanel.add( _topCanvas, BorderLayout.CENTER );
        
        // bottom canvas shows "zoomed" view
        _bottomCanvas = new PhetPCanvas();
        _bottomCanvas.setBackground( CANVAS_BACKGROUND );
        _bottomCanvas.getCamera().setViewScale( BOTTOM_SCALE );
        _bottomCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                handleBottomCanvasResized();
            }
        } );
        
        // Layout
        setLayout( new BorderLayout() );
        add( topPanel, BorderLayout.NORTH );
        add( _bottomCanvas, BorderLayout.CENTER );
        
        // Layers, back to front
        _valleyLayer = new PLayer();
        _glacierLayer = new PLayer();
        _toolboxLayer = new PLayer();
        _toolsLayer = new PLayer();
        _viewportLayer = new PLayer();
        addToTopAndBottom( _valleyLayer );
        addToTopAndBottom( _glacierLayer );
        addToTopAndBottom( _toolsLayer );
        addToBottom( _toolboxLayer );
        addToTop( _viewportLayer );
        
        // viewport in the top canvas determines what is shown in the bottom canvas
        _viewport = new Viewport( new Rectangle2D.Double( 0, 0, 1, 1 ) ); // don't care about initial width & height, they will be adjusted
        _viewport.addListener( new ViewportListener() {
            public void boundsChanged() {
                handleViewportBoundsChanged();
            }
        });
        ViewportNode viewportNode = new ViewportNode( _viewport, VIEWPORT_STROKE_WIDTH );
        _viewportLayer.addChild( viewportNode );
        
        // Valley
        PNode valleyNode = new ValleyNode();
        _valleyLayer.addChild( valleyNode );
        
        // Toolbox
        _toolboxNode = new ToolboxNode();
        _toolboxLayer.addChild( _toolboxNode );
        _toolboxNode.addListener( new ToolIconListener() {
            public void addTool( AbstractTool tool ) {
                PNode node = ToolNodeFactory.createNode( tool );
                node.addInputEventListener( new CursorHandler() );
                node.addInputEventListener( new PDragEventHandler() );
                _toolsLayer.addChild( node );
                _model.addModelElement( tool );
            }
        });
        
        // initialize
        handleBottomCanvasResized();
        handleViewportBoundsChanged();
    }
    
    public void addToTop( PLayer layer ) {
        _topCanvas.getCamera().addLayer( layer );
    }
    
    public void addToBottom( PLayer layer ) {
        _bottomCanvas.getCamera().addLayer( layer );
    }
    
    public void addToTopAndBottom( PLayer layer ) {
        addToTop( layer );
        addToBottom( layer );
    }
    
    /*
     * When the viewport bounds change, translate the camera.
     */
    private void handleViewportBoundsChanged() {
        
        // translate the bottom canvas' camera
        Rectangle2D viewportBounds = _viewport.getBounds();
        double scale = _bottomCanvas.getCamera().getViewScale();
        _bottomCanvas.getCamera().setViewOffset( -viewportBounds.getX() * scale, -viewportBounds.getY() * scale );
        
        // move the toolbox
        updateToolboxPosition();
    }
    
    /* 
     * When the bottom canvas is resized...
     */
    private void handleBottomCanvasResized() {
        
        Dimension2D screenSize = _bottomCanvas.getScreenSize();
        if ( screenSize.getWidth() <= 0 || screenSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GlaciersConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout screenSize=" + screenSize );//XXX
        }
        
        // resize the viewport
        Rectangle2D canvasBounds = _bottomCanvas.getBounds();
        double scale = _bottomCanvas.getCamera().getViewScale();
        Rectangle2D viewportBounds = _viewport.getBounds();
        double x = viewportBounds.getX();
        double y = viewportBounds.getY();
        double w = canvasBounds.getWidth() / scale;
        double h = canvasBounds.getHeight() / scale;
        _viewport.setBounds( new Rectangle2D.Double( x, y, w, h ) );
        
        // move the toolbox
        updateToolboxPosition();
    }
    
    /*
     * Moves toolbox to lower-left corner of bottom canvas
     */
    private void updateToolboxPosition() {
        Rectangle2D viewportBounds = _viewport.getBounds();
        double xOffset = viewportBounds.getX() + 5;
        double yOffset = viewportBounds.getY() + viewportBounds.getHeight() - _toolboxNode.getFullBoundsReference().getHeight() - 5;
        _toolboxNode.setOffset( xOffset, yOffset );
    }
}
