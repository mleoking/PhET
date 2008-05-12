/* Copyright 2007-2008, University of Colorado */ 

package edu.colorado.phet.glaciers.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.ScrollArrowNode;
import edu.colorado.phet.glaciers.control.ScrollArrowNode.LeftScrollArrowNode;
import edu.colorado.phet.glaciers.control.ScrollArrowNode.RightScrollArrowNode;
import edu.colorado.phet.glaciers.model.AbstractModel;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Viewport;
import edu.colorado.phet.glaciers.model.IToolProducer.ToolProducerListener;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
import edu.colorado.phet.glaciers.view.tools.AbstractToolNode;
import edu.colorado.phet.glaciers.view.tools.ToolNodeFactory;
import edu.colorado.phet.glaciers.view.tools.ToolboxNode;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

/**
 * PlayArea is the area of the application that constains the birds-eye and zoomed views
 * of the world.  
 * <p>
 * The birds-eye view appears at the top of the play area, and shows a tiny
 * overview picture of the world. A viewport shown in the birds-eye view indicates the 
 * portion of the world shown in the zoomed view. A penguin image can be dragged 
 * horizontally to move the zoomed viewport.
 * <p>
 * The zoomed view appears below the birds-eye view, and displays a zoomed-in 
 * view of a portion of the world.
 * <p>
 * The two views are implemented as separate Piccolo canvases.  The canvases
 * share common layers, and their cameras are manipulated (scales and translated)
 * to display the appropriate portions of the world.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlayArea extends JPanel implements ToolProducerListener {
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_BACKGROUND_IMAGE_ALIGNMENT = true;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // maximum glacier length of interest
    private static final double MAX_GLACIER_LENGTH = 80000;
    
    // constant height of the birds-eye view, in pixels
    private static final double BIRDS_EYE_VIEW_HEIGHT = 75;
    
    // camera view scales
    private static final double BIRDS_EYE_CAMERA_VIEW_SCALE = 0.2;
    private static final double ZOOMED_CAMERA_VIEW_SCALE = 1;
    
    // offset of upper-left corner of birds-eye viewport from highest point on the glacier
    private static final Point2D BIRDS_EYE_VIEWPORT_OFFSET = new Point2D.Double( -1500, +1000 ); // meters
    
    // width of the stroke used to display the zoomed viewport, in pixels
    private static final float VIEWPORT_STROKE_WIDTH = 1;
    
    // properties used to layout the canvases in the play area
    private static final int VERTICAL_CANVAS_SPACING = 4;
    private static final int BORDER_WIDTH = 5;
    private static final Color BACKGROUND_COLOR = GlaciersConstants.CONTROL_PANEL_BACKGROUND_COLOR;
    private static final Border CANVAS_BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final Border PLAY_AREA_BORDER = BorderFactory.createLineBorder( BACKGROUND_COLOR, BORDER_WIDTH );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private AbstractModel _model;
    private Viewport _birdsEyeViewport, _zoomedViewport;
    
    // View
    private PhetPCanvas _birdsEyeCanvas, _zoomedCanvas;
    private PLayer _backgroundLayer, _iceLayer, _velocityLayer, _coordinatesLayer, _toolboxLayer, _toolsLayer, _viewportLayer, _debugLayer;
    private IceFlowNode _iceFlowNode;
    private ToolboxNode _toolboxNode;
    private PNode _penguinNode;
    private EquilibriumLineNode _equilibriumLineNode;
    private ELAValueNode _elaValueNode;
    private ElevationAxisNode _leftElevationAxisNode, _rightElevationAxisNode;
    private DistanceAxisNode _distanceAxisNode;
    private HashMap _toolsMap; // key=AbstractTool, value=AbstractToolNode, used for removing tool nodes when their model elements are deleted
    private ModelViewTransform _mvt;
    private ScrollArrowNode _leftScrollArrowNode, _rightScrollArrowNode;
    
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
        
        // headwall position
        Point2D headwallPosition = _model.getValley().getHeadwallPositionReference();
        
        // birds-eye viewport
        _birdsEyeViewport = new Viewport( "birds-eye" ); // bounds will be set when play area is resized
        _birdsEyeViewport.setPosition( headwallPosition.getX() + BIRDS_EYE_VIEWPORT_OFFSET.getX(), headwallPosition.getY() + BIRDS_EYE_VIEWPORT_OFFSET.getY() );
        _birdsEyeViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                handleBirdsEyeViewportChanged();
            }
        });
        
        // zoomed viewport
        _zoomedViewport = new Viewport( "zoomed" ); // bounds will be set when play area is resized
        _zoomedViewport.setPosition( _birdsEyeViewport.getX(), _birdsEyeViewport.getY() ); // upper-left of birds-eye viewport
        _zoomedViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                verticallyAlignZoomedViewport();
                handleZoomedViewportChanged();
            }
        });

        // birds-eye view
        _birdsEyeCanvas = new PhetPCanvas();
        _birdsEyeCanvas.setBorder( null ); // no border on the canvas, because we use canvas bounds in calculations
        _birdsEyeCanvas.setBackground( GlaciersConstants.BIRDS_EYE_CANVAS_COLOR );
        _birdsEyeCanvas.getCamera().setViewScale( BIRDS_EYE_CAMERA_VIEW_SCALE );
        
        // zoomed view
        _zoomedCanvas = new PhetPCanvas();
        _zoomedCanvas.setBorder( null ); // no border on the canvas, because we use canvas bounds in calculations
        _zoomedCanvas.setBackground( GlaciersConstants.ZOOMED_CANVAS_COLOR );
        _zoomedCanvas.getCamera().setViewScale( ZOOMED_CAMERA_VIEW_SCALE );
        // zoomed camera offset will be set based on viewport position

        // Layout the panel
        {
            // put a border around the birds-eye canvas, and constrain its height
            JPanel birdsEyeWrapperPanel = new JPanel( new BorderLayout() );
            birdsEyeWrapperPanel.add( Box.createVerticalStrut( (int) BIRDS_EYE_VIEW_HEIGHT ), BorderLayout.WEST ); // fixed height
            birdsEyeWrapperPanel.add( _birdsEyeCanvas, BorderLayout.CENTER );
            birdsEyeWrapperPanel.setBorder( CANVAS_BORDER );

            // add a vertical spacer below the birds-eye canvas
            JPanel topPanel = new JPanel( new BorderLayout() );
            topPanel.setBackground( BACKGROUND_COLOR );
            topPanel.add( birdsEyeWrapperPanel, BorderLayout.CENTER );
            topPanel.add( Box.createVerticalStrut( VERTICAL_CANVAS_SPACING ), BorderLayout.SOUTH );

            // put a border around birds-eye canvas
            JPanel bottomPanel = new JPanel( new BorderLayout() );
            bottomPanel.add( _zoomedCanvas, BorderLayout.CENTER );
            bottomPanel.setBorder( CANVAS_BORDER );

            // birds-eye view above zoomed view, make the zoomed canvas fill all available space
            setBorder( PLAY_AREA_BORDER );
            setLayout( new BorderLayout() );
            add( topPanel, BorderLayout.NORTH );
            add( bottomPanel, BorderLayout.CENTER );
        }
        
        // update layout when the play area is resized
        PlayAreaResizeListener resizeListener = new PlayAreaResizeListener( this );
        this.addComponentListener( resizeListener );
        this.addAncestorListener( resizeListener );
        
        // Layers, back to front
        _backgroundLayer = new PLayer();
        _iceLayer = new PLayer();
        _coordinatesLayer = new PLayer();
        _velocityLayer = new PLayer();
        _toolboxLayer = new PLayer();
        _toolsLayer = new PLayer();
        _viewportLayer = new PLayer();
        _debugLayer = new PLayer();
        addToBothViews( _backgroundLayer );
        addToBothViews( _iceLayer );
        addToZoomedView( _velocityLayer );
        addToZoomedView( _coordinatesLayer );
        addToZoomedView( _toolboxLayer );
        addToBothViews( _toolsLayer );
        addToBirdsEyeView( _viewportLayer );
        addToBothViews( _debugLayer );
        
        // viewport in the birds-eye view indicates what is shown in zoomed view
        float strokeWidth = VIEWPORT_STROKE_WIDTH / (float)BIRDS_EYE_CAMERA_VIEW_SCALE;
        ViewportNode viewportNode = new ViewportNode( _zoomedViewport, strokeWidth, _mvt );
        _viewportLayer.addChild( viewportNode );
        
        // Background image (mountains & valley)
        PNode mountainsAndValleyNode = new MountainsAndValleyNode( _mvt );
        _backgroundLayer.addChild( mountainsAndValleyNode );
        
        /*
         * The background image contains a alignment markers at a few (x,F(x)) locations.
         * The alignment markers created here should line up with the ones in the image file.
         * If they don't line up, then adjust scaling and translation in MountainsAndValleyNode.
         */
        if ( DEBUG_BACKGROUND_IMAGE_ALIGNMENT ) {
            
            double x = 0;
            PNode markerNode1 = new MarkerNode();
            markerNode1.setOffset( _mvt.modelToView( x, _model.getValley().getElevation( x ) ) );
            _debugLayer.addChild( markerNode1 );
            
            x = 10000;
            PNode markerNode2 = new MarkerNode();
            markerNode2.setOffset( _mvt.modelToView( x, _model.getValley().getElevation( x ) ) );
            _debugLayer.addChild( markerNode2 );
            
            x = 70000;
            PNode markerNode3 = new MarkerNode();
            markerNode3.setOffset( _mvt.modelToView( x, _model.getValley().getElevation( x ) ) );
            _debugLayer.addChild( markerNode3 );
        }
        
        // Glacier
        IceNode iceNode = new IceNode( _model.getGlacier(), _mvt );
        _iceLayer.addChild( iceNode );
        _iceFlowNode = new IceFlowNode( _model.getGlacier(), _mvt );
        _velocityLayer.addChild( _iceFlowNode );
        
        // Axes
        _leftElevationAxisNode = new ElevationAxisNode( _mvt, GlaciersConstants.ELEVATION_AXIS_RANGE, GlaciersConstants.ELEVATION_AXIS_TICK_SPACING, false );
        _rightElevationAxisNode = new ElevationAxisNode( _mvt, GlaciersConstants.ELEVATION_AXIS_RANGE, GlaciersConstants.ELEVATION_AXIS_TICK_SPACING, true );
        _distanceAxisNode = new DistanceAxisNode( _model.getValley(), _mvt, GlaciersConstants.DISTANCE_AXIS_TICK_SPACING );
        _coordinatesLayer.addChild( _leftElevationAxisNode );
        _coordinatesLayer.addChild( _rightElevationAxisNode );
        _coordinatesLayer.addChild( _distanceAxisNode );
        
        // Equilibrium line
        _equilibriumLineNode = new EquilibriumLineNode( _model.getGlacier(), _mvt );
        _iceLayer.addChild( _equilibriumLineNode );
        
        // Toolbox
        _toolsMap = new HashMap();
        _toolboxNode = new ToolboxNode( _model, _mvt );
        _toolboxLayer.addChild( _toolboxNode );
        
        // ELA value display
        _elaValueNode = new ELAValueNode( _model.getClimate() );
        _elaValueNode.setVisible( PhetApplication.instance().isDeveloperControlsEnabled() );
        _toolboxLayer.addChild( _elaValueNode );
        
        // Penguin is the control for moving the zoomed viewport
        final double maxX = headwallPosition.getX() + MAX_GLACIER_LENGTH;
        _penguinNode = new PenguinNode( _birdsEyeViewport, _zoomedViewport, _mvt, maxX );
        _viewportLayer.addChild( _penguinNode );
        
        // Arrows for moving zoomed viewport
        _leftScrollArrowNode = new LeftScrollArrowNode( _birdsEyeViewport, _zoomedViewport );
        _toolboxLayer.addChild( _leftScrollArrowNode );
        _rightScrollArrowNode = new RightScrollArrowNode( _birdsEyeViewport, _zoomedViewport );
        _toolboxLayer.addChild( _rightScrollArrowNode );
    }
    
    public void cleanup() {
        _model.removeToolProducerListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEquilibriumLineVisible( boolean visible ) {
        _equilibriumLineNode.setVisible( visible );
    }
    
    public void setAxesVisible( boolean visible ) {
        _leftElevationAxisNode.setVisible( visible );
        _rightElevationAxisNode.setVisible( visible );
        _distanceAxisNode.setVisible( visible );
    }
    
    public void setIceFlowVisible( boolean visible ) {
        _iceFlowNode.setVisible( visible );
    }
    
    public static Point2D getBirdsEyeViewportOffset() {
        return new Point2D.Double( BIRDS_EYE_VIEWPORT_OFFSET.getX(), BIRDS_EYE_VIEWPORT_OFFSET.getY() );
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
        
        // set the bounds of the zoomed viewport, based on the zoomed canvas size
        updateZoomedViewportBounds();

        // keep the zoomed viewport inside the birds-eye viewport
        constrainZoomedViewport();
        
//        System.out.println( "PlayArea.updateLayout birdsEyeViewport.bounds   =" + _birdsEyeViewport.getBoundsReference() );//XXX
//        System.out.println( "PlayArea.updateLayout birdsEyeCamera.viewBounds =" + _birdsEyeCanvas.getCamera().getViewBounds() );//XXX
//        System.out.println( "PlayArea.updateLayout zoomedViewport.bounds     =" + _zoomedViewport.getBoundsReference() );//XXX
//        System.out.println( "PlayArea.updateLayout zoomedCamera.viewBounds   =" + _zoomedCanvas.getCamera().getViewBounds() );//XXX
    }
    
    /*
     * Updates the dimensions of the birds-eye viewport, based on the size of the birds-eye canvas.
     * The position of the birds-eye viewport never changes.
     */
    private void updateBirdsEyeViewportBounds() {
        Rectangle2D bb = _birdsEyeCanvas.getBounds();
        assert ( !bb.isEmpty() );
        double scale = _birdsEyeCanvas.getCamera().getViewScale();
        Rectangle2D rView = new Rectangle2D.Double( 0, 0, bb.getWidth() / scale, bb.getHeight() / scale );
        Rectangle2D rModel = _mvt.viewToModel( rView );
        _birdsEyeViewport.setBounds( _birdsEyeViewport.getX(), _birdsEyeViewport.getY(), rModel.getWidth(), rModel.getHeight() );
    }
    
    /*
     * Updates the bounds of the zoomed viewport based on the size of the zoomed canvas.
     */
    private void updateZoomedViewportBounds() {
        Rectangle2D zb = _zoomedCanvas.getBounds();
        assert ( !zb.isEmpty() );
        double scale = _zoomedCanvas.getCamera().getViewScale();
        Rectangle2D rView = _mvt.modelToView( _zoomedViewport.getBoundsReference() );
        rView.setRect( rView.getX(), rView.getY(), zb.getWidth() / scale, zb.getHeight() / scale );
        Rectangle2D rModel = _mvt.viewToModel( rView );
        _zoomedViewport.setBounds( rModel );
    }
    
    /*
     * Vertically aligns the zoomed viewport.
     * Keeps the glacier vertically centered in the viewport.
     */
    private void verticallyAlignZoomedViewport() {
        Rectangle2D rModel = _zoomedViewport.getBounds();
        double centerX = rModel.getCenterX();
        double elevation = _model.getValley().getElevation( centerX );
        double newY = elevation + ( 0.55 * rModel.getHeight() );
        rModel.setRect( rModel.getX(), newY, rModel.getWidth(), rModel.getHeight() );
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
        
        // move the ELA value display
        updateELAValuePosition();
        
        // move the vertical (elevation) axis
        updateElevationAxis();
        
        // reposition the left/right scroll arrows
        updateScrollArrows();
    }
    
    /*
     * Moves the left/right scroll arrows to the upper left/right corners of the zoomed viewport.
     */
    private void updateScrollArrows() {
        
        final double margin = 5;
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        
        // left
        double xOffset = rView.getX() + margin;
        double yOffset = rView.getY() + _leftScrollArrowNode.getFullBoundsReference().getHeight()/2 + margin ;
        _leftScrollArrowNode.setOffset( xOffset, yOffset );
        
        // right
        xOffset = rView.getMaxX() - margin;
        yOffset = rView.getY() + _rightScrollArrowNode.getFullBoundsReference().getHeight()/2 + margin ;
        _rightScrollArrowNode.setOffset( xOffset, yOffset );
        
        // visibility of arrows
        _leftScrollArrowNode.setVisible( _zoomedViewport.getBoundsReference().getX() > _birdsEyeViewport.getBoundsReference().getX() );
        _rightScrollArrowNode.setVisible( _zoomedViewport.getBoundsReference().getMaxX() < _birdsEyeViewport.getBoundsReference().getMaxX() );
    }
    
    /*
     * Moves the toolbox to the lower-left corner of the zoomed viewport
     */
    private void updateToolboxPosition() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double xOffset = rView.getX() + 5;
        double yOffset = rView.getY() + rView.getHeight() - _toolboxNode.getFullBoundsReference().getHeight() - 5;
        _toolboxNode.setOffset( xOffset, yOffset );
    }
    
    /*
     * Moves the ELA value display to the lower-right corner of the zoomed viewport
     */
    private void updateELAValuePosition() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double xOffset = _toolboxNode.getFullBoundsReference().getMaxX() + 5;
        double yOffset = rView.getMaxY() - _elaValueNode.getFullBoundsReference().getHeight() - 5;
        _elaValueNode.setOffset( xOffset, yOffset );
    }
    
    /*
     * Moves the elevation (vertical) coordinate axes to the left & right edges of the zoomed viewport.
     */
    private void updateElevationAxis() {
        
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        
        // reposition the vertical (elevation) axes
        final double margin = 15; // pixels
        _leftElevationAxisNode.setOffset( rView.getMinX() + margin, _rightElevationAxisNode.getYOffset() );
        _rightElevationAxisNode.setOffset( rView.getMaxX() - margin, _rightElevationAxisNode.getYOffset() );
        
        // rebuild the horizontal (distance) axis, ticks in multiples of 1000 meters
        final int precision = 1000;
        final int minX = precision * (int)( ( rModel.getX() / precision ) - 1 );
        final int maxX = precision * (int)( ( ( rModel.getX() + rModel.getWidth() ) / precision ) + 1 );
        _distanceAxisNode.setRange( minX, maxX );
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
        AbstractToolNode node = ToolNodeFactory.createNode( tool, _mvt );
        _toolsLayer.addChild( node );
        _toolsMap.put( tool, node );
        _toolboxNode.getTrashCan().addManagedNode( node );
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
        _toolboxNode.getTrashCan().removeManagedNode( toolNode );
        toolNode.cleanup();
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
