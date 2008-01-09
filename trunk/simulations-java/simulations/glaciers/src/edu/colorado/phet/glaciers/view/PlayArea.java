/* Copyright 2007-2008, University of Colorado */ 

package edu.colorado.phet.glaciers.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JPanel;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.ToolboxNode;
import edu.colorado.phet.glaciers.model.AbstractModel;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer.ToolProducerListener;
import edu.colorado.phet.glaciers.view.Viewport.ViewportListener;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

/**
 * PlayArea
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlayArea extends JPanel implements ToolProducerListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BIRDS_EYE_VIEW_HEIGHT = 75; // pixels, height of top panel will be constrained to this many pixels
    private static final double BIRDS_EYE_VIEW_SCALE = 0.2;
    private static final double ZOOMED_VIEW_SCALE = 1;
    private static final Color CANVAS_BACKGROUND = new Color( 180, 158, 134 ); // tan, should match the ground color in the valley image
    private static final float VIEWPORT_STROKE_WIDTH = 4;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private AbstractModel _model;
    
    // View
    private PhetPCanvas _birdsEyeCanvas, _zoomedCanvas;
    private Viewport _birdsEyeViewport, _zoomedViewport;
    private PLayer _valleyLayer, _glacierLayer, _toolboxLayer, _toolsLayer, _viewportLayer;
    private ToolboxNode _toolboxNode;
    private PNode _penguinNode;
    private HashMap _toolsMap; // key=AbstractTool, value=AbstractToolNode, used for removing tool nodes when their model elements are deleted

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PlayArea( AbstractModel model ) {
        super();
        
        _model = model;
        _model.addToolProducerListener( this ); // manage nodes when tools are added/removed
        
        // viewports
        _birdsEyeViewport = new Viewport(); // bounds will be set when top canvas is resized
        _zoomedViewport = new Viewport(); // bounds will be set when bottom canvas is resized
        _zoomedViewport.addListener( new ViewportListener() {
            public void boundsChanged() {
                handleZoomedViewportChanged();
            }
        });
        
        // "birds-eye" view, has a fixed height
        _birdsEyeCanvas = new PhetPCanvas();
        _birdsEyeCanvas.setBackground( CANVAS_BACKGROUND );
        _birdsEyeCanvas.getCamera().setViewScale( BIRDS_EYE_VIEW_SCALE );
        JPanel topPanel = new JPanel( new BorderLayout() );
        topPanel.add( Box.createVerticalStrut( (int) BIRDS_EYE_VIEW_HEIGHT ), BorderLayout.WEST ); // fixed height
        topPanel.add( _birdsEyeCanvas, BorderLayout.CENTER );
        
        // "zoomed" view
        _zoomedCanvas = new PhetPCanvas();
        _zoomedCanvas.setBackground( CANVAS_BACKGROUND );
        _zoomedCanvas.getCamera().setViewScale( ZOOMED_VIEW_SCALE );
        
        // Layout, birds-eye view above zoomed view, zoomed view grows/shrinks to fit
        setLayout( new BorderLayout() );
        add( topPanel, BorderLayout.NORTH );
        add( _zoomedCanvas, BorderLayout.CENTER );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                handlePlayAreaResized();
            }
        });
        
        // Layers, back to front
        _valleyLayer = new PLayer();
        _glacierLayer = new PLayer();
        _toolboxLayer = new PLayer();
        _toolsLayer = new PLayer();
        _viewportLayer = new PLayer();
        addToBothViews( _valleyLayer );
        addToBothViews( _glacierLayer );
        addToZoomedView( _toolboxLayer );
        addToBothViews( _toolsLayer );
        addToBirdsEyeView( _viewportLayer );
        
        // viewport in the birds-eye view indicates what is shown in zoomed view
        ViewportNode viewportNode = new ViewportNode( _zoomedViewport, VIEWPORT_STROKE_WIDTH );
        _viewportLayer.addChild( viewportNode );
        
        // Valley
        PNode valleyNode = new ValleyNode();
        _valleyLayer.addChild( valleyNode );
        
        // Toolbox
        _toolsMap = new HashMap();
        _toolboxNode = new ToolboxNode( _model );
        _toolboxLayer.addChild( _toolboxNode );
        
        // Penguin is the control for moving the zoomed viewport
        _penguinNode = new PenguinNode( _birdsEyeViewport, _zoomedViewport );
        _viewportLayer.addChild( _penguinNode );
        _penguinNode.setOffset( 100, 0 );

        // initialize
        handlePlayAreaResized();
        handleZoomedViewportChanged();
    }
    
    public void cleanup() {
        _model.removeToolProducerListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Management of layers, viewports and layout
    //----------------------------------------------------------------------------
    
    /*
     * Adds layer to the birds-eye view.
     */
    private void addToBirdsEyeView( PLayer layer ) {
        _birdsEyeCanvas.getCamera().addLayer( layer );
        _birdsEyeCanvas.getRoot().addChild( layer );
    }
    
    /*
     * Adds a layer to the zoomed view.
     */
    private void addToZoomedView( PLayer layer ) {
        _zoomedCanvas.getCamera().addLayer( layer );
        _zoomedCanvas.getRoot().addChild( layer );
    }
    
    /*
     * Adds a layer to both the birds-eye and zoomed views.
     */
    private void addToBothViews( PLayer layer ) {
        addToBirdsEyeView( layer );
        addToZoomedView( layer );
    }
    
    /*
     * When the zoomed viewport changes...
     */
    private void handleZoomedViewportChanged() {
        
        // translate the zoomed view's camera
        Rectangle2D viewportBounds = _zoomedViewport.getBounds();
        double scale = _zoomedCanvas.getCamera().getViewScale();
        _zoomedCanvas.getCamera().setViewOffset( -viewportBounds.getX() * scale, -viewportBounds.getY() * scale );
        
        // move the toolbox
        updateToolboxPosition();
    }
    
    /*
     * When the play area is resized...
     */
    private void handlePlayAreaResized() {
        
        Dimension2D screenSize = _birdsEyeCanvas.getScreenSize();
        if ( screenSize.getWidth() <= 0 || screenSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GlaciersConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PlayArea.handleTopCanvasResized screenSize=" + screenSize );//XXX
        }
        
        // set the dimensions of the birds-eye viewport, based on the screen size
        {
            double w = _birdsEyeCanvas.getScreenSize().getWidth() / _birdsEyeCanvas.getCamera().getViewScale();
            double h = _birdsEyeCanvas.getScreenSize().getHeight() / _birdsEyeCanvas.getCamera().getViewScale();
            _birdsEyeViewport.setBounds( new Rectangle2D.Double( 0, 0, w, h ) );
        }
        
        // set the dimensions of the zoomed viewport, based on the size of the zoomed canvas
        {
            Rectangle2D canvasBounds = _zoomedCanvas.getBounds();
            double scale = _zoomedCanvas.getCamera().getViewScale();
            Rectangle2D viewportBounds = _zoomedViewport.getBounds();
            double x = viewportBounds.getX();
            double y = viewportBounds.getY();
            double w = canvasBounds.getWidth() / scale;
            double h = canvasBounds.getHeight() / scale;
            _zoomedViewport.setBounds( new Rectangle2D.Double( x, y, w, h ) );
        }
        
        // keep the zoomed viewport inside the birds-eye view's bounds
        Rectangle2D bb = _birdsEyeViewport.getBoundsReference();
        Rectangle2D zb = _zoomedViewport.getBoundsReference();
        if ( !bb.contains( zb ) ) {
            double dx = bb.getMaxX() - zb.getMaxX(); // viewport only moves horizontally
            _zoomedViewport.translate( dx, 0 );
        }
        
        // move the toolbox
        updateToolboxPosition();
    }
    
    /*
     * Moves the toolbox to the lower-left corner of the zoomed view
     */
    private void updateToolboxPosition() {
        Rectangle2D viewportBounds = _zoomedViewport.getBounds();
        double xOffset = viewportBounds.getX() + 5;
        double yOffset = viewportBounds.getY() + viewportBounds.getHeight() - _toolboxNode.getFullBoundsReference().getHeight() - 5;
        _toolboxNode.setOffset( xOffset, yOffset );
    }
    
    //----------------------------------------------------------------------------
    // ToolProducerListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When a tool is added to the model, create a node for it.
     * 
     * @param tool
     */
    public void toolAdded( AbstractTool tool ) {
        PNode node = ToolNodeFactory.createNode( tool );
        _toolsLayer.addChild( node );
        _toolsMap.put( tool, node );
    }

    /**
     * When a tool is removed from the mode, remove its corresponding node.
     * 
     * @param tool
     */
    public void toolRemoved( AbstractTool tool ) {
        AbstractToolNode toolNode = (AbstractToolNode)_toolsMap.get( tool );
        _toolsLayer.removeChild( toolNode );
        _toolsMap.remove( tool );
    }
}
