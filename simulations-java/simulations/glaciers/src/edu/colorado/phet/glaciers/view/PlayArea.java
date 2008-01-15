/* Copyright 2007-2008, University of Colorado */ 

package edu.colorado.phet.glaciers.view;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.ToolboxNode;
import edu.colorado.phet.glaciers.model.AbstractModel;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.TracerFlag;
import edu.colorado.phet.glaciers.model.Viewport;
import edu.colorado.phet.glaciers.model.IToolProducer.ToolProducerListener;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
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
    
    // constant height of the birds-eye view, in pixels
    private static final double BIRDS_EYE_VIEW_HEIGHT = 75;
    
    // camera view scales
    private static final double BIRDS_EYE_CAMERA_VIEW_SCALE = 0.2;
    private static final double ZOOMED_CAMERA_VIEW_SCALE = 1;
    
    // width of the stroke used to display the zoomed viewport, in view coordinates
    private static final float VIEWPORT_STROKE_WIDTH = 4;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private AbstractModel _model;
    private Viewport _birdsEyeViewport, _zoomedViewport;
    
    // View
    private PhetPCanvas _birdsEyeCanvas, _zoomedCanvas;
    private PLayer _valleyLayer, _glacierLayer, _toolboxLayer, _toolsLayer, _viewportLayer;
    private ToolboxNode _toolboxNode;
    private PNode _penguinNode;
    private HashMap _toolsMap; // key=AbstractTool, value=AbstractToolNode, used for removing tool nodes when their model elements are deleted
    private ModelViewTransform _mvt;
    private Point2D _birdsEyeCameraViewOffset; // store this because there's no way to get it from the PCamera
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PlayArea( AbstractModel model, ModelViewTransform mvt ) {
        super();
        
        assert( ZOOMED_CAMERA_VIEW_SCALE >= BIRDS_EYE_CAMERA_VIEW_SCALE );
        assert( BIRDS_EYE_CAMERA_VIEW_SCALE > 0 );
        assert( ZOOMED_CAMERA_VIEW_SCALE > 0 );
        
        _model = model;
        _model.addToolProducerListener( this ); // manage nodes when tools are added/removed
        
        _mvt = mvt;
        
        // viewports
        _birdsEyeViewport = new Viewport( "birds-eye" ); // bounds will be set when top canvas is resized
        _birdsEyeViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                handleBirdsEyeViewportChanged();
            }
        });
        _zoomedViewport = new Viewport( "zoomed" ); // bounds will be set when bottom canvas is resized
        _zoomedViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                handleZoomedViewportChanged();
            }
        });
        
        // "birds-eye" view, has a fixed height
        _birdsEyeCanvas = new PhetPCanvas();
        _birdsEyeCanvas.setBackground( GlaciersConstants.BIRDS_EYE_CANVAS_COLOR );
        _birdsEyeCanvas.getCamera().setViewScale( BIRDS_EYE_CAMERA_VIEW_SCALE );
        JPanel topPanel = new JPanel( new BorderLayout() );
        topPanel.add( Box.createVerticalStrut( (int) BIRDS_EYE_VIEW_HEIGHT ), BorderLayout.WEST ); // fixed height
        topPanel.add( _birdsEyeCanvas, BorderLayout.CENTER );
        
        // "zoomed" view
        _zoomedCanvas = new PhetPCanvas();
        _zoomedCanvas.setBackground( GlaciersConstants.ZOOMED_CANVAS_COLOR );
        _zoomedCanvas.getCamera().setViewScale( ZOOMED_CAMERA_VIEW_SCALE );
        // zoomed camera offset will be set based on viewport position
        
        // Layout, birds-eye view above zoomed view, zoomed view grows/shrinks to fit
        setLayout( new BorderLayout() );
        add( topPanel, BorderLayout.NORTH );
        add( _zoomedCanvas, BorderLayout.CENTER );
        
        // update layout when the play area is resized
        PlayAreaResizeListener resizeListener = new PlayAreaResizeListener( this );
        this.addComponentListener( resizeListener );
        this.addAncestorListener( resizeListener );
        
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
        ViewportNode viewportNode = new ViewportNode( _zoomedViewport, VIEWPORT_STROKE_WIDTH, _mvt );
        _viewportLayer.addChild( viewportNode );
        
        // Valley
        PNode valleyNode = new ValleyNode( _model.getValley(), _mvt );
        _valleyLayer.addChild( valleyNode );
        
        // Glacier
        PNode glacierNode = new GlacierNode( _model.getGlacier(), _mvt );
        _glacierLayer.addChild( glacierNode );
        
        // Toolbox
        _toolsMap = new HashMap();
        _toolboxNode = new ToolboxNode( _model, _mvt );
        _toolboxLayer.addChild( _toolboxNode );
        
        // Penguin is the control for moving the zoomed viewport
        _penguinNode = new PenguinNode( _birdsEyeViewport, _zoomedViewport, _mvt );
        _viewportLayer.addChild( _penguinNode );
        
        //XXX debug: put a tracer flag at the Valley's high point
        double x = 0;
        double y = _model.getValley().getElevation( x );
        Point2D highPoint =  new Point2D.Double( x, y );
        System.out.println( "PlayArea.init, placed flag at " + highPoint );//XXX
        TracerFlag flag = new TracerFlag( highPoint, _model.getGlacier() );
        toolAdded( flag );
        
        // birds-eye camera offset
        final double upperLeftX = highPoint.getX() - 50;
        final double upperLeftY = highPoint.getY() + 50;
        Point2D pModel = new Point2D.Double( upperLeftX, upperLeftY );
        Point2D pView = _mvt.modelToView( pModel );
        _birdsEyeCameraViewOffset = new Point2D.Double( pView.getX(), pView.getY() );
        System.out.println( "PlayArea.init birdsEyeCameraViewOffset=" + _birdsEyeCameraViewOffset );//XXX
        _birdsEyeCanvas.getCamera().setViewOffset( _birdsEyeCameraViewOffset.getX(), _birdsEyeCameraViewOffset.getY() );
        
        // zoomed viewport at upper left of birds-eye viewport
        _zoomedViewport.setPosition( upperLeftX, upperLeftY );
    }
    
    public void cleanup() {
        _model.removeToolProducerListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Layer management
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
    
    //----------------------------------------------------------------------------
    // Viewport and layout management
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of the play area when it is resized or made visible.
     */
    private void updateLayout() {

        // set the bounds of the birds-eye viewport, based on the birds-eye canvas size
        updateBirdsEyeViewportBounds();
        
        // set the size of the zoomed viewport, based on the zoomed canvas size
        updateZoomedViewportBounds();

        // keep the zoomed viewport inside the birds-eye viewport
        constrainZoomedViewport();
        
//        System.out.println( "PlayArea.updateLayout birdsEyeViewport.bounds   =" + _birdsEyeViewport.getBoundsReference() );//XXX
//        System.out.println( "PlayArea.updateLayout birdsEyeCamera.viewBounds =" + _birdsEyeCanvas.getCamera().getViewBounds() );//XXX
//        System.out.println( "PlayArea.updateLayout zoomedViewport.bounds     =" + _zoomedViewport.getBoundsReference() );//XXX
//        System.out.println( "PlayArea.updateLayout zoomedCamera.viewBounds   =" + _zoomedCanvas.getCamera().getViewBounds() );//XXX
    }
    
    /*
     * Updates the bounds of the birds-eye viewport, based on the size of the birds-eye canvas.
     */
    private void updateBirdsEyeViewportBounds() {
        Rectangle2D bb = _birdsEyeCanvas.getBounds();
        assert ( !bb.isEmpty() );
        double scale = _birdsEyeCanvas.getCamera().getViewScale();
        Rectangle2D rView = new Rectangle2D.Double( _birdsEyeCameraViewOffset.getX(), _birdsEyeCameraViewOffset.getY(), bb.getWidth() / scale, bb.getHeight() / scale );
        Rectangle2D rModel = _mvt.viewToModel( rView );
        _birdsEyeViewport.setBounds( rModel );
    }
    
    /*
     * Updates the bounds of the zoomed viewport based on the size of the zoomed canvas.
     */
    private void updateZoomedViewportBounds() {
        Rectangle2D zb = _zoomedCanvas.getBounds();
        assert ( !zb.isEmpty() );
        double scale = _zoomedCanvas.getCamera().getViewScale();
        Rectangle2D rView = new Rectangle2D.Double( 0, 0, zb.getWidth() / scale, zb.getHeight() / scale );
        Rectangle2D rModel = _mvt.viewToModel( rView );
        rModel.setRect( _zoomedViewport.getX(), _birdsEyeViewport.getY(), rModel.getWidth(), rModel.getHeight() );
        _zoomedViewport.setBounds( rModel );
    }
    
    /* 
     * Keeps the left & right edges of the zoomed viewport inside the birds-eye viewport.
     */
    private void constrainZoomedViewport() {
        Rectangle2D bb = _birdsEyeViewport.getBoundsReference();
        Rectangle2D zb = _zoomedViewport.getBoundsReference();
        if ( zb.getX() < bb.getX() ) {
            double dx = bb.getX() - zb.getX();
            _zoomedViewport.translate( dx, 0 );
        }
        else if ( zb.getMaxX() > bb.getMaxX() ) {
            double dx = zb.getMaxX() - bb.getMaxX();
            _zoomedViewport.translate( -dx, 0 );
        }
    }
    
    /* 
     * When the birds-eye viewport changes...
     */
    private void handleBirdsEyeViewportChanged() {
        
        // translate the birds-eye view's camera
        Rectangle2D rModel = _birdsEyeViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double scale = _birdsEyeCanvas.getCamera().getViewScale();
        _birdsEyeCanvas.getCamera().setViewOffset( -rView.getX() * scale, -rView.getY() * scale );
    }
    
    /*
     * When the zoomed viewport changes...
     */
    private void handleZoomedViewportChanged() {
        
        // translate the zoomed view's camera
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double scale = _zoomedCanvas.getCamera().getViewScale();
        _zoomedCanvas.getCamera().setViewOffset( -rView.getX() * scale, -rView.getY() * scale );
        
        // move the toolbox
        updateToolboxPosition();
    }
    
    /*
     * Moves the toolbox to the lower-left corner of the zoomed view
     */
    private void updateToolboxPosition() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double xOffset = rView.getX() + 5;
        double yOffset = rView.getY() + rView.getHeight() - _toolboxNode.getFullBoundsReference().getHeight() - 5;
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
        PNode node = ToolNodeFactory.createNode( tool, _mvt );
        _toolsLayer.addChild( node );
        _toolsMap.put( tool, node );
    }

    /**
     * When a tool is removed from the model, remove its corresponding node.
     * 
     * @param tool
     */
    public void toolRemoved( AbstractTool tool ) {
        AbstractToolNode toolNode = (AbstractToolNode)_toolsMap.get( tool );
        _toolsLayer.removeChild( toolNode );
        _toolsMap.remove( tool );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * PlayAreaResizeListener listens for size and visibility changes to the play area.
     */
    private static class PlayAreaResizeListener extends ComponentAdapter implements AncestorListener {

        private PlayArea _playArea;
        private boolean _layoutDirty;

        public PlayAreaResizeListener( PlayArea playArea ) {
            _playArea = playArea;
            _layoutDirty = true;
        }

        // If the play area is resized while visible, update the layout.
        // Otherwise, mark the layout as dirty and wait for it to become visible.
        public void componentResized( ComponentEvent e ) {
            if ( _playArea.isShowing() ) {
                _playArea.updateLayout();
                _layoutDirty = false;
            }
            else {
                _layoutDirty = true;
            }
        }

        // Called when the play area or one of its ancestors is make visible, either
        // by setVisible(true) being called or by its being added to the component hierarchy.
        // If the layout is dirty when this happens, update the layout.
        public void ancestorAdded( AncestorEvent e ) {
            if ( _layoutDirty && _playArea.isShowing() ) {
                _playArea.updateLayout();
                _layoutDirty = false;
            }
        }

        public void ancestorMoved( AncestorEvent event ) {}

        public void ancestorRemoved( AncestorEvent event ) {}
    }
}
