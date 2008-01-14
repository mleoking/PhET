/* Copyright 2007-2008, University of Colorado */ 

package edu.colorado.phet.glaciers.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
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
    private ModelViewTransform _mvt;
    
    private Rectangle2D _rModel, _rView; // reusable rectangles
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PlayArea( AbstractModel model, ModelViewTransform mvt ) {
        super();
        
        _rModel = new Rectangle2D.Double();
        _rView = new Rectangle2D.Double();
        
        _model = model;
        _model.addToolProducerListener( this ); // manage nodes when tools are added/removed
        
        _mvt = mvt;
        
        // viewports
        _birdsEyeViewport = new Viewport( "birds-eye" ); // bounds will be set when top canvas is resized
        _zoomedViewport = new Viewport( "zoomed" ); // bounds will be set when bottom canvas is resized
        _zoomedViewport.setPosition( _mvt.viewToModel( 0, 0 ) ); //XXX initial position at upper left of birds-eye view
        _zoomedViewport.addViewportListener( new ViewportListener() {
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

        // initialize
        handleZoomedViewportChanged();
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

        // make sure the penguin is aligned with bottom of birds-eye viewport, and scaled to fit
        scalePenguinToZoomedViewport();
        centerPenguinInZoomedViewport();
    }
    
    /*
     * Updates the bounds of the birds-eye viewport, based on the size of the birds-eye canvas.
     */
    private void updateBirdsEyeViewportBounds() {
        Rectangle2D bb = _birdsEyeCanvas.getBounds();
        assert ( !bb.isEmpty() );
        double scale = _birdsEyeCanvas.getCamera().getViewScale();
        _rView.setRect( 0, 0, bb.getWidth() / scale, bb.getHeight() / scale );
        _mvt.viewToModel( _rView, _rModel );
        _birdsEyeViewport.setBounds( _rModel );
    }
    
    /*
     * Updates the bounds of the zoomed viewport based on the size of the zoomed canvas.
     */
    private void updateZoomedViewportBounds() {
        Rectangle2D zb = _zoomedCanvas.getBounds();
        assert ( !zb.isEmpty() );
        double scale = _zoomedCanvas.getCamera().getViewScale();
        _rView.setRect( 0, 0, zb.getWidth() / scale, zb.getHeight() / scale );
        _mvt.viewToModel( _rView, _rModel );
        _zoomedViewport.setSize( _rModel.getWidth(), _rModel.getHeight() );
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
     * Centers the penguin in the zoomed viewport.
     */
    private void centerPenguinInZoomedViewport() {
        double yOffset = _mvt.modelToView( 0, _birdsEyeViewport.getBoundsReference().getMaxY() ).getY() - _penguinNode.getFullBoundsReference().getHeight();
        _penguinNode.setOffset( _penguinNode.getXOffset(), yOffset );
    }
    
    /*
     * Scales the penguin to fit into the birds-eye viewport.
     */
    private void scalePenguinToZoomedViewport() {
        final double portionOfViewportToFill = 0.8; // percent of birds-eye view height to be filled by the penguin
        double desiredHeight = portionOfViewportToFill * _mvt.modelToView( 0, _birdsEyeViewport.getBoundsReference().getHeight() ).getY();
        double penguinHeight = _penguinNode.getFullBoundsReference().getHeight();
        double yScale = 1;
        if ( penguinHeight > desiredHeight ) {
            // scale the penguin down
            yScale = 1 - ( penguinHeight - desiredHeight ) / penguinHeight;
        }
        else {
            // scale the penguin up
            yScale = 1 + ( desiredHeight - penguinHeight ) / desiredHeight;
        }
        _penguinNode.setScale( yScale );
    }
    
    /*
     * When the zoomed viewport changes...
     */
    private void handleZoomedViewportChanged() {
        
        // translate the zoomed view's camera
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        _mvt.modelToView(  rModel, _rView );
        double scale = _zoomedCanvas.getCamera().getViewScale();
        _zoomedCanvas.getCamera().setViewOffset( -_rView.getX() * scale, -_rView.getY() * scale );
        
        // move the toolbox
        updateToolboxPosition();
    }
    
    /*
     * Moves the toolbox to the lower-left corner of the zoomed view
     */
    private void updateToolboxPosition() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        _mvt.modelToView( rModel, _rView ); // convert from view to model coordinates
        double xOffset = _rView.getX() + 5;
        double yOffset = _rView.getY() + _rView.getHeight() - _toolboxNode.getFullBoundsReference().getHeight() - 5;
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
     * PlayAreaResizeListener listens for size or visibility changes to the play area.
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
